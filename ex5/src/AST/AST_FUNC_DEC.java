package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import java.util.ArrayList;
import java.util.List;

public class AST_FUNC_DEC extends AST_CLASS_FIELDS_DEC {
    
    public AST_TYPE returnType;
    public AST_LIST<AST_VAR_DEC> params;
    public AST_LIST<AST_STMT> body;
    
    // Field to track the next available offset during the pre-calculation pass
    private int nextLocalOffset; 

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, AST_LIST<AST_VAR_DEC> params, AST_LIST<AST_STMT> body) {
        super(funcName,returnType);
        this.returnType = returnType;
        this.params = params;
        this.body = body;
        
    }

    @Override
    public void PrintMe() {        
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"FUNC_DECLARATION\n "+returnType.type+" "+this.getName()+"("+params+")");
		
        if (params != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);
            params.PrintMe();
        }
        if(body != null)
        {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
            body.PrintMe();
        }
    }

    @Override
    public TYPE_FUNCTION SemantMe() throws SemanticException{
        if(isDeclaredInCurrentScope())
        {
            throw new SemanticException(lineNumber,String.format("Cannot redeclare function %s", getName()));
        }
        TYPE returnT = returnType.SemantMeLog();
        if (returnT == null){
            throw new SemanticException(lineNumber,"Null not good");
        }
        SYMBOL_TABLE instance = SYMBOL_TABLE.getInstance();
        TYPE_FUNCTION t = new TYPE_FUNCTION(returnT, getName(),lineNumber);
        instance.enter(t.getName(),(TYPE)t);

        instance.beginScope();

        TYPE_LIST list =new TYPE_LIST();
        if (params != null) {
            for (AST_DEC param : params) {
                TYPE paramType = param.SemantMe();
                if(paramType.isPrimitive())
                {
                    paramType = new TYPE_VAR_DEC(paramType, param.getName());    
                }
                
                instance.enter(paramType.getName(), paramType);
                list.add(paramType,param.lineNumber);
            }
        }
        t.setParams(list);
        if(body == null)
        {
            instance.endScope();
            return t;
        }
        for(AST_STMT statement : body)
        {
            TYPE statementType =null;
            statementType = statement.SemantMe();
            
            
            if(statement instanceof AST_STMT_RETURN)
            {
                validateReturnType((TYPE_RETURN)statementType,new TYPE_RETURN(returnT),statement.lineNumber);
            }
            if(statement instanceof AST_STMT_CONDITIONAL)
            {
                TYPE_LIST typeList = (TYPE_LIST)statementType;
                validateTypeListReturnType(typeList,new TYPE_RETURN(returnT));
            }
            
        }
        instance.endScope();
        return t;
    }
    private void validateReturnType(TYPE_RETURN statementType,TYPE_RETURN returnType,int lineNumber) throws SemanticException
    {
        if(!returnType.isAssignable(statementType))
        {

            throw new SemanticException(lineNumber,String.format("you cannot assign %s to %s and thus is invalid return type",statementType,returnType));
        }
    }
    private void validateTypeListReturnType(TYPE_LIST list, TYPE_RETURN returnType)throws SemanticException
    {
        if(list == null)
        {
            return;
        }
        for(int i = 0 ; i < list.size() ; i ++)
        {
            TYPE innerStatementType = list.get(i);
            if(innerStatementType instanceof TYPE_RETURN)
            {
                validateReturnType((TYPE_RETURN)innerStatementType,returnType,list.getLineNumber(i));
            }
            if( innerStatementType instanceof TYPE_LIST)
            {
                validateTypeListReturnType((TYPE_LIST)innerStatementType, returnType);
            }
        }
    }

    @Override
    public TEMP IRme() {
        IR ir = IR.getInstance();
        SYMBOL_TABLE symTable = SYMBOL_TABLE.getInstance();
		String funcName = this.varName;

        // Built-in functions handled elsewhere
        if (funcName.equals("PrintInt") || funcName.equals("PrintString")) { 
            return null;
        }

        String label_start = IRcommand.getFreshLabel(funcName + "_start");
        String label_end = IRcommand.getFreshLabel(funcName + "_end");

        symTable.beginScope(); // Scope for parameters and locals
        ir.registerFunctionLabel(this.getName(), label_start);
        ir.pushFunctionEndLabel(label_end);

        // --- Parameter Offset Assignment ---
        // Corrected: First parameter is at $fp + 4 
        int paramOffset = 0; 
        if (params != null) {
            for (AST_VAR_DEC param : params) {
                // Parameters are expected to be found in the current scope
                SYMBOL_TABLE_ENTRY entry = symTable.findEntry(param.getName()); 
                System.out.println("entry: " + entry);
                 if (entry != null && !entry.isGlobal) {
                    entry.offset = paramOffset;
                    System.out.printf("  Assigning param '%s' offset: %d($fp)\n", param.getName(), entry.offset);
                    paramOffset += 4; // Next param is 4 bytes higher
                } else {
                     throw new RuntimeException("Compiler Error: Param '" + param.getName() + "' symbol table entry issue during offset assignment.");
                }
            }
        }

        // --- Local Variable Offset Pre-calculation ---
        // Calculate total frame size using existing count method
        int numberOfLocals = countLocalDeclarations(body);
        int localsSize = numberOfLocals * 4; // Assuming 4 bytes per local/pointer
        int frameSize = localsSize + 8; // 8 bytes for saved $fp and $ra
        System.out.printf("Function '%s': Calculated locals=%d, localsSize=%d, frameSize=%d\n", this.getName(), numberOfLocals, localsSize, frameSize);

        // Initialize the starting offset for locals (below saved $fp and $ra)
        this.nextLocalOffset = -12; 
        
        // Recursively traverse the body to assign offsets to all locals
        System.out.printf("--- Assigning Local Offsets for %s ---\n", funcName);
        assignLocalOffsetsHelper(this.body); 
        System.out.printf("--- Finished Assigning Local Offsets for %s ---\n", funcName);


        // --- IR Generation ---
        // Emit function label and prologue (uses pre-calculated frameSize)
        ir.Add_IRcommand(new IRcommand_Label(label_start));
        ir.Add_IRcommand(new IRcommand_Prologue(frameSize));

        // Generate IR for the function body. Load/Store commands within will use the offsets
        // previously assigned and stored in the symbol table entries.
        if (body != null) {
            body.IRme(); // This call no longer needs/uses offset parameters
        }

        // Emit function end label and epilogue
        ir.Add_IRcommand(new IRcommand_Label(label_end));
        ir.Add_IRcommand(new IRcommand_Epilogue(frameSize));

        ir.popFunctionEndLabel();
        symTable.endScope(); // End scope for parameters and locals

        return null; // Function declaration itself doesn't produce a value TEMP
    }

    // Recursive helper to assign offsets to local variables before IR generation
    private void assignLocalOffsetsHelper(AST_LIST<? extends AST_STMT> statements) {
        if (statements == null) return;

        for (AST_STMT stmt : statements) {
            if (stmt instanceof AST_STMT_VAR_DECL) {
                // Found a local variable declaration
                AST_DEC declaration = ((AST_STMT_VAR_DECL) stmt).varDec;
                String varName = declaration.getName();
                SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntry(varName);
                System.out.println("entry: " + entry);
                if (entry != null && !entry.isGlobal) {
                     // Assign the current offset and decrement for the next local
                    entry.offset = this.nextLocalOffset; 
                    System.out.printf("  Assigning local '%s' offset: %d($fp)\n", varName, entry.offset);
                    this.nextLocalOffset -= 4; 
                } else {
                    // This indicates an internal error - the variable should be in the table
                    System.err.printf("Warning: Could not find symbol table entry for local '%s' during offset assignment pass.\n", varName);
                     // Optionally throw: throw new RuntimeException("Compiler Error: Local var '" + varName + "' not found in ST during offset assignment.");w
                }
            } else if (stmt instanceof AST_STMT_IF) {
                // Assume AST_STMT_IF has field 'body' 
                AST_STMT_IF ifStmt = (AST_STMT_IF) stmt;
                assignLocalOffsetsHelper(ifStmt.body);
                // Removed else branch processing as it's not supported
            } else if (stmt instanceof AST_STMT_WHILE) {
                // Assume AST_STMT_WHILE has field 'body'
                AST_STMT_WHILE whileStmt = (AST_STMT_WHILE) stmt;
                assignLocalOffsetsHelper(whileStmt.body);
            } 
            // Add recursive calls for other compound statements (e.g., FOR loops) if they exist
        }
    }


    // Keep this method to calculate total space needed for the frame BEFORE processing the body.
    // Ensure it correctly counts declarations in all relevant blocks.
    private int countLocalDeclarations(AST_LIST<? extends AST_STMT> statements) {
        int count = 0;
        if (statements == null) return 0;

        for (AST_STMT stmt : statements) {
            if (stmt instanceof AST_STMT_VAR_DECL) {
                // Count every variable declaration statement
                count++; 
            } else if (stmt instanceof AST_STMT_IF) {
                // Assume AST_STMT_IF has field 'body' 
                AST_STMT_IF ifStmt = (AST_STMT_IF) stmt;
                count += countLocalDeclarations(ifStmt.body);
                 // Removed else branch counting as it's not supported
            } else if (stmt instanceof AST_STMT_WHILE) {
                // Assume AST_STMT_WHILE has field 'body'
                AST_STMT_WHILE whileStmt = (AST_STMT_WHILE) stmt;
                count += countLocalDeclarations(whileStmt.body);
            } 
            // Add other compound statements if necessary
        }
        return count;
    }
}



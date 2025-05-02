package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_VAR_DEC extends AST_DEC {
    
    public AST_TYPE t;
    public AST_EXP varValue;
    public String varName;

    public AST_VAR_DEC(String varName, AST_TYPE varType, AST_EXP initialValue) {
        super(varName);
        this.varName = varName;
        this.t = varType;
        this.varValue = initialValue;
    
    }
    public AST_VAR_DEC(String varName, AST_TYPE varType) {
        this(varName,varType,null);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"AST_VAR_DEC "+ this.toString());	
    }
    @Override
    public String toString() {
        return t.toString()+ " "+getName() + (varValue != null ? "="+varValue:"");
    }

    @Override
    public TYPE SemantMe() throws SemanticException{
        String currentVarName = getName(); // Store name for clarity
        System.out.println("--- Checking variable declaration for: " + currentVarName + " ---");
        System.out.println("Symbol Table Top before check: " + SYMBOL_TABLE.getInstance().getTopEntryName());
        boolean exists = SYMBOL_TABLE.getInstance().isDeclaredInImmediateScope(currentVarName);
        System.out.println("Result of isDeclaredInImmediateScope(" + currentVarName + "): " + exists);
        if(exists) // Check the stored result
        {
            System.out.println("--- ERROR: Variable already exists in current scope! ---");
            throw new SemanticException(lineNumber, String.format("Cannot declare %s was already declared in this scope", currentVarName));
        }
        TYPE type = t.SemantMeLog();
        if(type.isPrimitive())
        {
            type = new TYPE_VAR_DEC(type,getName());
        }
        boolean isGlobal = SYMBOL_TABLE.getInstance().isAtGlobalScope();
        System.out.println("Entering variable2: " + getName() + " with type: " + type + " and isGlobal: " + isGlobal);
        SYMBOL_TABLE.getInstance().enter(getName(), type,  isGlobal);
        if( varValue != null)
        {
            TYPE valueType = varValue.SemantMeLog();
            if(!type.isAssignable(valueType))
            {
                throw new SemanticException(lineNumber,String.format("Initial value %s does not match type %s", valueType,type));
            }
        }
        System.out.println("--- Finished variable declaration for: " + currentVarName + " ---");
        return type;
    }
    @Override
    public TEMP IRme()
	{
        // 1. Create the TEMP for this variable
        TEMP varTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
                
        // 2. Associate this TEMP with the variable name in the symbol table
        SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntry(getName());
        SYMBOL_TABLE.getInstance().associateTemp(getName(), varTemp);
        boolean isGlobal = entry.isGlobal;
        if(isGlobal)
        {
            IR.getInstance().Add_IRcommand(new IRcommand_Allocate(getName()));
        }
        // 3. Handle initialization if present
        // System.out.println("IRme for variable: " + entry.toString() + " VAR VALUE: " + this.varValue);
        if (this.varValue != null) {
            TEMP initValTemp = this.varValue.IRme();
            if(isGlobal)
            {
                // Global variable initialization: Use Global_Init_Store
                IR.getInstance().Add_IRcommand(new IRcommand_Global_Init_Store(getName(), initValTemp));
            }
            else
            {
                IR.getInstance().Add_IRcommand(new IRcommand_Store(varTemp, initValTemp, getName())); 
            }
        }
        
        // IRme for a declaration doesn't return a value TEMP
        return null; 
    }
}
package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import SYMBOL_TABLE.SYMBOL_TABLE;

public class AST_FUNC_DEC extends AST_CLASS_FIELDS_DEC {
    
    public AST_TYPE returnType;
    public AST_LIST<AST_VAR_DEC> params;
    public AST_LIST<AST_STMT> body;

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
		String funcName = this.varName;

		String label_start = IRcommand.getFreshLabel(funcName + "_start");
        String label_end   = IRcommand.getFreshLabel(funcName + "_end");

        int numLocals = 0;
        if (body != null) {
            for (AST_STMT stmt : body) {
                if (stmt instanceof AST_STMT_VAR_DECL) {
                    numLocals++;
                }
            }
        }
        int frameSize = 8 + numLocals * 4;

        IR ir = IR.getInstance(); // Get instance once
        
        if (funcName.equals("main")) {
            System.out.println("Generating main function IR");
            _mainIr(body);
            return null;
        }
        ir.registerFunctionLabel(funcName, label_start);
        ir.Add_IRcommand(new IRcommand_Label(label_start));
        ir.Add_IRcommand(new IRcommand_Prologue(frameSize));
        
        // --- Parameter Loading ---
        if (params != null) {
            int paramIndex = 0;
            for (AST_VAR_DEC param : params) {
                int offset = 0 + paramIndex * 4; 
                TEMP paramTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
                ir.Add_IRcommand(new IRcommand_Load(paramTemp, offset, param.getName()));
                SYMBOL_TABLE.getInstance().associateTemp(param.getName(), paramTemp);
                paramIndex++;
            }
        }

        // --- Process Body with End Label Tracking ---
        ir.pushFunctionEndLabel(label_end); // Push label before processing body
        if (body != null) {
            body.IRme();
        }
        ir.popFunctionEndLabel(); // Pop label after processing body

        // --- End Label and Epilogue ---
        ir.Add_IRcommand(new IRcommand_Label(label_end)); // End label position
        ir.Add_IRcommand(new IRcommand_Epilogue(frameSize)); // Epilogue comes AFTER end label


        return null;
    }
    public void _mainIr(AST_LIST<AST_STMT> body) {
        IR ir = IR.getInstance();

        ir.Add_IRcommand(new IRcommand_Label("mainStart"));
        ir.Add_IRcommand(new IRcommand_Prologue(12));
        ir.pushFunctionEndLabel("main");
        if (body != null) {
            body.IRme();
        }
        ir.popFunctionEndLabel();
        ir.Add_IRcommand(new IRcommand_Epilogue(12));

    }
}


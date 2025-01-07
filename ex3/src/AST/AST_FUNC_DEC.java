package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class AST_FUNC_DEC extends AST_CLASS_FIELDS_DEC {
    
    public AST_TYPE returnType;
    public AST_LIST<AST_VAR_DEC> params;
    public AST_LIST<AST_STMT> body;

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, AST_LIST<AST_VAR_DEC> params, AST_LIST<AST_STMT> body) {
        super(funcName);
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
        TYPE returnT = returnType.SemantMeLog();
        if (returnT == null){
            throw new SemanticException(lineNumber,"Null not good");
        }
        String name = this.getName();
        TYPE_LIST paramTypes = null;
        if (params != null) {
            for (AST_VAR_DEC param : params) {
                TYPE paramType = param.SemantMe();
                paramTypes = new TYPE_LIST(paramType, paramTypes);
            }
        }

        TYPE_FUNCTION t = new TYPE_FUNCTION(returnT, name, paramTypes,lineNumber);

        // SYMBOL_TABLE.getInstance().enter(name, t);
        // System.out.println(t + "-----------------------");
        return t;

    }
}

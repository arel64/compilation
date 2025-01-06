package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_FUNC_INVO extends AST_EXP {
    
    public String funcName;
    public AST_LIST<AST_EXP> params;
    public AST_VAR var;

    public AST_FUNC_INVO(AST_VAR var,String funcName,AST_LIST<AST_EXP> params) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_FUNC_INVO(String funcName, AST_LIST<AST_EXP> params) {
        this(null,funcName,params);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("FUNC_INVO\n %s",toString())
        );
        if (var != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
            var.PrintMe();
        }
        if (params != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, params.SerialNumber);
            params.PrintMe();
        }
    }
    @Override
    public String toString() {
        String varRepresentation = var != null ? var.toString()+"." : "";  
        String paramsRepresentation = params != null ? ((params.size()== 0) ? "" : params.toString()) : "";
        return  String.format("%s%s(%s)",varRepresentation,funcName,paramsRepresentation);
        
    }

    @Override
    public TYPE_FUNCTION SemantMe() throws SemanticException{
        SYMBOL_TABLE table = SYMBOL_TABLE.getInstance();
        TYPE symbol = table.find(funcName);
        if(symbol == null)
        {
            throw new SemanticException(String.format("%s does not exist and cannot be invoked", funcName));
        }

        if(!(symbol instanceof TYPE_FUNCTION))
        {
            throw new SemanticException(String.format("%s cannot be used like a function", funcName));
        }
        TYPE_FUNCTION myFunctionType = (TYPE_FUNCTION)symbol;
        //TODO::
        //two cases one for invoc in class (x.()) and one for regular function call
        //check if v has this function id in the scope of the class
        //check for each e in the param list if the type returned is like what we want 
        //return the type returned and check if it is what we declared 
        
        return myFunctionType;

    }
}

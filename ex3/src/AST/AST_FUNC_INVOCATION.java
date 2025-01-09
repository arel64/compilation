package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_FUNC_INVOCATION extends AST_EXP {
    
    public String funcName;
    public AST_LIST<AST_EXP> params;
    public AST_VAR var;

    public AST_FUNC_INVOCATION(AST_VAR var,String funcName,AST_LIST<AST_EXP> params) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_FUNC_INVOCATION(String funcName, AST_LIST<AST_EXP> params) {
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
    public TYPE SemantMe() throws SemanticException{
        SYMBOL_TABLE table = SYMBOL_TABLE.getInstance();
        TYPE functionType = table.find(funcName);
        System.out.println(functionType);
        if(functionType == null)
        {
            throw new SemanticException(lineNumber,String.format("%s does not exist and cannot be invoked", funcName));
        }

        if(!(functionType instanceof TYPE_FUNCTION))
        {
            throw new SemanticException(lineNumber,String.format("%s cannot be used like a function", funcName));
        }
        TYPE_FUNCTION myFunctionType = (TYPE_FUNCTION)functionType;

        table.beginScope();
        if(params != null)
        {
            System.out.println("siz1 "+ params.size() + " size2 "+myFunctionType.getParams().size());
            if(params.size() != myFunctionType.getParams().size())
            {
                throw new SemanticException(lineNumber,String.format("incorrect function %s invocation, number of parameters", funcName));
            }
            for(int i = 0 ; i < params.size() ;i ++)
            {
                TYPE expType = params.at(i).SemantMeLog();
                TYPE param = myFunctionType.getParam(i);
                System.out.println(expType+ " sd " +param);
                if(!param.isInterchangeableWith(expType))
                {
                    throw new SemanticException(lineNumber,String.format("incorrect function %s invocation for value %s to param %s", funcName, expType,param));
                }
            }
        }
        table.endScope();
        
        return myFunctionType.getReturnType();

    }
}

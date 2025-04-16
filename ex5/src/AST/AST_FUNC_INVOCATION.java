package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import java.util.ArrayList;
import java.util.List;
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
        
        
        TYPE varType = null;
        if(var != null)
        {
            varType = var.SemantMe();
        } 
        if(varType!=null && !varType.isClass())
        {
            throw new SemanticException(lineNumber,String.format("%s does not exist and cannot be invoked", varType));
        }
        TYPE functionType = null;
        if(varType == null)
        {
            functionType = table.find(funcName);
        }
        else
        {
            TYPE_CLASS varClass = (TYPE_CLASS)varType;
            functionType = varClass.getDataMember(funcName).t;
        }
        
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
            if(params.size() != myFunctionType.getParams().size())
            {
                throw new SemanticException(lineNumber,String.format("incorrect function %s invocation, number of parameters", funcName));
            }
            for(int i = 0 ; i < params.size() ;i ++)
            {
                TYPE expType = params.at(i).SemantMeLog();
                TYPE param = myFunctionType.getParam(i);
                if(!param.isInterchangeableWith(expType))
                {
                    throw new SemanticException(lineNumber,String.format("incorrect function %s invocation for value  param %s=%s ", funcName,param,expType));
                }
            }
        }
        table.endScope();
        
        return myFunctionType.getReturnType();

    }

    @Override
    public TEMP IRme() {
        ArrayList<TEMP> paramsTemp = new ArrayList<TEMP>();
        if (params != null)
            for (AST_EXP param : params)
            {
                paramsTemp.add(param.IRme());
            }
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
        if (var == null)
        {
            IR.getInstance().Add_IRcommand(new IRcommand_Func_Call(dst, funcName, paramsTemp));

        }
        else
        {
            IR.getInstance().Add_IRcommand(new IRcommand_Class_Method_Call(dst, var.IRme(), funcName, paramsTemp));
        }

        // IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(func_label)); // TODO fix label
        // IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(end_label));

        return dst;
    }
}

package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP condition;
	public AST_LIST<AST_STMT> body;
	public AST_STMT_IF(AST_EXP condition, AST_LIST<AST_STMT> body) {
        this.condition = condition;
        this.body = body;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        condition.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "IF("+condition+")");
        body.PrintMe();
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,condition.SerialNumber);
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        TYPE conditionType = condition.SemantMe();
        if(!(conditionType instanceof TYPE_INT))
        {
            throw new SemanticException(lineNumber,String.format("If statement condition must be int, got: %s ",conditionType));
        }
        return conditionType;
    }
}
package AST;
import TYPES.*;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP condition;
	public AST_STMT_LIST body;
	public AST_STMT_IF(AST_EXP condition, AST_STMT_LIST body) {
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
}
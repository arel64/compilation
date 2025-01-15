package AST;
public class AST_STMT_IF extends AST_STMT_CONDITIONAL
{
	public AST_STMT_IF(AST_EXP condition, AST_LIST<AST_STMT> body) {
        super(condition,body);
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        getCondition().PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "IF("+getCondition()+")");
        getBody().PrintMe();
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,getBody().SerialNumber);
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,getCondition().SerialNumber);
    }
}
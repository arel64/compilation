package AST;
import TYPES.*;

public class AST_STMT_WHILE extends AST_STMT {
    public AST_EXP condition;
    public AST_STMT_LIST body;

    public AST_STMT_WHILE(AST_EXP condition, AST_STMT_LIST body) {
        this.condition = condition;
        this.body = body;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    
    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "WHILE("+condition+")");
        body.PrintMe();
    }
}

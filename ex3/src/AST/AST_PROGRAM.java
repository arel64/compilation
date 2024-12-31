package AST;
import TYPES.*;

public class AST_PROGRAM extends AST_Node {
    public AST_DEC_LIST declerationList;

    public AST_PROGRAM(AST_DEC_LIST declerationList)
    {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.declerationList = declerationList;
    }

    @Override
    public void PrintMe()
    {

        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "PROGRAM"
        );
        if (declerationList != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, declerationList.SerialNumber);
            declerationList.PrintMe();
        }
    }
}

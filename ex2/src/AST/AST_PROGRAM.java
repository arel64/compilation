package AST;

public class AST_PROGRAM extends AST_Node {
    public AST_DEC_LIST decList;

    public AST_PROGRAM(AST_DEC_LIST decList)
    {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.decList = decList;
    }

    @Override
    public void PrintMe()
    {
        System.out.printf("PROGRAM\n");
        if (decList != null) decList.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "PROGRAM"
        );
        if (decList != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, decList.SerialNumber);
        }
    }
}

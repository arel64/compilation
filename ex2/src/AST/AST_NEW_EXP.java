package AST;


public class AST_NEW_EXP extends AST_EXP {
    public AST_TYPE type;

    public AST_NEW_EXP(AST_TYPE currType, AST_EXP currExp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
    }

    public AST_NEW_EXP(AST_TYPE currType) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,type.SerialNumber);
        type.PrintMe();
    }
}

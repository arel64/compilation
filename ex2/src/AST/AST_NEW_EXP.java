package AST;


public class AST_NEW_EXP extends AST_EXP {
    public AST_TYPE type;
    public AST_EXP currExp;

    public AST_NEW_EXP(AST_TYPE currType, AST_EXP currExp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
        this.currExp = currExp;
    }

    public AST_NEW_EXP(AST_TYPE currType) {
        this(currType, null);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("%s",toString())
        );
    }
    @Override
    public String toString() {
        String expString = currExp == null ? "" : String.format("[%s]", currExp);
        return String.format("new %s %s", type,expString);
    }
}

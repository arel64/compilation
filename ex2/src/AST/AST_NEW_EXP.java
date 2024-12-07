package AST;


public class AST_NEW_EXP extends AST_Node {
    public AST_TYPE type;
    public AST_EXP possibleExp;

    // Constructor for class declaration
    public AST_NEW_EXP(AST_TYPE currType, AST_EXP currExp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
        this.possibleExp = currExp;
    }

    public AST_NEW_EXP(AST_TYPE currType) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
        this.possibleExp = null;
    }

    @Override
    public void PrintMe() {
        type.PrintMe();
        if (possibleExp != null) {
            possibleExp.PrintMe();
        }
    }
}

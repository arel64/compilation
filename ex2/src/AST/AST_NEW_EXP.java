package AST;


public class AST_NEW_EXP extends AST_NODE {
    public AST_TYPE type;
    public AST_EXP possibleExp;

    // Constructor for class declaration
    public AST_NEW_EXP(AST_TYPE currType, AST_EXP currExp) {
        this.type = currType;
        this.possibleExp = currExp;
    }

    public AST_NEW_EXP(AST_TYPE currType) {
        this.type = currType;
        this.possibleExp = null;
    }

    @Override
    public void printMe() {
        type.printMe();
        if (possibleExp != null) {
            possibleExp.printMe();
        }
    }
}

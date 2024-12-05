package AST;

public class AST_VAR_DEC extends AST_DEC {
    public String varName;
    public AST_TYPE varType;
    public AST_EXP varValue;

    public AST_VAR_DEC(String varName, AST_TYPE varType, AST_EXP initialValue) {
        this.varName = varName;
        this.varType = varType;
        this.varValue = initialValue;
    }
    //Constructor for dec with no assignment
    public AST_VAR_DEC(String varName, AST_TYPE varType) {
        this.varName = varName;
        this.varType = varType;
        this.varValue = null; // No assignment
    }

    @Override
    public void printMe() {
        if (this.varValue != null) {
            System.out.printf("VAR DEC: %s %s = ", this.varType, this.varName);
            this.varValue.printMe();  // Print the value of the assignment
        } else {
            System.out.printf("VAR DEC: %s %s\n", this.varType, this.varName);
        }
    }
}
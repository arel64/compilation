package AST;

public class AST_ARRAY_TYPEDEF extends AST_DEC {
    public String arrayName;  // Name of the array typedef (e.g., IntArray)
    public AST_TYPE baseType; // Base type: could be AST_EXP_INT, AST_VAR_SIMPLE, etc.

    public AST_ARRAY_TYPEDEF(String arrayName, AST_TYPE baseType) {
        this.arrayName = arrayName;
        this.baseType = baseType;
    }

    @Override
    public void printMe() {
        System.out.printf("ARRAY TYPEDEF: %s = %s[]\n", arrayName, baseType.getClass().getSimpleName());
    }
}
//TODO make sure that we dont need to override the getSimpleName for each type 
package AST;

public class AST_ARRAY_TYPEDEF extends AST_DEC {
    public String arrayName; 
    public AST_TYPE baseType;

    public AST_ARRAY_TYPEDEF(String arrayName, AST_TYPE baseType) {
        this.arrayName = arrayName;
        this.baseType = baseType;
    }

    @Override
    public void PrintMe() {
        System.out.printf("ARRAY TYPEDEF: %s = %s[]\n", arrayName, baseType.getClass().getSimpleName());
    }
}

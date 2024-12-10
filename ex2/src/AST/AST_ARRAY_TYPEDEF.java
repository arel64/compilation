package AST;

public class AST_ARRAY_TYPEDEF extends AST_DEC {
    public AST_TYPE baseType;

    public AST_ARRAY_TYPEDEF(String arrayName, AST_TYPE baseType) {
        super(arrayName);
        this.baseType = baseType;
    }

    @Override
    public void PrintMe() {
        super.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ARRAY_DEC ");
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, baseType.SerialNumber);
        baseType.PrintMe();
    }
}

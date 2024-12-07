package AST;

public class AST_PARAM extends AST_Node{
    public AST_TYPE paramType;
    public String paramName;

    public AST_PARAM(AST_TYPE paramType, String paramName) {
        this.paramType = paramType;
        this.paramName = paramName;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public void printMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("PARAM(%s,%s)", paramType,paramName)
        );
    }
}

package AST;

public class AST_FIELD extends AST_Node {
    
    public AST_DEC declaration;
    
    public AST_FIELD(AST_DEC declaration)
    {
        this.declaration = declaration;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe()
    {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("FIELD(%s)", declaration)
        );
    }
}

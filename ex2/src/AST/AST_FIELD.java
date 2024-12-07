package AST;

public class AST_FIELD extends AST_Node {
    
    public String name;
    
    public AST_FIELD(String name)
    {
        this.name = name;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe()
    {
        System.out.printf("AST FIELD %s\n", name);
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("FIELD(%s)", name)
        );
    }
}

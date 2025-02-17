package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;


public abstract class AST_DEC extends AST_Node {
    private String name;
    public AST_DEC(String name)
    {
        this.name = name;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }
    public String getName()
    {
        return name;
    }
    @Override
    public void PrintMe()
    {
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"'AST_DEC\nParmeter name: '"+getName());		
    }
    protected boolean isDeclaredInCurrentScope()
    {
        return SYMBOL_TABLE.getInstance().existsInCurrentScope(name);
    }
}

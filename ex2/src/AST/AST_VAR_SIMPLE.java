package AST;

public class AST_VAR_SIMPLE extends AST_VAR
{
	
	public AST_VAR_SIMPLE(String name)
	{
		super(name);	
	}

	public void PrintMe()
	{

		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",this.val));
	}
}

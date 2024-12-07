package AST;

public class AST_EXP_INT extends AST_EXP
{
	public int value;
	
	public AST_EXP_INT(int value)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();

	this.value = value;
	}

	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("INT(%d)",value));
	}
}

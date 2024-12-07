package AST;

public class AST_EXP_STRING extends AST_EXP
{
	public String value;
	
	public AST_EXP_STRING(String value)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.value = value;
	}

	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING(%s)",value));
	}
}

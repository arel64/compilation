package AST;

public class AST_EXP_STRING extends AST_EXP
{
	public String value;
	
	public AST_EXP_STRING(String value)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> STRING( %s )\n", value);

		this.value = value;
	}

	public void PrintMe()
	{
		System.out.format("AST NODE STRING( %s )\n",value);
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING(%s)",value));
	}
}

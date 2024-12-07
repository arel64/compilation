package AST;

public class AST_EXP_INT extends AST_EXP
{
	public int value;
	
	public AST_EXP_INT(int value)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> INT( %d )\n", value);
	this.value = value;
	}

	public void PrintMe()
	{
		System.out.format("AST NODE INT( %d )\n",value);
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("INT(%d)",value));
	}
}

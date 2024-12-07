package AST;

public class AST_EXP_VAR extends AST_EXP
{
	public AST_VAR var;
	public AST_EXP_VAR(AST_VAR var)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();


		this.var = var;
	}
	
	public void PrintMe()
	{
		if (var != null) var.PrintMe();
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"EXP\nVAR");

		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
			
	}
}

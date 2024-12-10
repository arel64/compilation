package AST;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_EXP subscript;
	
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript)
	{
		super(var.val);
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.subscript = subscript;
	}


	@Override
	public void PrintMe()
	{
		super.PrintMe();
		if (subscript != null) subscript.PrintMe();
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		if (val       != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,super.SerialNumber);
		if (subscript != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
}

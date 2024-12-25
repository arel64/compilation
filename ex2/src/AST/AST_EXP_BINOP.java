package AST;

public class AST_EXP_BINOP extends AST_EXP
{
	public AST_BINOP binop;
	public AST_EXP left;
	public AST_EXP right;
	
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right, AST_BINOP OP)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.left = left;
		this.right = right;
		this.binop = OP;
	}
	
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"BINOP\n"+this.toString()
			);
		
		if (left  != null)
		{
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
			left.PrintMe();
		} 
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,binop.SerialNumber);	
		this.binop.PrintMe();
		if (right != null)
		{
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);	
			right.PrintMe();
		}
		

	}
	@Override
	public String toString() {
		return left.toString()+binop.toString()+right.toString();
	}
}

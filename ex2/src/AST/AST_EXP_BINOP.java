package AST;

public class AST_EXP_BINOP extends AST_EXP
{
	public AST_BINOP binop;
	public AST_EXP left;
	public AST_EXP right;
	
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right, AST_BINOP OP)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		System.out.print("====================== exp -> exp BINOP exp\n");
		this.left = left;
		this.right = right;
		this.binop = OP;
	}
	
	public void PrintMe()
	{
		System.out.print("AST NODE BINOP EXP\n");

		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		this.binop.PrintMe();
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"BINOP"
			);
		
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
}

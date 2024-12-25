package AST;

public class AST_BINOP extends AST_EXP
{
	String op;
	
	public AST_BINOP(String op)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.op = op;
	}
	
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP\n%s",op));
	}
	@Override
	public String toString() {
		return op;
	}
}

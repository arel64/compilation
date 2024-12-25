package AST;

public class AST_TYPE extends AST_Node
{
	public String type;
	public AST_TYPE(String currType)
	{
		this.type = currType;
	}
	@Override
	public String toString() {
		return type;
	}
}

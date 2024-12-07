package AST;

public class AST_TYPE extends AST_Node
{
	public String type;
	public AST_TYPE(String currType)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.type = currType;
	}
    @Override
	public void PrintMe()
	{
	}
}

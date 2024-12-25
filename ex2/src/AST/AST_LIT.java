package AST;

public abstract class AST_LIT extends AST_EXP
{
   abstract String getValue();

   @Override
   public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("LITERAL(%s)",getValue()));
	}
	@Override
	public String toString() {
		return getValue();
	}
}

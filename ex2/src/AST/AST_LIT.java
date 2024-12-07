package AST;

public abstract class AST_LIT extends AST_Node
{
   abstract String getValue();

   @Override
   public void PrintMe()
	{
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("LITERAL\n...->%s",getValue()));
	}
}

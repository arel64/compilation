package AST;

public abstract class AST_VAR_SIMPLE extends AST_NODE
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

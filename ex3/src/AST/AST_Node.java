package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("%s",toString())
        );
	}
	public abstract TYPE SemantMe() throws SemanticException;
}

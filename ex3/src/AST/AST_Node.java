package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
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
	public String metadata;
	public int lineNumber;
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
	public void logMetadata(TYPE myType)
	{
		String myTypeName = "No Type";
		String myTypeClassName ="";
		if(myType != null)
		{
			myTypeName = myType.name;
			myTypeClassName = (myType.getClass()).toGenericString();
		}
		SYMBOL_TABLE instance = SYMBOL_TABLE.getInstance(); 
		AST_GRAPHVIZ.getInstance().metadataNode(
			SerialNumber,
			String.format("Scope Level %s Type name: %S myClass %s",instance.getCurrentScopeIndex(),myTypeName,myTypeClassName)
		);	
	}
	public abstract TYPE SemantMe() throws SemanticException;
	final public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	public TYPE SemantMeLog() throws SemanticException
	{
		TYPE myType = SemantMe();
		logMetadata(myType);
		return myType;
	}	
}

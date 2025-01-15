package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	
	public AST_VAR_SIMPLE(String name)
	{
		super(name);	
	}

	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",this.val));
	}
	@Override
	public String toString() {
		return this.val;
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		TYPE t = SYMBOL_TABLE.getInstance().find(val);
		if( t== null)
		{
			throw new SemanticException(lineNumber,String.format("Cannot find var: %s ",this.val));
		}
		return t;
	}
}

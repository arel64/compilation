package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_EXP subscript;
	public AST_VAR var;
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript)
	{
		super(var.val);
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.subscript = subscript;
		this.var = var;
	}


	@Override
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SUBSCRIPT\n %s",toString()));
	}
	@Override
	public String toString() {
		return String.format("%s[%s]",this.var,this.subscript);
	}


	@Override
	public TYPE SemantMe() throws SemanticException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'SemantMe'");
	}
}

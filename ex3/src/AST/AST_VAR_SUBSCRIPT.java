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

		TYPE varType = this.var.SemantMeLog();
		if(!varType.isArray())
		{
			throw new SemanticException(lineNumber, String.format("%s is not subscriptable", varType));
		}
		TYPE_ARRAY vArray = (TYPE_ARRAY)varType;
		TYPE subscriptType = subscript.SemantMeLog();
		if(!(subscriptType instanceof TYPE_INT))
		{
			throw new SemanticException(lineNumber, String.format("subscriptable must be integral type, got ", subscriptType));
		}
		if(subscript instanceof AST_LIT_NUMBER && ((AST_LIT_NUMBER)subscript).val < 0)
		{
			throw new SemanticException(lineNumber, String.format("subscriptable must be gt 0 got ", ((AST_LIT_NUMBER)subscript).val));
		}
		return vArray.t;
	}
}

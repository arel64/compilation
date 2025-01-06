package AST;

import SYMBOL_TABLE.SemanticException;
import TYPES.TYPE;

public class AST_VAR_FIELD extends AST_VAR
{
	public String fieldName;	
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		super(var.val);
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		return String.format("%s.%s",this.val,fieldName);
	}
	@Override
	public TYPE SemantMe() throws SemanticException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'SemantMe'");
	}
}

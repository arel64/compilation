package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_FIELD;
import TYPES.TYPE_VAR_DEC;

public class AST_VAR_FIELD_INVOCATION extends AST_VAR
{
	public String fieldName;
	private AST_VAR var;	
	public AST_VAR_FIELD_INVOCATION(AST_VAR var,String fieldName)
	{
		super(var.val);
		this.var = var;
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		return String.format("%s.%s",this.val,fieldName);
	}
	@Override
	public TYPE SemantMe() throws SemanticException {
		TYPE varType = var.SemantMeLog();
		TYPE declaredType = SYMBOL_TABLE.getInstance().find(varType.getName());
		if(!declaredType.isClass())
		{
			throw new SemanticException(lineNumber, String.format("Can not invoke %s.%s on a none class type %s", varType.getName(),fieldName,declaredType));
		}
		TYPE_CLASS varClass = (TYPE_CLASS)varType;
		TYPE_CLASS_FIELD member = varClass.getDataMember(fieldName);
		if(member == null)
		{
			throw new SemanticException(lineNumber, String.format("Can not invoke %s.%s on class type %s, it does not exist",varType.getName(),fieldName,varType));
		}
		return member.t;
	}
}

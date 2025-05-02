package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_FIELD;
import TEMP.*;
import IR.*;
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

	@Override
	public TEMP IRme()
	{
		try {
			TEMP objectBaseAddr = var.IRme();
			if (objectBaseAddr == null) {
				System.err.printf("IR Error(ln %d): Cannot access field '%s' because base object expression did not yield a value.\n", lineNumber, fieldName);
                return null;
			}

			TYPE varType = var.SemantMeLog();
			if (!(varType instanceof TYPE_CLASS)) {
				System.err.printf("IR Error(ln %d): Cannot access field on non-class type: %s\n", lineNumber, varType.getName());
                return null;
			}
			TYPE_CLASS classType = (TYPE_CLASS) varType;

			int offset = classType.getDataMemberOffset(fieldName);

			TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(
				new IRcommand_Class_Field_Access(dst, objectBaseAddr, offset, classType.getName(), fieldName)
			);
			return dst;
		} catch (SemanticException e) {
            System.err.printf("IR Generation Error (ln %d) accessing field '%s': %s\n", lineNumber, fieldName, e.getMessage());
            return null;
        }
	}

	@Override
	public TEMP storeValueIR(TEMP sourceValue)
	{
		try {
			TEMP objectBaseAddr = var.IRme();
			if (objectBaseAddr == null) {
				System.err.printf("IR Error(ln %d): Cannot set field '%s' because base object expression did not yield a value.\n", lineNumber, fieldName);
                return null;
			}

			TYPE varType = var.SemantMeLog();
			if (!(varType instanceof TYPE_CLASS)) {
				System.err.printf("IR Error(ln %d): Cannot set field on non-class type: %s\n", lineNumber, varType.getName());
                return null;
			}
			TYPE_CLASS classType = (TYPE_CLASS) varType;

			int offset = classType.getDataMemberOffset(fieldName);

			IR.getInstance().Add_IRcommand(
				new IRcommand_Class_Field_Set(objectBaseAddr, offset, sourceValue, classType.getName(), fieldName)
			);
			return null;
		} catch (SemanticException e) {
            System.err.printf("IR Generation Error (ln %d) setting field '%s': %s\n", lineNumber, fieldName, e.getMessage());
            return null;
        }
	}
}

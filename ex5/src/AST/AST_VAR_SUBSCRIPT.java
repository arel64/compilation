package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
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
		if(!((subscriptType.isAssignable(TYPE_INT.getInstance())) || subscriptType.equals(TYPE_INT.getInstance())))
		{
			throw new SemanticException(lineNumber, String.format("subscriptable must be integral type, got %s", subscriptType));
		}
		if(subscript instanceof AST_LIT_NUMBER && ((AST_LIT_NUMBER)subscript).val < 0)
		{
			throw new SemanticException(lineNumber, String.format("subscriptable must be gt 0 got ", ((AST_LIT_NUMBER)subscript).val));
		}
		if(vArray.t.isPrimitive())
		{
			return new TYPE_ARRAY_SUBSCRIPT(vArray,subscriptType);
		}
		return vArray.t;

		
	}

	public TEMP IRme()
	{
		System.out.println("AST_VAR_SUBSCRIPT IRme + VAR: "+this.var.toString() );
		TEMP arrBaseAddr = this.var.IRme();
		TEMP index = this.subscript.IRme();
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_Array_Access(dst, arrBaseAddr, index));

		return dst;
	}

	@Override
	public TEMP storeValueIR(TEMP sourceValue) {
		TEMP arrBaseAddr = this.var.IRme();
		TEMP index = this.subscript.IRme();
		IR.getInstance().Add_IRcommand(new IRcommand_Array_Set(arrBaseAddr, index, sourceValue));

		return null; // Store operation doesn't produce a result TEMP
	}
}

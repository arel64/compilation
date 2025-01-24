package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_EXP_VAR extends AST_EXP
{
	public AST_VAR var;
	public AST_EXP_VAR(AST_VAR var)
	{
		SerialNumber = var.SerialNumber;
		this.var = var;
	}
	
	public void PrintMe()
	{
		var.PrintMe();
	}
	@Override
	public String toString() {
		return var.toString();
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		return var.SemantMeLog();
	}

	@Override
	public TEMP IRme() {
		TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_Load(dst, var.val));
		return dst;
	}
}

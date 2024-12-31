package AST;
import TYPES.*;

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
}

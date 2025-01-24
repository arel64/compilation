/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class IRcommand_Func_Call extends IRcommand
{
	TEMP dst;
	TEMP func;
	ArrayList<TEMP> args;

	public IRcommand_Func_Call(TEMP func, ArrayList<TEMP> args)
	{
		this.func = func;
		this.args = args;
	}

	public IRcommand_Func_Call(TEMP dst, TEMP func, ArrayList<TEMP> args)
	{
		this.dst = dst;
		this.func = func;
		this.args = args;
	}
	
	@Override
    public String toString() {
        String result = "IRcommand_Func_Call: func=" + func + ", args=" + args.toString();
        if (dst != null) {
            result += ", dst=" + dst;
        }
        return result;
    }
}

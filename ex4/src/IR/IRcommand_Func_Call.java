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
}

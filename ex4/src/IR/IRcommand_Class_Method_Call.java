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

public class IRcommand_Class_Method_Call extends IRcommand
{
	TEMP dst;
	TEMP src;
	String method;
	ArrayList<TEMP> args;
	
	public IRcommand_Class_Method_Call(TEMP src, String method, ArrayList<TEMP> args)
	{
		this.src = src;
		this.method = method;
		this.args = args;
	}

	public IRcommand_Class_Method_Call(TEMP dst, TEMP src, String method, ArrayList<TEMP> args)
	{
		this.dst = dst;
		this.src = src;
		this.method = method;
		this.args = args;
	}
	@Override
    public String toString() {
        String result = "IRcommand_Class_Method_Call: method=" + method + ", src=" + src;
        if (dst != null) {
            result += ", dst=" + dst;
        }
        result += ", args=" + args.toString();
        return result;
    }
}


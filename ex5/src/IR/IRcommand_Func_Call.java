/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
import java.util.stream.*;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Func_Call extends IRcommand
{
	public String func;
	public ArrayList<TEMP> args;

	public IRcommand_Func_Call(String func, ArrayList<TEMP> args)
	{
		this.func = func;
		this.args = args;
	}

	public IRcommand_Func_Call(TEMP dst, String func, ArrayList<TEMP> args)
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

	public void staticAnalysis() {
		if (args.stream().anyMatch(temp -> !temp.initialized))
			dst.initialized = false;
		super.staticAnalysis();
	}

	@Override
	public void MIPSme() {
		// Handle predefined functions
		if (func.equals("PrintInt") && args.size() == 1) {
			// PrintInt is implemented using MIPS syscall
			MIPSGenerator.getInstance().print_int(args.get(0));
			return;
		}
		
		// For other functions, we would generate code to:
		// 1. Push arguments to stack
		// 2. Jump to function label
		// 3. Store return value (if any) to dst
		
		// This part would be implemented based on your calling convention
		// For now, we're only handling the PrintInt predefined function
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(args);
	}
}

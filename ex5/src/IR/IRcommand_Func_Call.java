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
	public String funcName;
	public ArrayList<TEMP> args;

	public IRcommand_Func_Call(String funcName, ArrayList<TEMP> args)
	{
		this.funcName = funcName;
		this.args = args;
	}

	public IRcommand_Func_Call(TEMP dst, String funcName, ArrayList<TEMP> args)
	{
		this.dst = dst;
		this.funcName = funcName;
		this.args = args;
	}

	@Override
    public String toString() {
        String result = "IRcommand_Func_Call: funcName=" + funcName + ", args=" + args.toString();
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
		MIPSGenerator gen = MIPSGenerator.getInstance();

		// Handle predefined functions
		if (funcName.equals("PrintInt") && args.size() == 1) {
			gen.print_int(args.get(0));
			// PrintInt doesn't return a value, so no need to handle dst
			return;
		}
		// Add other predefined functions here (e.g., PrintString, Allocate, etc.)

		// --- Standard Function Call --- 

		// 1. Push arguments onto stack (in reverse order)
		int argSpace = args.size() * 4;
		if (argSpace > 0) {
			// Allocate space on stack for arguments
			gen.addi_imm("$sp", "$sp", -argSpace);
			
			// Store arguments
			for (int i = 0; i < args.size(); i++) {
				TEMP argTemp = args.get(i);
				// Calculate offset relative to the *new* $sp
				// Args pushed ..., arg1, arg0. arg0 is at $sp+0, arg1 at $sp+4, ...
				// If pushed in reverse: argN-1 at $sp+0, ... arg0 at $sp+(N-1)*4
				// Let's push 0 to N-1: arg0 at $sp+0, arg1 at $sp+4, ...
				// Switched to standard order push for simplicity with offset calc below.
				int offset = i * 4; 
				gen.sw_sp(argTemp, offset); // sw arg_temp, offset($sp)
			}
		}

		// 2. Call the function
		String labelToJumpTo = IR.getInstance().getFunctionLabel(this.funcName);
		if (labelToJumpTo == null) {
			System.err.printf("ERROR: Could not find label for function '%s' during MIPS generation.\n", this.funcName);
			// Potentially generate an error or halt? For now, skip the call.
			return; 
		}
		gen.jal(labelToJumpTo);

		// 3. Clean up stack (remove arguments)
		if (argSpace > 0) {
			gen.addi_imm("$sp", "$sp", argSpace);
		}

		// 4. Handle return value (if function returns one)
		if (dst != null) {
			gen.move_from_v0(dst); // dst = $v0
		}
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(args);
	}
}

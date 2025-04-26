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

		if (this.funcName.equals("PrintInt")) {
			System.out.println("DEBUG: IRcommand_Func_Call calling PrintInt");
			gen.print_int(args.get(0));
			return;
		}
		// 0. Save $v0 (register 2) - Do this for ALL calls for simplicity
		final String V0_REG_NAME = "$v0"; // Use register name
		final int V0_SAVE_AREA_SIZE = 4;
		gen.addi_imm("$sp", "$sp", -V0_SAVE_AREA_SIZE); // Allocate space for $v0
		gen.sw_reg_sp(V0_REG_NAME, 0); // sw $v0, 0($sp)

		// 1. Push arguments onto stack
		int argSpace = args.size() * 4;
		if (argSpace > 0) {
			gen.addi_imm("$sp", "$sp", -argSpace);
			for (int i = 0; i < args.size(); i++) {
				TEMP argTemp = args.get(i);
				int offset = i * 4; 
				// Store argument ONLY if its TEMP has a register allocated
				if (IR.getInstance().getRegister(argTemp) >= 0) {
 					gen.sw_sp(argTemp, offset); // sw arg_temp, offset($sp)
				} else {
					// If argument TEMP not allocated, do nothing. The space is allocated
					// on the stack, but left uninitialized. This is safe if the 
					// liveness analysis correctly determined the arg wasn't needed.
				}
			}
		}

		// 2. Call the function
		String labelToJumpTo = IR.getInstance().getFunctionLabel(this.funcName);

		if (labelToJumpTo == null) {
			System.err.printf("ERROR: Could not find label for function '%s' during MIPS generation.\n", this.funcName);
			// Clean up stack (args and saved v0) before returning
			if (argSpace > 0) gen.addi_imm("$sp", "$sp", argSpace);
			// Restore v0 first before deallocating its space
			gen.lw_reg_sp(V0_REG_NAME, 0); // lw $v0, 0($sp)
			gen.addi_imm("$sp", "$sp", V0_SAVE_AREA_SIZE); 
			return; 
		}
		gen.jal(labelToJumpTo);

		// 3. Clean up stack (remove arguments)
		if (argSpace > 0) {
			gen.addi_imm("$sp", "$sp", argSpace);
		}

		// 4. Restore $v0 
		gen.lw_reg_sp(V0_REG_NAME, 0); // lw $v0, 0($sp)
		gen.addi_imm("$sp", "$sp", V0_SAVE_AREA_SIZE); // Deallocate space for $v0

		// 5. Handle return value (move from the now restored $v0)
		// Check dst exists and is allocated BEFORE trying to move $v0
		if (dst != null && IR.getInstance().getRegister(dst) >= 0) {
			gen.move_from_v0(dst); // dst = $v0
		}
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(args);
	}
}

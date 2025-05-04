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
import java.util.HashSet;
import java.util.List;
import MIPS.MIPSGenerator;

public class IRcommand_Class_Method_Call extends IRcommand {

	public TEMP objAddrTemp;
	public int methodOffset;
	public List<TEMP> args;

	public IRcommand_Class_Method_Call(TEMP dst, TEMP objAddrTemp, int methodOffset, List<TEMP> args) {
		this.dst = dst;
		this.objAddrTemp = objAddrTemp;
		this.methodOffset = methodOffset;
		this.args = args != null ? args : new ArrayList<>();
	}

	@Override
	public String toString() {
		String result = "IRcommand_Class_Method_Call: methodoffset=" + methodOffset + ", objAddrTemp=" + objAddrTemp;
		if (dst != null) {
			result += ", dst=" + dst;
		}
		result += ", args=" + args.toString();
		return result;
	}

	public void staticAnalysis() {
		if (!objAddrTemp.initialized || args.stream().anyMatch(temp -> !temp.initialized))
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		HashSet<TEMP> live = new HashSet<>(args);
		live.add(objAddrTemp);
		return live;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// Determine the effective object address TEMP
		TEMP effectiveObjAddrTemp = this.objAddrTemp;
		if (this.objAddrTemp == null) {
			// Implicit 'this' call: Load 'this' from the frame pointer (0($fp))
			// NOTE: Creating a fresh TEMP here relies on MIPSGenerator's lw_fp
			// correctly handling a TEMP that wasn't present during register allocation.
			// This usually works if lw_fp internally maps it to a transient register for
			// the load.
			generator.lw_fp(effectiveObjAddrTemp, 0); // Load 0($fp) into the register for the new TEMP
		}

		// Stack setup for arguments + 'this'
		int argCount = args != null ? args.size() : 0;
		int totalArgsSize = (argCount + 1) * 4; // +1 for 'this'
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -totalArgsSize);

		// Store arguments (if any)
		int currentArgOffset = totalArgsSize - 4;
		if (args != null) {
			for (int i = args.size() - 1; i >= 0; i--) {
				generator.sw_sp(args.get(i), currentArgOffset); // Store arg TEMP relative to $sp
				currentArgOffset -= 4;
			}
		}

		// Store 'this' pointer (using the effective TEMP)
		generator.sw_sp(effectiveObjAddrTemp, 0); // Store at 0($sp)

		// Null check on 'this' (using the effective TEMP)
		generator.genNullCheck(effectiveObjAddrTemp);

		// Load VMT address from object (using the effective TEMP)
		String vmtAddrReg = MIPSGenerator.TEMP_REG_1; // Use $s0 for VMT address
		// Use the lw_offset version that takes TEMP as base
		generator.lw_offset(vmtAddrReg, 0, effectiveObjAddrTemp);

		// Load method address from VMT (using vmtAddrReg = $s0 as base)
		String methodAddrReg = MIPSGenerator.TEMP_REG_2; // Use $s1 for method address
		generator.lw_offset(methodAddrReg, methodOffset, vmtAddrReg);

		// Call method
		generator.jalr(methodAddrReg); // Call address in $s1

		// Clean up stack
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, totalArgsSize);

		// Move result if needed
		if (dst != null) {
			generator.move_from_v0(dst);
		}
	}
}

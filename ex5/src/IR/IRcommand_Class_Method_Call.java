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

		int argStackSize = (args.size() + 1) * 4;
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -argStackSize);
		int currentArgOffset = argStackSize - 4;

		for (int i = args.size() - 1; i >= 0; i--) {
			generator.sw_sp(args.get(i), currentArgOffset);
			currentArgOffset -= 4;
		}

		generator.sw_sp(objAddrTemp, 0);

		generator.genNullCheck(objAddrTemp);

		String vmtAddrReg = MIPSGenerator.TEMP_REG_1;
		generator.lw_offset(vmtAddrReg, 0, objAddrTemp);

		String methodAddrReg = MIPSGenerator.TEMP_REG_2;
		generator.lw_offset(methodAddrReg, methodOffset, vmtAddrReg);

		generator.jalr(methodAddrReg);

		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, argStackSize);

		if (dst != null) {
			generator.move_from_v0(dst);
		}
	}
}

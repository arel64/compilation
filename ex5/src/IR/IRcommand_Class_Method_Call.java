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
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Class_Method_Call extends IRcommand
{
	public TEMP src;
	public String method;
	public ArrayList<TEMP> args;
	
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

	public void staticAnalysis() {
		if (!src.initialized || args.stream().anyMatch(temp -> !temp.initialized))
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		args.add(src);
		return new HashSet<TEMP>(args);
	}

	@Override
	public void MIPSme() {
		MIPSGenerator gen = MIPSGenerator.getInstance();
		IR irInstance = IR.getInstance();
		List<TEMP> callerSavedRegistersToSave = new ArrayList<>();
		Map<TEMP, Integer> registerAllocation = irInstance.getRegisterAllocation();
		HashSet<TEMP> liveOut = irInstance.getLiveOutSet(this.index);

		// --- ORIGINAL SAVING LOGIC (using liveOut) ---
		// Now that liveness analysis is (hopefully) accurate, only save $t registers
		// that are actually live after this call instruction.
		for (TEMP liveTemp : liveOut) {
			if (registerAllocation != null && registerAllocation.containsKey(liveTemp)) { 
				int color = registerAllocation.get(liveTemp);
				// Use the constant: check if color is within the range [0, K-1]
				if (color >= 0 && color < MIPSGenerator.NUM_ALLOCATABLE_REGISTERS) { 
					callerSavedRegistersToSave.add(liveTemp);
				}
			} else if (registerAllocation == null) {
				System.err.println("DEBUG WARNING: registerAllocation map is null in MIPSme for index " + this.index);
			} else if (!registerAllocation.containsKey(liveTemp)) {
				// This might happen if a TEMP is live but wasn't allocated a register (e.g., spilled) - should be ok.
				// System.err.println("DEBUG WARNING: registerAllocation does not contain TEMP " + liveTemp + " in MIPSme for index " + this.index + " LiveOut=" + liveOut.toString() );
			}
		}
		// --- END ORIGINAL SAVING LOGIC ---

		int registersSaveSpace = callerSavedRegistersToSave.size() * 4;
		if (registersSaveSpace > 0) {
			gen.addi_imm("$sp", "$sp", -registersSaveSpace);
			for (int i = 0; i < callerSavedRegistersToSave.size(); i++) {
				TEMP tempToSave = callerSavedRegistersToSave.get(i);
				int offset = i * 4;
				gen.sw_sp(tempToSave, offset);
			}
		}

		int argSpace = args.size() * 4;
		if (argSpace > 0) {
			gen.addi_imm("$sp", "$sp", -argSpace);
			for (int i = 0; i < args.size(); i++) {
				TEMP argTemp = args.get(i);
				int offset = i * 4;
				if (irInstance.getRegister(argTemp) >= 0) {
 					gen.sw_sp(argTemp, offset);
				}
			}
		}

		String labelToJumpTo = irInstance.getFunctionLabel(this.src + "." + this.method);

		if (labelToJumpTo == null) {
			throw new RuntimeException("ERROR: Could not find label for function '" + this.funcName + "' during MIPS generation.");
		}
		gen.jal(labelToJumpTo);

		if (argSpace > 0) {
			gen.addi_imm("$sp", "$sp", argSpace);
		}

		// --- BEGIN Register Restoring ---
		// Restore caller-saved registers from stack (in reverse order of saving)
		if (registersSaveSpace > 0) {
            gen.appendRawInstruction("\t# DEBUG: Restoring registers loop start\n");
			for (int i = callerSavedRegistersToSave.size() - 1; i >= 0; i--) {
				TEMP tempToRestore = callerSavedRegistersToSave.get(i);
				int offset = i * 4;
				gen.lw_sp(tempToRestore, offset); // lw temp_reg, offset($sp)
			}
            gen.appendRawInstruction("\t# DEBUG: Adjusting SP after restore\n");
			gen.addi_imm("$sp", "$sp", registersSaveSpace);
		}
		// --- END Register Restoring ---

		// 5. Handle return value (original code, moved after restore)
		// Check dst exists AND is live out BEFORE trying to move $v0
		if (dst != null && irInstance.getRegister(dst) >= 0 && liveOut != null && liveOut.contains(dst)) {
			gen.move_from_v0(dst); // dst = $v0
		}
	}
}


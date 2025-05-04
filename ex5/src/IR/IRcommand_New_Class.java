/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;
import MIPS.MIPSGenerator;

public class IRcommand_New_Class extends IRcommand {
	TEMP sizeTemp; // TEMP holding the size to allocate
	String className; // Name of the class being instantiated (for VMT lookup)

	public IRcommand_New_Class(TEMP dst, TEMP sizeTemp, String className) {
		System.out.println("IRcommand_New_Class: dst=" + dst + ", sizeTemp=" + sizeTemp + ", className=" + className);
		this.dst = dst;
		this.sizeTemp = sizeTemp;
		this.className = className;
	}

	@Override
	public String toString() {
		return String.format("NEW_CLASS: %s := new %s (size=%s)", dst, className, sizeTemp);
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		// The size calculation TEMP is used
		return new HashSet<TEMP>(Arrays.asList(sizeTemp));
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// 1. Allocate memory on the heap
		// malloc(TEMP dst, TEMP size) puts the result address in the dst TEMP
		generator.malloc(dst, sizeTemp);

		// 2. Load the address of the Virtual Method Table (VMT)
		String vmtLabel = "VMT_" + className;
		String tempAddrReg = MIPSGenerator.TEMP_REG_1; // Use $s0 as temporary
		generator.la(tempAddrReg, vmtLabel);

		// 3. Store the VMT address at the beginning of the allocated object (offset 0)
		// Use the correct helper: sw_offset_from_reg(String srcReg, int offset, TEMP
		// baseTemp)
		generator.sw_offset_from_reg(tempAddrReg, 0, dst); // dst holds the object address

		// 6. Save caller-saved registers ($t0-$t9) before the call
		int stackOffsetForSave = 0; // Offset within the 40-byte save area
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -4 * MIPSGenerator.NUM_ALLOCATABLE_REGISTERS); // Allocate
																												// space
																												// for
																												// $t0-$t9
		for (int i = 0; i < MIPSGenerator.NUM_ALLOCATABLE_REGISTERS; i++) {
			// Use sw_reg_sp for clarity, offset is relative to the new SP
			generator.sw_reg_sp("$t" + i, stackOffsetForSave + (i * 4));
		}

		// --- NEW: Call Implicit Constructor ---
		// 4. Construct constructor label
		String constructorLabel = "__init_" + className + "_start";

		// 5. Push 'this' argument (the object address currently in TEMP dst)
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -4); // Make space on stack
		// Use sw_offset_from_temp to store the value from the TEMP dst onto the stack
		// ($sp)
		generator.sw_offset_from_temp(dst, 0, MIPSGenerator.SP);

		// 7. Call the constructor
		generator.jal(constructorLabel);

		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, 4);
		// 8. Restore caller-saved registers ($t0-$t9) after the call
		// $sp should be unchanged by the callee
		for (int i = 0; i < MIPSGenerator.NUM_ALLOCATABLE_REGISTERS; i++) {
			// Use lw_reg_sp for clarity, offset is relative to the current SP
			generator.lw_reg_sp("$t" + i, stackOffsetForSave + (i * 4));
		}
		generator.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, 4 * MIPSGenerator.NUM_ALLOCATABLE_REGISTERS); // Deallocate
																												// space
																												// for
																												// $t0-$t9

		// 9. Clean up stack (remove 'this' argument)
		// --- End Constructor Call ---
	}

	@Override
	public void staticAnalysis() {
		// Define uses/defines for static analysis
		if (sizeTemp != null && !sizeTemp.initialized) {
			if (dst != null)
				dst.initialized = false;
		}
		super.staticAnalysis();
	}
}

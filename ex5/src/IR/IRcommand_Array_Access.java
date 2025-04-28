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

public class IRcommand_Array_Access extends IRcommand
{
	public TEMP arr;
	public TEMP index;
	// public TEMP temp; // No longer needed for MIPS generation

	public IRcommand_Array_Access(TEMP dst, TEMP arr, TEMP index/*, TEMP temp removed */)
	{
		this.dst = dst;
		this.arr = arr;
		this.index = index;
		// this.temp = temp; // Removed
	}

	@Override
	public String toString() {
		// Update toString if needed, as 'temp' is removed
		return "IRcommand_Array_Access: dst=" + dst + ", arr=" + arr + ", index=" + index;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// Get the globally allocated physical registers for the input/output TEMPs
		String arrReg = generator.tempToRegister(this.arr);
		String indexReg = generator.tempToRegister(this.index);
		String dstReg = generator.tempToRegister(this.dst); // Get the destination register

		// Use $t8 (TEMP_REG_1) for the intermediate address calculation
		generator.sll_registers(MIPSGenerator.TEMP_REG_1, indexReg, 2);       // $t8 = indexReg * 4
		generator.add_registers(MIPSGenerator.TEMP_REG_1, arrReg, MIPSGenerator.TEMP_REG_1); // $t8 = arrReg + $t8 (offset)

		// Load the value using the final address in $t8 into the destination register
		// lw dstReg, 0($t8)
		generator.lw_offset(dstReg, 0, MIPSGenerator.TEMP_REG_1);
	}

	public void staticAnalysis() {
		if (!index.initialized || !arr.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		// Correct: Only includes TEMPs read by the command
		return new HashSet<TEMP>(Arrays.asList(arr, index));
	}
}

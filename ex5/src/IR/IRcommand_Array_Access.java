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

	public IRcommand_Array_Access(TEMP dst, TEMP arr, TEMP index/*, TEMP temp removed */)
	{
		this.dst = dst;
		this.arr = arr;
		this.index = index;
	}

	@Override
	public String toString() {
		// Update toString if needed, as 'temp' is removed
		return "IRcommand_Array_Access: dst=" + dst + ", arr=" + arr + ", index=" + index;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// Use TEMP_REG_1 ($s0) for the intermediate address calculation
		String tempAddrReg = MIPSGenerator.TEMP_REG_1;

		// 1. Calculate offset: offset = index * 4
		generator.sll_temp_into_reg(tempAddrReg, this.index, 2); // tempAddrReg = index * 4

		// 2. Calculate final address: final_addr = arr + offset
		generator.add_temp_into_reg(tempAddrReg, this.arr, tempAddrReg); // tempAddrReg = arr + offset

		// 3. Load the value from the final address into the destination TEMP
		// lw dst, 0(tempAddrReg)
		generator.lw_offset_from_reg(this.dst, 0, tempAddrReg);
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

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

public class IRcommand_Array_Set extends IRcommand
{
	public TEMP array;
	public TEMP index;
	public TEMP value;

	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value)
	{
		this.array = array;
		this.index = index;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "IRcommand_Array_Set: array=" + array + ", index=" + index + ", value=" + value;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// Use TEMP_REG_1 ($s0) for the intermediate address calculation
		String tempAddrReg = MIPSGenerator.TEMP_REG_1;

		// 1. Calculate offset: offset = index * 4
		generator.sll_temp_into_reg(tempAddrReg, this.index, 2); // tempAddrReg = index * 4

		// 2. Calculate final address: final_addr = array + offset
		generator.add_temp_into_reg(tempAddrReg, this.array, tempAddrReg); // tempAddrReg = array + offset

		// 3. Store the value into the final address
		// sw value, 0(tempAddrReg)
		generator.sw_offset_from_temp(this.value, 0, tempAddrReg);
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(array, index, value));
	}

}

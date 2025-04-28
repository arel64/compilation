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
	// public TEMP temp; // No longer needed for MIPS generation

	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value/*, TEMP temp removed */)
	{
		this.array = array;
		this.index = index;
		this.value = value;
		// this.temp = temp; // Removed
	}
	
	@Override
	public String toString() {
		// Update toString if needed, as 'temp' is removed
		return "IRcommand_Array_Set: array=" + array + ", index=" + index + ", value=" + value;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		// Get the globally allocated physical registers for the input TEMPs
		String arrayReg = generator.tempToRegister(this.array);
		String indexReg = generator.tempToRegister(this.index);
		String valueReg = generator.tempToRegister(this.value);

		// Use $t8 (TEMP_REG_1) for the intermediate address calculation
		generator.sll_registers(MIPSGenerator.TEMP_REG_1, indexReg, 2);       // $t8 = indexReg * 4
		generator.add_registers(MIPSGenerator.TEMP_REG_1, arrayReg, MIPSGenerator.TEMP_REG_1); // $t8 = arrayReg + $t8 (offset)

		// Store the value: sw valueReg, 0($t8)
		generator.sw_offset(valueReg, 0, MIPSGenerator.TEMP_REG_1);
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(array, index, value));
	}

}

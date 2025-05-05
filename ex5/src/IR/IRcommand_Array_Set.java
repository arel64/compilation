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

public class IRcommand_Array_Set extends IRcommand {
	public TEMP array;
	public TEMP index;
	public TEMP value;

	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value) {
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

		String arrayBaseReg = MIPSGenerator.TEMP_REG_2; // $s1 for array base address
		String indexReg = MIPSGenerator.TEMP_REG_3; // Use $s2 constant
		String addressReg = MIPSGenerator.TEMP_REG_4; // Use $s3 constant

		// 0. Move array base address and index value into dedicated registers
		generator.move_from_temp_to_reg(arrayBaseReg, this.array); // $s1 = array base
		generator.move_from_temp_to_reg(indexReg, this.index); // $s2 = index

		// --- Bounds Checking --- Call the centralized function ---
		generator.genBoundsCheck(arrayBaseReg, indexReg);
		// The code below only executes if the check passes

		// --- Bounds check passed (implicit after genBoundsCheck) ---

		// 4. Calculate offset: offset = index * 4
		generator.sll_registers(addressReg, indexReg, 2); // $s3 = $s2 * 4

		// 5. Calculate final address: final_addr = arrayBase + offset
		generator.add_registers(addressReg, arrayBaseReg, addressReg); // $s3 = $s1 + $s3

		// 6. Store the value into the final address
		generator.sw_offset_from_temp(this.value, 0, addressReg); // Store value TEMP into 0($s3)

	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(array, index, value));
	}

}

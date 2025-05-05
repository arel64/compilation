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

public class IRcommand_Array_Access extends IRcommand {
	public TEMP arr;
	public TEMP index;

	public IRcommand_Array_Access(TEMP dst, TEMP arr, TEMP index/* , TEMP temp removed */) {
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
		String access_violation_label = IRcommand.getFreshLabel("Access_Violation");
		String check_passed_label = IRcommand.getFreshLabel("Bounds_Check_OK");

		// Use dedicated temporary registers
		String lengthReg = MIPSGenerator.TEMP_REG_1; // $s0 for length
		String arrayBaseReg = MIPSGenerator.TEMP_REG_2; // $s1 for array base address
		String indexReg = MIPSGenerator.TEMP_REG_3; // Use $s2 constant
		String addressReg = MIPSGenerator.TEMP_REG_4; // Use $s3 constant
		String tempCompareReg = MIPSGenerator.TEMP_REG_CMP; // Use $s4 constant

		// --- Bounds Checking ---

		// 0. Move array base address and index value into dedicated registers
		generator.move_from_temp_to_reg(arrayBaseReg, this.arr); // $s1 = arr
		generator.move_from_temp_to_reg(indexReg, this.index); // $s2 = index

		// 1. Check index < 0
		generator.bltz_reg(indexReg, access_violation_label); // Use $s2

		// 2. Load array length (from offset -4 relative to array base pointer in $s1)
		generator.lw_offset(lengthReg, -4, arrayBaseReg); // lw $s0, -4($s1)

		// 3. Check index >= length (using slt: index < length == false)
		generator.slt_registers(tempCompareReg, indexReg, lengthReg); // slt $s4, $s2, $s0
		generator.beq_registers(tempCompareReg, MIPSGenerator.ZERO, access_violation_label); // beq $s4, $zero,
																								// violation

		// --- Bounds check passed ---

		// 4. Calculate offset: offset = index * 4
		generator.sll_registers(addressReg, indexReg, 2); // $s3 = $s2 * 4

		// 5. Calculate final address: final_addr = arrayBase + offset
		generator.add_registers(addressReg, arrayBaseReg, addressReg); // $s3 = $s1 + $s3

		// 6. Load the value from the final address into the destination TEMP
		generator.lw_offset_from_reg(this.dst, 0, addressReg); // lw dstReg, 0($s3)

		// 7. Jump over error handling
		generator.jump(check_passed_label);

		// --- Error Handling ---
		generator.label(access_violation_label);
		generator.print_string_from_label("string_access_violation");
		generator.exit();

		// --- End Label ---
		generator.label(check_passed_label);
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

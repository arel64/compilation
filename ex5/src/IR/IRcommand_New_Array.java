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
import MIPS.*; // Assuming MIPSGenerator is in this package
import SYMBOL_TABLE.SYMBOL_TABLE;
import IR.IRcommand; // Import base class to use getFreshLabel
import TYPES.TYPE; // Import TYPE
import TYPES.TYPE_VOID; // Import TYPE_VOID
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_New_Array extends IRcommand
{
	public String type;
	public TEMP size; // TEMP holding the number of elements
	
	public IRcommand_New_Array(TEMP dst, String type, TEMP size)
	{
		this.dst = dst;
		this.type = type;
		this.size = size;
	}

	@Override
    public String toString() {
        return String.format("IRcommand_New_Array: dst=%s type=%s, size=%s", 
                             dst, type, size);
    }

	public void staticAnalysis() {
		if (!size.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(size));
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();

		String label_Invalid_Size = IRcommand.getFreshLabel("Invalid_Array_Size");
		String label_Alloc_Done   = IRcommand.getFreshLabel("New_Array_Alloc_Done");

		// Get element size from Symbol Table using the type name
		int elementSize = 4; // Default/fallback size
		

		// 1. Check if size < 0. 
		// Note: 'size' TEMP holds the number of elements
		generator.bltz(size, label_Invalid_Size); // Branch if size TEMP < 0

		// 2. Calculate total bytes needed: (size * elementSize) + 4 (for length header)
		// Use dedicated temp registers $s0, $s1 from MIPSGenerator
		String tempReg1 = MIPSGenerator.TEMP_REG_1; // e.g., $s0
		String tempReg2 = MIPSGenerator.TEMP_REG_2; // e.g., $s1
		generator.li_imm(tempReg1, elementSize);           // tempReg1 = elementSize
		generator.mul_temp_reg(tempReg2, size, tempReg1); // tempReg2 = size * tempReg1
		generator.addi_imm(tempReg2, tempReg2, 4);       // tempReg2 = total bytes (size*elemSize + 4)

		// 3. Allocate memory using malloc (syscall 9)
		// Pass the allocation size (in tempReg2) to malloc via $a0
		generator.malloc(dst, tempReg2); // Modifies $a0, $v0. Result pointer in dstReg <- $v0.

		// 4. Runtime check: Check if allocation failed (optional, malloc might handle this)
		// Example: Could add: generator.beq(dst, MIPSGenerator.ZERO_TEMP, label_OutOfMemory); 
		// Requires a ZERO_TEMP in TEMP_FACTORY or passing $zero register name

		// 5. Store the original size (number of elements) in the header (at offset 0)
		generator.sw_offset(size, 0, dst); // sw size_TEMP, 0(dst_TEMP)

		// 6. Adjust the destination pointer to point *after* the header
		generator.addi(dst, dst, 4); // dst = dst + 4

		generator.jump(label_Alloc_Done);

		generator.label(label_Invalid_Size);
		generator.print_string_from_label("string_invalid_array_size");
		generator.exit();

		generator.label(label_Alloc_Done);
	}

}

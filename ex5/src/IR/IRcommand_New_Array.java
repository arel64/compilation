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
        
		TYPE elementType = SYMBOL_TABLE.getInstance().find(this.type);
		if (elementType != null) {
			elementSize = elementType.getSize();
		} else {
				System.err.printf("ERROR: Type '%s' not found in symbol table for array element size lookup. Using default size 4.\n", this.type);
				// Handle error: use default size
		}
		if (elementSize <= 0 && !(elementType instanceof TYPE_VOID)) { 
				System.err.printf("ERROR: Invalid element size (%d) calculated for type '%s'. Using default size 4.\n", elementSize, this.type);
				elementSize = 4; // Reset to default? 
		}

    
		// 1. Check if size <= 0. 
        // Note: 'size' TEMP holds the number of elements
		generator.blez(size, label_Invalid_Size);

		// 2. Calculate total bytes: (size * elementSize) 
		generator.mul_imm(size, size, elementSize);  // tTotalBytes = size * tElementSize

		generator.malloc(dst, size);
		
        generator.jump(label_Alloc_Done);

		generator.label(label_Invalid_Size);
        generator.print_string_from_label("string_invalid_array_size");
		generator.exit();

        generator.label(label_Alloc_Done);
	}

}

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

public class IRcommand_New_Class extends IRcommand
{
	TEMP dst; // Destination TEMP for the object pointer
	TEMP sizeTemp; // TEMP holding the size to allocate
	String className; // Name of the class being instantiated (for VMT lookup)
	
	public IRcommand_New_Class(TEMP dst, TEMP sizeTemp, String className)
	{
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
	public void MIPSme()
	{
		MIPSGenerator generator = MIPSGenerator.getInstance();
		// String dstReg = generator.tempToRegister(dst); <-- Removed

		// 1. Allocate memory on the heap
		// malloc(TEMP dst, TEMP size) puts the result address in the dst TEMP
		generator.malloc(dst, sizeTemp); 

		// 2. Load the address of the Virtual Method Table (VMT)
		String vmtLabel = "VMT_" + className;
		String tempAddrReg = MIPSGenerator.TEMP_REG_1; // Use $s0 as temporary
		generator.la(tempAddrReg, vmtLabel);

		// 3. Store the VMT address at the beginning of the allocated object (offset 0)
		// Use the correct helper: sw_offset_from_reg(String srcReg, int offset, TEMP baseTemp)
		generator.sw_offset_from_reg(tempAddrReg, 0, dst);
	}

	@Override
	public void staticAnalysis() {
		// Define uses/defines for static analysis
		if (sizeTemp != null && !sizeTemp.initialized) {
            if (dst != null) dst.initialized = false;
        }
		super.staticAnalysis();
	}
}

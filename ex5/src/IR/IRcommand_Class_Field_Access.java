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

public class IRcommand_Class_Field_Access extends IRcommand
{
	public TEMP dst;
	public TEMP objAddr;
	public int fieldOffset;

	public IRcommand_Class_Field_Access(TEMP dst, TEMP objAddr, int fieldOffset)
	{
		this.dst = dst;
		this.objAddr = objAddr;
		this.fieldOffset = fieldOffset;
	}
	@Override
	public String toString() {
		return String.format("CLASS_FIELD_ACCESS: %s := %s (offset %d)", dst, objAddr, fieldOffset);
	}

	public void staticAnalysis() {
		if (!objAddr.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(objAddr));
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator generator = MIPSGenerator.getInstance();
		generator.genLoadField(dst, objAddr, fieldOffset);
	}
}

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
	public TEMP src;
	public int offset;
	public String className;
	public String fieldName;
	
	public IRcommand_Class_Field_Access(TEMP dst, TEMP src, int offset, String className, String fieldName)
	{
		this.dst = dst;
		this.src = src;
		this.offset = offset;
		this.className = className;
		this.fieldName = fieldName;
	}
	@Override
	public String toString() {
		return String.format("CLASS_FIELD_ACCESS: %s := %s.%s (offset %d)", dst, src, fieldName, offset);
	}

	public void staticAnalysis() {
		if (!src.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(src));
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator.getInstance().lw_offset(dst, offset, src);
	}
}

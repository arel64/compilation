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

public class IRcommand_Class_Field_Access extends IRcommand
{
	public TEMP src;
	public String field;
	
	public IRcommand_Class_Field_Access(TEMP dst, TEMP src, String field)
	{
		this.dst = dst;
		this.src = src;
		this.field = field;
	}
	@Override
	public String toString() {
		return "IRcommand_Class_Field_Access: dst=" + dst + ", src=" + src + ", field=" + field;
	}

	public void staticAnalysis() {
		if (!src.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(src));
	}

}

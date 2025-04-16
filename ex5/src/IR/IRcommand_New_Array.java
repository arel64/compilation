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

public class IRcommand_New_Array extends IRcommand
{
	public String type;
	public TEMP size;
	
	public IRcommand_New_Array(TEMP dst, String type, TEMP size)
	{
		this.dst = dst;
		this.type = type;
		this.size = size;
	}

	@Override
    public String toString() {
        return "IRcommand_New_Array: dst=" + dst + " type=" + type + ", size=" + size;
    }

	public void staticAnalysis() {
		if (!size.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(size));
	}
}

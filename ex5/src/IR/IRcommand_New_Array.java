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
	TEMP dst;
	String type;
	TEMP size;
	
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

	public void staticAnanlysis() {
		if (!size.initialized)
			dst.initialized = false;
		super.staticAnanlysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(size));
	}
}

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

public class IRcommand_New_Array extends IRcommand
{
	TEMP dst;
	TEMP size;
	
	public IRcommand_New_Array(TEMP dst, TEMP size)
	{
		this.dst = dst;
		this.size = size;
	}

	@Override
    public String toString() {
        return "IRcommand_New_Array: dst=" + dst + ", size=" + size;
    }

	public void staticAnanlysis() {
		if (!size.initialized)
			dst.initialized = false;
		super.staticAnanlysis();
	}
}

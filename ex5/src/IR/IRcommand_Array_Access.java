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

public class IRcommand_Array_Access extends IRcommand
{
	public TEMP src;
	public TEMP index;
	
	public IRcommand_Array_Access(TEMP dst, TEMP src, TEMP index)
	{
		this.dst = dst;
		this.src = src;
		this.index = index;
		IR.getInstance().recordVarTemp("temp_" + dst.getSerialNumber(), dst);
	}
	@Override
	public String toString() {
		return "IRcommand_Array_Access: dst=" + dst + ", src=" + src + ", index=" + index;
	}

	@Override
	public void MIPSme() {
		//MIPSGenerator.getInstance().allocate(var_name);
	}

	public void staticAnanlysis() {
		if (!index.initialized || !src.initialized)
			dst.initialized = false;
		super.staticAnanlysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(src, index));
	}
}

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

public class IRcommand_Array_Access extends IRcommand
{
	public TEMP src;
	public TEMP index;
	
	public IRcommand_Array_Access(TEMP dst, TEMP src, TEMP index)
	{
		this.dst = dst;
		this.src = src;
		this.index = index;
	}
	@Override
	public String toString() {
		return "IRcommand_Array_Access: dst=" + dst + ", src=" + src + ", index=" + index;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();
		// Calculate offset = index * 4 (assuming 4-byte elements) into dst
		generator.sll(dst, index, 2); // dst = index << 2
		// Calculate address = src (base) + offset (in dst) into dst
		generator.add(dst, src, dst);   // dst = src + dst (offset)
		// Load the value from memory: dst = *(address in dst)
		generator.lw_temp_offset(dst, 0, dst); // dst = *(dst)
	}

	public void staticAnalysis() {
		if (!index.initialized || !src.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(src, index,dst));
	}
}

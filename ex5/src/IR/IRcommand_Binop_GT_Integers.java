/***********/
/* PACKAGE */
/***********/
package IR;

import MIPS.MIPSGenerator;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Binop_GT_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;

	public IRcommand_Binop_GT_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String toString() {
		return "IRcommand_Binop_GT_Integers: dst=" + dst + ", t1=" + t1 + ", t2=" + t2;
	}

	public void staticAnalysis() {
		if (!t1.initialized || !t2.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	@Override
	public void MIPSme()
	{
		// Generate a unique label for the comparison
		String trueLabel = "gt_true";
		String endLabel = "gt_end";
		
		// Compare t1 > t2 (this is the same as t2 < t1)
		// We don't have a direct "greater than" instruction, so we use "less than" in reverse
		MIPSGenerator.getInstance().blt(t2, t1, trueLabel);
		
		// If not greater than, set result to 0
		MIPSGenerator.getInstance().li(dst, 0);
		MIPSGenerator.getInstance().jump(endLabel);
		
		// If greater than, set result to 1
		MIPSGenerator.getInstance().label(trueLabel);
		MIPSGenerator.getInstance().li(dst, 1);
		
		// End of comparison
		MIPSGenerator.getInstance().label(endLabel);
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(t1, t2));
	}
}

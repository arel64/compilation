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

public class IRcommand_Binop_EQ_Integers extends IRcommand {
	public TEMP t1;
	public TEMP t2;

	public IRcommand_Binop_EQ_Integers(TEMP dst, TEMP t1, TEMP t2) {
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String toString() {
		return "IRcommand_Binop_EQ_Integers: dst=" + dst + ", t1=" + t1 + ", t2=" + t2;
	}

	public void staticAnalysis() {
		if (!t1.initialized || !t2.initialized)
			dst.initialized = false;
		super.staticAnalysis();
	}

	@Override
	public void MIPSme() {
		MIPSGenerator gen = MIPSGenerator.getInstance();
		if (dst == null || IR.getInstance().getRegister(dst) < 0) {
			// Optional: Add check if dst is used
			return;
		}

		// Generate unique labels
		String trueLabel = getFreshLabel("eq_true");
		String endLabel = getFreshLabel("eq_end");

		// *** Corrected equality check ***
		// Branch to trueLabel if t1 == t2
		gen.beq(t1, t2, trueLabel); // Use t1 and t2 directly

		// If condition is false (t1 != t2), result (dst) is 0
		gen.li(dst, 0);
		gen.jump(endLabel);

		// If condition is true (t1 == t2), result (dst) is 1
		gen.label(trueLabel);
		gen.li(dst, 1);

		// End label
		gen.label(endLabel);
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(t1, t2));
	}
}

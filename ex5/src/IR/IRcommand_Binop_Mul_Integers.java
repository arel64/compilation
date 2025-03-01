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
import MIPS.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Binop_Mul_Integers extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	
	public IRcommand_Binop_Mul_Integers(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String toString() {
		return "IRcommand_Binop_Mul_Integers: dst=" + dst + ", t1=" + t1 + ", t2=" + t2;
	}

	public void staticAnanlysis() {
		if (!t1.initialized || !t2.initialized)
			dst.initialized = false;
		super.staticAnanlysis();
	}

	public void MIPSme()
	{
		MIPSGenerator.getInstance().mul(dst,t1,t2);
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(t1, t2));
	}
}

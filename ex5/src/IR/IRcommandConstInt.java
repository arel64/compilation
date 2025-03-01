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

public class IRcommandConstInt extends IRcommand
{
	public int value;
	
	public IRcommandConstInt(TEMP dst,int value)
	{
		this.dst = dst;
		this.value = value;
	}

	@Override
    public String toString() {
        return "IRcommandConstInt: dst=" + dst + ", value=" + value;
    }

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().li(dst, value);
	}
}

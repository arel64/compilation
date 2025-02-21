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

public class IRcommandConstInt extends IRcommand
{
	TEMP t;
	int value;
	
	public IRcommandConstInt(TEMP t,int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
    public String toString() {
        return "IRcommandConstInt: t=" + t + ", value=" + value;
    }

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().li(t, value);
	}
}

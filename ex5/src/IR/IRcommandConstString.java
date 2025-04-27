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

public class IRcommandConstString extends IRcommand
{
	public String value;
	
	public IRcommandConstString(TEMP dst, String value)
	{
		this.dst = dst;
		this.value = value;
	}

	@Override
    public String toString() {
        return "IRcommandConstString: dst=" + dst + ", value=" + value;
    }

	@Override
	public void MIPSme() {
		// implement 
		//MIPSGenerator.getInstance().li(dst, value);
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}
}

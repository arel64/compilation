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
import MIPS.MIPSGenerator;
import java.util.HashSet;
import java.util.Arrays;

public class IRcommand_PrintInt extends IRcommand
{
	public TEMP t;
	
	public IRcommand_PrintInt(TEMP t)
	{
		this.t = t;
	}

	@Override
    public String toString() {
        return "IRcommand_PrintInt: t=" + t;
    }
    
    @Override
    public void MIPSme() {
        // Call the MIPS generator to print the integer
        MIPSGenerator.getInstance().print_int(t);
    }

    public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(t));
	}
}

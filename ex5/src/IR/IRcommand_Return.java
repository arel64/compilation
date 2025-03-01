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

public class IRcommand_Return extends IRcommand
{
	TEMP register;
	
	public IRcommand_Return(TEMP register)
	{
		this.register = register;
	}

	@Override
    public String toString() {
        return "IRcommand_Return: register=" + register;
    }

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(register));
	}
}

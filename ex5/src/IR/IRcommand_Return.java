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

public class IRcommand_Return extends IRcommand
{
	public TEMP register;
	
	public IRcommand_Return(TEMP register)
	{
		this.register = register;
	}

	@Override
    public String toString() {
        return "IRcommand_Return: register=" + register;
    }

	public HashSet<TEMP> liveTEMPs() {
		if (register != null) return new HashSet<TEMP>(Arrays.asList(register));
		return new HashSet<TEMP>();
	}

    @Override
    public void MIPSme() {
        MIPSGenerator generator = MIPSGenerator.getInstance();
        if (this.register != null) {
            generator.genMoveReturnValue(this.register);
        }
    }
    
}

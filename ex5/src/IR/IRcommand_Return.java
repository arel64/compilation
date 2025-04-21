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
import MIPS.*;

public class IRcommand_Return extends IRcommand
{
	TEMP src;
	
	public IRcommand_Return(TEMP src)
	{
		this.src = src;
	}

	@Override
    public String toString() {
        return "IRcommand_Return: src=" + src;
    }

	public HashSet<TEMP> liveTEMPs() {
		if (src != null) return new HashSet<TEMP>(Arrays.asList(src));
		return new HashSet<TEMP>();
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator gen = MIPSGenerator.getInstance();

		// Handle return value (if any)
		if (src != null) {
			gen.genMoveReturnValue(src);
		}

		// Epilogue generation (restore registers, deallocate stack)
		// TODO: Use actual frame size calculated during prologue/analysis
		int frameSize = 8; // Placeholder matches prologue
		gen.genEpilogue(frameSize);

		// Jump back to caller
		gen.genReturnJump();
	}
}

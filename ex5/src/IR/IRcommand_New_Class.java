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

public class IRcommand_New_Class extends IRcommand
{
	public String type;
	public int size;
	
	public IRcommand_New_Class(TEMP dst, String type, int size)
	{
		this.dst = dst;
		this.type = type;
		this.size = size;
	}

	@Override
    public String toString() {
        return String.format("NEW_CLASS: %s := new %s (size=%d)", dst, type, size);
    }

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator generator = MIPSGenerator.getInstance();
		generator.li_imm(MIPSGenerator.A0, this.size);
		generator._malloc(dst);
	}
}

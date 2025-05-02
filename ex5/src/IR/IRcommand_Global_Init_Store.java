/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.Set;
import java.util.HashSet;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_Global_Init_Store extends IRcommand
{
	String var_name;
	TEMP srcTemp;


	public IRcommand_Global_Init_Store(String var_name, TEMP srcTemp)
	{
		this.var_name = var_name;
		this.srcTemp = srcTemp;
        this.dst = null;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		// This command is processed in Pass 2 (GLOBAL_INIT context) in IR.java
		// It assumes the value to store is already loaded into srcTemp by a preceding Const command.
		// It stores this value into the global variable's memory location.
        String globalLabel = "global_" + var_name;
		MIPSGenerator.getInstance().sw_global(srcTemp, globalLabel);
	}

    @Override
    public HashSet<TEMP> liveTEMPs() {
        HashSet<TEMP> used = new HashSet<>();
        if (srcTemp != null) {
            used.add(srcTemp);
        }
        return used;
    }

    // Removed deadTEMPs as it's not in the base class and liveness relies on dst field
} 
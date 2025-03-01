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

public class IRcommand_New_Class extends IRcommand
{
	public String type;
	
	public IRcommand_New_Class(TEMP dst, String type)
	{
		this.dst = dst;
		this.type = type;
	}

	@Override
    public String toString() {
        return "IRcommand_New_Class: dst= " + dst + ", type=" + type;
    }
}

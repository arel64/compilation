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

public class IRcommand_New_Class extends IRcommand
{
	TEMP dst;
	String type;
	AST_LIST<TEMP> params;
	
	public IRcommand_New_Class(TEMP dst, String type, AST_LIST<TEMP> params)
	{
		this.dst = dst;
		this.type = type;
		this.params = params;
	}

	@Override
    public String toString() {
        return "IRcommand_New_Class: dst=" + dst + ", type=" + type;
    }
}

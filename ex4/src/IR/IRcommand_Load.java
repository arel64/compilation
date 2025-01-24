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

public class IRcommand_Load extends IRcommand
{
	TEMP dst;
	String var_name;
	
	public IRcommand_Load(TEMP dst,String var_name)
	{
		this.dst = dst;
		this.var_name = var_name;
	}

	@Override
    public String toString() {
        return "IRcommand_Load: dst=" + dst + ", var_name=" + var_name;
    }
}

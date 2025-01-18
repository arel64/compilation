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

public class IRcommand_Return extends IRcommand
{
	TEMP var_name;
	
	public IRcommand_Return(TEMP var_name)
	{
		this.var_name = var_name;
	}
}

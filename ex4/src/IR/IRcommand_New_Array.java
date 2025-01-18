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

public class IRcommand_New_Array extends IRcommand
{
	TEMP dst;
	TEMP size;
	
	public IRcommand_New_Array(TEMP dst, TEMP size)
	{
		this.dst = dst;
		this.size = size;
	}
}

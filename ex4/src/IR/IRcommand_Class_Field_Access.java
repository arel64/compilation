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
	TEMP src;
	String field;
	
	public IRcommand_New_Class(TEMP dst, TEMP src, String field)
	{
		this.dst = dst;
		this.src = src;
		this.field = field;
	}
}

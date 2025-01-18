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

public class IRcommand_Array_Access extends IRcommand
{
	TEMP dst;
	TEMP src;
	TEMP index;
	
	public IRcommand_Array_Access(TEMP dst, TEMP src, TEMP index)
	{
		this.dst = dst;
		this.src = src;
		this.index = index;
	}
}

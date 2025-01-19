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

public class IRcommand_Array_Set extends IRcommand
{
	TEMP array;
	TEMP index;
	TEMP value;
	
	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value)
	{
		this.array = array;
		this.index = index;
		this.value = value;
	}
}

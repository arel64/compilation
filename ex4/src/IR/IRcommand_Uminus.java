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

public class IRcommand_Uminus extends IRcommand
{
	public TEMP dst;
	public TEMP src;
	
	public IRcommand_Uminus(TEMP dst,TEMP src)
	{
		this.dst = dst;
		this.src = src;
	}
}

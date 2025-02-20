/***********/
/* PACKAGE */
/***********/
package IR;
import AST.AST_TYPE;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class IRcommand_Array_Dec extends IRcommand
{
	String name;
	AST_TYPE type;
	
	public IRcommand_Array_Dec(String name, AST_TYPE type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
    public String toString() {
        return "IRcommand_Array_Dec: name=" + this.name + " type=" + this.type;
    }
}

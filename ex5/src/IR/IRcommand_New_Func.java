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

public class IRcommand_New_Func extends IRcommand
{
	String name;
	AST_TYPE type;
	
	public IRcommand_New_Func(String name, AST_TYPE type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
    public String toString() {
        return "IRcommand_New_Func: name=" + this.name + " type=" + this.type;
    }
}

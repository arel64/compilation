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

public class IRcommand_Class_Dec extends IRcommand
{
	String name;
	String parent;
	
	public IRcommand_Class_Dec(String name, String parent)
	{
		this.name = name;
		this.parent = parent;
	}

	@Override
    public String toString() {
        return "IRcommand_Class_Dec: name=" + this.name + " parent=" + this.parent;
    }
}

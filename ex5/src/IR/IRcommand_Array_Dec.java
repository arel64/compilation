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
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Array_Dec extends IRcommand
{
	public String name;
	public AST_TYPE type;
	
	public IRcommand_Array_Dec(String name, AST_TYPE type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
    public String toString() {
        return "IRcommand_Array_Dec: name=" + this.name + " type=" + this.type;
    }

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}
}

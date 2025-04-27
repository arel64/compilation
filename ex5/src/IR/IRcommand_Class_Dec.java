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

public class IRcommand_Class_Dec extends IRcommand
{
	public String name;
	public String parent;
	
	public IRcommand_Class_Dec(String name, String parent)
	{
		this.name = name;
		this.parent = parent;
	}

	@Override
    public String toString() {
        return "IRcommand_Class_Dec: name=" + this.name + " parent=" + this.parent;
    }

	@Override
	public void staticAnalysis() {
		this.inClassVarDecs = true;
		super.staticAnalysis();
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}
}

/***********/
/* PACKAGE */
/***********/
package IR;
import AST.*;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class IRcommand_Func_Dec extends IRcommand
{
	String name;
	AST_TYPE type;
	AST_LIST<AST_VAR_DEC> params;
	
	public IRcommand_Func_Dec(String name, AST_TYPE type)
	{
		this.name = name;
		this.type = type;
	}

	@Override
    public String toString() {
        return "IRcommand_Func_Dec: name=" + this.name + " type=" + this.type;
    }

	
	@Override
	public void staticAnanlysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (param : params) {
			in.add(new Init(param.varName, this.index));
		}
		this.out = new HashSet<Init>(in);
		if (nextCommands != null)
			for (int i : nextCommands) {
				workList.add(i);
			}
		}
	}
}

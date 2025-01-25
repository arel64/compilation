/***********/
/* PACKAGE */
/***********/
package IR;
import java.util.HashSet;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class IRcommand_Load extends IRcommand
{
	TEMP dst;
	String var_name;
	
	public IRcommand_Load(TEMP dst,String var_name)
	{
		this.dst = dst;
		this.var_name = var_name;
	}

	public HashSet<Init> staticAnanlysis(HashSet<Init> in) {
		this.out = in.copy();
		this.out.stream().filter(init => init.var != var_name);
		this.out.add(new Init(var_name, this.index));
		return this.out;
	}

	@Override
    public String toString() {
        return "IRcommand_Load: dst=" + dst + ", var_name=" + var_name;
    }
}

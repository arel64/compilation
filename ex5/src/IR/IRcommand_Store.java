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
import java.util.HashSet;
import java.util.stream.Collectors;

public class IRcommand_Store extends IRcommand
{
	String var_name;
	TEMP src;
	
	public IRcommand_Store(String var_name,TEMP src)
	{
		this.src      = src;
		this.var_name = var_name;
	}

	@Override
    public String toString() {
        return "IRcommand_Store: var_name=" + var_name + ", src=" + src;
    }

	public void staticAnanlysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}

		if (src != null && src.initialized)
		{
			in = in.stream().filter(init -> !init.var.equals(var_name)).collect(Collectors.toCollection(HashSet::new));
			in.add(new Init(var_name, this.index));
		}

		if (!in.equals(this.out)) {
			this.out = in;
			if (nextCommands != null)
				for (int i : nextCommands) {
					workList.add(i);
				}
		}
	}

}

/***********/
/* PACKAGE */
/***********/
package IR;
import java.util.HashSet;
import java.util.stream.Collectors; 

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

	public void staticAnanlysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (in.stream().allMatch(init -> init.var != var_name)) {
			exceptionVariables.add(var_name); // do lexi
		}
		else {
			in = in.stream().filter(init -> init.var != var_name).collect(Collectors.toCollection(HashSet::new));
			in.add(new Init(var_name, this.index));
		}
		if (!in.equals(this.out)) {
			this.out = in;
			for (int i : nextCommands) {
				workList.add(i);
			}
		}
	}

	@Override
    public String toString() {
        return "IRcommand_Load: dst=" + dst + ", var_name=" + var_name;
    }
}

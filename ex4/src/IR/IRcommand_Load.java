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

	public void staticAnanlysis(IRcommand prev) {
		workList.remove(workList.indexOf(this.index));
		this.prevCommands.add(prev.index);
		HashSet<Init> in = new HashSet<Init>(prev.out);
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (in.stream().allMatch(init -> init.var != var_name)) {
			exceptionVariables.add(var_name); // do lexi
		}
		else {
			HashSet<Init> newOut = in.stream().filter(init -> init.var != var_name).collect(Collectors.toCollection(HashSet::new));
			newOut.add(new Init(var_name, this.index));
		}
		if (!this.out.equals(newOut)) {
			this.out = newOut;
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

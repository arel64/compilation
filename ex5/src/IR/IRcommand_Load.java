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
import MIPS.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Load extends IRcommand
{
	public String var_name;
	
	public IRcommand_Load(TEMP dst, String var_name)
	{
		this.dst = dst;
		this.var_name = var_name;
	}

	public void staticAnalysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}
		try {
        	Integer.parseInt(var_name);
    	} 
		catch (NumberFormatException e) {
			if (in.stream().allMatch(init -> !init.var.equals(var_name) || init.line == -1)) {
				exceptionVariables.add(var_name);
				dst.initialized = false;
			}
		}
		if (!in.equals(this.out)) {
			this.out = in;
			if (nextCommands != null)
				for (int i : nextCommands) {
					workList.add(i);
				}
		}
	}

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().load(dst, var_name);
	}

	@Override
    public String toString() {
        return "IRcommand_Load: dst=" + dst + ", var_name=" + var_name;
    }
}

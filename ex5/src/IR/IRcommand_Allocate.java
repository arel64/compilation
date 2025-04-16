/***********/
/* PACKAGE */
/***********/
package IR;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Arrays;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_Allocate extends IRcommand
{
	public String var_name;
	
	public IRcommand_Allocate(String var_name)
	{
		this.var_name = var_name;
	}

	public void staticAnanlysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}
		in = in.stream().filter(init -> !init.var.equals(var_name)).collect(Collectors.toCollection(HashSet::new));
		if (this.inClassVarDecs) // incase we are in a class then this is a class var and it counts as initialized
			in.add(new Init(var_name, this.index));
		else 
			in.add(new Init(var_name, -1));
		if (!in.equals(this.out)) {
			this.out = new HashSet<Init>(in);
			if (nextCommands != null)
				for (int i : nextCommands) {
					workList.add(i);
				}
		}
	}

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().allocate(var_name);
	}

	@Override
	public String toString() {
		return "IRcommand_Allocate: Allocate " + var_name;
	}

}

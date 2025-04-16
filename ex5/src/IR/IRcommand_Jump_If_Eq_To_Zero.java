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
import MIPS.*;
import java.util.HashSet;
import java.util.Arrays;

public class IRcommand_Jump_If_Eq_To_Zero extends IRcommand
{
	public TEMP t;
	public String label_name;
	
	public IRcommand_Jump_If_Eq_To_Zero(TEMP t, String label_name)
	{
		this.t = t;
		this.label_name = label_name;
		this.nextCommands = new int[]{this.index + 1, -1};
	}

	@Override
    public String toString() {
        return "IRcommand_Jump_If_Eq_To_Zero: t=" + t + ", label=" + label_name;
    }

	@Override
	public void staticAnalysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}
		if (!in.equals(this.out)) {
			this.out = new HashSet<Init>(in);
			if (nextCommands != null)
				for (int i : nextCommands) {
					if (i == -1) {
						workList.add(findLabel(this.label_name));
					}
					else {
						workList.add(i);
					}

				}
		}
	}

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().beqz(t, label_name);
	}

	private int findLabel(String name) {
		for (IRcommand command : IR.getInstance().commandList) {
			if (command instanceof IRcommand_Label) {
				if (((IRcommand_Label)command).label_name == name) {
					command.prevCommands.add(this.index);
					return command.index;
				}
			}
		}
		return -1;
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(t));
	}
}

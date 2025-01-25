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

public class IRcommand_Jump_If_Eq_To_Zero extends IRcommand
{
	TEMP t;
	public String label_name;
	
	public IRcommand_Jump_If_Eq_To_Zero(TEMP t, String label_name)
	{
		this.t = t;
		this.label_name = label_name;
		this.nextCommands = new int[]{this.index + 1, -1};
		IR.getInstance().commandList.get(this.index + 1).prevCommands.add(this.index);
	}

	@Override
    public String toString() {
        return "IRcommand_Jump_If_Eq_To_Zero: t=" + t + ", label=" + label_name;
    }

	@Override
	public void staticAnanlysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (!in.equals(this.out)) {
			this.out = new HashSet<Init>(in);
			for (int i : nextCommands) {
				if (i == -1) {
					workList.add(findLabel(this.label_name));
					System.out.println("added " + findLabel(this.label_name) + " to worklist");
				}
				else {
					workList.add(i);
					System.out.println("added " + i + " to worklist");
				}
					
			}
		}
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
}

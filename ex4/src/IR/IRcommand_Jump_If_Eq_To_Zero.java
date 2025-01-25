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

public class IRcommand_Jump_If_Eq_To_Zero extends IRcommand
{
	TEMP t;
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
	public void staticAnanlysis(IRcommand prev) {
		workList.remove(workList.indexOf(this.index));
		this.prevCommands.add(prev.index); // fix
		HashSet<Init> in = new HashSet<Init>(prev.out);
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (!this.out.equals(in)) {
			this.out = new HashSet<Init>(in);
			for (int i : nextCommands) {
				if (i == -1) {
					workList.add(findLabel(this.label_name));
				}
				else workList.add(i);
			}
		}
	}

	private int findLabel(String name) {
		for (IRcommand command : IR.getInstance().commandList) {
			if (command instanceof IRcommand_Label) {
				if (command.label_name == name)
					return command.index;
			}
		}
		return -1;
	}
}

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
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Label extends IRcommand
{
	public String label_name;
	public String closing_label;
	public boolean funcEnd = false;
	public boolean ClassEnd = false;
	
	public IRcommand_Label(String label_name)
	{
		this.label_name = label_name;
	}

	public IRcommand_Label(String label_name, String closing_label)
	{
		this.label_name = label_name;
		this.closing_label = closing_label;
	}

	public IRcommand_Label(String label_name, boolean funcFlag, boolean classFlag)
	{
		this.label_name = label_name;
		this.funcEnd = funcFlag;
		this.ClassEnd = classFlag;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().label(label_name);
	}

	@Override
    public String toString() {
        return "IRcommand_Label: label=" + label_name;
    }

	@Override
	public void staticAnalysis() {
		if (closing_label == null) { // regular label of if or while
			super.staticAnalysis();
		}
		else if (this.funcEnd || this.ClassEnd) { // end label of class or func
			if (this.ClassEnd) this.inClass = "";
			workList.remove(workList.indexOf(this.index));
			this.nextCommands = new int[]{};
		}
		else { // start label of class or func
			int afterBlockCommand = findLabel(this.closing_label) + 1;
			if (afterBlockCommand < workList.size())
				this.nextCommands = new int[]{this.index + 1, afterBlockCommand};
			super.staticAnalysis();
		}
	}

	// closing_label != null then:
	// if open label add end label to worklist
	// if close label of function or class dont asdd to work list

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

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}
}

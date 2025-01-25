/***********/
/* PACKAGE */
/***********/
package IR;
import java.util.HashSet;
import java.util.ArrayList;
/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public abstract class IRcommand
{
	protected static int commandCounter = 0;
	public static ArrayList<Integer> workList = new ArrayList<Integer>();
	public static ArrayList<String> exceptionVariables = new ArrayList<String>();
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int label_counter = 0;
	public static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	public HashSet<Integer> prevCommands = new HashSet<Integer>();
	public int[] nextCommands;
	public HashSet<Init> out = null;
	public int index;

	public IRcommand() {
		this.index = commandCounter++;
		if (this.index != IR.getInstance().commandList.size() - 1)
		{
			this.nextCommands = new int[]{this.index + 1};
			if (this.index > 0 && !(IR.getInstance().commandList.get(this.index - 1) instanceof IRcommand_Jump_Label))
				this.prevCommands.add(this.index - 1);
		}
	}

	public void staticAnanlysis() {
		System.out.println("analysis in line " + index + " my next command is " + nextCommands[0]);
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (!in.equals(this.out)) {
			this.out = new HashSet<Init>(in);
			for (int i : nextCommands) {
				workList.add(i);
				System.out.println("added " + i + " to worklist in command: " + this.index);
			}
		}
	}

	protected class Init {
		String var;
		int line;
		public Init(String var, int line) {
			this.var = var;
			this.line = line;
		}
		@Override
    	public boolean equals(Object obj) {
        	if (this == obj) return true;
        	if (obj == null || getClass() != obj.getClass()) return false;
        	Init other = (Init) obj;
        	return line == other.line && var.equals(other.var);
    	}
    	@Override
    	public int hashCode() {
    	    return var.hashCode();
    	}
	}
}

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
	public static HashSet<String> exceptionVariables = new HashSet<String>();
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
		this.nextCommands = new int[]{this.index + 1};

		if (this.index > 0 && !(IR.getInstance().commandList.get(this.index - 1) instanceof IRcommand_Jump_Label))
			this.prevCommands.add(this.index - 1);
	}

	public void staticAnanlysis() {
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
					workList.add(i);
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

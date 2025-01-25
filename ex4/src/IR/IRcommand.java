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

	public ArrayList<Integer> prevCommands = new ArrayList<Integer>();
	public int[] nextCommands;
	public HashSet<Init> out;
	public int index;

	public IRcommand() {
		this.index = commandCounter++;
		this.nextCommands = new int[]{this.index + 1};
	}

	public void staticAnanlysis(IRcommand prev) {
		workList.remove(workList.indexOf(this.index));
		this.prevCommands.add(prev.index);
		HashSet<Init> in = new HashSet<Init>(prev.out);
		for (Integer i : prevCommands) {
			in.addAll(IR.getInstance().commandList.get(i).out);
		}
		if (this.out != in) {// need to implement set comparison
			this.out = new HashSet<Init>(in);
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

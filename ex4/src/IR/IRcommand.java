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
	public static ArrayList<IRcommand> workList = new ArrayList<IRcommand>();
	public static ArrayList<String> exceptionVariables = new ArrayList<String>();
	/*****************/
	/* Label Factory */
	/*****************/
	protected static int label_counter = 0;
	public static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s",label_counter++,msg);
	}

	public IRcommand[] prevCommands;
	public int[] nextCommands;
	public HashSet<Init> out;
	public int index;

	public IRcommand() {
		this.index = commandCounter++;
		this.nextCommands = new int[]{this.index + 1};
	}

	public HashSet<Init> staticAnanlysis(HashSet<Init> in) {
		//this.out = in.copy();
		// update worklist
		return this.out;
	}

	protected class Init {
		String var;
		int line;
		public Init(String var, int line) {
			this.var = var;
			this.line = line;
		}
	}
}

/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IR
{
	public ArrayList<IRcommand> commandList = new ArrayList<IRcommand>();
	
	/******************/
	/* Add IR command */
	/******************/
	public void Add_IRcommand(IRcommand cmd)
	{
		commandList.add(cmd);
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static IR instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected IR() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static IR getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new IR();
		}
		return instance;
	}

	public static void StaticAnalysis() {
		//IRcommand.workList.add(instance.head);
		// while (!IRcommand.workList.isEmpty()) {
		//	HashSet<Init> out = instance.head.staticAnanlysis(new HashSet<Init>());
		// 	worklist.staticAnanlysis(new HashSet<Init>());
		// }
		// here we will take our instance that includes a list of instructions
		// and we will do chaotic iterations over them to find uninitielized uses of variables
		// we need to maintain a "work list" that holds indexes of instructions that we need to visit
		// we should add to each IR instruction class an analasys function
		// this function will make the changes in the declarations set and update the work list
		// additionally if the instruction uses a variable it must check if it was initialized based on the declarations set
		// if not save that variable name for later logging
	}

	public static void checkInitializations() {
		
	}

	@Override
	public String toString() {
		String print = "";
		for (IRcommand command : commandList)
		{
			print += command.toString() + "\n";
		}
		return print;
	}

}

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
	public IRcommand head=null;
	public IRcommandList tail=null;

	public static ArrayList<int[]> workflowGraph = new ArrayList<int[]>();

	public static ArrayList<Integer> workList = new ArrayList<Integer>();
	public static ArrayList<String> exceptionVariables = new ArrayList<String>();

	/******************/
	/* Add IR command */
	/******************/
	public void Add_IRcommand(IRcommand cmd)
	{
		if ((head == null) && (tail == null))
		{
			this.head = cmd;
		}
		else if ((head != null) && (tail == null))
		{
			this.tail = new IRcommandList(cmd,null);
		}
		else
		{
			IRcommandList it = tail;
			while ((it != null) && (it.tail != null))
			{
				it = it.tail;
			}
			it.tail = new IRcommandList(cmd,null);
		}
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
		//createDataflowGraph();
		// here we will take our instance that includes a list of instructions
		// and we will do chaotic iterations over them to find uninitielized uses of variables
		// we need to maintain a "work list" that holds indexes of instructions that we need to visit
		// we should add to each IR instruction class an analasys function
		// this function will make the changes in the declarations set and update the work list
		// additionally if the instruction uses a variable it must check if it was initialized based on the declarations set
		// if not save that variable name for later logging
	}

	// public static void createDataflowGraph() {
	// 	IRcommand itr = head;
	// 	int counter = 0;
	// 	while (itr != null)
	// 	{
			
	// 		if (itr.tail) 
	// 			itr = itr.tail.head();
	// 		else 
	// 			itr = null;

	// 	}
	// }

	@Override
	public String toString() {
		IRcommandList itr = instance.tail;
		String print = "";
		print += instance.head.toString() + "\n";
		while (itr != null)
		{
			print += itr.head.toString() + "\n";
			itr = itr.tail;
		}
		return print;
	}
}

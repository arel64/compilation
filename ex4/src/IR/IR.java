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
		IRcommand.workList.add(0);
		instance.commandList.get(IRcommand.workList.get(0)).staticAnanlysis();
		while (!IRcommand.workList.isEmpty()) {
			System.out.println(IRcommand.workList);
			instance.commandList.get(IRcommand.workList.get(0)).staticAnanlysis();
		}
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

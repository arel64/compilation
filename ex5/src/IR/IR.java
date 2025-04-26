/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import SYMBOL_TABLE.SYMBOL_TABLE;
import TEMP.*;
/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IR
{
	public ArrayList<IRcommand> commandList = new ArrayList<IRcommand>();
	public InterferenceGraph interferenceGraph;
  	public Map<TEMP, Integer> registerAllocation;
	private Map<String, String> functionLabels = new HashMap<>(); 
	private Stack<String> functionEndLabels = new Stack<>(); 

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
		instance.commandList.get(IRcommand.workList.get(0)).staticAnalysis();
		while (!IRcommand.workList.isEmpty()) {
			int next = IRcommand.workList.get(0);
			if (next >= instance.commandList.size())
				IRcommand.workList.remove(IRcommand.workList.indexOf(next));
			else 
				instance.commandList.get(next).staticAnalysis();
		}

	}
		

	private void buildInterferenceGraph() {
		interferenceGraph = new InterferenceGraph();
		Set<TEMP> liveTEMPs = new HashSet<TEMP>();
		// For each instruction
		ArrayList<IRcommand> reversedCommands = new ArrayList<>(commandList);
    	Collections.reverse(reversedCommands);
		for (IRcommand cmd : reversedCommands) {
			// Get live variables after this instruction
			liveTEMPs.addAll(cmd.liveTEMPs());
			if (cmd.dst != null)
				liveTEMPs.remove(cmd.dst);

			System.out.println("In cmd number : " + cmd.index + " Current live TEMPs: " + liveTEMPs.toString());
			
			// Add interference edges between all live temps
			for (TEMP t1 : liveTEMPs) {
				for (TEMP t2 : liveTEMPs) {
					interferenceGraph.addEdge(t1, t2);
				}
			}
		}
	}

	public int getRegister(TEMP temp) {
		return registerAllocation.getOrDefault(temp, -1);
	}

	public static void MIPSme()
	{
		for (IRcommand command : instance.commandList) {
			command.MIPSme();
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

	public Map<TEMP, Integer> getRegisterAllocation() {
		return registerAllocation;
	}

	public void allocateRegisters() {
		// Build the interference graph from liveness information
		buildInterferenceGraph();
		
		// Collect *all* defined/used TEMPs to ensure allocation even for non-interfering ones
		Set<TEMP> allTemps = new HashSet<>();
		for (IRcommand cmd : commandList) {
			if (cmd.dst != null) {
				allTemps.add(cmd.dst);
			}
			allTemps.addAll(cmd.liveTEMPs());
		}

		// Debug the interference graph
		System.out.println("Interference graph nodes: " + interferenceGraph.getNodeCount());
		System.out.println("Interference graph: " + interferenceGraph.toString());
		
		// Color the graph to get register assignments, passing all TEMPs
		registerAllocation = interferenceGraph.colorGraph(allTemps);
		
		// Debug output
		for (TEMP t : registerAllocation.keySet())
			System.out.println("TEMP: " + t + " is given color: " + registerAllocation.get(t) +"\n");
	}

	public void registerFunctionLabel(String funcName, String label) {
		functionLabels.put(funcName, label);
	}

	public String getFunctionLabel(String funcName) {
		System.out.println("Getting function label for: " + funcName);
		String label = functionLabels.get(funcName);
		if (label == null) {
			throw new RuntimeException("Function label not found for: " + funcName);
		}
		return label;
	}

	// --- Function End Label Stack Management ---
	public void pushFunctionEndLabel(String label) {
		functionEndLabels.push(label);
	}

	public void popFunctionEndLabel() {
		if (!functionEndLabels.isEmpty()) {
			functionEndLabels.pop();
		}
	}

	public String getCurrentFunctionEndLabel() {
		return functionEndLabels.isEmpty() ? null : functionEndLabels.peek();
	}
	
	// public static void addPrintIntIR() {
	// 	IR ir = IR.getInstance();
	// 	String funcLabel = "PrintInt"; // Use the actual function name as the label

	// 	// 1. Register the label so IRcommand_Func_Call can find it
	// 	ir.registerFunctionLabel(funcLabel, funcLabel+"Start");

	// 	// 2. Create the IR command sequence
	// 	ir.Add_IRcommand(new IRcommand_Label(funcLabel+"Start"));
	// 	ir.Add_IRcommand(new IRcommand_Prologue(8)); // Minimal frame size (save $fp, $ra)

	// 	TEMP argTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
	// 	// Load the first argument (at offset 0 relative to the $fp established by the prologue)
	// 	ir.Add_IRcommand(new IRcommand_Load(argTemp, 8, "printIntArg"));
	// 	SYMBOL_TABLE.getInstance().associateTemp("printIntArg", argTemp);
	// 	ir.pushFunctionEndLabel(funcLabel+"End"); // Push label before processing body

	// 	// Call the syscalls using the generic command
	// 	ir.Add_IRcommand(new IRcommand_Syscall(1, argTemp)); // code 1 = print_int, arg = argTemp
	// 	ir.Add_IRcommand(new IRcommand_Syscall(11, 32)); // code 11 = print_char, arg_imm = 32 (space)
	// 	ir.popFunctionEndLabel(); // Pop label after processing body

	// 	// Epilogue
	// 	ir.Add_IRcommand(new IRcommand_Epilogue(8));
	// 	System.out.println("Added IR sequence for PrintInt function using IRcommand_Syscall.");
	// }	
}

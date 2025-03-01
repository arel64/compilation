/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Collections;

import IR.IRcommand.Init;
import java.lang.reflect.*;
import TEMP.*;
/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IR
{
	public ArrayList<IRcommand> commandList = new ArrayList<IRcommand>();
	public InterferenceGraph interferenceGraph;
	public Map<TEMP, Integer> registerAllocation;
	public Map<String, TEMP> varToTemp = new HashMap<>();
	
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
			int next = IRcommand.workList.get(0);
			if (next >= instance.commandList.size())
				IRcommand.workList.remove(IRcommand.workList.indexOf(next));
			else 
				instance.commandList.get(next).staticAnanlysis();
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
					if (t1 != t2) {
						interferenceGraph.addEdge(t1, t2);
					}
				}
			}
		}
	}

	public void recordVarTemp(String var, TEMP temp) {
		//System.out.println("Recording var-temp mapping: " + var + " -> " + temp);
		varToTemp.put(var, temp);
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
		// Debug the varToTemp map
		System.out.println("varToTemp map size: " + varToTemp.size());
		for (String var : varToTemp.keySet()) {
			System.out.println("Var: " + var + " -> TEMP: " + varToTemp.get(var));
		}
		
		// Build the interference graph from liveness information
		buildInterferenceGraph();
		
		// Debug the interference graph
		System.out.println("Interference graph nodes: " + interferenceGraph.getNodeCount());
		System.out.println("Interference graph: " + interferenceGraph.toString());
		
		// Color the graph to get register assignments
		registerAllocation = interferenceGraph.colorGraph();
		
		// Debug output
		System.out.println("Register allocation complete: " + registerAllocation.size() + " temps assigned");
	}

}

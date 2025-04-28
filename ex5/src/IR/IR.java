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
import java.util.List;

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
	private Map<Integer, HashSet<TEMP>> liveOutSets = new HashMap<>(); 

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
		Map<Integer, HashSet<TEMP>> liveIn = new HashMap<>();
		Map<Integer, HashSet<TEMP>> liveOut = new HashMap<>();
		Map<Integer, List<Integer>> successors = new HashMap<>();
		Map<String, Integer> labelToIndex = new HashMap<>();
		int n = commandList.size();

		// Initialize live sets and build label map & basic successors
		for (int i = 0; i < n; i++) {
			liveIn.put(i, new HashSet<>());
			liveOut.put(i, new HashSet<>());
			successors.put(i, new ArrayList<>());
			IRcommand cmd = commandList.get(i);
			cmd.index = i; // Ensure index is set

			if (cmd instanceof IRcommand_Label) {
				labelToIndex.put(((IRcommand_Label) cmd).label_name, i);
			}
			// Default successor (next instruction)
			if (i + 1 < n) {
				if (!(cmd instanceof IRcommand_Jump_Label) && !(cmd instanceof IRcommand_Return) && !(cmd instanceof IRcommand_Epilogue)) {
					// Commands that don't unconditionally jump/return fall through
					successors.get(i).add(i + 1);
				} else if (cmd instanceof IRcommand_Epilogue) {
					// Epilogue has no successor within the function body analysis
				}
			}
		}

		// Add jump successors
		for (int i = 0; i < n; i++) {
			IRcommand cmd = commandList.get(i);
			if (cmd instanceof IRcommand_Jump_Label) {
				String targetLabel = ((IRcommand_Jump_Label) cmd).label_name;
				if (labelToIndex.containsKey(targetLabel)) {
					successors.get(i).add(labelToIndex.get(targetLabel));
				} else {
					System.err.println("Warning: Jump target label not found: " + targetLabel);
				}
			} else if (cmd instanceof IRcommand_Jump_If_Eq_To_Zero) {
				String targetLabel = ((IRcommand_Jump_If_Eq_To_Zero) cmd).label_name;
				if (labelToIndex.containsKey(targetLabel)) {
					successors.get(i).add(labelToIndex.get(targetLabel));
				} else {
					System.err.println("Warning: Conditional jump target label not found: " + targetLabel);
				}
				// Note: Fallthrough successor already added if applicable
			}
		}

		// Iterative Dataflow Analysis
		boolean changed = true;
		while (changed) {
			changed = false;
			// Iterate backwards for potentially faster convergence
			for (int i = n - 1; i >= 0; i--) {
				IRcommand cmd = commandList.get(i);
				HashSet<TEMP> currentLiveOut = new HashSet<>();
				
				// LiveOut[i] = Union(LiveIn[s]) for all successors s of i
				if (successors.containsKey(i)) {
					for (int successorIndex : successors.get(i)) {
						if (liveIn.containsKey(successorIndex)) {
							currentLiveOut.addAll(liveIn.get(successorIndex));
						}
					}
				}

				HashSet<TEMP> oldLiveOut = liveOut.get(i);
				if (!oldLiveOut.equals(currentLiveOut)) {
					liveOut.put(i, new HashSet<>(currentLiveOut)); // Use copy
					changed = true;
				}

				// LiveIn[i] = Use[i] U (LiveOut[i] - Def[i])
				Set<TEMP> useSet = cmd.liveTEMPs();
				Set<TEMP> defSet = new HashSet<>();
				if (cmd.dst != null) {
					defSet.add(cmd.dst);
				}

				HashSet<TEMP> currentLiveIn = new HashSet<>(currentLiveOut); // Start with LiveOut
				currentLiveIn.removeAll(defSet);                        // Subtract Def
				currentLiveIn.addAll(useSet);                           // Add Use

				HashSet<TEMP> oldLiveIn = liveIn.get(i);
				if (!oldLiveIn.equals(currentLiveIn)) {
					liveIn.put(i, currentLiveIn); // Already a copy
					changed = true;
				}
			}
		}

		// Store the final calculated liveOut sets
		this.liveOutSets = liveOut; 

		// Build Interference Graph based on accurate liveness
		for (int i = 0; i < n; i++) {
			IRcommand cmd = commandList.get(i);
			Set<TEMP> defSet = new HashSet<>();
			if (cmd.dst != null) {
				defSet.add(cmd.dst);
			}
			
			Set<TEMP> currentLiveOut = liveOutSets.getOrDefault(i, new HashSet<>());

			for (TEMP def : defSet) {
				for (TEMP live : currentLiveOut) {
					// Add interference edge if def and live are different
					// Special case for move instructions (if applicable, IRcommand_Move?) could be handled here
					// to avoid interference between src and dst if they are the same var, but we don't have explicit moves
					if (def != live) { 
						interferenceGraph.addEdge(def, live);
					}
				}
			}
		}
	}

	public HashSet<TEMP> getLiveOutSet(int commandIndex) {
        return liveOutSets.getOrDefault(commandIndex, new HashSet<>());
    }

	public int getRegister(TEMP temp) {
		if (registerAllocation == null) {
			System.err.println("Warning: Register allocation map is null when calling getRegister.");
			return -1;
		}
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
		buildInterferenceGraph();
		
		Set<TEMP> allTemps = new HashSet<>();
		for (IRcommand cmd : commandList) {
			if (cmd.dst != null) {
				allTemps.add(cmd.dst);
			}
			allTemps.addAll(cmd.liveTEMPs());
		}

		System.out.println("Interference graph nodes: " + interferenceGraph.getNodeCount());
		System.out.println("Interference graph: " + interferenceGraph.toString());
		
		registerAllocation = interferenceGraph.colorGraph(allTemps);
		
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
	
	public static void addPrintIntIR() {
		IR ir = IR.getInstance();
		String funcLabel = "PrintInt";

		ir.registerFunctionLabel(funcLabel, funcLabel+"Start");

		ir.Add_IRcommand(new IRcommand_Label(funcLabel+"Start"));
		ir.Add_IRcommand(new IRcommand_Prologue(8));

		TEMP argTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		ir.Add_IRcommand(new IRcommand_Load(argTemp, 0, "printIntArg"));
		ir.pushFunctionEndLabel(funcLabel+"End");

		ir.Add_IRcommand(new IRcommand_Syscall(1, argTemp));
		ir.Add_IRcommand(new IRcommand_Syscall(11, 32));
		ir.popFunctionEndLabel();

		ir.Add_IRcommand(new IRcommand_Epilogue(8));
		System.out.println("Added IR sequence for PrintInt function using IRcommand_Syscall.");
	}	
	
}

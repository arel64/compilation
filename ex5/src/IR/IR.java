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
import MIPS.MIPSGenerator;

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
		MIPSGenerator generator = MIPSGenerator.getInstance();
		HashSet<Integer> processedIndicesPass2 = new HashSet<>(); // Track commands processed in Pass 2

		// Pass 1: Process only Allocations for .data section
		// Note: allocate() in MIPSGenerator already manages its context internally.
		System.out.println("--- IR MIPSme Pass 1: Allocations (.data) ---");
		for (IRcommand command : instance.commandList) {
			if (command instanceof IRcommand_Allocate) {
				System.out.println("[Pass 1] Processing (Allocate): " + command);
				command.MIPSme();
			}
		}

		// Pass 2: Process Global Initializers (.text initial section)
		System.out.println("--- IR MIPSme Pass 2: Global Initializers (GLOBAL_INIT context) ---");
		generator.setContextGlobalInit();
		for (int i = 0; i < instance.commandList.size(); i++) {
			IRcommand command = instance.commandList.get(i);
			
			// Look for Global_Init_Store commands to place initialization code early in .text
			if (command instanceof IRcommand_Global_Init_Store) {
				IRcommand_Global_Init_Store initStoreCmd = (IRcommand_Global_Init_Store) command;
				// Look for the preceding Const command defining the source TEMP
				if (i > 0 && initStoreCmd.srcTemp != null) { 
					IRcommand prevCommand = instance.commandList.get(i - 1);
					// Check if the previous command is a Const command defining the temp used by Init_Store
					if ((prevCommand instanceof IRcommandConstInt || prevCommand instanceof IRcommandConstString) && 
						prevCommand.dst != null && prevCommand.dst.equals(initStoreCmd.srcTemp)) 
					{ 
						// Process the Const command first in GLOBAL_INIT context
						System.out.println("[Pass 2] Processing (Const for Global Init Store): " + prevCommand);
						prevCommand.MIPSme(); 
						processedIndicesPass2.add(i - 1); // Mark Const as processed

						// Process the Global_Init_Store command itself
						System.out.println("[Pass 2] Processing (Global Init Store for Const): " + initStoreCmd);
						initStoreCmd.MIPSme();
						processedIndicesPass2.add(i); // Mark Init Store as processed
					}
					// Case 2: Preceded by a New_Array command (Handles global array initializers)
					else if (prevCommand instanceof IRcommand_New_Array &&
							 prevCommand.dst != null && prevCommand.dst.equals(initStoreCmd.srcTemp))
					{
						IRcommand_New_Array newArrayCmd = (IRcommand_New_Array) prevCommand;
						System.out.printf("[Pass 2] Found New Array at index %d: %s%n", i - 1, newArrayCmd);

						// Now, find the command that defines the size TEMP for New_Array
						TEMP sizeTemp = newArrayCmd.size;
						IRcommand sizeDefCommand = null;
						int sizeDefIndex = -1;
						for (int j = i - 2; j >= 0; j--) { // Search backwards from before New_Array
							IRcommand potentialDef = instance.commandList.get(j);
							if (potentialDef.dst != null && potentialDef.dst.equals(sizeTemp)) {
								// Found the command defining the size TEMP (likely IRcommandConstInt)
								if (potentialDef instanceof IRcommandConstInt) { // Be specific if possible
									sizeDefCommand = potentialDef;
									sizeDefIndex = j;
									System.out.printf("[Pass 2] Found Size Def (ConstInt) at index %d: %s%n", j, sizeDefCommand);
									break;
								} // Add checks for other ways size might be defined if necessary
							}
						}

						if (sizeDefCommand != null) {
							// Process the Size Definition command first
							System.out.println("[Pass 2] Processing (Size Def for New Array): " + sizeDefCommand);
							sizeDefCommand.MIPSme();
							processedIndicesPass2.add(sizeDefIndex); // Mark Size Def as processed

							// Process the New_Array command 
							System.out.println("[Pass 2] Processing (New Array for Global Init Store): " + newArrayCmd);
							newArrayCmd.MIPSme();
							processedIndicesPass2.add(i - 1); // Mark New Array as processed

							// Process the Global_Init_Store command itself
							System.out.println("[Pass 2] Processing (Global Init Store for New Array): " + initStoreCmd);
							initStoreCmd.MIPSme();
							processedIndicesPass2.add(i); // Mark Init Store as processed
						} else {
							System.err.printf("Error [Pass 2]: Could not find defining command for size TEMP %s needed by New_Array at index %d.%n", sizeTemp, i - 1);
							// Decide how to handle this error - skip processing? throw exception?
						}
					}
					// Add other potential preceding command types here if needed (e.g., Load Global?)
					else {
						System.err.printf("Warning [Pass 2]: Global_Init_Store at index %d is preceded by an unexpected command type (%s) or mismatching TEMP.%n", i, prevCommand.getClass().getSimpleName());
						// Optionally process the store anyway if the TEMP is expected to be valid?
						// initStoreCmd.MIPSme(); 
						// processedIndicesPass2.add(i);
					}
				} else {
					System.err.printf("Warning [Pass 2]: Global_Init_Store at index %d has no preceding command or null srcTemp.%n", i);
					// Optionally process the store anyway?
					// initStoreCmd.MIPSme();
					// processedIndicesPass2.add(i);
				}
			}
			// Note: We don't process other command types here in Pass 2 unless they are part
			// of a recognized global initialization sequence (like Const or New_Array above).
		}

		// Pass 3: Process all other commands (function code, labels, remaining constants, runtime global stores, etc.)
		System.out.println("--- IR MIPSme Pass 3: Function Code & Others (FUNCTION context) ---");
		generator.setContextFunction();
		for (int i = 0; i < instance.commandList.size(); i++) {
			IRcommand command = instance.commandList.get(i);
			
			// Skip commands processed in Pass 1 (Allocate) or Pass 2 (Global Init Stores & related Consts)
			if (command instanceof IRcommand_Allocate || processedIndicesPass2.contains(i)) {
				System.out.println("[Pass 3] Skipping (Already Processed): " + command.getClass().getSimpleName() + " at index " + i);
				continue;
			}

			// Process all remaining commands (including runtime IRcommand_Global_Store) in the FUNCTION context
			System.out.println("[Pass 3] Processing (Function/Other): " + command);
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
		TEMP_FACTORY.getInstance().isRegistersAllocated = true;
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
		ir.Add_IRcommand(new IRcommand_Label(funcLabel + "End"));
		System.out.println("Added IR sequence for PrintInt function using IRcommand_Syscall.");
	}	
	
	public static void addPrintStringIR() {
		IR ir = IR.getInstance();
		String funcLabel = "PrintString";

		ir.registerFunctionLabel(funcLabel, funcLabel+"Start");
		ir.Add_IRcommand(new IRcommand_Label(funcLabel+"Start"));
		ir.Add_IRcommand(new IRcommand_Prologue(8)); 

		TEMP argTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		// Assuming the string address is passed similarly to PrintInt's argument
		ir.Add_IRcommand(new IRcommand_Load(argTemp, 0, "printStringArg")); 
		ir.pushFunctionEndLabel(funcLabel+"End");

		// Syscall 4 prints a null-terminated string whose address is in $a0
		ir.Add_IRcommand(new IRcommand_Syscall(4, argTemp)); 

		ir.popFunctionEndLabel();

		ir.Add_IRcommand(new IRcommand_Epilogue(8));
		ir.Add_IRcommand(new IRcommand_Label(funcLabel+"End"));
		System.out.println("Added IR sequence for PrintString function using IRcommand_Syscall.");
	}
	
}

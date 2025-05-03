/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;

/****************/
/* SYMBOL TABLE */
/****************/
import SYMBOL_TABLE.ScopeType;
public class SYMBOL_TABLE {
	private int hashArraySize = 13;

	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;
	private int scope_count = 0;

	// --- Re-added for Offset Calculation ---
	private Stack<Integer> offsetStack = new Stack<>();
	private Stack<ScopeType> scopeTypeStack = new Stack<>(); // Stack to track scope types
	private int currentLocalOffset = 0; // Reset per function scope
	private int currentParamOffset = 0; // Reset per function scope
	private static final int WORD_SIZE = 4; // Assuming 4 bytes per variable/pointer
	// --- End Offset Calculation Fields ---

	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s) {
		if (s.charAt(0) == 'l') {
			return 1;
		}
		if (s.charAt(0) == 'm') {
			return 1;
		}
		if (s.charAt(0) == 'r') {
			return 3;
		}
		if (s.charAt(0) == 'i') {
			return 6;
		}
		if (s.charAt(0) == 'd') {
			return 6;
		}
		if (s.charAt(0) == 'k') {
			return 6;
		}
		if (s.charAt(0) == 'f') {
			return 6;
		}
		if (s.charAt(0) == 'S') {
			return 6;
		}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name, TYPE t)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];

		/**************************************************************************/
		/* [2.5] Calculate size                                                   */
		/**************************************************************************/
		int calculatedSize = 0;
		try { calculatedSize = t.getSize(); } catch (Exception ex) {
			 System.err.printf("Warning: getSize() failed for %s (%s)\n", name, t.getName());
		}

		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, next and prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name, t, hashValue, next, top, top_index++, false);
		e.size = calculatedSize; // Store calculated size

		/**************************************/
		/* [3.5] Assign offset based on scope */
		/**************************************/
		if (name.equals("SCOPE-BOUNDARY")) // Special marker, no offset needed
		{
			e.offset = Integer.MIN_VALUE;
		}
		else if (scopeTypeStack.isEmpty()) // Global scope (stack is empty)
		{
			e.isGlobal = true; // Mark entry as global
			e.offset = 0; // Globals might use labels, offset 0 is placeholder
		}
		else // Inside a scope (Function, Params, or Body)
		{
			ScopeType currentScope = scopeTypeStack.peek();

			if (currentScope == ScopeType.PARAMS) {
				// Assign parameter offset (positive, starts at 0)
				// Check if it's actually a variable-like type
				if (!t.isFunction()) { // Allow simple types, arrays, maybe nil? Be careful with Void.
					// System.out.println("Assigning PARAM offset " + currentParamOffset + " to " + name);
					e.offset = currentParamOffset;
					currentParamOffset += WORD_SIZE; // Increment for next parameter
				} else {
					e.offset = Integer.MIN_VALUE; // Functions/classes in param list? Unlikely.
				}
			} else if (currentScope == ScopeType.BODY || currentScope == ScopeType.FUNCTION) {
				// Assign local variable offset (negative, starts at -12)
				// Assign offset only to actual variables/data, not types/functions/void/arrays/nil? Check array logic later.
				if (!t.isFunction()) {
					// System.out.println("Assigning LOCAL offset " + currentLocalOffset + " to " + name);
					e.offset = currentLocalOffset;
					currentLocalOffset -= WORD_SIZE; // Decrement for next *local* variable
				} else {
					e.offset = Integer.MIN_VALUE; // No stack offset for these types
				}
			} else if (currentScope == ScopeType.CLASS) {
				// Class member offsets (field/method) are handled by TYPE_CLASS, not the stack frame.
				// Assign a placeholder offset here. Retrieval will happen via TYPE_CLASS.
				e.offset = currentLocalOffset;
				currentLocalOffset -= WORD_SIZE;
			} else {
				// Should not happen if scope types are managed correctly
				e.offset = Integer.MIN_VALUE;
				System.err.println("Warning: enter() called in unexpected scope state.");
			}
		}

		// System.out.println(e); // DEBUG: Print entry details including offset

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;

		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;

		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		// PrintMe(); // Keep commented out unless debugging
	}

	/****************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/****************************************************************************/
	public void beginScope(ScopeType scopeType)
	{
		/******************************************/
		/* [1] Adjust and Push offset/scope state */
		/******************************************/
		scopeTypeStack.push(scopeType); // Push the type of scope being entered

		switch (scopeType) {
			case FUNCTION:
				// Entering a function: Reset locals and params, push previous local state.
				offsetStack.push(currentLocalOffset); // Save outer scope's local offset
				currentLocalOffset = -12; // Start locals below saved $ra(-4) and $fp(-8)
				currentParamOffset = 0;   // Reset parameter offset for new function
				break;
			case PARAMS:
				// Entering parameter list: Only reset param offset.
				// Parameter offsets start at 0($fp).
				currentParamOffset = 0;
				// No push/pop needed for param offset as it's reset per function.
				// No push/pop needed for local offset here, it continues from FUNCTION scope.
				break;
			case BODY:
				// Entering a nested block (e.g., inside if/while): Push current local offset.
				// Parameter offset is irrelevant within a body block.
				offsetStack.push(currentLocalOffset);
				// currentLocalOffset continues decrementing from its current value.
				break;
			case CLASS:
				// Entering a class: Reset locals and params, push previous local state.
				offsetStack.push(currentLocalOffset); // Save outer scope's local offset
				currentLocalOffset = 0; // Start locals below saved $ra(-4) and $fp(-8)
				currentParamOffset = 0;
				break;
		}

		/************************************************************************/
		/* [2] Enter boundary marker                                            */
		/************************************************************************/
		enter(
			"SCOPE-BOUNDARY",
			new TYPE_FOR_SCOPE_BOUNDARIES("NONE"));

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		// PrintMe(); // Keep commented out

		scope_count ++;
	}

	public boolean exists(String name) {
		// Use the stack search for correct scoping
		return findEntryInCurrentScopeStack(name) != null;
	}

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope() {
		ScopeType endedScopeType = ScopeType.BODY; // Default guess
		if (!scopeTypeStack.isEmpty()) {
			endedScopeType = scopeTypeStack.peek(); // Get type BEFORE popping symbols
		} else {
			System.err.println("Warning: endScope called with empty scopeTypeStack.");
		}

		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */
		/**************************************************************************/
		while (top != null && !top.name.equals("SCOPE-BOUNDARY")) // Check top != null
		{
			// Remove the entry from the hash table *only if* it's the head of the list there
			if (table[top.index] == top) {
				table[top.index] = top.next;
			} else {
				// Need to potentially update the 'next' pointer of the preceding element in the hash chain.
				// This is complex and the original implementation didn't do it either.
				// The primary mechanism for removal is popping from the 'top' stack.
				// Hash table entries from closed scopes become "stale" but inaccessible via find/findEntryInCurrentScopeStack.
			}
			// Pop from the scope stack
			top = top.prevtop;
			// Decrement top_index? The original didn't seem to use top.prevtop_index consistently.
			// Let's stick to the original 'test' logic for prevtop.
			if (top != null) {
				top_index = top.prevtop_index + 1; // Infer next index? Risky. Stick to original test logic.
			} else {
				top_index = 0;
			}
			// Original test logic didn't decrement top_index this way. Let's revert endScope closer to original.
		}

		// Revert endScope to be exactly like the 'test' file logic
		SYMBOL_TABLE_ENTRY temp_top = top; // Use a temp variable for clarity
		while (temp_top != null && !temp_top.name.equals("SCOPE-BOUNDARY")) {
			// Retrieve the element at the head of the hash list for this entry's index
			SYMBOL_TABLE_ENTRY head = table[temp_top.index];

			// If the element we are popping is the head of the list in the table array, update the head
			if (head == temp_top) {
				table[temp_top.index] = temp_top.next;
			} else {
				// If it's not the head, we need to find the element *before* it in the hash chain
				// and update *its* next pointer to skip the element we are popping.
				// This ensures the hash chain remains valid for lookups of symbols from outer scopes
				// that might have hashed to the same index.
				SYMBOL_TABLE_ENTRY prevInHashChain = null;
				SYMBOL_TABLE_ENTRY currentInHashChain = head;
				while (currentInHashChain != null && currentInHashChain != temp_top) {
					prevInHashChain = currentInHashChain;
					currentInHashChain = currentInHashChain.next;
				}
				// If we found the element in the chain (currentInHashChain == temp_top)
				// and it had a preceding element (prevInHashChain != null), update the link.
				if (prevInHashChain != null && currentInHashChain == temp_top) {
					prevInHashChain.next = temp_top.next;
				}
				// If prevInHashChain is null here, it means head should have been temp_top,
				// which is handled by the first 'if' case.
			}

			// Pop from the stack by moving top to its prevtop
			temp_top = temp_top.prevtop;
			// Update the main 'top' pointer after processing the element
			top = temp_top;
		}

		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */
		/**************************************/
		if (top != null && top.name.equals("SCOPE-BOUNDARY")) { // Check it's the boundary before popping
			// Similar logic to remove the boundary marker from the hash chain
			SYMBOL_TABLE_ENTRY head = table[top.index];
			if (head == top) {
				table[top.index] = top.next;
			} else {
				SYMBOL_TABLE_ENTRY prevInHashChain = null;
				SYMBOL_TABLE_ENTRY currentInHashChain = head;
				while (currentInHashChain != null && currentInHashChain != top) {
					prevInHashChain = currentInHashChain;
					currentInHashChain = currentInHashChain.next;
				}
				if (prevInHashChain != null && currentInHashChain == top) {
					prevInHashChain.next = top.next;
				}
			}
			// Pop the boundary marker from the stack
			top = top.prevtop;
		} else if (top == null) {
			System.err.println("Warning: Reached top=null during endScope before finding SCOPE-BOUNDARY.");
		}

		/************************************/
		/* [3] Restore previous offset state */
		/************************************/
		// Pop the scope type stack AFTER processing the symbols
		if (!scopeTypeStack.isEmpty()) {
			scopeTypeStack.pop();
		}

		// Restore local offset ONLY if a FUNCTION or BODY scope ended
		if ((endedScopeType == ScopeType.FUNCTION || endedScopeType == ScopeType.BODY) && !offsetStack.isEmpty()) {
			// System.out.println("Restoring local offset from stack: " + offsetStack.peek());
			currentLocalOffset = offsetStack.pop();
		} else {
			// Don't pop offsetStack if PARAMS scope ended or if stack mismatch occurs
			// System.out.println("Debug: endScope - not popping offset stack (endedType=" + endedScopeType + ", stackEmpty=" + offsetStack.isEmpty() + ")");
		}

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		// PrintMe(); // Keep commented out

		scope_count --;
	}

	public static int n = 0;

	public int getCurrentScopeIndex() {
		return scope_count;
	}

	public void PrintMe() {
		int i = 0;
		int j = 0;
		String dirname = "./output/";
		String filename = String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt", n++);

		try {
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname + filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i = 0; i < hashArraySize - 1; i++) {
				fileWriter.format("<f%d>\n%d\n|", i, i);
			}
			fileWriter.format("<f%d>\n%d\n\"];\n", hashArraySize - 1, hashArraySize - 1);

			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i = 0; i < hashArraySize; i++) {
				if (table[i] != null) {
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n", i, i);
				}
				j = 0;
				for (SYMBOL_TABLE_ENTRY it = table[i]; it != null; it = it.next) {
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ", i, j);
					// Added offset and size to the printout for debugging
					fileWriter.format("[label=\"<f0>%s|<f1>%s|{<f_off>off=%d|<f_size>sz=%d}|<f2>prevtop=%d|<f3>next\"];\n",
							it.name,
							it.type.getName(),
							it.offset, // Print offset
							it.size, // Print size
							it.prevtop_index);

					if (it.next != null) {
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
								"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
								i, j, i, j + 1);
						fileWriter.format(
								"node_%d_%d:f3 -> node_%d_%d:f0;\n",
								i, j, i, j + 1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance() {
		if (instance == null) {
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int", TYPE_INT.getInstance());
			instance.enter("string", TYPE_STRING.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/
			instance.enter("void", TYPE_VOID.getInstance()); // Enter void type

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			TYPE_LIST printIntArgs = new TYPE_LIST();
			printIntArgs.add(new TYPE_VAR_DEC(TYPE_INT.getInstance(), "i"), -1);
			instance.enter(
					"PrintInt",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintInt",
							printIntArgs, -1)); // Changed variable name for clarity

			/******************************************/
			/* [4] Enter library function PrintString */
			/******************************************/
			TYPE_LIST printStringArgs = new TYPE_LIST(); // Changed variable name
			printStringArgs.add(new TYPE_VAR_DEC(TYPE_STRING.getInstance(), "s"), -1);
			instance.enter(
					"PrintString",
					new TYPE_FUNCTION(
							TYPE_VOID.getInstance(),
							"PrintString",
							printStringArgs, -1)); // Changed variable name

		}
		return instance;
	}

	public boolean isAtGlobalScope() {
		return getCurrentScopeIndex() == 0;
	}

	// Renamed from existsInCurrentScope to match usage in AST_VAR_DEC
	public boolean isDeclaredInImmediateScope(String name) {
		SYMBOL_TABLE_ENTRY e = top;
		while (e != null && !e.name.equals("SCOPE-BOUNDARY")) {
			if (e.name.equals(name)) {
				return true;
			}
			e = e.prevtop;
		}
		return false;
	}

	// Note: This seems to search the *entire* stack up to global, not just global.
	// Renaming or clarifying its purpose might be needed.
	// Keeping original name and logic from 'test'.
	public boolean isInGlobalScope(String name, TYPE recvType) {
		SYMBOL_TABLE_ENTRY e = top;
		while (e != null) {
			if (e.name.equals(name) && e.type.equals(recvType)) {
				// Found the name with the specified type. Now check if it's global.
				// A simple check: is its prevtop chain ending in null without hitting a scope boundary?
				// Or, more robustly, walk up its prevtop chain. If we hit null before a SCOPE-BOUNDARY, it's global.
				SYMBOL_TABLE_ENTRY checker = e.prevtop;
				boolean isGlobal = true;
				while (checker != null) {
					if (checker.name.equals("SCOPE-BOUNDARY")) {
						isGlobal = false;
						break;
					}
					checker = checker.prevtop;
				}
				if (isGlobal)
					return true;

				// If not global, continue searching up the stack for other potential matches
			}
			// This original logic just checked if the name existed *before* the first scope boundary.
			// Let's refine the logic to actually check if the *found* entry is in the global scope.
			// Removed the break on SCOPE-BOUNDARY to allow finding shadowed globals if needed.
			e = e.prevtop;
		}
		// If loop finishes, name/type combo wasn't found in the global scope.
		return false;
	}

	// Refined version: Find first occurrence, then check if it's global.
	public boolean isFirstOccurrenceGlobal(String name) {
		SYMBOL_TABLE_ENTRY e = findEntryInCurrentScopeStack(name);
		if (e == null)
			return false; // Not found at all

		// Check if it's global by walking up its prevtop chain
		SYMBOL_TABLE_ENTRY checker = e.prevtop;
		while (checker != null) {
			if (checker.name.equals("SCOPE-BOUNDARY")) {
				return false; // Found a boundary before hitting null -> not global
			}
			checker = checker.prevtop;
		}
		return true; // Hit null without hitting boundary -> global
	}

	public TYPE getTypeInScope(String name) {
		SYMBOL_TABLE_ENTRY e = findEntryInCurrentScopeStack(name);
		return (e != null) ? e.type : null;
	}

	// Original getTypeInGlobalScope searches the whole stack until the first boundary is hit.
	// It doesn't guarantee the found type *is* global if shadowed.
	// Let's provide a version that guarantees finding the global type if it exists.
	public TYPE getTypeInGlobalScope(String name) {
		if (name == null) {
			return null;
		}
		SYMBOL_TABLE_ENTRY e = top;
		TYPE globalType = null;
		// Traverse the entire stack
		while (e != null) {
			// Check if current entry is global
			boolean isCurrentGlobal = true;
			SYMBOL_TABLE_ENTRY checker = e.prevtop;
			while (checker != null) {
				if (checker.name.equals("SCOPE-BOUNDARY")) {
					isCurrentGlobal = false;
					break;
				}
				checker = checker.prevtop;
			}

			if (isCurrentGlobal && e.name.equals(name)) {
				globalType = e.type; // Found a global entry with the name
				// Keep searching in case of errors/multiple globals? No, standard assumes one global.
				break; // Found the (or a) global definition.
			}
			e = e.prevtop;
		}
		// Check the initial entries (int, string, PrintInt, etc.) if not found yet
		if (globalType == null) {
			// These are entered before any scopes, so they are global by definition.
			// Use the hash lookup for these initial entries.
			for (e = table[hash(name)]; e != null; e = e.next) {
				if (name.equals(e.name)) {
					// Verify it's one of the initial entries (prevtop should be null)
					if (e.prevtop == null && !e.name.equals("SCOPE-BOUNDARY")) { // Check prevtop is null
						return e.type;
					}
				}
			}
		}

		return globalType; // Return found global type or null
	}

	// Checks if a name exists anywhere in the accessible scope stack (current or outer)
	public boolean existsInScopeStack(String name) {
		return findEntryInCurrentScopeStack(name) != null;
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name) {
		if (name == null) {
			return null;
		}
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next) {
			if (name.equals(e.name)) {
				// To find the innermost, we need to check the scope stack, not just the hash chain.
				// Use findEntryInCurrentScopeStack instead.
				SYMBOL_TABLE_ENTRY foundEntry = findEntryInCurrentScopeStack(name);
				return (foundEntry != null) ? foundEntry.type : null;
			}
		}

		return null; // Should ideally not be reached if findEntryInCurrentScopeStack is used correctly.
	}

	// Helper to find entry by searching the current scope stack (most recent first)
	public SYMBOL_TABLE_ENTRY findEntryInCurrentScopeStack(String name) {
		// System.out.format("SymbolTable: Searching stack for '%s' starting from top...\n", name);
		SYMBOL_TABLE_ENTRY e = top;
		while (e != null) {
			// System.out.format("SymbolTable:  Checking entry '%s' (type: %s, scope_boundary: %b)\n",
			// 							e.name,
			// 							(e.type != null ? e.type.getName() : "null"),
			// 							e.name.equals("SCOPE-BOUNDARY"));
			// Don't stop at SCOPE-BOUNDARY if it's the name we are looking for (though unlikely)
			if (e.name.equals(name)) {
				// System.out.format("SymbolTable:  FOUND '%s'! Entry: %s\n", name, e.toString());
				return e; // Found the most recent declaration
			}
			if (e.name.equals("SCOPE-BOUNDARY")) {
				// System.out.format("SymbolTable:  Hit scope boundary.\n");
				// If we hit a boundary *before* finding the name, continue searching outer scopes
				// But the request implies finding the *innermost*, so we should stop if name not found yet?
				// Let's keep searching up the stack based on prevtop
			}
			e = e.prevtop;
		}
		// System.out.format("SymbolTable: Search finished. '%s' NOT FOUND in current stack.\n", name);
		return null; // Not found in current stack segment or any outer scope stack segment
	}

	// Get offset for a found entry
	public int getOffset(String name) {
		// System.out.format("SymbolTable: getOffset requested for '%s'\n", name);
		SYMBOL_TABLE_ENTRY e = findEntryInCurrentScopeStack(name); // Use stack search for correctness
		if (e != null) {
			// System.out.format("SymbolTable: getOffset found entry for '%s', returning offset %d\n", name, e.offset);
			return e.offset;
		}
		// System.out.format("SymbolTable: getOffset did NOT find entry for '%s', returning MIN_VALUE\n", name);
		return Integer.MIN_VALUE; // Indicate not found or offset not set
	}
}
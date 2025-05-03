/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Store extends IRcommand {
	String var_name;
	TEMP src = null;
	TEMP dst_addr_temp = null; // Used only for register-to-register move (if kept)
	public int offset; // Offset relative to $fp for stack stores
	boolean is_stack_store = false; // True if storing to stack via offset
		// Constructor 

	public IRcommand_Store(TEMP src, int offset, String var_name) {
		this.src = src; // The TEMP containing the value to store
		this.offset = offset; // The stack offset ($fp + offset)
		this.var_name = var_name; // Name for debugging/comments
		this.is_stack_store = true;
		this.dst = null; // Destination is the stack memory, not a TEMP
	}

	// Constructor for moving TEMP to TEMP (potentially redundant?)
	// If kept, dst is the destination TEMP, src is the source TEMP
	// offset is unused. is_stack_store is false.
	public IRcommand_Store(TEMP dst_temp, TEMP src, String var_name) {
		this.dst = dst_temp; // Destination TEMP
		this.src = src; // Source TEMP
		this.var_name = var_name; // Name for debugging
		this.is_stack_store = false; // This is a register move
		this.offset = Integer.MIN_VALUE; // Offset is irrelevant
	}

	@Override
	public String toString() {
		if (is_stack_store) {
			return String.format("STORE [%s] : M[$fp + %d] := %s", var_name, offset, src);
		} else {
			return String.format("STORE [%s] : %s := %s", var_name, dst, src);
		}
	}

	public void staticAnalysis() {
		if (workList.contains(this.index)) {
			workList.remove(workList.indexOf(this.index));
		}
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}

		// If it's a register move, it defines dst based on src.
		if (!is_stack_store) { // Register move: Check src, define dst
			if (src != null && src.initialized) {
				// Modify liveness based on dst definition
				in = in.stream().filter(init -> !init.var.equals(var_name))
						.collect(Collectors.toCollection(HashSet::new));
				in.add(new Init(var_name, this.index));
				if (dst != null)
					dst.initialized = true;
			} else {
				if (dst != null)
					dst.initialized = false;
			}
		} else { // Stack store: Only check src initialization
			if (src == null || !src.initialized) {
				// Storing an uninitialized value to memory is potentially problematic
				// but the analysis might not need modification here, depends on requirements.
				System.out.printf("Warning: Storing potentially uninitialized TEMP %s to stack var %s\n", src,
						var_name);
			}
			// Stack store doesn't define a variable in the same way,
			// it modifies memory state. Liveness of TEMPs isn't directly affected by the
			// store itself,
			// only by the use of the src TEMP.
		}

		// Propagate changes
		if (!in.equals(this.out)) {
			this.out = in;
			if (nextCommands != null) {
				for (int i : nextCommands) {
					if (!workList.contains(i)) { // Add only if not already present
						workList.add(i);
					}
				}
			}
		}
	}

	@Override
	public void MIPSme() {
		// Check source TEMP register
		if (src == null) {
			System.err.printf("ERROR: Null source TEMP for Store (%s)\n", var_name);
			throw new RuntimeException("Null source TEMP in Store");
		}
		int srcRegNum = IR.getInstance().getRegister(src);
		if (srcRegNum < 0) {
			System.err.printf(
					"Warning: Source TEMP %s for Store (%s) has no register. Static analysis might be incomplete or value unused? Skipping MIPSme.\n",
					src, var_name);
			return; // Cannot store if source has no register
		}

		MIPSGenerator gen = MIPSGenerator.getInstance();
		if (is_stack_store) {
			// Store value from source TEMP register into stack: sw $src_reg, offset($fp)
			gen.sw_fp(src, offset);
		} else {
			// This is the register-to-register move case
			int dstRegNum = IR.getInstance().getRegister(dst);
			if (dstRegNum < 0) {
				System.err.printf(
						"Warning: Destination TEMP %s for Store (move, var=%s) has no register. Skipping MIPSme.\n",
						dst, var_name);
				return;
			}
			gen.move(dst, src);
		}
	}

	public HashSet<TEMP> liveTEMPs() {
		HashSet<TEMP> used = new HashSet<>();
		if (src != null) {
			used.add(src); // Always uses the source value
		}
		// The register-to-register move form *also* uses dst if not identical to src?
		// Liveness definition: TEMPs whose values are needed *before* this instruction
		// overwrites them.
		// Store `M[addr] := src` uses `src`.
		// Store `dst := src` uses `src`.
		return used;
	}

}

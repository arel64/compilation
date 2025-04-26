package IR;
import java.util.HashSet;
import TEMP.*;
import MIPS.*;
public class IRcommand_Load extends IRcommand
{
	public String var_name;
	public int offset;
	public boolean is_offset = false;
	public TEMP src = null;
	public IRcommand_Load(TEMP dst, int offset, String var_name)
	{
		this.dst = dst;
		this.offset = offset;
		this.var_name = var_name;
		this.is_offset = true;
	}
	public IRcommand_Load(TEMP dst, TEMP src, String var_name)
	{
		this.dst = dst;
		this.src = src;
		this.var_name = var_name;
		this.is_offset = false;
	}

	public void staticAnalysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}

		// Determine initialization of dst based on operation type
		if (is_offset) {
			// Memory load: Assume destination is initialized after loading
			// (Assuming the memory location itself contains valid data)
			dst.initialized = true; 
		} else {
			// Register move: dst gets initialization status from src
			if (src != null) {
				dst.initialized = src.initialized;
			} else {
				// If src is null, dst should probably be considered uninitialized
				System.err.printf("Warning: IRcommand_Load (move) has null src for dst=%s, var=%s. Marking dst uninitialized.\n", dst, var_name);
				dst.initialized = false;
			}
		}

		// Transfer function: Output state is the input state 
		// (Load/Move doesn't change initialization status of other variables)
		// We only modified the status of 'dst' directly above.
		if (!in.equals(this.out)) { 
			this.out = in;
			if (nextCommands != null)
				for (int i : nextCommands) {
					workList.add(i);
				}
		}
	}

	@Override
	public void MIPSme() {
		MIPSGenerator gen = MIPSGenerator.getInstance();
		if (is_offset) {
			// Generate lw instruction relative to frame pointer
			gen.lw_fp(dst, offset); 

		} else {
			// Generate move instruction
			if (src != null) { // Ensure src is not null for move
				MIPSGenerator.getInstance().move(dst, src);
			} else {
				System.err.printf("ERROR: IRcommand_Load (move) has null src for dst=%s, var=%s\n", dst, var_name);
			}
		}
	}

	@Override
    public String toString() {
        return String.format("IRcommand_Load: dst=%s, offset=%d($fp), var=%s", dst, offset, var_name);
    }
}

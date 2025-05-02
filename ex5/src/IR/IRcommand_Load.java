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
		if (workList.contains(this.index)) {
			workList.remove(workList.indexOf(this.index));
		}
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
		// Check if destination TEMP needs a register (might be unused later)
		int dstRegNum = IR.getInstance().getRegister(dst);
		if (dstRegNum < 0) {
			// If the destination temporary doesn't get a register, 
			// it means its value is never used later. We can potentially skip the load.
			// However, for simplicity now, let's just print a warning and return.
			System.out.printf("Warning: Destination TEMP %s for Load (%s) has no register. Skipping MIPSme.\n", dst, var_name);
			return; 
		}
		if (src != null && IR.getInstance().getRegister(src) < 0) {
			// If the source temporary (for a move) doesn't have a register, it's an error.
			System.err.printf("ERROR: Source TEMP %s for Load (move, var=%s) has no register!\n", src, var_name);
			throw new RuntimeException("Source TEMP without register in Load (move)");
		}

		MIPSGenerator gen = MIPSGenerator.getInstance();
		if (is_offset) {
			// This is loading from the stack frame ($fp + offset)
			gen.lw_fp(dst, offset); 
		} else {
			// Generate move instruction
			if (src != null) { // Ensure src is not null for a register-to-register move
				gen.move(dst, src);
			} else {
				// This case should ideally not happen if AST_VAR_SIMPLE is correct
				System.err.printf("ERROR: IRcommand_Load (move form) has null src for dst=%s, var=%s\n", dst, var_name);
				throw new RuntimeException("Null source in Load (move form)");
			}
		}
	}

	@Override
    public String toString() {
        return String.format("IRcommand_Load: dst=%s, offset=%d($fp), var=%s", dst, offset, var_name);
    }

	@Override
	public HashSet<TEMP> liveTEMPs() {
		HashSet<TEMP> used = new HashSet<>();
		if (!is_offset && src != null) { // Only the move form uses a source TEMP
			used.add(src);
		}
		return used;
	}
}

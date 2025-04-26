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

public class IRcommand_Store extends IRcommand
{
	String var_name;
	TEMP src = null;
	public int offset;
	boolean is_offset = false;
	public IRcommand_Store(TEMP dst, TEMP src, String var_name)
	{
		this.dst = dst;
		this.src = src;
		this.var_name = var_name;
	}
	public IRcommand_Store(TEMP dst, int offset, String var_name)
	{
		this.dst = dst;
		this.offset = offset;
		this.var_name = var_name;
		is_offset = true;
	}
	@Override
    public String toString() {
		String base = String.format("IRcommand_Store: var_name=%s, src=%s, dst=%s", var_name, src, dst);
        return base;
    }

	public void staticAnalysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}

		if (src != null && src.initialized || is_offset)
		{
			in = in.stream().filter(init -> !init.var.equals(var_name)).collect(Collectors.toCollection(HashSet::new));
			in.add(new Init(var_name, this.index));
		}

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
		if (IR.getInstance().getRegister(dst) < 0) {
			System.err.println("ERROR: Destination register is not initialized" + var_name);
			return;
		}
		if (src != null && IR.getInstance().getRegister(src) < 0) {
			throw new RuntimeException("ERROR: Source register is not initialized despite being used in dest, static analysis failed" + var_name);
		}

		if (src != null) {
			MIPSGenerator.getInstance().move(dst, src);
		} else {
			MIPSGenerator.getInstance().sw_fp(dst, offset);
		}
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(src));
	}

}

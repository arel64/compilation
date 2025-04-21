/***********/
/* PACKAGE */
/***********/
package IR;
import AST.*;
import java.util.HashSet;
import java.util.ArrayList;
import MIPS.MIPSGenerator;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Func_Dec extends IRcommand
{
	public String name;
	public AST_TYPE type;
	public AST_LIST<AST_VAR_DEC> params;
	
	public IRcommand_Func_Dec(String name, AST_TYPE type, AST_LIST<AST_VAR_DEC> params)
	{
		this.name = name;
		this.type = type;
		this.params = params;
	}

	@Override
    public String toString() {
        return "IRcommand_Func_Dec: name=" + this.name + " type=" + this.type;
    }

	
	@Override
	public void staticAnalysis() {
		workList.remove(workList.indexOf(this.index));
		HashSet<Init> in = new HashSet<Init>();
		for (Integer i : prevCommands) {
			HashSet<Init> temp = IR.getInstance().commandList.get(i).out;
			if (temp != null)
				in.addAll(temp);
		}
		if (this.params != null) {
			for (AST_VAR_DEC param : this.params) {
				in.add(new Init(param.varName, this.index));
			}
		}
		this.out = new HashSet<Init>(in);
		if (nextCommands != null) {
			for (int i : nextCommands) {
				workList.add(i);
			}
		}
	}

	public void MIPSme() {
		// Prologue generation is now handled by MIPSGenerator
		// TODO: Calculate actual frame size based on locals and max args passed
		int frameSize = 8; // Placeholder for $fp (4 bytes) + $ra (4 bytes)
		MIPSGenerator.getInstance().genPrologue(frameSize);

		// NOTE: Argument handling (moving from $a0..$a3 or stack) would go here.
	}
}

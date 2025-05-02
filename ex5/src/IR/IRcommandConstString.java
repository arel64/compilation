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

public class IRcommandConstString extends IRcommand
{
	private static int stringCounter = 0; // Counter for unique string labels
	public String value;
	private String dataLabel; // Store the generated label for this instance
	
	public IRcommandConstString(TEMP dst, String value)
	{
		this.dst = dst;
		this.value = value;
		// Generate and store the unique label when the command is created
		this.dataLabel = "string_literal_" + stringCounter++; 
	}

	@Override
    public String toString() {
        // Optionally include the label in toString for debugging
        return String.format("IRcommandConstString: %s = %s (%s)", dst, value, dataLabel);
    }

	@Override
	public void MIPSme() {
		MIPSGenerator mipsGen = MIPSGenerator.getInstance();
		IR ir = IR.getInstance();

		// Only generate code if the destination temporary is actually used (allocated a register)
		if (ir.getRegister(dst) < 0) {
			return; 
		}

		// 1. Add the string literal to the .data segment
		// Ensure proper escaping of the string value for MIPS .asciiz
		// The input value might already contain quotes if it's from the source code literal
		String stringContent = value;
		if (stringContent.startsWith("\"") && stringContent.endsWith("\"") && stringContent.length() >= 2) {
			stringContent = stringContent.substring(1, stringContent.length() - 1);
		}
		String escapedValue = stringContent.replace("\\", "\\\\").replace("\"", "\\\""); 
		String dataDirective = String.format("%s: .asciiz \"%s\"", dataLabel, escapedValue);
		mipsGen.addDataDirective(dataDirective);

		// 2. Load the address of the label into the destination register
		mipsGen.la(dst, dataLabel);
	}

	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}
}

/***********/
/* PACKAGE */
/***********/
package IR;
import AST.AST_TYPE;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import MIPS.MIPSGenerator;

public class IRcommand_Class_Dec extends IRcommand
{
	public String name;
	public List<String> vmtMethodLabels;
	
	public IRcommand_Class_Dec(String name, List<String> vmtMethodLabels)
	{
		this.name = name;
		this.vmtMethodLabels = (vmtMethodLabels != null) ? new ArrayList<>(vmtMethodLabels) : new ArrayList<>();
	}

	@Override
    public String toString() {
        return String.format("CLASS_DEC: %s, VMT_Labels: %s", name, vmtMethodLabels);
    }


	@Override
	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>();
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator generator = MIPSGenerator.getInstance();
		String vmtLabel = "VMT_" + name;
		StringBuilder vmtData = new StringBuilder();
		vmtData.append(vmtLabel).append(": .word ");

		if (vmtMethodLabels.isEmpty()) {
			vmtData.append("0");
		} else {
			for (int i = 0; i < vmtMethodLabels.size(); i++) {
				vmtData.append(vmtMethodLabels.get(i));
				if (i < vmtMethodLabels.size() - 1) {
					vmtData.append(", ");
				}
			}
		}
		generator.addDataDirective(vmtData.toString());
	}
}

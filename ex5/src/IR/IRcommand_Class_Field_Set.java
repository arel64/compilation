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
import java.util.Arrays;
import java.util.HashSet;
import MIPS.MIPSGenerator;

public class IRcommand_Class_Field_Set extends IRcommand {
	public TEMP objectBaseAddr;
	public int offset;
	public TEMP valueToStore;

	public IRcommand_Class_Field_Set(TEMP objectBaseAddr, int offset, TEMP valueToStore) {
		this.objectBaseAddr = objectBaseAddr;
		this.offset = offset;
		this.valueToStore = valueToStore;
	}

	@Override
	public String toString() {
		return String.format("CLASS_FIELD_SET: (%s) offset %d := %s", objectBaseAddr, offset, valueToStore);
	}

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(valueToStore, objectBaseAddr));
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();
		// Correctly call genStoreField with TEMP objects
		System.out.println("The offset is11 " + offset + " for " + valueToStore);
		generator.genStoreField(valueToStore, objectBaseAddr, offset);
	}
}

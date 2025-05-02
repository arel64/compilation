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

public class IRcommand_Class_Field_Set extends IRcommand
{
	public TEMP objectBaseAddr;
	public int offset;
	public TEMP valueToStore;
	public String className;
	public String fieldName;
	
	public IRcommand_Class_Field_Set(TEMP objectBaseAddr, int offset, TEMP valueToStore, String className, String fieldName)
	{
		this.objectBaseAddr = objectBaseAddr;
		this.offset = offset;
		this.valueToStore = valueToStore;
		this.className = className;
		this.fieldName = fieldName;
	}

	@Override
    public String toString() {
        return String.format("CLASS_FIELD_SET: %s.%s (offset %d) := %s", objectBaseAddr, fieldName, offset, valueToStore);
    }

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(valueToStore, objectBaseAddr));
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		MIPSGenerator.getInstance().sw_offset(valueToStore, offset, objectBaseAddr);
	}
}

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

public class IRcommand_Array_Set extends IRcommand
{
	TEMP array;
	TEMP index;
	TEMP value;
	
	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value)
	{
		this.array = array;
		this.index = index;
		this.value = value;
		IR.getInstance().recordVarTemp("temp_" + array.getSerialNumber(), array);
	}
	
	@Override
	public String toString() {
		return "IRcommand_Array_Set: array=" + array + ", index=" + index + ", value=" + value;
	}

	@Override
	public void MIPSme() {
		//MIPSGenerator.getInstance().allocate(var_name);
	}

		public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(array, index, value));
	}

}

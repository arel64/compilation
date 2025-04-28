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

public class IRcommand_Array_Set extends IRcommand
{
	public TEMP array;
	public TEMP index;
	public TEMP value;
	
	public IRcommand_Array_Set(TEMP array, TEMP index, TEMP value)
	{
		this.array = array;
		this.index = index;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "IRcommand_Array_Set: array=" + array + ", index=" + index + ", value=" + value;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator generator = MIPSGenerator.getInstance();
		// Calculate offset = index * 4 into the index register itself
		generator.sll(index, index, 2); // index = index << 2
		// Calculate address = arrayBase + offset (now in index) into the index register
		generator.add(index, array, index); // index = array + index (offset)
		// Store the value: *(address in index) = value
		generator.sw_temp_offset(value, 0, index); // *(index) = value
	}

		public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(array, index, value));
	}

}

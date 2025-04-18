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

public class IRcommand_Class_Field_Set extends IRcommand
{
	public TEMP src;
	public String field;
	public TEMP value;
	
	public IRcommand_Class_Field_Set(TEMP src, String field, TEMP value)
	{
		this.src = src;
		this.field = field;
		this.value = value;
	}

	@Override
    public String toString() {
        return "IRcommand_Class_Field_Set: src=" + src + ", field=" + field + ", value=" + value;
    }

	public HashSet<TEMP> liveTEMPs() {
		return new HashSet<TEMP>(Arrays.asList(value, src));
	}
}

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

public class IRcommand_Class_Field_Set extends IRcommand
{
	TEMP dst;
	String field;
	TEMP value;
	
	public IRcommand_Class_Field_Set(TEMP dst, String field, TEMP value)
	{
		this.dst = dst;
		this.field = field;
		this.value = value;
	}

	@Override
    public String toString() {
        return "IRcommand_Class_Field_Set: dst=" + dst + ", field=" + field + ", value=" + value;
    }
}

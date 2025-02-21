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

public class IRcommand_Label extends IRcommand
{
	public String label_name;
	
	public IRcommand_Label(String label_name)
	{
		this.label_name = label_name;
	}

	@Override
	public void MIPSme() {
		MIPSGenerator.getInstance().label(label_name);
	}

	@Override
    public String toString() {
        return "IRcommand_Label: label=" + label_name;
    }
}

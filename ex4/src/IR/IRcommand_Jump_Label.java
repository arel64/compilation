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

public class IRcommand_Jump_Label extends IRcommand
{
	String label_name;
	
	public IRcommand_Jump_Label(String label_name)
	{
		this.label_name = label_name;
		this.nextCommands = new int[]{-1}; // need to create a map of labels to index or something...
	}

	@Override
    public String toString() {
        return "IRcommand_Jump_Label: label=" + label_name;
    }
}

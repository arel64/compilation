/***********/
/* PACKAGE */
/***********/
package TEMP;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class TEMP
{
	private int serial=0;
	public boolean initialized = true;
	
	public TEMP(int serial)
	{
		this.serial = serial;
	}
	
	public int getSerialNumber()
	{
		return serial;
	}

	@Override
	public String toString() {
        return "TEMP_" + serial;
    }
}

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

public class TEMP_FACTORY
{
	private int counter=0;
	public boolean isRegistersAllocated = false;
	public TEMP getFreshTEMP()
	{
		if (isRegistersAllocated)
		{
			throw new RuntimeException("Registers are  allocated cannot call fresh");
		}
		return new TEMP(counter++);
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TEMP_FACTORY instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TEMP_FACTORY() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TEMP_FACTORY getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new TEMP_FACTORY();
		}
		return instance;
	}
}

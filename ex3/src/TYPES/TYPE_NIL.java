package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_NIL extends TYPE{
    private static TYPE_NIL instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_NIL() {super("nil");}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_NIL getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_NIL();
		}
		return instance;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return false;
	}
}

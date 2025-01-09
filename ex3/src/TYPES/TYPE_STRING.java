package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_STRING extends TYPE
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TYPE_STRING instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_STRING() {super("string");}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_STRING getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_STRING();
		}
		return instance;
	}
	
	@Override
	public String toString() {
		return "TYPE_STRING";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_STRING;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return other instanceof TYPE_STRING;
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
}

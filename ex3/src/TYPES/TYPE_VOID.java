package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_VOID extends TYPE
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TYPE_VOID instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_VOID() {super("void");}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_VOID getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_VOID();
		}
		return instance;
	}
	@Override
	public boolean isVoid() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_VOID;
	}
	@Override
	public boolean isAssignable(TYPE other){
		System.out.printf("HERE 6");
		return false;
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
}

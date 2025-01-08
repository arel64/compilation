package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_INT extends TYPE
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TYPE_INT instance = new TYPE_INT();

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_INT() {super("int");}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_INT getInstance()
	{

		return instance;
	}
	@Override
	public String toString() {
		return "TYPE_INT";
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_INT;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return other instanceof TYPE_INT;
	}
}

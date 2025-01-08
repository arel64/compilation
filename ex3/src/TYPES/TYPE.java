package TYPES;

import SYMBOL_TABLE.SemanticException;

public abstract class TYPE
{
	private String name;
	public TYPE(String name)
	{
		this.name = name;
	}

	public boolean isClass(){ return this instanceof TYPE_CLASS;}
	public boolean isArray(){ return false;}
	public boolean isFunction(){ return this instanceof TYPE_FUNCTION;}
	public boolean isVoid() { return this instanceof TYPE_VOID;};
	public String getName(){
		return name;
	}
	public abstract boolean isAssignable(TYPE other) throws SemanticException;
}

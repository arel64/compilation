package TYPES;

import SYMBOL_TABLE.SemanticException;

public abstract class TYPE_WRAPPER extends TYPE
{
	private String name;
	public TYPE_WRAPPER(String name)
	{
		super(name);
	}

	public boolean isClass(){ return this instanceof TYPE_CLASS;}
	public boolean isArray(){ return false;}
	public boolean isFunction(){ return this instanceof TYPE_FUNCTION;}
	public boolean isVoid() { return this instanceof TYPE_VOID;};
	public String getName(){
		return name;
	}
	public boolean isPrimitive(){ return false;}
	public abstract boolean isAssignable(TYPE other)throws SemanticException;
	public boolean isInterchangeableWith(TYPE other) throws SemanticException{ 
		return isAssignable(other);
	}
}

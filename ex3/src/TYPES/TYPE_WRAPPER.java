package TYPES;

import SYMBOL_TABLE.SemanticException;

public abstract class TYPE_WRAPPER extends TYPE
{
	public TYPE t;
	public TYPE_WRAPPER(String name,TYPE t)
	{
		super(name);
		this.t = t;
	}

	public boolean isAssignable(TYPE other) throws SemanticException
	{
		TYPE otherType = null;
		if(other instanceof TYPE_WRAPPER)
		{
			otherType = ((TYPE_WRAPPER)other).t;
			return t.isAssignable(otherType);
		}
		else if(other.isPrimitive())
		{
			return t.equals(other);
		}
		return false;

	}
}

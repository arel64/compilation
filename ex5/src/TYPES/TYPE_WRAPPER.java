package TYPES;

import SYMBOL_TABLE.SemanticException;

public abstract class TYPE_WRAPPER extends TYPE
{
	public TYPE t;
	public TYPE_WRAPPER(String name,TYPE t)
	{
		super(name);
		if(t instanceof TYPE_WRAPPER)
		{
			this.t = ((TYPE_WRAPPER)t).t;
		}
		else
		{
			this.t = t;
		}
	}

	public boolean isAssignable(TYPE other) throws SemanticException
	{
		TYPE otherType = null;
		if(other instanceof TYPE_WRAPPER)
		{
			otherType = ((TYPE_WRAPPER)other).t;
			return isAssignable(otherType);
		}
		return (t.equals(other));
	}

	public boolean isDoubleWrapped() {
		return t instanceof TYPE_WRAPPER;
	}
}

package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_RETURN extends TYPE_WRAPPER
{
	public TYPE_RETURN(TYPE t)
	{
		super("return",t);
	}
	@Override
	public String toString() {
		return String.format("RETURN %s",t);
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		if(!(other instanceof TYPE_RETURN))
		{
			if(other.isClass())
			{
				return ((TYPE_CLASS)other).isAssignable(t);
			}
			return super.isAssignable(other);
		}
		TYPE otherInner = ((TYPE_RETURN)other).t;
		return isAssignable(otherInner);
	}
	
	
}

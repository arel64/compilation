package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_VAR_DEC extends TYPE
{
	public TYPE t;
	public TYPE_VAR_DEC(TYPE t,String name)
	{
		super(name);
		this.t = t;
	}
	@Override
	public String toString() {
		return String.format("VAR DEC %s %s",t,getName());
	}
	public boolean isFunction() {
		return this instanceof TYPE_FUNCTION;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_VAR_DEC && t == ((TYPE_VAR_DEC)obj).t;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return (other instanceof TYPE_VAR_DEC && t.isAssignable(((TYPE_VAR_DEC)other).t) || (other.isPrimitive() && t.equals(other)));
	}
	
}

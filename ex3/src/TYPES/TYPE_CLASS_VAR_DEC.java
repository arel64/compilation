package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS_VAR_DEC extends TYPE
{
	public TYPE t;
	public int line;
	public TYPE_CLASS_VAR_DEC(TYPE t,String name,int line)
	{
		super(name);
		this.t = t;
		this.line = line;
	}
	@Override
	public String toString() {
		return String.format("CLASS VAR DEC %s %s",t,getName());
	}
	public boolean isFunction() {
		return this instanceof TYPE_FUNCTION;
	}
	@Override
	public boolean equals(Object obj) {
		System.out.printf("var dec %s %s",this.t,((TYPE_CLASS_VAR_DEC)obj).t);
		return obj instanceof TYPE_CLASS_VAR_DEC && t == ((TYPE_CLASS_VAR_DEC)obj).t;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return other instanceof TYPE_CLASS_VAR_DEC && t.isAssignable(other);
	}
}

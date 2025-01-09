package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_ARRAY_SUBSCRIPT extends TYPE_WRAPPER
{
	public TYPE_ARRAY arr;
	public TYPE subscript;
	public TYPE_ARRAY_SUBSCRIPT(TYPE_ARRAY arr ,TYPE subscript)
	{
		super("arraySubscipt",arr.t);
		this.arr = arr;
		this.subscript = subscript;
	}

	@Override
	public boolean isArray() {
		return arr.t.isArray();
	}
	@Override
	public boolean isClass() {
		return arr.t.isClass();
	}
	
}

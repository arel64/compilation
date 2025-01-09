package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_ARRAY_SUBSCRIPT extends TYPE
{
	public TYPE_ARRAY arr;
	public TYPE subscript;
	public TYPE_ARRAY_SUBSCRIPT(TYPE_ARRAY arr ,TYPE subscript)
	{
		super("arraySubscipt");
		this.arr = arr;
		this.subscript = subscript;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		System.out.println("Checked assignable " +other+" to "+arr.t );
		return other.isAssignable(arr.t);
	}

}

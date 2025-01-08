package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_FOR_SCOPE_BOUNDARIES extends TYPE
{
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FOR_SCOPE_BOUNDARIES(String name)
	{
		super(name);
	}

	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		throw new UnsupportedOperationException("Unimplemented method 'isAssignable'");
	}
}

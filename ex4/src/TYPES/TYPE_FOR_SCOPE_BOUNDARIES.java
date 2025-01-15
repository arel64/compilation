package TYPES;

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
	public boolean isAssignable(TYPE other) {
		throw new UnsupportedOperationException("Unimplemented method 'isAssignable'");
	}
}

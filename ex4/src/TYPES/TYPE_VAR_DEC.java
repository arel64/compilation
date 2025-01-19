package TYPES;


public class TYPE_VAR_DEC extends TYPE_WRAPPER
{
	public TYPE_VAR_DEC(TYPE t,String name)
	{
		super(name,t);
	}
	@Override
	public String toString() {
		return String.format("VAR DEC %s %s",t,getName());
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_VAR_DEC && t .equals(((TYPE_VAR_DEC)obj).t);
	}
	
}

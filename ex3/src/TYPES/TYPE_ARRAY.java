package TYPES;

public class TYPE_ARRAY extends TYPE
{
	public TYPE t;
	
	public TYPE_ARRAY(TYPE t,String name)
	{
		super(name);
		this.t = t;
	}

	@Override
	public boolean isVoid() {
		return t.isVoid();
	}
	@Override
	public boolean isArray() {
		return true;
	}
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TYPE_ARRAY && t.equals(((TYPE_ARRAY)obj).t) && getName().equals(((TYPE_ARRAY)obj).getName());
	}
	@Override
	public boolean isAssignable(TYPE other) {
		return other instanceof TYPE_NIL;
	}

}

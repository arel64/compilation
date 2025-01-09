package TYPES;

public class TYPE_ARRAY extends TYPE_VAR_DEC
{
	public TYPE_ARRAY(TYPE t,String name)
	{
		super(t,name);
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
		return isInterchangeableWith(other) || (((TYPE_ARRAY)other).t == t);
	}
	@Override
	public boolean isInterchangeableWith(TYPE other) {
		return (other instanceof TYPE_NIL)  // Null is assignable to arrrays.
			|| (other instanceof TYPE_ARRAY) && // Must be array otherwise
					(other.getName().equals(getName())// If the types have the same name its fine
			);
	}
	@Override
	public String toString() {
		return String.format("%s %s[]", t.getName(),getName());
	}

}

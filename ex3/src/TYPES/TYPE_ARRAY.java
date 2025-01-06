package TYPES;

public class TYPE_ARRAY extends TYPE
{
	public TYPE t;
	
	public TYPE_ARRAY(TYPE t)
	{
		this.t = t;
	}

	@Override
	public boolean isVoid() {
		return t.isVoid();
	}
}

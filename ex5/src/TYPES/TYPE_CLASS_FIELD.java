package TYPES;

public class TYPE_CLASS_FIELD extends TYPE_WRAPPER
{
	public int line;
	public int offset = -1; // Initialize to -1 or some invalid value

	public TYPE_CLASS_FIELD(String name,TYPE t,int line)
	{
		super(name, t);
		this.line =line;
	}


	@Override
	public String toString() {
		return String.format("CLASS Field %s type %s (offset: %d)", this.getName(), t, offset);
	}
}

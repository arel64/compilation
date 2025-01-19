package TYPES;

public class TYPE_CLASS_FIELD extends TYPE_WRAPPER
{
	public int line;
	public TYPE_CLASS_FIELD(String name,TYPE t,int line)
	{
		super(name, t);
		this.line =line;
	}
	@Override
	public String toString() {
		return String.format("CLASS Field %s type %s",this.getName(),t);
	}
	
	
}

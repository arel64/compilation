package TYPES;

public class TYPE_CLASS_FIELD
{
	public int line;
	public TYPE t;
	public String name;
	public TYPE_CLASS_FIELD(String name,TYPE t,int line)
	{
		this.name =name;
		this.t = t;
		this.line =line;
	}
	@Override
	public String toString() {
		return String.format("CLASS Field %s type %s",name,t);
	}
	
}

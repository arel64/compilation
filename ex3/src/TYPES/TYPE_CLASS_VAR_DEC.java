package TYPES;

public class TYPE_CLASS_VAR_DEC extends TYPE
{
	public TYPE t;
	public String name;
	public int line;
	public TYPE_CLASS_VAR_DEC(TYPE t,String name,int line)
	{
		this.t = t;
		this.name = name;
		this.line = line;
	}
	@Override
	public String toString() {
		return String.format("CLASS VAR DEC %s %s",t,name);
	}
	public boolean isFunction() {
		return this instanceof TYPE_FUNCTION;
	}

}

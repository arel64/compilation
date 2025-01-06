package TYPES;

public abstract class TYPE
{
	public String name;

	public boolean isClass(){ return false;}

	public boolean isArray(){ return false;}

	public boolean isVoid() { return false;};
	public boolean equals(TYPE obj) {
		return name == obj.name;
	}
}

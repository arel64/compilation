package TYPES;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	public boolean isClass(){ return false;}

	public boolean isArray(){ return false;}

	public boolean equals(TYPE obj) {
		return name == obj.name;
	}
}

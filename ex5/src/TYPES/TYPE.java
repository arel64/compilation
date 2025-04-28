package TYPES;

import SYMBOL_TABLE.SemanticException;

public abstract class TYPE
{
	private String name;
	public TYPE(String name)
	{
		this.name = name;
	}

	public boolean isClass(){ return this instanceof TYPE_CLASS;}
	public boolean isArray(){ return false;}
	public boolean isFunction(){ return this instanceof TYPE_FUNCTION;}
	public boolean isVoid() { return this instanceof TYPE_VOID;};
	public String getName(){
		return name;
	}
	public boolean isPrimitive(){ return false;}
	public abstract boolean isAssignable(TYPE other)throws SemanticException;
	public boolean isInterchangeableWith(TYPE other) throws SemanticException{ 
		return isAssignable(other);
	}

    /**
     * Returns the size of this type in bytes for memory allocation/layout.
     * Primitives have fixed sizes, pointers are typically 4 bytes,
     * classes sum non-method field sizes, functions/void are 0.
     */
    public abstract int getSize();
}

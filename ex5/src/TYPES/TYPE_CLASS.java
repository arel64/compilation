package TYPES;

import java.util.HashSet;
import java.util.Set;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;
	private TYPE_CLASS_VAR_DEC_LIST memberList;
	public int line;
    private int instanceSize = -1; // Store calculated instance size
	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/	
	/****************/
	/* CTROR(S) ... */
	/**
	 * @throws SemanticException **************/
	public TYPE_CLASS(TYPE_CLASS father,String name,int line) throws SemanticException
	{
		this((TYPE)father, name,line);
	}
	public TYPE_CLASS(String father,String name,int line) throws SemanticException
	{
		this(SYMBOL_TABLE.getInstance().find(father),name,line);
	}
	private TYPE_CLASS(TYPE father, String name, int line) throws SemanticException
	{
		super(name);
		this.line =line;
		if(father!=null && !(father instanceof TYPE_CLASS))
		{
			throw new SemanticException(line,String.format("Cannot extends non class type %s",father));
		}
		if ( father != null)
		{
			this.father = (TYPE_CLASS) father;
		}

	}
	@Override
	public boolean isClass() {
		return true;
	}
	public boolean isDerivedFrom(TYPE_CLASS other) throws SemanticException
	{
		String sharedType = getSharedType(other);
		if(sharedType == null)
		{
			return false;
		}
		TYPE_CLASS iterator = this;
		while(iterator != null)
		{
			if(iterator.getName() == other.getName())
			{
				return true;
			}
			iterator = iterator.father;
		}
		return false;
	}
	public String getSharedType(TYPE_CLASS other) throws SemanticException
	{
		if(other == null)
		{
			return null;
		}
		SYMBOL_TABLE table = SYMBOL_TABLE.getInstance();
		if(!table.exists(this.getName()) || !table.exists(other.getName()))
		{
			throw new SemanticException(line,"Shared check between inexistant classes" + this.getName() +" "+ other.getName());
		}
		if(getName() == other.getName())
		{
			return getName();
		}
		TYPE_CLASS t1 = this;
		Set<TYPE_CLASS> ancestors = new HashSet<>();
		while(t1 != null)
		{
			ancestors.add(t1);
			t1 = t1.father;
		}
		TYPE_CLASS t2 = other;
		while(t2 != null)
		{
			if(ancestors.contains(t2))
			{
				return t2.getName();
			}
			t2 = t2.father;
		}
		return null;
		
	}

	@Override
	public String toString() {
	 	return String.format("Name: %s",getName());
	}
	public TYPE_CLASS_VAR_DEC_LIST getDataMembers()
	{
		return this.memberList;
	}

	public void setDataMembers(TYPE_CLASS_VAR_DEC_LIST members){
		this.memberList = members;
	}
	// Add a setter for the instance size
	public void setInstanceSize(int size) {
		this.instanceSize = size;
	}

	public TYPE_CLASS_FIELD getDataMember(String name)
	{
		
		if(memberList == null)
		{
			return null;
		}
		for(TYPE_CLASS_FIELD member : memberList)
		{
			if(member.getName().equals(name))
			{
				return member;
			}
		}
		return null;

	}
	public int getDataMemberOffset(String fieldName) throws SemanticException {
		 TYPE_CLASS_FIELD field = getDataMember(fieldName);
		 if (field == null) {
			 throw new SemanticException(line, String.format("Field '%s' not found in class '%s'.", fieldName, getName()));
		 }
		 if (field.offset < 0) {
			 // This likely means it's a method or offset wasn't calculated correctly
			 throw new SemanticException(line, String.format("Offset for field '%s' in class '%s' is invalid (%d).", fieldName, getName(), field.offset));
		 }
		 return field.offset;
	}

	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return other instanceof TYPE_NIL || (other instanceof TYPE_CLASS && ((TYPE_CLASS)other).isDerivedFrom(this));
	}

    @Override
    public int getSize()  {
        // Return the pre-calculated size. Should not be -1 if SemantMe ran correctly.
        if (instanceSize < 0) {
             // Consider throwing an error or logging a warning if size wasn't calculated
             System.err.printf("Warning: getSize() called on class '%s' before instance size was calculated.%n", getName());
             return 0; // Or throw?
        }
        return instanceSize;
    }

    // Remove or comment out getSizeRecursive if no longer needed
    // private int getSizeRecursive(Set<TYPE_CLASS> visited)  {
    // ...
    //}
}

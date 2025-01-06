package TYPES;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_CLASS_VAR_DEC_LIST data_members;
	
	/****************/
	/* CTROR(S) ... */
	/**
	 * @throws SemanticException **************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_CLASS_VAR_DEC_LIST data_members) throws SemanticException
	{
		this((TYPE)father, name, data_members);
	}
	public TYPE_CLASS(String father,String name,TYPE_CLASS_VAR_DEC_LIST data_members) throws SemanticException
	{
		this(SYMBOL_TABLE.getInstance().find(father),name,data_members);
	}
	private TYPE_CLASS(TYPE father, String name, TYPE_CLASS_VAR_DEC_LIST data_members) throws SemanticException
	{
		if(father!=null && !(father instanceof TYPE_CLASS))
		{
			throw new SemanticException(String.format("Cannot extends non class type %s",father));
		}
		this.name = name;
		this.data_members = data_members;
		/**
		 * Verify no overloading, shadow varriables
		 */
		if ( father != null)
		{
			this.father = (TYPE_CLASS)father;
			data_members.extending(((TYPE_CLASS)father).data_members);		
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
			if(iterator.name == sharedType)
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
		if(!table.exists(this.name) || !table.exists(other.name))
		{
			throw new SemanticException("Shared check between inexistant classes" + this.name +" "+ other.name);
		}
		if(this.name == other.name)
		{
			return this.name;
		}
		String otherFatherShared = getSharedType(other.father);
		String myFatherShared = getSharedType(father);
		//Impies cycle in inheritence
		assert(!(otherFatherShared != null && myFatherShared != null));
		if(myFatherShared == null)
		{
			return otherFatherShared;
		}
		return myFatherShared;
		
	}
	@Override
	public String toString() {
		return String.format("Father: %s Name: %s",father,name);
	}
}

package TYPES;

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
	/****************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_CLASS_VAR_DEC_LIST data_members)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
		/**
		 * Verify no overloading, shadow varriables
		 */
		data_members.extending(father.data_members);		
	}
	@Override
	public boolean isClass() {
		return true;
	}
}

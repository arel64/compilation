package TYPES;

public class TYPE_FUNCTION extends TYPE_CLASS_VAR_DEC
{

	public TYPE_LIST params;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params)
	{
		super(returnType, name);
		this.params = params;
	}

    public boolean isOverriding(TYPE_FUNCTION t) {
		return this.name == t.name && t.params.equals(params) && this.t == t.t;
    }
	
}

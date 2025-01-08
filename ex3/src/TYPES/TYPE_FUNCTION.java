package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_FUNCTION extends TYPE_CLASS_VAR_DEC
{

	private TYPE_LIST params;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,int line)
	{
		this(returnType, name, null, line);
	}
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params,int line)
	{
		super(returnType, name,line);
		this.params = params;
		System.out.println(String.format("Function type name %s",name));	
	}

    public boolean isOverriding(TYPE_FUNCTION t) {
		if(t == null)
		{
			return false;
		}
		boolean cond =this.getName().equals(t.getName()) && this.t == t.t;
		if(t.params != null)
		{
			cond = cond && t.params.equals(params);
		}
		else
		{
			cond = cond && params == null;
		}
		return cond;
    }

	@Override
	public String toString(){
		return String.format("FUNCTION %s(%s) -> %s",getName(), params, t);
	}
	public void setParams(TYPE_LIST params)
	{
		this.params = params;
	}
	public TYPE getParam(int i){
		return params.get(i);
	}
	public TYPE_LIST getParams()
	{
		return params;
	}
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return false;
	}
}

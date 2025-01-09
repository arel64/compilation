package TYPES;

import SYMBOL_TABLE.SemanticException;

public class TYPE_FUNCTION extends TYPE_VAR_DEC
{

	private TYPE_LIST params;
	private int line;
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,int line)
	{
		this(returnType, name, null, line);
	}
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params,int line)
	{
		super(returnType, name);
		this.params = params;
		this.line = line;
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
	public TYPE getReturnType()
	{
		return t;
	}
}

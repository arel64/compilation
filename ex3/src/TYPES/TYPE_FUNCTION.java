package TYPES;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_FUNCTION extends TYPE_CLASS_VAR_DEC
{

	public TYPE_LIST params;
	
	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params,int line)
	{
		super(returnType, name,line);
		this.params = params;
	}

    public boolean isOverriding(TYPE_FUNCTION t) {
		// System.out.println("this is the t"+ this.t.toString());
		return this.name == t.name && t.params.equals(params) && this.t == t.t;
    }

	@Override
	public String toString(){
		return String.format("FUNCTION %s(%s) -> %s", name, params, t);
	}
	
}

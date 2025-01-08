package TYPES;

import AST.AST_LIST;
import AST.AST_Node;
import SYMBOL_TABLE.SemanticException;

public class TYPE_LIST
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public TYPE_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/**
	 * @throws SemanticException ****************/
	public TYPE_LIST(AST_LIST<? extends AST_Node> list) throws SemanticException
	{
		if (list == null) {
			throw new SemanticException(-1,"Not found in the symbol table!!!fooyaaaa");
		}
		tail = null;
		head = null;
		if (list.size() > 0)
		{
			head = list.at(0).SemantMe();
			if (head == null)
			{
				throw new SemanticException(list.lineNumber,"First element in the list cannot be null");
			}
			if (list.size() > 1) {
				tail = new TYPE_LIST(list.from(1));
			}
		}
	}
	public TYPE get(int i)
	{

		if(i ==0)
		{
			return head;
		}
		if( head == null)
		{
			return null;
		}
		return get(i-1);

	}
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}
	public boolean equals(TYPE_LIST obj) {
		System.out.printf("list eq %s %s %s \n", this.head,obj.head,this.head.equals( obj.head));
		if(tail == null)
		{
			return this.head .equals( obj.head) && obj.tail == null;
		}
		return this.head .equals(  obj.head) && tail.equals(obj); 
	}
	@Override
	public String toString() {
		return String.format("%s %s",head,tail);
	}

}

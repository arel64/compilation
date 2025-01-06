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
		tail = null;
		head = null;
		if(list.size() > 0)
		{
			head = list.at(0).SemantMe();
		}
		tail = new TYPE_LIST(list.from(0));
	}
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}
	public boolean equals(TYPE_LIST obj) {
		return this.head == obj.head && tail.equals(obj); 
	}
}

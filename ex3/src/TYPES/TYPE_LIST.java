package TYPES;

import java.util.ArrayList;
import java.util.List;

import AST.AST_LIST;
import AST.AST_Node;
import SYMBOL_TABLE.SemanticException;

public class TYPE_LIST
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	private List<TYPE> list;


	
	public TYPE_LIST(List<TYPE> list)
	{
		this.list = list;
		System.out.println("List size :" + list.size());
	}
	/******************/
	/* CONSTRUCTOR(S) */
	/**
	 * @throws SemanticException ****************/
	public TYPE_LIST(AST_LIST<? extends AST_Node> list) throws SemanticException
	{
		List<TYPE> typeList = new ArrayList<>();
		for(AST_Node element : list)
		{
			TYPE t =element.SemantMeLog();
			typeList.add(t);
		}
		this.list = typeList;
	}
	public TYPE get(int i)
	{
		return list.get(i);
	}
	public int size()
	{
		return list.size();
	}
	public boolean equals(TYPE_LIST obj) {
		if (obj == null || obj.size() != size())
		{
			return false;
		}
		for(int i =0; i < size(); i++)
		{
			if(get(i)!= obj.get(i))
			{
				return false;
			}
		}
		return true;
	}
	@Override
	public String toString() {
		return String.format(list.toString());
	}

}

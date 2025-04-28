package TYPES;

import java.util.ArrayList;
import java.util.List;

import AST.AST_LIST;
import AST.AST_Node;
import SYMBOL_TABLE.SemanticException;

public class TYPE_LIST extends TYPE
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	private List<TYPE> list;
	private List<Integer> lineNumbers;

	public TYPE_LIST()
	{
		super("typelist");
		list = new ArrayList<TYPE>();
		lineNumbers = new ArrayList<>();
	}
	/******************/
	/* CONSTRUCTOR(S) */
	/**
	 * @throws SemanticException ****************/
	public TYPE_LIST(AST_LIST<? extends AST_Node> list) throws SemanticException
	{
		super("typelist");
		List<TYPE> typeList = new ArrayList<>();
		List<Integer> lineNumbers = new ArrayList<>();

		for(AST_Node element : list)
		{
			TYPE t =element.SemantMeLog();
			typeList.add(t);
			lineNumbers.add(element.lineNumber);
		}
		this.list = typeList;
		this.lineNumbers = lineNumbers;
	}

	public void add(TYPE t,int lineNumber)
	{
		list.add(t);
		lineNumbers.add(lineNumber);
	}
	public TYPE get(int i)
	{
		return list.get(i);
	}
	public int getLineNumber(int i)
	{
		return lineNumbers.get(i);
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
			if(!get(i).equals(obj.get(i)))
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
	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return false;
	}
	@Override
	public int getSize() {
		int totalSize = 0;
		for (TYPE t : list) {
			totalSize += t.getSize();
		}
		return totalSize;
	}
}

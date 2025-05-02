/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

import TEMP.TEMP;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;


/**********************/
/* SYMBOL TABLE ENTRY */
/**********************/
public class SYMBOL_TABLE_ENTRY
{
	/*********/
	/* index */
	/*********/
	int index;
	
	/********/
	/* name */
	/********/
	public String name;

	/******************/
	/* TYPE value ... */
	/******************/
	public TYPE type;

	
	/******************************************/
	/* Offset relative to frame pointer ($fp) */
	/* Used for local variables & parameters  */
	/* Negative for locals, positive for params */
	/******************************************/
	public int offset = Integer.MIN_VALUE; // Sentinel: Not yet assigned

	/******************************************/
	/* Size of the type in memory (bytes)   */
	/******************************************/
	public int size = 0; // Will be set during semantic analysis

	/******************************************/
	/* AST Node for the declaration (VAR_DEC) */
	/******************************************/

	/*********************************************/
	/* prevtop and next symbol table entries ... */
	/*********************************************/
	public SYMBOL_TABLE_ENTRY prevtop;
	public SYMBOL_TABLE_ENTRY next;
	public boolean isGlobal;
	/****************************************************/
	/* The prevtop_index is just for debug purposes ... */
	/****************************************************/
	public int prevtop_index;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public SYMBOL_TABLE_ENTRY(
		String name,
		TYPE type,
		int index,
		SYMBOL_TABLE_ENTRY next,
		SYMBOL_TABLE_ENTRY prevtop,
		int prevtop_index,
		boolean isGlobal)
	{
		this.index = index;
		this.name = name;
		this.type = type;
		this.next = next;
		this.prevtop = prevtop;
		this.prevtop_index = prevtop_index;
		this.isGlobal = isGlobal;
	}

	@Override
	public String toString() {
		return "SYMBOL_TABLE_ENTRY{ \n" + 
				"name='" + name + '\n' +
				", type=" + type + '\n' +
				", index=" + index + '\n' +
				", size=" + size + '\n' +
				", isGlobal=" + isGlobal + '\n' +
				'}';
	}
	public boolean isGlobal() {
		return isGlobal;
	}
}

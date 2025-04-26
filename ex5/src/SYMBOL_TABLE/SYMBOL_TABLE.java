/***********/
/* PACKAGE */
/***********/
package SYMBOL_TABLE;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;
import TEMP.*;
import AST.AST_VAR_DEC;

/****************/
/* SYMBOL TABLE */
/****************/
public class SYMBOL_TABLE
{
	private int hashArraySize = 13;
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SYMBOL_TABLE_ENTRY[] table = new SYMBOL_TABLE_ENTRY[hashArraySize];
	private SYMBOL_TABLE_ENTRY top;
	private int top_index = 0;
	private int scope_count = 0;
	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public void enter(String name,TYPE t)
	{
		enter(name, t, null);
	}

	public void enter(String name, TYPE t, AST_VAR_DEC declarationNode)
	{
		/*************************************************/
		/* [1] Compute the hash value for this new entry */
		/*************************************************/
		int hashValue = hash(name);

		/******************************************************************************/
		/* [2] Extract what will eventually be the next entry in the hashed position  */
		/*     NOTE: this entry can very well be null, but the behaviour is identical */
		/******************************************************************************/
		SYMBOL_TABLE_ENTRY next = table[hashValue];
	
		/**************************************************************************/
		/* [3] Prepare a new symbol table entry with name, type, node, next, prevtop */
		/**************************************************************************/
		SYMBOL_TABLE_ENTRY e = new SYMBOL_TABLE_ENTRY(name, t, hashValue, next, top, top_index++, declarationNode);

		/**********************************************/
		/* [4] Update the top of the symbol table ... */
		/**********************************************/
		top = e;
		
		/****************************************/
		/* [5] Enter the new entry to the table */
		/****************************************/
		table[hashValue] = e;
		
		/**************************/
		/* [6] Print Symbol Table */
		/**************************/
		PrintMe();
	}

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public TYPE find(String name)
	{

		SYMBOL_TABLE_ENTRY e = findEntry(name);
		if (e != null) {
			return e.type;
		}
		return null;
	}

	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope()
	{
		/************************************************************************/
		/* Though <SCOPE-BOUNDARY> entries are present inside the symbol table, */
		/* they are not really types. In order to be ablt to debug print them,  */
		/* a special TYPE_FOR_SCOPE_BOUNDARIES was developed for them. This     */
		/* class only contain their type name which is the bottom sign: _|_     */
		/************************************************************************/
		enter(
			"SCOPE-BOUNDARY",
			new TYPE_FOR_SCOPE_BOUNDARIES("NONE"));

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		PrintMe();

		scope_count ++;
	}
	public boolean exists(String name)
	{
		return find(name) != null;
	}
	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */		
		/**************************************************************************/
		while (top != null && !top.name.equals("SCOPE-BOUNDARY")) // Added null check for safety
		{
			// DO NOT MODIFY HASH TABLE LINKS (table[top.index] = top.next;)
			// Only move the top pointer back to logically close the scope
			top_index = top.prevtop_index; // Restore previous top_index if needed (though maybe not necessary if top_index isn't used for lookups)
			top = top.prevtop; 
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */		
		/**************************************/
		if (top != null) { // Ensure top is not null before accessing prevtop
			// DO NOT MODIFY HASH TABLE LINKS (table[top.index] = top.next;)
			top_index = top.prevtop_index; // Restore previous top_index
			top = top.prevtop; 
		}

		/*********************************************/
		/* Print the symbol table after every change */		
		/*********************************************/
		PrintMe();

		scope_count --;
	}
	
	public static int n=0;
	public int getCurrentScopeIndex(){
		return scope_count;
	}
	public void PrintMe()
	{
		int i=0;
		int j=0;
		String dirname="./output/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SYMBOL_TABLE_ENTRY it=table[i];it!=null;it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.getName(),
						it.prevtop_index);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static SYMBOL_TABLE instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SYMBOL_TABLE() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SYMBOL_TABLE getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SYMBOL_TABLE();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TYPE_INT.getInstance());
			instance.enter("string",TYPE_STRING.getInstance());

			/*************************************/
			/* [2] How should we handle void ??? */
			/*************************************/

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			TYPE_LIST list = new TYPE_LIST();
			list.add(new TYPE_VAR_DEC(TYPE_INT.getInstance(),"i"),-1);
			instance.enter(
				"PrintInt",
				new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					"PrintInt",
					list, -1));
			list = new TYPE_LIST();
			list.add(new TYPE_VAR_DEC(TYPE_STRING.getInstance(),"s"),-1);					
			instance.enter(
				"PrintString",
				new TYPE_FUNCTION(
					TYPE_VOID.getInstance(),
					"PrintString",
					list, -1));
			
		}
		return instance;
	}

    public boolean isAtGlobalScope() {
        return getCurrentScopeIndex() == 0;
    }

	public boolean existsInCurrentScope(String name) {
        SYMBOL_TABLE_ENTRY e = top;
        while (e != null && !e.name.equals("SCOPE-BOUNDARY")) {
            if (e.name.equals(name) ) {
               return true;
            }
        e = e.prevtop;
        }
        return false;
    }

	public boolean isInGlobalScope(String name, TYPE recvType){
        SYMBOL_TABLE_ENTRY e = top;
        while (e != null) {
            if (e.name.equals("SCOPE-BOUNDARY")) {
               break; //we got to global -- weeepeeeee
            }
            if (e.name.equals(name) && e.type.equals(recvType)) {
               return true;
            }
            e = e.prevtop;
        }
        return false;
    }

	public TYPE getTypeInGlobalScope(String name) throws SemanticException{
        SYMBOL_TABLE_ENTRY e = top;
        while (e != null) {
            if (e.name.equals("SCOPE-BOUNDARY")) {
               break; //we got to global -- weeepeeeee
            }
            if (e.name.equals(name)) {
               return e.type;
            }
            e = e.prevtop;
        }
        return null;
    }

	// Add a method to find the SYMBOL_TABLE_ENTRY itself, not just the TYPE
	public SYMBOL_TABLE_ENTRY findEntry(String name)
	{
		if(name == null)
		{
			return null;
		}
		SYMBOL_TABLE_ENTRY e;

		for (e = table[hash(name)]; e != null; e = e.next) {
			if (name.equals(e.name)) {
				return e;
			}
		}
		return null;
	}

	public void associateTemp(String name, TEMP temp)
	{
		SYMBOL_TABLE_ENTRY entry = findEntry(name);
		if (entry != null) {
			entry.temp = temp;
		} else {
			// This would be a compiler error - trying to associate TEMP with non-existent var
			System.err.println("ERROR: Could not find symbol table entry for " + name + " to associate TEMP.");
			// Consider throwing an exception
		}
	}
}

package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import java.util.ArrayList;
import java.util.List;
public class AST_VAR_SIMPLE extends AST_VAR
{
	
	public AST_VAR_SIMPLE(String name)
	{
		super(name);	
	}

	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",this.val));
	}
	@Override
	public String toString() {
		return this.val;
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		TYPE t = SYMBOL_TABLE.getInstance().find(val);
		if( t== null)
		{
			throw new SemanticException(lineNumber,String.format("Cannot find var: %s ",this.val));
		}
		return t;
	}

	@Override
	public TEMP IRme()
	{
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntry(this.val);
		TEMP dstTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		if (entry.isGlobal) {
			System.out.println("IR: Loading global variable: " + this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Global(dstTemp, this.val));
			
		} else { // Local variable or parameter
			System.out.printf("IR: Loading local variable '%s' from offset %d\n", this.val, entry.offset);
			if (entry.offset == Integer.MIN_VALUE) {
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set during IRme load.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Load(dstTemp, entry.offset, this.val));
		}
		return dstTemp;
	}
	@Override
	public TEMP storeValueIR(TEMP sourceValue) {
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntry(this.val);
		if (entry == null) {
			throw new RuntimeException("Compiler Error: Symbol table entry not found for variable " + this.val + " during storeValueIR.");
		}

		if (entry.isGlobal) {
			System.out.println("IR: Storing to global variable: " + this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Global_Store(this.val, sourceValue));
		} else {
			System.out.printf("IR: Storing to local variable '%s' at offset %d\n", this.val, entry.offset);
			if (entry.offset == Integer.MIN_VALUE) {
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set during IRme store.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Store(sourceValue, entry.offset, this.val));
		}
		return null; // Store operation doesn't produce a result TEMP
	}

}

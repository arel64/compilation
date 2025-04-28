package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
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
		if (entry == null) {
            throw new RuntimeException("Compiler Error: Symbol table entry not found for variable " + this.val + " during IR generation.");
        }
		if (entry.temp == null) {
            throw new RuntimeException("Compiler Error: TEMP not associated with variable " + this.val + " during IR generation.");
        }
		
		return entry.temp;
	}

	@Override
	public TEMP storeValueIR(TEMP sourceValue) {
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntry(this.val);
		if (entry == null) {
			throw new RuntimeException("Compiler Error: Symbol table entry not found for variable " + this.val + " during storeValueIR.");
		}
		if (entry.temp == null) {
			throw new RuntimeException("Compiler Error: TEMP not associated with variable " + this.val + " during storeValueIR.");
		}
		IR.getInstance().Add_IRcommand(new IRcommand_Store(entry.temp, sourceValue, this.val));
		return null; // Store operation doesn't produce a result TEMP
	}

}

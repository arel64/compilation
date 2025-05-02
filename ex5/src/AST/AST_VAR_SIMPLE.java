package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	boolean isGlobal;
	int offset = Integer.MIN_VALUE;
	public AST_VAR_SIMPLE(String name)
	{
		super(name);	
	}

	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",this.toString()));
	}
	@Override
	public String toString() {
		return String.format("%s(offset:%d, global:%b)", this.val, offset, isGlobal);
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntryInCurrentScopeStack(val);
		
		if (entry == null) {
			System.err.format(">> ERROR [line:%d] variable '%s' is not declared in this scope or outer scopes.\n", this.lineNumber, val);
			throw new SemanticException(this.lineNumber, String.format("Variable '%s' not declared.", val));
		}

		this.isGlobal = entry.isGlobal; 
		this.offset = entry.offset; 
		
		System.out.format("SemantMe AST_VAR_SIMPLE: Retrieved for '%s': offset=%d, isGlobal=%b\n", val, this.offset, this.isGlobal);
		
		return entry.type;
	}

	@Override
	public TEMP IRme()
	{
		TEMP dstTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		System.out.format("IRme AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b for load\n", this.val, this.offset, this.isGlobal);
		if (this.isGlobal) {
			System.out.format("IRme: Using GLOBAL for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Global(dstTemp, this.val));
			
		} else { // Local variable or parameter
			System.out.format("IRme: Using LOCAL offset %d for '%s'\n", this.offset, this.val);
			if (this.offset == Integer.MIN_VALUE) {
				System.err.format("ERROR: IRme called for local '%s' but offset is MIN_VALUE!\n", this.val);
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set during IRme load.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Load(dstTemp, this.offset, this.val));
		}
		return dstTemp;
	}
	@Override
	public TEMP storeValueIR(TEMP sourceValue) {

		System.out.format("storeValueIR AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b for store\n", this.val, this.offset, this.isGlobal);
		if (this.isGlobal) {
			System.out.format("storeValueIR: Using GLOBAL for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Global_Store(this.val, sourceValue));
		} else {
			System.out.format("storeValueIR: Using LOCAL offset %d for '%s'\n", this.offset, this.val);
			if (this.offset == Integer.MIN_VALUE) {
				System.err.format("ERROR: storeValueIR called for local '%s' but offset is MIN_VALUE!\n", this.val);
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set during IRme store.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Store(sourceValue, this.offset, this.val));
		}
		return null; 
	}

}

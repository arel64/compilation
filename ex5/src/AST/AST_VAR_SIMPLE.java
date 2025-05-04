package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import SYMBOL_TABLE.ScopeType;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SIMPLE extends AST_VAR {
	boolean isGlobal;
	int offset = Integer.MIN_VALUE;
	private boolean isClassField = false;

	public AST_VAR_SIMPLE(String name) {
		super(name);
	}

	public void PrintMe() {
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("SIMPLE\nVAR\n(%s)", this.toString()));
	}

	@Override
	public String toString() {
		return String.format("%s(offset:%d, global:%b, field:%b)", this.val, offset, isGlobal, isClassField);
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntryInCurrentScopeStack(val);

		if (entry == null) {
			System.err.format(">> ERROR [line:%d] variable '%s' is not declared in this scope or outer scopes.\n",
					this.lineNumber, val);
			System.out.println("Stack trace: ");
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (int i = 0; i < stackTrace.length; i++) {
				System.out.println("  " + stackTrace[i].toString());
			}
			throw new SemanticException(this.lineNumber, String.format("Variable '%s' not declared.", val));
		}

		this.isGlobal = entry.isGlobal;
		this.offset = entry.offset;

		if (!this.isGlobal && entry.inClassScope) {
			this.isClassField = true;
		} else {
			this.isClassField = false;
		}

		return entry.type;
	}

	@Override
	public TEMP IRme() {
		TEMP dstTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		System.out.format("IRme AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b, isClassField=%b for load\n",
				this.val, this.offset, this.isGlobal, this.isClassField);
		if (this.isGlobal) {
			System.out.format("IRme: Using GLOBAL for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Global(dstTemp, this.val));

		} else { // Non-global: Could be local, parameter, or class field
			if (this.offset == Integer.MIN_VALUE) {
				System.err.format("ERROR: IRme called for non-global '%s' but offset is MIN_VALUE!\n", this.val);
				throw new RuntimeException(
						"Compiler Error: Non-global variable '" + this.val + "' offset not set during IRme load.");
			}

			if (this.isClassField) {
				TEMP tempThis = TEMP_FACTORY.getInstance().getFreshTEMP();
				IR.getInstance().Add_IRcommand(new IRcommand_Load(tempThis, 0, "this"));
				IR.getInstance().Add_IRcommand(new IRcommand_Class_Field_Access(dstTemp, tempThis, this.offset));
			} else {
				IR.getInstance().Add_IRcommand(new IRcommand_Load(dstTemp, this.offset, this.val));
			}
		}
		return dstTemp;
	}

	@Override
	public TEMP storeValueIR(TEMP sourceValue) {

		System.out.format("storeValueIR AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b, isClassField=%b for store\n",
				this.val, this.offset, this.isGlobal, this.isClassField);
		if (this.isGlobal) {
			System.out.format("storeValueIR: Using GLOBAL for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Global_Store(this.val, sourceValue));
		} else {
			if (this.offset == Integer.MIN_VALUE) {
				System.err.format("ERROR: storeValueIR called for non-global '%s' but offset is MIN_VALUE!\n",
						this.val);
				throw new RuntimeException(
						"Compiler Error: Non-global variable '" + this.val + "' offset not set during IRme store.");
			}

			if (this.isClassField) {
				System.out.format("storeValueIR: Using CLASS FIELD offset %d for '%s'\n", this.offset, this.val);
				TEMP tempThis = TEMP_FACTORY.getInstance().getFreshTEMP();
				IR.getInstance().Add_IRcommand(new IRcommand_Load(tempThis, 0, "this"));
				IR.getInstance().Add_IRcommand(new IRcommand_Class_Field_Set(tempThis, this.offset, sourceValue));
			} else {
				System.out.format("storeValueIR: Using LOCAL offset %d for '%s'\n", this.offset, this.val);
				IR.getInstance().Add_IRcommand(new IRcommand_Store(sourceValue, this.offset, this.val));
			}
		}
		return null;
	}

}

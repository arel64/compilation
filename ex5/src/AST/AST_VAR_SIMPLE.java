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
	boolean isParameter;
	boolean isClassField;
	boolean isInClassMethodParameter;
	int offset = Integer.MIN_VALUE;
	private static final int WORD_SIZE = 4; // Define WORD_SIZE if not globally accessible

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
		return String.format("%s(offset:%d, global:%b, param:%b, inClassMthdParam:%b, field:%b)",
				this.val, offset, isGlobal, isParameter, isInClassMethodParameter, isClassField);
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntryInCurrentScopeStack(val);

		if (entry == null) {
			System.err.format(">> ERROR [line:%d] variable '%s' is not declared in this scope or outer scopes.\n",
					this.lineNumber, val);
			throw new SemanticException(this.lineNumber, String.format("Variable '%s' not declared.", val));
		}

		// Store info from symbol table entry
		this.isGlobal = entry.isGlobal;
		this.isParameter = entry.isParameter;
		this.offset = entry.offset;

		// Determine if it's a class field (declared in CLASS scope, not PARAM or LOCAL)
		// This heuristic assumes parameters and locals inside methods are not fields.
		this.isClassField = (!this.isGlobal && !this.isParameter && entry.inClassScope);

		// Determine if it's specifically a parameter within a class method
		this.isInClassMethodParameter = this.isParameter && entry.inClassScope; // Check scope ONLY if it's a parameter

		System.out.format(
				"SemantMe AST_VAR_SIMPLE '%s': isGlobal=%b, isParameter=%b, isClassField=%b, inClassMethodParam=%b, offset=%d, inClassScope=%b\n",
				this.val, this.isGlobal, this.isParameter, this.isClassField, this.isInClassMethodParameter,
				this.offset, entry.inClassScope);

		return entry.type;
	}

	@Override
	public TEMP IRme() {
		TEMP dstTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
		System.out.format(
				"IRme AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b, isParameter=%b, inClassMethodParam=%b, isClassField=%b for load\n",
				this.val, this.offset, this.isGlobal, this.isParameter, this.isInClassMethodParameter,
				this.isClassField);

		if (this.isGlobal) {
			System.out.format("IRme: Generating Load_Global for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Load_Global(dstTemp, this.val));

		} else if (this.isClassField) {
			System.out.format("IRme: Generating Class_Field_Access for '%s' (offset %d)\n", this.val, this.offset);
			TEMP tempThis = TEMP_FACTORY.getInstance().getFreshTEMP();
			// Load 'this' pointer from 0($fp)
			IR.getInstance().Add_IRcommand(new IRcommand_Load(tempThis, 0, "this")); // Assuming offset 0 for 'this'
			IR.getInstance().Add_IRcommand(new IRcommand_Class_Field_Access(dstTemp, tempThis, this.offset));

		} else if (this.isParameter) {
			// Calculate offset based on whether it's a class method parameter (determined
			// in SemantMe)
			int paramMipsOffset = this.offset + (this.isInClassMethodParameter ? WORD_SIZE : 0);

			System.out.format(
					"IRme: Generating Load Parameter for '%s' from %d($fp) (inClassMethodParam=%b, original offset %d)\n",
					this.val, paramMipsOffset, this.isInClassMethodParameter, this.offset);
			IR.getInstance().Add_IRcommand(new IRcommand_Load(dstTemp, paramMipsOffset, this.val));

		} else { // Must be a local variable
			System.out.format("IRme: Generating Load Local for '%s' from %d($fp)\n", this.val, this.offset);
			if (this.offset == Integer.MIN_VALUE) { // Error check
				System.err.format("ERROR: IRme called for local '%s' but offset is MIN_VALUE!\n", this.val);
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Load(dstTemp, this.offset, this.val));
		}

		return dstTemp;
	}

	@Override
	public TEMP storeValueIR(TEMP sourceValue) {
		System.out.format(
				"storeValueIR AST_VAR_SIMPLE '%s': Using offset=%d, isGlobal=%b, isParameter=%b, inClassMethodParam=%b, isClassField=%b for store\n",
				this.val, this.offset, this.isGlobal, this.isParameter, this.isInClassMethodParameter,
				this.isClassField);

		if (this.isGlobal) {
			System.out.format("storeValueIR: Generating Global_Store for '%s'\n", this.val);
			IR.getInstance().Add_IRcommand(new IRcommand_Global_Store(this.val, sourceValue));

		} else if (this.isClassField) {
			System.out.format("storeValueIR: Generating Class_Field_Set for '%s' (offset %d)\n", this.val, this.offset);
			TEMP tempThis = TEMP_FACTORY.getInstance().getFreshTEMP();
			// Load 'this' pointer from 0($fp)
			IR.getInstance().Add_IRcommand(new IRcommand_Load(tempThis, 0, "this")); // Assuming offset 0 for 'this'
			IR.getInstance().Add_IRcommand(new IRcommand_Class_Field_Set(tempThis, this.offset, sourceValue));

		} else if (this.isParameter) {
			// Calculate offset based on whether it's a class method parameter (determined
			// in SemantMe)
			int paramMipsOffset = this.offset + (this.isInClassMethodParameter ? WORD_SIZE : 0);

			System.out.format(
					"storeValueIR: Generating Store Parameter for '%s' to %d($fp) (inClassMethodParam=%b, original offset %d)\n",
					this.val, paramMipsOffset, this.isInClassMethodParameter, this.offset);
			IR.getInstance().Add_IRcommand(new IRcommand_Store(sourceValue, paramMipsOffset, this.val));

		} else { // Must be a local variable
			System.out.format("storeValueIR: Generating Store Local for '%s' to %d($fp)\n", this.val, this.offset);
			if (this.offset == Integer.MIN_VALUE) { // Error check
				System.err.format("ERROR: storeValueIR called for local '%s' but offset is MIN_VALUE!\n", this.val);
				throw new RuntimeException("Compiler Error: Local variable '" + this.val + "' offset not set.");
			}
			IR.getInstance().Add_IRcommand(new IRcommand_Store(sourceValue, this.offset, this.val));
		}

		return null;
	}

}

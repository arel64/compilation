package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;

public class AST_BINOP extends AST_EXP
{
	public enum Operation {
		PLUS('+'),
		MINUS('-'),
		MULTIPLY('*'),
		DIVIDE('/'),
		GT('>'),
		LT('<'),
		EQUALS('=');
	
		private final char symbol;
	
		Operation(char symbol) {
			this.symbol = symbol;
		}
		public static Operation fromSymbol(char symbol) {
			for (Operation op : values()) {
				if (op.symbol == symbol) {
					return op;
				}
			}
			throw new IllegalArgumentException("No enum constant with symbol: " + symbol);
		}
		@Override
		public String toString() {
			return symbol+"";
		}
	}
	public final Operation operation;
	public AST_BINOP(String op)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.operation = Operation.fromSymbol(op.charAt(0));
	}
	
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP\n%s",operation));
	}
	@Override
	public String toString() {
		return operation.toString();
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		return null;
	}
	
	@Override
	public TEMP IRme()
	{
		throw new RuntimeException("AST_BINOP.IRme() should not be called directly.");
	}
}

package AST;
import TYPES.*;
import SYMBOL_TABLE.SemanticException;


public class AST_EXP_BINOP extends AST_EXP
{
	public AST_BINOP binop;
	public AST_EXP left;
	public AST_EXP right;
	
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right, AST_BINOP OP)
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		this.left = left;
		this.right = right;
		this.binop = OP;
	}
	
	public void PrintMe()
	{
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"BINOP\n"+this.toString()
			);
		
		if (left  != null)
		{
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
			left.PrintMe();
		} 
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,binop.SerialNumber);	
		this.binop.PrintMe();
		if (right != null)
		{
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);	
			right.PrintMe();
		}
	}

	@Override
	public String toString() {
		return left.toString()+binop.toString()+right.toString();
	}

	@Override
	public TYPE SemantMe(){
		TYPE leftType = left.SemantMe();
		TYPE rightType = right.SemantMe();
        if (binop.op.equals("+") && leftType == TYPE_STRING && rightType == TYPE_STRING){
			return TYPE_STRING;  // String concatenation
		}
		String operator = binop.toString();
		if (binop.op.equals("<") || binop.op.equals(">") || binop.op.equals("+") || binop.op.equals("-") || binop.op.equals("*"))
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				return TYPE_INT;
			} else {
				throw new SemanticException("Invalid types for binary operator '" + operator + "'");
			}
		if (binop.op.equals("/")) {
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				if (right == 0) { //TODO do we know this? 
					throw new SemanticException("Division by zero");
				}
				return TYPE_INT;
			} else {
				throw new SemanticException("Invalid types for arithmetic operator '" + operator + "'");
			}
		} else {
			throw new SemanticException("Unsupported binary operator '" + operator + "'");
		} 
	}
}
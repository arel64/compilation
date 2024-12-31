package AST;
import TYPES.*;

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

		// if op = + - * 
		//if op is + allow also string 
			// Addition or string concatenation
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				return TYPE_INT; // Integer addition
			} else {
				// throw new SemanticError("Invalid types for '+' operator"); //TODO implement the error 
			}
		if (binop.op.equals("/")) {
			// Arithmetic operations
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				if (right.isZeroConstant()) {
					throw new SemanticError("Division by zero");
				}
				return TYPE_INT;
			} else {
				throw new SemanticError("Invalid types for arithmetic operator '" + operator + "'");
			}
		} else if (binop.op.equals("<") || binop.op.equals(">")) {
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				return TYPE_INT;
			} else {
				throw new SemanticError("Invalid types for comparison operator '" + operator + "'");
			}
		} else {
			throw new SemanticError("Unsupported binary operator '" + operator + "'");
		} 
	}
}
//achinoam change naming 
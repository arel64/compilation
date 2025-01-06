package AST;
import TYPES.*;
import AST.AST_BINOP.Operation;
import SYMBOL_TABLE.SYMBOL_TABLE;
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
	private boolean isSameType()
	{
		return left.equals(right);
	}
	private boolean isDerivedType() throws SemanticException
	{
		TYPE leftType = left.SemantMe();
		TYPE rightType = right.SemantMe();
		if(!(leftType.isClass() && rightType.isClass()))
		{
			return false;
		}
		TYPE_CLASS leftClass = (TYPE_CLASS)leftType;
		TYPE_CLASS rightClass = (TYPE_CLASS)rightType;
		return leftClass.getSharedType(rightClass) != null;
	}
	@Override
	public TYPE SemantMe() throws SemanticException{
		final Operation binopOperation = binop.operation;
		if(binopOperation == Operation.EQUALS)
		{
			if(isSameType())
			{
				return left.SemantMe();
			}
			TYPE leftType = left.SemantMe();
			TYPE rightType = right.SemantMe();
			if(!(leftType.isClass() && rightType.isClass()))
			{
				throw new SemanticException(String.format("Cannot compare between %s and %s different types and are not classes",leftType,rightType));
			}
			TYPE_CLASS leftClass = (TYPE_CLASS)leftType;
			TYPE_CLASS rightClass = (TYPE_CLASS)rightType;
			String sharedClass = leftClass.getSharedType(rightClass);
			if(sharedClass == null)
			{
				throw new SemanticException(String.format("Cannot compare between %s and %s different types and are not derived",leftType,rightType));
			}
			return SYMBOL_TABLE.getInstance().find(sharedClass);
		}
		TYPE leftType = left.SemantMe();
		if (!isSameType())
		{
			throw new SemanticException("Cannot '" + binopOperation + "' for different types");
		}
		
		TYPE varriablesType = leftType;
		if(varriablesType.isVoid())
		{
			throw new SemanticException("Cannot assign to'" + binopOperation + "' where one of the parameters is void");
		}

		if(varriablesType instanceof TYPE_STRING)
		{
			if( binopOperation != Operation.PLUS)
			{
				throw new SemanticException("Not supported '" + binopOperation + "' for strings");
			}
			return TYPE_STRING.getInstance();
		}

		if (!(varriablesType instanceof TYPE_INT)) {
			throw new SemanticException("Invalid types for binary operator '" + binopOperation + "'");
		}

		if (binopOperation == Operation.DIVIDE ) {
			if (right instanceof AST_LIT_NUMBER && Integer.parseInt(((AST_LIT_NUMBER)right).getValue()) == 0) {
				throw new SemanticException("Division by zero");
			}
		}
		return varriablesType;
	}
}
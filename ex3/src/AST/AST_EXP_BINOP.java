package AST;
import TYPES.*;
import AST.AST_BINOP.Operation;
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
	public TYPE SemantMe() throws SemanticException{
		final Operation binopOperation = binop.operation;
		TYPE leftType = left.SemantMe();
		TYPE rightType = right.SemantMe();
		if(binopOperation == Operation.EQUALS)
		{
			
			if(leftType.equals(rightType))
			{
				return TYPE_INT.getInstance();
			}
			
			if(!(leftType.isClass() && (rightType.isClass() || rightType instanceof TYPE_NIL)))
			{
				throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s different types and are not classes",leftType,rightType));
			}
			TYPE_CLASS leftClass = (TYPE_CLASS)leftType;
			if(rightType instanceof TYPE_NIL)
			{
				return TYPE_INT.getInstance();
			}
			TYPE_CLASS rightClass = (TYPE_CLASS)rightType;
			if(!rightClass.isDerivedFrom(leftClass))
			{
				throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s different types and are not derived",leftType,rightType));
			}
			return TYPE_INT.getInstance();
		}
		if (!leftType.equals(rightType))
		{
			throw new SemanticException(lineNumber,"Cannot '" + binopOperation + "' for different types");
		}
		
		TYPE varriablesType = leftType;
		if(varriablesType.isVoid())
		{
			throw new SemanticException(lineNumber,"Cannot assign to'" + binopOperation + "' where one of the parameters is void");
		}

		if(varriablesType instanceof TYPE_STRING)
		{
			if( binopOperation != Operation.PLUS)
			{
				throw new SemanticException(lineNumber,"Not supported '" + binopOperation + "' for strings");
			}
			return TYPE_STRING.getInstance();
		}

		if (!(varriablesType instanceof TYPE_INT)) {
			throw new SemanticException(lineNumber,"Invalid types for binary operator '" + binopOperation + "'");
		}

		if (binopOperation == Operation.DIVIDE ) {
			if (right instanceof AST_LIT_NUMBER && Integer.parseInt(((AST_LIT_NUMBER)right).getValue()) == 0) {
				throw new SemanticException(lineNumber,"Division by zero");
			}
		}
		return varriablesType;
	}
}
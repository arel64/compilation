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
		if(leftType.isFunction() || rightType.isFunction())
		{
			throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s function",leftType,rightType)); 
		}
		if(binopOperation == Operation.EQUALS)
		{
			if((rightType instanceof TYPE_NIL && (leftType.isArray() || leftType.isClass())) || ((leftType instanceof TYPE_NIL) && (rightType.isArray() || rightType.isClass())))
			{
				return TYPE_INT.getInstance();
			}
			if(leftType.isArray() || rightType.isArray())
			{
				throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s arrays",leftType,rightType)); 
			}
			if((leftType.isClass() && (!rightType.isClass()))||(!leftType.isClass() && (rightType.isClass())))
			{
				throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s class and non class",leftType,rightType));
			}
			else if(leftType.isClass())
			{
				if(((TYPE_CLASS)leftType).getSharedType((TYPE_CLASS)rightType) == null)
				{
					throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s classes without shared type",leftType,rightType));
				}
				return TYPE_INT.getInstance();
			}

			TYPE primitiveTypeLeft = (leftType instanceof TYPE_VAR_DEC) ?((TYPE_VAR_DEC)leftType).t : leftType;
			TYPE primitiveTypeRight = (rightType instanceof TYPE_VAR_DEC) ?((TYPE_VAR_DEC)rightType).t : rightType;
			if(primitiveTypeLeft.equals(primitiveTypeRight))
			{
				return TYPE_INT.getInstance();
			}
			
			throw new SemanticException(lineNumber,String.format("Cannot compare between %s and %s",leftType,rightType));
			
		}
		
		
		
		if(leftType.isVoid() || rightType.isVoid())
		{
			throw new SemanticException(lineNumber,"Cannot assign to'" + binopOperation + "' where one of the parameters is void");
		}
		if(leftType.isArray() || leftType.isClass() || rightType.isArray() || rightType.isClass() )
		{
			throw new SemanticException(lineNumber,String.format("Cannot %s between %s and %s",binopOperation,leftType,rightType));
		}

		TYPE primitiveTypeLeft = (leftType instanceof TYPE_VAR_DEC) ?((TYPE_VAR_DEC)leftType).t : leftType;
		TYPE primitiveTypeRight = (rightType instanceof TYPE_VAR_DEC) ?((TYPE_VAR_DEC)rightType).t : rightType;
		if(!primitiveTypeLeft.equals(primitiveTypeRight))
		{
			throw new SemanticException(lineNumber,String.format("Cannot %s for different primitive types %s %s",binopOperation,leftType,rightType));
		}
		if(primitiveTypeLeft instanceof TYPE_STRING)
		{
			if( binopOperation != Operation.PLUS)
			{
				throw new SemanticException(lineNumber,"Not supported '" + binopOperation + "' for strings");
			}
			return TYPE_STRING.getInstance();
		}
		if (binopOperation == Operation.DIVIDE ) {
			if (right instanceof AST_LIT_NUMBER && Integer.parseInt(((AST_LIT_NUMBER)right).getValue()) == 0) {
				throw new SemanticException(lineNumber,"Division by zero");
			}
		}
		return primitiveTypeLeft;
	}
}
package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_STMT_ASSIGN extends AST_STMT
{

	public AST_VAR var;
	public AST_EXP exp;


	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP exp)
	{

		SerialNumber = AST_Node_Serial_Number.getFresh();


		this.var = var;
		this.exp = exp;
	}

	public void PrintMe()
	{

		if (var != null) var.PrintMe();
		if (exp != null) exp.PrintMe();

		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"STMT ASSIGN\n"+var+" := "+ exp+"\n");
		
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	@Override
	public TYPE SemantMe() throws SemanticException {
		TYPE expType = exp.SemantMe();
		TYPE varType = var.SemantMe();
		if(expType.isClass() && varType.isClass())
		{
			TYPE_CLASS classExpr = (TYPE_CLASS)expType;
			TYPE_CLASS classVar = (TYPE_CLASS)varType;
			if(!classExpr.isDerivedFrom(classVar))
			{
				throw new SemanticException(lineNumber,String.format("Cannot assign class types %s is not derived from %s",classVar,classExpr));
			}
			String sharedType = classVar.getSharedType(classExpr);
			return SYMBOL_TABLE.getInstance().find(sharedType);
		}
		if(expType instanceof TYPE_NIL)
		{
			//TODO:: NIL HANDELING
		}
		if(expType != varType)
		{
			throw new SemanticException(lineNumber,String.format("Cannot assign incompatible types %s %s",expType,varType));
		}
		return varType;
	}
}

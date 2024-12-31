package AST;
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
}

package AST;

public class AST_VAR_FIELD extends AST_VAR
{
	public String fieldName;	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		super(var.val);
		SerialNumber = AST_Node_Serial_Number.getFresh();
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		String var = this.val;
		System.out.print("AST NODE FIELD VAR\n");
		System.out.format("FIELD NAME( %s )\n",fieldName);
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,SerialNumber);
	}
}

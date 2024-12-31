package AST;
import TYPES.*;

public class AST_VAR_FIELD extends AST_VAR
{
	public String fieldName;	
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		super(var.val);
		this.fieldName = fieldName;
	}

	public void PrintMe()
	{
		String var = this.val;
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR FIELD\n %s",toString()));
	}
	@Override
	public String toString() {
		return String.format("%s.%s",this.val,fieldName);
	}
}

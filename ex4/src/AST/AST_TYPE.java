package AST;
import TYPES.*;
import SYMBOL_TABLE.*;

public class AST_TYPE extends AST_Node
{
	public String type;
	public AST_TYPE(String currType)
	{
		this.type = currType;
	}
	@Override
	public String toString() {
		return type;
	}
	@Override
	public TYPE SemantMe() throws SemanticException{
		if (type == "int")
			return TYPE_INT.getInstance();
		if (type == "String")
			return TYPE_STRING.getInstance();
		if (type == "void")
			return TYPE_VOID.getInstance();
			
		TYPE curr = SYMBOL_TABLE.getInstance().find(type);
        if (curr == null)
        {
           throw new SemanticException(lineNumber,"The type does not exist."+ toString() );
        }
        return curr;
	}
}

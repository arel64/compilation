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
	public TYPE SemantMe(){
		if (type == "int")
			return TYPE_INT.getInstance();
		if (type == "String")
			return TYPE_STRING.getInstance();
		if (type == "void")
			return TYPE_VOID.getInstance();
		//if we got to here, type is id:
		TYPE curr = SYMBOL_TABLE.getInstance().find(type);
        if (curr == null)
        {
           System.out.println("The type does not exist.");
        //    throw new SemanticErrorException("" + lineNumber);
        }
        return curr;
	}
}

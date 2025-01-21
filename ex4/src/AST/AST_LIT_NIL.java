package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
public class AST_LIT_NIL extends AST_LIT
{
    
    public AST_LIT_NIL(){
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override 
    public String getValue() {
        return "nil";
    }

    @Override
    public TYPE_NIL SemantMe(){
        return TYPE_NIL.getInstance();
    }

}

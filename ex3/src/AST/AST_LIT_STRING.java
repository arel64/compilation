package AST;
import TYPES.*;
public class AST_LIT_STRING extends AST_LIT
{
    public String val;
    
    public AST_LIT_STRING(String val){
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.val = val;
    }

    @Override 
    public String getValue() {
        return val;
    }

    @Override
    public TYPE_STRING SemantMe(){
        return TYPE_STRING.getInstance();
    }
   
}

package AST;
import TYPES.*;
public class AST_LIT_NUMBER extends AST_LIT
{
    public int val;
    
    public AST_LIT_NUMBER(boolean isNeg, int val){
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.val = val;
        if (isNeg){
            this.val = -1 * val;
        }
    }

    @Override 
    public String getValue() {
        return Integer.toString(this.val);
    }

    @Override
    public TYPE_INT SemantMe(){
        return TYPE_INT.getInstance();
    }
}

package AST;

public class AST_LIT_NUMBER extends AST_LIT
{
    public int val;
    
    public AST_LIT_NUMBER(String isNeg, int val){
        if (isNeg != null || isNeg != ""){
            this.val = -1 * val;
        } else {
            this.val = val;
        }
    }

    @Override 
    public String getValue() {
        return Interger.toString(this.val);
    }

   
}

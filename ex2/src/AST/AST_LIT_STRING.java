package AST;

public class AST_LIT_STRING extends AST_LIT
{
    public String val;
    
    public AST_LIT_STRING(String val){
        this.val = val;
    }

    @Override 
    public String getValue() {
        reuturn val;
    }

   
}

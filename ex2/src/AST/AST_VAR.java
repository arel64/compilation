package AST;

public class AST_VAR extends AST_Node
{
    public String val;
    public AST_VAR(String val){
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.val = val;
    }
    @Override
    public String toString() {
        return val;
    }
}
package AST;
public class AST_STMT_ASSIGN_NEW extends AST_STMT {
    public AST_VAR var;
    public AST_NEW_EXP newExp;

    public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEW_EXP newExp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.var = var;
        this.newExp = newExp;
    }
    @Override
    public void PrintMe() {
        newExp.PrintMe();
    }
}
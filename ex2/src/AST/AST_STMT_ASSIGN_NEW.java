public class AST_STMT_ASSIGN_NEW extends AST_STMT {
    public AST_VAR var;
    public AST_NEW_EXP newExp;

    public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEW_EXP newExp) {
        this.var = var;
        this.newExp = newExp;
    }

    @Override
    public void printMe() {
        System.out.printf("ASSIGN NEW: %s = ", var);
        newExp.printMe();
    }
}
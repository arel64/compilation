public class AST_STMT_RETURN extends AST_STMT {
    public AST_EXP exp;

    public AST_STMT_RETURN(AST_EXP exp) {
        this.exp = exp;
    }

    @Override
    public void printMe() {
        if (exp != null) {
            System.out.print("RETURN: ");
            exp.printMe();
        } else {
            System.out.println("RETURN;");
        }
    }
}

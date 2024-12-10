package AST;

public class AST_STMT_FUNC_INVO extends AST_STMT {
    public AST_VAR var;
    public String name;
    public AST_EXP_LIST args;

    public AST_STMT_FUNC_INVO(AST_VAR var, String name, AST_EXP_LIST args) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.var = var;
        this.name = name;
        this.args = args;
    }
    public AST_STMT_FUNC_INVO(String name, AST_EXP_LIST args) {

        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.name = name;
        this.args = args;
    }

    @Override
    public void PrintMe() {
        if (var != null) var.PrintMe();
        if (args != null) args.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("STMT_FUNC_INVO(%s)", name)
        );
        if (var != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
            var.PrintMe();
        }
        if (args != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, args.SerialNumber);
            args.PrintMe();
        }
    }
}

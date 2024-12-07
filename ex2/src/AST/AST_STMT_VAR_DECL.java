package AST;

public class AST_STMT_VAR_DECL extends AST_STMT {
    public AST_VAR_DEC varDec;

    public AST_STMT_VAR_DECL(AST_VAR_DEC varDec) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.varDec = varDec;
    }

    @Override
    public void PrintMe() {
        if (varDec != null) varDec.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "STMT_VAR_DECL"
        );
        if (varDec != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, varDec.SerialNumber);
        }
    }
}

package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_STMT_FUNC_INVO extends AST_STMT {
    public AST_FUNC_INVO invocation;

    public AST_STMT_FUNC_INVO(AST_FUNC_INVO invocation) {
        SerialNumber = invocation.SerialNumber;
        this.invocation = invocation;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("STMT_FUNC_INVO\n %s",invocation)
        );
        invocation.PrintMe();
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        return invocation.SemantMe();
    }
}

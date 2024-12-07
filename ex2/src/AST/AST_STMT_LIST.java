package AST;

public class AST_STMT_LIST extends AST_LIST<AST_STMT>{

    public AST_STMT_LIST(AST_STMT first) {
        super(first);
    }

    public AST_STMT_LIST(AST_LIST<AST_STMT> prev, AST_STMT next) {
        super(prev,next);
    }
}

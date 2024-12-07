package AST;
public class AST_PARAM_LIST extends AST_LIST<AST_PARAM>{

    public AST_PARAM_LIST(AST_PARAM first) {
        super(first);
    }

    public AST_PARAM_LIST(AST_LIST<AST_PARAM> prev, AST_PARAM next) {
        super(prev,next);
    }
}

package AST;
public class AST_FIELD_LIST extends AST_LIST<AST_FIELD>{

    public AST_FIELD_LIST(AST_FIELD first) {
        super(first);
    }

    public AST_FIELD_LIST(AST_LIST<AST_FIELD> prev, AST_FIELD next) {
        super(prev,next);
    }
}

package AST;
public class AST_EXP_LIST extends AST_LIST<AST_EXP>{

    public AST_EXP_LIST(AST_EXP first) {
        super(first);
    }

    public AST_EXP_LIST(AST_LIST<AST_EXP> prev, AST_EXP next ) {
        super(prev,next);
    }
}

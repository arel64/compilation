package AST;
public class AST_EXP_LIST extends AST_LIST<AST_EXP>{

    public AST_EXP_LIST(AST_EXP first) {
        super(first,AST_EXP.class);
    }

    public AST_EXP_LIST(AST_LIST<AST_EXP> prev, AST_EXP next ) {
        super(prev,next,AST_EXP.class);
    }
}

package AST;
public class AST_DEC_LIST extends AST_LIST<AST_DEC>{

    public AST_DEC_LIST(AST_DEC first) {
        super(first,AST_DEC.class);
    }

    public AST_DEC_LIST(AST_LIST<AST_DEC> prev, AST_DEC next ) {
        super(prev,next,AST_DEC.class);
    }

}

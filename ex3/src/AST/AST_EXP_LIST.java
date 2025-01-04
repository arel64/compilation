package AST;
import TYPES.*;

public class AST_EXP_LIST extends AST_LIST<AST_EXP>{

    public AST_EXP_LIST(AST_EXP first) {
        super(first,AST_EXP.class);
    }

    public AST_EXP_LIST(AST_LIST<AST_EXP> prev, AST_EXP next ) {
        super(prev,next,AST_EXP.class);
    }

    // @Override
    // public TYPE SemantMe(){
    //     //loop through all the e that we have in the list and check if there type is equal to what we want in the list
    // }
}
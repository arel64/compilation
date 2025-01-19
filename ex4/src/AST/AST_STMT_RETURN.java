package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_STMT_RETURN extends AST_STMT {
    public AST_EXP exp;

    public AST_STMT_RETURN(AST_EXP exp) {
        this.exp = exp;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "RETURN "+exp    
        );
        if(exp != null)
        {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
            exp.PrintMe();

        }

        
    }
    
    @Override
    public TYPE SemantMe() throws SemanticException {
        //TOOD:: Add scope validation that I am in a funcction / A statement conditinal
        TYPE t = TYPE_VOID.getInstance();
        if(exp != null)
        {
            t = exp.SemantMe();
        }
        
        return new TYPE_RETURN(t);
        
    }
}

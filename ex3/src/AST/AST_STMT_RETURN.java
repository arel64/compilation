package AST;
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
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
        exp.PrintMe();
        
    }
}

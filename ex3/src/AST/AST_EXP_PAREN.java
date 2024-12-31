package AST;
import TYPES.*;

public class AST_EXP_PAREN extends AST_EXP {
    public AST_EXP exp;

    public AST_EXP_PAREN(AST_EXP exp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.exp = exp;
    }

    @Override
    public void PrintMe() {
        if (exp != null) exp.PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "EXP_PAREN"
        );
        if (exp != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
            exp.PrintMe();
        }
    }

    @Override
	public TYPE SemantMe(){
		return exp.SemantMe();
	}

}

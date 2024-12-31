package AST;
import TYPES.*;

public class AST_STMT_VAR_DECL extends AST_STMT {
    public AST_VAR_DEC varDec;

    public AST_STMT_VAR_DECL(AST_VAR_DEC varDec) {
        this.SerialNumber = varDec.SerialNumber;
        this.varDec = varDec;
    }

    @Override
    public void PrintMe() {
        varDec.PrintMe();
    }
}

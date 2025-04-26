package AST;
import SYMBOL_TABLE.SemanticException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.*;
import TEMP.*;
import IR.*;
import MIPS.MIPSGenerator;

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

    @Override
    public TYPE SemantMe() throws SemanticException {
        return varDec.SemantMe();
    }

    @Override
    public TEMP IRme()
	{
        return varDec.IRme();
	}
}

package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
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

    public TEMP IRme()
	{
	
		return null;
	}
}

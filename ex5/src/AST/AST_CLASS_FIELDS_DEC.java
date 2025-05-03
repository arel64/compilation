package AST;

import IR.IR;
import IR.IRcommand_Load;
import SYMBOL_TABLE.SemanticException;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;

public class AST_CLASS_FIELDS_DEC extends AST_VAR_DEC {

    public AST_CLASS_FIELDS_DEC(AST_VAR_DEC v) {
        super(v.getName(), v.t, v.varValue);
        this.offset = v.offset;
    }

    public AST_CLASS_FIELDS_DEC(String name, AST_TYPE t) {
        super(name, t);
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        TYPE myType = super.SemantMe();
        if (varValue != null && !(this.varValue instanceof AST_LIT)) {
            throw new SemanticException(lineNumber,
                    String.format("cannot initialize field %s with non constant expr", getName()));
        }
        return myType;
    }

    @Override
    public TEMP IRme() {
        TEMP temp = TEMP_FACTORY.getInstance().getFreshTEMP();
        TEMP initialValue = varValue.IRme();
        IR.getInstance().Add_IRcommand(new IRcommand_Load(temp, initialValue, getName()));
        return temp;
    }
}

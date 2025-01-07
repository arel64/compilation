package AST;

import SYMBOL_TABLE.SemanticException;
import TYPES.TYPE;
import TYPES.TYPE_CLASS_VAR_DEC;

public abstract class AST_CLASS_FIELDS_DEC extends AST_DEC {
    public AST_CLASS_FIELDS_DEC(String name)
    {
        super(name);
    }
}

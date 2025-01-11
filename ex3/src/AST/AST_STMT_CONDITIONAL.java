package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.TYPE;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;

public abstract class AST_STMT_CONDITIONAL extends AST_STMT
{
    private AST_EXP condition;
    private AST_LIST<? extends AST_STMT> body;
    public AST_STMT_CONDITIONAL(AST_EXP condition, AST_LIST<AST_STMT> body)
    {
        this.body = body;
        this.condition = condition;
    }
    public AST_LIST<? extends AST_STMT> getBody()
    {
        return body;
    }
    public AST_EXP getCondition()
    {
        return condition;
    }
    

    @Override
    public TYPE_LIST SemantMe() throws SemanticException {
        TYPE conditionType = getCondition().SemantMe();
        if(!(conditionType.isAssignable(TYPE_INT.getInstance()) || conditionType.equals(TYPE_INT.getInstance())))
        {
            throw new SemanticException(lineNumber,String.format("Conditional statement condition must be int, got: %s ",conditionType));
        }
        SYMBOL_TABLE.getInstance().beginScope();
        TYPE_LIST t = getBody().SemantMe();
        SYMBOL_TABLE.getInstance().endScope();
        return t;
    }

}

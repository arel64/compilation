package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_STMT_ASSIGN_NEW extends AST_STMT {
    public AST_VAR var;
    public AST_NEW_EXP newExp;

    public AST_STMT_ASSIGN_NEW(AST_VAR var, AST_NEW_EXP newExp) {
        SerialNumber = newExp.SerialNumber;
        this.var = var;
        this.newExp = newExp;
    }
    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("%s := %s", this.var,this.newExp));
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
        var.PrintMe();

    }
    @Override
    public TYPE SemantMe() throws SemanticException {
        TYPE newExpType = newExp.SemantMe();
        TYPE varType = var.SemantMe();
        if(!newExpType.isAssignable(varType))
        {
            throw new SemanticException(lineNumber,String.format("%s and %s are part of a new statement but of different types", newExpType,varType));
        }
        return varType;
    }
}
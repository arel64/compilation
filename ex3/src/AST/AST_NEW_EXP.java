package AST;
import TYPES.*;

public class AST_NEW_EXP extends AST_EXP {
    public AST_TYPE type;
    public AST_EXP currExp;
    public trueNumber;

    public AST_NEW_EXP(AST_TYPE currType, AST_EXP currExp, int trueNumber) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = currType;
        this.currExp = currExp;
        this.trueNumber = trueNumber;
    }

    public AST_NEW_EXP(AST_TYPE currType) {
        this(currType, null, 2);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            String.format("%s",toString())
        );
    }
    @Override
    public String toString() {
        String expString = currExp == null ? "" : String.format("[%s]", currExp);
        return String.format("new %s %s", type,expString);
    }

    @Override
    public TYPE SemantMe(){
        TYPE expType = currExp.SemantMe();
        if (expType != TYPE_INT || type.SemantMe() == null){
            throw new SemanticException("");
        }
        if (trueNumber == 0){
            int number = (AST_LIT_NUMBER)currExp.getValue();
            if (number <= 0)
               throw new SemanticException("LEN<=0 for array length");
        }
        if (trueNumber == 1){
            return new TYPE_ARRAY(type.SemantMe());//maybe also the name? 
        }
        return type.SemantMe();
    }
}

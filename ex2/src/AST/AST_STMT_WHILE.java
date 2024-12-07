package AST;
import java.util.List;

public class AST_STMT_WHILE extends AST_STMT {
    public AST_EXP condition;
    public AST_STMT_LIST body;

    public AST_STMT_WHILE(AST_EXP condition, AST_STMT_LIST body) {
        this.condition = condition;
        this.body = body;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    
    @Override
    public void PrintMe() {

        if (condition != null) {
            condition.PrintMe();
        } else {

        }
        

        condition.PrintMe();
        for (AST_STMT field : body.list) {
            field.PrintMe();
        }
        
    }
}

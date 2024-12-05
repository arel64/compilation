package AST;
import java.util.List;

public class AST_STMT_WHILE extends AST_STMT {
    public AST_EXP condition;
    public List<AST_STMT> body;

    public AST_STMT_WHILE(AST_EXP condition, List<AST_STMT> body) {
        this.condition = condition;
        this.body = body;
    }

    
    @Override
    public void printMe() {
        System.out.println("WHILE LOOP:");
        System.out.print("Condition: ");
        if (condition != null) {
            condition.printMe();
        } else {
            System.out.println("null");
        }

        System.out.println("Body:");
        for (AST_STMT stmt : body) {
            if (stmt != null) {
                stmt.printMe();
            }
        }
    }
}

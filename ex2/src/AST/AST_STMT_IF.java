package AST;
import java.util.List;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP cond;
	public List<AST_STMT> body;
	public AST_STMT_IF(AST_EXP condition, List<AST_STMT> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void printMe() {
        System.out.print("IF: ");
        condition.printMe();
        for (AST_STMT stmt : body) {
            stmt.printMe();
        }
    }
}
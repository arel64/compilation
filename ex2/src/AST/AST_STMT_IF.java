package AST;
import java.util.List;

public class AST_STMT_IF extends AST_STMT
{
	public AST_EXP condition;
	public List<AST_STMT> body;
	public AST_STMT_IF(AST_EXP condition, List<AST_STMT> body) {
        this.condition = condition;
        this.body = body;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        System.out.print("IF: ");
        condition.PrintMe();
        for (AST_STMT stmt : body) {
            stmt.PrintMe();
        }
    }
}
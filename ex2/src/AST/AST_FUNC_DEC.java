package AST;

import java.util.List;

public class AST_FUNC_DEC extends AST_DEC {
    public String funcName;
    public AST_TYPE returnType;
    public List<AST_PARAM> params;
    public List<AST_STMT> body;

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, List<AST_PARAM> params, List<AST_STMT> body) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.params = params;
        this.body = body;
    }

    @Override
    public void printMe() {
        System.out.printf("FUNCTION DECLARATION: %s %s(", returnType, funcName);
        if (params != null) {
            for (AST_PARAM param : params) {
                param.printMe();
            }
        }
        System.out.println(")");
        for (AST_STMT stmt : body) {
            stmt.printMe();
        }
    }
}

package AST;

import java.util.List;

public class AST_FUNC_INVO extends AST_NODE {
    public String funcName;
    public List<AST_PARAM> params;
    public AST_VAR var;
    public AST_FUNC_INVO(String funcName, List<AST_PARAM> params, AST_VAR var) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
    }

    public AST_FUNC_INVO(String funcName, List<AST_PARAM> params) {
        this.funcName = funcName;
        this.params = params;
        this.var = null;
    }

    @Override
    public void printMe() {
        if (var != null){
            var.printMe();
        }
        System.out.printf("FUNCTION INVOCATION: %s(",funcName);
        if (params != null) {
            for (AST_PARAM param : params) {
                param.printMe();
            }
        }
        System.out.println(")");
    }
}

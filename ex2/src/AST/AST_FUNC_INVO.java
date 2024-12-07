package AST;


public class AST_FUNC_INVO extends AST_EXP {
    public String funcName;
    public AST_EXP_LIST params;
    public AST_VAR var;
    public AST_FUNC_INVO(String funcName, AST_EXP_LIST params, AST_VAR var) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_FUNC_INVO(String funcName, AST_EXP_LIST params) {
        this.funcName = funcName;
        this.params = params;
        this.var = null;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        if (var != null){
            var.PrintMe();
        }
        if (params != null) {
            for (AST_EXP param : params.list) {
                param.PrintMe();
            }
        }
    }
}

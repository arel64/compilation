package AST;


public class AST_FUNC_INVO extends AST_Node {
    public String funcName;
    public AST_PARAM_LIST params;
    public AST_VAR var;
    public AST_FUNC_INVO(String funcName, AST_PARAM_LIST params, AST_VAR var) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_FUNC_INVO(String funcName, AST_PARAM_LIST params) {
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
        System.out.printf("FUNCTION INVOCATION: %s(",funcName);
        if (params != null) {
            for (AST_PARAM param : params.list) {
                param.PrintMe();
            }
        }
        System.out.println(")");
    }
}

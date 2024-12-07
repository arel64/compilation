package AST;

public class AST_FUNC_DEC extends AST_DEC {
    public String funcName;
    public AST_TYPE returnType;
    public AST_PARAM_LIST params;
    public AST_STMT_LIST body;

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, AST_PARAM_LIST params, AST_STMT_LIST body) {
        this.funcName = funcName;
        this.returnType = returnType;
        this.params = params;
        this.body = body;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        System.out.printf("FUNCTION DECLARATION: %s %s(", returnType, funcName);
        if (params != null) {
            for (AST_PARAM param : params.list) {
                param.printMe();
            }
        }
        System.out.println(")");
        body.head.PrintMe();
    }
}

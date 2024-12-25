package AST;

public class AST_FUNC_DEC extends AST_DEC {
    
    public AST_TYPE returnType;
    public AST_DEC_LIST params;
    public AST_STMT_LIST body;

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, AST_DEC_LIST params, AST_STMT_LIST body) {
        super(funcName);
        this.returnType = returnType;
        this.params = params;
        this.body = body;
        
    }

    @Override
    public void PrintMe() {        
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"FUNC_DECLARATION\n "+returnType.type+" "+this.getName()+"("+params+")");
		
        if (params != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);
            params.PrintMe();
        }
        if(body != null)
        {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
            body.PrintMe();
        }
    }
}

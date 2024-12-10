package AST;

public class AST_VAR_DEC extends AST_DEC {
    
    public AST_TYPE varType;
    public AST_EXP varValue;

    public AST_VAR_DEC(String varName, AST_TYPE varType, AST_EXP initialValue) {
        super(varName);
        this.varType = varType;
        this.varValue = initialValue;
    
    }
    public AST_VAR_DEC(String varName, AST_TYPE varType) {
        this(varName,varType,null);
    }

    @Override
    public void PrintMe() {
        super.PrintMe();
        if (this.varValue != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varValue.SerialNumber); 
            varValue.PrintMe();
        } 
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varType.SerialNumber); 
        varType.PrintMe();
    }
}
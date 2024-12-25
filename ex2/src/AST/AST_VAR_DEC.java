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
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"AST_DEC\n"+ this.toString());	
    }
    @Override
    public String toString() {
        return varType.toString()+ " "+getName() + (varValue != null ? "="+varValue:"");
    }
}
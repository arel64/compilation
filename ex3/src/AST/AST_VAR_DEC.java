package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

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
    @Override
    public TYPE SemantMe() throws SemanticException{
        TYPE type = varType.SemantMe();
        TYPE valueType = varValue.SemantMe();
        if(type != valueType)
        {
            throw new SemanticException("VAR DEC MISMATCH TYPE");
        }
        return null;
        // return new TYPE_CLASS_VAR_DEC(valueType, getName());  //this is not only used for class 
    }
}
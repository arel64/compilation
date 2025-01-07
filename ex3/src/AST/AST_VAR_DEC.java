package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_VAR_DEC extends AST_CLASS_FIELDS_DEC {
    
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
			"AST_DEC "+ this.toString());	
    }
    @Override
    public String toString() {
        return varType.toString()+ " "+getName() + (varValue != null ? "="+varValue:"");
    }
    @Override
    public TYPE_CLASS_VAR_DEC SemantMe() throws SemanticException{
        TYPE type = varType.SemantMeLog();
        if(varValue == null)
        {
            return new TYPE_CLASS_VAR_DEC(type,this.getName(),lineNumber);
        }
        TYPE valueType = varValue.SemantMeLog();
        if(type != valueType)
        {
            throw new SemanticException(lineNumber,"VAR DEC MISMATCH TYPE");
        }
        SYMBOL_TABLE.getInstance().enter(getName(), valueType);
        return new TYPE_CLASS_VAR_DEC(valueType, getName(), lineNumber);
    }
}
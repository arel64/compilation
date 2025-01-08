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
    public TYPE SemantMe() throws SemanticException{
        TYPE type = varType.SemantMeLog();
        SYMBOL_TABLE.getInstance().enter(getName(), type);
        if( varValue == null)
        {
            return type;    
        }
        TYPE valueType = varValue.SemantMeLog();
        System.out.printf("comparing %s %s \n" ,valueType,type);
        if(type.isArray())
        {
            TYPE_ARRAY ltype = (TYPE_ARRAY)type;
            if(!ltype.t.equals(valueType))
            {
                throw new SemanticException(lineNumber,String.format("Cannot allocate array %s with mismatch type %s", ltype,valueType));    
            }
            return ltype;
        }
        if(!type.isAssignable(valueType))
        {
            throw new SemanticException(lineNumber,String.format("Initial value %s does not match type %s", valueType,type));
        }
        return type;
    }
}
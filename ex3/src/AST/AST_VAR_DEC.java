package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;

public class AST_VAR_DEC extends AST_DEC {
    
    public AST_TYPE t;
    public AST_EXP varValue;
    public String varName;

    public AST_VAR_DEC(String varName, AST_TYPE varType, AST_EXP initialValue) {
        super(varName);
        this.varName = varName;
        this.t = varType;
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
        return t.toString()+ " "+getName() + (varValue != null ? "="+varValue:"");
    }

    @Override
    public TYPE SemantMe() throws SemanticException{
        if(isDeclaredInCurrentScope())
        {
            throw new SemanticException(lineNumber, String.format("Cannot declare %s was already declared in this scope", getName()));
        }
        TYPE type = t.SemantMeLog();
        if(type.isPrimitive())
        {
            type = new TYPE_VAR_DEC(type,getName());
        }
        SYMBOL_TABLE.getInstance().enter(getName(), type);
        if( varValue != null)
        {
            TYPE valueType = varValue.SemantMeLog();
            if(!type.isAssignable(valueType))
            {
                throw new SemanticException(lineNumber,String.format("Initial value %s does not match type %s", valueType,type));
            }
        }
        return type;
    }
}
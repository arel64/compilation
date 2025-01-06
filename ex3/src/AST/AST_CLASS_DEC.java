package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
public class AST_CLASS_DEC extends AST_DEC {
      
    public String parentClassName;
    public AST_LIST<AST_CLASS_FIELDS_DEC> fields; 

    public AST_CLASS_DEC(String className, String parentClass, AST_LIST<AST_CLASS_FIELDS_DEC> fields) {
        super(className);
        this.parentClassName = parentClass;
        this.fields = fields;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            toString()
        );     
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,fields.SerialNumber);
        fields.PrintMe();
    }
    @Override
    public String toString() {
        String represtation = String.format("class %s",super.getName());
        if (parentClassName != null)
        {
            represtation += " extends " + parentClassName;
        }
        return represtation;
    }
    private boolean isParentClassValid() throws SemanticException
    {
        if (parentClassName == null)
        {
            return true;
        }      
        SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
        TYPE parent = symbol_table.getTypeInGlobalScope(parentClassName);
        return parent != null;
        
		
    }
    @Override
	public TYPE SemantMe() throws SemanticException{
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();

		if (!symbol_table.isAtGlobalScope()){
			throw new SemanticException("Scope mismatch found scope:" +symbol_table.getCurrentScopeIndex());
		}
		if(!isParentClassValid())
        {
            throw new SemanticException("Specified extends class "+ parentClassName+" not found");
        }
		TYPE_CLASS currentClass = new TYPE_CLASS(parentClassName, this.getName(),new TYPE_CLASS_VAR_DEC_LIST(this.fields));
        symbol_table.enter(currentClass.name,currentClass);
		return currentClass;  
	}
}

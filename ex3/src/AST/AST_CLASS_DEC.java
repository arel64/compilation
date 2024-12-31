package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
public class AST_CLASS_DEC extends AST_DEC {
      
    public String parentClassName;
    public AST_DEC_LIST fields; 

    public AST_CLASS_DEC(String className, String parentClass, AST_DEC_LIST fields) {
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
    
    @Override
	public TYPE SemantMe(){
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		int scope = symbol_table.getCurrentScopeIndex();
		if (scope != 0){
			throw new SemanticException("Scope mismatch found scope:" +scope);
		}
		if (parentClassName != null){
			TYPE parent = symbol_table.find(parentClassName);
			if (parent == null)
			{
				throw new SemanticException("Specified extends not found");
			}
		}
		TYPE_CLASS currentClass = new TYPE_CLASS(parentClassName, this.getName(),new TYPE_CLASS_VAR_DEC_LIST(this.fields));
		symbol_table.enter(currentClass.name,currentClass);

		
	}
}

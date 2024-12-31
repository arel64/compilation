package AST;
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
		int scope = SYMBOL_TABLE.getInstance().getCurrentScopeIndex();
		if (scope != 0){
			//throw error 
		}
		if (parentClassName != null){
			TYPE parrent = SYMBOL_TABLE.getInstance().find(parentClassName);
		}
		
	}
}

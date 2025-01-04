package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.*;

public class AST_ARRAY_TYPEDEF extends AST_DEC {
    public AST_TYPE baseType;

    public AST_ARRAY_TYPEDEF(String arrayName, AST_TYPE baseType) {
        super(arrayName);
        this.baseType = baseType;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("ARRAY_DEC\n array %s = %s[];",this.getName(),this.baseType)
        );
    }

    @Override
    public TYPE SemantMe(){
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		int scope = symbol_table.getCurrentScopeIndex();
		if (scope != 0){
			throw new SemanticException("Scope mismatch found scope:" +scope);
		}
        TYPE arrayType = baseType.SemantMe();
        if (arrayType == TYPE_VOID){
            throw new SemanticException("Array of type void" +this.getName());
        }
        symbol_table.enter(this.getName(), arrayType);
        return null;  //maybe create an array type? 
    }
    //where do we ckeck if the type is equal to the arrayType?
}

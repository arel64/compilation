package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
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
    public TYPE_ARRAY SemantMe() throws SemanticException{
		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		if (!symbol_table.isAtGlobalScope()){
			throw new SemanticException(lineNumber,"Scope mismatch found scope:" +  symbol_table.getCurrentScopeIndex());
		}
        TYPE_ARRAY arrayType = new TYPE_ARRAY(baseType.SemantMeLog());
        if (arrayType.isVoid()){
            throw new SemanticException(lineNumber,"Array of type void" +this.getName());
        }
        symbol_table.enter(this.getName(), arrayType);
        return arrayType;
    }
}

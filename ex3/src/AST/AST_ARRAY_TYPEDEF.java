package AST;
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
        //check if the type is a valid type
        TYPE arrayType = baseType.semantMe()
        
    }
    
}
=
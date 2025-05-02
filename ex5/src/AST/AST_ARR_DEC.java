package AST;


// New class extending AST_VAR_DEC for array declarations
public class AST_ARR_DEC extends AST_VAR_DEC {

    // Constructor mirroring AST_VAR_DEC
    public AST_ARR_DEC(String arrayName, AST_TYPE referenceType, AST_NEW_EXP newExp) {
        super(arrayName, referenceType, newExp);
    }


    @Override
    public void PrintMe() {
         AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"AST_ARR_DEC "+ this.toString());	
         // Log edge to the type definition node
         if (t != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,t.SerialNumber);
         // Log edge to the initial value expression (e.g., AST_NEW_EXP)
		 if (varValue != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,varValue.SerialNumber);
    }

   
} 
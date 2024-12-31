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
		TYPE leftType = left.SemantMe();
		TYPE rightType = right.SemantMe();

		// if op = + - * 
			// Addition or string concatenation
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				return TYPE_INT; // Integer addition
			} else {
				throw new SemanticError("Invalid types for '+' operator");
			}
		if (binop.op.equals("/")) {
			// Arithmetic operations
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				if (right.isZeroConstant()) {
					throw new SemanticError("Division by zero");
				}
				return TYPE_INT;
			} else {
				throw new SemanticError("Invalid types for arithmetic operator '" + operator + "'");
			}
		} else if (binop.op.equals("<") || binop.op.equals(">")) {
			if (leftType == TYPE_INT && rightType == TYPE_INT) {
				return TYPE_INT;
			} else {
				throw new SemanticError("Invalid types for comparison operator '" + operator + "'");
			}
		} else {
			throw new SemanticError("Unsupported binary operator '" + operator + "'");
		} 
	}
}

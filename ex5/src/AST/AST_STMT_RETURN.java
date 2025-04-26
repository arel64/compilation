package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_STMT_RETURN extends AST_STMT {
    public AST_EXP exp;

    public AST_STMT_RETURN(AST_EXP exp) {
        this.exp = exp;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "RETURN "+exp    
        );
        if(exp != null)
        {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, exp.SerialNumber);
            exp.PrintMe();

        }

        
    }
    
    @Override
    public TYPE SemantMe() throws SemanticException {
        //TOOD:: Add scope validation that I am in a funcction / A statement conditinal
        TYPE t = TYPE_VOID.getInstance();
        if(exp != null)
        {
            t = exp.SemantMe();
        }
        
        return new TYPE_RETURN(t);
        
    }

    public TEMP IRme()
	{
        String endLabel = IR.getInstance().getCurrentFunctionEndLabel();
        if (endLabel == null) {
            // This should ideally be caught during semantic analysis,
            // but adding a runtime check for safety.
            throw new RuntimeException("IR Generation Error: return statement found outside of a function context.");
        }

        // 1. Handle return value (if expression exists)
        if (exp != null) { 
            TEMP returnValue = exp.IRme();
            // Use IRcommand_Return to generate the 'move $v0, returnValue' instruction
            IR.getInstance().Add_IRcommand(new IRcommand_Return(returnValue));
        } else {
            // For void return, no need to set $v0 explicitly.
            // IRcommand_Return(null) doesn't generate code anyway.
        }

		// 2. Generate unconditional jump to the function's end label
        IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(endLabel));

		return null; // Return statement itself doesn't produce a TEMP value
	}
}

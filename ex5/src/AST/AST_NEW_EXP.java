package AST;

import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;

import java.util.ArrayList;

import IR.*;

public class AST_NEW_EXP extends AST_EXP {
    public AST_TYPE type;
    public AST_EXP exp;

    // Store results from SemantMe for IRme (class instance case)
    private TYPE_CLASS resolvedClassType = null;
    private int calculatedSize = -1;

    public AST_NEW_EXP(AST_TYPE type, AST_EXP exp) {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.type = type;
        this.exp = exp;
    }

    public AST_NEW_EXP(AST_TYPE type) {
        this(type, null);
    }

    @Override
    public String toString() {
        String expString = exp == null ? "" : String.format("[%s]", exp);
        return String.format("new %s %s", type, expString);
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        TYPE myType = type.SemantMeLog();

        if (exp == null) { // Class instance creation
            if (!(myType instanceof TYPE_CLASS)) {
                throw new SemanticException(lineNumber,
                        String.format("Cannot 'new' a non-class type: %s", myType.getName()));
            }
            this.resolvedClassType = (TYPE_CLASS) myType;
            // Get size calculated during AST_CLASS_DEC.SemantMe
            this.calculatedSize = this.resolvedClassType.getInstanceSize();
            if (this.calculatedSize < 0) {
                // This indicates an issue - size should have been set in AST_CLASS_DEC
                throw new SemanticException(lineNumber,
                        String.format("Internal Error: Size not calculated for class %s before 'new'.",
                                this.resolvedClassType.getName()));
            }
            return this.resolvedClassType; // Return the class type itself
        }

        TYPE expType = exp.SemantMe();
        if (expType != TYPE_INT.getInstance()) {
            throw new SemanticException(lineNumber, "New array expression size is not int");
        }
        if (expType.isVoid()) {
            throw new SemanticException(lineNumber, "New expr type cannot be void");
        }
        if ((exp instanceof AST_LIT_NUMBER)) {
            int value = Integer.parseInt(((AST_LIT_NUMBER) exp).getValue());
            if (value <= 0) {
                throw new SemanticException(lineNumber, "LEN<=0 for array length");
            }
        }
        return new TYPE_ARRAY(myType, myType.getName());

    }

    public TEMP IRme() {
        TEMP address = TEMP_FACTORY.getInstance().getFreshTEMP();
        try {
            TYPE instanceType = type.SemantMeLog();

            if (this.exp != null) { // Array creation
                TEMP sizeTemp = this.exp.IRme();
                if (sizeTemp == null) {
                    System.err.printf("IR Error(ln %d): Array size expression did not yield a value.\n", lineNumber);
                    return null;
                }
                String arrayBaseTypeName = "unknown_array_base";
                if (instanceType instanceof TYPE_ARRAY) {
                    arrayBaseTypeName = ((TYPE_ARRAY) instanceType).t.getName();
                } else if (instanceType != null) {
                    arrayBaseTypeName = instanceType.getName();
                }
                IR.getInstance().Add_IRcommand(new IRcommand_New_Array(address, arrayBaseTypeName, sizeTemp));
            } else { // Class instance creation
                     // Use stored info from SemantMe
                if (this.resolvedClassType == null || this.calculatedSize < 0) {
                    System.err.printf(
                            "IR Error(ln %d): Class type or size not resolved during SemantMe for 'new %s'.\n",
                            lineNumber, type.toString());
                    return null; // Cannot proceed
                }

                // 1. Load the calculated size into a TEMP
                TEMP sizeTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
                IR.getInstance().Add_IRcommand(new IRcommandConstInt(sizeTemp, this.calculatedSize));

                // 2. Generate the New_Class command
                IR.getInstance()
                        .Add_IRcommand(new IRcommand_New_Class(address, sizeTemp, this.resolvedClassType.getName()));

            }
            return address;
        } catch (SemanticException e) {
            System.err.printf("IR Generation Error (ln %d) during 'new' expression: %s\n", lineNumber, e.getMessage());
            return null;
        }
    }
}

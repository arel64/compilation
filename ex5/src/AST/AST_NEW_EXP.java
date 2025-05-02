package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_NEW_EXP extends AST_EXP {
    public AST_TYPE type;
    public AST_EXP exp;

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
        return String.format("new %s %s", type,expString);
    }

    @Override
    public TYPE SemantMe() throws SemanticException{
        TYPE myType = type.SemantMeLog();
        if (exp == null) {
            return myType;
        }
        TYPE expType = exp.SemantMe();
        //TODO:: need to be assignable to int ? Check definitions
        if (expType != TYPE_INT.getInstance()){
            throw new SemanticException(lineNumber,"New expr type is not int");
        }
        if(expType.isVoid())
        {
            throw new SemanticException(lineNumber,"New expr type cannot be void");
        }
        if ((exp instanceof AST_LIT_NUMBER)){
            int value = Integer.parseInt(((AST_LIT_NUMBER)exp).getValue());
            if (value <= 0)
            {
                throw new SemanticException(lineNumber,"LEN<=0 for array length");
            }
        }
        return new TYPE_ARRAY(myType,myType.getName());
    }
    public TEMP IRme()
	{
        TEMP address = TEMP_FACTORY.getInstance().getFreshTEMP();
        try {
            TYPE instanceType = type.SemantMeLog();

            if (this.exp != null) {
                TEMP sizeTemp = this.exp.IRme();
                if (sizeTemp == null) {
                    System.err.printf("IR Error(ln %d): Array size expression did not yield a value.\n", lineNumber);
                    return null;
                }
                String arrayBaseTypeName = "unknown_array_base";
                if (instanceType instanceof TYPE_ARRAY) {
                    arrayBaseTypeName = ((TYPE_ARRAY)instanceType).t.getName();
                } else if (instanceType != null) {
                    arrayBaseTypeName = instanceType.getName();
                }
                IR.getInstance().Add_IRcommand(new IRcommand_New_Array(address, arrayBaseTypeName, sizeTemp));
            }
            else {
                if (!(instanceType instanceof TYPE_CLASS)) {
                    System.err.printf("IR Error(ln %d): Attempting 'new' on a non-class type: %s\n", lineNumber, instanceType.getName());
                    return null;
                }
                TYPE_CLASS classType = (TYPE_CLASS) instanceType;
                int size = classType.getSize();
                if (size < 0) {
                    System.err.printf("IR Error(ln %d): Could not determine size for class: %s\n", lineNumber, classType.getName());
                    return null;
                }
                IR.getInstance().Add_IRcommand(new IRcommand_New_Class(address, classType.getName(), size));
            }
            return address;
        } catch (SemanticException e) {
            System.err.printf("IR Generation Error (ln %d) during 'new' expression: %s\n", lineNumber, e.getMessage());
            return null;
        }
	}
}

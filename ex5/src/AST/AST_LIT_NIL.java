package AST;

import TYPES.*;
import TEMP.*;
import IR.IR;
import IR.IRcommand_Load;
import IR.IRcommand_Load_Global;
import MIPS.MIPSGenerator;

public class AST_LIT_NIL extends AST_LIT {

    public AST_LIT_NIL() {
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public TYPE_NIL SemantMe() {
        return TYPE_NIL.getInstance();
    }

    @Override
    public TEMP IRme() {
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRcommand(new IRcommand_Load_Global(dst, "nil"));
        return dst;
    }

}

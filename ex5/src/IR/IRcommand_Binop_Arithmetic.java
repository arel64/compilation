package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Binop_Arithmetic extends IRcommand {
    public TEMP t1;
    public TEMP t2;
    public ArithmeticOperation operation;

    public enum ArithmeticOperation {
        ADD, SUB, MUL, DIV
    }

    public IRcommand_Binop_Arithmetic(TEMP dst, TEMP t1, TEMP t2, ArithmeticOperation operation) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
        this.operation = operation;
    }

    @Override
    public String toString() {
        String opStr = "";
        switch (operation) {
            case ADD: opStr = "Add"; break;
            case SUB: opStr = "Sub"; break;
            case MUL: opStr = "Mul"; break;
            case DIV: opStr = "Div"; break;
        }
        return String.format("IRcommand_Binop_%s_Integers: dst=%s, t1=%s, t2=%s", opStr, dst, t1, t2);
    }

    @Override
    public void staticAnalysis() {
        if (!t1.initialized || !t2.initialized)
            dst.initialized = false;
        super.staticAnalysis();
    }

    @Override
    public void MIPSme() {
        MIPSGenerator gen = MIPSGenerator.getInstance();
        
        if (dst == null || IR.getInstance().getRegister(dst) < 0)
        {
            return;
        }

        switch (operation) {
            case ADD:
                gen.add(dst, t1, t2);
                break;
            case SUB:
                gen.sub(dst, t1, t2);
                break;
            case MUL:
                gen.mul(dst, t1, t2);
                break;
            case DIV:
                // Add runtime check for division by zero
                String okLabel = getFreshLabel("div_ok");
                String errLabel = "string_illegal_div_by_0"; // Use predefined error label
                
                // Branch to okLabel if t2 != 0 (equivalent to NOT(t2 == 0))
                // Use beqz to jump *over* the error handling if t2 IS zero
                String afterErrLabel = getFreshLabel("div_after_error"); // Label after error handling
                gen.beqz(t2, afterErrLabel); // If t2 == 0, skip the division, jump to error handling
                
                // If t2 != 0, perform division and jump over error handling
                gen.div(dst, t1, t2);    // Perform division
                gen.jump(okLabel);      // Jump past error handling

                // Error handling block (if t2 == 0)
                gen.label(afterErrLabel); 
                // Load address of error string into $a0
                gen.la("$a0", errLabel); // Use the new la method
                // Load syscall code 4 (print_string) into $v0
                gen.li_imm("$v0", 4);     
                // Execute syscall
                gen.syscall(); // Use the new syscall method
                // Load syscall code 10 (exit) into $v0
                gen.li_imm("$v0", 10);    
                // Execute syscall
                gen.syscall(); // Use the new syscall method

                // Label for the case where division is safe
                gen.label(okLabel);
                break;
        }
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        // The operands t1 and t2 are used by this command.
        return new HashSet<TEMP>(Arrays.asList(t1, t2));
    }
} 
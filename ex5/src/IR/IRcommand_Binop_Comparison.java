package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

public class IRcommand_Binop_Comparison extends IRcommand {
    public TEMP t1;
    public TEMP t2;
    public ComparisonOperation operation;

    public enum ComparisonOperation {
        LT, // Less Than
        GT  // Greater Than
    }

    public IRcommand_Binop_Comparison(TEMP dst, TEMP t1, TEMP t2, ComparisonOperation operation) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
        this.operation = operation;
    }

    @Override
    public String toString() {
        String opStr = (operation == ComparisonOperation.LT) ? "LT" : "GT";
        return String.format("IRcommand_Binop_%s_Integers: dst=%s, t1=%s, t2=%s", opStr, dst, t1, t2);
    }

    @Override
    public void staticAnalysis() {
        // Basic initialization check: if either operand is uninitialized, the result is.
        if (t1 == null || t2 == null || !t1.initialized || !t2.initialized) {
             if (dst != null) dst.initialized = false;
        } else {
             if (dst != null) dst.initialized = true;
        }
        super.staticAnalysis();
    }

    @Override
    public void MIPSme() {
        MIPSGenerator gen = MIPSGenerator.getInstance();
        if (dst == null || IR.getInstance().getRegister(dst) < 0)
        {
            return;
        }

        String trueLabel = getFreshLabel("cmp_true");
        String endLabel = getFreshLabel("cmp_end");

        // MIPS 'blt' (branch if less than) is the core comparison instruction used.
        if (operation == ComparisonOperation.LT) {
            // Compare t1 < t2
            gen.blt(t1, t2, trueLabel);
        } else { // GT
            // Compare t1 > t2 (which is equivalent to t2 < t1)
            gen.blt(t2, t1, trueLabel);
        }

        // If condition is false, result (dst) is 0
        gen.li(dst, 0);
        gen.jump(endLabel);

        // If condition is true, result (dst) is 1
        gen.label(trueLabel);
        gen.li(dst, 1);

        // End label
        gen.label(endLabel);
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        // The operands t1 and t2 are used by this command.
        return new HashSet<TEMP>(Arrays.asList(t1, t2));
    }
} 
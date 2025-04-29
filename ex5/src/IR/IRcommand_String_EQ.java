package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * IR Command for String Content Equality (s1 == s2).
 * Compares strings byte-by-byte.
 */
public class IRcommand_String_EQ extends IRcommand {
    public TEMP s1;
    public TEMP s2;

    public IRcommand_String_EQ(TEMP dst, TEMP s1, TEMP s2) {
        this.dst = dst;
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public String toString() {
        return String.format("IRcommand_String_EQ: dst=%s, s1=%s, s2=%s", dst, s1, s2);
    }

    @Override
    public void MIPSme() {
        MIPSGenerator gen = MIPSGenerator.getInstance();

        String s1Reg = gen.tempToRegister(s1);
        String s2Reg = gen.tempToRegister(s2);
        String dstReg = gen.tempToRegister(dst);

        // Check if destination register is valid
        if (IR.getInstance().getRegister(dst) < 0) return;

        // 1. Move string addresses to argument registers $a0, $a1
        gen.move_registers(MIPSGenerator.A0, s1Reg);
        gen.move_registers(MIPSGenerator.A1, s2Reg);

        // 2. Call the strcmp helper function
        gen.jal("strcmp"); // $v0 = 1 if equal, 0 if not equal. Clobbers $t0, $t1.

        // 3. Move the result from $v0 to the destination register
        gen.move_registers(dstReg, MIPSGenerator.V0);
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        return new HashSet<TEMP>(Arrays.asList(s1, s2));
    }

     @Override
    public void staticAnalysis() {
         if (!s1.initialized || !s2.initialized)
            dst.initialized = false;
        super.staticAnalysis();
    }
} 
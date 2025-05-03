package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * IR Command for String Concatenation (s1 + s2).
 * Allocates new memory on the heap for the result.
 */
public class IRcommand_String_Concat extends IRcommand {
    public TEMP s1;
    public TEMP s2;

    public IRcommand_String_Concat(TEMP dst, TEMP s1, TEMP s2) {
        this.dst = dst;
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public String toString() {
        return String.format("IRcommand_String_Concat: dst=%s, s1=%s, s2=%s", dst, s1, s2);
    }

    @Override
    public void MIPSme() {
        MIPSGenerator gen = MIPSGenerator.getInstance();

        if (IR.getInstance().getRegister(dst) < 0)
            return;

        // --- Calculate Lengths ---
        // 1. Calculate length of s1 -> $s0 (TEMP_REG_1)
        gen.move_from_temp_to_reg(MIPSGenerator.A0, s1);
        gen.jal("strlen");
        gen.move_registers(MIPSGenerator.TEMP_REG_1, MIPSGenerator.V0); // $s0 = len1

        // 2. Calculate length of s2 -> $s1 (TEMP_REG_2)
        gen.move_from_temp_to_reg(MIPSGenerator.A0, s2);
        gen.jal("strlen");
        gen.move_registers(MIPSGenerator.TEMP_REG_2, MIPSGenerator.V0); // $s1 = len2

        // --- Save Temps to Stack ---
        // Allocate stack space for 3 items (s1 addr, s2 addr, len1)
        gen.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -12);
        gen.sw_reg_sp(s1, 8);
        gen.sw_reg_sp(s2, 4);
        gen.sw_reg_sp(MIPSGenerator.TEMP_REG_1, 0); // Use String version for specific reg

        // --- Allocate Memory ---
        // 3. Calculate total length = len1 + len2 + 1 -> $a0
        // Use $s0 (len1) and $s1 (len2), which are safe *before* stack save
        gen.add_registers(MIPSGenerator.A0, MIPSGenerator.TEMP_REG_1, MIPSGenerator.TEMP_REG_2);
        gen.addi_imm(MIPSGenerator.A0, MIPSGenerator.A0, 1);

        // 4. Allocate memory on the heap
        gen.malloc(dst, MIPSGenerator.A0); // $v0 = new buffer address, moved to dstReg

        // --- Copy Strings ---
        // Note: strcpy clobbers $a0, $a1, $v0, and internally uses $s0 ($t8)
        // 5. Copy s1 to the new memory
        gen.move_from_temp_to_reg(MIPSGenerator.A0, dst);
        gen.lw_reg_sp(MIPSGenerator.A1, 8); // Use String version: a1 = s1 address (restored from stack)
        gen.jal("strcpy"); // strcpy(dst, s1). Clobbers $s0, $s1

        // 6. Copy s2 to the end of s1 in the new memory
        gen.lw_reg_sp(MIPSGenerator.TEMP_REG_1, 0); // Use String version: Restore len1 ($s0)
        gen.add_temp_reg(MIPSGenerator.A0, dst, MIPSGenerator.TEMP_REG_1);
        gen.lw_reg_sp(MIPSGenerator.A1, 4); // Use String version: a1 = s2 address (restored from stack)
        gen.jal("strcpy"); // strcpy(dst + len1, s2)

        // --- Clean Up Stack ---
        gen.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, 12); // Deallocate stack space
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
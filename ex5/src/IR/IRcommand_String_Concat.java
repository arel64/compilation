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

        String s1Reg = gen.tempToRegister(s1);
        String s2Reg = gen.tempToRegister(s2);
        String dstReg = gen.tempToRegister(dst);

		
		if (IR.getInstance().getRegister(dst) < 0) return;

        // --- Calculate Lengths ---
        // 1. Calculate length of s1 -> $s0 (TEMP_REG_1)
        gen.move_registers(MIPSGenerator.A0, s1Reg);
        gen.jal("strlen");
        gen.move_registers(MIPSGenerator.TEMP_REG_1, MIPSGenerator.V0); // $s0 = len1

        // 2. Calculate length of s2 -> $s1 (TEMP_REG_2)
        gen.move_registers(MIPSGenerator.A0, s2Reg);
        gen.jal("strlen");
        gen.move_registers(MIPSGenerator.TEMP_REG_2, MIPSGenerator.V0); // $s1 = len2

        // --- Save Temps to Stack ---
        // Allocate stack space for 3 items (s1 addr, s2 addr, len1)
        gen.addi_imm(MIPSGenerator.SP, MIPSGenerator.SP, -12);
        gen.sw_reg_sp(s1Reg, 8); // Save s1 address
        gen.sw_reg_sp(s2Reg, 4); // Save s2 address
        gen.sw_reg_sp(MIPSGenerator.TEMP_REG_1, 0); // Save len1 ($s0)

        // --- Allocate Memory ---
        // 3. Calculate total length = len1 + len2 + 1 -> $a0
        // Use $s0 (len1) and $s1 (len2), which are safe *before* stack save
        gen.add_registers(MIPSGenerator.A0, MIPSGenerator.TEMP_REG_1, MIPSGenerator.TEMP_REG_2);
        gen.addi_imm(MIPSGenerator.A0, MIPSGenerator.A0, 1);

        // 4. Allocate memory on the heap
        gen.malloc(dst, MIPSGenerator.A0); // $v0 = new buffer address, moved to dstReg

        // --- Copy Strings ---
        // 5. Copy s1 to the new memory
        gen.move_registers(MIPSGenerator.A0, dstReg);       // a0 = destination buffer
        gen.lw_reg_sp(MIPSGenerator.A1, 8);                // a1 = s1 address (restored from stack)
        gen.jal("strcpy");                                  // strcpy(dst, s1). Clobbers $s0, $s1

        // 6. Copy s2 to the end of s1 in the new memory
        gen.lw_reg_sp(MIPSGenerator.TEMP_REG_1, 0);        // Restore len1 ($s0) from stack *after* it was clobbered
        gen.add_registers(MIPSGenerator.A0, dstReg, MIPSGenerator.TEMP_REG_1); // a0 = dst + len1 (use restored len1)
        gen.lw_reg_sp(MIPSGenerator.A1, 4);                // a1 = s2 address (restored from stack)
        gen.jal("strcpy");                                  // strcpy(dst + len1, s2)

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
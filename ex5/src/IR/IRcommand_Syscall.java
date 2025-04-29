package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.HashSet;
import java.util.Arrays;

public class IRcommand_Syscall extends IRcommand {
    int syscallCode; // Value for $v0
    TEMP arg0Temp = null; // Optional TEMP for $a0
    Integer arg0Imm = null; // Optional Immediate for $a0 (used if arg0Temp is null)
    
    // Constructor for syscalls with TEMP argument (e.g., print_int)
    public IRcommand_Syscall(int code, TEMP arg0) {
        if (code != 1 && code != 5 && code != 4) { // Add other codes needing TEMP arg here
             System.err.printf("Warning: Using TEMP argument constructor for syscall %d that might not expect one.\n", code);
        }
        this.syscallCode = code;
        this.arg0Temp = arg0;
    }

    // Constructor for syscalls with Immediate argument (e.g., print_char)
    public IRcommand_Syscall(int code, int arg0) {
         if (code != 11) { // Add other codes needing Imm arg here
             System.err.printf("Warning: Using Immediate argument constructor for syscall %d that might not expect one.\n", code);
        }
        this.syscallCode = code;
        this.arg0Imm = arg0;
    }

     // Constructor for syscalls with no argument (e.g., exit)
    public IRcommand_Syscall(int code) {
        if (code != 10) { // Add other codes needing no arg here
             System.err.printf("Warning: Using no-argument constructor for syscall %d that might expect one.\n", code);
        }
        this.syscallCode = code;
    }

    @Override
    public void MIPSme() {
        MIPSGenerator gen = MIPSGenerator.getInstance();

        // Load argument into $a0 if provided
        if (arg0Temp != null) {
            // Check if TEMP is allocated before trying to move
            if (IR.getInstance().getRegister(arg0Temp) < 0) {
                 System.err.printf("ERROR: Cannot perform syscall %d with unallocated TEMP %s. Skipping call.\n", syscallCode, arg0Temp);
                 return; // Skip MIPS generation if TEMP not valid
            }
            // Use the new generator method
            gen.move_temp_to_a0(arg0Temp); 
        } else if (arg0Imm != null) {
            gen.li_imm("$a0", arg0Imm);
        }
        // Else: no argument needed in $a0

        // Load syscall code into $v0
        System.out.printf("DEBUG: IRcommand_Syscall calling li_imm for $v0 with code %d\n", syscallCode);
        gen.li_imm("$v0", syscallCode);

        // Perform syscall
        gen.syscall(); 
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        if (arg0Temp != null) {
            return new HashSet<TEMP>(Arrays.asList(arg0Temp));
        } else {
            return new HashSet<TEMP>();
        }
    }

    @Override
    public String toString() {
        String argStr = "";
        if (arg0Temp != null) argStr = String.format("arg0=%s", arg0Temp);
        else if (arg0Imm != null) argStr = String.format("arg0_imm=%d", arg0Imm);
        return String.format("SYSCALL(code=%d, %s)", syscallCode, argStr);
    }
} 
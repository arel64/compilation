package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.HashSet;

public class IRcommand_Load_Global extends IRcommand {
    String varName;

    // dst is the TEMP where the loaded value will be stored
    public IRcommand_Load_Global(TEMP dst, String varName) {
        this.dst = dst;
        this.varName = varName;
    }

    @Override
    public void MIPSme() {
        MIPSGenerator.getInstance().load_global(dst, varName);
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        // This command defines dst, but doesn't use any TEMPs before defining it.
        // Liveness analysis handles this: the variable 'dst' becomes live *after* this.
        return new HashSet<>(); 
    }

    @Override
    public String toString() {
        return String.format("%s := LoadGlobal %s", dst, varName);
    }
} 
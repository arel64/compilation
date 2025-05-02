package IR;

import MIPS.MIPSGenerator;
import TEMP.TEMP;
import java.util.HashSet;
import java.util.Set;

public class IRcommand_Global_Store extends IRcommand {
    String varName;
    TEMP srcTemp;

    public IRcommand_Global_Store(String varName, TEMP srcTemp) {
        this.varName = varName;
        this.srcTemp = srcTemp;
    }

    @Override
    public void MIPSme() {
        // This command is handled in Pass 3 (FUNCTION context)
        // sw_global internally prefixes varName with "global_"
        String globalLabel = "global_" + varName;
        MIPSGenerator.getInstance().sw_global(srcTemp, globalLabel);
    }

    @Override
    public HashSet<TEMP> liveTEMPs() {
        // The source TEMP is used (live) before this command
        HashSet<TEMP> used = new HashSet<>();
        if (srcTemp != null) {
            used.add(srcTemp);
        }
        return used;
    }

    @Override
    public String toString() {
        return String.format("GlobalStore %s := %s", varName, srcTemp);
    }
} 
package IR;

import MIPS.MIPSGenerator;
import TEMP.*;

public class IRcommand_Prologue extends IRcommand {
    int frameSize;

    public IRcommand_Prologue(int frameSize) {
        super();
        this.frameSize = frameSize;
    }

    @Override
    public void MIPSme() {
        MIPSGenerator generator = MIPSGenerator.getInstance();
        generator.genPrologue(frameSize);
    }

    @Override
    public String toString() {
        return String.format("PROLOGUE (frameSize=%d)", frameSize);
    }
} 
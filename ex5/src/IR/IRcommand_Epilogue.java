package IR;

import MIPS.MIPSGenerator;
import TEMP.*;
import java.util.HashSet;

public class IRcommand_Epilogue extends IRcommand {
    int frameSize; // Total stack frame size in bytes

    public IRcommand_Epilogue(int frameSize) {
        super(); // Call base class constructor
        this.frameSize = frameSize;
        // Epilogue is a terminal command in the function's flow graph
        this.nextCommands = new int[]{}; 
    }

    @Override
    public void MIPSme() {
        MIPSGenerator generator = MIPSGenerator.getInstance();
        generator.genEpilogue(frameSize);
    }

    @Override
    public String toString() {
        return String.format("EPILOGUE (frameSize=%d)", frameSize);
    }

    // Static analysis for epilogue:
    // - Reads $ra, $fp from the stack.
    // - Modifies $sp, $fp.
    // - Jumps to $ra, ending the function's control flow.
    @Override
    public void staticAnalysis() {
        // Since this terminates the function block, we might clear the worklist entry
        // and set nextCommands to empty.
        if (workList.contains(this.index)) {
            workList.remove(workList.indexOf(this.index));
        }
        this.nextCommands = new int[]{}; // No commands follow epilogue in this function
        this.out = new HashSet<>(); // Liveness info likely doesn't propagate past the return
    }
    
    // Needs to declare usage of $ra potentially, if liveness tracks specific registers
    // For now, assuming liveness focuses on TEMPs.
} 
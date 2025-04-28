/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.util.Map;

import IR.IR;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class MIPSGenerator
{
	// Define the number of registers available to the allocator ($t0-$t7)
	public static final int NUM_ALLOCATABLE_REGISTERS = 8;

	// Add the $at register constant
	public static final String AT = "$at";
	public static final String V0 = "$v0"; // Useful for syscalls/return values
	public static final String A0 = "$a0"; // Useful for syscalls/arguments
	public static final String SP = "$sp";
	public static final String FP = "$fp";
	public static final String RA = "$ra";
	public static final String ZERO = "$zero";
	// Add dedicated temp registers, excluded from allocator
	public static final String TEMP_REG_1 = "$t8";
	public static final String TEMP_REG_2 = "$t9";

	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	private StringBuilder dataContent = new StringBuilder();
	private StringBuilder textContent = new StringBuilder();

	// Helper to append instructions consistently
	private void commandWrite(String instruction) {
		textContent.append("\t").append(instruction).append("\n");
	}

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile()
	{
		try {
			// Create a new file writer for the final output
			PrintWriter finalWriter = new PrintWriter("./output/MIPS.txt");
			
			// Build the complete MIPS file with correct organization
			finalWriter.print(".data\n");
			finalWriter.print("    string_access_violation: .asciiz \"Access Violation\"\n");
			finalWriter.print("    string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
			finalWriter.print("    string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
			finalWriter.print("    string_invalid_array_size: .asciiz \"Invalid Array Size\"\n");
			finalWriter.print(dataContent.toString());
			
			finalWriter.print("\n.text\n");
			finalWriter.print(".globl main\n");
			finalWriter.print("main:\n");

			String mainStartLabel = IR.getInstance().getFunctionLabel("main");
			finalWriter.format("\tj %s\n", mainStartLabel);

			String textContentStr = textContent.toString();

			finalWriter.print(textContentStr);
			
			// Add the program exit syscall sequence at the very end
			finalWriter.print("\nprogram_exit:\n"); // Add a label for clarity
			finalWriter.print("\tli $v0,10\n");
			finalWriter.print("\tsyscall\n");
			finalWriter.close();
			// No need to close fileWriter as we won't be using it anymore
			if (fileWriter != null) {
				fileWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void allocate(String var_name)
	{
		String instruction = String.format("\tglobal_%s: .word 721\n", var_name);
		dataContent.append(instruction);
	}
	public void store(TEMP src, int offset) {
		String reg = tempToRegister(src);
		commandWrite(String.format("sw %s,%d($sp)", reg, offset));
	}
	public void li(TEMP t,int value)
	{
		String reg = tempToRegister(t);
		commandWrite(String.format("li %s,%d", reg, value));
	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		String dstReg = tempToRegister(dst);
		String src1Reg = tempToRegister(oprnd1);
		String src2Reg = tempToRegister(oprnd2);
		add_registers(dstReg, src1Reg, src2Reg);
	}
    public void sub(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("sub %s,%s,%s", dstReg, src1Reg, src2Reg));
    }
	public void mul(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("mul %s,%s,%s", dstReg, src1Reg, src2Reg));
    }
	public void mul_imm(TEMP dst, TEMP oprnd1, int immediate) {
		String dstReg = tempToRegister(dst);
		String src1Reg = tempToRegister(oprnd1);
		commandWrite(String.format("mul %s,%s,%d", dstReg, src1Reg, immediate));
	}
    public void div(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("div %s,%s,%s", dstReg, src1Reg, src2Reg));
    }
	public String label(String inlabel)
	{
		String l = String.format("%s:\n", inlabel);
		String instruction = l;
		textContent.append(instruction);
		return l;
	}	
	public void jump(String inlabel)
	{
		commandWrite(String.format("j %s", inlabel));
	}	
	public void blt(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("blt %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void bge(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("bge %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void bne(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("bne %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void beq(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		commandWrite(String.format("beq %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void beqz(TEMP oprnd1, String label) {
        String src1Reg = tempToRegister(oprnd1);
		commandWrite(String.format("beqz %s,%s", src1Reg, label));
    }
	
	public void jal(String label) {
		commandWrite(String.format("jal %s", label));
    }
	
	public void addi(TEMP dst, TEMP src, int immediate) {
		String dstReg = tempToRegister(dst);
		String srcReg = tempToRegister(src);
		addi_imm(dstReg, srcReg, immediate);
	}

	public void sw_sp(TEMP src, int offset) { // Store word relative to $sp
		sw_reg_sp(tempToRegister(src), offset);
	}

	public void sw_fp(TEMP src, int offset) { // Store word relative to $fp
		String srcReg = tempToRegister(src);
		commandWrite(String.format("sw %s,%d($fp)", srcReg, offset));
	}

	public void lw_sp(TEMP dst, int offset) { // Load word relative to $sp
		lw_reg_sp(tempToRegister(dst), offset);
	}

	public void lw_fp(TEMP dst, int offset) { // Load word relative to $fp
		String dstReg = tempToRegister(dst);
		commandWrite(String.format("lw %s,%d($fp)", dstReg, offset));
	}

	public void move(TEMP dst, TEMP src) {
		String dstReg = tempToRegister(dst);
        String srcReg = tempToRegister(src);
		commandWrite(String.format("move %s,%s", dstReg, srcReg));
	}

	public void jr(TEMP target) {
		String targetReg = tempToRegister(target);
		commandWrite(String.format("jr %s", targetReg));
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MIPSGenerator instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MIPSGenerator() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static MIPSGenerator getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new MIPSGenerator();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
				String dirname="./output/";
				String filename=String.format("MIPS.txt");

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(dirname+filename);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			// Don't add main: label here - it will be added in finalizeFile
		}
		return instance;
	}

	// Helper method to convert TEMP to register name ($t0-$t9)
	public String tempToRegister(TEMP temp) {
		if (temp == null) {
			System.err.println("ERROR: Attempting to get register for null TEMP.");
			throw new RuntimeException("Null TEMP encountered in tempToRegister");
		}
		int regNum = IR.getInstance().getRegister(temp);
		if (regNum >= 0 && regNum < NUM_ALLOCATABLE_REGISTERS) { // Use the constant K
			return "$t" + regNum;
		} else if (regNum == -1) { // TEMP wasn't allocated a register
			System.err.println("ERROR: TEMP " + temp + " was not allocated a register (color -1).");
			throw new RuntimeException("Register allocation failed for TEMP " + temp);
		} else { // Allocated color is out of expected range (should be 0-7)
			 System.err.println("ERROR: TEMP " + temp + " assigned unexpected color " + regNum + ". Expected 0-" + (NUM_ALLOCATABLE_REGISTERS - 1) + ".");
			 throw new RuntimeException("Unexpected register color " + regNum + " for TEMP " + temp + ". Expected 0-" + (NUM_ALLOCATABLE_REGISTERS - 1) + ".");
		}
	}

	// --- Dedicated Prologue/Epilogue --- 

	public void genPrologue(int frameSize) {
		textContent.append("#### Prologue ####\n");
		// Allocate space on stack: addi $sp, $sp, -frameSize
		// frameSize includes space for locals, saved $fp, saved $ra
		addi_imm(SP, SP, -frameSize);

		// Save return address: sw $ra, offset($sp) (e.g., offset = frameSize - 4)
		sw_reg_sp(RA, frameSize - 4);

		// Save old frame pointer: sw $fp, offset($sp) (e.g., offset = frameSize - 8)
		sw_reg_sp(FP, frameSize - 8);

		// Set new frame pointer: addi $fp, $sp, frameSize 
		addi_imm(FP, SP, frameSize);
		textContent.append("#### Prologue End ####\n");
	}

	public void genEpilogue(int frameSize) {
		textContent.append("#### Epilogue ####\n");
		// Restore return address: lw $ra, offset($sp)
		lw_reg_sp(RA, frameSize - 4);

		// Restore old frame pointer: lw $fp, offset($sp)
		lw_reg_sp(FP, frameSize - 8);

		// Deallocate stack frame: addi $sp, $sp, frameSize
		addi_imm(SP, SP, frameSize);

		genReturnJump(); // Adds jr $ra
		textContent.append("#### Epilogue End ####\n");

	}

	public void genReturnJump() {
		jr_register(RA);
	}

	public void genMoveReturnValue(TEMP src) {
		String srcReg = tempToRegister(src);
		move_registers(V0, srcReg);
	}

    // Add immediate instruction (allows using register names like $sp)
    public void addi_imm(String dstReg, String srcReg, int immediate) {
		commandWrite(String.format("addi %s,%s,%d", dstReg, srcReg, immediate));
    }

    // Load immediate value into a specific register (e.g., $a0)
    public void li_imm(String dstReg, int immediate) {
		commandWrite(String.format("li %s,%d", dstReg, immediate));
    }

    public void move_from_v0(TEMP dst) {
        String dstReg = tempToRegister(dst);
        move_registers(dstReg, V0);
    }

    // Load Address
    public void la(String dstReg, String label) {
		commandWrite(String.format("la %s,%s", dstReg, label));
    }

    // Syscall
    public void syscall() {
		commandWrite("syscall");
    }

    // Store register relative to $sp
    public void sw_reg_sp(String regName, int offset) {
		commandWrite(String.format("sw %s,%d($sp)", regName, offset));
    }

    // Load register relative to $sp
    public void lw_reg_sp(String regName, int offset) {
		commandWrite(String.format("lw %s,%d($sp)", regName, offset));
    }

    // Append a raw, pre-formatted MIPS instruction
    public void appendRawInstruction(String instruction) {
        // Ensure proper formatting (e.g., leading tab, trailing newline)
        String formatted = instruction.trim();
        if (!formatted.isEmpty()) {
            commandWrite(formatted);
        }
    }

    // Move TEMP value into $a0
    public void move_temp_to_a0(TEMP src) {
        String srcReg = tempToRegister(src);
		move_registers(A0, srcReg);
    }

	public void print_int(TEMP t) {
		move_temp_to_a0(t);
		li_imm(V0, 1); // Syscall code for print_int
		syscall();
		li_imm(A0, 32); // ASCII code for space
		li_imm(V0, 11); // Syscall code for print_char
		syscall();
	}

    // Branch if less than or equal to zero
    public void blez(TEMP cond, String label) {
        String condReg = tempToRegister(cond);
		commandWrite(String.format("blez %s,%s", condReg, label));
    }

    // Store TEMP value at offset relative to another TEMP's address
    public void sw_temp_offset(TEMP src, int offset, TEMP base) {
        String srcReg = tempToRegister(src);
        String baseReg = tempToRegister(base);
		sw_offset(srcReg, offset, baseReg);
    }

    // Load TEMP value from offset relative to another TEMP's address
    public void lw_temp_offset(TEMP dst, int offset, TEMP base) {
        String dstReg = tempToRegister(dst);
        String baseReg = tempToRegister(base);
		lw_offset(dstReg, offset, baseReg);
    }

    // Print string given a data label
    public void print_string_from_label(String label) {
        la(A0, label);
        li_imm(V0, 4);
        syscall();
    }

    // Exit program syscall
    public void exit() {
        li_imm(V0, 10);
        syscall();
    }

    // Branch if less than zero
    public void bltz(TEMP cond, String label) {
        String condReg = tempToRegister(cond);
		commandWrite(String.format("bltz %s,%s", condReg, label));
    }

	public void malloc(TEMP dst, TEMP size) {
		move_temp_to_a0(size);
		li_imm(V0, 9); // Syscall code for sbrk
		syscall();     // Allocate memory, address is in $v0
		move_from_v0(dst); // Move address from $v0 to dst TEMP's register
	}

    // Shift Left Logical using TEMPs
    public void sll(TEMP dst, TEMP src, int shiftAmount) {
        String dstReg = tempToRegister(dst);
        String srcReg = tempToRegister(src);
		sll_registers(dstReg, srcReg, shiftAmount);
    }

	// NEW: Shift Left Logical using register names
	public void sll_registers(String dstReg, String srcReg, int shiftAmount) {
		commandWrite(String.format("sll %s,%s,%d", dstReg, srcReg, shiftAmount));
	}

	// NEW: Move using register names
	public void move_registers(String dstReg, String srcReg) {
		commandWrite(String.format("move %s,%s", dstReg, srcReg));
	}

	// NEW: Store word using register names for src and base
	public void sw_offset(String srcReg, int offset, String baseReg) {
		commandWrite(String.format("sw %s,%d(%s)", srcReg, offset, baseReg));
	}

	// NEW: Load word using register names for dst and base
	public void lw_offset(String dstReg, int offset, String baseReg) {
		commandWrite(String.format("lw %s,%d(%s)", dstReg, offset, baseReg));
	}

	// NEW: Add operation using register names (strings)
	public void add_registers(String dstReg, String src1Reg, String src2Reg) {
		commandWrite(String.format("add %s,%s,%s", dstReg, src1Reg, src2Reg));
	}

	// NEW: Jump register using name
	public void jr_register(String targetReg) {
		commandWrite(String.format("jr %s", targetReg));
	}
}
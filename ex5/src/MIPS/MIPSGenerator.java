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
	public static final int NUM_ALLOCATABLE_REGISTERS = 10;

	// Add the $at register constant
	public static final String AT = "$at";
	public static final String V0 = "$v0"; // Useful for syscalls/return values
	public static final String A0 = "$a0"; // Useful for syscalls/arguments
	public static final String A1 = "$a1"; // Useful for syscalls/arguments
	public static final String SP = "$sp";
	public static final String FP = "$fp";
	public static final String RA = "$ra";
	public static final String ZERO = "$zero";
	// Add dedicated temp registers, excluded from allocator
	public static final String TEMP_REG_1 = "$s0";
	public static final String TEMP_REG_2 = "$s1";

	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	private StringBuilder dataContent = new StringBuilder();
	private StringBuilder textContent = new StringBuilder();
	private StringBuilder globalInitContent = new StringBuilder(); // New buffer for global init

	// Helper to append instructions consistently
	private void commandWrite(String instruction) {
		textContent.append("\t").append(instruction).append("\n");
	}

	// Helper to append global init instructions
	private void globalInitWrite(String instruction) {
		globalInitContent.append("\t").append(instruction).append("\n");
	}

	/**
	 * Appends a directive to the .data segment content.
	 * @param directive The complete data directive line (e.g., "my_string: .asciiz \"Hello\"").
	 */
	public void addDataDirective(String directive) {
		dataContent.append("    ").append(directive).append("\n"); // Indent for readability
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
			// Print global initialization code first
			finalWriter.print(globalInitContent.toString()); 
			// Then jump to the actual start of the user's main function
			finalWriter.print("\tjal mainStart\n");
			finalWriter.print("j program_exit\n");

			String textContentStr = textContent.toString();

			finalWriter.print(textContentStr);
			
			// Add Standard Library Helper Functions
			appendHelperFunctions(finalWriter); 

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
		// Use .space to reserve space, initialization happens in .text
		String instruction = String.format("\tglobal_%s: .space 4\n", var_name); 
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

	/**
	 * Load Address using TEMP object for destination.
	 * Gets the register name for the TEMP and calls la(String, String).
	 */
	public void la_temp(TEMP dst, String label) {
		String dstReg = tempToRegister(dst);
		la(dstReg, label);
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
		_malloc(dst);
	}

	/**
	 * Allocates memory on the heap using the sbrk syscall.
	 * @param dst The TEMP to store the address of the allocated memory.
	 * @param sizeReg The REGISTER NAME holding the number of bytes to allocate.
	 */
	public void malloc(TEMP dst, String sizeReg) {
		// Ensure size is in $a0 for the syscall
		if (!sizeReg.equals(A0)) {
			move_registers(A0, sizeReg);
		}
		_malloc(dst);
	}

	private void _malloc(TEMP dst) {
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
		// This command now writes to the regular text content
		commandWrite(String.format("sw %s,%d(%s)", srcReg, offset, baseReg));
	}

	public void sw_global(String srcReg, String globalVarLabel) {
		// 1. Load address of global variable into $at
		globalInitWrite(String.format("la %s, %s", AT, globalVarLabel));
		// 2. Store the value from srcReg into the global variable address
		globalInitWrite(String.format("sw %s, 0(%s)", srcReg, AT));
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

	// NEW: Adds standard library helper functions (strlen, strcpy, strcmp) to the output
	private void appendHelperFunctions(PrintWriter finalWriter) {
		finalWriter.println("\n");
		finalWriter.println("#-----------------------------------------------\n");
		finalWriter.println("# Standard Library Helper Functions             \n");
		finalWriter.println("#-----------------------------------------------\n");
		finalWriter.println("\n");

		// strlen: Calculates length of a null-terminated string.
		// $a0: Address of the string
		// $v0: Length of the string (excluding null terminator)
		// Preserves: $s0, $s1 (Saves/restores them internally)
		finalWriter.println("strlen:");
		finalWriter.println("\taddi    $sp, $sp, -4 # Allocate stack space for $s0");
		finalWriter.println(String.format("\tsw      %s, 0($sp)  # Save $s0", TEMP_REG_1));
		finalWriter.println("\tli      $v0, 0      # Initialize length counter");
		finalWriter.println("strlen_loop:");
		finalWriter.println(String.format("\tlb      %s, 0($a0) # Load byte from string into $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tbeqz    %s, strlen_end # If byte is null, end", TEMP_REG_1));
		finalWriter.println("\taddi    $v0, $v0, 1 # Increment length");
		finalWriter.println("\taddi    $a0, $a0, 1 # Move to next character");
		finalWriter.println("\tj       strlen_loop # Loop");
		finalWriter.println("strlen_end:");
		finalWriter.println(String.format("\tlw      %s, 0($sp)  # Restore $s0", TEMP_REG_1));
		finalWriter.println("\taddi    $sp, $sp, 4  # Deallocate stack space");
		finalWriter.println("\tjr      $ra         # Return");
		finalWriter.println("");

		// strcpy: Copies a null-terminated string from src to dst.
		// $a0: Address of destination buffer
		// $a1: Address of source string
		// $v0: Address of destination buffer ($a0)
		// Preserves: $s0 (Saves/restores it internally)
		finalWriter.println("strcpy:");
		finalWriter.println("\taddi    $sp, $sp, -4 # Allocate stack space for $s0");
		finalWriter.println(String.format("\tsw      %s, 0($sp)  # Save $s0", TEMP_REG_1));
		finalWriter.println("\tmove    $v0, $a0    # Store destination address for return value");
		finalWriter.println("strcpy_loop:");
		finalWriter.println(String.format("\tlb      %s, 0($a1) # Load byte from source into $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tsb      %s, 0($a0) # Store byte to destination using $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tbeqz    %s, strcpy_end # If byte is null, end", TEMP_REG_1));
		finalWriter.println("\taddi    $a0, $a0, 1 # Move to next destination byte");
		finalWriter.println("\taddi    $a1, $a1, 1 # Move to next source byte");
		finalWriter.println("\tj       strcpy_loop # Loop");
		finalWriter.println("strcpy_end:");
		finalWriter.println(String.format("\tlw      %s, 0($sp)  # Restore $s0", TEMP_REG_1));
		finalWriter.println("\taddi    $sp, $sp, 4  # Deallocate stack space");
		finalWriter.println("\tjr      $ra         # Return");
		finalWriter.println("");

		// strcmp: Compares two null-terminated strings for content equality.
		// $a0: Address of string 1
		// $a1: Address of string 2
		// $v0: 1 if strings are equal, 0 otherwise
		// Preserves: $s0, $s1 (Saves/restores them internally)
		finalWriter.println("strcmp:");
		finalWriter.println("\taddi    $sp, $sp, -8 # Allocate stack space for $s0, $s1");
		finalWriter.println(String.format("\tsw      %s, 4($sp)  # Save $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tsw      %s, 0($sp)  # Save $s1", TEMP_REG_2));
		finalWriter.println("\tli      $v0, 1      # Assume equal initially");
		finalWriter.println("strcmp_loop:");
		finalWriter.println(String.format("\tlb      %s, 0($a0) # Load byte from str1 into $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tlb      %s, 0($a1) # Load byte from str2 into $s1", TEMP_REG_2));
		finalWriter.println(String.format("\tbne     %s, %s, strcmp_neq # If bytes differ, strings not equal", TEMP_REG_1, TEMP_REG_2));
		finalWriter.println(String.format("\tbeqz    %s, strcmp_end # If byte is null (and they matched), strings are equal, end", TEMP_REG_1));
		finalWriter.println("\taddi    $a0, $a0, 1 # Move to next char str1");
		finalWriter.println("\taddi    $a1, $a1, 1 # Move to next char str2");
		finalWriter.println("\tj       strcmp_loop # Loop");
		finalWriter.println("strcmp_neq:");
		finalWriter.println("\tli      $v0, 0      # Set result to 0 (not equal)");
		finalWriter.println("\tj       strcmp_restore # Go to restore sequence"); // Jump to common restore point
		finalWriter.println("strcmp_end:");
		// $v0 is already 1 (equal) from initialization or previous loop state
		finalWriter.println("strcmp_restore:");
		finalWriter.println(String.format("\tlw      %s, 4($sp)  # Restore $s0", TEMP_REG_1));
		finalWriter.println(String.format("\tlw      %s, 0($sp)  # Restore $s1", TEMP_REG_2));
		finalWriter.println("\taddi    $sp, $sp, 8  # Deallocate stack space");
		finalWriter.println("\tjr      $ra         # Return");
		finalWriter.println("");
	}
}
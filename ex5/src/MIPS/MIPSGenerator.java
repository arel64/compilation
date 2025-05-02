/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import java.nio.file.Path;
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

	// NEW: Context tracking for writing MIPS code
	private enum CodeContext { DATA, GLOBAL_INIT, FUNCTION }
	private CodeContext currentContext = CodeContext.FUNCTION; // Default to function code

	// NEW: Methods to set the writing context
	public void setContextData() { this.currentContext = CodeContext.DATA; }
	public void setContextGlobalInit() { this.currentContext = CodeContext.GLOBAL_INIT; }
	public void setContextFunction() { this.currentContext = CodeContext.FUNCTION; }

	// NEW: Unified helper to append code based on context
	private void appendCode(String code) {
		String indentedCode = "\t" + code + "\n"; // Indent instructions
		switch (currentContext) {
			case DATA:
				dataContent.append("    ").append(code).append("\n"); // Data directives usually have different indentation
				break;
			case GLOBAL_INIT:
				globalInitContent.append(indentedCode);
				break;
			case FUNCTION:
				textContent.append(indentedCode);
				break;
		}
	}

	/**
	 * Appends a directive to the .data segment content.
	 * @param directive The complete data directive line (e.g., "my_string: .asciiz \"Hello\"").
	 */
	public void addDataDirective(String directive) {
		CodeContext originalContext = this.currentContext; // Save context
		setContextData(); // Set context for data
		appendCode(directive); // Use the unified append method
		this.currentContext = originalContext; // Restore context
	}

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile(String filepath)
	{
		try {
			// Create a new file writer for the final output
			PrintWriter finalWriter = new PrintWriter(filepath);
			
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
 			// Get the registered label for the main function
 			String mainLabel = IR.getInstance().getFunctionLabel("main");
 			finalWriter.format("\tjal %s\n", mainLabel); // Jump to the registered label
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
		// This method should only handle the .data part.
		CodeContext originalContext = this.currentContext;
		setContextData();
		appendCode(".align 2");
		appendCode(String.format("global_%s: .space 4", var_name)); 
		this.currentContext = originalContext;
	}
	public void store(TEMP src, int offset) {
		String reg = tempToRegister(src);
		appendCode(String.format("sw %s,%d($sp)", reg, offset));
	}
	public void li(TEMP t,int value)
	{
		String reg = tempToRegister(t);
		appendCode(String.format("li %s,%d", reg, value));
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
		appendCode(String.format("sub %s,%s,%s", dstReg, src1Reg, src2Reg));
    }
	public void mul(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("mul %s,%s,%s", dstReg, src1Reg, src2Reg));
    }
	public void mul_imm(TEMP dst, TEMP oprnd1, int immediate) {
		String dstReg = tempToRegister(dst);
		String src1Reg = tempToRegister(oprnd1);
		appendCode(String.format("mul %s,%s,%d", dstReg, src1Reg, immediate));
	}
    public void div(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("div %s,%s", src1Reg, src2Reg)); // MIPS div instruction only takes two operands
		appendCode(String.format("mflo %s", dstReg)); // Move result from LO register
    }
	public String label(String inlabel)
	{
		String l = String.format("%s:", inlabel);
		switch (currentContext) {
			case GLOBAL_INIT:
				globalInitContent.append(l).append("\n");
				break;
			case FUNCTION:
				textContent.append(l).append("\n");
				break;
			default: // Labels usually don't belong in .data
				textContent.append(l).append("\n"); // Default to text section
		}
		return l;
	}	
	public void jump(String inlabel)
	{
		appendCode(String.format("j %s", inlabel));
	}	
	/**
	 * Branch if less than. Compares TEMP oprnd1 with TEMP oprnd2.
	 */
	public void blt(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("blt %s,%s,%s", src1Reg, src2Reg, label));
    }
	
	/**
	 * Branch if less than. Compares TEMP oprnd1 with the value in register oprnd2Reg.
	 */
	public void blt_temp_reg(TEMP oprnd1, String oprnd2Reg, String label) {
        String src1Reg = tempToRegister(oprnd1);
		appendCode(String.format("blt %s,%s,%s", src1Reg, oprnd2Reg, label));
    }

	/**
	 * Branch if greater than. Compares TEMP oprnd1 with TEMP oprnd2.
	 */
	public void bgt(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        appendCode(String.format("bgt %s,%s,%s", src1Reg, src2Reg, label));
    }

	/**
	 * Branch if greater than. Compares TEMP oprnd1 with the value in register oprnd2Reg.
	 */
	public void bgt_temp_reg(TEMP oprnd1, String oprnd2Reg, String label) {
        String src1Reg = tempToRegister(oprnd1);
        appendCode(String.format("bgt %s,%s,%s", src1Reg, oprnd2Reg, label));
    }

	public void bge(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("bge %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void bne(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("bne %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void beq(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
		appendCode(String.format("beq %s,%s,%s", src1Reg, src2Reg, label));
    }
	public void beqz(TEMP oprnd1, String label) {
        String src1Reg = tempToRegister(oprnd1);
		appendCode(String.format("beqz %s,%s", src1Reg, label));
    }
	
	public void jal(String label) {
		appendCode(String.format("jal %s", label));
    }
	
	public void addi(TEMP dst, TEMP src, int immediate) {
		String dstReg = tempToRegister(dst);
		String srcReg = tempToRegister(src);
		addi_imm(dstReg, srcReg, immediate);
	}

	public void sw_sp(TEMP src, int offset) { // Store word relative to $sp
		appendCode(String.format("sw %s,%d(%s)", tempToRegister(src), offset, SP));
	}

	public void sw_fp(TEMP src, int offset) { // Store word relative to $fp
		String srcReg = tempToRegister(src);
		appendCode(String.format("sw %s,%d(%s)", srcReg, offset, FP));
	}

	public void lw_sp(TEMP dst, int offset) { // Load word relative to $sp
		appendCode(String.format("lw %s,%d(%s)", tempToRegister(dst), offset, SP));
	}

	public void lw_fp(TEMP dst, int offset) { // Load word relative to $fp
		String dstReg = tempToRegister(dst);
		appendCode(String.format("lw %s,%d(%s)", dstReg, offset, FP));
	}

	public void move(TEMP dst, TEMP src) {
		String dstReg = tempToRegister(dst);
        String srcReg = tempToRegister(src);
		appendCode(String.format("move %s,%s", dstReg, srcReg));
	}

	public void jr(TEMP target) {
		String targetReg = tempToRegister(target);
		appendCode(String.format("jr %s", targetReg));
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
	private String tempToRegister(TEMP temp) {
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
		appendCode("# Prologue Start");
		// Allocate space on stack: addi $sp, $sp, -frameSize
		// frameSize includes space for locals, saved $fp, saved $ra
		appendCode(String.format("addi %s,%s,%d", SP, SP, -frameSize));

		// Save return address: sw $ra, offset($sp) (e.g., offset = frameSize - 4)
		appendCode(String.format("sw %s,%d(%s)", RA, frameSize - 4, SP));

		// Save old frame pointer: sw $fp, offset($sp) (e.g., offset = frameSize - 8)
		appendCode(String.format("sw %s,%d(%s)", FP, frameSize - 8, SP));

		// Set new frame pointer: addi $fp, $sp, frameSize 
		appendCode(String.format("addi %s,%s,%d", FP, SP, frameSize));
		appendCode("# Prologue End");
	}

	public void genEpilogue(int frameSize) {
		appendCode("# Epilogue Start");
		// Restore return address: lw $ra, offset($sp)
		appendCode(String.format("lw %s,%d(%s)", RA, frameSize - 4, SP));

		// Restore old frame pointer: lw $fp, offset($sp)
		appendCode(String.format("lw %s,%d(%s)", FP, frameSize - 8, SP));

		// Deallocate stack frame: addi $sp, $sp, frameSize
		appendCode(String.format("addi %s,%s,%d", SP, SP, frameSize));

		genReturnJump(); // Adds jr $ra
		appendCode("# Epilogue End");
	}

	public void genReturnJump() {
		appendCode(String.format("jr %s", RA));
	}

	public void genMoveReturnValue(TEMP src) {
		String srcReg = tempToRegister(src);
		appendCode(String.format("move %s,%s", V0, srcReg));
	}

    // Add immediate instruction (allows using register names like $sp)
    public void addi_imm(String dstReg, String srcReg, int immediate) {
		appendCode(String.format("addi %s,%s,%d", dstReg, srcReg, immediate));
    }

    // Load immediate value into a specific register (e.g., $a0)
    public void li_imm(String dstReg, int immediate) {
		appendCode(String.format("li %s,%d", dstReg, immediate));
    }

    public void move_from_v0(TEMP dst) {
        String dstReg = tempToRegister(dst);
        appendCode(String.format("move %s,%s", dstReg, V0));
    }

	/**
	 * Load Address using TEMP object for destination.
	 * Gets the register name for the TEMP and calls the private la(String, String).
	 */
	public void la(TEMP dst, String label) {
		String dstReg = tempToRegister(dst);
		la(dstReg, label);
	}

    // Syscall
    public void syscall() {
		appendCode("syscall");
    }

    // Store register relative to $sp
    public void sw_reg_sp(String regName, int offset) { // Keep this version for String register names
		appendCode(String.format("sw %s,%d(%s)", regName, offset, SP));
    }

    // Load register relative to $sp
    public void lw_reg_sp(String regName, int offset) { // Keep this version for String register names
		appendCode(String.format("lw %s,%d(%s)", regName, offset, SP));
    }

    // Append a raw, pre-formatted MIPS instruction
    public void appendRawInstruction(String instruction) {
        // Ensure proper formatting (e.g., leading tab, trailing newline)
        String formatted = instruction.trim();
        if (!formatted.isEmpty()) {
            appendCode(formatted);
        }
    }

    // Move TEMP value into $a0
    public void move_temp_to_a0(TEMP src) {
        String srcReg = tempToRegister(src);
		appendCode(String.format("move %s,%s", A0, srcReg));
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
		appendCode(String.format("blez %s,%s", condReg, label));
    }

    // Store TEMP value at offset relative to another TEMP's address
    public void sw_offset(TEMP src, int offset, TEMP base) {
        String srcReg = tempToRegister(src);
        String baseReg = tempToRegister(base);
		appendCode(String.format("sw %s,%d(%s)", srcReg, offset, baseReg));
    }

    // Load TEMP value from offset relative to another TEMP's address
    public void lw_offset(TEMP dst, int offset, TEMP base) {
        String dstReg = tempToRegister(dst);
        String baseReg = tempToRegister(base);
		appendCode(String.format("lw %s,%d(%s)", dstReg, offset, baseReg));
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
		appendCode(String.format("bltz %s,%s", condReg, label));
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
			appendCode(String.format("move %s, %s", A0, sizeReg));
		}
		_malloc(dst);
	}

	// Make this public so IRcommand_New_Class can call it
	public void _malloc(TEMP dst) {
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
		appendCode(String.format("sll %s,%s,%d", dstReg, srcReg, shiftAmount));
	}

	// NEW: Shift Left Logical using TEMP source into a specific register
	public void sll_temp_into_reg(String dstReg, TEMP src, int shiftAmount) {
		String srcReg = tempToRegister(src);
		sll_registers(dstReg, srcReg, shiftAmount);
	}

	// NEW: Move using register names
	public void move_registers(String dstReg, String srcReg) {
		appendCode(String.format("move %s,%s", dstReg, srcReg));
	}

	// NEW: Move from TEMP to specific register
	public void move_from_temp_to_reg(String dstReg, TEMP src) {
		String srcReg = tempToRegister(src);
		move_registers(dstReg, srcReg);
	}

	// NEW: Move from specific register to TEMP
	public void move_from_reg_to_temp(TEMP dst, String srcReg) {
		String dstReg = tempToRegister(dst);
		move_registers(dstReg, srcReg);
	}

	// NEW: Store word using register names for src and base
	public void sw_offset(String srcReg, int offset, String baseReg) {
		// This command now writes to the regular text content
		appendCode(String.format("sw %s,%d(%s)", srcReg, offset, baseReg));
	}

	// NEW: Store word using TEMP source and register name base
	public void sw_offset_from_temp(TEMP src, int offset, String baseReg) {
		String srcReg = tempToRegister(src);
		sw_offset(srcReg, offset, baseReg);
	}

	// NEW: Store word specifically for global initialization
	public void sw_global_reg(String srcReg, String globalVarLabel) {
		// Use TEMP_REG_1 ($s0) as a temporary register for the address
		String tempAddrReg = TEMP_REG_1; 
		// 1. Load address of global variable into the temporary register
		appendCode(String.format("la %s, %s", tempAddrReg, globalVarLabel));
		// 2. Store the value from srcReg into the global variable address (held in tempAddrReg)
		appendCode(String.format("sw %s, 0(%s)", srcReg, tempAddrReg));
	}

	// NEW: Store word specifically for global initialization using TEMP source
	public void sw_global(TEMP src, String globalVarLabel) {
		String srcReg = tempToRegister(src);
		sw_global_reg(srcReg, globalVarLabel); // Call the register-based version
	}

	// NEW: Load word using register names for dst and base
	public void lw_offset(String dstReg, int offset, String baseReg) {
		appendCode(String.format("lw %s,%d(%s)", dstReg, offset, baseReg));
	}

	// NEW: Load word using register name base into TEMP destination
	public void lw_offset_from_reg(TEMP dst, int offset, String baseReg) {
		String dstReg = tempToRegister(dst);
		lw_offset(dstReg, offset, baseReg);
	}

	// NEW: Add operation using register names (strings)
	public void add_registers(String dstReg, String src1Reg, String src2Reg) {
		appendCode(String.format("add %s,%s,%s", dstReg, src1Reg, src2Reg));
	}

	// NEW: Add operation using TEMP source 1 and register source 2 into a register dest
	public void add_temp_into_reg(String dstReg, TEMP src1, String src2Reg) {
		String src1Reg = tempToRegister(src1);
		add_registers(dstReg, src1Reg, src2Reg);
	}

	// NEW: Add operation using TEMP source 1 and register source 2 into a register dest
	public void add_temp_reg(String dstReg, TEMP src1, String src2Reg) {
		String src1Reg = tempToRegister(src1);
		add_registers(dstReg, src1Reg, src2Reg);
	}

	// NEW: Load global variable into TEMP
	public void load_global(TEMP dst, String varName) {
		String dstReg = tempToRegister(dst);
        String globalLabel = "global_" + varName;
		// Use TEMP_REG_1 ($s0) as a temporary register for the address
		String tempAddrReg = TEMP_REG_1;
		// 1. Load address of the global variable into the temporary register
		la(tempAddrReg, globalLabel);
		// 2. Load the word from the global variable address (in tempAddrReg) into dstReg
		lw_offset(dstReg, 0, tempAddrReg);
	}

	// NEW: Jump register using name
	public void jr_register(String targetReg) {
		appendCode(String.format("jr %s", targetReg));
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

	// Overload for specific register destination
	public void la(String dstReg, String label) {
		appendCode(String.format("la %s,%s", dstReg, label));
	}

	// NEW: Store TEMP relative to $sp
	public void sw_reg_sp(TEMP src, int offset) {
		sw_offset(tempToRegister(src), offset, SP);
	}

	// NEW: Load TEMP relative to $sp
	public void lw_reg_sp(TEMP dst, int offset) {
		lw_offset(tempToRegister(dst), offset, SP);
	}

	// NEW: Multiply operation using TEMP source 1 and register source 2 into a register destination
	public void mul_temp_reg(String dstReg, TEMP src1, String src2Reg) {
		String src1Reg = tempToRegister(src1);
		mul_registers(dstReg, src1Reg, src2Reg); // Use the existing mul_registers
	}

	// NEW: Multiply operation using register names
	public void mul_registers(String dstReg, String src1Reg, String src2Reg) {
		appendCode(String.format("mul %s,%s,%s", dstReg, src1Reg, src2Reg));
	}
}
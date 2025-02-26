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
	private int WORD_SIZE=4;
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	private StringBuilder dataContent = new StringBuilder();
	private StringBuilder textContent = new StringBuilder();

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
			finalWriter.print(dataContent.toString());
			
			finalWriter.print("\n.text\n");
			finalWriter.print(".globl main\n");
			finalWriter.print("main:\n");
			finalWriter.print("\tj Label_0_main_start\n"); // Jump to our actual code
			
			// Remove the "main:" line from textContent if it exists
			String textContentStr = textContent.toString();
			if (textContentStr.startsWith("main:\n")) {
				textContentStr = textContentStr.substring(6); // Remove "main:\n"
			}
			finalWriter.print(textContentStr);
			
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
	public void print_int(TEMP t)
	{
		String reg = tempToRegister(t);
		String instruction = String.format("\tmove $a0,%s\n", reg);
		textContent.append(instruction);
		
		instruction = "\tli $v0,1\n";
		textContent.append(instruction);
		
		instruction = "\tsyscall\n";
		textContent.append(instruction);
		
		instruction = "\tli $a0,32\n";
		textContent.append(instruction);
		
		instruction = "\tli $v0,11\n";
		textContent.append(instruction);
		
		instruction = "\tsyscall\n";
		textContent.append(instruction);
	}
	//public TEMP addressLocalVar(int serialLocalVarNum)
	//{
	//	TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
	//	int idx = t.getSerialNumber();
	//
	//	fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
	//	
	//	return t;
	//}
	public void allocate(String var_name)
	{
		String instruction = String.format("\tglobal_%s: .word 721\n", var_name);
		dataContent.append(instruction);
	}
	public void load(TEMP dst, String var_name) {
		String reg = tempToRegister(dst);
		String instruction = String.format("\tlw %s,global_%s\n", reg, var_name);
		textContent.append(instruction);
	}
	public void store(String var_name, TEMP src) {
		String reg = tempToRegister(src);
		String instruction = String.format("\tsw %s,global_%s\n", reg, var_name);
		textContent.append(instruction);
	}
	public void li(TEMP t,int value)
	{
		String reg = tempToRegister(t);
		String instruction = String.format("\tli %s,%d\n", reg, value);
		textContent.append(instruction);
	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		String dstReg = tempToRegister(dst);
		String src1Reg = tempToRegister(oprnd1);
		String src2Reg = tempToRegister(oprnd2);
		String instruction = String.format("\tadd %s,%s,%s\n", dstReg, src1Reg, src2Reg);
		textContent.append(instruction);
	}
    public void sub(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tsub %s,%s,%s\n", dstReg, src1Reg, src2Reg);
        textContent.append(instruction);
    }
	public void mul(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tmul %s,%s,%s\n", dstReg, src1Reg, src2Reg);
        textContent.append(instruction);
    }
    public void div(TEMP dst, TEMP oprnd1, TEMP oprnd2) {
        String dstReg = tempToRegister(dst);
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tdiv %s,%s,%s\n", dstReg, src1Reg, src2Reg);
        textContent.append(instruction);
    }
	public void label(String inlabel)
	{
		String instruction = String.format("%s:\n", inlabel);
		textContent.append(instruction);
	}	
	public void jump(String inlabel)
	{
		String instruction = String.format("\tj %s\n", inlabel);
		textContent.append(instruction);
	}	
	public void blt(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tblt %s,%s,%s\n", src1Reg, src2Reg, label);
        textContent.append(instruction);
    }
	public void bge(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tbge %s,%s,%s\n", src1Reg, src2Reg, label);
        textContent.append(instruction);
    }
	public void bne(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tbne %s,%s,%s\n", src1Reg, src2Reg, label);
        textContent.append(instruction);
    }
	public void beq(TEMP oprnd1, TEMP oprnd2, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String src2Reg = tempToRegister(oprnd2);
        String instruction = String.format("\tbeq %s,%s,%s\n", src1Reg, src2Reg, label);
        textContent.append(instruction);
    }
	public void beqz(TEMP oprnd1, String label) {
        String src1Reg = tempToRegister(oprnd1);
        String instruction = String.format("\tbeqz %s,%s\n", src1Reg, label);
        textContent.append(instruction);
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

	// Add this helper method to convert TEMP to register name
	private String tempToRegister(TEMP temp) {
		int regNum = IR.getInstance().getRegister(temp);
		if (regNum >= 0 && regNum < 10) {
			return "$t" + regNum;  // Use $t0 through $t9 for our registers
		}
		// If no register was allocated or spilled, use a default
		return "$t" + temp.getSerialNumber() % 10;
	}
}
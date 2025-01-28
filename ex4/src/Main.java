
import java.io.*;
import java.util.*; 

import java_cup.runtime.Symbol;
import AST.*;
import IR.*;
import SYMBOL_TABLE.SemanticException;

public class Main {
	static public void main(String argv[]) {
		Lexer l;
		Parser p;
		Symbol s;
		AST_PROGRAM AST;
		FileReader file_reader;
		PrintWriter file_writer;
		String inputFilename = argv[0];
		String outputFilename = argv[1];

		try {
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			file_reader = new FileReader(inputFilename);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			file_writer = new PrintWriter(outputFilename);

			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(file_reader);

			/*******************************/
			/* [4] Initialize a new parser */
			/*******************************/
			p = new Parser(l);

			/***********************************/
			/* [5] 3 ... 2 ... 1 ... Parse !!! */
			/***********************************/
			try {
				AST = (AST_PROGRAM) p.parse().value;
				AST.PrintMe();
				AST.SemantMeLog();
				AST.IRme();
				System.out.println(IR.getInstance().toString());
				IR.StaticAnalysis();
				if (IRcommand.exceptionVariables.isEmpty()) {
					file_writer.write("OK");
				}
				else {
        			List<String> sortedList = new ArrayList<>(IRcommand.exceptionVariables);
        			Collections.sort(sortedList);
        			for (String var : sortedList) {
        			    file_writer.write(var + "\n");
        			}
				}
			} catch (LexerError e) {
				System.out.println(e.getMessage());
				file_writer.write("ERROR");
			} catch (SemanticException e) {
				e.printStackTrace();
				file_writer.write("ERROR(" + (e.lineNumber + 1) + ")\n");
			} catch (Exception e) {
				e.printStackTrace();
				file_writer.write("ERROR(" + (p.errorLine) + ")\n");
			} catch (Throwable e) {
				file_writer.write("ERROR");
			}

			/*************************/
			/* [6] Print the AST ... */
			/*************************/

			/*************************/
			/* [7] Close output file */
			/*************************/
			file_writer.close();

			/*************************************/
			/* [8] Finalize AST GRAPHIZ DOT file */
			/*************************************/
			AST_GRAPHVIZ.getInstance().finalizeFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

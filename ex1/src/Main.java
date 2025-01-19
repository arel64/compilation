
import java.io.*;

import java_cup.runtime.Symbol;

public class Main {
	static public void main(String argv[]) {
		Lexer l;
		Symbol s;
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

			/***********************/
			/* [4] Read next token */
			/***********************/
			String output = "";
			try {

				s = l.next_token();

				/********************************/
				/* [5] Main reading tokens loop */
				/********************************/
				while (s.sym != TokenNames.EOF) {
					/************************/
					/* [6] Print to console */
					/************************/
					System.out.print("[");
					System.out.print(l.getLine());
					System.out.print(",");
					System.out.print(l.getTokenStartPosition());
					System.out.print("]:");
					System.out.print(s.value);
					System.out.print(" " + TokenNames.getTokenName(s.sym));
					System.out.print("\n");

					/*********************/
					/* [7] Print to file */
					/*********************/
					output += (TokenNames.getTokenName(s.sym));
					if (s.value != null) {
						output += ("(" + s.value + ")");
					}
					output += ("[");
					output += (l.getLine());
					output += (",");
					output += (l.getTokenStartPosition());
					output += ("]");
					output += ("\n");

					/***********************/
					/* [8] Read next token */
					/***********************/
					s = l.next_token();
				}
			} catch (Throwable e) {
				output = "ERROR";
			} finally {
				file_writer.print(output);
			}

			/******************************/
			/* [9] Close lexer input file */
			/******************************/
			l.yyclose();

			/**************************/
			/* [10] Close output file */
			/**************************/
			file_writer.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

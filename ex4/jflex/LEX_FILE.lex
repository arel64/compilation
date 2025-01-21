/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column
%state MULTI_LINE_COMMENT
/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               { return new Symbol(type, yyline, yycolumn); }
	private Symbol symbol(int type, Object value) { return new Symbol(type, yyline, yycolumn, value); }

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; } 
	public int getCharPos() { return yycolumn;   }
	public int parseIntToken()
	{
		String representation = yytext();
		if (representation.length() > 6)
		{
			//Prevent very big numbers from overflowing int
			throw new RuntimeException("Out of bounds integer");
		}
        int value = Math.abs(Integer.parseInt(representation));
		if (value <= 32767) {
            return value;
        } 
		throw new RuntimeException("Out of bounds integer");
	}
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator   = \r|\n|\r\n
WhiteSpace       = {LineTerminator} | [ \t]
INTEGER          = -?0|-?[1-9][0-9]*
STRING           = \"[a-zA-Z]*\"
ID               = [a-zA-Z_][a-zA-Z0-9_]*
TYPE_1_COMMENT 	 = \/\/[(|)|\[|\]|{|} | \?|\!|\.|\; | \+|\-|\*|\/ | [0-9] | [a-zA-Z_]]*{WhiteSpace}*
VALID_COMMENT_CHAR = [a-zA-Z0-9 \t\n\r\(\)\[\]\{\}\?\!\+\-\*/\.;]

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

< MULTI_LINE_COMMENT > {
    "*/"                    /* End of comment */ { yybegin(YYINITIAL); } 
    {VALID_COMMENT_CHAR} { } 
}

<YYINITIAL> {
"/*"			{ yybegin(MULTI_LINE_COMMENT); }
"+"             { return symbol(TokenNames.PLUS); }
"-"             { return symbol(TokenNames.MINUS); }
"*"             { return symbol(TokenNames.TIMES); }
"/"             { return symbol(TokenNames.DIVIDE); }
"("             { return symbol(TokenNames.LPAREN); }
")"             { return symbol(TokenNames.RPAREN); }
"["             { return symbol(TokenNames.LBRACK); }
"]"             { return symbol(TokenNames.RBRACK); }
"{"             { return symbol(TokenNames.LBRACE); }
"}"             { return symbol(TokenNames.RBRACE); }
","             { return symbol(TokenNames.COMMA); }
"."             { return symbol(TokenNames.DOT); }
";"             { return symbol(TokenNames.SEMICOLON); }
":="             { return symbol(TokenNames.ASSIGN); }
"="            { return symbol(TokenNames.EQ); }
"<"             { return symbol(TokenNames.LT); }
">"             { return symbol(TokenNames.GT); }
"nil"           { return symbol(TokenNames.NIL); }
"array"         { return symbol(TokenNames.ARRAY); }
"class"         { return symbol(TokenNames.CLASS); }
"extends"       { return symbol(TokenNames.EXTENDS); }
"return"        { return symbol(TokenNames.RETURN); }
"while"         { return symbol(TokenNames.WHILE); }
"if"            { return symbol(TokenNames.IF); }
"new"           { return symbol(TokenNames.NEW); }
"void"          { return symbol(TokenNames.TYPE_VOID); }
"int"           { return symbol(TokenNames.TYPE_INT); }
"string"        { return symbol(TokenNames.TYPE_STRING); }
{TYPE_1_COMMENT} { }
{INTEGER}       { return symbol(TokenNames.INT, parseIntToken()); }
{ID}            { return symbol(TokenNames.ID, yytext()); }
{STRING}        { return symbol(TokenNames.STRING, new String(yytext()));}
{WhiteSpace}    { /* skip whitespace */ }
<<EOF>>         { return symbol(TokenNames.EOF); }
}

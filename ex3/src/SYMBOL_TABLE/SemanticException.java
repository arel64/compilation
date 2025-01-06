package SYMBOL_TABLE;
public class SemanticException extends Exception {
    public int lineNumber;
    public SemanticException(int line,String error)
    {
        super(error);
        
        lineNumber = line;
    }
}

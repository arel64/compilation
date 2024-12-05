package AST;

public class AST_PARAM {
    public AST_TYPE paramType;
    public String paramName;

    public AST_PARAM(AST_TYPE paramType, String paramName) {
        this.paramType = paramType;
        this.paramName = paramName;
    }

    public void printMe() {
        System.out.printf("%s %s", paramType, paramName);
    }
}

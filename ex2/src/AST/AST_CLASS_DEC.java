package AST;

import java.util.List;

public class AST_CLASS_DEC extends AST_DEC {
    public String className;     // Name of the class
    public String parentClass;   // Parent class (null if no extends)
    public List<AST_CFIELD> fields;  // List of class fields (variables or methods)

    // Constructor for class declaration
    public AST_CLASS_DEC(String className, String parentClass, List<AST_CFIELD> fields) {
        this.className = className;
        this.parentClass = parentClass;
        this.fields = fields;
    }

    @Override
    public void printMe() {
        if (parentClass != null) {
            System.out.printf("CLASS %s EXTENDS %s\n", className, parentClass);
        } else {
            System.out.printf("CLASS %s\n", className);
        }
        System.out.println("FIELDS:");
        for (AST_CFIELD field : fields) {
            field.printMe();  // Assuming each field has a printMe method
        }
    }
}

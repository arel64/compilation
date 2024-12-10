package AST;


public class AST_CLASS_DEC extends AST_DEC {
      
    public String parentClassName;
    public AST_DEC_LIST fields; 

    public AST_CLASS_DEC(String className, String parentClass, AST_DEC_LIST fields) {
        super(className);
        this.parentClassName = parentClass;
        this.fields = fields;
    }

    @Override
    public void PrintMe() {
        super.PrintMe();
        if (parentClassName != null) {
            AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "Parent \n("+parentClassName+")"
            );     
        }
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,fields.SerialNumber);
        fields.PrintMe();
    }
}

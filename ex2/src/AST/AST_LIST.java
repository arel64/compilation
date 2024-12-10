package AST;

public class AST_LIST<T extends AST_Node> extends AST_Node{
    public java.util.ArrayList<T> list;
    private final Class<T> clazz;
    
    public AST_LIST(T first,Class<T> clazz) {
        this(null,first,clazz);
    }

    public AST_LIST(AST_LIST<T> prev, T next,Class<T> clazz) {
        this.clazz =clazz;
        SerialNumber = AST_Node_Serial_Number.getFresh();

        if(prev == null)
        {
            list = new java.util.ArrayList<T>();
        }
        else
        {
            list = new java.util.ArrayList<T>(prev.list);
        }
        
        if(next != null)
        {
            list.add(next);
        }
    }
    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "LIST<" + clazz.getSimpleName()+">" 
        );
        for (T dec : list) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,dec.SerialNumber);
            dec.PrintMe();
        }
    }
 
}

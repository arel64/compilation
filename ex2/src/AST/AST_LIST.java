package AST;

public class AST_LIST<T extends AST_Node> extends AST_Node{
    public java.util.ArrayList<T> list;

    
    public AST_LIST(T first) {
        list = new java.util.ArrayList<T>();
        if(first !=null)
        {
            list.add(first);
        }
        
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_LIST(AST_LIST<T> prev, T next) {
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
        for (T dec : list) {
            dec.PrintMe();
        }
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "LIST OF " + list.getClass().getCanonicalName()
        );
    }
 
}

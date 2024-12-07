package AST;

public class AST_LIST<T extends AST_Node> extends AST_Node{
    public java.util.ArrayList<T> list;

    
    public AST_LIST(T first) {
        list = new java.util.ArrayList();
        if(first !=null)
        {
            list.add(first);
        }
        
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_LIST(AST_LIST<T> prev, T next) {
        list = new java.util.ArrayList(prev.list);
        if(next != null)
        {
            list.add(next);
        }
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }
    @Override
    public void PrintMe() {
        System.out.printf("LIST OF\n %s",list.getClass().getCanonicalName());
        for (T dec : list) {
            dec.PrintMe();
        }
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "LIST OF " + list.getClass().getCanonicalName()
        );
    }
 
}

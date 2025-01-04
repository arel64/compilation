package AST;
import TYPES.*;

import java.util.ArrayList;
import java.util.List;

public class AST_LIST<T extends AST_Node> extends AST_Node{
    public java.util.ArrayList<T> list;
    private final Class<T> clazz;
    
    public AST_LIST(T first,Class<T> clazz) {
        this(null,first,clazz);
    }
    public AST_LIST(AST_LIST<T> prev, T next,Class<T> clazz) {
        this.clazz =clazz;
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
        
        SerialNumber = AST_Node_Serial_Number.getFresh();
        
    }
    private AST_LIST(List<T> list, Class<T> clazz)
    {
        this.list = new ArrayList<T>(list);
        this.clazz = clazz;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }
    @Override
    public void PrintMe() {
        if (list.isEmpty())
        {
            return;
        }
        int removeIndex = 0;
        T next = list.get(removeIndex);
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,next.SerialNumber);
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "LIST<" + clazz.getSimpleName()+">" 
        );
        if(removeIndex != list.size() - 1)
        {
            
            List<T> newList = list.subList(removeIndex + 1 , list.size());
            AST_LIST<T> nestedASTList = new AST_LIST<T>(newList,clazz);
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,nestedASTList.SerialNumber);
            nestedASTList.PrintMe();
        }
        next.PrintMe();
    }
    @Override
    public String toString() {
        if (this.list.isEmpty()) {
            return "";
        }
        String listRepresentation = "";
        for(T dec : this.list)
        {
            listRepresentation += dec.toString()+",";
        }
        return listRepresentation.substring(0,listRepresentation.length()-1);
    }

    @Override
    public TYPE SemantMe(){
        if (this.list.isEmpty()) {
            return null;
        }
        TYPE_LIST result = null;
        for (int i = this.list.size() - 1; i >= 0; i--) {
            T node = this.list.get(i);
            TYPE currType = node.SemantMe();  // Get the TYPE of the current node
            if (currType == null) {
                // Handle the error case where the SemantMe of the node returns null
                throw new SemanticException("WTF");
            }
            result = new TYPE_LIST(currType, result);
        }
        return result;
    }
}

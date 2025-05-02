package AST;
import TYPES.*;
import TEMP.*;
import IR.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class AST_LIST<T extends AST_Node> extends AST_Node implements Iterable<T>{
    private ArrayList<T> list;
    private final Class<T> clazz;
    public AST_LIST(T first, Class<T> clazz) {
        this(null, first, clazz);
    }
    public AST_LIST(AST_LIST<T> prev, T end, Class<T> clazz) {
        this.clazz = clazz;
        if (prev == null) {
            list = new ArrayList<>();
        } else {
            list = new ArrayList<>(prev.list);
        }
        if (end != null) {
            list.add(end);
        }
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }
    private AST_LIST(List<T> list, Class<T> clazz) {
        this.list = new ArrayList<>(list);
        this.clazz = clazz;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }
    public T at(int index){
        return list.get(index);
    }
    public void add(T item){
        list.add(item);
    }
    public int size(){
        return list.size();
    }
    public AST_LIST<T> from(int index){
        List<T> subList = list.subList(index, list.size());
        return new AST_LIST<>(subList, clazz);
    }
    @Override
    public void PrintMe() {
        if (list.isEmpty()) {
            return;
        }
        int removeIndex = 0;
        T next = list.get(removeIndex);
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, next.SerialNumber);
        AST_GRAPHVIZ.getInstance().logNode(SerialNumber, "LIST<" + clazz.getSimpleName()+">");
        if(removeIndex != list.size() - 1) {
            List<T> newList = list.subList(removeIndex + 1, list.size());
            AST_LIST<T> nestedASTList = new AST_LIST<>(newList, clazz);
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, nestedASTList.SerialNumber);
            nestedASTList.PrintMe();
        }
        next.PrintMe();
    }
    @Override
    public String toString() {
        if (list.isEmpty()) {
            return "";
        }
        String listRepresentation = "";
        for(T dec : list) {
            listRepresentation += dec.toString() + ",";
        }
        return listRepresentation.substring(0, listRepresentation.length() - 1);
    }
    @Override
    public TYPE_LIST SemantMe() throws SemanticException {
        throw new SemanticException(lineNumber, "this causes bugs iterate properly");
    }
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
    @Override
    public TEMP IRme()
	{
        for (int i = 0; i < list.size(); i++) {
            T node = list.get(i);
            if (node != null) {
                node.IRme();
            }
        }
		
		return null;
	}
}

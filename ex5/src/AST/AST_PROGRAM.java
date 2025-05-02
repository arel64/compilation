package AST;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_PROGRAM extends AST_Node {
    public AST_LIST<AST_DEC> declerationList;

    public AST_PROGRAM(AST_LIST<AST_DEC> declerationList)
    {
        SerialNumber = AST_Node_Serial_Number.getFresh();
        this.declerationList = declerationList;
    }

    @Override
    public void PrintMe()
    {

        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "PROGRAM"
        );
        if (declerationList != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, declerationList.SerialNumber);
            declerationList.PrintMe();
        }
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        if (declerationList.size() == 0) {
            return new TYPE_LIST();
        }
        for (int i = 0; i < declerationList.size(); i++) {
            AST_DEC node = declerationList.at(i);
            node.SemantMeLog();
        }
        return null;
    }

    @Override
    public TEMP IRme()
	{
        IR.addPrintIntIR();
        IR.addPrintStringIR();
        return declerationList.IRme();
	}
}

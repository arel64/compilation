package AST;
import TEMP.*;
import IR.*;

public class AST_STMT_WHILE extends AST_STMT_CONDITIONAL {

    public AST_STMT_WHILE(AST_EXP condition, AST_LIST<AST_STMT> body) {
        super(condition,body);
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    
    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,getBody().SerialNumber);
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "WHILE("+getCondition()+")");
        getBody().PrintMe();
    }

    @Override
    public TEMP IRme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end   = IRcommand.getFreshLabel("while_end");
		String label_start = IRcommand.getFreshLabel("while_start");
	
		/*********************************/
		/* [2] entry label for the while */
		/*********************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Label(label_start));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		TEMP cond_temp = condition.IRme();

		/******************************************/
		/* [4] Jump conditionally to the loop end */
		/******************************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Jump_If_Eq_To_Zero(cond_temp,label_end));		

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.IRme();

		/******************************/
		/* [6] Jump to the loop entry */
		/******************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Jump_Label(label_start));		

		/**********************/
		/* [7] Loop end label */
		/**********************/
		IR.getInstance().Add_IRcommand(new IRcommand_Label(label_end));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}
}

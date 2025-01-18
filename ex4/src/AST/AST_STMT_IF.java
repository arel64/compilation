package AST;
import TEMP.*;
import IR.*;

public class AST_STMT_IF extends AST_STMT_CONDITIONAL
{
	public AST_STMT_IF(AST_EXP condition, AST_LIST<AST_STMT> body) {
        super(condition,body);
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    @Override
    public void PrintMe() {
        getCondition().PrintMe();
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            "IF("+getCondition()+")");
        getBody().PrintMe();
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,getBody().SerialNumber);
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,getCondition().SerialNumber);
    }

    @Override
    public TEMP IRme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end   = IRcommand.getFreshLabel("end");
		String label_start = IRcommand.getFreshLabel("start");
	
		/*********************************/
		/* [2] entry label for the if    */
		/*********************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Label(label_start));

		/********************/
		/* [3] cond.IRme(); */
		/********************/
		TEMP cond_temp = cond.IRme();

		/***********************************************/
		/* [4] Jump after the if block if cond is zero */
		/***********************************************/
		IR.getInstance().Add_IRcommand(new IRcommand_Jump_If_Eq_To_Zero(cond_temp,label_end));		

		/*******************/
		/* [5] body.IRme() */
		/*******************/
		body.IRme();	

		/**********************/
		/* [7] If end label */
		/**********************/
		IR.getInstance().Add_IRcommand(new IRcommand_Label(label_end));

		/*******************/
		/* [8] return null */
		/*******************/
		return null;
	}
}
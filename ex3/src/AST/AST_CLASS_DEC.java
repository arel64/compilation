package AST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
public class AST_CLASS_DEC extends AST_DEC {
      
    public String parentClassName;
    public AST_LIST<AST_CLASS_FIELDS_DEC> fields; 

    public AST_CLASS_DEC(String className, String parentClass, AST_LIST<AST_CLASS_FIELDS_DEC> fields) {
        super(className);
        this.parentClassName = parentClass;
        this.fields = fields;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
            SerialNumber,
            toString()
        );     
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,fields.SerialNumber);
        fields.PrintMe();
    }
    @Override
    public String toString() {
        String represtation = String.format("class %s",super.getName());
        if (parentClassName != null)
        {
            represtation += " extends " + parentClassName;
        }
        return represtation;
    }
    private boolean isParentClassValid() throws SemanticException
    {
        if (parentClassName == null)
        {
            return true;
        }      
        SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
        TYPE parent = symbol_table.getTypeInGlobalScope(parentClassName);
        return parent != null;
        
		
    }
    @Override
	public TYPE SemantMe() throws SemanticException{
        System.out.println("Creating class for " + getName() + lineNumber);

		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		if (!symbol_table.isAtGlobalScope()){
			throw new SemanticException(lineNumber,"Scope mismatch found scope:" +symbol_table.getCurrentScopeIndex());
		}
		if(!isParentClassValid())
        {
            throw new SemanticException(lineNumber,"Specified extends class "+ parentClassName+" not found " + getName());
        }
		TYPE_CLASS currentClass = new TYPE_CLASS(parentClassName, this.getName(),lineNumber);
        SYMBOL_TABLE.getInstance().enter(getName(),currentClass);
        symbol_table.beginScope();
        /**** Add to scope all fields ***/
        /** Using isoverriding and *some* of extend */
        fields.SemantMeLog();
        symbol_table.endScope();
		return currentClass;  
	}
    // private void checkForShadowing(TYPE_CLASS_VAR_DEC_LIST currentDataMembers, TYPE_CLASS father) throws SemanticException
	// {
	// 	while (father != null)
	// 	{
	// 		TYPE_CLASS_VAR_DEC_LIST ancestorDataMembers = father.data_members;
	// 		for (TYPE_CLASS_VAR_DEC currentMember : currentDataMembers)
	// 		{
	// 			for (TYPE_CLASS_VAR_DEC ancestorMember : ancestorDataMembers)
	// 			{
	// 				if (currentMember.name.equals(ancestorMember.name))
	// 				{
	// 					// System.out.println(currentMember);
	// 					// System.out.println(currentMember.t);
	// 					// System.out.println("why is this not a funciton for god  "+currentMember.isFunction());

	// 					// System.out.println("this is the print from the function check shadow");

	// 					if (currentMember.isFunction() && ancestorMember.isFunction())  //overriding is ok
	// 						continue;
	// 					if (!currentMember.isFunction() && !ancestorMember.isFunction())
	// 					{
	// 						throw new SemanticException(currentMember.line,String.format(
	// 							"Shadowing detected: Variable '%s' in class '%s' shadows variable in ancestor class '%s'.",
	// 							currentMember.name, this.name, father.name
	// 						));
	// 					}
	// 					throw new SemanticException(currentMember.line,String.format(
	// 						"Conflict detected: '%s' in class '%s' conflicts with '%s' in ancestor class '%s'.",
	// 						currentMember.name, this.name, ancestorMember.name, father.name
	// 					));
	// 				}
	// 			}
	// 		}
	// 		father = father.father;
	// 	}
	// }
}

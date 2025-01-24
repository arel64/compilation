package AST;

import java.util.ArrayList;
import java.util.List;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
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

		SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
		if (!symbol_table.isAtGlobalScope()){
			throw new SemanticException(lineNumber,"Scope mismatch found scope:" +symbol_table.getCurrentScopeIndex());
		}
        if(isDeclaredInCurrentScope())
        {
            throw new SemanticException(lineNumber, String.format("Cannot declare class %s was already declared", getName()));
        }
		if(!isParentClassValid())
        {
            throw new SemanticException(lineNumber,"Specified extends class "+ parentClassName+" not found " + getName());
        }
        TYPE_CLASS father = (TYPE_CLASS)symbol_table.getTypeInGlobalScope(parentClassName);
		TYPE_CLASS currentClass = new TYPE_CLASS(parentClassName, getName(),lineNumber);
        SYMBOL_TABLE.getInstance().enter(getName(),currentClass);
        symbol_table.beginScope();
        TYPE_CLASS_VAR_DEC_LIST parentAttributes = null;
        List<String> potenitianlyOverridenFunctions = new ArrayList<>();
        TYPE_CLASS_VAR_DEC_LIST declist = new TYPE_CLASS_VAR_DEC_LIST(potenitianlyOverridenFunctions);

        if(father != null && father.getDataMembers() != null)
        {
            parentAttributes = father.getDataMembers();
            for(TYPE_CLASS_FIELD field : parentAttributes)
            {
                if(field.t.isFunction()){
                    boolean isAdded = false;
                    for(AST_CLASS_FIELDS_DEC myfield : fields)
                    {
                        if(myfield.varName.equals(field.getName()))
                        {
                            potenitianlyOverridenFunctions.add(field.getName());
                            isAdded =true;
                            break;
                        }
                    }
                    if(isAdded)
                    {
                        continue;
                    }
                }
                SYMBOL_TABLE.getInstance().enter(field.getName(), field.t);
                declist.add(field);
                currentClass.setDataMembers(declist);
                
            }
        }        
        for(AST_CLASS_FIELDS_DEC myField : fields)
        {
            TYPE myFieldType = myField.SemantMe();
            declist.add(new TYPE_CLASS_FIELD(myField.getName(),myFieldType,myField.lineNumber));

            if(parentAttributes != null)
            {
                TYPE_CLASS_FIELD previousFieldDeclaration = parentAttributes.get(myField.getName());
                if( previousFieldDeclaration != null)
                {
                    if(!(myFieldType.isFunction() && previousFieldDeclaration.t.isFunction() &&
                             ((TYPE_FUNCTION)myFieldType).isOverriding((TYPE_FUNCTION)previousFieldDeclaration.t)))
                    {
                        throw new SemanticException(myField.lineNumber,String.format("Function redeclared %s but does not override %s", myFieldType, previousFieldDeclaration));
                    }
                }
            }
            SYMBOL_TABLE.getInstance().enter(myField.getName(), myFieldType);
            currentClass.setDataMembers(declist);
        }
        symbol_table.endScope();
		return currentClass;  
	}

    @Override
	public TEMP IRme()
	{
	
		return null;
	}
}

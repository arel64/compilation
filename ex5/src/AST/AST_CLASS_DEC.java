package AST;

import java.util.ArrayList;
import java.util.List;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.ScopeType;
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
        if(isDeclaredInCurrentScope()) {
            throw new SemanticException(lineNumber, String.format("Cannot declare class %s: already declared", getName()));
        }
		if(!isParentClassValid()) {
            throw new SemanticException(lineNumber,"Specified extends class \""+ parentClassName+"\" not found for class " + getName());
        }
        
        TYPE_CLASS father = (parentClassName != null) ? (TYPE_CLASS)symbol_table.getTypeInGlobalScope(parentClassName) : null;
		TYPE_CLASS currentClass = new TYPE_CLASS(father, getName(),lineNumber); // Pass father TYPE_CLASS
        SYMBOL_TABLE.getInstance().enter(getName(),currentClass); // Enter class type itself
                
        TYPE_CLASS_VAR_DEC_LIST memberList = new TYPE_CLASS_VAR_DEC_LIST(); // Final list for this class

        // Handle Inheritance
        if(father != null && father.getDataMembers() != null) {
            for(TYPE_CLASS_FIELD parentField : father.getDataMembers()) {
                // Create a new instance to avoid modifying father's list
                TYPE_CLASS_FIELD inheritedField = new TYPE_CLASS_FIELD(parentField.getName(), parentField.t, parentField.line);
                memberList.add(inheritedField);
            }
        }        
        SYMBOL_TABLE.getInstance().beginScope(ScopeType.CLASS);
        // Process Current Class Declarations
        for(AST_CLASS_FIELDS_DEC myFieldAST : fields) {
            TYPE myFieldType = myFieldAST.SemantMe();
            String myFieldName = myFieldAST.getName();

            TYPE_CLASS_FIELD existingMember = memberList.get(myFieldName);
            TYPE_CLASS_FIELD myNewMember = new TYPE_CLASS_FIELD(myFieldName, myFieldType, myFieldAST.lineNumber);

            if (existingMember != null) { // Member with this name exists (potentially inherited)
                if (myFieldType.isFunction() && existingMember.t.isFunction()) {
                    // Method Overriding Check
                    TYPE_FUNCTION existingMethodType = (TYPE_FUNCTION) existingMember.t;
                    TYPE_FUNCTION overridingMethodType = (TYPE_FUNCTION) myFieldType;
                    if (!overridingMethodType.isOverriding(existingMethodType)) {
                         throw new SemanticException(myFieldAST.lineNumber, String.format("Method '%s' in class '%s' does not correctly override the version in a parent class.", myFieldName, getName()));
                    }
                    // Valid override: Update the type in the list, keep the VMT offset
                    existingMember.t = overridingMethodType; // Update type
                    // Offset (VMT index) remains the same as parent
                } else {
                    // Illegal redeclaration (field hiding field, method hiding field, field hiding method)
                    throw new SemanticException(myFieldAST.lineNumber, String.format("Cannot redeclare member '%s' in class '%s'. Illegal shadowing or type mismatch.", myFieldName, getName()));
                }
            } else {
                memberList.add(myNewMember);
            }
            // Enter the member's name and its *TYPE* into the symbol table for the current class scope.
            // The offset information is stored within the TYPE_CLASS's member list.
            SYMBOL_TABLE.getInstance().enter(myFieldName, myFieldType);
            int offset = SYMBOL_TABLE.getInstance().findEntryInCurrentScopeStack(myFieldName).offset;
            myNewMember.offset = offset;
        }
        SYMBOL_TABLE.getInstance().endScope();
        // Set the final calculated list and size
        currentClass.setDataMembers(memberList);
        
		return currentClass;  
	}

    @Override
	public TEMP IRme()
	{
        // Retrieve the TYPE_CLASS info which includes calculated offsets
        TYPE selfType = SYMBOL_TABLE.getInstance().find(getName());
        if (!(selfType instanceof TYPE_CLASS)) {
                System.err.printf("IR Error: Could not find TYPE_CLASS for %s during IR generation.\n", getName());
                return null; // Should not happen if SemantMe succeeded
        }
        TYPE_CLASS classType = (TYPE_CLASS) selfType;

        // --- Generate VMT --- 
        // 1. Collect method labels in VMT order
        // Assumes memberList is already sorted by VMT offset for methods due to SemantMe logic
        List<String> vmtMethodLabels = new ArrayList<>();
        if (classType.getDataMembers() != null) {
                // Need to sort members by method offset (VMT index) to ensure correct VMT layout
                List<TYPE_CLASS_FIELD> sortedMethods = new ArrayList<>();
                for (TYPE_CLASS_FIELD member : classType.getDataMembers()) {
                    if (member.t.isFunction()) {
                        sortedMethods.add(member);
                    }
                }
                // Sort based on the stored VMT index (offset)
                sortedMethods.sort((m1, m2) -> Integer.compare(m1.offset, m2.offset));

                for (TYPE_CLASS_FIELD methodField : sortedMethods) {
                    // Construct the expected label for the method implementation
                    // Ensure AST_FUNC_DEC uses the same convention!
                    String methodLabel = String.format("%s_%s", classType.getName(), methodField.getName()); 
                    vmtMethodLabels.add(methodLabel);
                }
        }

        // 2. Add IR command to create the VMT in .data section
        IR.getInstance().Add_IRcommand(new IRcommand_Class_Dec(classType.getName(), vmtMethodLabels));

        // --- Generate IR for method bodies --- 
        if (fields != null) {
            fields.IRme(); // Calls IRme on each AST_FUNC_DEC and AST_VAR_DEC in the class
        }

        
        return null;
	}
}

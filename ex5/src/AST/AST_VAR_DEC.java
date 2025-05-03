package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;

public class AST_VAR_DEC extends AST_DEC {

    public AST_TYPE t;
    public AST_EXP varValue;
    public String varName;

    public AST_VAR_DEC(String varName, AST_TYPE varType, AST_EXP initialValue) {
        super(varName);
        this.varName = varName;
        this.t = varType;
        this.varValue = initialValue;

    }

    public AST_VAR_DEC(String varName, AST_TYPE varType) {
        this(varName, varType, null);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "AST_VAR_DEC " + this.toString());
    }

    @Override
    public String toString() {
        return t.toString() + " " + getName() + (varValue != null ? "=" + varValue : "") + "offset: " + offset
                + "isGlobal: " + isGlobal;
    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        String currentVarName = getName(); // Store name for clarity
        System.out.println("--- Checking variable declaration for: " + currentVarName + " ---");
        boolean exists = SYMBOL_TABLE.getInstance().isDeclaredInImmediateScope(currentVarName);
        if (exists) // Check the stored result
        {
            System.out.println("--- ERROR: Variable already exists in current scope! ---");
            throw new SemanticException(lineNumber,
                    String.format("Cannot declare %s was already declared in this scope", currentVarName));
        }
        TYPE type = t.SemantMeLog();
        if (type.isPrimitive()) {
            type = new TYPE_VAR_DEC(type, getName());
        }
        SYMBOL_TABLE.getInstance().enter(getName(), type);

        // Retrieve the entry that was just created to get its calculated offset
        SYMBOL_TABLE_ENTRY entry = SYMBOL_TABLE.getInstance().findEntryInCurrentScopeStack(getName());
        System.out.println("Printing the entry " + entry);
        if (entry != null) {
            this.offset = entry.offset; // Store the correct offset
            this.isGlobal = entry.isGlobal; // Use the flag from the entry
            // +++ DEBUG LOGGING +++
            // System.out.format("SemantMe: Retrieved for '%s': offset=%d, isGlobal=%b\\n",
            // getName(), this.offset, this.isGlobal);
        } else {
            // This indicates a serious internal error
            System.err.println("FATAL Semantic Error: Could not find symbol table entry for '" + getName()
                    + "' immediately after entering.");
            this.offset = Integer.MIN_VALUE; // Assign default error value
            this.isGlobal = false; // Assign default error value
            // Consider throwing an exception here instead of just printing
            throw new SemanticException(lineNumber,
                    "Internal compiler error: Symbol table state inconsistency for variable " + getName());
        }

        if (varValue != null) {
            TYPE valueType = varValue.SemantMeLog();
            System.out.println("valueType: " + valueType);
            if (!type.isAssignable(valueType)) {
                throw new SemanticException(lineNumber,
                        String.format("Initial value %s does not match type %s", valueType, type));
            }
        }
        return type;
    }

    @Override
    public TEMP IRme() {
        if (this.isGlobal) {
            IR.getInstance().Add_IRcommand(new IRcommand_Allocate(getName()));
        }

        if (this.varValue != null) {
            TEMP initValTemp = this.varValue.IRme();
            // +++ DEBUG LOGGING +++
            System.out.format("IRme AST_VAR_DEC '%s': Using offset=%d, isGlobal=%b for store/init\\n", getName(),
                    this.offset, this.isGlobal);
            if (this.isGlobal) {
                IR.getInstance().Add_IRcommand(new IRcommand_Global_Init_Store(getName(), initValTemp));
            } else {
                if (this.offset == Integer.MIN_VALUE) {
                    System.err.println(
                            "ERROR: Local variable '" + getName() + "' offset is MIN_VALUE during IRme store!");
                    // Potentially throw exception here
                }
                IR.getInstance().Add_IRcommand(new IRcommand_Store(initValTemp, this.offset, getName()));
            }
        }
        return null;
    }
}
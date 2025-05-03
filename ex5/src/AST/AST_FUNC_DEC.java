package AST;

import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.ScopeType;

public class AST_FUNC_DEC extends AST_CLASS_FIELDS_DEC {

    public AST_TYPE returnType;
    public AST_LIST<AST_VAR_DEC> params;
    public AST_LIST<AST_STMT> body;
    public String funcName;

    public AST_FUNC_DEC(String funcName, AST_TYPE returnType, AST_LIST<AST_VAR_DEC> params, AST_LIST<AST_STMT> body) {
        super(funcName, returnType);
        this.returnType = returnType;
        this.params = params;
        this.body = body;

    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "FUNC_DECLARATION\n " + returnType.type + " " + this.getName() + "(" + params + ")");

        if (params != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, params.SerialNumber);
            params.PrintMe();
        }
        if (body != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, body.SerialNumber);
            body.PrintMe();
        }
    }

    @Override
    public TYPE_FUNCTION SemantMe() throws SemanticException {
        if (SYMBOL_TABLE.getInstance().isDeclaredInImmediateScope(getName())) {
            throw new SemanticException(lineNumber, String.format("Cannot redeclare function %s", getName()));
        }
        if (!SYMBOL_TABLE.getInstance().isInClassScope() && !SYMBOL_TABLE.getInstance().isAtGlobalScope()) {
            throw new SemanticException(lineNumber, "Cannot declare function in non-class scope");
        }
        TYPE returnT = returnType.SemantMeLog();
        if (returnT == null) {
            throw new SemanticException(lineNumber, "Null not good");
        }
        SYMBOL_TABLE instance = SYMBOL_TABLE.getInstance();
        TYPE_FUNCTION t = new TYPE_FUNCTION(returnT, getName(), lineNumber);
        this.funcName = instance.enter(t.getName(), (TYPE) t);
        this.offset = instance.getOffset(this.funcName);
        instance.beginScope(ScopeType.FUNCTION);
        instance.beginScope(ScopeType.PARAMS);
        TYPE_LIST list = new TYPE_LIST();
        if (params != null) {
            for (AST_DEC param : params) {
                TYPE paramType = param.SemantMe();
                list.add(paramType, param.lineNumber);
            }
        }
        instance.beginScope(ScopeType.BODY);
        t.setParams(list);
        if (body == null) {
            instance.endScope();
            return t;
        }
        for (AST_STMT statement : body) {
            TYPE statementType = null;
            statementType = statement.SemantMe();

            if (statement instanceof AST_STMT_RETURN) {
                validateReturnType((TYPE_RETURN) statementType, new TYPE_RETURN(returnT), statement.lineNumber);
            }
            if (statement instanceof AST_STMT_CONDITIONAL) {
                TYPE_LIST typeList = (TYPE_LIST) statementType;
                validateTypeListReturnType(typeList, new TYPE_RETURN(returnT));
            }

        }
        instance.endScope();
        instance.endScope();
        instance.endScope();
        return t;
    }

    private void validateReturnType(TYPE_RETURN statementType, TYPE_RETURN returnType, int lineNumber)
            throws SemanticException {
        if (!returnType.isAssignable(statementType)) {

            throw new SemanticException(lineNumber, String
                    .format("you cannot assign %s to %s and thus is invalid return type", statementType, returnType));
        }
    }

    private void validateTypeListReturnType(TYPE_LIST list, TYPE_RETURN returnType) throws SemanticException {
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            TYPE innerStatementType = list.get(i);
            if (innerStatementType instanceof TYPE_RETURN) {
                validateReturnType((TYPE_RETURN) innerStatementType, returnType, list.getLineNumber(i));
            }
            if (innerStatementType instanceof TYPE_LIST) {
                validateTypeListReturnType((TYPE_LIST) innerStatementType, returnType);
            }
        }
    }

    @Override
    public TEMP IRme() {
        IR ir = IR.getInstance();
        String funcName = this.funcName;

        // Built-in functions handled elsewhere
        if (funcName.equals("PrintInt") || funcName.equals("PrintString")) {
            return null;
        }
        String label = IR.getInstance().getFunctionLabel(funcName);
        System.out.println("The label is " + label + " for " + funcName);
        String label_end = null;
        String label_start = null;
        if (label == null) {
            label = IRcommand.getFreshLabel(funcName);
        }
        label_end = label + "_end";
        label_start = label + "_start";
        ir.registerFunctionLabel(funcName, label_start);
        ir.pushFunctionEndLabel(label_end);
        int numberOfLocals = countLocalDeclarations(body);
        int localsSize = numberOfLocals * 4; // Assuming 4 bytes per local/pointer
        int frameSize = localsSize + 8; // 8 bytes for saved $fp and $ra
        ir.Add_IRcommand(new IRcommand_Label(label_start));
        ir.Add_IRcommand(new IRcommand_Prologue(frameSize));

        // Generate IR for the function body. Load/Store commands within will use the
        // offsets
        // previously assigned and stored in the symbol table entries.
        if (body != null) {
            body.IRme(); // This call no longer needs/uses offset parameters
        }

        // Emit function end label and epilogue
        ir.Add_IRcommand(new IRcommand_Label(label_end));
        ir.Add_IRcommand(new IRcommand_Epilogue(frameSize));

        ir.popFunctionEndLabel();

        return null; // Function declaration itself doesn't produce a value TEMP
    }

    private int countLocalDeclarations(AST_LIST<? extends AST_STMT> statements) {
        int count = 0;
        if (statements == null)
            return 0;

        for (AST_STMT stmt : statements) {
            if (stmt instanceof AST_STMT_VAR_DECL) {
                // Count every variable declaration statement
                count++;
            } else if (stmt instanceof AST_STMT_IF) {
                // Assume AST_STMT_IF has field 'body'
                AST_STMT_IF ifStmt = (AST_STMT_IF) stmt;
                count += countLocalDeclarations(ifStmt.body);
                // Removed else branch counting as it's not supported
            } else if (stmt instanceof AST_STMT_WHILE) {
                // Assume AST_STMT_WHILE has field 'body'
                AST_STMT_WHILE whileStmt = (AST_STMT_WHILE) stmt;
                count += countLocalDeclarations(whileStmt.body);
            }
            // Add other compound statements if necessary
        }
        return count;
    }
}

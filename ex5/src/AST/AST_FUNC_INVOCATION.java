package AST;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.ScopeType;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.IR;
import IR.IRcommand_Func_Call;
import IR.IRcommand_Class_Method_Call;
import java.util.ArrayList;

public class AST_FUNC_INVOCATION extends AST_EXP {

    public String funcName;
    public AST_LIST<AST_EXP> params;
    public AST_VAR var;
    public int methodOffset;
    private TYPE_CLASS resolvedClassType = null;

    public AST_FUNC_INVOCATION(AST_VAR var, String funcName, AST_LIST<AST_EXP> params) {
        this.funcName = funcName;
        this.params = params;
        this.var = var;
        SerialNumber = AST_Node_Serial_Number.getFresh();
    }

    public AST_FUNC_INVOCATION(String funcName, AST_LIST<AST_EXP> params) {
        this(null, funcName, params);
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                String.format("FUNC_INVO\n %s", toString()));
        if (var != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, var.SerialNumber);
            var.PrintMe();
        }
        if (params != null) {
            AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, params.SerialNumber);
            params.PrintMe();
        }
    }

    @Override
    public String toString() {
        String varRepresentation = var != null ? var.toString() + "." : "";
        String paramsRepresentation = params != null ? ((params.size() == 0) ? "" : params.toString()) : "";
        return String.format("%s%s(%s)", varRepresentation, funcName, paramsRepresentation);

    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        SYMBOL_TABLE table = SYMBOL_TABLE.getInstance();

        TYPE varType = null;
        if (var != null) {
            varType = var.SemantMe();
        }
        if (varType != null && !varType.isClass()) {
            throw new SemanticException(lineNumber, String.format("%s does not exist and cannot be invoked", varType));
        }

        this.resolvedClassType = (TYPE_CLASS) varType;
        String baseFuncName = funcName;
        String qualifiedFuncName = null;
        TYPE functionType = null;
        if (resolvedClassType == null) {
            String className = SYMBOL_TABLE.getInstance().getInScopeClass();
            String funcNameWithClass = null;
            if (className != null) {
                funcNameWithClass = SYMBOL_TABLE.getClassFunctionName(className, baseFuncName);
                functionType = table.find(funcNameWithClass);
            }
            if (functionType != null) {
                this.resolvedClassType = (TYPE_CLASS) SYMBOL_TABLE.getInstance().find(className);
                qualifiedFuncName = funcNameWithClass;
            } else {
                functionType = table.find(baseFuncName);
                qualifiedFuncName = baseFuncName;
            }
        } else {
            TYPE_CLASS varClass = (TYPE_CLASS) varType;
            functionType = varClass.getDataMember(baseFuncName).t;
        }
        if (functionType == null) {
            throw new SemanticException(lineNumber, String.format("%s does not exist and cannot be invoked",
                    baseFuncName));
        }

        if (!(functionType instanceof TYPE_FUNCTION)) {
            throw new SemanticException(lineNumber, String.format("%s cannot be used like a function",
                    qualifiedFuncName));
        }
        TYPE_FUNCTION myFunctionType = (TYPE_FUNCTION) functionType;
        System.out.println("resolvedClassType: " + resolvedClassType + " " + baseFuncName + " " + qualifiedFuncName);
        if (this.resolvedClassType != null) {
            this.methodOffset = resolvedClassType.getDataMember(baseFuncName).offset;
        }

        table.beginScope(ScopeType.FUNCTION);
        if (params != null) {
            if (params.size() != myFunctionType.getParams().size()) {
                throw new SemanticException(lineNumber,
                        String.format("incorrect function %s invocation, number of parameters", qualifiedFuncName));
            }
            for (int i = 0; i < params.size(); i++) {
                TYPE expType = params.at(i).SemantMeLog();
                TYPE param = myFunctionType.getParam(i);
                if (!param.isInterchangeableWith(expType)) {
                    throw new SemanticException(lineNumber, String.format(
                            "incorrect function %s invocation for value  param %s=%s ",
                            qualifiedFuncName, param, expType));
                }
            }
        }
        table.endScope();
        return myFunctionType.getReturnType();

    }

    @Override
    public TEMP IRme() {
        ArrayList<TEMP> paramsTemp = new ArrayList<TEMP>();
        if (params != null) {
            for (AST_EXP param : params) {
                paramsTemp.add(param.IRme());
            }
        }
        TEMP dst = TEMP_FACTORY.getInstance().getFreshTEMP();

        if (resolvedClassType == null) { // Global function call
            System.out.printf("IRme: Adding IRcommand_Func_Call for %s\n", funcName);
            IR.getInstance().Add_IRcommand(new IRcommand_Func_Call(dst, funcName, paramsTemp));
        } else { // Class method call (explicit or implicit)
            TEMP objAddrTemp = null; // Initialize to null

            if (var != null) { // Explicit call: var.funcName()
                System.out.printf("IRme: Generating explicit method call IR for %s.%s\n", var.toString(), funcName);
                objAddrTemp = var.IRme();
                // Robustness: Check if var.IRme() returned a valid TEMP
                if (objAddrTemp == null) {
                    System.err.printf("IR Error(ln %d): Base object expression for %s.%s did not yield a TEMP.\n",
                            lineNumber, var.toString(), funcName);
                    return null; // Or handle error appropriately
                }
                // Consider adding IR for null pointer check on objAddrTemp here if needed
                // IR.getInstance().Add_IRcommand(new
                // IRcommand_Check_Null_Pointer(objAddrTemp));
            } else { // Implicit call: funcName() inside a method
                // Signal implicit call by leaving objAddrTemp as null.
                // The actual loading of 'this' from 0($fp) will be handled by
                // IRcommand_Class_Method_Call.MIPSme()
                objAddrTemp = TEMP_FACTORY.getInstance().getFreshTEMP();
                System.out.printf("IRme: Signaling implicit 'this' method call IR for %s (objAddrTemp=null)\n",
                        funcName);
            }

            // Check if method offset is valid (should be set in SemantMe)
            if (this.methodOffset < 0) {
                System.err.printf("IR Error(ln %d): Invalid method offset (%d) for %s.\n", lineNumber,
                        this.methodOffset, funcName);
                return null;
            }

            // Generate the call command, passing null objAddrTemp for implicit calls
            System.out.printf("IRme: Adding IRcommand_Class_Method_Call for %s (offset %d), obj TEMP: %s\n", funcName,
                    this.methodOffset, objAddrTemp);
            IR.getInstance()
                    .Add_IRcommand(new IRcommand_Class_Method_Call(dst, objAddrTemp, this.methodOffset, paramsTemp));
        }

        return dst;
    }
}

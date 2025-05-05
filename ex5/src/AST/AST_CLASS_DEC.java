package AST;

import java.util.ArrayList;
import java.util.List;

import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.ScopeType;
import SYMBOL_TABLE.SemanticException;
import TYPES.*;
import TEMP.*;
import IR.*;
import MIPS.MIPSGenerator;

public class AST_CLASS_DEC extends AST_DEC {

    public String parentClassName;
    public AST_LIST<AST_CLASS_FIELDS_DEC> fields;
    public ArrayList<AST_CLASS_FIELDS_DEC> finalFunctions = new ArrayList<AST_CLASS_FIELDS_DEC>();

    public AST_CLASS_DEC(String className, String parentClass, AST_LIST<AST_CLASS_FIELDS_DEC> fields) {
        super(className);
        this.parentClassName = parentClass;
        this.fields = fields;
    }

    @Override
    public void PrintMe() {
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                toString());
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber, fields.SerialNumber);
        fields.PrintMe();
    }

    @Override
    public String toString() {
        String represtation = String.format("class %s", super.getName());
        if (parentClassName != null) {
            represtation += " extends " + parentClassName;
        }
        return represtation;
    }

    private boolean isParentClassValid() throws SemanticException {
        if (parentClassName == null) {
            return true;
        }
        SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
        TYPE parent = symbol_table.getTypeInGlobalScope(parentClassName);
        return parent != null;

    }

    @Override
    public TYPE SemantMe() throws SemanticException {
        SYMBOL_TABLE symbol_table = SYMBOL_TABLE.getInstance();
        if (!symbol_table.isAtGlobalScope()) {
            throw new SemanticException(lineNumber,
                    "Scope mismatch found scope:" + symbol_table.getCurrentScopeIndex());
        }
        if (isDeclaredInCurrentScope()) {
            throw new SemanticException(lineNumber,
                    String.format("Cannot declare class %s: already declared", getName()));
        }
        if (!isParentClassValid()) {
            throw new SemanticException(lineNumber,
                    "Specified extends class \"" + parentClassName + "\" not found for class " + getName());
        }

        TYPE_CLASS father = (parentClassName != null) ? (TYPE_CLASS) symbol_table.getTypeInGlobalScope(parentClassName)
                : null;
        TYPE_CLASS currentClass = new TYPE_CLASS(father, getName(), this, lineNumber); // Pass father TYPE_CLASS
        SYMBOL_TABLE.getInstance().enter(getName(), currentClass); // Enter class type itself

        TYPE_CLASS_VAR_DEC_LIST memberList = new TYPE_CLASS_VAR_DEC_LIST(); // Final list for this class
        int currentDataOffset = 4; // Start after VMT pointer
        int currentMethodOffset = 0; // VMT index counter

        // Handle Inheritance
        if (father != null && father.getDataMembers() != null) {
            for (TYPE_CLASS_FIELD parentField : father.getDataMembers()) {
                // Create a new instance to avoid modifying father's list
                TYPE_CLASS_FIELD inheritedField = new TYPE_CLASS_FIELD(parentField.getName(), parentField.t,
                        parentField.line);
                inheritedField.offset = parentField.offset; // Crucial: inherit offset
                inheritedField.specifiedName = parentField.specifiedName; // Inherit specified name too
                memberList.add(inheritedField);

                // Update offsets for the next fields/methods based on parent
                if (parentField.t.isFunction()) {
                    // VMT offsets should be contiguous. Find the max VMT index + 1.
                    currentMethodOffset = Math.max(currentMethodOffset, parentField.offset + 1);
                } else {
                    // Data offsets should be contiguous. Find the max data offset + 4.
                    currentDataOffset = Math.max(currentDataOffset, parentField.offset + 4);
                }
            }
        }
        currentClass.setDataMembers(memberList); // Set initial members (inherited)

        SYMBOL_TABLE.getInstance().beginScope(ScopeType.CLASS);
        // Process Current Class Declarations
        for (AST_CLASS_FIELDS_DEC myFieldAST : fields) {
            String myFieldName = myFieldAST.getName();
            // Semant the field/method declaration itself first
            TYPE myFieldType = myFieldAST.SemantMe();

            TYPE_CLASS_FIELD existingMember = memberList.get(myFieldName); // Check if inherited

            if (existingMember != null) { // Member with this name exists (must be inherited)
                if (myFieldType.isFunction() && existingMember.t.isFunction()) {
                    // --- Method Overriding ---
                    TYPE_FUNCTION existingMethodType = (TYPE_FUNCTION) existingMember.t;
                    TYPE_FUNCTION overridingMethodType = (TYPE_FUNCTION) myFieldType;
                    if (!overridingMethodType.isOverriding(existingMethodType)) {
                        throw new SemanticException(myFieldAST.lineNumber, String.format(
                                "Method '%s' in class '%s' does not correctly override the version in a parent class.",
                                myFieldName, getName()));
                    }
                    // Valid override: Update the type, keep the inherited VMT offset
                    existingMember.t = overridingMethodType;
                    myFieldAST.offset = existingMember.offset; // Store VMT offset in AST node
                    existingMember.specifiedName = getName() + "." + myFieldName; // Update specified name to this class
                    finalFunctions.add(myFieldAST);
                } else {
                    // Illegal redeclaration (field hiding field, method hiding field, field hiding
                    // method)
                    throw new SemanticException(myFieldAST.lineNumber,
                            String.format(
                                    "Cannot redeclare member '%s' in class '%s'. Illegal shadowing or type mismatch with inherited member.",
                                    myFieldName, getName()));
                }
            } else {
                // --- New Member (not inherited) ---
                TYPE_CLASS_FIELD myNewMember = new TYPE_CLASS_FIELD(myFieldName, myFieldType, myFieldAST.lineNumber);
                myNewMember.specifiedName = getName() + "." + myFieldName;

                if (myFieldType.isFunction()) {
                    // Assign the next available VMT offset (index)
                    int vmtIndex = currentMethodOffset;
                    int vmtByteOffset = vmtIndex * 4; // Calculate byte offset
                    myNewMember.offset = vmtByteOffset; // Store byte offset
                    myFieldAST.offset = vmtByteOffset; // Store byte offset in AST node too
                    currentMethodOffset++; // Increment index for next method
                    finalFunctions.add(myFieldAST);
                } else {
                    // Assign the next available data offset
                    myNewMember.offset = currentDataOffset;
                    myFieldAST.offset = currentDataOffset; // Store data offset in AST node
                    currentDataOffset += 4; // Increment for the next data field (assuming 4 bytes)
                }
                memberList.add(myNewMember); // Add the new member to the list
            }
        }
        SYMBOL_TABLE.getInstance().endScope();
        currentClass.setDataMembers(memberList); // Set the final list including new members

        return currentClass;
    }

    @Override
    public TEMP IRme() {
        // Retrieve the TYPE_CLASS info which includes calculated offsets
        TYPE selfType = SYMBOL_TABLE.getInstance().find(getName());
        if (!(selfType instanceof TYPE_CLASS)) {
            System.err.printf("IR Error: Could not find TYPE_CLASS for %s during IR generation.\n", getName());
            return null; // Should not happen if SemantMe succeeded
        }
        TYPE_CLASS classType = (TYPE_CLASS) selfType;

        // --- Generate VMT ---
        // 1. Collect method labels in VMT order
        // Assumes memberList is already sorted by VMT offset for methods due to
        // SemantMe logic
        List<String> vmtMethodLabels = new ArrayList<>();
        List<TYPE_CLASS_FIELD> sortedMethods = null;
        if (classType.getDataMembers() != null) {
            // Need to sort members by method offset (VMT index) to ensure correct VMT
            // layout
            sortedMethods = new ArrayList<>();
            for (TYPE_CLASS_FIELD member : classType.getDataMembers()) {
                if (member.t.isFunction()) {
                    sortedMethods.add(member);
                }
            }
            // Sort based on the stored VMT index (offset)
            sortedMethods.sort((m1, m2) -> Integer.compare(m1.offset, m2.offset));
            System.out.format("--- VMT Generation for class: %s ---\n", classType.getName());
            for (TYPE_CLASS_FIELD methodField : sortedMethods) {
                System.out.format("  Processing method: %s (specifiedName: %s)\n", methodField.getName(),
                        methodField.specifiedName);
                // Attempt to retrieve the label registered for this method's specified name.
                String baseLabel = IR.getInstance().getFunctionLabel(methodField.specifiedName);
                System.out.format("    getFunctionLabel returned: %s\n", baseLabel);
                if (baseLabel == null) {
                    // Label not found (likely a method defined in *this* class), generate and
                    // register it.
                    baseLabel = IRcommand.getFreshLabel(methodField.specifiedName);
                    System.out.format("    Generated fresh label: %s\n", baseLabel);
                    IR.getInstance().registerFunctionLabel(methodField.specifiedName, baseLabel);
                }
                // Else: Label was found (inherited method), reuse it.

                // Construct the final label name with "_start" for the VMT entry.
                String labelForVMT = baseLabel; // TODO :: This is a hack to fix the VMT generation for inherited
                                                // methods
                if (!baseLabel.endsWith("_start")) {

                    labelForVMT = baseLabel + "_start";
                }
                System.out.format("    Adding to VMT labels: %s\n", labelForVMT);
                vmtMethodLabels.add(labelForVMT);
            }
        }
        IR.getInstance().Add_IRcommand(new IRcommand_Class_Dec(classType.getName(), vmtMethodLabels));
        for (AST_CLASS_FIELDS_DEC field : finalFunctions) {
            field.IRme();
        }
        // --- Generate Implicit CONSTRUCTOR (`__init_ClassName`) ---
        String constructorLabel = "__init_" + classType.getName();
        String constructorStartLabel = constructorLabel + "_start";
        // Register the constructor label so it can be called
        IR.getInstance().registerFunctionLabel(constructorLabel, constructorStartLabel);
        IR.getInstance().Add_IRcommand(new IRcommand_Label(constructorStartLabel));

        // Frame size needs to accommodate saved $ra, $fp, and potentially local temps
        // for initializers.
        // Increase size slightly just in case.
        int constructorFrameSize = 12;
        IR.getInstance().Add_IRcommand(new IRcommand_Prologue(constructorFrameSize));

        // Load 'this' pointer once (passed as first arg, at offset 0($fp))
        TEMP tempThis = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRcommand(new IRcommand_Load(tempThis, 0, "this")); // Load 'this' from frame

        // --- Call Parent Constructor if it exists ---
        if (classType.father != null) {
            System.out.format("--- Constructor %s: Calling parent constructor %s ---\n", constructorLabel,
                    "__init_" + classType.father.getName());
            String parentConstructorBaseName = "__init_" + classType.father.getName(); // Get the base name

            // Prepare argument list (just 'this')
            ArrayList<TEMP> parentArgs = new ArrayList<>();
            parentArgs.add(tempThis);

            // Add the function call command using the BASE name
            // IRcommand_Func_Call's MIPSme will look up the correct _start label
            IR.getInstance().Add_IRcommand(new IRcommand_Func_Call(parentConstructorBaseName, parentArgs));

        }
        // --- End Parent Constructor Call ---

        // Generate initialization code for each field defined in THIS class
        System.out.format("--- Generating constructor %s body ---\n", constructorLabel);
        for (AST_CLASS_FIELDS_DEC fieldAST : fields) { // Use the correct list type
            System.out.format("--- Constructor %s: Processing field %s ---\n", constructorLabel, fieldAST.getName());

            // Find the corresponding TYPE_CLASS_FIELD to get the offset
            TYPE_CLASS_FIELD fieldInfo = null;
            if (classType.getDataMembers() != null) {
                for (TYPE_CLASS_FIELD f : classType.getDataMembers()) {
                    // Assuming TYPE_CLASS_FIELD has a getName() method or direct access
                    if (f.getName().equals(fieldAST.getName())) {
                        fieldInfo = f;
                        break;
                    }
                }
            }
            if (fieldInfo == null) {
                System.err
                        .println("!!! Error: Could not find field info for " + fieldAST.getName() + " in constructor.");
                continue; // Skip this field if info not found
            }

            int fieldOffset = fieldInfo.offset; // Assuming getOffset() exists

            TEMP initialValueTemp = null;
            if (fieldAST.varValue != null) {
                System.out.format("--- Constructor %s: Evaluating initial value for %s ---\n", constructorLabel,
                        fieldAST.getName());
                initialValueTemp = fieldAST.varValue.IRme();
            }

            if (initialValueTemp != null) {
                System.out.format("--- Constructor %s: Setting field %s (offset %d) ---\n", constructorLabel,
                        fieldAST.getName(), fieldOffset);
                IR.getInstance().Add_IRcommand(
                        new IRcommand_Class_Field_Set(tempThis, fieldOffset, initialValueTemp));
            } else {
                System.err.println("!!! Error: Initial value computation failed for " + fieldAST.getName());
            }
        }

        IR.getInstance().Add_IRcommand(new IRcommand_Epilogue(constructorFrameSize));
        IR.getInstance().Add_IRcommand(new IRcommand_Label(constructorLabel + "_end"));

        return null;
    }
}

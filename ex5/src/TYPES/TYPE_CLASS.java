package TYPES;

import java.util.HashSet;
import java.util.Set;

import AST.AST_CLASS_DEC;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS extends TYPE {
	/*********************************************************************/
	/* If this class does not extend a father class this should be null */
	/*********************************************************************/
	public TYPE_CLASS father;
	private TYPE_CLASS_VAR_DEC_LIST memberList;
	public int line;
	public AST_CLASS_DEC classDeclaration;
	/**************************************************/
	/* Gather up all data members in one place */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods */
	/**************************************************/

	/****************/
	/* CTROR(S) ... */
	/**
	 * @throws SemanticException
	 **************/
	public TYPE_CLASS(TYPE_CLASS father, String name, AST_CLASS_DEC classDeclaration, int line)
			throws SemanticException {
		this((TYPE) father, name, classDeclaration, line);
	}

	public TYPE_CLASS(String father, String name, AST_CLASS_DEC classDeclaration, int line)
			throws SemanticException {
		this(SYMBOL_TABLE.getInstance().find(father), name, classDeclaration, line);
	}

	private TYPE_CLASS(TYPE father, String name, AST_CLASS_DEC classDeclaration, int line) throws SemanticException {
		super(name);
		this.line = line;
		if (father != null && !(father instanceof TYPE_CLASS)) {
			throw new SemanticException(line, String.format("Cannot extends non class type %s", father));
		}
		if (father != null) {
			this.father = (TYPE_CLASS) father;
		}
		this.classDeclaration = classDeclaration;
	}

	@Override
	public boolean isClass() {
		return true;
	}

	public boolean isDerivedFrom(TYPE_CLASS other) throws SemanticException {
		String sharedType = getSharedType(other);
		if (sharedType == null) {
			return false;
		}
		TYPE_CLASS iterator = this;
		while (iterator != null) {
			if (iterator.getName() == other.getName()) {
				return true;
			}
			iterator = iterator.father;
		}
		return false;
	}

	public String getSharedType(TYPE_CLASS other) throws SemanticException {
		if (other == null) {
			return null;
		}
		SYMBOL_TABLE table = SYMBOL_TABLE.getInstance();
		if (!table.exists(this.getName()) || !table.exists(other.getName())) {
			throw new SemanticException(line,
					"Shared check between inexistant classes" + this.getName() + " " + other.getName());
		}
		if (getName() == other.getName()) {
			return getName();
		}
		TYPE_CLASS t1 = this;
		Set<TYPE_CLASS> ancestors = new HashSet<>();
		while (t1 != null) {
			ancestors.add(t1);
			t1 = t1.father;
		}
		TYPE_CLASS t2 = other;
		while (t2 != null) {
			if (ancestors.contains(t2)) {
				return t2.getName();
			}
			t2 = t2.father;
		}
		return null;

	}

	@Override
	public String toString() {
		return String.format("Name: %s", getName());
	}

	public TYPE_CLASS_VAR_DEC_LIST getDataMembers() {
		return this.memberList;
	}

	public void setDataMembers(TYPE_CLASS_VAR_DEC_LIST members) {
		this.memberList = members;
	}

	public TYPE_CLASS_FIELD getDataMember(String name) {

		if (memberList == null) {
			return null;
		}
		for (TYPE_CLASS_FIELD member : memberList) {
			if (member.getName().equals(name)) {
				return member;
			}
		}
		return null;

	}

	public int getDataMemberOffset(String fieldName) throws SemanticException {
		TYPE_CLASS_FIELD field = getDataMember(fieldName);
		if (field == null) {
			throw new SemanticException(line,
					String.format("Field '%s' not found in class '%s'.", fieldName, getName()));
		}

		return field.offset;
	}

	@Override
	public boolean isAssignable(TYPE other) throws SemanticException {
		return other instanceof TYPE_NIL || (other instanceof TYPE_CLASS && ((TYPE_CLASS) other).isDerivedFrom(this));
	}

	public int getInstanceSize() {
		int size = 0;
		for (TYPE_CLASS_FIELD field : this.memberList) {
			size += field.isFunction() ? 0 : 4;
		}
		return size + 4;
	}

	@Override
	public int getSize() {
		return 4;
	}

}

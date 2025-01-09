package TYPES;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import AST.AST_CLASS_FIELDS_DEC;
import AST.AST_LIST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS_VAR_DEC_LIST implements Iterable<TYPE_CLASS_FIELD>
{
	private List<TYPE_CLASS_FIELD> attributes;
	public TYPE_CLASS_VAR_DEC_LIST(List<TYPE_CLASS_FIELD> attributes)
	{
		this.attributes = attributes;
	}
	public TYPE_CLASS_VAR_DEC_LIST(AST_LIST<? extends AST_CLASS_FIELDS_DEC> declarationList) throws SemanticException
	{
		this.attributes = new ArrayList<>();
		Set<String> definedNames = new HashSet<>();
		for(AST_CLASS_FIELDS_DEC declaration : declarationList)
		{
			TYPE declarationType = declaration.SemantMeLog();
			if(definedNames.contains(declaration.getName()))
			{
				throw new SemanticException(declarationList.lineNumber,"Multiple fields with the same name in function");
			}
			definedNames.add(declaration.getName());
			if (declarationType.isClass() && SYMBOL_TABLE.getInstance().find(declarationType.getName())==null)
			{
				throw new SemanticException(declaration.lineNumber,String.format("Cannot create a class with undefined varriables types, attempted %s", declarationType));
			}
			this.attributes.add(new TYPE_CLASS_FIELD(declaration.getName(),declarationType,declaration.lineNumber));
		}
	}
	public List<TYPE_CLASS_FIELD> getAttributes()
	{
		return this.attributes;
	}
	public void extendAll(TYPE_CLASS_VAR_DEC_LIST other) throws SemanticException
	{

		Set<String> overridenAttributes = new HashSet<>();
		
		for(TYPE_CLASS_FIELD otherAttribute : other.attributes)
		{
			for (TYPE_CLASS_FIELD classAttribute : this.attributes)
			{
				String otherName = otherAttribute.getName();
				TYPE classAttributeType = classAttribute.t;
				if(classAttribute.getName().equals(otherName))
				{
					if(!(classAttributeType instanceof TYPE_FUNCTION && otherAttribute.t instanceof TYPE_FUNCTION))
					{
						throw new SemanticException(classAttribute.line,String.format("shadowing for %s is disallowed %s vs %s", classAttribute.t.getName(),classAttribute.t,otherAttribute.t));
					}
					if(!((TYPE_FUNCTION)classAttributeType).equals((TYPE_FUNCTION)otherAttribute.t))
					{
						throw new SemanticException(classAttribute.line,String.format("Extended function share the same name and type %s but are not overriding %s vs %s", classAttribute.t.getName(),classAttribute.t,otherAttribute.t));
					}
					overridenAttributes.add(classAttribute.getName());
				}
			}
		}
		List<TYPE_CLASS_FIELD> newAtrributes = new ArrayList<>();
		newAtrributes.addAll(this.attributes);
		for(TYPE_CLASS_FIELD attribute :newAtrributes)
		{
			if(!overridenAttributes.contains(attribute.getName()))
			{
				SYMBOL_TABLE.getInstance().enter(attribute.getName(), attribute.t);
			}
		}
		for(TYPE_CLASS_FIELD attribute : other.attributes)
		{
			if(!overridenAttributes.contains(attribute.getName()))
			{
				newAtrributes.add(attribute);
			}
		}
		this.attributes = newAtrributes;
	}

	@Override
	public Iterator<TYPE_CLASS_FIELD> iterator() {
		return attributes.iterator();
	}
	@Override
	public String toString() {
		return String.format("Declaration List : %s",attributes);
	}
}

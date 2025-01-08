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

public class TYPE_CLASS_VAR_DEC_LIST implements Iterable<TYPE_CLASS_VAR_DEC>
{
	private List<TYPE_CLASS_VAR_DEC> attributes;
	public TYPE_CLASS_VAR_DEC_LIST(List<TYPE_CLASS_VAR_DEC> attributes)
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
			System.out.println("Adding "+ declaration + "name: " +declarationType.getName());
			if(definedNames.contains(declaration.getName()))
			{
				throw new SemanticException(declarationList.lineNumber,"Multiple fields with the same name in function");
			}
			definedNames.add(declaration.getName());
			System.out.printf("isclass %s, instance %s",declarationType.isClass(),SYMBOL_TABLE.getInstance().find(declarationType.getName()));
			if (declarationType.isClass() && SYMBOL_TABLE.getInstance().find(declarationType.getName())==null)
			{
				throw new SemanticException(declarationList.lineNumber,String.format("Cannot create a class with undefined varriables types, attempted %s", declarationType));
			}
			SYMBOL_TABLE.getInstance().enter(declarationType.getName(), declarationType);
			this.attributes.add(new TYPE_CLASS_VAR_DEC(declarationType,declaration.getName(),declaration.lineNumber));
		}
	}
	public List<TYPE_CLASS_VAR_DEC> getAttributes()
	{
		return this.attributes;
	}
	public void extendAll(TYPE_CLASS_VAR_DEC_LIST other) throws SemanticException
	{
		System.out.println(String.format("working on %s 1",other));

		for(TYPE_CLASS_VAR_DEC attribute : other.attributes)
		{

			extend(attribute);
		}
	}
	private void extend(TYPE_CLASS_VAR_DEC attribute) throws SemanticException
	{
		List<TYPE_CLASS_VAR_DEC> addedAttributes = new ArrayList<>();
		String name = attribute.getName();
		for ( TYPE_CLASS_VAR_DEC classAttribute : this.attributes)
		{
			System.out.println(String.format("working on %s with %s",classAttribute,attribute));
			System.out.println(String.format("names %s %s",classAttribute.getName(),attribute.getName()));
			TYPE classAttributeType = classAttribute.t;
			addedAttributes.add(classAttribute);
			if(!classAttribute.getName().equals(name))
			{
				continue;
			}
			if(classAttributeType instanceof TYPE_FUNCTION && attribute.t instanceof TYPE_FUNCTION)
			{
				System.out.println(String.format("Checking function overriding for %s",classAttributeType));
				
				if(!(classAttributeType.isFunction() && attribute.t.isFunction() && ((TYPE_FUNCTION)classAttributeType).isOverriding((TYPE_FUNCTION)attribute.t)))
				{
					throw new SemanticException(classAttribute.line,String.format("Extended function share the same name and type %s but are not overriding %s vs %s", classAttribute.t.getName(),classAttribute.t,attribute.t));
				}
				continue;
			}
			else 
			{
				throw new SemanticException(classAttribute.line,String.format("Extended attribute share the same name %s but of diffrent types %s vs %s", classAttribute.getName(),classAttribute.t,attribute.t));
			}
		}
	}
	@Override
	public Iterator<TYPE_CLASS_VAR_DEC> iterator() {
		return attributes.iterator();
	}
	@Override
	public String toString() {
		return String.format("Declaration List : %s",attributes);
	}
}

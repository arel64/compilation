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
	
	public TYPE_CLASS_VAR_DEC_LIST(AST_LIST<? extends AST_CLASS_FIELDS_DEC> declarationList) throws SemanticException
	{
		this.attributes = new ArrayList<>();
		Set<String> definedNames = new HashSet<>();
		for(AST_CLASS_FIELDS_DEC declaration : declarationList)
		{
			
			TYPE_CLASS_VAR_DEC currentDeclaration = declaration.SemantMe();
			if(definedNames.contains(currentDeclaration.name))
			{
				throw new SemanticException("Multiple fields with the same name in function");
			}
			definedNames.add(currentDeclaration.name);
			if (currentDeclaration.isClass() && ! SYMBOL_TABLE.getInstance().existsInCurrentScope(currentDeclaration.name))
			{
				throw new SemanticException(String.format("Cannot create a class with undefined varriables types, attempted %s", currentDeclaration));
			}
			this.attributes.add(currentDeclaration);
		}
	}
	public List<TYPE_CLASS_VAR_DEC> getAttributes()
	{
		return this.attributes;
	}
	public void extending(TYPE_CLASS_VAR_DEC_LIST other) throws SemanticException
	{
		for(TYPE_CLASS_VAR_DEC attribute : other.attributes)
		{
			extend(attribute);
		}
	}
	private void extend(TYPE_CLASS_VAR_DEC attribute) throws SemanticException
	{
		List<TYPE_CLASS_VAR_DEC> addedAttributes = new ArrayList<>();
		String name = attribute.name;
		for ( TYPE_CLASS_VAR_DEC classAttribute : this.attributes)
		{
			if(classAttribute.name != name)
			{
				addedAttributes.add(classAttribute);
				continue;
			}
			if(classAttribute.t != attribute.t)
			{
				throw new SemanticException(String.format("Extended attribute share the same name %s but of diffrent types %s vs %s", classAttribute.t.name,classAttribute.t,attribute.t));
			}
			if(classAttribute.t instanceof TYPE_FUNCTION)
			{
				
				if(!((TYPE_FUNCTION)classAttribute.t).isOverriding((TYPE_FUNCTION)attribute.t))
				{
					throw new SemanticException(String.format("Extended function share the same name and type %s but are not overriding %s vs %s", classAttribute.t.name,classAttribute.t,attribute.t));
				}
				continue;
			}
			throw new SemanticException("Unknown class var dec list exception");
		}
	}
	@Override
	public Iterator<TYPE_CLASS_VAR_DEC> iterator() {
		return attributes.iterator();
	}
}

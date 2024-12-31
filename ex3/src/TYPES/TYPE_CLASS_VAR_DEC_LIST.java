package TYPES;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import AST.AST_LIST;
import AST.AST_VAR_DEC;

import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS_VAR_DEC_LIST
{
	private List<TYPE_CLASS_VAR_DEC> attributes;
	
	public TYPE_CLASS_VAR_DEC_LIST(AST_LIST<? extends AST_VAR_DEC> declarationList) throws SemanticException
	{
		this.attributes = new ArrayList<>();
		Set<String> definedNames = new HashSet<>();
		for(AST_VAR_DEC declaration : declarationList.list)
		{
			
			TYPE_CLASS_VAR_DEC temp = declaration.SemantMe();
			if(definedNames.contains(temp.name))
			{
				throw new SemanticException("Multiple fields with the same name in function");
			}
			this.attributes.add(temp);
		}
	}
	public List<TYPE_CLASS_VAR_DEC> getAttributes()
	{
		return this.attributes;
	}
	public void extending(TYPE_CLASS_VAR_DEC_LIST other)
	{
		for(TYPE_CLASS_VAR_DEC attribute : other.attributes)
		{
			extend(attribute);
		}
	}
	private void extend(TYPE_CLASS_VAR_DEC attribute)
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
				//throw error
			}
			if(classAttribute.t instanceof TYPE_FUNCTION)
			{
				
				if(!((TYPE_FUNCTION)classAttribute.t).isOverriding((TYPE_FUNCTION)attribute.t))
				{
					// Throw error
				}
				continue;
			}
			//throw
		}
	}
}

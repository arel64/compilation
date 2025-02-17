package TYPES;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import AST.AST_CLASS_FIELDS_DEC;
import AST.AST_LIST;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SemanticException;

public class TYPE_CLASS_VAR_DEC_LIST implements Iterable<TYPE_CLASS_FIELD>
{
	private Set<String> definedNames;
	private List<TYPE_CLASS_FIELD> attributes;
	private List<String> overridenFunction;
	public TYPE_CLASS_VAR_DEC_LIST(List<String> overridenFunction) throws SemanticException
	{
		attributes = new ArrayList<>();
		definedNames = new HashSet<>();
		this.overridenFunction = overridenFunction;
		
	}
	public List<TYPE_CLASS_FIELD> getAttributes()
	{
		return this.attributes;
	}
	@Override
	public Iterator<TYPE_CLASS_FIELD> iterator() {
		return attributes.iterator();
	}
	@Override
	public String toString() {
		return String.format("Declaration List : %s",attributes);
	}
    public TYPE_CLASS_FIELD  get(String name) {
        Optional<TYPE_CLASS_FIELD> val =  this.attributes.stream().filter(x -> x.getName().equals(name)).findFirst() ;
		if(val.isPresent())
		{
			return val.get();
		}
		return null;
    }
	public void add(TYPE_CLASS_FIELD declarationType) throws SemanticException {
		if(definedNames.contains(declarationType.getName()))
		{
			throw new SemanticException(declarationType.line,"Multiple fields with the same name in function");
		}
		definedNames.add(declarationType.getName());
		if (declarationType.isClass() && SYMBOL_TABLE.getInstance().find(declarationType.getName())==null)
		{
			throw new SemanticException(declarationType.line,String.format("Cannot create a class with undefined varriables types, attempted %s", declarationType));
		}
		if(overridenFunction.contains(declarationType.getName()) && !declarationType.t.isFunction())
		{
			throw new SemanticException(declarationType.line,String.format("Redeclared %s as non function in dervied type", declarationType));
		}
		this.attributes.add(declarationType);
		
    }
}

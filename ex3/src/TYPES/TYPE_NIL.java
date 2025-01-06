package TYPES;

public class TYPE_NIL extends TYPE{
    private static TYPE_NIL instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_NIL() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_NIL getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_NIL();
			instance.name = "nil";
		}
		return instance;
	}
    @Override
    public boolean isClass() {
        return true;
    }
    @Override
    public boolean isArray() {
        return true;
    }
}

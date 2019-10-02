package com.rafalesoft.org.coachacook;

public class RecipeComponent 
{
	public static final String TABLE_NAME = "recipe_parts";
	public static final String COLUMN_RECIPE = "recipeId";
	public static final String COLUMN_INGREDIENT = "ingredientId";
	public static final String COLUMN_AMOUNT_TITLE = "amount";
	public static final String COLUMN_UNIT_TITLE = "unit";
	
	private String _name = null;
	private Amount _amount = new Amount();


	/**
	 * Elaborates the SQL query to create ingredient table
	 * @return the sql query string
	 */
	static String getTableQuery()
	{
		return "CREATE TABLE " + RecipeComponent.TABLE_NAME + " ("
				+ RecipesDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ RecipeComponent.COLUMN_RECIPE + " INTEGER,"
				+ RecipeComponent.COLUMN_INGREDIENT + " INTEGER,"
				+ RecipeComponent.COLUMN_AMOUNT_TITLE + " REAL,"
				+ RecipeComponent.COLUMN_UNIT_TITLE + " VARCHAR(4)"
				+ ");";
	}

	/**
	 * Name setter.
	 * @param value : new component name.
	 */
	void set_name(String value)
	{
		_name = value;
	}

	/**
	 * Name getter.
	 * @return component name.
	 */
	String get_name()
	{
		return _name;
	}

	/**
	 * Quantity setter.
	 * @param value : new component quantity.
	 */
	void set_quantity(double value)
	{
		get_amount().set_quantity(value);
	}

	/**
	 * Quantity getter.
	 * @return the component quantity.
	 */
	Double get_quantity()
	{
		return get_amount().get_quantity();
	}

    /**
     * Unit getter.
     * @return the component unit.
     */
	Unit get_unit()
	{
		return get_amount().get_unit();
	}

    /**
     * Unit setter.
     * @param unit : new component unit.
     */
	void set_unit(Unit unit)
	{
		get_amount().set_unit(unit);
	}

    /**
     * Amount getter.
     * @return the component amount.
     */
    Amount get_amount()
    {
        return _amount;
    }

    /**
     * Amount setter.
     * @param amount : new component amount.
     */
    void set_amount(Amount amount)
    {
        _amount = amount;
    }
}

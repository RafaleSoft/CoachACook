package com.rafalesoft.org.coachacook;

public class RecipeComponent 
{
	public static final String TABLE_NAME = "recipe_parts";
	public static final String COLUMN_RECIPE_TITLE = "recipeId";
	public static final String COLUMN_INGREDIENT_TITLE = "ingredientId";
	public static final String COLUMN_AMOUNT_TITLE = "amount";
	public static final String COLUMN_UNIT_TITLE = "unit";
	
	private String _name = null;
	private Double _quantity = 0.0;
	private Unit _unit = Unit.GRAM;

	/**
	 * Elaborates the SQL query to create ingredient table
	 * @return the sql query string
	 */
	public static String getTableQuery()
	{
		return "CREATE TABLE " + RecipeComponent.TABLE_NAME + " ("
				+ RecipesDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ RecipeComponent.COLUMN_RECIPE_TITLE + " INTEGER,"
				+ RecipeComponent.COLUMN_INGREDIENT_TITLE + " INTEGER,"
				+ RecipeComponent.COLUMN_AMOUNT_TITLE + " REAL,"
				+ RecipeComponent.COLUMN_UNIT_TITLE + " VARCHAR(4)"
				+ ");";
	}

	/**
	 * Name setter.
	 * @param value : new component name.
	 */
	public void set_name(String value) 
	{
		_name = value;
	}

	/**
	 * Name getter.
	 * @return component name.
	 */
	public String get_name() 
	{
		return _name;
	}

	/**
	 * Quantity setter.
	 * @param value : new component quantity.
	 */
	public void set_quantity(double value) 
	{
		_quantity = value;
	}

	/**
	 * Quantity getter.
	 * @return the component quantity.
	 */
	public Double get_quantity() 
	{
		return _quantity;
	}

    /**
     * Unit getter.
     * @return the component unit.
     */
	public Unit get_unit()
	{
		return _unit;
	}

    /**
     * Unit setter.
     * @param unit : return the component unit.
     */
	public void set_unit(Unit unit)
	{
		_unit = unit;
	}
}

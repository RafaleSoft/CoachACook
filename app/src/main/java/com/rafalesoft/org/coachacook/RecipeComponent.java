package com.rafalesoft.org.coachacook;

public class RecipeComponent 
{
	public static final String TABLE_NAME = "recipe_parts";
	public static final String _ID = "_id";
	public static final String COLUMN_RECIPE_TITLE = "recipeId";
	public static final String COLUMN_INGREDIENT_TITLE = "ingredientId";
	public static final String COLUMN_AMOUNT_TITLE = "amount";
	public static final String COLUMN_UNIT_TITLE = "unit";
	
	private String _name = null;
	private Double _quantity = 0.0;
	private String _unit = null;
	
	public void set_name(String value) 
	{
		_name = value;
	}
	public String get_name() 
	{
		return _name;
	}
	public void set_quantity(double value) 
	{
		_quantity = value;
	}
	public Double get_quantity() 
	{
		return _quantity;
	}
	public String get_unit()
	{
		return _unit;
	}
	public void set_unit(String unit) 
	{
		_unit = unit;
	}
}

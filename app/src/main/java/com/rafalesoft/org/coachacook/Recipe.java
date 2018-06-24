package com.rafalesoft.org.coachacook;

import java.util.ArrayList;
import org.xml.sax.Attributes;

public class Recipe 
{
	public static final String TABLE_NAME = "recipes";
	public static final String COLUMN_GUESTS_TITLE = "guests";
	public static final String COLUMN_PREPARATION_TITLE = "preparation";
	public static final String COLUMN_DIFFICULTY_TITLE = "difficulty";
    public static final String COLUMN_COST_TITLE = "cost";
	public static final String COLUMN_PREPARE_TITLE = "prepare_time";
    public static final String COLUMN_TIME_TITLE = "cook_time";
	
	private String _name;
	private final ArrayList<RecipeComponent> _components = new ArrayList<>();
	private int _guests = 0;


    private int _difficulty = 0;
    private int _cost = 0;
    private int _cook_time = 0;
    private int _prepare_time = 0;
	private String _preparation = null;


	public Recipe(String name)
    {
        _name = name;
    }

	/**
	 * Elaborates the SQL query to create ingredient table
	 * @return the sql query string
	 */
	public static String getTableQuery()
	{
		return "CREATE TABLE " + Recipe.TABLE_NAME + " ("
				+ RecipesDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ RecipesDB.NAME + " VARCHAR(32) NOT NULL,"
				+ Recipe.COLUMN_GUESTS_TITLE + " INTEGER,"
				+ Recipe.COLUMN_PREPARATION_TITLE + " TEXT,"
				+ Recipe.COLUMN_DIFFICULTY_TITLE + " INTEGER,"
				+ Recipe.COLUMN_COST_TITLE + " INTEGER,"
				+ Recipe.COLUMN_PREPARE_TITLE + " INTEGER,"
				+ Recipe.COLUMN_TIME_TITLE + " INTEGER"
				+ ");";
	}

    /**
     * Name getter.
     * @return recipe name.
     */
	public String get_name()
    {
        return _name;
    }

    /**
     * Name setter.
     * @param _name : the new recipe name.
     */
	private void set_name(String _name)
	{
		this._name = _name;
	}

    /**
     * Preparation getter.
     * @return the preparation of the recipe.
     */
	public String get_preparation()
	{
		return _preparation;
	}

    /**
     * Preparation setter.
     * @param preparation : the new recipe preparation.
     */
	public void set_preparation(String preparation)
	{
		_preparation = preparation;
	}


    /**
     * Guest getter.
     * @return the number of guest for the recipe in database.
     */
	public int get_guests()
	{
		return _guests;
	}

    /** Guests setter.
     * Define the new number of guests for this recipe.
     * @param guests
     */
	public void set_guests(int guests)
	{
		_guests = guests;
	}

    /**
     * Difficulty getter.
     * @return recipe difficulty.
     */
    public int get_difficulty()
    {
        return _difficulty;
    }

    /**
     * Difficulty setter.
     * @param difficulty : recipe difficulty, 0 to 100 scale.
     */
	private void set_difficulty(int difficulty)
    {
        _difficulty = difficulty;
    }

    /**
     * Cost setter.
     * @param cost : recipe cost, 0 to 100 scale.
     */
    private void set_cost(int cost)
    {
        _cost = cost;
    }

    /**
     * Cost getter.
     * @return the recipe relative cost.
     */
    public int get_cost()
    {
        return _cost;
    }

    /**
     * Recipe preparation setter.
     * @param time : the recipe preparation time in minutes.
     */
    private void set_prepare_time(int time)
    {
        _prepare_time = time;
    }

    /**
     * Recipe preparation getter.
     * @return the preparation time.
     */
    public int get_prepare_time()
    {
        return _prepare_time;
    }

    /**
     * Cooking time setter.
     * @param time : the recipe cooking time in minutes.
     */
    private void set_cook_time(int time)
    {
        _cook_time = time;
    }

    /**
     * Cooking time getter.
     * @return the cooking time.
     */
    public int get_cook_time()
    {
        return _cook_time;
    }

    /**
     * Getter for the number of recipe ingredients.
     * @return the number of ingredients.
     */
    public int nbComponents()
	{
		return _components.size();
	}


	public void addComponent(RecipeComponent component) 
	{
		_components.add(component);
	}
	
	public RecipeComponent getComponent(int numComponent) 
	{
		if (numComponent < _components.size())
			return _components.get(numComponent);
		else
			return null;
	}
	
	public static boolean load_recipes()
	{
		RecipeLoader loader = new RecipeLoader();
		return loader.load_data(R.string.recipe_file);
	}
	
	private static class RecipeLoader extends DataLoader
	{
		private boolean _parsingRecipe = false;
		private boolean _parsingPreparation = false;
		private Recipe _recipe = null;

		RecipeLoader() {}

		@Override
		public void onElementLoaded(String localName, Attributes attrs)
		{
			if (localName.compareTo("Recipe") == 0)
			{
				_recipe = new Recipe("");
				_parsingRecipe = true;
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo(RecipesDB.NAME) == 0)
						_recipe.set_name(attrs.getValue(i));
					else if (name.compareTo(Recipe.COLUMN_GUESTS_TITLE) == 0)
						_recipe.set_guests(Integer.parseInt(attrs.getValue(i)));
                    else if (name.compareTo(Recipe.COLUMN_DIFFICULTY_TITLE) == 0)
                        _recipe.set_difficulty(Integer.parseInt(attrs.getValue(i)));
                    else if (name.compareTo(Recipe.COLUMN_COST_TITLE) == 0)
                        _recipe.set_cost(Integer.parseInt(attrs.getValue(i)));
                    else if (name.compareTo(Recipe.COLUMN_PREPARE_TITLE) == 0)
                        _recipe.set_prepare_time(Integer.parseInt(attrs.getValue(i)));
                    else if (name.compareTo(Recipe.COLUMN_TIME_TITLE) == 0)
                        _recipe.set_cook_time(Integer.parseInt(attrs.getValue(i)));
				}
			}
			else if ((localName.compareTo("Component") == 0) &&
					(_parsingRecipe))
			{
				RecipeComponent component = new RecipeComponent();
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo("name") == 0)
						component.set_name(attrs.getValue(i));
					else if (name.compareTo("quantity") == 0)
						component.set_quantity(Double.parseDouble(attrs.getValue(i)));
					else if (name.compareTo("unit") == 0)
						component.set_unit(Unit.parse(attrs.getValue(i)));
				}
				_recipe.addComponent(component);
			}
			else if ((localName.compareTo("Preparation") == 0) &&
					(_parsingRecipe))
			{
				_parsingPreparation = true;
				_recipe.set_preparation("");
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
		{
			if (_parsingRecipe && _parsingPreparation)
			{
				String prep = _recipe.get_preparation() + String.valueOf(ch, start, length);
				_recipe.set_preparation(prep);
			}
		}
	
		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if (localName.compareTo("Recipe") == 0)
			{
				_parsingRecipe = false;
				_db.insert(_recipe);
				_recipe = null;
			}
			else if (localName.compareTo("Preparation") == 0)
			{
				_parsingPreparation = false;
			}
		}
	}
}

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
    public static final String COLUMN_TIME_TITLE = "time";
	
	private String _name;
	private ArrayList<RecipeComponent> _components = new ArrayList<>();
	private int _guests = 0;


    private int _difficulty = 0;
    private int _cost = 0;
    private int _time = 0;
	private String _preparation = null;


	public Recipe(String name)
    {
        _name = name;
    }

    public String get_name()
    {
        return _name;
    }
	private void set_name(String _name)
	{
		this._name = _name;
	}
	public String get_preparation()
	{
		return _preparation;
	}
	public void set_preparation(String _preparation)
	{
		this._preparation = _preparation;
	}
	public int get_guests()
	{
		return _guests;
	}
	public void set_guests(int _guests)
	{
		this._guests = _guests;
	}
    public int get_difficulty()
    {
        return _difficulty;
    }
	private void set_difficulty(int difficulty)
    {
        _difficulty = difficulty;
    }
    private void set_cost(int cost)
    {
        _cost = cost;
    }
    public int get_cost()
    {
        return _cost;
    }
    private void set_time(int time)
    {
        _time = time;
    }
    public int get_time()
    {
        return _time;
    }

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
                    else if (name.compareTo(Recipe.COLUMN_TIME_TITLE) == 0)
                        _recipe.set_time(Integer.parseInt(attrs.getValue(i)));
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
						component.set_unit(attrs.getValue(i));
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
				_recipe.set_preparation(new String(ch));
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

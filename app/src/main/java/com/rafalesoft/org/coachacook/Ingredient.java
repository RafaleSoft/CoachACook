package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;

public class Ingredient 
{
	public static final String TABLE_NAME = "ingredients";
	public static final String COLUMN_STOCK_TITLE = "quantity";
	public static final String COLUMN_UNIT_TITLE = "unit";
	public static final String COLUMN_TYPE_TITLE = "type";

	
	
	private String 	_name;
	private Double	_quantity;
	private String 	_unit = null;
	private int 	_type = 0;

	public Ingredient(String name, double q)
	{		
		_name = name;
		_quantity = q;
	}

	public String get_name() 
	{
		return _name;
	}

	public void set_name(String _name) 
	{
		this._name = _name;
	}

	public Double get_quantity() 
	{
		return _quantity;
	}

	public void set_quantity(Double _quantity) 
	{
		this._quantity = _quantity;
	}

	public String get_unit()
	{
		return _unit;
	}

	public void set_unit(String _unit)
	{
		this._unit = _unit;
	}

	public int get_type() 
	{
		return _type;
	}

	public void set_type(int type) 
	{
		_type = type;
	}

	public static boolean load_ingredients(CoachACook cook)
	{
		IngredientLoader loader = new IngredientLoader(cook.getRecipesDB());
		return loader.load_data(cook, cook.getString(R.string.ingredient_file));
	}
		
	private static class IngredientLoader extends DataLoader
	{
		private boolean _parsingStock = false;
		private RecipesDB _db;

		public IngredientLoader(RecipesDB db)
		{
			_db = db;
		}

		@Override
		public void onElementLoaded(String localName, Attributes attrs)
		{
			if (_parsingStock)
			{
				Ingredient newIngredient = new Ingredient("",0);
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo("name") == 0)
						newIngredient.set_name(attrs.getValue(i));
					else if (name.compareTo("quantity") == 0)
						newIngredient.set_quantity(Double.parseDouble(attrs.getValue(i)));
					else if (name.compareTo("unit") == 0)
						newIngredient.set_unit(attrs.getValue(i));
					else if (name.compareTo("type") == 0)
						newIngredient.set_type(0); //attrs.getValue(i));
				}
				_db.insert(newIngredient);
			}
			else if (localName.compareTo("Stock") == 0)
				_parsingStock = true;
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if (localName.compareTo("Stock") == 0)
				_parsingStock = false;
		}
	
	}
}

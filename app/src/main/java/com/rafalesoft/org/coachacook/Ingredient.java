package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;

public class Ingredient 
{
	public static final String TABLE_NAME = "ingredients";
	public static final String COLUMN_STOCK_TITLE = "quantity";
	public static final String COLUMN_UNIT_TITLE = "unit";
	public static final String COLUMN_TYPE_TITLE = "type";
	public static final String COLUMN_IMAGE_ID = "image";
	
	
	private String 	_name;
	private Double	_quantity;
	private String 	_unit = null;
	private int 	_type = 0;
	private int 	_image = 0;

	private Ingredient(String name, double q)
	{		
		_name = name;
		_quantity = q;
	}

	public String get_name() 
	{
		return _name;
	}

	private void set_name(String _name)
	{
		this._name = _name;
	}

	public Double get_quantity() 
	{
		return _quantity;
	}

	private void set_quantity(Double _quantity)
	{
		this._quantity = _quantity;
	}

	public String get_unit()
	{
		return _unit;
	}

	private void set_unit(String _unit)
	{
		this._unit = _unit;
	}

	public int get_type() 
	{
		return _type;
	}

	private void set_type(int type)
	{
		_type = type;
	}

	public int get_image()
	{
		return _image;
	}

	private void set_image(int image)
	{
		_image = image;
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

		IngredientLoader(RecipesDB db)
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
					else if (name.compareTo("image") == 0)
						newIngredient.set_image(Integer.parseInt(attrs.getValue(i)));
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

package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;

public class Ingredient 
{
	public static final String TABLE_NAME = "ingredients";
	public static final String COLUMN_STOCK_TITLE = "quantity";
	public static final String COLUMN_UNIT_TITLE = "unit";
	public static final String COLUMN_TYPE_TITLE = "type";
	public static final String COLUMN_IMAGE_ID = "image";
	
	
	private String 	_name = "";
	private Double	_quantity = 0.0;
	private String 	_unit = null;
	private String 	_type = "";
	private int 	_image = 0;

	private Ingredient() {	}

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
	public String get_type()
	{
		return _type;
	}
	private void set_type(String type)
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


	public static boolean load_ingredients()
	{
		IngredientLoader loader = new IngredientLoader();
		return loader.load_data(R.string.ingredient_file);
	}
		
	private static class IngredientLoader extends DataLoader
	{
		private boolean _parsingStock = false;

		IngredientLoader() { }

		@Override
		public void onElementLoaded(String localName, Attributes attrs)
		{
			if (_parsingStock)
			{
				Ingredient newIngredient = new Ingredient();
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo(RecipesDB.NAME) == 0)
						newIngredient.set_name(attrs.getValue(i));
					else if (name.compareTo(Ingredient.COLUMN_STOCK_TITLE) == 0)
						newIngredient.set_quantity(Double.parseDouble(attrs.getValue(i)));
					else if (name.compareTo(Ingredient.COLUMN_UNIT_TITLE) == 0)
						newIngredient.set_unit(attrs.getValue(i));
					else if (name.compareTo(Ingredient.COLUMN_IMAGE_ID) == 0)
						newIngredient.set_image(Integer.parseInt(attrs.getValue(i)));
					else if (name.compareTo(Ingredient.COLUMN_TYPE_TITLE) == 0)
						newIngredient.set_type(attrs.getValue(i));
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

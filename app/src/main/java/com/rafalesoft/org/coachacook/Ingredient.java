package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;

import static com.rafalesoft.org.coachacook.Category.Model.MODEL_SIZE;

public class Ingredient 
{
	public static final String TABLE_NAME = "ingredients";
	public static final String COLUMN_STOCK = "quantity";
	public static final String COLUMN_UNIT = "unit";
	public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_IMAGE_ID = "image";


	private String 	_name = "";
	private Amount _amount = new Amount();
	private Category.Model 	_type = MODEL_SIZE;
	private int 	_image = 0;


    /**
     * Elaborates the SQL query to create ingredient table
     * @return the sql query string
     */
	static String getTableQuery()
    {
        return "CREATE TABLE " + Ingredient.TABLE_NAME + " ("
                + RecipesDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RecipesDB.NAME + " VARCHAR(32) NOT NULL,"
                + Ingredient.COLUMN_STOCK + " REAL,"
                + Ingredient.COLUMN_UNIT + " INTEGER,"
                + Ingredient.COLUMN_TYPE + " INTEGER,"
                + Ingredient.COLUMN_IMAGE_ID + " INTEGER"
                + ");";
    }

	Ingredient() {	}

    /**
     * Name getter.
     * @return ingredient name.
     */
	String get_name()
	{
		return _name;
	}

    /**
     * Name setter.
     * @param name : new same of ingredient.
     */
	void set_name(String name)
	{
		_name = name;
	}

    /**
     * Amount getter.
     * @return the ingredient amount.
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

    /**
     * Quantity getter.
     * @return ingredient quantity.
     */
	Double get_quantity()
	{
		return get_amount().get_quantity();
	}

    /**
     * Quantity setter.
     * @param quantity : the amount of ingredient.
     */
	void set_quantity(Double quantity)
	{
		get_amount().set_quantity(quantity);
	}

    /**
     * Unit getter.
     * @return the unit of ingredient quantity.
     */
	Unit get_unit()
	{
		return get_amount().get_unit();
	}

    /**
     * Unit setter.
     * @param unit : the new unit.
     */
	void set_unit(Unit unit)
	{
		get_amount().set_unit(unit);
	}

    /**
     * Ingredient _type getter.
     * @return the Category of this ingredient.
     */
	Category.Model get_type()
	{
		return _type;
	}

    /**
     * Ingredient _type setter.
     * @param type : the new Category.
     */
	void set_type(Category.Model type)
	{
		_type = type;
	}

    /**
     * Ingredient image.
     * @return the image resource id.
     */
	int get_image()
	{
		return _image;
	}

    /**
     * Ingredient new image resource
     * @param image : the resource id of the new image
     */
	void set_image(int image)
	{
		_image = image;
	}


	public static boolean load_ingredients()
	{
		IngredientLoader loader = new IngredientLoader();

		boolean load = true;
		for (Category.Model model: Category.Model.values())
		    load = load & loader.load_data(model.getCategoryFile());

		return load;
	}

    private static class IngredientLoader extends DataLoader
	{
		private boolean _parsingStock = false;
		private Category.Model _type;

		IngredientLoader() { }

        @Override
        public void startDocument()
        {
            _type = Category.Model.MODEL_SIZE;
        }

		@Override
		public void onElementLoaded(String localName, Attributes attrs)
		{
			if (_parsingStock)
			{
				Ingredient newIngredient = new Ingredient();
				newIngredient.set_type(_type);

				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo(RecipesDB.NAME) == 0)
						newIngredient.set_name(attrs.getValue(i));
					else if (name.compareTo(Ingredient.COLUMN_STOCK) == 0)
						newIngredient.set_quantity(Double.parseDouble(attrs.getValue(i)));
					else if (name.compareTo(Ingredient.COLUMN_UNIT) == 0)
						newIngredient.set_unit(Unit.parse(attrs.getValue(i)));
					else if (name.compareTo(Ingredient.COLUMN_IMAGE_ID) == 0)
						newIngredient.set_image(Integer.parseInt(attrs.getValue(i)));
					else if (name.compareTo(Ingredient.COLUMN_TYPE) == 0)
						newIngredient.set_type(Category.Model.valueOf(attrs.getValue(i)));
				}
				_db.insert(newIngredient);
			}
			else if (localName.compareTo("Stock") == 0)
            {
                _parsingStock = true;
                int nbAttrs = attrs.getLength();
                for (int i=0;i<nbAttrs;i++)
                {
                    String name = attrs.getLocalName(i);
                    if (name.compareTo(Ingredient.COLUMN_TYPE) == 0)
                        _type = Category.Model.valueOf(attrs.getValue(i));
                }
            }
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if (localName.compareTo("Stock") == 0)
				_parsingStock = false;
		}
	
	}
}

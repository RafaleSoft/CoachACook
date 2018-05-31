package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;


public class Category
{
	public static final String TABLE_NAME = "categories";
    public static final String COLUMN_IMAGE_ID = "image";

	private String 	_name;
    private int 	_image = 0;

    /**
     * @param name : the name of the category
     */
    private Category(String name)
	{		
		_name = name;
	}
	
	public String get_name()
	{
		return _name;
	}

	private void set_name(String name)
	{
		_name = name;
	}

    public int get_image()
    {
        return _image;
    }

    private void set_image(int image)
    {
        _image = image;
    }


	public static boolean load_categories(CoachACook cook)
	{
		CategoryLoader loader = new CategoryLoader(cook.getRecipesDB());
		return loader.load_data(cook, cook.getString(R.string.category_file));
	}
	
	private static class CategoryLoader extends DataLoader
    {
        private boolean _parsingCategory = false;
        private RecipesDB _db;

        CategoryLoader(RecipesDB db)
        {
            _db = db;
        }

        @Override
        public void onElementLoaded(String localName, Attributes attrs)
        {
            if (_parsingCategory)
            {
                Category newCategory = new Category("");
                int nbAttrs = attrs.getLength();
                for (int i = 0; i < nbAttrs; i++)
                {
                    String name = attrs.getLocalName(i);
                    if (name.compareTo("name") == 0)
                        newCategory.set_name(attrs.getValue(i));
                    else if (name.compareTo("image") == 0)
                        newCategory.set_image(Integer.parseInt(attrs.getValue(i)));
                }
                _db.insert(newCategory);
            }
            else if (localName.compareTo("Stock") == 0)
            {
                _parsingCategory = true;
            }
        }
    }
}

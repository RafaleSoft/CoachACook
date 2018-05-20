package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;


public class Category
{
	public static final String TABLE_NAME = "categories";
	private String 	_name;

    /**
     * @param name: the name of the category
     */
	Category(String name)
	{		
		_name = name;
	}
	
	public String get_name()
	{
		return _name;
	}

	public void set_name(String name)
	{
		_name = name;
	}

	public static boolean load_categories(RecipesDB db, String xmlSource)
	{
		CategoryLoader loader = new CategoryLoader(db);
		return loader.load_data(xmlSource);
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
                    {
                        newCategory.set_name(attrs.getValue(i));
                    }
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

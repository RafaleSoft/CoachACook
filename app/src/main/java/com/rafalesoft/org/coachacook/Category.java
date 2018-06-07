package com.rafalesoft.org.coachacook;

import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;


public class Category
{
	public static final String TABLE_NAME = "categories";
    public static final String COLUMN_IMAGE_ID = "image";

	private String 	_name = "";

    public Category() { }

	public String get_name()
	{
		return _name;
	}
	private void set_name(String name)
	{
		_name = name;
	}


    public enum Model
    {
        LEGUME(R.string.category_legume, R.mipmap.ic_legumes),
        FRUIT(R.string.category_fruit, R.mipmap.ic_fruits),
        BOUCHERIE(R.string.category_boucherie, R.mipmap.ic_boucherie),
        CHARCUTERIE(R.string.category_charcuterie, R.mipmap.ic_charcuterie),
        POISSONNERIE(R.string.category_poissonnerie, R.mipmap.ic_poissonnerie),
        LAITAGE(R.string.category_laitage, R.mipmap.ic_laitage),
        EPICERIE(R.string.category_epicerie, R.mipmap.ic_epicerie),
        MODEL_SIZE(0,0);

        final private int mStringResId;
        final private int mImageResId;

        Model(int titleResId, int layoutResId)
        {
            mStringResId = titleResId;
            mImageResId = layoutResId;
        }

        public int getTitleResId()
        {
            return mStringResId;
        }
        public int getImageResId()
        {
            return mImageResId;
        }
    }

	public static boolean load_categories()
	{
		CategoryLoader loader = new CategoryLoader();
		return loader.load_data(R.string.category_file);
	}
	
	private static class CategoryLoader extends DataLoader
    {
        private boolean _parsingCategory = false;

        CategoryLoader() {

        }

        @Override
        public void onElementLoaded(String localName, Attributes attrs)
        {
            if (_parsingCategory)
            {
                Category newCategory = new Category();
                int nbAttrs = attrs.getLength();
                for (int i = 0; i < nbAttrs; i++)
                {
                    String name = attrs.getLocalName(i);
                    if (name.compareTo(RecipesDB.NAME) == 0)
                        newCategory.set_name(attrs.getValue(i));
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

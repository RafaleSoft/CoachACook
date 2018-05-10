package com.rafalesoft.org.coachacook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Xml;

public class Recipe 
{
	public static final String TABLE_NAME = "recipes";
	public static final String _ID = "_id";
	public static final String COLUMN_NAME_TITLE = "name";
	public static final String COLUMN_GUESTS_TITLE = "guests";
	public static final String COLUMN_PREPARATION_TITLE = "preparation";
	
	private String _name;
	private ArrayList<RecipeComponent> _components = new ArrayList<RecipeComponent>();
	private int _guests = 0;
	private String _preparation = null;


	public Recipe(String name)
    {
        _name = name;
    }

    public String get_name()
    {
        return _name;
    }

	public void set_name(String _name) 
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
	
	public static boolean load_recipes(RecipesDB db)
	{
		boolean res = false;
		RecipeLoader loader = new RecipeLoader();
		res = loader.load_recipes(db);
		
		return res;
	}
	
	private static class RecipeLoader implements ContentHandler
	{
		private boolean _parsingRecipe = false;
		private boolean _parsingPreparation = false;
		private Recipe _recipe = null;
		private RecipesDB _db = null;
		
		public boolean load_recipes(RecipesDB db)
		{
			try
			{
				_db = db;
				String xmlSource = Environment.getExternalStorageDirectory().getPath();
				xmlSource += "/CoachACook/Recipes.xml";
				InputStream input = new FileInputStream(xmlSource);
				Xml.parse(input, Xml.Encoding.ISO_8859_1, this);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
				return false;
			}
			catch (SAXException e)
			{
				e.printStackTrace();
				return false;
			}
			return true;
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
		public void endDocument()
		{
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

		@Override
		public void endPrefixMapping(String prefix)
		{	
		}
	
		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
		{
		}
	
		@Override
		public void processingInstruction(String target, String data)
		{
		}
	
		@Override
		public void setDocumentLocator(Locator locator) 
		{
		}
	
		@Override
		public void skippedEntity(String name) 
		{
		}
	
		@Override
		public void startDocument() 
		{
		}
	
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs) 
		{
			if (localName.compareTo("Recipe") == 0)
			{
				_recipe = new Recipe("");
				_parsingRecipe = true;
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo("name") == 0)
						_recipe.set_name(attrs.getValue(i));
					else if (name.compareTo("guests") == 0)
						_recipe.set_guests(Integer.parseInt(attrs.getValue(i)));
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
		public void startPrefixMapping(String prefix, String uri)
		{
		}
	}
}

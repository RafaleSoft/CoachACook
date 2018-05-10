package com.rafalesoft.org.coachacook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Xml;

public class Ingredient 
{
	public static final String TABLE_NAME = "ingredients";
	public static final String _ID = "_id";
	public static final String COLUMN_NAME_TITLE = "name";
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

	public static boolean load_ingredients(RecipesDB db)
	{
		IngredientLoader loader = new IngredientLoader();
		boolean res = loader.load_ingredients(db);
		
		return res;
	}
		
	private static class IngredientLoader implements ContentHandler
	{
		private boolean _parsingStock = false;
		private RecipesDB _db = null;
		
		public boolean load_ingredients(RecipesDB db)
		{
			try
			{
				_db = db;
				String xmlSource = Environment.getExternalStorageDirectory().getPath();
				xmlSource += "/CoachACook/Ingredients.xml";
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
		}
	
		@Override
		public void endDocument()
		{
		}
	
		@Override
		public void endElement(String uri, String localName, String qName)
		{
			if (localName.compareTo("Stock") == 0)
				_parsingStock = false;
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
		public void startPrefixMapping(String prefix, String uri)
		{
		}
	}
}

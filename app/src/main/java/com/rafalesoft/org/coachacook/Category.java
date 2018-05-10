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



public class Category
{
	public static final String TABLE_NAME = "categories";
	public static final String _ID = "_id";
	public static final String COLUMN_CATEGORY_NAME_TITLE = "name";
	
	private String 	_name;
	
	
	public Category(String name)
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

	public static boolean load_categories(RecipesDB db)
	{
		CategoryLoader loader = new CategoryLoader();
		boolean res = loader.load_categories(db);
		
		return res;
	}
	
	private static class CategoryLoader implements ContentHandler
	{
		private boolean _parsingCategory = false;
		private RecipesDB _db = null;
		
		public boolean load_categories(RecipesDB db)
		{
			try
			{
				_db = db;
				String xmlSource = Environment.getExternalStorageDirectory().getPath();
				xmlSource = xmlSource + "/CoachACook/Categories.xml";
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
			// TODO Auto-generated method stub
		}

		@Override
		public void endDocument()
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void endElement(String uri, String localName, String qName)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void endPrefixMapping(String prefix)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void processingInstruction(String target, String data)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void skippedEntity(String name)
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void startDocument()
		{
			// TODO Auto-generated method stub
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attrs)
		{
			if (_parsingCategory)
			{
				Category newCategory = new Category("");
				int nbAttrs = attrs.getLength();
				for (int i=0;i<nbAttrs;i++)
				{
					String name = attrs.getLocalName(i);
					if (name.compareTo("name") == 0)
						newCategory.set_name(attrs.getValue(i));
				}
				_db.insert(newCategory);
			}
			else if (localName.compareTo("Stock") == 0)
				_parsingCategory = true;
			
		}

		@Override
		public void startPrefixMapping(String prefix, String uri)
		{
			// TODO Auto-generated method stub
		}
	}
}

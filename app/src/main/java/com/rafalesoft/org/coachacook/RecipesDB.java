package com.rafalesoft.org.coachacook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

public class RecipesDB
{
	private static final String DATABASE_NAME = "recipes.db";
    public static final String ID = "_id";
	private static final int DATABASE_VERSION = 2;
	private final ArrayList<RecipesCursorHolder> _cursors = new ArrayList<RecipesCursorHolder>();
	private final Map<String, Long> _categories = new HashMap<String, Long>();
	
	private DatabaseHelper _mOpenHelper;
	private SQLiteQueryBuilder _qb = new SQLiteQueryBuilder();
	private Context _context;

	
	public RecipesDB(Context ctx)
	{
		_context = ctx;
		_mOpenHelper = new DatabaseHelper(ctx);
		_categories.clear();
		
		SQLiteDatabase db = _mOpenHelper.getReadableDatabase();
		String query = "SELECT * FROM "+ Category.TABLE_NAME;
		Cursor c = db.rawQuery(query,null);
		c.moveToFirst();
		while (c.isAfterLast() == false)
		{
			long categoryId = c.getLong(c.getColumnIndex(ID));
			String category = c.getString(c.getColumnIndex(Category.COLUMN_CATEGORY_NAME_TITLE));
			_categories.put(category, categoryId);

			c.moveToNext();
		}
		c.close();
	}

	public void addCursorHolder(RecipesCursorHolder holder)
	{
		_cursors.add(holder);
	}
	public void delCursorHolder(RecipesCursorHolder holder)
	{
		_cursors.remove(holder);
	}

	public void close()
	{
		for (RecipesCursorHolder cursor:_cursors)
			cursor.close();
		_cursors.clear();
		_mOpenHelper.close();
	}
	
	public boolean reset()
	{
		SQLiteDatabase db = _mOpenHelper.getWritableDatabase(); 
		
		// Erase all available tables except android specifics
		Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		c.moveToFirst();
		while (c.isAfterLast() == false)
		{
			String s = c.getString(c.getColumnIndex("name"));
			if ((s.compareTo("android_metadata") != 0) && (s.compareTo("sqlite_sequence") != 0))
			{
				db.execSQL("DROP TABLE IF EXISTS " + s);
			}
		       
			c.moveToNext();
		}
		c.close();
		
		// Recreate application tables
		_mOpenHelper.onCreate(db);
		_categories.clear();
		
		return true;
	}
	
	public boolean updateData()
	{
        String xmlPath = _context.getFilesDir().getPath() + "/";

		boolean res = Category.load_categories(this, xmlPath+_context.getString(R.string.category_file));
		res = res && Ingredient.load_ingredients(this, xmlPath+_context.getString(R.string.ingredient_file));
		res = res && Recipe.load_recipes(this,xmlPath+_context.getString(R.string.recipe_file));
		return res;
	}
	

	/**
	 * Queries the database and returns a cursor containing the results.
	 *
	 * @return A cursor containing the results of the query. The cursor exists but is empty if
	 * the query returns no results or an exception occurs.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	public Cursor query(String table,
						String[] projection, 
						String selection, 
						String[] selectionArgs,
						String sortOrder) 
	{
	   // Constructs a new query builder and sets its table name
	   _qb.setTables(table);
	
	   // Opens the database object in "read" mode, since no writes need to be done.
	   SQLiteDatabase db = _mOpenHelper.getReadableDatabase();

	   //
	   // * Performs the query. If no problems occur trying to read the database, then a Cursor
	   // * object is returned; otherwise, the cursor variable contains null. If no records were
	   // * selected, then the Cursor object is empty, and Cursor.getCount() returns 0.
	   // 
	   Cursor c = _qb.query(
	       db,            // The database to query
	       projection,    // The columns to return from the query
	       selection,     // The columns for the where clause
	       selectionArgs, // The values for the where clause
	       null, // don't group the rows
	       null,  // don't filter by row groups
	       sortOrder);    // The sort order
	   
	   return c;
	}


	public long insert(Category category)
	{
	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(Category.COLUMN_CATEGORY_NAME_TITLE, category.get_name());
		
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();
	
	    // Performs the insert and returns the ID of the new recipe.
	    long rowId = db.insert(
	        Category.TABLE_NAME,        // The table to insert into.
	        Category.COLUMN_CATEGORY_NAME_TITLE,  // A hack, SQLite sets this column value to null
	                                         // if values is empty.
	        values                           // A map of column names, and the values to insert
	                                         // into the columns.
	    );
	
	    // If the insert succeeded, the row ID exists.
	    if (rowId > 0)
	    {
	    	_categories.put(category.get_name(), rowId);
	    	return rowId;
	    }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
    	throw new SQLException("Failed to insert category:" + category.get_name());
	}

	public long insert(Recipe recipe) 
	{
	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(Recipe.COLUMN_NAME_TITLE, recipe.get_name());
	    values.put(Recipe.COLUMN_GUESTS_TITLE,recipe.get_guests());
	    values.put(Recipe.COLUMN_PREPARATION_TITLE, recipe.get_preparation());
		
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();
	
	    // Performs the insert and returns the ID of the new recipe.
	    long rowId = db.insert(
	        Recipe.TABLE_NAME,        // The table to insert into.
	        Recipe.COLUMN_NAME_TITLE,  // A hack, SQLite sets this column value to null
	                                         // if values is empty.
	        values                           // A map of column names, and the values to insert
	                                         // into the columns.
	    );
	
	    // If the insert succeeded, the row ID exists.
	    if (rowId > 0)
	    {
	    	for (int i=0;i<recipe.nbComponents();i++)
	    	{
	    		RecipeComponent component = recipe.getComponent(i);

	    		Cursor c = db.rawQuery(	"SELECT "+ID+" FROM "+Ingredient.TABLE_NAME+
	    								" WHERE "+Ingredient.COLUMN_NAME_TITLE+"='"+component.get_name()+"'",
	    								null);
	    		c.moveToFirst();
	    		int ingredientId = -1;
	    		if (c.getCount() > 0)
	    			ingredientId = c.getInt(c.getColumnIndex(ID));
	    		c.close();
	    		
	    		if (ingredientId > 0)
	    		{
		    		// A map to hold the new record's values.
		    	    ContentValues cmp_values = new ContentValues();
		    	    cmp_values.put(RecipeComponent.COLUMN_RECIPE_TITLE, rowId);
		    	    cmp_values.put(RecipeComponent.COLUMN_INGREDIENT_TITLE, ingredientId);
		    	    cmp_values.put(RecipeComponent.COLUMN_AMOUNT_TITLE, component.get_quantity());
		    	    cmp_values.put(RecipeComponent.COLUMN_UNIT_TITLE, component.get_unit());
		    	
		    	    // Performs the insert and returns the ID of the new recipe.
		    	    db.insert(
		    	        RecipeComponent.TABLE_NAME,// The table to insert into.
		    	        RecipeComponent.COLUMN_RECIPE_TITLE,  // A hack, SQLite sets this column value to null if values is empty.
		    	        cmp_values                 // A map of column names, and the values to insert into the columns.
		    	    );
	    		}
	    		else
	    		{
	    			String message = _context.getResources().getString(R.string.invalid_recipe);
	    			message += ": ";
	    			message += recipe.get_name();
	    			Toast toast = Toast.makeText(_context, message, Toast.LENGTH_LONG);
	    			toast.show();
	    		}
	    	}
	    	
	        // Notifies observers registered against this provider that the data changed.
	        //getContext().getContentResolver().notifyChange(noteUri, null);
	        return rowId;
	    }
	
	    // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
	    throw new SQLException("Failed to insert recipe:" + recipe.get_name());
	}
	
	public long insert(Ingredient ingredient) 
	{
		Long idCategory = _categories.get(ingredient.get_name());
	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(Ingredient.COLUMN_NAME_TITLE, ingredient.get_name());
	    values.put(Ingredient.COLUMN_STOCK_TITLE, ingredient.get_quantity());
	    values.put(Ingredient.COLUMN_UNIT_TITLE, ingredient.get_unit());
	    values.put(Ingredient.COLUMN_TYPE_TITLE, idCategory);
		
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();
	
	    // Performs the insert and returns the ID of the new note.
	    long rowId = db.insert(
	        Ingredient.TABLE_NAME,        // The table to insert into.
	        Ingredient.COLUMN_NAME_TITLE,  // A hack, SQLite sets this column value to null
	                                         // if values is empty.
	        values                           // A map of column names, and the values to insert
	                                         // into the columns.
	    );
	
	    // If the insert succeeded, the row ID exists.
	    if (rowId > 0)
	    {
	        // Notifies observers registered against this provider that the data changed.
	        //getContext().getContentResolver().notifyChange(noteUri, null);
	        return rowId;
	    }
	
	    // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
	    throw new SQLException("Failed to insert recipe:" + ingredient.get_name());
	}

	public Recipe getRecipe(String name)
	{
		// Opens the database object in "read" mode, since no writes need to be done.
		SQLiteDatabase db = _mOpenHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery(	"SELECT *"+
								" FROM "+Recipe.TABLE_NAME+
								" WHERE "+Recipe.COLUMN_NAME_TITLE+"='"+name+"'",
								null);
		c.moveToFirst();
		int recipeId = c.getInt(c.getColumnIndex(ID));
		Recipe recipe = new Recipe(name);
		recipe.set_guests(c.getInt(c.getColumnIndex(Recipe.COLUMN_GUESTS_TITLE)));
		recipe.set_preparation(c.getString(c.getColumnIndex(Recipe.COLUMN_PREPARATION_TITLE)));
		c.close();
		
		c = db.rawQuery("SELECT *"+ //RecipeComponent.COLUMN_INGREDIENT_TITLE+
						" FROM "+ RecipeComponent.TABLE_NAME+
						" WHERE "+ RecipeComponent.COLUMN_RECIPE_TITLE+"="+recipeId,
						null);
		c.moveToFirst();
		while (c.isAfterLast() == false)
		{
			int ingredientId = c.getInt(c.getColumnIndex(RecipeComponent.COLUMN_INGREDIENT_TITLE));
			double quantity = c.getDouble(c.getColumnIndex(RecipeComponent.COLUMN_AMOUNT_TITLE));
			String unit = c.getString(c.getColumnIndex(RecipeComponent.COLUMN_UNIT_TITLE));
			
			Cursor c2 = db.rawQuery("SELECT "+Ingredient.COLUMN_NAME_TITLE+
									" FROM "+Ingredient.TABLE_NAME+
									" WHERE "+ID+"="+ingredientId,
									null);
			
			RecipeComponent component = new RecipeComponent();
			c2.moveToFirst();
			component.set_name(c2.getString(c2.getColumnIndex(Ingredient.COLUMN_NAME_TITLE)));
			component.set_quantity(quantity);
			component.set_unit(unit);
			recipe.addComponent(component);
		    
			c2.close();
			c.moveToNext();
		}
		c.close();

		return recipe;
	}

	public Recipe selectRecipe() 
	{
		// Opens the database object in "read" mode, since no writes need to be done.
		SQLiteDatabase db = _mOpenHelper.getReadableDatabase();
		
		String query = "SELECT DISTINCT " + RecipeComponent.COLUMN_RECIPE_TITLE +
				" FROM "+RecipeComponent.TABLE_NAME+", "+Ingredient.TABLE_NAME+" AS i"+
				" WHERE "+RecipeComponent.COLUMN_INGREDIENT_TITLE+ " = i."+ID+
				" AND "+RecipeComponent.COLUMN_AMOUNT_TITLE+" <= i."+Ingredient.COLUMN_STOCK_TITLE;
		Cursor c = db.rawQuery(	query, null);
		if (c.getCount() < 1)
		{
			c.close();
			return null;
		}
		else
		{
			c.moveToFirst();
			int recipeId = c.getInt(c.getColumnIndex(RecipeComponent.COLUMN_RECIPE_TITLE));
			c.close();
			
			Cursor c2 = db.rawQuery("SELECT "+Recipe.COLUMN_NAME_TITLE+
					" FROM "+Recipe.TABLE_NAME+
					" WHERE "+ID+"="+recipeId,
					null);
			c2.moveToFirst();
			String name = c2.getString(c2.getColumnIndex(Recipe.COLUMN_NAME_TITLE));
			c2.close();
		
			return getRecipe(name);
		}
	}

	public boolean updateStock(String name, Double amount)
	{
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();

	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(Ingredient.COLUMN_STOCK_TITLE, amount);
		
	    String [] whereArgs = { name };

	    int res = db.update(Ingredient.TABLE_NAME, values, 
	    					Ingredient.COLUMN_NAME_TITLE+"=?", whereArgs);
	    
	    return (res == 1);
	}
	
	

	static class DatabaseHelper extends SQLiteOpenHelper 
	{
	   DatabaseHelper(Context context) 
	   { 
		   super(context, DATABASE_NAME, null, DATABASE_VERSION);
	   }

	   /**
	    * Creates the underlying database with table name and column 
	    * names taken from the Recipe class.
	    */
	   @Override
	   public void onCreate(SQLiteDatabase db) 
	   {   
	       db.execSQL("CREATE TABLE " + Recipe.TABLE_NAME + " ("
	               + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	               + Recipe.COLUMN_NAME_TITLE + " VARCHAR(32) NOT NULL,"
	               + Recipe.COLUMN_GUESTS_TITLE + " INTEGER,"
	               + Recipe.COLUMN_PREPARATION_TITLE + " TEXT"
	               + ");");
	       
	       db.execSQL("CREATE TABLE " + RecipeComponent.TABLE_NAME + " ("
	               + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	               + RecipeComponent.COLUMN_RECIPE_TITLE + " INTEGER,"
	               + RecipeComponent.COLUMN_INGREDIENT_TITLE + " INTEGER,"
	               + RecipeComponent.COLUMN_AMOUNT_TITLE + " REAL,"
	               + RecipeComponent.COLUMN_UNIT_TITLE + " VARCHAR(4)"
	               + ");");
	       
	       db.execSQL("CREATE TABLE " + Ingredient.TABLE_NAME + " ("
	               + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	               + Ingredient.COLUMN_NAME_TITLE + " VARCHAR(32) NOT NULL,"
	               + Ingredient.COLUMN_STOCK_TITLE + " REAL,"
	               + Ingredient.COLUMN_UNIT_TITLE + " VARCHAR(4),"
	               + Ingredient.COLUMN_TYPE_TITLE + " INTEGER"
	               + ");");
	       
	       db.execSQL("CREATE TABLE " + Category.TABLE_NAME + " ("
	               + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	               + Category.COLUMN_CATEGORY_NAME_TITLE + " VARCHAR(32) NOT NULL"
	               + ");");
	   }

	   /**
	    * Demonstrates that the provider must consider what happens when the
	    * underlying datastore is changed. In this sample, the database is 
	    * upgraded the database by destroying the existing data.
	    * A real application should upgrade the database in place.
	    */
	   @Override
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	   {
	       // Kills the table and existing data
		   db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
	       db.execSQL("DROP TABLE IF EXISTS " + Recipe.TABLE_NAME);
	       db.execSQL("DROP TABLE IF EXISTS " + Ingredient.TABLE_NAME);
	       db.execSQL("DROP TABLE IF EXISTS " + RecipeComponent.TABLE_NAME);

	       // Recreates the database with a new version
	       onCreate(db);
	   }
	}
}

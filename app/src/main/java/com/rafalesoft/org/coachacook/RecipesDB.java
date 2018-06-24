package com.rafalesoft.org.coachacook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;


public class RecipesDB
{
	private static final String DATABASE_NAME = "recipes.db";

    public static final String ID = "_id";
    public static final String NAME = "name";
	private static final int DATABASE_VERSION = 7;
	private final ArrayList<RecipesCursorHolder> _cursors = new ArrayList<>();

	
	private final DatabaseHelper _mOpenHelper;
	private final SQLiteQueryBuilder _qb = new SQLiteQueryBuilder();
	private final CoachACook _context;

	
	public RecipesDB(CoachACook ctx)
	{
		_context = ctx;
		_mOpenHelper = new DatabaseHelper(ctx);
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
        boolean clear = _mOpenHelper.clearDatabase(db);

		// Recreate application tables
		if (clear)
			_mOpenHelper.onCreate(db);

		return clear;
	}

	public boolean updateData()
	{
        boolean res = Ingredient.load_ingredients();
        res = res && Recipe.load_recipes();
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
						String[] selectionArgs)
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

		return _qb.query(
            db,            // The database to query
            projection,    // The columns to return from the query
            selection,     // The columns for the where clause
            selectionArgs, // The values for the where clause
            null, // don't group the rows
            null,  // don't filter by row groups
			RecipesDB.NAME);
	}

	public long insert(Recipe recipe)
	{
	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(NAME, recipe.get_name());
	    values.put(Recipe.COLUMN_GUESTS_TITLE,recipe.get_guests());
	    values.put(Recipe.COLUMN_PREPARATION_TITLE, recipe.get_preparation());
        values.put(Recipe.COLUMN_DIFFICULTY_TITLE, recipe.get_difficulty());
        values.put(Recipe.COLUMN_COST_TITLE, recipe.get_cost());
        values.put(Recipe.COLUMN_PREPARE_TITLE, recipe.get_prepare_time());
        values.put(Recipe.COLUMN_TIME_TITLE, recipe.get_cook_time());
		
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();
	
	    // Performs the insert and returns the ID of the new recipe.
	    long rowId = db.insert(	Recipe.TABLE_NAME,	// The table to insert into.
	        					NAME,  				// A hack, SQLite sets this column value to null if values is empty.
	        					values  );			// A map of column names, and the values to insert into the columns.

	    // If the insert succeeded, the row ID exists.
	    if (rowId > 0)
	    {
	    	for (int i=0;i<recipe.nbComponents();i++)
	    	{
	    		RecipeComponent component = recipe.getComponent(i);

	    		Cursor c = db.rawQuery(	"SELECT " + ID + " FROM " + Ingredient.TABLE_NAME+
	    								" WHERE " + NAME +"='"+component.get_name()+"'",
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
		    	    cmp_values.put(RecipeComponent.COLUMN_UNIT_TITLE, component.get_unit().toString());
		    	
		    	    // Performs the insert and returns the ID of the new recipe.
		    	    db.insert(	RecipeComponent.TABLE_NAME,// The table to insert into.
		    	        		RecipeComponent.COLUMN_RECIPE_TITLE,  // A hack, SQLite sets this column value to null if values is empty.
		    	        		cmp_values                 // A map of column names, and the values to insert into the columns.
		    	    );
	    		}
	    		else
                    throw new SQLException(_context.getResources().getString(R.string.invalid_recipe) +
											": " + recipe.get_name() +
											"(unknown ingredient " + component.get_name() + ")");
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
        long idCategory;
	    try
        {
            // ordinal is 0 based
            idCategory = ingredient.get_type().ordinal();
        }
		catch (IllegalArgumentException e)
        {
            Log.d("DB","Invalid Ingredient Category");
            return -1;
        }

	    // A map to hold the new record's values.
	    ContentValues values = new ContentValues();
	    values.put(NAME, ingredient.get_name());
	    values.put(Ingredient.COLUMN_STOCK_TITLE, ingredient.get_quantity());
	    values.put(Ingredient.COLUMN_UNIT_TITLE, ingredient.get_unit().ordinal());
	    values.put(Ingredient.COLUMN_TYPE_TITLE, idCategory);
		
	    // Opens the database object in "write" mode.
	    SQLiteDatabase db = _mOpenHelper.getWritableDatabase();
	
	    // Performs the insert and returns the ID of the new note.
	    long rowId = db.insert(	Ingredient.TABLE_NAME,  // The table to insert into.
	        					NAME,  					// A hack, SQLite sets this column value to null if values is empty.
	        					values );				// A map of column names, and the values to insert into the columns.

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

	public Ingredient getIngredient(String name)
	{
		// Opens the database object in "read" mode, since no writes need to be done.
		SQLiteDatabase db = _mOpenHelper.getReadableDatabase();

		Cursor c = db.rawQuery(	"SELECT * FROM " + Ingredient.TABLE_NAME+
						" WHERE " + NAME + "='"+name+"'", null);
		c.moveToFirst();

		Ingredient ingredient = new Ingredient();
		if (c.getCount() > 0)
		{
			ingredient.set_name(c.getString(c.getColumnIndex(NAME)));
			ingredient.set_quantity(c.getDouble(c.getColumnIndex(Ingredient.COLUMN_STOCK_TITLE)));
			ingredient.set_unit(Unit.values()[c.getInt(c.getColumnIndex(Ingredient.COLUMN_UNIT_TITLE))]);
			ingredient.set_type(Category.Model.values()[c.getInt(c.getColumnIndex(Ingredient.COLUMN_TYPE_TITLE))]);
		}
		c.close();

		return ingredient;
	}

	public Recipe getRecipe(String name)
	{
		// Opens the database object in "read" mode, since no writes need to be done.
		SQLiteDatabase db = _mOpenHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery(	"SELECT * FROM " + Recipe.TABLE_NAME +
								" WHERE " + NAME + "='" + name + "'",null);
		c.moveToFirst();
		int recipeId = c.getInt(c.getColumnIndex(ID));
		Recipe recipe = new Recipe(name);
		recipe.set_guests(c.getInt(c.getColumnIndex(Recipe.COLUMN_GUESTS_TITLE)));
		recipe.set_preparation(c.getString(c.getColumnIndex(Recipe.COLUMN_PREPARATION_TITLE)));
		c.close();
		
		c = db.rawQuery("SELECT *"+ //RecipeComponent.COLUMN_INGREDIENT_TITLE+
						" FROM "+ RecipeComponent.TABLE_NAME +
						" WHERE "+ RecipeComponent.COLUMN_RECIPE_TITLE + "=" + recipeId,
						null);
		c.moveToFirst();
		while (!c.isAfterLast())
		{
			int ingredientId = c.getInt(c.getColumnIndex(RecipeComponent.COLUMN_INGREDIENT_TITLE));
			double quantity = c.getDouble(c.getColumnIndex(RecipeComponent.COLUMN_AMOUNT_TITLE));
			String unit = c.getString(c.getColumnIndex(RecipeComponent.COLUMN_UNIT_TITLE));
			
			Cursor c2 = db.rawQuery("SELECT " + NAME + " FROM " + Ingredient.TABLE_NAME +
									" WHERE " + ID + "=" + ingredientId, null);
			
			RecipeComponent component = new RecipeComponent();
			c2.moveToFirst();
			component.set_name(c2.getString(c2.getColumnIndex(NAME)));
			component.set_quantity(quantity);
			component.set_unit(Unit.parse(unit));
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
			
			Cursor c2 = db.rawQuery("SELECT " + NAME + " FROM " + Recipe.TABLE_NAME +
									" WHERE " + ID + "=" + recipeId, null);
			c2.moveToFirst();
			String name = c2.getString(c2.getColumnIndex(NAME));
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

	    int res = db.update(Ingredient.TABLE_NAME, values, NAME + "=?", whereArgs);
	    
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
		   try
           {
                db.execSQL(Recipe.getTableQuery());
                db.execSQL(RecipeComponent.getTableQuery());
                db.execSQL(Ingredient.getTableQuery());
    		    db.execSQL(Nutriments.getTableQuery());
           }
	       catch (SQLException e)
		   {
			   Log.e("RecipeDB","Create database failed");
		   }
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
           clearDatabase(db);

	       // Recreates the database with a new version
	       onCreate(db);
	   }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // Kills the table and existing data
            clearDatabase(db);

            // Recreates the database with a new version
            onCreate(db);
        }

        private boolean clearDatabase(SQLiteDatabase db)
        {
        	try
			{
				String query = "DROP TABLE IF EXISTS " + Recipe.TABLE_NAME;
				db.execSQL(query);

				query = "DROP TABLE IF EXISTS " + Ingredient.TABLE_NAME;
				db.execSQL(query);

				query = "DROP TABLE IF EXISTS " + RecipeComponent.TABLE_NAME;
				db.execSQL(query);

                query = "DROP TABLE IF EXISTS " + Nutriments.TABLE_NAME;
                db.execSQL(query);
			}
			catch (SQLException e)
			{
				Log.e("RecipeDB","Clear database failed");
				return false;
			}

			return true;
        }
	}
}

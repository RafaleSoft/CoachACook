package com.rafalesoft.org.coachacook;

import android.database.Cursor;

class RecipesCursorHolder
{
	private Cursor _cursor = null;
	static protected CoachACook _cook = null;
	
	RecipesCursorHolder()
	{
        RecipesDB recipesDB = _cook.getRecipesDB();
        recipesDB.addCursorHolder(this);
	}

	public static void setCook(CoachACook owner)
	{
        _cook = owner;
	}
	Cursor getCursor()
	{
		return _cursor;
	}

	void updateCursor(String tableName, String[] projection)
	{
		close();
		_cursor = _cook.getRecipesDB().query(	tableName,
												projection);
	}

	public void close()
	{
        // Remove from RecipesDB is done at destruction, by calling RecipesDB.close()

		if (_cursor != null)
		{
			_cook.getRecipesDB().delCursorHolder(this);
			_cursor.close();
			_cursor = null;
		}
	}
}

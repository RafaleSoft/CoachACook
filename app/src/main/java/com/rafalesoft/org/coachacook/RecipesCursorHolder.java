package com.rafalesoft.org.coachacook;

import android.database.Cursor;

class RecipesCursorHolder
{
	private Cursor _cursor = null;
	CoachACook _cook;
	
	RecipesCursorHolder(CoachACook owner)
	{
		_cook = owner;
        RecipesDB recipesDB = _cook.getRecipesDB();
        recipesDB.addCursorHolder(this);
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
			_cursor.close();
	}
}

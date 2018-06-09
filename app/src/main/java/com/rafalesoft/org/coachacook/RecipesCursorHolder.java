package com.rafalesoft.org.coachacook;

import android.database.Cursor;

class RecipesCursorHolder
{
	private Cursor _cursor = null;
	static CoachACook _cook = null;
	
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
		String[] selectionArgs = {};
		_cursor = _cook.getRecipesDB().query(	tableName,
												projection,
                                                "",
                                                selectionArgs);
	}

    void updateCursor(String tableName,
                      String[] projection,
                      String selection,
                      String[] selectionArgs)
    {
        close();
        _cursor = _cook.getRecipesDB().query(	tableName,
                                                projection,
                                                selection,
                                                selectionArgs);
    }

	public void close()
	{
		if (_cursor != null)
		{
			// Remove from RecipesDB is done at destruction, by calling RecipesDB.close()
			_cursor.close();
			_cursor = null;
		}
	}
}

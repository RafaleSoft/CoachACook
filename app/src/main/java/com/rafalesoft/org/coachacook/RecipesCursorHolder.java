package com.rafalesoft.org.coachacook;

import android.database.Cursor;

class RecipesCursorHolder
{
	private Cursor _cursor = null;

	RecipesCursorHolder()
	{
        RecipesDB recipesDB = CoachACook.getCoach().getRecipesDB();
        recipesDB.addCursorHolder(this);
	}

	Cursor getCursor()
	{
		return _cursor;
	}

	void updateCursor(String tableName, String[] projection)
	{
		close();
		String[] selectionArgs = {};
		_cursor = CoachACook.getCoach().getRecipesDB().query(	tableName,
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
        _cursor = CoachACook.getCoach().getRecipesDB().query(	tableName,
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

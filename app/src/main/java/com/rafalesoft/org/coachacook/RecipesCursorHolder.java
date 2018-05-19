package com.rafalesoft.org.coachacook;

import android.database.Cursor;

public class RecipesCursorHolder 
{
	protected Cursor _cursor = null;
	protected CoachACook _cook;
	
	public RecipesCursorHolder(CoachACook owner)
	{
		_cook = owner;
        RecipesDB recipesDB = _cook.getRecipesDB();
        recipesDB.addCursorHolder(this);
	}
	
	public void close()
	{
        // Remove from RecipesDB is done at destruction, by calling RecipesDB.close()

		if (_cursor != null)
			_cursor.close();
	}
}

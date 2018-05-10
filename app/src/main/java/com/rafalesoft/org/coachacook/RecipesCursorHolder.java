package com.rafalesoft.org.coachacook;

import android.database.Cursor;

public class RecipesCursorHolder 
{
	protected Cursor _cursor = null;
	protected CoachACook _cook;
	
	public RecipesCursorHolder(CoachACook owner)
	{
		_cook = owner;
	}
	
	public void close()
	{
		if (_cursor != null)
			_cursor.close();
	}
}

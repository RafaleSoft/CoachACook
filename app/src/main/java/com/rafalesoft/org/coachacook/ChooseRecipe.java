package com.rafalesoft.org.coachacook;

import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


class ChooseRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    private final RecipeViewer _viewer = new RecipeViewer();

    public ChooseRecipe() { }


    private class RecipeCursorAdapter implements SimpleCursorAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex)
        {
            if (view.getId() == R.id.recipe_item_name)
            {
                String text = cursor.getString(columnIndex);
                ((TextView) view).setText(text);
                return true;
            }
            else if ((view.getId() == R.id.prepare_duration) || (view.getId() == R.id.cook_duration))
            {
                int minutes = cursor.getInt(columnIndex);
                int hours = minutes / 60;
                minutes = minutes - 60 * hours;
                ((TextView) view).setText(new StringBuilder().append(hours).append(':').append(minutes));
                return true;
            }
            else if (view instanceof ProgressBar)
            {
                int progress = cursor.getInt(columnIndex);
                ((ProgressBar) view).setProgress(progress);
                return true;
            }
            else
                return false;
        }
    }

	@Override
	public void onClick(View v)
	{
        View stock = CoachACook.getCoach().switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = { RecipesDB.ID,
                                RecipesDB.NAME,
                                Recipe.COLUMN_DIFFICULTY,
                                Recipe.COLUMN_COST,
                                Recipe.COLUMN_PREPARE,
                                Recipe.COLUMN_TIME};
        updateCursor( Recipe.TABLE_NAME, projection );

        String[] fromColumns = { RecipesDB.NAME, Recipe.COLUMN_DIFFICULTY, Recipe.COLUMN_COST, Recipe.COLUMN_PREPARE, Recipe.COLUMN_TIME};
        int[] toViews = { R.id.recipe_item_name , R.id.recipe_difficulty_progress, R.id.recipe_cost_progress, R.id.prepare_duration, R.id.cook_duration};

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(CoachACook.getCoach(), R.layout.recipe_stockview_item,
                                        getCursor(), fromColumns, toViews, 0);
        recipesDBAdapter.setViewBinder(new RecipeCursorAdapter());

        lvl.setAdapter(recipesDBAdapter);
        lvl.setOnItemClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        _viewer.initView(view);
    }
}

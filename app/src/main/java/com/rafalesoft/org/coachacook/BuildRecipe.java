package com.rafalesoft.org.coachacook;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class BuildRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    private final RecipeViewer _viewer = new RecipeViewer();
    private SharedPreferences _preferences;

    public BuildRecipe()
    {
        Context ctx = CoachACook.getCoach();
        _preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    private class RecipeAdapter implements SimpleAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation)
        {
            if (data instanceof Recipe)
            {
                Recipe recipe = (Recipe)data;

                switch (view.getId())
                {
                    case R.id.recipe_item_name:
                    {
                        ((TextView) view).setText(recipe.get_name());
                        break;
                    }
                    case R.id.recipe_difficulty_progress:
                    {
                        ((ProgressBar) view).setProgress(recipe.get_difficulty());
                        break;
                    }
                    case R.id.recipe_cost_progress:
                    {
                        ((ProgressBar) view).setProgress(recipe.get_cost());
                        break;
                    }
                    case R.id.prepare_duration:
                    {
                        int minutes = recipe.get_prepare_time();
                        int hours = minutes / 60;
                        minutes = minutes - 60 * hours;
                        ((TextView) view).setText(new StringBuilder().append(hours).append(':').append(minutes));
                        break;
                    }
                    case R.id.cook_duration:
                    {
                        int minutes = recipe.get_cook_time();
                        int hours = minutes / 60;
                        minutes = minutes - 60 * hours;
                        ((TextView) view).setText(new StringBuilder().append(hours).append(':').append(minutes));
                        break;
                    }
                    default:
                        return false;
                }

                return true;
            }
            else
                return false;
        }
    }

	@Override
	public void onClick(View v)
	{
	    //TODO : make onClick content asynchronous (AsyncContentProvider ?)
        ArrayList<Recipe> recipes = genRecipes();

        View stock = CoachACook.getCoach().switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        List<Map<String,Object>> data = new ArrayList<>();
        for (Recipe r:recipes)
        {
            Map<String,Object> recipe = new ArrayMap<>();
            recipe.put("N",r);
            recipe.put("D",r);
            recipe.put("C",r);
            recipe.put("P",r);
            recipe.put("T",r);

            data.add(recipe);
        }

        String[] fromColumns = { "N", "D", "C", "P", "T" };
        int[] toViews = { R.id.recipe_item_name , R.id.recipe_difficulty_progress, R.id.recipe_cost_progress, R.id.prepare_duration, R.id.cook_duration};

        SimpleAdapter adapter = new SimpleAdapter(CoachACook.getCoach(), data, R.layout.recipe_stockview_item, fromColumns, toViews);
        adapter.setViewBinder(new BuildRecipe.RecipeAdapter());

        lvl.setAdapter(adapter);
        lvl.setOnItemClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        _viewer.initView(view);
    }

    private ArrayList<Recipe> genRecipes()
    {
        String projection [] = {RecipesDB.NAME,
                                Ingredient.COLUMN_STOCK,
                                Ingredient.COLUMN_UNIT};
        String selectionArgs [] = {};
        updateCursor(Ingredient.TABLE_NAME,projection,Ingredient.COLUMN_STOCK + " > 0",selectionArgs);
        Cursor cursor = getCursor();

        Map<String,Amount> stock = new ArrayMap<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Amount a = new Amount();
            a.set_quantity(cursor.getDouble(1));
            a.set_unit(Unit.values()[cursor.getInt(2)]);
            stock.put(cursor.getString(0),a);
            cursor.moveToNext();
        }

        String projection2 [] = {   RecipesDB.NAME,
                                    RecipesDB.ID,
                                    Recipe.COLUMN_DIFFICULTY,
                                    Recipe.COLUMN_COST,
                                    Recipe.COLUMN_PREPARE,
                                    Recipe.COLUMN_TIME,
                                    Recipe.COLUMN_GUESTS};
        updateCursor(Recipe.TABLE_NAME,projection2);
        cursor = getCursor();

        RecipesDB recipesDB = CoachACook.getCoach().getRecipesDB();
        String key = CoachACook.getCoach().getString(R.string.number_guests_key);
        int nbGuests = Integer.parseInt(_preferences.getString(key,"1"));

        ArrayList<Recipe> recipes = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            String recipe_name = cursor.getString(0);
            Recipe recipe = recipesDB.getRecipe(recipe_name);
            Double distance = 0.0;
            for (int i=0;i<recipe.nbComponents();i++)
            {
                RecipeComponent c = recipe.getComponent(i);
                Amount s = stock.get(c.get_name());
                Amount n = c.get_amount();

                //  If stock contains less than required
                if (s.compareTo(n) < 0)
                    distance = distance + c.get_quantity() * c.get_unit().getSIFactor();
            }

            if (distance >= 0)
                recipes.add(recipe);

            cursor.moveToNext();
        }

        return recipes;
    }
}

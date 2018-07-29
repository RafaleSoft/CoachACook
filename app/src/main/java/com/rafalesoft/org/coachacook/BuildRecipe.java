package com.rafalesoft.org.coachacook;

import android.database.Cursor;
import android.util.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class BuildRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    public BuildRecipe() { }

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
        View recipeView = CoachACook.getCoach().switchToView(R.id.recipe_view);

        TextView recipe_name = view.findViewById(R.id.recipe_item_name);
        TextView tv_name = recipeView.findViewById(R.id.recipe_name);
        tv_name.setText(recipe_name.getText());

        Recipe r = CoachACook.getCoach().getRecipesDB().getRecipe(recipe_name.getText().toString());

        TextView tv_desc = recipeView.findViewById(R.id.recipe_description);
        tv_desc.setText(r.get_preparation());

        List<String> component_list = new ArrayList<>();
        for (int item=0;item<r.nbComponents();item++)
        {
            RecipeComponent c = r.getComponent(item);
            String ingredient = Integer.valueOf(c.get_quantity().intValue()).toString() +
                    " " + (null == c.get_unit() ? "":c.get_unit()) + " " + c.get_name();
            component_list.add(ingredient);
        }

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (CoachACook.getCoach(), android.R.layout.simple_list_item_1, component_list);

        ListView table = recipeView.findViewById(R.id.recipe_ingredients);
        table.setAdapter(arrayAdapter);
    }

    private ArrayList<Recipe> genRecipes()
    {
        String projection [] = {RecipesDB.NAME,
                                Ingredient.COLUMN_TYPE_TITLE,
                                Ingredient.COLUMN_STOCK_TITLE,
                                Ingredient.COLUMN_UNIT_TITLE};
        String selectionArgs [] = {};
        updateCursor(Ingredient.TABLE_NAME,projection,"Ingredient.COLUMN_STOCK_TITLE > 0",selectionArgs);
        Cursor cursor = getCursor();

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Ingredient i = new Ingredient();
            i.set_name(cursor.getString(0));
            i.set_type(Category.Model.values()[cursor.getInt(1)]);
            i.set_quantity(cursor.getDouble(2));
            i.set_unit(Unit.values()[cursor.getInt(3)]);
            cursor.moveToNext();
        }

        String projection2 [] = {   RecipesDB.NAME,
                                    RecipesDB.ID,
                                    Recipe.COLUMN_DIFFICULTY_TITLE,
                                    Recipe.COLUMN_COST_TITLE,
                                    Recipe.COLUMN_PREPARE_TITLE,
                                    Recipe.COLUMN_TIME_TITLE,
                                    Recipe.COLUMN_GUESTS_TITLE};
        updateCursor(Recipe.TABLE_NAME,projection2);
        cursor = getCursor();

        ArrayList<Recipe> recipes = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {

        }

        return recipes;
    }
}

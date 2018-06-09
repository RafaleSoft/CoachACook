package com.rafalesoft.org.coachacook;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class BuildRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    public BuildRecipe() { }

	@Override
	public void onClick(View v)
	{
        Recipe r = genRecipe();

        View stock = _cook.switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = {RecipesDB.ID, RecipesDB.NAME};
        updateCursor(Recipe.TABLE_NAME, projection);

        String[] fromColumns = { RecipesDB.NAME };
        int[] toViews = { R.id.recipe_item_name };

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(_cook, R.layout.recipe_stockview_item,
                                        getCursor(), fromColumns, toViews, 0);

        lvl.setAdapter(recipesDBAdapter);
        lvl.setOnItemClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        View recipeView = _cook.switchToView(R.id.recipe_view);

        TextView recipe_name = view.findViewById(R.id.recipe_item_name);
        TextView tv_name = recipeView.findViewById(R.id.recipe_name);
        tv_name.setText(recipe_name.getText());

        Recipe r = _cook.getRecipesDB().getRecipe(recipe_name.getText().toString());

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
                (_cook, android.R.layout.simple_list_item_1, component_list);

        ListView table = recipeView.findViewById(R.id.recipe_ingredients);
        table.setAdapter(arrayAdapter);
    }

    private Recipe genRecipe()
    {
        Recipe r = _cook.getRecipesDB().selectRecipe();

        return r;
    }
}

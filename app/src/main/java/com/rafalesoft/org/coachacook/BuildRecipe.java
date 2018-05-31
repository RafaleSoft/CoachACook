package com.rafalesoft.org.coachacook;

import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;


public class BuildRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    public BuildRecipe(CoachACook owner)
	{
		super(owner);
	}

    private static final int components[] = {   R.id.component1,
                                                R.id.component2,
                                                R.id.component3,
                                                R.id.component4,
                                                R.id.component5,
                                                R.id.component6,
                                                R.id.component7};

	@Override
	public void onClick(View v)
	{
        Recipe r = genRecipe();

        View stock = _cook.switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = {RecipesDB.ID, RecipesDB.NAME};
        String[] selectionArgs = {};

        _cursor = _cook.getRecipesDB().query(   Recipe.TABLE_NAME,
                                                projection,
                                                "",
                                                selectionArgs,
                                                RecipesDB.NAME);

        String[] fromColumns = { RecipesDB.NAME };
        int[] toViews = { R.id.recipe_item_name };

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(_cook, R.layout.recipe_stockview_item,
                        _cursor, fromColumns, toViews, 0);

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

        TableLayout table = recipeView.findViewById(R.id.recipe_ingredients);

        for (int item=0;item<r.nbComponents();item++)
        {
            RecipeComponent c = r.getComponent(item);
            View row = table.getChildAt(item);
            TextView tvi = row.findViewById(components[item]);

            String ingredient = Integer.valueOf(c.get_quantity().intValue()).toString() +
                    " " + (null == c.get_unit() ? "":c.get_unit()) + " " + c.get_name();
            tvi.setText(ingredient);
        }

        for (int j=r.nbComponents();j<7;j++)
        {
            View row = table.getChildAt(j);
            TextView tvi = row.findViewById(components[j]);
            tvi.setText("");
        }
    }

    private Recipe genRecipe()
    {
        Recipe r = _cook.getRecipesDB().selectRecipe();

        return r;
    }
}

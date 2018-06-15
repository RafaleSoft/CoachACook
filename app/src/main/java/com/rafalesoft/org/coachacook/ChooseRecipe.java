package com.rafalesoft.org.coachacook;

import android.database.Cursor;
import android.graphics.Color;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChooseRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    private final RecipeSpeech _rs;
    private final ArrayList<String> recipe_steps = new ArrayList<>();
    private int current_step = 0;
    private final RC _rc = new RC();

    private class RC implements RecipeSpeech.RecognitionCallback
    {
        @Override
        public void onRecognized(int stringId)
        {
            if (recipe_steps.size() == 0)
                return;

            boolean spk = false;
            switch (stringId)
            {
                case R.string.speech_demarre:
                {
                    spk = _rs.speak(recipe_steps.get(0));
                    current_step = 1;
                    _rs.Recognize(this);
                    break;
                }
                case R.string.speech_apres:
                {
                    if (current_step < recipe_steps.size())
                        spk = _rs.speak(recipe_steps.get(current_step++));
                    else
                        spk = _rs.speak(CoachACook.getCoach().getString(R.string.speech_recipe_over));
                    _rs.Recognize(this);
                    break;
                }
                case R.string.speech_avant:
                {
                    if (current_step > 0)
                        spk = _rs.speak(recipe_steps.get(--current_step));
                    else
                        spk = _rs.speak(CoachACook.getCoach().getString(R.string.speech_recipe_start));
                    _rs.Recognize(this);
                    break;
                }
                case R.string.speech_repete:
                {
                    if (current_step < recipe_steps.size())
                        spk = _rs.speak(recipe_steps.get(current_step));
                    else
                        spk = _rs.speak(CoachACook.getCoach().getString(R.string.speech_recipe_over));
                    _rs.Recognize(this);
                    break;
                }
                case R.string.speech_recommence:
                {
                    spk = _rs.speak(recipe_steps.get(0));
                    current_step = 1;
                    _rs.Recognize(this);
                    break;
                }
                case R.string.speech_termine:
                {
                    CoachACook.getCoach().onBackPressed();
                    break;
                }
            }

            if (!spk)
                Log.d("STT","onRecognized failed");
        }
    }

    private class VB implements SimpleCursorAdapter.ViewBinder
    {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex)
        {
            if (view instanceof TextView)
            {
                String text = cursor.getString(columnIndex);
                ((TextView) view).setText(text);
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

    private class VB2 implements SimpleAdapter.ViewBinder
    {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation)
        {
            if (data instanceof RecipeComponent)
            {
                RecipeComponent stock = (RecipeComponent)data;
                TextView tv;
                if (view instanceof TextView)
                    tv = (TextView) view;
                else
                    return false;

                tv.setTextSize(10);
                if (stock.get_quantity() <= 0)
                    tv.setTextColor(Color.RED);

                if (view.getId() == R.id.stock_item_name)
                    tv.setText(stock.get_name());
                else if (view.getId() == R.id.stock_item_quantity)
                    tv.setText(Double.toString(stock.get_quantity()));
                else if (view.getId() == R.id.stock_item_unit)
                    tv.setText(stock.get_unit());
                else
                    return false;

                return true;
            }
            else
            {
                return false;
            }
        }
    }


    public ChooseRecipe()
	{
		_rs = CoachACook.getCoach().getRecipeSpeech();
	}

	@Override
	public void onClick(View v)
	{
        View stock = CoachACook.getCoach().switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = {RecipesDB.ID, RecipesDB.NAME, Recipe.COLUMN_DIFFICULTY_TITLE, Recipe.COLUMN_COST_TITLE};
        updateCursor( Recipe.TABLE_NAME, projection );

        String[] fromColumns = { RecipesDB.NAME, Recipe.COLUMN_DIFFICULTY_TITLE, Recipe.COLUMN_COST_TITLE };
        int[] toViews = { R.id.recipe_item_name , R.id.recipe_difficulty_progress, R.id.recipe_cost_progress};

        SimpleCursorAdapter recipesDBAdapter =
                new SimpleCursorAdapter(CoachACook.getCoach(), R.layout.recipe_stockview_item,
                                        getCursor(), fromColumns, toViews, 0);
        recipesDBAdapter.setViewBinder(new VB());

        lvl.setAdapter(recipesDBAdapter);
        lvl.setOnItemClickListener(this);
	}

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        View recipeView = CoachACook.getCoach().switchToView(R.id.recipe_view);
        TextView recipe_name = view.findViewById(R.id.recipe_item_name);
        Recipe r = CoachACook.getCoach().getRecipesDB().getRecipe(recipe_name.getText().toString());

        fillRecipe(recipeView, r);

        parseDescription(r.get_preparation());
        if (null != _rs)
            _rs.Recognize(_rc);
    }

    private void fillRecipe(View recipeView, Recipe r)
    {
        TextView tv_name = recipeView.findViewById(R.id.recipe_name);
        tv_name.setText(r.get_name());

        TextView tv_desc = recipeView.findViewById(R.id.recipe_description);
        tv_desc.setText(r.get_preparation());

        List<Map<String,Object>> data = new ArrayList<>();
        for (int item=0;item<r.nbComponents();item++)
        {
            RecipeComponent c = r.getComponent(item);
            Ingredient stock = CoachACook.getCoach().getRecipesDB().getIngredient(c.get_name());
            if (stock.get_quantity() < c.get_quantity())
                c.set_quantity(-c.get_quantity());

            Map<String,Object> ingredient = new ArrayMap<>();
            ingredient.put("I",c);
            ingredient.put("V",c);
            ingredient.put("U",c);

            data.add(ingredient);
        }

        String[] from = { "I", "V", "U" };
        int[] to = { R.id.stock_item_name, R.id.stock_item_quantity, R.id.stock_item_unit};

        SimpleAdapter adapter = new SimpleAdapter(CoachACook.getCoach(), data, R.layout.stock_view_item, from, to);
        adapter.setViewBinder(new VB2());

        ListView table = recipeView.findViewById(R.id.recipe_ingredients);
        table.setAdapter(adapter);
    }

    private void parseDescription(String recipe_description)
    {
        current_step = 0;
        String [] tmp = recipe_description.split("[.]");
        for (String s: tmp)
            recipe_steps.add(s);
    }
}

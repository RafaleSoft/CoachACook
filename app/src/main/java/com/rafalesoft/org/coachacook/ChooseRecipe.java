package com.rafalesoft.org.coachacook;

import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChooseRecipe extends RecipesCursorHolder implements OnClickListener, OnItemClickListener
{
    private final ArrayList<String> recipe_steps = new ArrayList<>();
    private int current_step = 0;
    private final RecipeRecognitionCallback _recipeRecognitionCallback = new RecipeRecognitionCallback();
    private SpannableString _spText = null;
    private TextView _recipeDescription = null;
    private FloatingActionButton _floatingButton = null;
    private final ForegroundColorSpan _fgSpan = new ForegroundColorSpan(Color.BLUE);


    private class RecipeRecognitionCallback implements RecipeSpeech.RecognitionCallback, View.OnClickListener
    {
        private final RecipeSpeech _rs;
        private boolean _active = false;

        RecipeRecognitionCallback()
        {
            _rs = CoachACook.getCoach().getRecipeSpeech();
        }

        @Override
        public void onClick(View v)
        {
            _active = !_active;
            if (_active)
            {
                if (null != _rs)
                    _rs.Recognize(_recipeRecognitionCallback);
                else
                    _active = false;
            }

            if (_active)
                _floatingButton.setImageResource(android.R.drawable.presence_audio_busy);
            else
                _floatingButton.setImageResource(android.R.drawable.ic_btn_speak_now);
        }

        @Override
        public void onRecognized(int stringId)
        {
            if (recipe_steps.size() == 0)
                return;

            _floatingButton.setImageResource(android.R.drawable.presence_audio_online);
            boolean spk = false;
            switch (stringId)
            {
                case R.string.speech_not_understood:
                {
                    spk = _rs.speak(CoachACook.getCoach().getString(R.string.speech_not_understood));
                    _rs.Recognize(this);
                    break;
                }
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
                    spk = _rs.speak(CoachACook.getCoach().getString(R.string.speech_recipe_over));
                    current_step = 0;
                    CoachACook.getCoach().onBackPressed();
                    break;
                }
            }

            _floatingButton.setImageResource(android.R.drawable.presence_audio_busy);
            if (!spk)
                Log.d("STT","onRecognized failed");
            else
                updateSpan();
        }
    }

    private void updateSpan()
    {
        _spText.removeSpan(_fgSpan);
        int text_length = 0;

        if (current_step < recipe_steps.size())
        {
            for (int i = 0; i < current_step; i++)
                text_length += recipe_steps.get(i).length() + 1;    // split removed dot !
            if (text_length > _spText.length())
                text_length = _spText.length();
        }
        else
            text_length = _spText.length();

        _spText.setSpan(_fgSpan,0,text_length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _recipeDescription.setText(_spText, TextView.BufferType.SPANNABLE);
    }

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

    private class RecipeComponentAdapter implements SimpleAdapter.ViewBinder
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

                switch (view.getId())
                {
                    case R.id.stock_item_name:
                        tv.setText(stock.get_name());
                        break;
                    case R.id.stock_item_quantity:
                        tv.setText(Double.toString(stock.get_quantity()));
                        break;
                    case R.id.stock_item_unit:
                        tv.setText(stock.get_unit().toString());
                        break;
                    default:
                        return false;
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }


    public ChooseRecipe() { }

	@Override
	public void onClick(View v)
	{
        View stock = CoachACook.getCoach().switchToView(R.id.recipe_stockview);
        ListView lvl = stock.findViewById(R.id.recipe_list);

        String[] projection = { RecipesDB.ID,
                                RecipesDB.NAME,
                                Recipe.COLUMN_DIFFICULTY_TITLE,
                                Recipe.COLUMN_COST_TITLE,
                                Recipe.COLUMN_PREPARE_TITLE,
                                Recipe.COLUMN_TIME_TITLE};
        updateCursor( Recipe.TABLE_NAME, projection );

        String[] fromColumns = { RecipesDB.NAME, Recipe.COLUMN_DIFFICULTY_TITLE, Recipe.COLUMN_COST_TITLE, Recipe.COLUMN_PREPARE_TITLE, Recipe.COLUMN_TIME_TITLE };
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
        View recipeView = CoachACook.getCoach().switchToView(R.id.recipe_view);
        TextView recipe_name = view.findViewById(R.id.recipe_item_name);
        Recipe r = CoachACook.getCoach().getRecipesDB().getRecipe(recipe_name.getText().toString());

        fillRecipe(recipeView, r);

        _floatingButton = recipeView.findViewById(R.id.fab_speak);
        _floatingButton.setOnClickListener(new View.OnClickListener()
                               {
                                   @Override
                                   public void onClick(View view)
                                   {
                                       Snackbar.make(view, "Recipe speech", Snackbar.LENGTH_LONG)
                                               .setAction("Start !", _recipeRecognitionCallback).show();
                                   }
                               });

        current_step = 0;
        String [] tmp = r.get_preparation().split("[.]");
        recipe_steps.addAll(Arrays.asList(tmp));
    }

    private void fillRecipe(View recipeView, Recipe r)
    {
        TextView tv_name = recipeView.findViewById(R.id.recipe_name);
        tv_name.setText(r.get_name());

        _recipeDescription = recipeView.findViewById(R.id.recipe_description);
        _spText = new SpannableString(r.get_preparation());
        _spText.setSpan(_fgSpan,0,0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        _recipeDescription.setText(_spText, TextView.BufferType.SPANNABLE);

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
        adapter.setViewBinder(new RecipeComponentAdapter());

        ListView table = recipeView.findViewById(R.id.recipe_ingredients);
        table.setAdapter(adapter);
    }
}

package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;


public class CoachACook extends AppCompatActivity {

    private RecipesDB _dbRecipes = null;
    private int currentView = 0;
    private ViewFlipper _mainView = null;
    private ProgressBar _pg = null;

    public RecipeSpeech getRecipeSpeech()
    {
        return _recipeSpeech;
    }
    public RecipesDB getRecipesDB()
    {
        return _dbRecipes;
    }

    private RecipeSpeech _recipeSpeech;

    private static CoachACook theCoach = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        theCoach = this;
        _dbRecipes = new RecipesDB(this);
        _recipeSpeech = new RecipeSpeech(this);

        setContentView(R.layout.activity_coach_acook);
        _mainView = findViewById(R.id.view_flipper);
        _mainView.setAnimateFirstView(true);
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(500);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        _mainView.setInAnimation(fadeIn);
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(500);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        _mainView.setOutAnimation(fadeOut);

        View startView = getLayoutInflater().inflate(R.layout.content_coach_acook,_mainView);
        _pg = startView.findViewById(R.id.progress_bar);
        _pg.setVisibility(View.INVISIBLE);
        currentView = R.id.coach_a_cook;

        Button chooseButton = startView.findViewById(R.id.cook_book);
        ChooseRecipe _chooseRecipe = new ChooseRecipe(this);
        chooseButton.setOnClickListener(_chooseRecipe);

        Button manageButton = startView.findViewById(R.id.manage_stock);
        ManageStock _manageStock = new ManageStock(this);
        manageButton.setOnClickListener(_manageStock);

        Button buildButton = startView.findViewById(R.id.build_recipe);
        BuildRecipe _buildRecipe = new BuildRecipe(this);
        buildButton.setOnClickListener(_buildRecipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public View switchToView(int viewId)
    {
        View view = _mainView.findViewById(viewId);
        if (view == null)
        {
            int layout = 0;

            if (viewId == R.id.stock_view)
                layout = R.layout.stock_view;
            else if (viewId == R.id.recipe_stockview)
                layout = R.layout.recipe_stockview;
            else if (viewId == R.id.recipe_view)
                layout = R.layout.recipe_view;
            /*else if (viewId == R.id.recipe_buildview)
                layout = R.layout.recipe_buildview;
                */
            else if (viewId == R.id.coach_a_cook)
                layout = R.layout.activity_coach_acook;

            view = getLayoutInflater().inflate(layout,null);
            _mainView.addView(view);
        }

        if (view != null)
        {
            currentView = viewId;
            int index = _mainView.indexOfChild(view);
            _mainView.setDisplayedChild(index);
        }

        return view;
    }

    private class UpdateDataTask extends AsyncTask<Void, Void, Boolean>
    {
        private boolean _reset;
        UpdateDataTask(boolean reset)
        {
            _reset = reset;
        }
        protected Boolean doInBackground(Void... v)
        {
            if (_reset)
                return _dbRecipes.reset();
            else
                return _dbRecipes.updateData();
        }
        protected void onPreExecute()
        {
            _pg.setVisibility(View.VISIBLE);
            Button chooseButton = _mainView.findViewById(R.id.cook_book);
            chooseButton.setEnabled(false);
            Button manageButton = _mainView.findViewById(R.id.manage_stock);
            manageButton.setEnabled(false);
            Button buildButton = _mainView.findViewById(R.id.build_recipe);
            buildButton.setEnabled(false);
        }
        protected void onPostExecute(Boolean result)
        {
            _pg.setVisibility(View.INVISIBLE);
            Button chooseButton = _mainView.findViewById(R.id.cook_book);
            chooseButton.setEnabled(true);
            Button manageButton = _mainView.findViewById(R.id.manage_stock);
            manageButton.setEnabled(true);
            Button buildButton = _mainView.findViewById(R.id.build_recipe);
            buildButton.setEnabled(true);

            String message;
            if (_reset)
            {
                if (result)
                    message = theCoach.getResources().getString(R.string.data_erased);
                else
                    message = theCoach.getResources().getString(R.string.data_not_erased);
            }
            else
            {
                if (result)
                    message = theCoach.getResources().getString(R.string.data_updated);
                else
                    message = theCoach.getResources().getString(R.string.data_not_updated);
            }
            Toast toast = Toast.makeText(theCoach, message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coach_acook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsManager.class);
                startActivity(intent);
                return true;
            case R.id.action_reset:
                new UpdateDataTask(true).execute();
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_update_stock:
                new UpdateDataTask(false).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        if ((currentView == R.id.stock_view) ||
                (currentView == R.id.recipe_stockview))
      //          || (currentView == R.id.recipe_buildview))
            switchToView(R.id.coach_a_cook);
        else if (currentView == R.id.recipe_view)
            switchToView(R.id.recipe_stockview);
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_message);
            builder.setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener()
            { 	public void onClick(DialogInterface dialog, int id)
                { finish(); }
            });

            builder.setNegativeButton(R.string.exit_no, new DialogInterface.OnClickListener()
            {	public void onClick(DialogInterface dialog, int id)
                { 	}
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();        // The activity is about to become visible.
    }

    @Override
    protected void onResume()
    {
        super.onResume();        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause()
    {
        super.onPause();        // Another activity is taking focus (this activity is about to be "paused").
    }

    @Override
    protected void onStop()
    {
        super.onStop();        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy()
    {
        _recipeSpeech.DestroySpeech();
        _dbRecipes.close();
        super.onDestroy();        // The activity is about to be destroyed.
    }
}

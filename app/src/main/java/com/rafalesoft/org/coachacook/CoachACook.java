package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CoachACook extends AppCompatActivity {

    private RecipesDB _dbRecipes = null;
    private int currentView = 0;
    //ManageStock _manageStock = null;
    ProgressBar _pg = null;
    private static CoachACook theCoach = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        theCoach = this;
        _dbRecipes = new RecipesDB(this);

        //View startView = getLayoutInflater().inflate(R.layout.activity_coach_acook,null);
        setContentView(R.layout.activity_coach_acook);
        _pg = findViewById(R.id.progress_bar);
        _pg.setVisibility(View.INVISIBLE);
        currentView = R.id.coach_a_cook;

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

    private class UpdateDataTask extends AsyncTask<Void, Void, Boolean>
    {
        private boolean _reset;
        public UpdateDataTask(boolean reset)
        {
            _reset = reset;
        }
        protected Boolean doInBackground(Void... v)
        {
            if (_reset)
            {
                return _dbRecipes.reset();
            }
            else
            {
                return _dbRecipes.updateData();
            }
        }
        protected void onPreExecute()
        {
            _pg.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Boolean result)
        {
            _pg.setVisibility(View.INVISIBLE);
            String message;
            if (_reset)
            {
                message = theCoach.getResources().getString(R.string.data_erased);
            }
            else
            {
                message = theCoach.getResources().getString(R.string.data_updated);
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
      //  if ((currentView == R.id.stock_view) ||
      //          (currentView == R.id.recipe_stockview) ||
      //          (currentView == R.id.recipe_buildview))
      //      switchToView(R.id.coach_a_cook);
      //  else if (currentView == R.id.recipe_view)
      //      switchToView(R.id.recipe_stockview);
      //  else
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
        _dbRecipes.close();
        super.onDestroy();        // The activity is about to be destroyed.
    }
}

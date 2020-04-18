package com.rafalesoft.org.coachacook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class CoachACook extends AppCompatActivity implements RewardedVideoAdListener
{
    private RecipesDB _dbRecipes = null;
    private int currentView = 0;
    private ViewFlipper _mainView = null;
    private ProgressBar _pg = null;
    private RecipeSpeech _recipeSpeech = null;
    private ShoppingListManager _shopManager = null;
    private SettingsFragment _settings = null;

    public RecipeSpeech getRecipeSpeech()
    {
        return _recipeSpeech;
    }
    public RecipesDB getRecipesDB()
    {
        return _dbRecipes;
    }

    private static CoachACook theCoach = null;
    public static CoachACook getCoach() { return theCoach; }
    private RewardedVideoAd mRewardedVideoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544/5224354917");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        theCoach = this;
        _dbRecipes = new RecipesDB(this);
        _recipeSpeech = new RecipeSpeech(this);
        _shopManager = new ShoppingListManager();
        DataLoader.setDatabase(_dbRecipes);

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
        ChooseRecipe _chooseRecipe = new ChooseRecipe();
        chooseButton.setOnClickListener(_chooseRecipe);

        Button manageButton = startView.findViewById(R.id.manage_stock);
        ManageStock _manageStock = new ManageStock();
        manageButton.setOnClickListener(_manageStock);

        Button buildButton = startView.findViewById(R.id.build_recipe);
        BuildRecipe _buildRecipe = new BuildRecipe();
        buildButton.setOnClickListener(_buildRecipe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        _settings = new SettingsFragment();
        fragmentTransaction.add(R.id.view_flipper, _settings);
        fragmentTransaction.commit();

        FloatingActionButton fab = findViewById(R.id.fab_cart);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "View your cart", Snackbar.LENGTH_LONG)
                        .setAction("Show it!", _shopManager).show();
            }
        });
    }

    public View switchToView(int viewId)
    {
        View view = _mainView.findViewById(viewId);
        if (view == null)
        {
            int layout = 0;
            FloatingActionButton fab = findViewById(R.id.fab_cart);

            switch (viewId)
            {
                case R.id.stock_pager:
                    layout = R.layout.stock_pager;
                    fab.show();
                    break;
                case R.id.stock_view:
                    layout = R.layout.stock_view;
                    fab.show();
                    break;
                case R.id.recipe_stockview:
                    layout = R.layout.recipe_stockview;
                    fab.show();
                    break;
                case R.id.recipe_view:
                    layout = R.layout.recipe_view;
                    fab.show();
                    break;
                case R.id.coach_a_cook:
                    layout = R.layout.activity_coach_acook;
                    fab.show();
                    break;
            }

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
        private final boolean _reset;
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
            //if (mRewardedVideoAd.isLoaded())
            //{
            //    mRewardedVideoAd.show();
            //}

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
                switchToView(_settings.getViewId());
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
        switch (currentView)
        {
            case R.id.stock_view:
            case R.id.recipe_stockview:
            case R.id.stock_pager:
                switchToView(R.id.coach_a_cook);
                break;
            case R.id.recipe_view:
                switchToView(R.id.recipe_stockview);
                break;
            default:
            {
                if (currentView == _settings.getViewId())
                    switchToView(R.id.coach_a_cook);
                else
                    quitApplication();
                break;
            }
        }
    }

    private final void quitApplication()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_message);
        builder.setPositiveButton(R.string.exit_yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        });

        builder.setNegativeButton(R.string.exit_no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onResume()
    {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause()
    {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mRewardedVideoAd.destroy(this);
        _recipeSpeech.destroySpeech();
        _dbRecipes.close();
        super.onDestroy();        // The activity is about to be destroyed.
    }


    private void loadRewardedVideoAd()
    {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem)
    {
        Toast.makeText(this, "onRewarded! currency: " + rewardItem.getType() + "  amount: " +
                rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }
}


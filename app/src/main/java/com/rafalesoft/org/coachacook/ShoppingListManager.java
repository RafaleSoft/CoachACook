package com.rafalesoft.org.coachacook;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;

class ShoppingListManager implements View.OnClickListener
{
    private ArrayList<Ingredient> _list = new ArrayList<>();


    @Override
    public void onClick(View v)
    {
        Log.d("ShoppingListManager", "FloatingActionButton onClick");
    }
}

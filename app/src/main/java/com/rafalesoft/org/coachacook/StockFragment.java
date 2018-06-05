package com.rafalesoft.org.coachacook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StockFragment extends Fragment
{
    public static CoachACook _coach = null;
    private StockManager _stock = null;

    public StockFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.stock_view, container, false);

        _stock = new StockManager(rootView);

        return rootView;
    }
}

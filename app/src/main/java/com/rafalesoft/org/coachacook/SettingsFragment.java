package com.rafalesoft.org.coachacook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat
{
    private int _viewId = -1;

    public int getViewId()
    {
        return _viewId;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        EditTextPreference numRecipes = findPreference(getResources().getText(R.string.number_recipes_key));
        numRecipes.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

        ListPreference numGuests = findPreference(getResources().getText(R.string.number_guests_key));
        numGuests.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

        ListPreference listLanguage = findPreference(getResources().getText(R.string.language_key));
        listLanguage.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        _viewId = View.generateViewId ();
        v.setId(_viewId);
        return v;
    }
}

package com.example.ppo2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference button = findPreference(getString(R.string.pref_button));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.clearData)
                        .setMessage(R.string.clearDataSure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences prefs = PreferenceManager
                                        .getDefaultSharedPreferences(getContext());
                                prefs.edit().clear().commit();
                                DatabaseAdapter adapter = new DatabaseAdapter(getContext());
                                adapter.open();
                                adapter.clear();
                                adapter.close();
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
                return true;
            }
        });
        Preference theme = findPreference(getString(R.string.pref_theme));
        theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                if ((boolean)newValue)
                {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                }
                else
                {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });

        /*Preference lang = findPreference(getString(R.string.pref_lang));
        lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                switch ((String)newValue)
                {
                    case "Русский":
                        setLocale("ru");
                        break;
                    case "English":
                        setLocale("en");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });*/
    }

    /*public void setLocale(String lang)
    {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, null);
        Intent refresh = new Intent(this.getActivity(), SettingsActivity.class);
        getActivity().finish();
        startActivity(refresh);
    }*/
}
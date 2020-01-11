package com.eladper.sudoku;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class Preferences extends PreferenceFragment {


    private SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefChangeListener=new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                switch (s){
                    case "Vertical Guidelines":
                        if ((sharedPreferences).getBoolean("Vertical Guidelines",false))
                        Toast.makeText(getActivity().getApplicationContext(), "Vertical Guidelines will appear in the next tap", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Vertical Guidelines will disappear from the next tap", Toast.LENGTH_SHORT).show();
                        break;
                    case "Horizontal Guidelines":
                        if ((sharedPreferences).getBoolean("Horizontal Guidelines",false))
                            Toast.makeText(getActivity().getApplicationContext(), "Horizontal Guidelines will appear in the next tap", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Horizontal Guidelines will disappear from the next tap", Toast.LENGTH_SHORT).show();
                        break;
                    case "Block":
                        if ((sharedPreferences).getBoolean("Block",false))
                            Toast.makeText(getActivity().getApplicationContext(), "Blocks will be marked in the next tap", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Blocks will not be marked from the next tap", Toast.LENGTH_SHORT).show();
                        break;
                    case "Identical Number":
                        if ((sharedPreferences).getBoolean("Identical Number",false))
                            Toast.makeText(getActivity().getApplicationContext(), "Cells with identical numbers will be highlighted in the next tap", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity().getApplicationContext(), "Cells with identical numbers will not be highlighted from the next tap", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey()!=null) {
            if (preference.getKey().equals("Leave Feedback")) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.eladper.sudoku")));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.eladper.sudoku")));
                }
                return true;
            }
            if (preference.getKey().equals("App Info")) {
                startActivity(new Intent(getActivity(), AppInfoActivity.class));
                return true;
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(prefChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(prefChangeListener);

    }
}
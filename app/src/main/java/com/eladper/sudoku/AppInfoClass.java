package com.eladper.sudoku;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class AppInfoClass extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_info);
    }
}

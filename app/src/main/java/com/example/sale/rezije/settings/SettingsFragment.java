package com.example.sale.rezije.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.sale.rezije.R;

/**
 * Created by Sale on 7.5.2016..
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_fragment);
    }
}

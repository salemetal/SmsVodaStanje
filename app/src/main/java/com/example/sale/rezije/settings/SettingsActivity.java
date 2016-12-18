package com.example.sale.rezije.settings;

import android.app.Activity;
import android.os.Bundle;


/**
 * Created by Sale on 7.5.2016..
 */
public class SettingsActivity extends Activity {

    public static final String KEY_PREF_SMS_NUMBER = "pref_smsNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
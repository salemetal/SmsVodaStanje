package com.example.sale.rezije;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sale.rezije.db.AndroidDatabaseManager;
import com.example.sale.rezije.db.DBHandler;
import com.example.sale.rezije.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SendWaterStatus";
    private static final int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBHandler dbHandler = new DBHandler(this);
        WaterStatus waterStatusLast = dbHandler.getLastWaterStatus();

        TextView lastRefVals = (TextView) findViewById(R.id.textViewRefVelues);
        lastRefVals.setText("Zadnje stanje: \nWC: " + waterStatusLast.wcVal + "\nKUPAONA: " + waterStatusLast.kupaonaVal);

    }

    public void sendSmsToNumber(final View v) {

        EditText editTextWc = (EditText) findViewById(R.id.text_wc);
        EditText editTextKupaona = (EditText) findViewById(R.id.text_kupaona);

        if (Helpers.isEmpty(editTextWc) || Helpers.isEmpty(editTextKupaona)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.upozorenje));
            alertDialog.setMessage("Niste unijeli podatke!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            final WaterStatus waterStatusNew = new WaterStatus(Integer.parseInt(editTextWc.getText().toString()),
                    Integer.parseInt(editTextKupaona.getText().toString()));

            final DBHandler dbHandler = new DBHandler(this);
            WaterStatus waterStatusLast = dbHandler.getLastWaterStatus();

            final String smsString = getSmsString(waterStatusLast, waterStatusNew, v);

            if (smsString != "") {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                try {
                    builder
                            .setMessage(smsString + "\n Poslati?")
                            .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST);
                                        sendSmsToNumber(smsString);
                                    } else {
                                        sendSmsToNumber(smsString);
                                    }

                                    dbHandler.addWaterStatus(waterStatusNew);
                                    finish();
                                    startActivity(getIntent());
                                }
                            })
                            .setNegativeButton("NE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.v(TAG, e.getMessage());
                }
            }
        }
    }

    private void sendSmsToNumber(String smsString) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final String brMobForSms = settings.getString(SettingsActivity.KEY_PREF_SMS_NUMBER, "");

        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(brMobForSms, null, smsString, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS Failed!",
                    Toast.LENGTH_LONG).show();
            Log.v(TAG, e.getMessage());
        }
    }

    private String getSmsString(WaterStatus waterStatusLast, WaterStatus waterStatusNew, View v) {

        String smsString;
        int razlikaWc = waterStatusNew.wcVal - waterStatusLast.wcVal;
        int razlikaKup = (waterStatusNew.kupaonaVal - waterStatusLast.kupaonaVal);

        if (razlikaWc <= 0 || razlikaKup <= 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.upozorenje));
            alertDialog.setMessage("Podaci se ne slažu s prijašnjim stanjem! (Manje od nula)");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            return "";
        }

        smsString = "Pozdrav! \n" +
                "Stanje vode: \n" +
                "WC: " + waterStatusNew.wcVal + ", razlika " + razlikaWc + "\n" +
                "KUPAONA: " + waterStatusNew.kupaonaVal + ", razlika " + razlikaKup + "\n" +
                "Ukupno " + (razlikaWc + razlikaKup);

        return smsString;
    }

    public void insertNewRefValues(View v) {
        EditText editTextWc = (EditText) findViewById(R.id.text_wc);
        EditText editTextKupaona = (EditText) findViewById(R.id.text_kupaona);

        if (Helpers.isEmpty(editTextWc) || Helpers.isEmpty(editTextKupaona)) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.upozorenje));
            alertDialog.setMessage("Niste unijeli podatke!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            WaterStatus waterStatus = new WaterStatus(Integer.parseInt(editTextWc.getText().toString()),
                    Integer.parseInt(editTextKupaona.getText().toString()));

            DBHandler dbHandler = new DBHandler(this);
            dbHandler.addWaterStatus(waterStatus);

            editTextWc.setText("");
            editTextKupaona.setText("");

            Toast.makeText(v.getContext(), "Dodano!", Toast.LENGTH_SHORT)
                    .show();

            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent4 = new Intent(this, SettingsActivity.class);
                this.startActivity(intent4);
                return true;
            case R.id.db:
                Intent intent5 = new Intent(this, AndroidDatabaseManager.class);
                this.startActivity(intent5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

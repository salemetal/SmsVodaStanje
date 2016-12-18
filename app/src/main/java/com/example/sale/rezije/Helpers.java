package com.example.sale.rezije;

import android.widget.EditText;

/**
 * Created by Sale on 10.4.2016..
 */
public class Helpers {

    public static boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
}

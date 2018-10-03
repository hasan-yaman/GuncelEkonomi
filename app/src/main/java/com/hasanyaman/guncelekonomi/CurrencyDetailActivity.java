package com.hasanyaman.guncelekonomi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class CurrencyDetailActivity extends AppCompatActivity {

    private String currencyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_detail);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            currencyCode = bundle.getString(Constants.SELECTED_ITEM_CODE,"");
        }

        Log.i("Info","currencyCode -> " + currencyCode);
    }
}

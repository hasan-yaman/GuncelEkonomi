package com.hasanyaman.guncelekonomi;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;


import com.google.gson.Gson;
import com.hasanyaman.guncelekonomi.Adapters.CurrencyDetailAdapter;
import com.hasanyaman.guncelekonomi.Adapters.ViewPagerAdapter;
import com.hasanyaman.guncelekonomi.Data.Bank;
import com.hasanyaman.guncelekonomi.Data.Currency;
import com.hasanyaman.guncelekonomi.Utilities.ArrayUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class CurrencyDetailActivity extends AppCompatActivity implements OnTaskCompleted, TabFragment.OnFragmentInteractionListener {

    private static final String[] BANKS_CODES = {"akbank", "finansbank", "isbankasi", "vakifbank", "yapikredi", "sekerbank", "denizbank", "hsbc"};
    boolean[] allDone = new boolean[BANKS_CODES.length];

    private Currency selectedCurrency;
    private String type;

    ArrayList<Bank> banks = new ArrayList<>();

    private ListView listView;
    private TableRow headerRow;
    private View topDivider;
    private ProgressBar progressBar;

    private TableRow detailNames;
    private View detailDivider;
    private TableRow detailRow;

    private OnTaskCompleted listener;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String themeMode = sharedPreferences.getString(Constants.THEME, Constants.DAY_MODE);
        if(themeMode.equals(Constants.NIGHT_MODE)) {
            setTheme(R.style.AppDarkTheme);
        }

        setContentView(R.layout.activity_currency_detail);

        listener = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            selectedCurrency = gson.fromJson(bundle.getString(Constants.SELECTED_ITEM), Currency.class);
            type = bundle.getString(Constants.TYPE, "");
        }

        listView = findViewById(R.id.listView);

        headerRow = findViewById(R.id.headerRow);
        topDivider = findViewById(R.id.topDivider);
        progressBar = findViewById(R.id.progress_bar);

        detailNames = findViewById(R.id.detailNames);
        detailDivider = findViewById(R.id.detailDivider);
        detailRow = findViewById(R.id.detailRow);

        ViewPager viewPager = findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), selectedCurrency.getCode(), type);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        ImageView backImageView = findViewById(R.id.backImageView);
        TextView currencyCodeTextView = findViewById(R.id.currencyCode);
        TextView currencyFullNameTextView = findViewById(R.id.currencyFullName);

        TextView detailBuying = findViewById(R.id.detailBuying);
        TextView detailSelling = findViewById(R.id.detailSelling);
        TextView detailChangeRate = findViewById(R.id.detailChangeRate);
        TextView detailUpdateTime = findViewById(R.id.detailUpdateTime);

        switch (type) {
            case Constants.CURRENCY:
                getDataFromAPI();
                break;
            case Constants.GOLD:
                showUI();
                break;
            case Constants.PARITY:
                showUI();
                break;
        }


        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        switch (type) {
            case Constants.CURRENCY:
                currencyCodeTextView.setText(selectedCurrency.getCode());
                currencyFullNameTextView.setText(selectedCurrency.getName());
                break;
            case Constants.GOLD:
                currencyCodeTextView.setText(selectedCurrency.getName());
                currencyFullNameTextView.setText("");
                break;
            case Constants.PARITY:
                currencyCodeTextView.setText(selectedCurrency.getCode());
                currencyFullNameTextView.setText(selectedCurrency.getName());
                break;
        }


        DecimalFormat decimalFormatV = new DecimalFormat();
        decimalFormatV.setMinimumFractionDigits(4);
        decimalFormatV.setMaximumFractionDigits(4);
        decimalFormatV.setMinimumIntegerDigits(1);


        String buying = decimalFormatV.format(selectedCurrency.getBuying());
        String selling = decimalFormatV.format(selectedCurrency.getSelling());

        int maxLength = Math.max(buying.length(), selling.length());
        float textSize = 24f;

        if(maxLength <= 6 ) {
            textSize = 24f;
        } else if(maxLength <= 7) {
            textSize = 22f;
        } else if(maxLength <= 8) {
            textSize = 18f;
        } else if(maxLength <= 9) {
            textSize = 16f;
        } else if(maxLength <= 10){
            textSize = 14f;
        } else {
            textSize = 12f;
        }

        detailBuying.setText(buying);
        detailSelling.setText(selling);

        detailBuying.setTextSize(textSize);
        detailSelling.setTextSize(textSize);


        DecimalFormat decimalFormatCR = new DecimalFormat();
        decimalFormatCR.setMinimumFractionDigits(2);
        decimalFormatCR.setMaximumFractionDigits(2);
        decimalFormatCR.setMinimumIntegerDigits(1);
        String changeRate = "% " + decimalFormatCR.format(selectedCurrency.getChangeRate());

        detailChangeRate.setText(changeRate);

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(selectedCurrency.getUpdateDate() * 1000);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String updateTime = "";
        if (hour < 10) {
            updateTime = "0" + hour;
        } else {
            updateTime = String.valueOf(hour);
        }

        int minute = calendar.get(Calendar.MINUTE);

        if (minute < 10) {
            updateTime += ":0" + minute;
        } else {
            updateTime += ":" + minute;
        }

        detailUpdateTime.setText(updateTime);

    }


    @Override
    public void onTaskCompleted() {
        Log.i("Info", "Completed a task");
        if (ArrayUtilities.isAllDone(allDone)) {
            Log.i("Info", "Completed all the tasks");

            CurrencyDetailAdapter currencyDetailAdapter = new CurrencyDetailAdapter(this, banks);
            listView.setAdapter(currencyDetailAdapter);

            showUI();

            if (banks.size() > 0) {
                headerRow.setVisibility(View.VISIBLE);
                topDivider.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showUI() {
        detailNames.setVisibility(View.VISIBLE);
        detailDivider.setVisibility(View.VISIBLE);
        detailRow.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
    }

    private void getDataFromAPI() {
        for (int i = 0; i < BANKS_CODES.length; i++) {
            String url = "https://www.doviz.com/api/v1/currencies/" + selectedCurrency.getCode() + "/latest/" + BANKS_CODES[i];
            new DownloadTask(listener, i).execute(url);
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        private OnTaskCompleted listener;
        private int index;

        public DownloadTask(OnTaskCompleted listener, int index) {
            this.listener = listener;
            this.index = index;
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;

            try {

                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(strings[0]);

                HttpResponse httpResponse = httpclient.execute(httppost);

                HttpEntity httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);

                return response;

            } catch (Exception e) {

                Log.e("Error ", e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s.length() != 0) {
                    JSONObject jsonObject = new JSONObject(s);

                    String fullName = jsonObject.getString("source_full_name");
                    double buying = jsonObject.getDouble("buying");
                    double selling = jsonObject.getDouble("selling");
                    long updateDate = jsonObject.getLong("update_date");

                    banks.add(new Bank(fullName, buying, selling, updateDate));
                }

                allDone[index] = true;
                listener.onTaskCompleted();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

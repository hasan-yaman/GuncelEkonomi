package com.hasanyaman.guncelekonomi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.hasanyaman.guncelekonomi.Adapters.CurrencyDetailAdapter;
import com.hasanyaman.guncelekonomi.Data.Bank;
import com.hasanyaman.guncelekonomi.Utilities.ArrayUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrencyDetailActivity extends AppCompatActivity implements OnTaskCompleted {

    private static final String[] BANKS_CODES = {"akbank", "finansbank", "isbankasi", "vakifbank", "yapikredi", "sekerbank", "denizbank", "hsbc"};
    boolean[] allDone = new boolean[BANKS_CODES.length];

    private String currencyCode;

    ArrayList<Bank> banks = new ArrayList<>();

    private ListView listView;
    private TableRow headerRow;
    private View topDivider;
    private ProgressBar progressBar;

    private OnTaskCompleted listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_detail);

        listener = this;

        listView = findViewById(R.id.listView);

        headerRow = findViewById(R.id.headerRow);
        topDivider = findViewById(R.id.topDivider);
        progressBar = findViewById(R.id.progress_bar);
        ImageView backImageView = findViewById(R.id.backImageView);
        TextView currencyCodeTextView = findViewById(R.id.currencyCode);
        TextView currencyFullNameTextView = findViewById(R.id.currencyFullName);

        String currencyFullName = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currencyCode = bundle.getString(Constants.SELECTED_ITEM_CODE, "");
            currencyFullName = bundle.getString(Constants.SELECTED_ITEM_FULL_NAME, "");
        }

        Log.i("Info", "currencyCode -> " + currencyCode);

        getDataFromAPI();

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        currencyCodeTextView.setText(currencyCode);
        currencyFullNameTextView.setText(currencyFullName);
    }

    @Override
    public void onTaskCompleted() {
        Log.i("Info", "Completed a task");
        if (ArrayUtilities.isAllDone(allDone)) {
            Log.i("Info", "Completed all the tasks");

            CurrencyDetailAdapter currencyDetailAdapter = new CurrencyDetailAdapter(this,banks);
            listView.setAdapter(currencyDetailAdapter);

            progressBar.setVisibility(View.GONE);
            if(banks.size() > 0) {
                headerRow.setVisibility(View.VISIBLE);
                topDivider.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getDataFromAPI() {
        for(int i = 0; i<BANKS_CODES.length; i++) {
            String url = "https://www.doviz.com/api/v1/currencies/"+ currencyCode + "/latest/" + BANKS_CODES[i];
            new DownloadTask(listener,i).execute(url);
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
                if(s.length() != 0) {
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

}

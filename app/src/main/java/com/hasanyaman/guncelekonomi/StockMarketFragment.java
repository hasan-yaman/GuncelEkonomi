package com.hasanyaman.guncelekonomi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hasanyaman.guncelekonomi.Adapters.StockMarketAdapter;
import com.hasanyaman.guncelekonomi.Data.StockMarket;
import com.hasanyaman.guncelekonomi.Utilities.ArrayUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockMarketFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockMarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockMarketFragment extends Fragment implements OnTaskCompleted {

    private static final String[] STOCK_MARKETS = {"XU100", "XU050", "XU030"};
    boolean[] allDone = {false, false, false};

    ArrayList<StockMarket> stockMarkets = new ArrayList<>();
    StockMarketAdapter stockMarketAdapter;

    ListView listView;
    ProgressBar progressBar;

    TableRow headerRow;
    View topDivider;
    TextView errorTextView;
    SharedPreferences sharedPreferences;

    boolean isOnline;

    private OnFragmentInteractionListener mListener;

    private OnTaskCompleted listener;

    RelativeLayout endeksRL;
    RelativeLayout changeRateRL;


    public StockMarketFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StockMarketFragment.
     */
    public static StockMarketFragment newInstance() {
        StockMarketFragment fragment = new StockMarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_stock_market, container, false);

        listView = inflatedView.findViewById(R.id.listView);
        progressBar = inflatedView.findViewById(R.id.progress_bar);
        headerRow = inflatedView.findViewById(R.id.headerRow);
        topDivider = inflatedView.findViewById(R.id.topDivider);
        errorTextView = inflatedView.findViewById(R.id.errorMessage);

        endeksRL = inflatedView.findViewById(R.id.endeksRL);
        changeRateRL = inflatedView.findViewById(R.id.changeRateRL);

        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        isOnline = checkConnection();

        if (isOnline) {
            long lastUpdateTime = sharedPreferences.getLong(Constants.LAST_UPDATE_TIME_STOCK_MARKET, 0);
            long currentTime = System.currentTimeMillis();

            boolean isExpired = currentTime - lastUpdateTime > Constants.EXPIRE_TIME;

            if (isExpired) {
                getDataFromAPI();
            } else {
                Gson gson = new Gson();
                String response = sharedPreferences.getString(Constants.STOCK_MARKET_LIST, "");

                if (response.equals("")) {
                    getDataFromAPI();
                } else {
                    Type type = new TypeToken<ArrayList<StockMarket>>() {
                    }.getType();
                    stockMarkets = gson.fromJson(response, type);
                    updateUI();
                }
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Lütfen internet bağlantınızı kontrol edin")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create()
                    .show();

            Gson gson = new Gson();
            String response = sharedPreferences.getString(Constants.STOCK_MARKET_LIST, "");


            if (response.equals("")) {
                Log.i("Info", "new data");
                showAnErrorMessage();
            } else {
                Log.i("Info", "old data");
                Type type = new TypeToken<ArrayList<StockMarket>>() {
                }.getType();
                stockMarkets = gson.fromJson(response, type);
                updateUI();
            }
        }

        return inflatedView;
    }

    @Override
    public void onTaskCompleted() {
        Log.i("Info", "Completed a task");
        if (ArrayUtilities.isAllDone(allDone)) {
            Log.i("Info", "Completed all the tasks");
            updateUI();

            // Son güncelleme zamanını kaydet!

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putLong(Constants.LAST_UPDATE_TIME_STOCK_MARKET, System.currentTimeMillis());

            // Çektiğin verileri kaydet!

            Gson gson = new Gson();
            String jsonArrayList = gson.toJson(stockMarkets);

            editor.putString(Constants.STOCK_MARKET_LIST, jsonArrayList);

            editor.apply();
        }

    }

    private void updateUI() {
        stockMarketAdapter = new StockMarketAdapter(getActivity(), stockMarkets);
        listView.setAdapter(stockMarketAdapter);

        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        headerRow.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);

        handleSort();
    }

    private void showAnErrorMessage() {
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getDataFromAPI() {
        for (int i = 0; i < STOCK_MARKETS.length; i++) {
            String url = "https://www.doviz.com/api/v1/indexes/" + STOCK_MARKETS[i] + "/latest";
            new DownloadTask(this, i).execute(url);

        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        private OnTaskCompleted listener;
        private int index;

        public DownloadTask(OnTaskCompleted listener, int index) {
            this.listener = listener;
            this.index = index;
        }

        @Override
        protected String doInBackground(String... params) {
            String response;

            try {

                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(params[0]);

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
                JSONObject jsonObject = new JSONObject(s);
                Log.i("Info", "jsonObject -> " + jsonObject);

                stockMarkets.add(new StockMarket(jsonObject.getString("full_name"),
                        jsonObject.getDouble("latest"), jsonObject.getDouble("change_rate"), jsonObject.getDouble("first_seance_lowest"), jsonObject.getDouble("first_seance_highest"), jsonObject.getDouble("first_seance_closing"),
                        jsonObject.getDouble("second_seance_lowest"), jsonObject.getDouble("second_seance_highest"), jsonObject.getDouble("second_seance_closing"), jsonObject.getDouble("previous_closing")));

                allDone[index] = true;
                listener.onTaskCompleted();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void handleSort() {
        endeksRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(stockMarkets, new Comparator<StockMarket>() {
                    @Override
                    public int compare(StockMarket s1, StockMarket s2) {
                        if(bigToSmall) {
                            return Double.compare(s2.getLatest(), s1.getLatest());
                        }
                        return Double.compare(s1.getLatest(), s2.getLatest());
                    }
                });

                stockMarketAdapter.notifyDataSetChanged();
                bigToSmall = !bigToSmall;

            }
        });


        changeRateRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(stockMarkets, new Comparator<StockMarket>() {
                    @Override
                    public int compare(StockMarket s1, StockMarket s2) {
                        if(bigToSmall) {
                            return Double.compare(s2.getChangeRate(), s1.getChangeRate());
                        }
                        return Double.compare(s1.getChangeRate(), s2.getChangeRate());
                    }
                });

                stockMarketAdapter.notifyDataSetChanged();
                bigToSmall = !bigToSmall;
            }
        });
    }
}

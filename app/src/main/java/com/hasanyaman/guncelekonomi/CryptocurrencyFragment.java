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
import com.hasanyaman.guncelekonomi.Adapters.CryptocurrencyAdapter;
import com.hasanyaman.guncelekonomi.Data.Cryptocurrency;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CryptocurrencyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CryptocurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CryptocurrencyFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    ListView listView;
    CryptocurrencyAdapter adapter;
    ProgressBar progressBar;
    ArrayList<Cryptocurrency> cryptocurrencies = new ArrayList<>();

    TableRow headerRow;
    View topDivider;
    TextView errorTextView;

    RelativeLayout rowSellingRL;
    RelativeLayout rowVolumeRL;
    RelativeLayout rowChangeRateRL;

    boolean isOnline;

    SharedPreferences sharedPreferences;

    public CryptocurrencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CryptocurrencyFragment.
     */
    public static CryptocurrencyFragment newInstance() {
        CryptocurrencyFragment fragment = new CryptocurrencyFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_cryptocurrency, container, false);
        listView = inflatedView.findViewById(R.id.listView);
        progressBar = inflatedView.findViewById(R.id.progress_bar);
        headerRow = inflatedView.findViewById(R.id.headerRow);
        topDivider = inflatedView.findViewById(R.id.topDivider);
        errorTextView = inflatedView.findViewById(R.id.errorMessage);

        rowSellingRL = inflatedView.findViewById(R.id.rowSellingRL);
        rowVolumeRL = inflatedView.findViewById(R.id.rowVolumeRL);
        rowChangeRateRL = inflatedView.findViewById(R.id.rowChangeRateRL);


        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        isOnline = checkConnection();

        if (isOnline) {
            // Son kaydedilen dataların üzerinden 5 dakikadan fazla zaman geçmişse
            // yeniden çek
            // çekmemişse eski dataları göster
            // Eğer eski datalar boşsa (yoksa) -> yeniden çek

            long lastUpdateTime = sharedPreferences.getLong(Constants.LAST_UPDATE_TIME_CRYPTOCURRENCY, 0);

            long currentTime = System.currentTimeMillis();

            boolean isExpired = currentTime - lastUpdateTime > Constants.EXPIRE_TIME;


            if (isExpired) {
                Log.i("Info", "is expired");

                getDataFromAPI();

            } else {
                Log.i("Info", "is not expired");

                // Bu durumda eski dataya bak.

                Gson gson = new Gson();
                String response = sharedPreferences.getString(Constants.CRYPTOCURRENCY_LIST, "");

                if (response.equals("")) {
                    Log.i("Info", "new data");
                    getDataFromAPI();
                } else {
                    Log.i("Info", "old data");
                    Type type = new TypeToken<ArrayList<Cryptocurrency>>() {
                    }.getType();
                    cryptocurrencies = gson.fromJson(response, type);
                    updateUI();
                }
            }

        } else {
            // Kullanıcıyı uyar ve eski dataları göster.
            // Eğer eski datalar boşsa (yoksa) -> uyar

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
            String response = sharedPreferences.getString(Constants.CRYPTOCURRENCY_LIST, "");


            if (response.equals("")) {
                Log.i("Info", "new data");
                showAnErrorMessage();
            } else {
                Log.i("Info", "old data");
                Type type = new TypeToken<ArrayList<Cryptocurrency>>() {
                }.getType();
                cryptocurrencies = gson.fromJson(response, type);
                updateUI();
            }

        }


        return inflatedView;
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            Log.i("Info", "doInBackground start");

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
            Log.i("Info", "onPostExecute start");
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    cryptocurrencies.add(new Cryptocurrency(object.getString("full_name"), object.getString("symbol"), object.getString("selling_try"),
                            object.getDouble("change_rate"), object.getLong("volume"), object.getInt("rank")));
                }


                Collections.sort(cryptocurrencies, new Comparator<Cryptocurrency>() {
                    @Override
                    public int compare(Cryptocurrency o1, Cryptocurrency o2) {
                        return o1.getRank() - o2.getRank();
                    }
                });

                updateUI();

                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Çektiğin verileri kaydet!

                Gson gson = new Gson();
                String jsonArrayList = gson.toJson(cryptocurrencies);

                editor.putLong(Constants.LAST_UPDATE_TIME_CRYPTOCURRENCY, System.currentTimeMillis());
                editor.putString(Constants.CRYPTOCURRENCY_LIST, jsonArrayList);

                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getDataFromAPI() {
        String url = "https://www.doviz.com/api/v1/coins/all/latest";
        new DownloadTask().execute(url);
    }

    private void updateUI() {
        adapter = new CryptocurrencyAdapter(getActivity(), cryptocurrencies);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        headerRow.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);

        handleSort();

    }


    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showAnErrorMessage() {
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void handleSort() {
        rowSellingRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {

                Collections.sort(cryptocurrencies, new Comparator<Cryptocurrency>() {
                    @Override
                    public int compare(Cryptocurrency c1, Cryptocurrency c2) {

                        double first = 0;
                        double second = 0;

                        try {
                            // because (the French locale has , for decimal separator)
                            NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                            Number firstNumber = format.parse(c1.getValue_try());
                            first = firstNumber.doubleValue();

                            Number secondNumber = format.parse(c2.getValue_try());
                            second = secondNumber.doubleValue();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (bigToSmall) {
                            return Double.compare(second, first);
                        } else {
                            return Double.compare(first, second);

                        }
                    }
                });

                adapter.notifyDataSetChanged();
                bigToSmall = !bigToSmall;
            }
        });
        rowVolumeRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(cryptocurrencies, new Comparator<Cryptocurrency>() {
                    @Override
                    public int compare(Cryptocurrency c1, Cryptocurrency c2) {
                        if (bigToSmall) {
                            if (c2.getVolume() > c1.getVolume()) {
                                return 1;
                            } else if (c2.getVolume() < c1.getVolume()) {
                                return -1;
                            }
                            return 0;
                        } else {
                            if (c1.getVolume() > c2.getVolume()) {
                                return 1;
                            } else if (c1.getVolume() < c2.getVolume()) {
                                return -1;
                            }
                            return 0;
                        }
                    }
                });

                adapter.notifyDataSetChanged();
                bigToSmall = !bigToSmall;
            }
        });
        rowChangeRateRL.setOnClickListener(new View.OnClickListener() {
            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(cryptocurrencies, new Comparator<Cryptocurrency>() {
                    @Override
                    public int compare(Cryptocurrency c1, Cryptocurrency c2) {
                        if (bigToSmall) {
                            return Double.compare(c2.getChangeRate(), c1.getChangeRate());
                        } else {
                            return Double.compare(c1.getChangeRate(), c2.getChangeRate());
                        }
                    }
                });

                adapter.notifyDataSetChanged();
                bigToSmall = !bigToSmall;
            }
        });
    }


}

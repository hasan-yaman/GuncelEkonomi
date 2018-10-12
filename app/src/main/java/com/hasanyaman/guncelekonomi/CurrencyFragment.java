package com.hasanyaman.guncelekonomi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hasanyaman.guncelekonomi.Adapters.CurrencyAdapter;
import com.hasanyaman.guncelekonomi.Data.Currency;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrencyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrencyFragment extends Fragment {

    private static final String ARG_TYPE = "type";

    private String type;

    private OnFragmentInteractionListener mListener;

    private ArrayList<Currency> currencies = new ArrayList<>();
    private ProgressBar progressBar;

    private ListView listView;
    private CurrencyAdapter adapter;

    private TableRow headerRow;
    private TextView rowName;
    private View topDivider;
    private TextView errorTextView;

    private RelativeLayout rowSellingValueRL;
    private RelativeLayout rowBuyingValueRL;
    private RelativeLayout changeRateRL;

    private boolean inCurrencyMode;

    private SharedPreferences sharedPreferences;

    private ImageView sellingValueArrow;
    private ImageView buyingValueArrow;
    private ImageView changeRateArrow;

    public CurrencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type type of data(either gold or currency).
     * @return A new instance of fragment CurrencyFragment.
     */
    public static CurrencyFragment newInstance(String type) {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inCurrencyMode = type.equals(Constants.CURRENCY);

        View inflatedView = inflater.inflate(R.layout.fragment_currency, container, false);

        listView = inflatedView.findViewById(R.id.listView);

        setDetailsListener();

        progressBar = inflatedView.findViewById(R.id.progress_bar);
        headerRow = inflatedView.findViewById(R.id.headerRow);
        topDivider = inflatedView.findViewById(R.id.topDivider);
        rowName = inflatedView.findViewById(R.id.rowName);
        errorTextView = inflatedView.findViewById(R.id.errorMessage);

        rowSellingValueRL = inflatedView.findViewById(R.id.rowSellingValueRL);
        rowBuyingValueRL = inflatedView.findViewById(R.id.rowBuyingValueRL);
        changeRateRL = inflatedView.findViewById(R.id.changeRateRL);

        sellingValueArrow = inflatedView.findViewById(R.id.sellingValueArrow);
        buyingValueArrow = inflatedView.findViewById(R.id.buyingValueArrow);
        changeRateArrow = inflatedView.findViewById(R.id.changeRateArrow);

        sharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        boolean isOnline = checkConnection();

        if (isOnline) {
            // Son kaydedilen dataların üzerinden 5 dakikadan fazla zaman geçmişse
            // yeniden çek
            // çekmemişse eski dataları göster
            // Eğer eski datalar boşsa (yoksa) -> yeniden çek

            long lastUpdateTime = 0;
            if (inCurrencyMode) {
                lastUpdateTime = sharedPreferences.getLong(Constants.LAST_UPDATE_TIME_CURRENCY, 0);
            } else {
                lastUpdateTime = sharedPreferences.getLong(Constants.LAST_UPDATE_TIME_GOLD, 0);
            }

            long currentTime = System.currentTimeMillis();

            boolean isExpired = currentTime - lastUpdateTime > Constants.EXPIRE_TIME;

            Log.i("Info", "last update time " + lastUpdateTime);
            Log.i("Info", "current time " + currentTime);
            Log.i("Info", "difference " + (currentTime - lastUpdateTime));


            if (isExpired) {
                Log.i("Info", "is expired");

                getDataFromAPI();

            } else {
                Log.i("Info", "is not expired");

                // Bu durumda eski dataya bak.

                Gson gson = new Gson();
                String response = "";
                if (inCurrencyMode) {
                    response = sharedPreferences.getString(Constants.CURRENCY_LIST, "");
                } else {
                    response = sharedPreferences.getString(Constants.GOLD_LIST, "");
                }

                if (response.equals("")) {
                    Log.i("Info", "new data");
                    getDataFromAPI();
                } else {
                    Log.i("Info", "old data");
                    Type type = new TypeToken<ArrayList<Currency>>() {
                    }.getType();
                    currencies = gson.fromJson(response, type);
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
            String response = "";
            if (inCurrencyMode) {
                response = sharedPreferences.getString(Constants.CURRENCY_LIST, "");
            } else {
                response = sharedPreferences.getString(Constants.GOLD_LIST, "");
            }

            if (response.equals("")) {
                Log.i("Info", "new data");
                showAnErrorMessage();
            } else {
                Log.i("Info", "old data");
                Type type = new TypeToken<ArrayList<Currency>>() {
                }.getType();
                currencies = gson.fromJson(response, type);
                updateUI();
            }

        }

        return inflatedView;
    }

    private void handleSort() {
        rowSellingValueRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {


                Collections.sort(currencies, new Comparator<Currency>() {
                    @Override
                    public int compare(Currency c1, Currency c2) {
                        if (bigToSmall) {
                            // Büyükten kücüge sıralama
                            return Double.compare(c2.getSelling(), c1.getSelling());
                        } else {
                            return Double.compare(c1.getSelling(), c2.getSelling());
                        }

                    }
                });

                adapter.notifyDataSetChanged();

                showOnlySelectedArrow(sellingValueArrow, bigToSmall);

                bigToSmall = !bigToSmall;

            }
        });

        rowBuyingValueRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(currencies, new Comparator<Currency>() {
                    @Override
                    public int compare(Currency c1, Currency c2) {
                        if (bigToSmall) {
                            return Double.compare(c2.getBuying(), c1.getBuying());
                        } else {
                            return Double.compare(c1.getBuying(), c2.getBuying());
                        }
                    }
                });

                adapter.notifyDataSetChanged();

                showOnlySelectedArrow(buyingValueArrow, bigToSmall);

                bigToSmall = !bigToSmall;
            }
        });

        changeRateRL.setOnClickListener(new View.OnClickListener() {

            private boolean bigToSmall = true;

            @Override
            public void onClick(View view) {
                Collections.sort(currencies, new Comparator<Currency>() {
                    @Override
                    public int compare(Currency c1, Currency c2) {
                        if (bigToSmall) {
                            return Double.compare(c2.getChangeRate(), c1.getChangeRate());
                        } else {
                            return Double.compare(c1.getChangeRate(), c2.getChangeRate());
                        }
                    }
                });

                adapter.notifyDataSetChanged();

                showOnlySelectedArrow(changeRateArrow, bigToSmall);

                bigToSmall = !bigToSmall;
            }
        });
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


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
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
                //JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = new JSONArray(s);

                //Log.i("Info", "jsonArray -> " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //Log.i("Info","goldObject -> " + goldObject);
                    String name = object.getString("full_name");
                    double selling = object.getDouble("selling");
                    double buying = object.getDouble("buying");
                    double changeRate = object.getDouble("change_rate");
                    long updateDate = object.getLong("update_date");
                    String code;
                    if (inCurrencyMode) {
                        code = object.getString("code");
                    } else {
                        code = object.getString("name");
                    }

                    currencies.add(new Currency(name, code, buying, selling, changeRate, updateDate));
                }

                updateUI();

                // Son güncelleme zamanını kaydet!

                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Çektiğin verileri kaydet!

                Gson gson = new Gson();
                String jsonArrayList = gson.toJson(currencies);

                if (inCurrencyMode) {
                    editor.putLong(Constants.LAST_UPDATE_TIME_CURRENCY, System.currentTimeMillis());
                    editor.putString(Constants.CURRENCY_LIST, jsonArrayList);
                } else {
                    editor.putLong(Constants.LAST_UPDATE_TIME_GOLD, System.currentTimeMillis());
                    editor.putString(Constants.GOLD_LIST, jsonArrayList);
                }

                editor.apply();

            } catch (Exception e) {
                Log.i("Info", "Hata!PostExecute!" + e.toString());
            }
        }
    }


    private void updateUI() {
        adapter = new CurrencyAdapter(getActivity(), currencies);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        if (inCurrencyMode) {
            rowName.setText("Döviz Kuru");
        } else {
            rowName.setText("Altın");
        }

        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        headerRow.setVisibility(View.VISIBLE);
        topDivider.setVisibility(View.VISIBLE);

        handleSort();

    }

    private void hideArrows() {
        sellingValueArrow.setVisibility(View.INVISIBLE);
        buyingValueArrow.setVisibility(View.INVISIBLE);
        changeRateArrow.setVisibility(View.INVISIBLE);
    }

    private void showOnlySelectedArrow(ImageView arrow, boolean bigToSmall) {
        hideArrows();

        if(bigToSmall) {
            arrow.setImageResource(R.drawable.drow_down_arrow);
        } else {
            arrow.setImageResource(R.drawable.drop_up_arrow);
        }
        arrow.setVisibility(View.VISIBLE);
    }

    private void getDataFromAPI() {
        CurrencyFragment.DownloadTask downloadTask = new CurrencyFragment.DownloadTask();
        String url = "";

        if (inCurrencyMode) {
            url = "https://www.doviz.com/api/v1/currencies/all/latest";
        } else {
            url = "https://www.doviz.com/api/v1/golds/all/latest";
        }

        downloadTask.execute(url);
    }

    private void showAnErrorMessage() {
        progressBar.setVisibility(View.GONE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void setDetailsListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CurrencyDetailActivity.class);

                Gson gson = new Gson();
                intent.putExtra(Constants.SELECTED_ITEM, gson.toJson(currencies.get(i)));
                intent.putExtra(Constants.IN_CURRENCY_MODE, inCurrencyMode);

                startActivity(intent);
            }
        });
    }


    // Cihazın internet bağlantısını kontrol eder!
    public boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

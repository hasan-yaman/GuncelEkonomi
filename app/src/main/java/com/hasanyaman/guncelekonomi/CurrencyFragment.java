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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hasanyaman.guncelekonomi.Adapters.CurrencyAdapter;
import com.hasanyaman.guncelekonomi.Data.Currency;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrencyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrencyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CurrencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CurrencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CurrencyFragment newInstance(String param1, String param2) {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    String type = "";
    ArrayList<Currency> currencies = new ArrayList<>();


    ProgressBar progressBar;

    ListView listView;

    boolean haveInternet = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            type = bundle.getString("type");
        }

        if(!isOnline()) {
            haveInternet = false;
            // cihazın internet bağlantısı yok!
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.hasanyaman.guncelekonomi",Context.MODE_PRIVATE);
            String lastUpdateTime = sharedPreferences.getString("lastUpdateTime",""); // ikinci argument default value

            String message = "İnternet bağlantınız yok!";

            if(!lastUpdateTime.equals("")) {
                // son güncelleme zamanı bilgisi elimizde varsa.
                message += "Son güncelleme zamanı: " + lastUpdateTime;
            }

            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(message)
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View inlatedView = inflater.inflate(R.layout.fragment_currency, container, false);

        listView = inlatedView.findViewById(R.id.listView);
        progressBar = inlatedView.findViewById(R.id.progress_bar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),GraphActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.hasanyaman.guncelekonomi",Context.MODE_PRIVATE);

        if(haveInternet) {
            // cihazın interneti varsa, bilgileri almaya başlayabilirsin.

            CurrencyFragment.DownloadTask downloadTask = new CurrencyFragment.DownloadTask();
            String url = "";

            if(type.equals("gold")) {
                url = "https://www.doviz.com/api/v1/golds/all/latest";
            } else {
                url = "https://www.doviz.com/api/v1/currencies/all/latest";
            }

            downloadTask.execute(url);
        } else {
            // cihazın interneti yoksa, en son kaydettiklerini göster.

            ArrayList<String> last_names = new ArrayList<>();
            ArrayList<String> last_values = new ArrayList<>();
            ArrayList<String> last_changeRates = new ArrayList<>();

            // TODO getStringde direkt olarak type kullanılabilir, kod daha kısa olur

            /*if(type.equals("gold")) {
                try {
                    last_names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("gold-names",ObjectSerializer.serialize(new ArrayList<String>())));
                    last_values = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("gold-values",ObjectSerializer.serialize(new ArrayList<String>())));
                    last_changeRates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("gold-changeRates",ObjectSerializer.serialize(new ArrayList<String>())));
                } catch(Exception e) {
                    Log.i("Info","Shared preferences! " + e.toString());
                }
            } else {
                try {
                    last_names = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("currency-names",ObjectSerializer.serialize(new ArrayList<String>())));
                    last_values = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("currency-values",ObjectSerializer.serialize(new ArrayList<String>())));
                    last_changeRates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("currency-changeRates",ObjectSerializer.serialize(new ArrayList<String>())));
                } catch(Exception e) {
                    Log.i("Info","Shared preferences! " + e.toString());
                }
            }

           if(last_names.size() != 0) {
               names = last_names;
               values = last_values;
               changeRates = last_changeRates;
               updateLayouts();
           } else {
               // bu durumda daha önce hiç bilgiler alınmamış, şu andada internet bağlantısı yok!
               // yani kullanıcıya gösterilecek herhangi bir şey yok!
           } */

        }



        return inlatedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        // TODO: Update argument type and name
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
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                String result = "";
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Log.i("Info", "Hata!DownloadTask!doInBackground" + e.toString());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = new JSONArray(s);

                Log.i("Info","jsonArray -> " + jsonArray);
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //Log.i("Info","goldObject -> " + goldObject);
                    String name = object.getString("full_name");
                    double value = object.getDouble("selling");
                    double changeRate = object.getDouble("change_rate");
                    currencies.add(new Currency(name, value, changeRate));
                }

                updateUI();

                progressBar.setVisibility(View.INVISIBLE);

                // Son güncelleme zamanını kaydet!
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.hasanyaman.guncelekonomi",Context.MODE_PRIVATE);
                Date currentTime = Calendar.getInstance().getTime();
                String lastUpdateTime = currentTime.getHours() + ":" + currentTime.getMinutes();
                sharedPreferences.edit().putString("lastUpdateTime",lastUpdateTime).apply();

                // Çektiğin verileri kaydet!

                /*if(type.equals("gold")) {
                    sharedPreferences.edit().putString("gold-names",ObjectSerializer.serialize(names)).apply();
                    sharedPreferences.edit().putString("gold-values",ObjectSerializer.serialize(values)).apply();
                    sharedPreferences.edit().putString("gold-changeRates",ObjectSerializer.serialize(changeRates)).apply();
                } else {
                    sharedPreferences.edit().putString("currency-names",ObjectSerializer.serialize(names)).apply();
                    sharedPreferences.edit().putString("currency-values",ObjectSerializer.serialize(values)).apply();
                    sharedPreferences.edit().putString("currency-changeRates",ObjectSerializer.serialize(changeRates)).apply();
                } */


            } catch (Exception e) {
                Log.i("Info", "Hata!PostExecute!" + e.toString());
            }
        }
    }


    public void updateUI() {
        CurrencyAdapter adapter = new CurrencyAdapter(getActivity(),currencies);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

    }



    // Cihazın internet bağlantısını kontrol eder!
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

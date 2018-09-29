package com.hasanyaman.guncelekonomi;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.hasanyaman.guncelekonomi.Adapters.CrytocurrencyAdapter;
import com.hasanyaman.guncelekonomi.Data.Cryptocurrency;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CryptocurrencyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CryptocurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CryptocurrencyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListView listView;
    ProgressBar progressBar;
    ArrayList<Cryptocurrency> cryptocurrencies = new ArrayList<>();

    public CryptocurrencyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CryptocurrencyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CryptocurrencyFragment newInstance(String param1, String param2) {
        CryptocurrencyFragment fragment = new CryptocurrencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_cryptocurrency, container, false);
        listView = inflatedView.findViewById(R.id.listView);
        progressBar = inflatedView.findViewById(R.id.progress_bar);

        String url = "https://www.doviz.com/api/v1/coins/all/latest";
        new DownloadTask().execute(url);

        return inflatedView;
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
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(strings[0]);
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
                Log.i("Info","Hata!2" + e.toString());
            }
            return "FAIL";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    cryptocurrencies.add(new Cryptocurrency(object.getString("full_name"),object.getString("symbol"),object.getString("selling_try"),
                            object.getDouble("change_rate"),object.getDouble("volume"),object.getInt("rank")));
                }

                Collections.sort(cryptocurrencies, new Comparator<Cryptocurrency>() {
                    @Override
                    public int compare(Cryptocurrency o1, Cryptocurrency o2) {
                        return o1.getRank() - o2.getRank();
                    }
                });

                updateUI();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUI() {
        CrytocurrencyAdapter adapter = new CrytocurrencyAdapter(getActivity(), cryptocurrencies);
        listView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }


}

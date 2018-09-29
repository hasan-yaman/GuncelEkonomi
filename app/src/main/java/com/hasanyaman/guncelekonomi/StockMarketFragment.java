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

import com.hasanyaman.guncelekonomi.Adapters.StockMarketAdapter;
import com.hasanyaman.guncelekonomi.Data.StockMarket;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockMarketFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockMarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockMarketFragment extends Fragment implements OnTaskCompleted {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String[] STOCK_MARKETS = {"XU100","XU050","XU030"};
    boolean[] allDone = {false, false, false};

    ArrayList<StockMarket> stockMarkets = new ArrayList<>();
    ListView listView;
    StockMarketAdapter stockMarketAdapter;

    ProgressBar progressBar;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private OnTaskCompleted listener;


    public StockMarketFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StockMarketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockMarketFragment newInstance(String param1, String param2) {
        StockMarketFragment fragment = new StockMarketFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_stock_market, container, false);

        listView = inflatedView.findViewById(R.id.listView);
        progressBar = inflatedView.findViewById(R.id.progress_bar);

        for(int i = 0; i<STOCK_MARKETS.length; i++) {
            String url = "https://www.doviz.com/api/v1/indexes/"+ STOCK_MARKETS[i] + "/latest";
            new DownloadTask(this,i).execute(url);

        }

        return inflatedView;
    }

    @Override
    public void onTaskCompleted() {
        Log.i("Info","Completed a task");
        if(isAllDone()) {
            Log.i("Info","Completed all the tasks");
            stockMarketAdapter = new StockMarketAdapter(getActivity(), stockMarkets);
            listView.setAdapter(stockMarketAdapter);
            progressBar.setVisibility(View.GONE);
        }

    }

    private boolean isAllDone() {
        for(boolean b : allDone) {
            if(!b) {
                return false;
            }
        }
        return true;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public  class DownloadTask extends AsyncTask<String, Void, String> {

        private OnTaskCompleted listener;
        private int index;

        public DownloadTask(OnTaskCompleted listener, int index) {
            this.listener = listener;
            this.index = index;
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
                Log.i("Info","Hata!2" + e.toString());
            }
            return "FAIL";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                Log.i("Info","jsonObject -> " + jsonObject);

                stockMarkets.add(new StockMarket(jsonObject.getString("full_name"),
                        jsonObject.getDouble("latest"),jsonObject.getDouble("change_rate"),jsonObject.getDouble("first_seance_lowest"),jsonObject.getDouble("first_seance_highest"),jsonObject.getDouble("first_seance_closing"),
                        jsonObject.getDouble("second_seance_lowest"),jsonObject.getDouble("second_seance_highest"),jsonObject.getDouble("second_seance_closing"),jsonObject.getDouble("previous_closing")));

                allDone[index] = true;
                listener.onTaskCompleted();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

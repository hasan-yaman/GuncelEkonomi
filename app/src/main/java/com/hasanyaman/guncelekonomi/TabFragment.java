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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<Date> dates = new ArrayList<>();
    ArrayList<Double> sellingValues = new ArrayList<>();

    GraphView graphView;

    public TabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabFragment newInstance(String param1, String param2) {
        TabFragment fragment = new TabFragment();
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
        View inflatedView = inflater.inflate(R.layout.fragment_tab, container, false);

        String url = "https://doviz.com/api/v1/currencies/USD/daily";
        new DownloadTask().execute(url);

        graphView = inflatedView.findViewById(R.id.graph);
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
            Log.i("Info","start doInBackground");
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
                Log.i("Info","end doInBackground");
                return result;
            } catch (Exception e) {
                Log.i("Info", "Hata!DownloadTask!doInBackground" + e.toString());
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("Info","start onPostExecute");
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);

                Calendar calendar = new GregorianCalendar();
                Log.i("Info","start first forLoop");
                for (int i = 0; i < 100; i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    double selling = object.getDouble("selling");
                    long updateTime = object.getLong("update_date");

                    calendar.setTimeInMillis(updateTime * 1000);
                    Date date = calendar.getTime();
                    dates.add(date);

                    sellingValues.add(selling);

                    /*Calendar calendar = new GregorianCalendar();
                    calendar.setTimeInMillis(updateTime * 1000);
                    Date date = calendar.getTime();
                    //int hour = calendar.get(Calendar.HOUR);
                    //int minute = calendar.get(Calendar.MINUTE);
                    int hour = date.getHours();
                    int minute = date.getMinutes();

                    Log.i("Info", "updateTime->" + updateTime +" hour->" + hour + " minute->" + minute); */
                }
                Log.i("Info", "end first forLoop");
                DataPoint[] dataPoints = new DataPoint[100];
                for (int i = 0; i < 100; i++) {
                    dataPoints[i] = new DataPoint(dates.get(i), sellingValues.get(i));
                }
                Log.i("Info","end second forLoop");

                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                graphView.addSeries(series);

                // set date label formatter
                graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
                graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

                // set manual x bounds to have nice steps
                graphView.getViewport().setMinX(dates.get(0).getTime());
                graphView.getViewport().setMaxX(dates.get(99).getTime());
                graphView.getViewport().setXAxisBoundsManual(true);

                // as we use dates as labels, the human rounding to nice readable numbers
                // is not necessary
                graphView.getGridLabelRenderer().setHumanRounding(false);

                Log.i("Info","end settings of graph");


            } catch (Exception e) {
                Log.i("Info", "Hata!PostExecute!" + e.toString());

            }
        }
    }
}

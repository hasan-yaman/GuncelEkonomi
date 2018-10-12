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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hasanyaman.guncelekonomi.Data.GraphData;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class TabFragment extends Fragment {

    private static final String ARG_CODE = "arg-code";
    private static final String ARG_GRAPH_NAME = "arg-graph-name";
    private static final String ARG_IN_CURRENCY_MODE = "arg-in-currency-mode";

    private String code;
    private String graphName;
    private boolean inCurrencyMode;

    private OnFragmentInteractionListener mListener;

    private LineChart lineChart;

    ArrayList<GraphData> graphDatas = new ArrayList<>();


    public TabFragment() {
        // Required empty public constructor
    }


    public static TabFragment newInstance(String code, String graphName, boolean inCurrencyMode) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODE, code);
        args.putString(ARG_GRAPH_NAME, graphName);
        args.putBoolean(ARG_IN_CURRENCY_MODE, inCurrencyMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getString(ARG_CODE);
            graphName = getArguments().getString(ARG_GRAPH_NAME);
            inCurrencyMode = getArguments().getBoolean(ARG_IN_CURRENCY_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_tab, container, false);

        lineChart = inflatedView.findViewById(R.id.chart);
        preConfigureLineChart();

        getDataFromApi();

        return inflatedView;
    }

    private void preConfigureLineChart() {
        lineChart.setTouchEnabled(false);
        lineChart.setDescription(null);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();

        AxisValueFormatter formatter = new AxisValueFormatter(this.graphName);
        xAxis.setValueFormatter(formatter);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);


        //xAxis.setLabelCount(6,true);

        // hide right y axis
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
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

    private void getDataFromApi() {
        new DownloadGraphData().execute(getURL());
    }


    /*




     */

    private String getURL() {
        switch (graphName) {
            case Constants.DAILY_GRAPH:
                if (inCurrencyMode) {
                    return Constants.DETAIL_URL + this.code + "/daily";
                }
                return Constants.GOLD_DETAIL_URL + this.code + "/daily";
            case Constants.WEEKLY_GRAPH:
                return calculateDateForUrl(-7);
            case Constants.MONTHLY_GRAPH:
                return calculateDateForUrl(-30);
            case Constants.YEARLY_GRAPH:
                return calculateDateForUrl(-365);
            default:
                if (inCurrencyMode) {
                    return Constants.DETAIL_URL + this.code + "/daily";
                }
                return  Constants.GOLD_DETAIL_URL + this.code + "/daily";
        }
    }


    private String calculateDateForUrl(int backInTimeInDays) {
        Calendar calendar = Calendar.getInstance();

        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (backInTimeInDays == -7) {
            calendar.add(Calendar.DAY_OF_YEAR, backInTimeInDays);
        } else if (backInTimeInDays == -30) {
            calendar.add(Calendar.MONTH, -1);
        } else if (backInTimeInDays == -365) {
            calendar.add(Calendar.YEAR, -1);
        }
        int finishYear = calendar.get(Calendar.YEAR);
        int finishMonth = calendar.get(Calendar.MONTH) + 1;
        int finishDay = calendar.get(Calendar.DAY_OF_MONTH);

        String url;

        if (inCurrencyMode) {
            url = Constants.DETAIL_URL;
        } else {
            url = Constants.GOLD_DETAIL_URL;
        }

        url += this.code + "/archive?start=" + finishYear + "-" + finishMonth + "-" +
                finishDay + "&end=" + startYear + "-" + startMonth + "-" + startDay;

        Log.i("Info", "graph url -> " + url);
        return url;
    }

    public class DownloadGraphData extends AsyncTask<String, Void, String> {


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
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    float selling = (float) object.getDouble("selling");
                    long updateTime = object.getLong("update_date");
                    graphDatas.add(new GraphData(selling, updateTime));
                    //Log.i("Info","selling -> " + selling + " updateTime -> " + updateTime);
                }

                List<Entry> entries = new ArrayList<>();

                for (GraphData data : graphDatas) {
                    entries.add(new Entry(data.getUpdateDate(), data.getSelling()));
                }

                LineDataSet dataSet = new LineDataSet(entries, "Label");

                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setColors(new int[]{R.color.colorPrimary}, getContext());

                LineData lineData = new LineData(dataSet);

                /*XAxis xAxis = lineChart.getXAxis();
                long min = graphDatas.get(0).getUpdateDate();
                long max = graphDatas.get(graphDatas.size() - 1).getUpdateDate();

                xAxis.setAxisMinimum(min);
                xAxis.setAxisMaximum(max);*/

                lineChart.setData(lineData);
                lineChart.invalidate();

                Log.i("Info", "draw chart");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

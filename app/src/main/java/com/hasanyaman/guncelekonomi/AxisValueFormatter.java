package com.hasanyaman.guncelekonomi;



import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AxisValueFormatter implements IAxisValueFormatter {

    private String graphName;

    public AxisValueFormatter(String graphName) {
        this.graphName = graphName;
    }


    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis((long) value * 1000);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        switch (this.graphName) {
            case Constants.DAILY_GRAPH:
                return addLeadingZero(hour) + ":" + addLeadingZero(minutes);
            case Constants.WEEKLY_GRAPH:
                return addLeadingZero(day) + " " + Constants.MONTH_ABBR[month];
            case Constants.MONTHLY_GRAPH:
                return addLeadingZero(day) + " " + Constants.MONTH_ABBR[month];
            case Constants.YEARLY_GRAPH:
                return Constants.MONTH_ABBR[month] + " " + formatYear(year);
            default:
                return "";
        }
    }


    private String addLeadingZero(int x) {
        if (x < 10) {
            return "0" + x;
        }
        return String.valueOf(x);
    }

    private String formatYear(int year) {
        return "'" + String.valueOf(year).substring(2);
    }
}

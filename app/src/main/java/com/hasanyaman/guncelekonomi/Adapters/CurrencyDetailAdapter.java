package com.hasanyaman.guncelekonomi.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hasanyaman.guncelekonomi.CurrencyDetailActivity;
import com.hasanyaman.guncelekonomi.Data.Bank;
import com.hasanyaman.guncelekonomi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CurrencyDetailAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bank> banks;

    public CurrencyDetailAdapter(Context context, ArrayList<Bank> banks) {
        this.context = context;
        this.banks = banks;
    }


    @Override
    public int getCount() {
        return banks.size();
    }

    @Override
    public Object getItem(int i) {
        return banks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_currency_detail, parent, false);
            holder = new ViewHolder();

            holder.nameTextView = convertView.findViewById(R.id.name);
            holder.sellingTextView = convertView.findViewById(R.id.sellingValue);
            holder.buyingTextView = convertView.findViewById(R.id.buyingValue);
            holder.updateDateTextView = convertView.findViewById(R.id.updateDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nameTextView.setText(banks.get(position).getFullName());

        DecimalFormat decimalFormat = new DecimalFormat("#.####");


        holder.sellingTextView.setText(String.valueOf(decimalFormat.format(banks.get(position).getSelling())));
        holder.buyingTextView.setText(String.valueOf(decimalFormat.format(banks.get(position).getBuying())));

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(banks.get(position).getUpdateDate() * 1000);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String updateTime = hour + ":" + minute;

        holder.updateDateTextView.setText(updateTime);

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView sellingTextView;
        TextView buyingTextView;
        TextView updateDateTextView;
    }
}

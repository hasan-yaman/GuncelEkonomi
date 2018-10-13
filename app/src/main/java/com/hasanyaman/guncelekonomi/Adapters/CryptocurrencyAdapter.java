package com.hasanyaman.guncelekonomi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasanyaman.guncelekonomi.Data.Cryptocurrency;
import com.hasanyaman.guncelekonomi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CryptocurrencyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Cryptocurrency> cryptocurrencies;

    public CryptocurrencyAdapter(Context context, ArrayList<Cryptocurrency> cryptocurrencies) {
        this.context = context;
        this.cryptocurrencies = cryptocurrencies;

    }

    @Override
    public int getCount() {
        return cryptocurrencies.size();
    }

    @Override
    public Object getItem(int position) {
        return cryptocurrencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_cryptocurrency, parent, false);
            holder = new ViewHolder();

            holder.fullNameTextView = convertView.findViewById(R.id.fullName);
            holder.valueTextView = convertView.findViewById(R.id.value);
            holder.volumeTextView = convertView.findViewById(R.id.volume);
            holder.changeRateTextView = convertView.findViewById(R.id.changeRate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.fullNameTextView.setText(cryptocurrencies.get(position).getFullName());

        holder.valueTextView.setText(cryptocurrencies.get(position).getValue_try());

        DecimalFormat decimalFormatV = new DecimalFormat();
        String volume = decimalFormatV.format(Double.parseDouble(Long.toString(cryptocurrencies.get(position).getVolume())));
        holder.volumeTextView.setText(volume);

        String changeRate = "% " + String.valueOf(cryptocurrencies.get(position).getChangeRate());
        holder.changeRateTextView.setText(changeRate);

        if(cryptocurrencies.get(position).getChangeRate() > 0) {
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (cryptocurrencies.get(position).getChangeRate() < 0){
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView fullNameTextView;
        TextView valueTextView;
        TextView volumeTextView;
        TextView changeRateTextView;
    }
}

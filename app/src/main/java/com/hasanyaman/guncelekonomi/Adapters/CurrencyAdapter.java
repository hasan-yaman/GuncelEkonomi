package com.hasanyaman.guncelekonomi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasanyaman.guncelekonomi.Data.Currency;
import com.hasanyaman.guncelekonomi.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CurrencyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Currency> currencies;

    public CurrencyAdapter(Context context, ArrayList<Currency> currencies) {
        this.context = context;
        this.currencies = currencies;
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public Object getItem(int position) {
        return currencies.get(position);
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
            convertView = inflater.inflate(R.layout.row_currency, parent, false);
            holder = new ViewHolder();

            holder.nameTextView = convertView.findViewById(R.id.name);
            holder.valueTextView = convertView.findViewById(R.id.value);
            holder.changeRateTextView = convertView.findViewById(R.id.changeRate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(currencies.get(position).getName());

        DecimalFormat decimalFormatV = new DecimalFormat("#.####");
        holder.valueTextView.setText(decimalFormatV.format(currencies.get(position).getValue()));

        DecimalFormat decimalFormatCR = new DecimalFormat("#.##");
        String changeRate = "% " + decimalFormatCR.format(currencies.get(position).getChangeRate());
        holder.changeRateTextView.setText(changeRate);

        if(currencies.get(position).getChangeRate() > 0) {
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (currencies.get(position).getChangeRate() < 0){
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            holder.changeRateTextView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }


        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView valueTextView;
        TextView changeRateTextView;
    }
}

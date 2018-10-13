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
import java.text.DecimalFormatSymbols;
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
            holder.sellingValueTextView = convertView.findViewById(R.id.sellingValue);
            holder.buyingValueTextView = convertView.findViewById(R.id.buyingValue);
            holder.changeRateTextView = convertView.findViewById(R.id.changeRate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(currencies.get(position).getName());


        DecimalFormat decimalFormatV = new DecimalFormat();
        decimalFormatV.setMinimumFractionDigits(4);
        decimalFormatV.setMaximumFractionDigits(4);
        decimalFormatV.setMinimumIntegerDigits(1);

        holder.sellingValueTextView.setText(decimalFormatV.format(currencies.get(position).getSelling()));
        holder.buyingValueTextView.setText(decimalFormatV.format(currencies.get(position).getBuying()));

        DecimalFormat decimalFormatCR = new DecimalFormat();
        decimalFormatCR.setMinimumFractionDigits(2);
        decimalFormatCR.setMaximumFractionDigits(2);
        decimalFormatCR.setMinimumIntegerDigits(1);

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
        TextView sellingValueTextView;
        TextView buyingValueTextView;
        TextView changeRateTextView;
    }
}

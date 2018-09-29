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
            holder.arrowImageView = convertView.findViewById(R.id.imgArrow);
            holder.changeRateTextView = convertView.findViewById(R.id.changeRate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(currencies.get(position).getName());
        holder.valueTextView.setText(String.valueOf(currencies.get(position).getValue()));
        holder.changeRateTextView.setText(String.valueOf(currencies.get(position).getChangeRate()));
        if(currencies.get(position).getChangeRate() > 0) {
            holder.arrowImageView.setImageResource(R.drawable.up);
        } else {
            holder.arrowImageView.setImageResource(R.drawable.down);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView valueTextView;
        ImageView arrowImageView;
        TextView changeRateTextView;
    }
}

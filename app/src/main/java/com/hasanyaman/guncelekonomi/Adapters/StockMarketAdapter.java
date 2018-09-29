package com.hasanyaman.guncelekonomi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hasanyaman.guncelekonomi.R;
import com.hasanyaman.guncelekonomi.Data.StockMarket;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StockMarketAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<StockMarket> stockMarkets;

    public StockMarketAdapter(Context context, ArrayList<StockMarket> stockMarkets) {
        this.context = context;
        this.stockMarkets = stockMarkets;
    }


    @Override
    public int getCount() {
        return stockMarkets.size();
    }

    @Override
    public Object getItem(int position) {
        return stockMarkets.get(position);
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
            convertView = inflater.inflate(R.layout.row_stock_market, parent, false);
            holder = new ViewHolder();

            holder.nameTextView = convertView.findViewById(R.id.name);
            holder.latestTextView = convertView.findViewById(R.id.latest);
            holder.changeRate = convertView.findViewById(R.id.changeRate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(stockMarkets.get(position).getFullName());

        holder.latestTextView.setText(String.valueOf(stockMarkets.get(position).getLatest()));

        DecimalFormat decimalFormatCR = new DecimalFormat("#.##");
        String changeRate = "% " + decimalFormatCR.format(stockMarkets.get(position).getChangeRate());
        holder.changeRate.setText(changeRate);

        if(stockMarkets.get(position).getChangeRate() > 0) {
            holder.changeRate.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else if (stockMarkets.get(position).getChangeRate() < 0){
            holder.changeRate.setBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            holder.changeRate.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }


        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView latestTextView;
        TextView changeRate;
        /*TextView firstSeanceLowest;
        TextView firstSeanceHighest;
        TextView firstSeanceClosing;
        TextView secondSeanceLowest;
        TextView secondSeanceHighest;
        TextView secondSeanceClosing; */
    }
}

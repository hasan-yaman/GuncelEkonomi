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
            holder.imgArrow = convertView.findViewById(R.id.imgArrow);
            holder.changeRate = convertView.findViewById(R.id.changeRate);
            holder.firstSeanceLowest = convertView.findViewById(R.id.firstSeanceLowest);
            holder.firstSeanceHighest = convertView.findViewById(R.id.firstSeanceHighest);
            holder.firstSeanceClosing = convertView.findViewById(R.id.firstSeanceClosing);
            holder.secondSeanceLowest = convertView.findViewById(R.id.secondSeanceLowest);
            holder.secondSeanceHighest = convertView.findViewById(R.id.secondSeanceHighest);
            holder.secondSeanceClosing = convertView.findViewById(R.id.secondSeanceClosing);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.nameTextView.setText(stockMarkets.get(position).getFullName());
        holder.latestTextView.setText(String.valueOf(stockMarkets.get(position).getLatest()));
        holder.changeRate.setText(String.valueOf(stockMarkets.get(position).getChangeRate()));
        if(stockMarkets.get(position).getChangeRate() > 0){
            holder.imgArrow.setImageResource(R.drawable.up);
        } else {
            holder.imgArrow.setImageResource(R.drawable.down);
        }
        if(stockMarkets.get(position).getFirstSeanceLowest() != 0) {
            holder.firstSeanceLowest.setText(String.valueOf(stockMarkets.get(position).getFirstSeanceLowest()));
        } else {
            holder.firstSeanceLowest.setVisibility(View.GONE);
        }
        if(stockMarkets.get(position).getFirstSeanceHighest() != 0) {
            holder.firstSeanceHighest.setText(String.valueOf(stockMarkets.get(position).getFirstSeanceHighest()));
        } else {
            holder.firstSeanceHighest.setVisibility(View.GONE);
        }
        if(stockMarkets.get(position).getFirstSeanceClosing() != 0) {
            holder.firstSeanceClosing.setText(String.valueOf(stockMarkets.get(position).getFirstSeanceClosing()));
        } else {
            holder.firstSeanceClosing.setVisibility(View.GONE);
        }

        if(stockMarkets.get(position).getSecondSeanceLowest() != 0) {
            holder.secondSeanceLowest.setText(String.valueOf(stockMarkets.get(position).getSecondSeanceLowest()));
        } else {
            holder.secondSeanceLowest.setVisibility(View.GONE);
        }
        if(stockMarkets.get(position).getSecondSeanceHighest() != 0) {
            holder.secondSeanceHighest.setText(String.valueOf(stockMarkets.get(position).getSecondSeanceHighest()));
        } else {
            holder.secondSeanceHighest.setVisibility(View.GONE);
        }
        if(stockMarkets.get(position).getSecondSeanceClosing() != 0) {
            holder.secondSeanceClosing.setText(String.valueOf(stockMarkets.get(position).getSecondSeanceClosing()));
        } else {
            holder.secondSeanceClosing.setVisibility(View.GONE);
        }


        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView latestTextView;
        ImageView imgArrow;
        TextView changeRate;
        TextView firstSeanceLowest;
        TextView firstSeanceHighest;
        TextView firstSeanceClosing;
        TextView secondSeanceLowest;
        TextView secondSeanceHighest;
        TextView secondSeanceClosing;
    }
}

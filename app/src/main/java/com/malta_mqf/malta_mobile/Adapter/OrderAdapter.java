package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Order_history> mealTypeList;
    private final LayoutInflater mLayoutInflater;
    private final int snoCounter = 1; // Counter for auto-incrementing S.No

    public OrderAdapter(Context context, List<Order_history> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public Order_history getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_order, null);
            holder = new HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }
        holder.sno = convertView.findViewById(R.id.tvSerialNumber);
        holder.order_no = convertView.findViewById(R.id.tvOrderId);
        holder.date = convertView.findViewById(R.id.tvDate);
        holder.outletname = convertView.findViewById(R.id.tvOutletName);
        holder.not_synced = convertView.findViewById(R.id.not_synced);
        holder.synced = convertView.findViewById(R.id.synced);
        holder.approved = convertView.findViewById(R.id.approved);
        holder.deliverydone = convertView.findViewById(R.id.deliverydone);
        holder.rejected = convertView.findViewById(R.id.rejected);
        // Set auto-incremented S.No
        holder.sno.setText(String.valueOf(position + 1));


        holder.order_no.setText(mealTypeList.get(position).getOrder_no());
        holder.date.setText(mealTypeList.get(position).getDate());
        holder.outletname.setText(mealTypeList.get(position).getOutletname());
        if (mealTypeList.get(position).getOrderstatus().equalsIgnoreCase("NOT SYNCED")) {
            holder.not_synced.setVisibility(View.VISIBLE);
            holder.synced.setVisibility(View.GONE);
            holder.approved.setVisibility(View.GONE);
            holder.deliverydone.setVisibility(View.GONE);
        } else if (mealTypeList.get(position).getOrderstatus().equalsIgnoreCase("SYNCED")) {
            holder.not_synced.setVisibility(View.GONE);
            holder.synced.setVisibility(View.VISIBLE);
            holder.approved.setVisibility(View.GONE);
            holder.deliverydone.setVisibility(View.GONE);
        } else if (mealTypeList.get(position).getOrderstatus().equalsIgnoreCase("PENDING FOR DELIVERY")) {
            holder.not_synced.setVisibility(View.GONE);
            holder.synced.setVisibility(View.GONE);
            holder.approved.setVisibility(View.VISIBLE);
            holder.deliverydone.setVisibility(View.GONE);
        } else if (mealTypeList.get(position).getOrderstatus().equalsIgnoreCase("REJECTED") || mealTypeList.get(position).getOrderstatus().equalsIgnoreCase("REJECTED SYNCED")) {
            holder.not_synced.setVisibility(View.GONE);
            holder.synced.setVisibility(View.GONE);
            holder.approved.setVisibility(View.GONE);
            holder.rejected.setVisibility(View.VISIBLE);
            holder.deliverydone.setVisibility(View.GONE);
        } else {
            holder.not_synced.setVisibility(View.GONE);
            holder.synced.setVisibility(View.GONE);
            holder.approved.setVisibility(View.GONE);
            holder.deliverydone.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    public void clear() {
        mealTypeList.clear();
    }

    public void addAll(List<Order_history> newOrders) {
        mealTypeList.addAll(newOrders);
    }

    class HViewHolder {
        TextView sno, order_no, date, outletname;
        ImageView not_synced, synced, approved, deliverydone, rejected;
    }
}

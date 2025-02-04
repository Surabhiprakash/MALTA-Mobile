package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.OnlineOrderHistoryBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class OnlineOrderHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<OnlineOrderHistoryBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public OnlineOrderHistoryAdapter(Context context, List<OnlineOrderHistoryBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public OnlineOrderHistoryBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OnlineOrderHistoryAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_history, null);
            holder = new OnlineOrderHistoryAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (OnlineOrderHistoryAdapter.HViewHolder) convertView.getTag();
        }
        holder.agency = convertView.findViewById(R.id.tvSno);
        holder.item_name = convertView.findViewById(R.id.tvOid);
        holder.req_qty = convertView.findViewById(R.id.tvDate);
        holder.approved_qty = convertView.findViewById(R.id.tvQty);
        // Set auto-incremented S.No
        holder.agency.setText(mealTypeList.get(position).getAgencyName());


        holder.item_name.setText(mealTypeList.get(position).getItemName());
        String reqQty = String.valueOf(mealTypeList.get(position).getQty());
        if(mealTypeList.get(position).getApproved_qty()==null){
            String approvedQty = "N/A";
            holder.approved_qty.setText("N/A");
        }else{
            String approvedQty = String.valueOf(mealTypeList.get(position).getApproved_qty());
            holder.approved_qty.setText(approvedQty);
        }


        if (reqQty != null) {
            holder.req_qty.setText(reqQty);
        }

        return convertView;
    }

    class HViewHolder {
        TextView agency, item_name, req_qty,approved_qty;
    }
}

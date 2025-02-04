package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ReturnItemsBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class ReturnAdapter extends BaseAdapter {

    private Context mContext;
    private List<ReturnItemsBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public ReturnAdapter(Context context, List<ReturnItemsBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public ReturnItemsBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReturnAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_return_details, null);
            holder = new ReturnAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ReturnAdapter.HViewHolder) convertView.getTag();
        }
        holder.sno = convertView.findViewById(R.id.tvSno);
        holder.status = convertView.findViewById(R.id.tvStatus);
        holder.invoice_no = convertView.findViewById(R.id.tvInvoice);
        holder.date=convertView.findViewById(R.id.tvDate);
        holder.outletname=convertView.findViewById(R.id.tvOutletName);
        // Set auto-incremented S.No
        holder.sno.setText(String.valueOf(position + 1));


        holder.status.setText(mealTypeList.get(position).getStatus());
        holder.invoice_no.setText(mealTypeList.get(position).getInvoice_no());
        holder.date.setText(mealTypeList.get(position).getDate());
        holder.outletname.setText(mealTypeList.get(position).getOutletname());

        return convertView;
    }

    class HViewHolder {
        TextView sno, status, invoice_no,date,outletname;
    }
}
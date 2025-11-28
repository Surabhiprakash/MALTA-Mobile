package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ReturnHistoryBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class ReturnHistoryAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<ReturnHistoryBean> mealTypeList;
    private final LayoutInflater mLayoutInflater;
    private final int snoCounter = 1; // Counter for auto-incrementing S.No

    public ReturnHistoryAdapter(Context context, List<ReturnHistoryBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public ReturnHistoryBean getItem(int position) {
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
            convertView = mLayoutInflater.inflate(R.layout.delivery_orderd_invoiceno, null);
            holder = new HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }
        holder.sno = convertView.findViewById(R.id.tvSerialNumber);
        holder.order_no = convertView.findViewById(R.id.tvOrderId);
        holder.date = convertView.findViewById(R.id.tvDate);
        holder.status = convertView.findViewById(R.id.tvstatus);
        holder.customerName = convertView.findViewById(R.id.tvCustomerName);
        // Set auto-incremented S.No
        holder.sno.setText(String.valueOf(position + 1));
        holder.order_no.setText(mealTypeList.get(position).getCreditNoteID());
        holder.date.setText(mealTypeList.get(position).getDatetime());
        holder.status.setText(mealTypeList.get(position).getStatus());
        holder.customerName.setText(mealTypeList.get(position).getOutletName());
        return convertView;
    }

    class HViewHolder {
        TextView sno, order_no, date, status, customerName;
    }
}

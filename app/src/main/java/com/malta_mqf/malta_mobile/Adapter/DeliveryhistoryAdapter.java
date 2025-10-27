package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.deliveryhistorybean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class DeliveryhistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<deliveryhistorybean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public DeliveryhistoryAdapter(Context context, List<deliveryhistorybean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public deliveryhistorybean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mealTypeList == null || mealTypeList.isEmpty()) {
            return convertView;  // Or return an empty view if needed
        }

        final DeliveryhistoryAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.delivery_orderd_invoiceno, null);
            holder = new DeliveryhistoryAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (DeliveryhistoryAdapter.HViewHolder) convertView.getTag();
        }

        holder.sno = convertView.findViewById(R.id.tvSerialNumber);
        holder.order_no = convertView.findViewById(R.id.tvOrderId);
        holder.date = convertView.findViewById(R.id.tvDate);
        holder.status = convertView.findViewById(R.id.tvstatus);
        holder.customerName = convertView.findViewById(R.id.tvCustomerName);

        // Set auto-incremented S.No
        holder.sno.setText(String.valueOf(position + 1));

        // Safely access the list element
        deliveryhistorybean history = mealTypeList.get(position);

        holder.order_no.setText(history.getInvoiceOrOrderID());
        holder.date.setText(history.getDatetime());
        holder.status.setText(history.getStatus());
        holder.customerName.setText(history.getOutletName());

        return convertView;
    }

    class HViewHolder {
        TextView sno, order_no, date, status, customerName;
    }
}

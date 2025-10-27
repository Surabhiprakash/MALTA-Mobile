package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ReturnItemDetailsBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class ReturnedAdapter extends BaseAdapter {
    private Context mContext;
    private List<ReturnItemDetailsBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public ReturnedAdapter(Context context, List<ReturnItemDetailsBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public ReturnItemDetailsBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReturnedAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_returned, null);
            holder = new ReturnedAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ReturnedAdapter.HViewHolder) convertView.getTag();
        }
        holder.itemName = convertView.findViewById(R.id.tvItem);
        holder.delivered_qty = convertView.findViewById(R.id.tvDelQty);
        holder.return_qty = convertView.findViewById(R.id.tvReturnQty);
        holder.reason = convertView.findViewById(R.id.spReason);

        // Set auto-incremented S.No
        holder.itemName.setText(mealTypeList.get(position).getItemName());


        holder.delivered_qty.setText(mealTypeList.get(position).getDelivered_qty());
        holder.return_qty.setText(mealTypeList.get(position).getReturn_qty());

        // Add the TextWatcher to the EditText

        holder.reason.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, mealTypeList.get(position).getReasonList()));

        return convertView;
    }

    class HViewHolder {
        TextView itemName, delivered_qty;
        Spinner reason;
        TextWatcher textWatcher;
        EditText return_qty;
    }
}

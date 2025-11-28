package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.OrderConfrimBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class OrderConfrimSpinnerAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<OrderConfrimBean> mealTypeList;
    private final LayoutInflater mLayoutInflater;

    public OrderConfrimSpinnerAdapter(Context context, List<OrderConfrimBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public OrderConfrimBean getItem(int position) {
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
            convertView = mLayoutInflater.inflate(R.layout.stockinventoryitem, null);
            holder = new HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }
        holder.txtName = convertView.findViewById(R.id.tvItemLabel);
        holder.txtqty = convertView.findViewById(R.id.tvQtyLabel);
        holder.txtName.setText(mealTypeList.get(position).getProductName());
        holder.txtqty.setText(mealTypeList.get(position).getProductsQty());
        return convertView;
    }

    class HViewHolder {
        TextView txtName, txtqty;
    }
}

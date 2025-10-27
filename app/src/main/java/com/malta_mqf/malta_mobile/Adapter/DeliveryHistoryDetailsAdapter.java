package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class DeliveryHistoryDetailsAdapter extends BaseAdapter {

    private Context mContext;
    private List<DeliveryHistoryDeatilsBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public DeliveryHistoryDetailsAdapter(Context context, List<DeliveryHistoryDeatilsBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public DeliveryHistoryDeatilsBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DeliveryHistoryDetailsAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_delivery_history, null);
            holder = new DeliveryHistoryDetailsAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (DeliveryHistoryDetailsAdapter.HViewHolder) convertView.getTag();
        }
        holder.sno = convertView.findViewById(R.id.tvSerialNumber);
        holder.itemName = convertView.findViewById(R.id.tvItemName);
        holder.code = convertView.findViewById(R.id.tvCode);
        holder.qty = convertView.findViewById(R.id.tvQty);
        holder.price = convertView.findViewById(R.id.tvPrice);
        holder.disc = convertView.findViewById(R.id.tvDisc);
        holder.net = convertView.findViewById(R.id.tvNet);
        holder.vatPer = convertView.findViewById(R.id.tvVatPercent);
        holder.vatAmt = convertView.findViewById(R.id.tvVatAmt);
        holder.grossAmt = convertView.findViewById(R.id.tvGross);
        holder.sno.setText(String.valueOf(position + 1));
        // Set auto-incremented S.No
        holder.itemName.setText(mealTypeList.get(position).getItemname());


        holder.code.setText(mealTypeList.get(position).getItemCode());
        if (mealTypeList.get(position).getDelqty() == null) {
            holder.qty.setText("N/A");
        } else {
            holder.qty.setText(mealTypeList.get(position).getDelqty());
        }

        if (mealTypeList.get(position).getPrice() == null || mealTypeList.get(position).getPrice().isEmpty() || mealTypeList.get(position).getPrice().equals("null")) {
            holder.price.setText("N/A");
        } else {
            holder.price.setText(mealTypeList.get(position).getPrice());
        }

        holder.disc.setText(mealTypeList.get(position).getDisc());
        if (mealTypeList.get(position).getNet() == null || mealTypeList.get(position).getNet().equals("null")) {
            holder.net.setText("N/A");
        } else {
            holder.net.setText(mealTypeList.get(position).getNet());
        }

        if (mealTypeList.get(position).getVatpencet() == null || mealTypeList.get(position).getVatpencet().equals("null")) {
            holder.vatPer.setText("N/A");
        } else {
            holder.vatPer.setText(mealTypeList.get(position).getVatpencet());
        }

        if (mealTypeList.get(position).getVat() == null || mealTypeList.get(position).getVat().equals("null")) {
            holder.vatAmt.setText("N/A");
        } else {
            holder.vatAmt.setText(mealTypeList.get(position).getVat());
        }

        if (mealTypeList.get(position).getGross() == null || mealTypeList.get(position).getGross().equals("null")) {
            holder.grossAmt.setText("N/A");
        } else {
            holder.grossAmt.setText(mealTypeList.get(position).getGross());
        }


        return convertView;
    }

    class HViewHolder {
        TextView sno, itemName, code, qty, price, disc, net, vatPer, vatAmt, grossAmt;
    }
}
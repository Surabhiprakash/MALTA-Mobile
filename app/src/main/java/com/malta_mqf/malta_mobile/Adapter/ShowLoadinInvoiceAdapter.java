package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ShowLoadinInvoiceBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class ShowLoadinInvoiceAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShowLoadinInvoiceBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public ShowLoadinInvoiceAdapter(Context context, List<ShowLoadinInvoiceBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public ShowLoadinInvoiceBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ShowLoadinInvoiceAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_credit_note, null);
            holder = new ShowLoadinInvoiceAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ShowLoadinInvoiceAdapter.HViewHolder) convertView.getTag();
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
        holder.itemName.setText(mealTypeList.get(position).getProductname());


        holder.code.setText(mealTypeList.get(position).getProductcode());
        if (mealTypeList.get(position).getAvailableQty() == null) {
            holder.qty.setText("N/A");
        } else {
            holder.qty.setText(mealTypeList.get(position).getAvailableQty());
        }

        holder.price.setText(mealTypeList.get(position).getPurchasePrice());
        holder.disc.setText(mealTypeList.get(position).getDisc());
        if (mealTypeList.get(position).getItemNet() == null) {
            holder.net.setText("N/A");
        } else {
            holder.net.setText(mealTypeList.get(position).getItemNet());
        }

        holder.vatPer.setText(mealTypeList.get(position).getItemVatPercent());
        if (mealTypeList.get(position).getItemVat() == null) {
            holder.vatAmt.setText("N/A");
        } else {
            holder.vatAmt.setText(mealTypeList.get(position).getItemVat());
        }

        if (mealTypeList.get(position).getItemGross() == null) {
            holder.grossAmt.setText("N/A");
        } else {
            holder.grossAmt.setText(mealTypeList.get(position).getItemGross());
        }


        return convertView;
    }

    class HViewHolder {
        TextView sno, itemName, code, qty, price, disc, net, vatPer, vatAmt, grossAmt;
    }
}

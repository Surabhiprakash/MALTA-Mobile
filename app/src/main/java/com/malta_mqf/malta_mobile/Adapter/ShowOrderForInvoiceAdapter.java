package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class ShowOrderForInvoiceAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShowOrderForInvoiceBean> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private int snoCounter = 1; // Counter for auto-incrementing S.No

    public ShowOrderForInvoiceAdapter(Context context, List<ShowOrderForInvoiceBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public ShowOrderForInvoiceBean getItem(int position) {
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
            convertView = mLayoutInflater.inflate(R.layout.item_credit_note, parent, false);
            holder = new HViewHolder();

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

            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }

        // Ensure valid position before accessing the list
        if (position < mealTypeList.size()) {
            ShowOrderForInvoiceBean item = mealTypeList.get(position);

            // Set serial number correctly (1-based index)
            holder.sno.setText(String.valueOf(position + 1));

            holder.itemName.setText(item.getItemName());
            holder.code.setText(item.getItemCode());

            // Reset all values to avoid recycled data issues
            holder.qty.setText(item.getDelqty() != null ? item.getDelqty() : "0");
            holder.price.setText(item.getSellingprice() != null ? item.getSellingprice() : "0.00");
            holder.disc.setText(item.getDisc() != null ? item.getDisc() : "0.00");
            holder.net.setText(item.getNet() != null ? item.getNet() : "0.00");
            holder.vatPer.setText("5"); // Ensure VAT percentage is fixed at 5
            holder.vatAmt.setText(item.getVat_amt() != null ? item.getVat_amt() : "0.00");
            holder.grossAmt.setText(item.getGross() != null ? item.getGross() : "0.00");
        }

        return convertView;
    }

    class HViewHolder {
        TextView sno, itemName, code, qty, price, disc, net, vatPer, vatAmt, grossAmt;
    }
}
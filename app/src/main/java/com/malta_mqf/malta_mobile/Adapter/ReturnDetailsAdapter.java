package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.ReturnItemDetailsBean;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ReturnDetailsAdapter extends BaseAdapter {

    public interface OnReturnQuantityExceededListener {
        void onReturnQuantityExceeded(boolean isExceeded);
    }

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ReturnPrefs";
    private Context mContext;
    private List<ReturnItemDetailsBean> mealTypeList;
    private List<ReturnItemDetailsBean> fullList;

    private LayoutInflater mLayoutInflater;
    private OnReturnQuantityExceededListener returnQuantityExceededListener;

    public ReturnDetailsAdapter(Context context, List<ReturnItemDetailsBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = new ArrayList<>(mealTypeList); // Initialize with provided list
        this.fullList = new ArrayList<>(mealTypeList);
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setOnReturnQuantityExceededListener(OnReturnQuantityExceededListener listener) {
        this.returnQuantityExceededListener = listener;
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

    public void updateData(List<ReturnItemDetailsBean> newList) {
        this.mealTypeList.clear();
        this.mealTypeList.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList); // Update the full list as well
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_return, null);
            holder = new HViewHolder();
            holder.itemName = convertView.findViewById(R.id.tvItem);
            holder.delivered_qty = convertView.findViewById(R.id.tvDelQty);
            holder.return_qty = convertView.findViewById(R.id.tvReturnQty);
            holder.reason = convertView.findViewById(R.id.spReason);
            holder.textWatcher = new CustomTextWatcher(holder);
            holder.return_qty.addTextChangedListener(holder.textWatcher);
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }

        holder.textWatcher.updatePosition(position);

        final ReturnItemDetailsBean currentItem = mealTypeList.get(position);
        holder.itemName.setText(currentItem.getItemName());
        holder.delivered_qty.setText(currentItem.getDelivered_qty());

        String savedReturnQty = sharedPreferences.getString("return_qty_" + currentItem.getItemName(), null);
        if (savedReturnQty != null) {
            holder.return_qty.setText(savedReturnQty);
            currentItem.setReturn_qty(savedReturnQty);
        } else {
            holder.return_qty.setText(currentItem.getReturn_qty());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, currentItem.getReasonList());
        holder.reason.setAdapter(spinnerAdapter);
        holder.reason.setSelection(spinnerAdapter.getPosition(currentItem.getReason()));

        holder.reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentItem.setReason(parent.getItemAtPosition(pos).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return convertView;
    }

    private class CustomTextWatcher implements TextWatcher {
        private int position;
        private HViewHolder holder;

        public CustomTextWatcher(HViewHolder holder) {
            this.holder = holder;
        }

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String returnQtyStr = editable.toString();
            ReturnItemDetailsBean currentItem = mealTypeList.get(position);
            currentItem.setReturn_qty(returnQtyStr);

            boolean isExceeded = false;

            if (returnQtyStr.isEmpty()) {
                currentItem.setReturn_qty("");
            } else {
                int returnQty = Integer.parseInt(returnQtyStr);
                int deliveredQty = Integer.parseInt(currentItem.getDelivered_qty());
                if (returnQty > deliveredQty) {
                    holder.return_qty.setError("Return quantity cannot exceed delivered quantity.");
                    isExceeded = true;
                } else {
                    holder.return_qty.setError(null);
                    // Store in SharedPreferences using productID
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("return_qty_" + currentItem.getItemName(), returnQtyStr);
                    editor.apply();
                }
            }

            notifyReturnQuantityExceeded(isExceeded);
        }

        private void notifyReturnQuantityExceeded(boolean isExceeded) {
            if (returnQuantityExceededListener != null) {
                returnQuantityExceededListener.onReturnQuantityExceeded(isExceeded);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        if (fullList == null) {
            fullList = new ArrayList<>();
        }

        if (text.isEmpty()) {
            // Restore full list if filter text is empty
            mealTypeList.clear();
            if (fullList != null) {
                mealTypeList.addAll(fullList); // Restore the full list
            }
        } else {
            // Filter fullList based on text
            List<ReturnItemDetailsBean> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (ReturnItemDetailsBean item : fullList) {
                if (item.getItemName().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            mealTypeList.clear();
            mealTypeList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    static class HViewHolder {
        TextView itemName, delivered_qty;
        Spinner reason;
        EditText return_qty;
        CustomTextWatcher textWatcher;
    }
}

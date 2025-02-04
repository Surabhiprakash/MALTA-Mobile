package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class UpdateOrderAdapter extends BaseAdapter {
    private Context mContext;
    private List<Order_history> mealTypeList;
    private LayoutInflater mLayoutInflater;
    private SubmitOrderDB submitOrderDB;
    String position ;

    public UpdateOrderAdapter(Context context, List<Order_history> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
        this.submitOrderDB = new SubmitOrderDB(context);
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public Order_history getItem(int position) {
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
            convertView = mLayoutInflater.inflate(R.layout.update_order, null);
            holder = new HViewHolder();
            holder.sno = convertView.findViewById(R.id.tvSno);
            holder.order_no = convertView.findViewById(R.id.tvOid);
            holder.editTextQty = convertView.findViewById(R.id.qnty);
            holder.cancelButton = convertView.findViewById(R.id.cancelButton);
            holder.okButton = convertView.findViewById(R.id.okButton);
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }

        // Set auto-incremented S.No
        holder.sno.setText(mealTypeList.get(position).getAgencyName());

        holder.order_no.setText(mealTypeList.get(position).getItemName());
        holder.editTextQty.setText(String.valueOf(mealTypeList.get(position).getQty()));

        // Store the position as a tag in the EditText
        holder.editTextQty.setTag(position);

        // Attach an OnFocusChangeListener to the EditText to monitor focus changes
        holder.editTextQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When the EditText loses focus, update the quantity in the Order_history object
                    String qty = holder.editTextQty.getText().toString();
                    int newQty = qty.isEmpty() ? 0 : Integer.parseInt(qty);
                    int currentPosition = (int) holder.editTextQty.getTag();
                    mealTypeList.get(currentPosition).setQty(String.valueOf(newQty));

                    // Update the quantity in the SubmitOrderDB
                    String orderID = mealTypeList.get(currentPosition).getOrderID();
                    String productID = mealTypeList.get(currentPosition).getProductID();
                    System.out.println("Order ID.............: " + orderID);
                    System.out.println("Product ID...............: " + productID);
                //    boolean isUpdated = submitOrderDB.updateQuantityByProductIDAndOrderID(productID, orderID, String.valueOf(newQty));

                    // Notify the adapter that the data has changed
                  /*  if (isUpdated) {
                        notifyDataSetChanged();
                    } else {
                        // Handle update failure
                    }*/
                }
            }
        });
        return convertView;
    }

    /*private void updateQuantityInSubmitOrderDB(String orderID, String productID, int newQuantity) {
        // Perform the update in the SubmitOrderDB using the provided orderID, productID, and newQuantity
        updateQuantity(Integer.parseInt(position), newQuantity);
        submitOrderDB.updateQuantityByProductID(orderID, productID, newQuantity);
    }
    public void updateQuantity(int position, int newQuantity) {
        // Ensure the position is valid
        if (position >= 0 && position < mealTypeList.size()) {
            // Update the quantity in the Order_history object
            mealTypeList.get(position).setQty(newQuantity);
            // Notify the adapter that the data has changed
            notifyDataSetChanged();
        }
    }*/


    class HViewHolder {
        TextView sno, order_no;
        EditText editTextQty;
        Button cancelButton, okButton;
    }
}

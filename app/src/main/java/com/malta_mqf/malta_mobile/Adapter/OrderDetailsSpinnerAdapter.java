package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class OrderDetailsSpinnerAdapter extends RecyclerView.Adapter<OrderDetailsSpinnerAdapter.ViewHolder> {

    private Context mContext;
    private List<Order_history> mealTypeList;
    private SubmitOrderDB submitOrderDB;
    private LayoutInflater mLayoutInflater;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public OrderDetailsSpinnerAdapter(Context context, List<Order_history> mealTypeList) {
        this.mContext = context;
        this.mealTypeList = mealTypeList;
        this.submitOrderDB = new SubmitOrderDB(context);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public Order_history getItem(int position) {
        if (position >= 0 && position < mealTypeList.size()) {
            return mealTypeList.get(position);
        }
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_showfullorder, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order_history orderHistory = mealTypeList.get(position);
        String agencyName = orderHistory.getAgencyName();
        String productName = orderHistory.getItemName();
        String deletedItemQuantity = String.valueOf(orderHistory.getQty());

        holder.sno.setText(orderHistory.getAgencyName());
        holder.order_no.setText(orderHistory.getItemName());
        holder.date.setText(String.valueOf(orderHistory.getQty()));

    /*    holder.cancelButton.setOnClickListener(view -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position, orderHistory);
            }
        });

        holder.okButton.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(position, orderHistory);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mealTypeList.size();
    }

    public void restoreItem(int position, String agencyName, String productName, String deletedItemQuantity) {
        if (position >= 0 && position <= mealTypeList.size()) {
            // Restore the item to the original position
            mealTypeList.add(position, new Order_history(agencyName, productName, deletedItemQuantity));
            notifyItemInserted(position);
        } else {
            // Handle the case where the position is out of bounds
            // You might want to log a message or throw an exception
        }


    }

    public void removeItem(int itemPosition) {
        if (itemPosition >= 0 && itemPosition < mealTypeList.size()) {
            // Remove the item from the list
            mealTypeList.remove(itemPosition);
            notifyItemRemoved(itemPosition);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Order_history selectedItem);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position, Order_history selectedItem);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sno, order_no, date;
        Button cancelButton, okButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sno = itemView.findViewById(R.id.tvSno);
            order_no = itemView.findViewById(R.id.tvOid);
            date = itemView.findViewById(R.id.tvqty);
            cancelButton = itemView.findViewById(R.id.cancelButton);
            okButton = itemView.findViewById(R.id.okButton);
        }
    }
}

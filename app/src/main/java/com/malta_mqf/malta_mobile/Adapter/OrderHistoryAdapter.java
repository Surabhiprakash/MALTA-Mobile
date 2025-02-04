package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.R;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Order_history> mealTypeList;
    private LayoutInflater mLayoutInflater;

    public OrderHistoryAdapter(Context context, List<Order_history> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.layout_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order_history orderHistory = mealTypeList.get(position);

        holder.agency.setText(orderHistory.getAgencyName());
        holder.item_name.setText(orderHistory.getItemName());
        if (String.valueOf(orderHistory.getQty()) == null) {
            String reqQty = "N/A";
        } else {
            String reqQty = String.valueOf(orderHistory.getQty());
            holder.req_qty.setText(reqQty);

        }

        if (orderHistory.getApproved_qty() == null) {
            holder.approved_qty.setText("N/A");
        } else {
            String approvedQty = String.valueOf(orderHistory.getApproved_qty());
            holder.approved_qty.setText(approvedQty);
        }

        if (orderHistory.getDeliveryQty() == null) {
            holder.delivered_qty.setText("N/A");
        } else {
            String deliveredQty = String.valueOf(orderHistory.getDeliveryQty());
            holder.delivered_qty.setText(deliveredQty);
        }
        }


    @Override
    public int getItemCount() {
        return mealTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView agency, item_name, req_qty, approved_qty,delivered_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            agency = itemView.findViewById(R.id.tvSno);
            item_name = itemView.findViewById(R.id.tvOid);
            req_qty = itemView.findViewById(R.id.tvDate);
            approved_qty = itemView.findViewById(R.id.tvQty);
            delivered_qty = itemView.findViewById(R.id.tvDelQty);
        }
    }
}

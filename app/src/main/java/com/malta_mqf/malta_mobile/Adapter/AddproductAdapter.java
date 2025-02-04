package com.malta_mqf.malta_mobile.Adapter;

import static com.malta_mqf.malta_mobile.AddQuantity.selectedproduct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.AddQuantity;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.RepeatAddQuantity;


import java.util.List;

public class AddproductAdapter extends RecyclerView.Adapter<AddproductAdapter.ViewHolder> {
    private Context mContext;
    private List<OutletsByIdResponse> mlist;

    public AddproductAdapter(Context context, List<OutletsByIdResponse> list) {
        this.mContext = context;
        this.mlist = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_additems, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int maxLength = 45;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);

        holder.descp.setFilters(filters);
        holder.descp.setEllipsize(TextUtils.TruncateAt.END);
        holder.descp.setText(mlist.get(position).getOutletName());
       // holder.info.setText(mlist.get(position));



        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddQuantity.class);
                intent.putExtra("outletId",mlist.get(position).getId());
                intent.putExtra("outletName",mlist.get(position).getOutletName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
                selectedproduct.clear();
                mContext.startActivity(intent);
            }
        });

        holder.repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RepeatAddQuantity.class);
                intent.putExtra("outletId",mlist.get(position).getId());
                intent.putExtra("outletName",mlist.get(position).getOutletName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public long getItemId(int position) {
//return super.getItemId(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
    public String removeItem(String customerName, int position) {
        if (position >= 0 && position < mlist.size()) {
            mlist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mlist.size());
        }
        return null;
    }

    public void restoreItem(int position, String customerName, String outletName) {
        if (isValidPosition(position)) {
            // Restore the item to the original position
         //   mlist.add(position, String.valueOf(new AbstractMap.SimpleEntry<>(mlist.get(position).getCustomerName()  , mlist.get(position).getName())));
           mlist.add(position, new OutletsByIdResponse(customerName, outletName));
            notifyItemInserted(position);
        } else {
            // Handle the case where the position is out of bounds
            // You might want to log a message or throw an exception
        }
    }

    private boolean isValidPosition(int position) {
        return position >= 0 && position <= mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descp;
        public ImageView info,repeat;


        public ViewHolder(View itemView) {
            super(itemView);
            descp = itemView.findViewById(R.id.outletname);
            info = itemView.findViewById(R.id.addItemImageView);
            repeat = itemView.findViewById(R.id.repeatItemImageView);
        }
    }


}


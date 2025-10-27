package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<StockBean> itemList;

    private List<StockBean> fullList;
    private StockAdapter.OnItemClickListener clickListener;

    public StockAdapter(List<StockBean> itemList) {
        this.itemList = new ArrayList<>(itemList); // Initialize with provided list
        this.fullList = new ArrayList<>(itemList);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, StockBean selectedItem);
    }

    public void setOnItemClickListener(StockAdapter.OnItemClickListener listener) {
        this.clickListener = listener;
    }
    public void updateData(List<StockBean> newList) {
        this.itemList.clear();
        this.itemList.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList); // Update the full list as well
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.stockinventoryheader, parent, false);
            return new StockAdapter.HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.stockinventoryitem, parent, false);
            return new StockAdapter.InventoryViewHolder(itemView, clickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            StockAdapter.InventoryViewHolder inventoryViewHolder = (StockAdapter.InventoryViewHolder) holder;
            // Adjust the index by -1 to account for the header
            StockBean item = itemList.get(position - 1);
            inventoryViewHolder.bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1; // +1 for the header
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Header view bindings
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views
        }
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, etQuantity, etQuantityapv;
        private EditText deliveryqty; // Corrected to EditText

        public InventoryViewHolder(@NonNull View itemView, final StockAdapter.OnItemClickListener listener) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemLabel);
            etQuantity = itemView.findViewById(R.id.tvQtyLabel);
           // Corrected initialization

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Check if position is valid and listener is set
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        // Adjust the index by -1 to account for the header when passing the item
                        listener.onItemClick(position - 1, itemList.get(position - 1));
                    }
                }
            });


        }


        public void bind(StockBean item) {
            tvItemName.setText(item.getProductName());
            // int qty = Integer.parseInt(item.getQuantity());
            etQuantity.setText(item.getQty()); // Ensure conversion to String
           // Assuming you have a getter for delivery quantity; adjust as needed
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        if (fullList == null) {
            fullList = new ArrayList<>();
        }

        if (text.isEmpty()) {
            // Restore full list if filter text is empty
            itemList.clear();
            if (fullList != null) {
                itemList.addAll(fullList); // Restore the full list
            }
        } else {
            // Filter fullList based on text
            List<StockBean> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (StockBean item : fullList) {
                if (item.getProductName().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }
}

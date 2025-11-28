package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Model.ProductBean;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class LoadInLoadOutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private final List<ProductBean> itemList;


    private List<ProductBean> mlist;
    private List<ProductBean> fullList;
    private OnItemClickListener clickListener;

    public LoadInLoadOutAdapter(List<ProductBean> itemList) {

        //   this.itemList = itemList;
        this.itemList = new ArrayList<>(itemList); // Initialize with provided list
        this.fullList = new ArrayList<>(itemList);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.item_inventory_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.item_product, parent, false);
            return new InventoryViewHolder(itemView, clickListener);
        }
    }

    public void updateData(List<ProductBean> newList) {
        this.itemList.clear();
        this.itemList.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList); // Update the full list as well
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            InventoryViewHolder inventoryViewHolder = (InventoryViewHolder) holder;
            // Adjust the index by -1 to account for the header
            ProductBean item = itemList.get(position - 1);
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
            List<ProductBean> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (ProductBean item : fullList) {
                if (item.getProductName().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ProductBean selectedItem);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Header view bindings
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views
        }
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView etQuantity;
        private final TextView etQuantityapv;
        private final TextView tvpurchaseprice;
        private final EditText deliveryqty; // Corrected to EditText

        public InventoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            etQuantity = itemView.findViewById(R.id.etQuantity);
            etQuantityapv = itemView.findViewById(R.id.etQuantityapv);
            deliveryqty = itemView.findViewById(R.id.etQuantitydelivery); // Corrected initialization
            tvpurchaseprice = itemView.findViewById(R.id.tvItemprice);
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

            deliveryqty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // Update the ProductBean instance with the latest input
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && getAdapterPosition() > 0) { // Check position is valid and not the header
                        itemList.get(getAdapterPosition() - 1).setDeliveryQty(editable.toString());
                    }
                }
            });
        }


        public void bind(ProductBean item) {
            tvItemName.setText(item.getProductName());
            // int qty = Integer.parseInt(item.getQuantity());
            etQuantity.setText(item.getQuantity()); // Ensure conversion to String
            etQuantityapv.setText(String.valueOf(item.getApprovedqty())); // Ensure conversion to String
            deliveryqty.setText(String.valueOf(item.getDeliveryQty()));// Assuming you have a getter for delivery quantity; adjust as needed
            tvpurchaseprice.setText(String.valueOf(item.getPurchase_price()));
        }

    }


}

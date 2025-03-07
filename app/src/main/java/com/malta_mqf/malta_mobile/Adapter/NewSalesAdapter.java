package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewSalesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<NewSaleBean> itemList;
    private List<NewSaleBean> fullList;
    private List<Boolean> errorStates;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "NewSalesPrefs";

    public interface OnDeliveryQuantityExceededListener {
        void onDeliveryQuantityExceeded(boolean isExceeded);
    }

    private OnDeliveryQuantityExceededListener deliveryQuantityExceededListener;

    public void setOnDeliveryQuantityExceededListener(OnDeliveryQuantityExceededListener listener) {
        this.deliveryQuantityExceededListener = listener;
    }

    private OnItemClickListener clickListener;

    public NewSalesAdapter(List<NewSaleBean> itemList, Context context) {
        this.itemList = new ArrayList<>(itemList); // Initialize with provided list
        this.fullList = new ArrayList<>(itemList);
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        errorStates = new ArrayList<>(Collections.nCopies(itemList.size(), false)); // Initialize error states with false

        // Load saved delivery quantities from SharedPreferences
        for (NewSaleBean item : this.itemList) {
            String savedDeliveryQty = sharedPreferences.getString("delivery_qty_" + item.getProductID(), null);
            if (savedDeliveryQty != null) {
                item.setDeliveryQty(savedDeliveryQty);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, NewSaleBean selectedItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void updateData(List<NewSaleBean> newList) {
        this.itemList.clear();
        this.itemList.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList); // Update the full list as well

        // Reset error states for new list
        errorStates = new ArrayList<>(Collections.nCopies(newList.size(), false));
        // Check for any existing errors
        for (int i = 0; i < newList.size(); i++) {
            NewSaleBean item = newList.get(i);
            String savedDeliveryQty = sharedPreferences.getString("delivery_qty_" + item.getProductID(), null);
            if (savedDeliveryQty != null) {
                try {
                    int deliveryQty = Integer.parseInt(savedDeliveryQty);
                    int vanStock = Integer.parseInt(item.getVanstock());
                    if (deliveryQty > vanStock) {
                        errorStates.set(i, true);
                    }
                } catch (NumberFormatException e) {
                    errorStates.set(i, true);
                }
            }
        }

        notifyDataSetChanged();
        notifyDeliveryQuantityExceeded();
    }
    private void notifyDeliveryQuantityExceeded() {
        if (deliveryQuantityExceededListener != null) {
            boolean isAnyError = errorStates.contains(true); // Check if any item has an error
            deliveryQuantityExceededListener.onDeliveryQuantityExceeded(isAnyError);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.new_sale_header, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.new_sale_items, parent, false);
            return new InventoryViewHolder(itemView, clickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            InventoryViewHolder inventoryViewHolder = (InventoryViewHolder) holder;
            NewSaleBean item = itemList.get(position - 1); // Adjust index for header
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
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, pricePerQTY, etQuantityapv, itemsStock;
        private EditText deliveryqty;

        public InventoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            etQuantityapv = itemView.findViewById(R.id.etQuantityapv);
            deliveryqty = itemView.findViewById(R.id.etQuantitydelivery);
            pricePerQTY = itemView.findViewById(R.id.pricePerQty);
            itemsStock = itemView.findViewById(R.id.etVanStock);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position - 1, itemList.get(position - 1)); // Adjust index for header
                }
            });

            deliveryqty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position > 0) {
                        NewSaleBean item = itemList.get(position - 1); // Adjust index for header
                        String deliveryQtyStr = editable.toString();
                        boolean isExceeded = false;

                        if (deliveryQtyStr.isEmpty()) {
                            deliveryqty.setError("Delivery quantity cannot be empty.");
                            isExceeded = true;
                        } else {
                            try {
                                int deliveryQty = Integer.parseInt(deliveryQtyStr);
                                int vanStock = Integer.parseInt(item.getVanstock());

                                if (vanStock == 0) {
                                    if (!deliveryQtyStr.equals("0")) { // Only set text if it's not already "0"
                                        deliveryqty.setText("0");
                                        //  deliveryqty.setError("Delivery quantity set to 0 due to insufficient van stock.");
                                    }
                                    //isExceeded = true;
                                } else if (deliveryQty > vanStock) {
                                    deliveryqty.setText(String.valueOf(vanStock));
                                } else {
                                    deliveryqty.setError(null);
                                    item.setDeliveryQty(deliveryQtyStr); // Update the item with the new delivery quantity
                                    // Store in SharedPreferences using productID
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("delivery_qty_" + item.getProductID(), deliveryQtyStr);
                                    editor.apply();
                                }
                            } catch (NumberFormatException e) {
                                deliveryqty.setError("Invalid quantity format.");
                                isExceeded = true;
                            }
                        }

                        // Update error state
                        errorStates.set(position - 1, isExceeded);
                        // Notify the listener
                        notifyDeliveryQuantityExceeded();
                    }
                }

            });
        }

        public void bind(NewSaleBean item) {
            tvItemName.setText(item.getProductName());
            itemsStock.setText(String.valueOf(item.getApprovedQty()));
            String savedDeliveryQty = sharedPreferences.getString("delivery_qty_" + item.getProductID(), null);
            int vanStock = Integer.parseInt(item.getVanstock());

            if (vanStock == 0 || (savedDeliveryQty != null && Integer.parseInt(savedDeliveryQty) > vanStock)) {
                deliveryqty.setText("0");
                deliveryqty.setError("Delivery quantity set to 0 due to insufficient van stock.");
                item.setDeliveryQty("0"); // Update the item with 0 delivery quantity
            } else {
                if (savedDeliveryQty != null) {
                    deliveryqty.setText(savedDeliveryQty);
                    item.setDeliveryQty(savedDeliveryQty); // Update the item with the saved value
                } else {
                    deliveryqty.setText(item.getApprovedQty()); // Ensure this is the current delivery quantity
                }
            }

            etQuantityapv.setText(item.getVanstock());
            pricePerQTY.setText(item.getSellingPrice());
        }


    }

    public List<String> getDeliveryQuantities() {
        List<String> deliveryQuantities = new ArrayList<>();
        for (NewSaleBean item : itemList) {
            deliveryQuantities.add(item.getDeliveryQty()); // Assuming getDeliveryQty() returns the saved quantity
        }
        return deliveryQuantities;
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
            List<NewSaleBean> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (NewSaleBean item : fullList) {
                if (item.getProductName().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(filteredList);
        }

        // Reset error states for filtered items
        errorStates = new ArrayList<>(Collections.nCopies(itemList.size(), false));
        notifyDataSetChanged();
    }

    public void addItems(List<NewSaleBean> newItems) {
        for (NewSaleBean item : newItems) {
            if (!itemList.contains(item)) {
                itemList.add(item);
                errorStates.add(false); // Add a default error state for the new item
            }
        }
        notifyDataSetChanged();
    }
}

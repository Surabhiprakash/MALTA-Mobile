package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.R;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnAddQtyAdapter extends RecyclerView.Adapter<ReturnAddQtyAdapter.ViewHolder> {
    private Context mContext;
    private QuantityChangeListener mListener;
    private List<Map.Entry<String, String>> mlist;
    private List<Map.Entry<String, String>> fullList; // Full list to restore after filter
    private Map<String, String> currentQuantities; // Map to store the current quantities

    private int selectedPosition = -1;
    private int totalitems = 0;

    public ReturnAddQtyAdapter(Context context, List<Map.Entry<String, String>> list) {
        this.mContext = context;
        this.mlist = new ArrayList<>(list); // Initialize with provided list
        this.fullList = new ArrayList<>(list); // Keep a copy of the full list
        this.currentQuantities = new HashMap<>(); // Initialize the current quantities map
        for (Map.Entry<String, String> entry : list) {
            currentQuantities.put(entry.getKey(), entry.getValue());
        }
    }

    public void setQuantityChangeListener(QuantityChangeListener listener) {
        this.mListener = listener;
    }

    public void updateData(List<Map.Entry<String, String>> newList) {
        this.mlist.clear();
        this.mlist.addAll(newList);
        this.fullList.clear();
        this.fullList.addAll(newList); // Update the full list as well
        this.currentQuantities.clear();
        for (Map.Entry<String, String> entry : newList) {
            currentQuantities.put(entry.getKey(), entry.getValue());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_addqty_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, String> entry = mlist.get(position);
        int maxLength = 45;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);

        holder.productname.setFilters(filters);
        holder.productname.setEllipsize(TextUtils.TruncateAt.END);
        holder.productname.setText(entry.getKey());
        holder.quantity.setText(currentQuantities.get(entry.getKey())); // Set quantity from the current quantities map

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.highlight_color)); // Define highlight_color in your colors.xml
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < mlist.size()) {
            String key = mlist.get(position).getKey();
            mlist.remove(position); // Remove item from mlist
            fullList.removeIf(entry -> entry.getKey().equals(key)); // Remove item from fullList
            currentQuantities.remove(key); // Remove item from current quantities map

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mlist.size());

            totalitems--;  // Decrement the total items count
            updateTotalQuantity();  // Update the total quantity after removing an item
        }
    }

    public void restoreItem(int position, String deletedItemName, String deletedItemQuantity) {
        if (position >= 0 && position <= mlist.size()) {
            Map.Entry<String, String> newItem = new AbstractMap.SimpleEntry<>(deletedItemName, deletedItemQuantity);
            mlist.add(position, newItem); // Add item to mlist
            fullList.add(newItem); // Add item to fullList
            currentQuantities.put(deletedItemName, deletedItemQuantity); // Add item to current quantities map
            notifyItemInserted(position);

            totalitems++;  // Increment the total items count
            updateTotalQuantity();
        }
    }

    public void onQuantityChange(int itemIndex, int newQuantity) {
        if (itemIndex != RecyclerView.NO_POSITION && itemIndex < mlist.size()) {
            String key = mlist.get(itemIndex).getKey();
            currentQuantities.put(key, String.valueOf(newQuantity)); // Update the current quantities map
            mlist.get(itemIndex).setValue(String.valueOf(newQuantity));
            fullList.stream()
                    .filter(entry -> entry.getKey().equals(key))
                    .forEach(entry -> entry.setValue(String.valueOf(newQuantity))); // Update the fullList
            updateTotalQuantity();
        }
    }

    public void updateTotalQuantity() {
        int totalQuantity = 0;
        totalitems = 0;

        // Calculate total quantity and total items from current quantities map
        for (String value : currentQuantities.values()) {
            try {
                int quantity = Integer.parseInt(value);
                totalQuantity += quantity;
                if (quantity > 0) {
                    totalitems++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Notify listeners
        if (mListener != null) {
            mListener.onTotalQuantityChanged(totalQuantity);
            mListener.onTotalItemChanged(totalitems);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        if (text.isEmpty()) {
            // Restore full list if filter text is empty
            mlist.clear();
            mlist.addAll(fullList); // Restore the full list
        } else {
            // Filter fullList based on text
            List<Map.Entry<String, String>> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (Map.Entry<String, String> item : fullList) {
                if (item.getKey().toLowerCase().contains(text) || item.getValue().toLowerCase().contains(text)) {
                    filteredList.add(item);
                }
            }
            mlist.clear();
            mlist.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    public interface QuantityChangeListener {
        void onTotalQuantityChanged(int totalQuantity);

        void onTotalItemChanged(int totalItems);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productname;
        public ImageButton increasebutton, decreasebutton;
        public EditText quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            productname = itemView.findViewById(R.id.productName);
            increasebutton = itemView.findViewById(R.id.increaseButton);
            decreasebutton = itemView.findViewById(R.id.decreaseButton);
            quantity = itemView.findViewById(R.id.quantityEditText);

            quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String qty = s.toString();
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < mlist.size()) {
                        String key = mlist.get(adapterPosition).getKey();
                        currentQuantities.put(key, qty); // Update the current quantities map
                        mlist.get(adapterPosition).setValue(qty);
                        fullList.stream()
                                .filter(entry -> entry.getKey().equals(key))
                                .forEach(entry -> entry.setValue(qty)); // Update the fullList
                        updateTotalQuantity(); // Update total quantity on text change
                    }
                }
            });

            increasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qty = quantity.getText().toString().trim(); // Trim to avoid spaces
                    if (qty.isEmpty()) {
                        qty = "0"; // Default to 0 if the EditText is empty
                    }

                    int newQty = Integer.parseInt(qty) + 1;
                    quantity.setText(String.valueOf(newQty));
                    onQuantityChange(getAdapterPosition(), newQty);
                }
            });
            decreasebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qty = quantity.getText().toString().trim(); // Trim to avoid spaces
                    if (qty.isEmpty()) {
                        qty = "0"; // Default to 0 if the EditText is empty
                    }

                    int newQty = Integer.parseInt(qty) - 1;
                    // Ensure quantity does not go below zero
                    if (newQty < 0) {
                        newQty = 0;
                    }

                    quantity.setText(String.valueOf(newQty));
                    onQuantityChange(getAdapterPosition(), newQty);
                }
            });
        }
    }

}

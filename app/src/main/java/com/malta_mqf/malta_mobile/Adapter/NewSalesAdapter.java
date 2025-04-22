package com.malta_mqf.malta_mobile.Adapter;

import static com.malta_mqf.malta_mobile.NewSaleActivity.Total_Amount_Payable;
import static com.malta_mqf.malta_mobile.NewSaleActivity.Total_Net_amt;
import static com.malta_mqf.malta_mobile.NewSaleActivity.Total_Qty;
import static com.malta_mqf.malta_mobile.NewSaleActivity.Total_vat_amt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewSalesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<NewSaleBean> itemList;
    private List<NewSaleBean> fullList;
    private List<Boolean> errorStates;
    private boolean isEditingQuantity = false;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "NewSalesPrefs";

    public interface OnTotalCalculationListener {
        void onTotalsUpdated(int totalQty, BigDecimal totalNet, BigDecimal totalVat, BigDecimal totalGross);
    }

    private OnTotalCalculationListener totalCalculationListener;

    public void setOnTotalCalculationListener(OnTotalCalculationListener listener) {
        this.totalCalculationListener = listener;
    }



    public interface OnQuantityChangeListener {
        void onQuantityChanged(int position, String newQuantity);
    }

    private OnQuantityChangeListener onQuantityChangeListener;

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.onQuantityChangeListener = listener;
    }

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
        // ✅ Call `calculateTotals()` here so it calculates everything on screen load
        calculateTotals();
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
        private EditText quantityEditText;
        private ImageButton decreaseButton,etincreaseButton;

        public InventoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            etQuantityapv = itemView.findViewById(R.id.etQuantityapv);
//            deliveryqty = itemView.findViewById(R.id.etQuantitydelivery);
            decreaseButton=itemView.findViewById(R.id.decreaseButton);
            quantityEditText=itemView.findViewById(R.id.quantityEditText);
            etincreaseButton=itemView.findViewById(R.id.etincreaseButton);
            pricePerQTY = itemView.findViewById(R.id.pricePerQty);
            itemsStock = itemView.findViewById(R.id.etVanStock);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position - 1, itemList.get(position - 1)); // Adjust index for header
                }
            });

            // **Decrease Button Click Listener**
            decreaseButton.setOnClickListener(v -> {
                updateQuantity(-1); // Decrease by 1
            });

            // **Increase Button Click Listener**
            etincreaseButton.setOnClickListener(v -> {
                updateQuantity(1); // Increase by 1
            });

            quantityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                //
                @Override
                public void afterTextChanged(Editable editable) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION || position <= 0) return;

                    NewSaleBean item = itemList.get(position - 1);
                    String deliveryQtyStr = editable.toString().trim();

                    if (deliveryQtyStr.isEmpty()) {
                        quantityEditText.setError("Delivery quantity cannot be empty.");
                        return;
                    }

                    try {
                        int deliveryQty = Integer.parseInt(deliveryQtyStr);
                        int vanStock = Integer.parseInt(item.getVanstock());

                        if (deliveryQty > vanStock) {
                            quantityEditText.setText(String.valueOf(vanStock));
                            item.setDeliveryQty(String.valueOf(vanStock));
                        } else {
                            quantityEditText.setError(null);
                            item.setDeliveryQty(deliveryQtyStr);
                        }

                        // ✅ Also update `fullList` for accurate totals
                        for (NewSaleBean fullItem : fullList) {
                            if (fullItem.getProductID().equals(item.getProductID())) {
                                fullItem.setDeliveryQty(item.getDeliveryQty());
                                break;
                            }
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("delivery_qty_" + item.getProductID(), item.getDeliveryQty());
                        editor.apply();

                        if (onQuantityChangeListener != null) {
                            onQuantityChangeListener.onQuantityChanged(position - 1, item.getDeliveryQty());
                        }

                        // ✅ Prevent infinite loops by setting the tag before updating text
                        quantityEditText.setTag(String.valueOf(deliveryQty));

                        // ✅ Call `calculateTotals();` to update total amounts
                        calculateTotals();

                    } catch (NumberFormatException e) {
                        quantityEditText.setError("Invalid quantity format.");
                    }
                }
            });

        }
        private void updateQuantity(int change) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && position > 0) {
                NewSaleBean item = itemList.get(position - 1);
                String currentQtyStr = quantityEditText.getText().toString().trim();

                int currentQty = 0;
                if (!currentQtyStr.isEmpty()) {
                    try {
                        currentQty = Integer.parseInt(currentQtyStr);
                    } catch (NumberFormatException e) {
                        quantityEditText.setError("Invalid quantity format.");
                        return;
                    }
                }

                int vanStock = Integer.parseInt(item.getVanstock());
                int newQty = currentQty + change;

                // **Ensure quantity does not go below 0 or above van stock**
                if (newQty < 0) {
                    newQty = 0;
                } else if (newQty > vanStock) {
                    newQty = vanStock;
                }

                // ✅ **Update the quantity in the data list**
                item.setDeliveryQty(String.valueOf(newQty));

                // ✅ **Store updated quantity in SharedPreferences**
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("delivery_qty_" + item.getProductID(), String.valueOf(newQty));
                editor.apply();

                // ✅ **Notify quantity change listener**
                if (onQuantityChangeListener != null) {
                    onQuantityChangeListener.onQuantityChanged(position - 1, String.valueOf(newQty));
                }

                // ✅ **Trigger Total Calculation for all items**
                calculateTotals();

                // 🔥 **Update UI for the current view**
                quantityEditText.setText(String.valueOf(newQty));
                quantityEditText.setTag(String.valueOf(newQty));
            }
        }

        public void bind(NewSaleBean item) {
            tvItemName.setText(item.getProductName());
            itemsStock.setText(String.valueOf(item.getApprovedQty()));

            String savedDeliveryQty = sharedPreferences.getString("delivery_qty_" + item.getProductID(), null);
            int vanStock = Integer.parseInt(item.getVanstock());

            // 🛠 Only set EditText if it's empty or different from the stored value
            if (quantityEditText.getTag() == null || !quantityEditText.getTag().equals(savedDeliveryQty)) {
                if (vanStock == 0) {
                    quantityEditText.setText("0");
                    quantityEditText.setError("Delivery quantity set to 0 due to insufficient van stock.");
                    item.setDeliveryQty("0");
                } else if (savedDeliveryQty != null) {
                    quantityEditText.setText(savedDeliveryQty);
                    item.setDeliveryQty(savedDeliveryQty);
                } else {
                    quantityEditText.setText(item.getApprovedQty());
                }
                quantityEditText.setTag(savedDeliveryQty);  // ✅ Set tag to prevent redundant updates
            }

            etQuantityapv.setText(item.getVanstock());
            pricePerQTY.setText(item.getSellingPrice());
        }



    }

    public void calculateTotals() {
        int totalQty = 0;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal totalGross = BigDecimal.ZERO;

        for (NewSaleBean product : fullList) {  // ✅ This goes through all items, even the scrolled ones!
            try {
                int quantity = Integer.parseInt(product.getDeliveryQty());
                totalQty += quantity;

                BigDecimal price = new BigDecimal(product.getSellingPrice());
                BigDecimal itemNet = price.multiply(BigDecimal.valueOf(quantity));
                BigDecimal itemVat = itemNet.multiply(BigDecimal.valueOf(0.05));
                BigDecimal itemGross = itemNet.add(itemVat);

                totalNet = totalNet.add(itemNet);
                totalVat = totalVat.add(itemVat);
                totalGross = totalGross.add(itemGross);

            } catch (NumberFormatException e) {
                Log.e("CalculationError", "Invalid quantity for product: " + product.getProductName(), e);
            }
        }

        // ✅ Update TextViews
        Total_Qty.setText("Total Quantity: " + totalQty);
        Total_Net_amt.setText("Net: " + totalNet.setScale(2, RoundingMode.HALF_UP));
        Total_vat_amt.setText("VAT: " + totalVat.setScale(2, RoundingMode.HALF_UP));
        Total_Amount_Payable.setText("Total Payable: " + totalGross.setScale(2, RoundingMode.HALF_UP));

        // Notify listener if needed
        if (totalCalculationListener != null) {
            totalCalculationListener.onTotalsUpdated(totalQty, totalNet, totalVat, totalGross);
        }
    }


    public List<String> getDeliveryQuantities() {
        List<String> deliveryQuantities = new ArrayList<>();
        for (NewSaleBean item : itemList) {
            deliveryQuantities.add(item.getDeliveryQty()); // Assuming getDeliveryQty() returns the saved quantity
        }
        return deliveryQuantities;
    }
    public List<NewSaleBean> getItemList() {
        return itemList; // Your existing main product list
    }


    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        if (fullList == null) fullList = new ArrayList<>();
        if (text.isEmpty()) {
            itemList.clear();
            itemList.addAll(fullList);
        } else {
            List<NewSaleBean> filteredList = new ArrayList<>();
            text = text.toLowerCase().trim();

            for (NewSaleBean item : fullList) {
                if (item.getProductName().toLowerCase().contains(text)) {
                    // ✅ Preserve updated deliveryQty values
                    for (NewSaleBean originalItem : fullList) {
                        if (originalItem.getProductID().equals(item.getProductID())) {
                            item.setDeliveryQty(originalItem.getDeliveryQty());
                            break;
                        }
                    }
                    filteredList.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }




    public void addItems(List<NewSaleBean> newItems) {
        for (NewSaleBean item : newItems) {
            if (!fullList.contains(item)) {
                fullList.add(item); // ✅ Add to full list (important for calculations)
            }
            if (!itemList.contains(item)) {
                itemList.add(item);
                errorStates.add(false);
            }
        }
        notifyDataSetChanged();
        calculateTotals(); // ✅ Ensure totals update immediately
    }
}



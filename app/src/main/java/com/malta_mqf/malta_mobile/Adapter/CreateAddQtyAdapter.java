package com.malta_mqf.malta_mobile.Adapter;

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

import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class CreateAddQtyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final String PREFS_NAME = "createaddqtypref";
    private List<StockBean> itemList;
    private SharedPreferences sharedPreferences;
    private OnDeliveryQuantityExceededListener deliveryQuantityExceededListener;
    private OnItemClickListener clickListener;

    public CreateAddQtyAdapter(Context context, List<StockBean> itemList) {
        this.itemList = itemList;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setOnDeliveryQuantityExceededListener(OnDeliveryQuantityExceededListener listener) {
        this.deliveryQuantityExceededListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View headerView = inflater.inflate(R.layout.stockinventoryneworderheader, parent, false);
            return new HeaderViewHolder(headerView);
        } else {
            View itemView = inflater.inflate(R.layout.list_item_stockinventory_neworder, parent, false);
            return new InventoryViewHolder(itemView, clickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            InventoryViewHolder inventoryViewHolder = (InventoryViewHolder) holder;
            // Adjust the index by -1 to account for the header
            StockBean item = itemList.get(position - 1);
            inventoryViewHolder.bind(item, position - 1);
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

    public interface OnDeliveryQuantityExceededListener {
        void onDeliveryQuantityExceeded(boolean isExceeded);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, StockBean selectedItem);
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Header view bindings
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views
        }
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemName, vanStock;
        private EditText deliveryqty;

        public InventoryViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItem);
            deliveryqty = itemView.findViewById(R.id.etQty);
            vanStock = itemView.findViewById(R.id.tvVanStock);
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
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position > 0) {
                        StockBean item = itemList.get(position - 1);
                        String deliveryQtyStr = editable.toString();
                        boolean isExceeded = false;

                        if (!deliveryQtyStr.isEmpty()) {
                            int deliveryQty = Integer.parseInt(deliveryQtyStr);
                            int vanStockQty = Integer.parseInt(item.getQty());
                            if (deliveryQty > vanStockQty) {
                                deliveryqty.setError("Delivery quantity cannot exceed van stock.");
                                isExceeded = true;
                            } else {
                                deliveryqty.setError(null);
                                item.setDelQty(String.valueOf(deliveryQty)); // Update delQty
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("delivery_qty" + (position - 1), deliveryQtyStr);
                                editor.apply();
                            }
                        } else {
                            item.setDelQty("0");
                        }

                        notifyDeliveryQuantityExceeded(isExceeded);
                    }
                }
            });
        }

        private void notifyDeliveryQuantityExceeded(boolean isExceeded) {
            if (deliveryQuantityExceededListener != null) {
                deliveryQuantityExceededListener.onDeliveryQuantityExceeded(isExceeded);
            }
        }

        public void bind(StockBean item, int position) {
            tvItemName.setText(item.getProductName());
            vanStock.setText(String.valueOf(item.getQty()));
            String savedDeliveryQty = sharedPreferences.getString("delivery_qty" + position, null);

            if (savedDeliveryQty != null) {
                deliveryqty.setText(savedDeliveryQty);
                item.setDelQty(savedDeliveryQty);
            } else {
                deliveryqty.setText("0");
                item.setDelQty("0");
            }
        }
    }
}

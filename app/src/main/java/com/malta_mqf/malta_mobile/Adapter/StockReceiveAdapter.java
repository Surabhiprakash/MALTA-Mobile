package com.malta_mqf.malta_mobile.Adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Model.VanStockUnloadModel;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class StockReceiveAdapter extends RecyclerView.Adapter<StockReceiveAdapter.ViewHolder> {
    private Context context;
    private List<VanStockUnloadModel> vanStockList;
    private List<VanStockUnloadModel> fullList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public StockReceiveAdapter(Context context, List<VanStockUnloadModel> vanStockList) {
        this.context = context;
        this.vanStockList = vanStockList;
        this.fullList = new ArrayList<>(vanStockList);
        sharedPreferences = context.getSharedPreferences("UnloadStockPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_van_stock_recevice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VanStockUnloadModel item = vanStockList.get(position);

        holder.tvProductName.setText(item.getProductName());
        holder.tvAvailableQty.setText(String.valueOf(item.getAvailableQty()));
        if (holder.textWatcher != null) {
            holder.etUnloadQty.removeTextChangedListener(holder.textWatcher);
        }
//        holder.etUnloadQty.setText(item.getUnloadQty());


        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                String updatedQty = s.toString().trim();
                item.setUnloadQty(updatedQty.isEmpty() ? "0" : updatedQty);

                for (VanStockUnloadModel originalItem : fullList) {
                    if (originalItem.getProductName().equals(item.getProductName())) {
                        originalItem.setUnloadQty(item.getUnloadQty());
                        break;
                    }
                }

                System.out.println("Updated: " + item.getProductName() + " -> " + item.getUnloadQty());
            }
        };
//        holder.etUnloadQty.addTextChangedListener(holder.textWatcher);





    }

    @Override
    public int getItemCount() {
        return vanStockList.size();
    }

    public void updateList(List<VanStockUnloadModel> newList) {
        this.vanStockList = newList;
        notifyDataSetChanged();
    }

    public void filter(String query) {
        List<VanStockUnloadModel> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (VanStockUnloadModel item : fullList) {
                if (item.getProductName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        // Preserve unloadQty values from current list
        for (VanStockUnloadModel filteredItem : filteredList) {
            for (VanStockUnloadModel originalItem : vanStockList) {
                if (filteredItem.getProductName().equals(originalItem.getProductName())) {
                    filteredItem.setUnloadQty(originalItem.getUnloadQty()); // Preserve quantity changes
                    break;
                }
            }
        }

        vanStockList.clear();
        vanStockList.addAll(filteredList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvAvailableQty;
        EditText etUnloadQty;

        TextWatcher textWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvAvailableQty = itemView.findViewById(R.id.tvAvailableQty);
            etUnloadQty = itemView.findViewById(R.id.etUnloadQty);

        }
    }

    public List<VanStockUnloadModel> getUnloadList() {
        List<VanStockUnloadModel> nList = new ArrayList<>();

        for (VanStockUnloadModel item : fullList) {
            String qtyStr = item.getUnloadQty();

            // Log quantity and reason for debugging
            System.out.println("qtyStr: " + qtyStr);
            System.out.println("reason: " + item.getUnloadReason());

            // Ensure qtyStr is not null or empty before parsing
            if (qtyStr != null && qtyStr.matches("\\d+")) {  // Ensures only numeric values
                int unloadQty = Integer.parseInt(qtyStr);
                if (unloadQty > 0) {
                    nList.add(item);
                }
            }
        }
        return nList;
    }


}


package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EndsWithArrayAdapter extends ArrayAdapter<String> {
    private List<String> originalList; // The original list of vehicle numbers
    private List<String> filteredList; // The filtered list to show as suggestions
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            filteredList.clear();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : originalList) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List<String>) results.values);
            notifyDataSetChanged();
        }
    };

    public EndsWithArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.originalList = new ArrayList<>(objects);
        this.filteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }
}


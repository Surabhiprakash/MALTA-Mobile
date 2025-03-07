package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class EndsWithAgencyArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> originalList;
    private final List<String> filteredList;
    private final Object lock = new Object();

    public EndsWithAgencyArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.originalList = new ArrayList<>(objects);
        this.filteredList = new ArrayList<>(objects);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public String getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    synchronized (lock) {
                        results.values = new ArrayList<>(originalList);
                        results.count = originalList.size();
                    }
                } else {
                    List<String> filtered = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (String item : originalList) {
                        if (item.toLowerCase().contains(filterPattern)) {
                            filtered.add(item);
                        }
                    }

                    results.values = filtered;
                    results.count = filtered.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<String>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_text, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.list_textView_value);
        textView.setText(getItem(position));

        return convertView;
    }
}


package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.malta_mqf.malta_mobile.Model.OutletBean;
import com.malta_mqf.malta_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class OnlineEndsWithArrayAdapter extends ArrayAdapter<OutletBean> {
    private List<OutletBean> originalList; // The original list of outlet beans
    private LayoutInflater inflater;
    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<OutletBean> filteredOutlets = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredOutlets.addAll(originalList); // No filtering, add all outlets
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (OutletBean item : originalList) {
                    String outletName = item.getOutletnames();
                    if (outletName != null && outletName.toLowerCase().contains(filterPattern)) {
                        filteredOutlets.add(item);
                    }
                }
            }

            results.values = filteredOutlets;
            results.count = filteredOutlets.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List<OutletBean>) results.values);
            notifyDataSetChanged();
        }
    };

    public OnlineEndsWithArrayAdapter(Context context, List<OutletBean> objects) {
        super(context, 0, objects);
        this.originalList = new ArrayList<>(objects);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_text, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = convertView.findViewById(R.id.list_textView_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(getItem(position).getOutletnames());
        return convertView;
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent); // Use the same view for drop-down
    }

    private static class ViewHolder {
        TextView textView;
    }
}

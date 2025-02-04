package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> values;

    public CustomArrayAdapter(Context context, List<String> values) {
        super(context, R.layout.list_item_text, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_text, parent, false);

        TextView textView = rowView.findViewById(R.id.list_textView_value);
        textView.setText(values.get(position));

        return rowView;
    }
}


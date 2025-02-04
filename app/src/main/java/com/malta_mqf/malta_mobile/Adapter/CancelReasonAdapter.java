package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class CancelReasonAdapter extends ArrayAdapter<String> {

    public CancelReasonAdapter(Context context, List<String> reasons) {
        super(context, 0, reasons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_cancel_reason, parent, false);
        }

        String reason = getItem(position);
        TextView textView = convertView.findViewById(R.id.text_cancel_reason_title);
        textView.setText(reason);

        return convertView;
    }
}


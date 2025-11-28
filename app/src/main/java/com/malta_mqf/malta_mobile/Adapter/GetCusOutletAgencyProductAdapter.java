package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class GetCusOutletAgencyProductAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<String> mealTypeList;
    private final LayoutInflater mLayoutInflater;

    public GetCusOutletAgencyProductAdapter(Context context, List<String> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_text, null);
            holder = new HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }
        holder.txtName = convertView.findViewById(R.id.list_textView_value);

        holder.txtName.setText(mealTypeList.get(position));

        return convertView;
    }

    class HViewHolder {
        TextView txtName;
    }
}

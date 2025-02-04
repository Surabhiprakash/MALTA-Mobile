package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.OutletBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class onlineoutletrelatedAdapter extends BaseAdapter {
    private Context mContext;
    private List<OutletBean> mealTypeList;
    private LayoutInflater mLayoutInflater;

    public onlineoutletrelatedAdapter(Context context, List<OutletBean> mealTypeList) {
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
        final onlineoutletrelatedAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_text, null);
            holder = new onlineoutletrelatedAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (onlineoutletrelatedAdapter.HViewHolder) convertView.getTag();
        }
        holder.txtName = convertView.findViewById(R.id.list_textView_value);

        holder.txtName.setText(mealTypeList.get(position).getOutletnames());

        return convertView;
    }

    class HViewHolder {
        TextView txtName;
    }
}

package com.malta_mqf.malta_mobile.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.malta_mqf.malta_mobile.Model.TodaysOrderBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class TodaysOrderAdapter extends BaseAdapter {
    private Context mContext;
    private List<TodaysOrderBean> mealTypeList;
    private LayoutInflater mLayoutInflater;

    public TodaysOrderAdapter(Context context, List<TodaysOrderBean> mealTypeList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mealTypeList = mealTypeList;
    }

    @Override
    public int getCount() {
        return mealTypeList.size();
    }

    @Override
    public TodaysOrderBean getItem(int position) {
        return mealTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TodaysOrderAdapter.HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.route_outletname, null);
            holder = new TodaysOrderAdapter.HViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (TodaysOrderAdapter.HViewHolder) convertView.getTag();
        }
        holder.orderstatusimg = convertView.findViewById(R.id.right_arrow);
        holder.txtOutletName = convertView.findViewById(R.id.routeName);
        if (mealTypeList.get(position).getOutletName() == null) {
            holder.txtOutletName.setText(mealTypeList.get(position).getOrderid());
        } else {
            holder.txtOutletName.setText(mealTypeList.get(position).getOrderid());
        }
        holder.orderstatusimg = convertView.findViewById(R.id.right_arrow);


        try {
            if (mealTypeList.get(position).getOrderStatus().equals("DELIVERED")) {
                Drawable checkDrawable = ContextCompat.getDrawable(mContext, R.drawable.check);
                holder.orderstatusimg.setImageDrawable(checkDrawable);
            } else {

                Drawable checkDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.right);
                holder.orderstatusimg.setImageDrawable(checkDrawable1);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        holder.txtOutletLocation = convertView.findViewById(R.id.routeDescription);
        holder.txtOutletLocation.setText(mealTypeList.get(position).getOutletAddress());
        return convertView;
    }

    class HViewHolder {
        TextView txtOutletName, txtOutletLocation;
        ImageView orderstatusimg;
    }
}

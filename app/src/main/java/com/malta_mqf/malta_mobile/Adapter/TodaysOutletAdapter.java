package com.malta_mqf.malta_mobile.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.Model.TodaysOrderBean;
import com.malta_mqf.malta_mobile.R;

import java.util.List;

public class TodaysOutletAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<TodaysOrderBean> mealTypeList;
    private final LayoutInflater mLayoutInflater;

    public TodaysOutletAdapter(Context context, List<TodaysOrderBean> mealTypeList) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.route_outletname, parent, false);
            holder = new HViewHolder();
            holder.txtOutletName = convertView.findViewById(R.id.routeName);
            holder.orderstatusimg = convertView.findViewById(R.id.right_arrow);
            holder.txtOutletLocation = convertView.findViewById(R.id.routeDescription);
            convertView.setTag(holder);
        } else {
            holder = (HViewHolder) convertView.getTag();
        }

        TodaysOrderBean todaysOrderBean = getItem(position);

        if (todaysOrderBean.getOutletName() == null) {
            holder.txtOutletName.setText(todaysOrderBean.getOrderid());
        } else {
            holder.txtOutletName.setText(todaysOrderBean.getOutletName() + " " + "(" + todaysOrderBean.getOutletCode() + ")");
        }

        holder.txtOutletLocation.setText(todaysOrderBean.getOutletAddress());

        // Update ImageView based on delivered status
        try {
            if (todaysOrderBean.isDelivered()) {
                holder.orderstatusimg.setImageResource(R.drawable.check); // Your tick image resource
            } else {
                holder.orderstatusimg.setImageResource(R.drawable.right); // Your default or pending image resource
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class HViewHolder {
        TextView txtOutletName, txtOutletLocation;
        ImageView orderstatusimg;
    }
}

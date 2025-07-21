package com.malta_mqf.malta_mobile.Adapter;

import static com.malta_mqf.malta_mobile.ReturnAddQtyActivity.selectedproduct;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;
import com.malta_mqf.malta_mobile.R;

import com.malta_mqf.malta_mobile.ReturnAddQtyActivity;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ReturnAddItemsAdapter  extends RecyclerView.Adapter<ReturnAddItemsAdapter.ViewHolder> {
    private Context mContext;
    private List<OutletsByIdResponse> mlist;
    ReturnDB returnDB;
    UserDetailsDb userDetailsDb;
    public static String  lastreturninvoicenumber,route,credID;
    public ReturnAddItemsAdapter(Context context, List<OutletsByIdResponse> list) {
        this.mContext = context;
        this.mlist = list;
        returnDB=new ReturnDB(context);
        userDetailsDb=new UserDetailsDb(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_additems, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int maxLength = 45;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);

        holder.descp.setFilters(filters);
        holder.descp.setEllipsize(TextUtils.TruncateAt.END);
        holder.descp.setText(mlist.get(position).getOutletName());
        // holder.info.setText(mlist.get(position));


        holder.info.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                Cursor cursor2 = userDetailsDb.readAllData();
                while (cursor2.moveToNext()) {
                    route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
                    lastreturninvoicenumber=returnDB.getLastInvoiceNumber();

                    if (lastreturninvoicenumber == null || lastreturninvoicenumber.isEmpty() || lastreturninvoicenumber.length()>15) {
                        lastreturninvoicenumber=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.RETURN_INVOICE_NUMBER_UPDATING));

                    }System.out.println("last return invoice: "+lastreturninvoicenumber);
                    CustomerLogger.i("last cred id is :",lastreturninvoicenumber);
                }
//                String routeName = String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 2));
                String routeName = String.valueOf(route.charAt(0)) + route.substring(route.length() - 2);
                credID = routeName + "R" + getCurrentDate() + generateNextInvoiceNumber(lastreturninvoicenumber);
                System.out.println("CRED number: " + credID);
                CustomerLogger.i("new cred id is generated",credID);
                cursor2.close();

                Intent intent = new Intent(mContext, ReturnAddQtyActivity.class);
                intent.putExtra("outletId", mlist.get(position).getId());
                intent.putExtra("outletName", mlist.get(position).getOutletName());
                intent.putExtra("credID",credID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
                selectedproduct.clear();
                mContext.startActivity(intent);
            }
        });

        holder.repeat.setVisibility(View.GONE);


    }


    @Override
    public long getItemId(int position) {
//return super.getItemId(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) >= 22)) {
            // Move to the next day and set time to 12:00 AM
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        // Format the date and time as "dd/MMM/yyyy HH:mm:ss"
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");
        return formatter.format(calendar.getTime());
    }

    public String generateNextInvoiceNumber(String lastvoiceInvoicenumber) {
        // Assuming the lastInvoice is in the format "F1R031120240000"
        String numericPart = lastvoiceInvoicenumber.substring(lastvoiceInvoicenumber.length() - 4);
        String prefix = lastvoiceInvoicenumber.substring(0, lastvoiceInvoicenumber.length() - 4);

        // Increment the numeric part
        int nextNumber = Integer.parseInt(numericPart) + 1;
        System.out.println("nextnumberr is :"+nextNumber);


        // Format the number to keep leading zeros
        String newInvoiceNumber = String.format("%04d", nextNumber);

        System.out.println("new invoioce no is :"+newInvoiceNumber);
        CustomerLogger.i("new invoioce no is : ",newInvoiceNumber);

        return newInvoiceNumber;
    }
    public String removeItem(String customerName, int position) {
        if (position >= 0 && position < mlist.size()) {
            mlist.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mlist.size());
        }
        return null;
    }

    public void restoreItem(int position, String customerName, String outletName) {
        if (isValidPosition(position)) {
            // Restore the item to the original position
            //   mlist.add(position, String.valueOf(new AbstractMap.SimpleEntry<>(mlist.get(position).getCustomerName()  , mlist.get(position).getName())));
            mlist.add(position, new OutletsByIdResponse(customerName, outletName));
            notifyItemInserted(position);
        } else {
            // Handle the case where the position is out of bounds
            // You might want to log a message or throw an exception
        }
    }

    private boolean isValidPosition(int position) {
        return position >= 0 && position <= mlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView descp;
        public ImageView info, repeat;


        public ViewHolder(View itemView) {
            super(itemView);
            descp = itemView.findViewById(R.id.outletname);
            info = itemView.findViewById(R.id.addItemImageView);
            repeat = itemView.findViewById(R.id.repeatItemImageView);
        }
    }
}
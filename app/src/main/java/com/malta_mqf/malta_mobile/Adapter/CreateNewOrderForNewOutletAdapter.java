package com.malta_mqf.malta_mobile.Adapter;

import static com.malta_mqf.malta_mobile.CreateNewOrderForNewOutlet.customercode;
import static com.malta_mqf.malta_mobile.CreateNewOrderForNewOutlet.outletID;

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

import com.malta_mqf.malta_mobile.CreateNewOrderForOutletAddQty;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CreateNewOrderForNewOutletAdapter extends RecyclerView.Adapter<CreateNewOrderForNewOutletAdapter.ViewHolder> {
    private Context mContext;
    private List<OutletsByIdResponse> mlist;
    public static String newOrderId,NewOrderinvoiceNumber,route,lastinvoicenumber;
    UserDetailsDb userDetailsDb;
    SubmitOrderDB submitOrderDB;


    public CreateNewOrderForNewOutletAdapter(Context context, List<OutletsByIdResponse> list) {
        submitOrderDB=new SubmitOrderDB(context);
        userDetailsDb=new UserDetailsDb(context);
        this.mContext = context;
        this.mlist = list;
    }

    @NonNull
    @Override
    public CreateNewOrderForNewOutletAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_outlets_for_create_order, parent, false);
        return new CreateNewOrderForNewOutletAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateNewOrderForNewOutletAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
              //  newOrderId= processCustomerCode(mlist.get(position).getCustomerName().toUpperCase())+mlist.get(position).getId()+String.valueOf(generateorder())+"-M-EX";
                Cursor cursor2 = userDetailsDb.readAllData();
                while (cursor2.moveToNext()) {
                    route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
                    lastinvoicenumber=submitOrderDB.getLastInvoiceNumber();
                    if (lastinvoicenumber == null || lastinvoicenumber.isEmpty() || lastinvoicenumber.length()> 15){
                        lastinvoicenumber=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.INVOICE_NUMBER_UPDATING));

                    }
                }
//                String routeName=String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 2));
                String routeName = String.valueOf(route.charAt(0)) + route.substring(route.length() - 2);
                NewOrderinvoiceNumber = routeName+ "S" + getCurrentDate() + generateNextInvoiceNumber(lastinvoicenumber) ;
                String processedCustomerCode = processCustomerCode(customercode);
                String newOrderId= processCustomerCode(customercode)+outletID+String.valueOf(generateorder()) + "-M-EX";
                System.out.println("new invoice number: "+NewOrderinvoiceNumber);
                Intent intent = new Intent(mContext, CreateNewOrderForOutletAddQty.class);
                intent.putExtra("outletId",mlist.get(position).getId());
                intent.putExtra("outletName",mlist.get(position).getOutletName());
                intent.putExtra("customerName",mlist.get(position).getCustomerName());
                intent.putExtra("customercode",mlist.get(position).getCustomerCode());
                intent.putExtra("newOrderId",newOrderId);
                intent.putExtra("NewOrderinvoiceNumber",NewOrderinvoiceNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
                mContext.startActivity(intent);
            }
        });

    }

    private long generateorder() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }


    private String processCustomerCode(String customerCode) {
        if (customerCode == null || customerCode.isEmpty()) {
            return "";
        }
        String[] words = customerCode.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        return initials.toString();
    }

    public String generateNextInvoiceNumber(String lastvoiceInvoicenumber) {
        // Assuming the lastInvoice is in the format "D3S160920240000"
        String numericPart = lastvoiceInvoicenumber.substring(lastvoiceInvoicenumber.length() - 4);
        String prefix = lastvoiceInvoicenumber.substring(0, lastvoiceInvoicenumber.length() - 4);
        System.out.println("numericpart is"+numericPart);
        CustomerLogger.i("numericpart is",numericPart);

        // Increment the numeric part
        int nextNumber = Integer.parseInt(numericPart) + 1;
        System.out.println("next number is "+nextNumber);

        // Format the number to keep leading zeros
        String newInvoiceNumber = String.format("%04d", nextNumber);
        System.out.println("new invoice number is"+newInvoiceNumber);
        CustomerLogger.i("new invoice number is",newInvoiceNumber);

        return newInvoiceNumber;
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

    @Override
    public long getItemId(int position) {
//return super.getItemId(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
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

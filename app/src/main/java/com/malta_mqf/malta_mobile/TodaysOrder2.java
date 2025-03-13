package com.malta_mqf.malta_mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.Adapter.TodaysOrderAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.TodaysOrderBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodaysOrder2 extends AppCompatActivity {
    ListView todaysOrders;
    String outletname, outletlocation, outletid, outletcode,customerCode,trn_no,customeraddress,customerName;
    SubmitOrderDB submitOrderDB;
    Toolbar toolbar;
    TodaysOrderAdapter todaysOrderAdapter;
    List<TodaysOrderBean> todaysOrderBeanList = new ArrayList<>();
    private ALodingDialog aLodingDialog;
    AllCustomerDetailsDB allCustomerDetailsDB;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_order2);
        allCustomerDetailsDB=new AllCustomerDetailsDB(this);
        todaysOrderBeanList = new ArrayList<>();

        submitOrderDB = new SubmitOrderDB(this);
        todaysOrders = findViewById(R.id.todaysOrder);
        toolbar = findViewById(R.id.toolbar);
        aLodingDialog = new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve saved state if available
        if (savedInstanceState != null) {
            outletname = savedInstanceState.getString("outletname");
            outletlocation = savedInstanceState.getString("outletlocation");
            outletid = savedInstanceState.getString("outletid");
            outletcode = savedInstanceState.getString("outletcode");
            customerCode= savedInstanceState.getString("customerCode");
        } else {
            // Get intent extras for the first time
            outletname = getIntent().getStringExtra("outletname");
            outletlocation = getIntent().getStringExtra("outletlocation");
            outletid = getIntent().getStringExtra("outletid");
            outletcode = getIntent().getStringExtra("outletcode");
            customerCode=getIntent().getStringExtra("customerCode");
        }

        System.out.println("outletiddd: " + outletid);
        getCustomerTrn(customerCode);
        getOrdersBsdOnOutletId(outletid,  "DELIVERED");
        getSupportActionBar().setTitle(outletname + "  " + "(" + outletcode + ")");
      /*  todaysOrders.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                TodaysOrderBean selectedOrder = todaysOrderBeanList.get(position);
                String orderId = selectedOrder.getOrderid();

                // Show confirmation dialog with delete icon
                AlertDialog.Builder builder = new AlertDialog.Builder(TodaysOrder2.this);
                builder.setTitle("Delete Order")

                        .setMessage("Warning !! Do not Delete if you have same orders/duplicate orders."+"\n"+"Are you sure you want to delete this order?")
                        .setIcon(R.drawable.delete) // Set delete icon
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Delete from database
                                boolean isDeleted = deleteOrderByIdAndOutlet(orderId, outletid);
                                if (isDeleted) {
                                    // Remove from list and notify adapter
                                    todaysOrderBeanList.remove(position);
                                    todaysOrderAdapter.notifyDataSetChanged();
                                    Toast.makeText(TodaysOrder2.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(TodaysOrder2.this, "Failed to delete order", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true; // Return true to indicate long press was handled
            }
        });*/


      /*  todaysOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Show loading dialog immediately if the activity is still active
                if (!TodaysOrder2.this.isFinishing() && !TodaysOrder2.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                // Use an ExecutorService to perform intensive work in the background
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000); // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Perform background work (getting order details and clearing shared preferences)
                        String OrderId = todaysOrderBeanList.get(i).getOrderid();

                        Intent intent = new Intent(TodaysOrder2.this, NewSaleActivity.class);
                        intent.putExtra("outletname", outletname);
                        //  intent.putExtra("outletlocation", outletlocation);
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("orderid", OrderId);
                        intent.putExtra("trn_no",trn_no);
                        intent.putExtra("customeraddress",customeraddress);
                        intent.putExtra("customerCode",customerCode);
                        intent.putExtra("customerName",customerName);

                        // Clear shared preferences
                        clearAllSharedPreferences();

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Safely dismiss the dialog and start the new activity
                                if (!TodaysOrder2.this.isFinishing() && !TodaysOrder2.this.isDestroyed()) {
                                    if (aLodingDialog.isShowing()) {
                                        aLodingDialog.dismiss(); // Safely dismiss the dialog
                                    }
                                    // Start the new activity
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        });
*/

        todaysOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!isOnline()) {
                    TodaysOrderBean selectedOrder = todaysOrderBeanList.get(position);
                    String invOrOrderno = selectedOrder.getInvoiceOrOrderID();
                    String outletNameee = selectedOrder.getOutletName();
                    System.out.println("invOrOrderno is :" + invOrOrderno);
                    System.out.println("outletNameee is :" + outletNameee);


                    Intent intent = new Intent(TodaysOrder2.this, DeliveryHistoryDetails.class);
                    intent.putExtra("invOrOrderno", invOrOrderno);
                    intent.putExtra("outletname", outletNameee);
                    intent.putExtra("outletCode", outletcode);
                    intent.putExtra("sourceActivity", "DeliveryHistory");
                    System.out.println("invOrOrderno: " + invOrOrderno);
                    System.out.println("outletname: " + outletNameee);
                    System.out.println("outletCode too next page: " + outletcode);

                    startActivity(intent);
                    new Handler().postDelayed(() -> {
                        if (aLodingDialog.isShowing()) {
                            aLodingDialog.cancel();
                        }
                    }, 2000);
                }else{
                    Toast.makeText(TodaysOrder2.this,"you have connected to internet you cannot see the offline data, Turn off to see!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    // Save the important data in case of activity recreation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("outletname", outletname);
        outState.putString("outletlocation", outletlocation);
        outState.putString("outletid", outletid);
        outState.putString("outletcode", outletcode);
    }
    private boolean deleteOrderByIdAndOutlet(String orderid, String outletid) {
        return submitOrderDB.deleteOrders(orderid, outletid);
    }
    // Reload data on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        if (outletid != null) {
            getOrdersBsdOnOutletId(outletid,  "DELIVERED");
        }
    }

    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("NewSalesPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
    @SuppressLint("Range")
    private void getCustomerTrn(String customerCode) {
        Cursor cursor1 = allCustomerDetailsDB.readDataByCustomerCode(customerCode);

        if (cursor1 != null && cursor1.moveToFirst()) {
            trn_no = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
            customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
            customerName=cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
        } else {
            trn_no = "00000000000000";
        }

        // Always close the cursor after using it
        if (cursor1 != null) {
            cursor1.close();
        }

        System.out.println("TRN in Today's Order: " + trn_no);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
    private void getOrdersBsdOnOutletId(String outletid, String status) {
        todaysOrderBeanList.clear();
        Cursor cursor = submitOrderDB.readDataByOutletsIDAndStatus2(outletid, status);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range")
                String orderid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                @SuppressLint("Range") String orderStatus = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                @SuppressLint("Range") String invoiceno=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                String outletname=getOutletNameById(outletid);
                TodaysOrderBean todaysOrderBean = new TodaysOrderBean();
                todaysOrderBean.setOrderid(orderid);
                todaysOrderBean.setInvoiceOrOrderID(invoiceno);
                todaysOrderBean.setOrderStatus(orderStatus);
                todaysOrderBean.setOutletName(outletname);
                todaysOrderBeanList.add(todaysOrderBean);
            }
            cursor.close();
            // Initialize adapter only once, after data is added to the list
            todaysOrderAdapter = new TodaysOrderAdapter(this, todaysOrderBeanList);
            todaysOrders.setAdapter(todaysOrderAdapter);

            // Notify adapter of data changes
            todaysOrderAdapter.notifyDataSetChanged();
        }
    }


    private String getOutletNameById(String outletid) {
        String outletName = ""; // Default value in case of missing data

        OutletByIdDB outletByIdDB = new OutletByIdDB(this);
        Cursor cursor = outletByIdDB.getOutletNameById(outletid); // Assuming this method exists in your DB class
        System.out.println(cursor.getCount());
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME);
                    System.out.println("columnIndex is :"+columnIndex);
                    if (columnIndex != -1) {
                        outletName = cursor.getString(columnIndex);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return outletName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (aLodingDialog != null && aLodingDialog.isShowing()) {
            aLodingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aLodingDialog != null && aLodingDialog.isShowing()) {
            aLodingDialog.dismiss();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TodaysOrder2.this, TodaysDelivery.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.malta_mqf.malta_mobile.Adapter.TodaysOutletAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.TodaysOrderBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TodaysDelivery extends AppCompatActivity {

    ListView todaysOutlet, todayDeliveryDone;
    SubmitOrderDB submitOrderDB;
    Toolbar toolbar;
    TodaysOutletAdapter todaysDeliveryAdapter;
    TodaysOutletAdapter todaysDeliveryAdapter1;
    OutletByIdDB outletByIdDB;
    TodaysOrderBean todaysOrderBean;
    List<TodaysOrderBean> todaysOrderBeanList;
    List<TodaysOrderBean> todaysOrderDeliveryDoneList;
    String outletname, outletlocation, outletid, customerCode, customername;
    TextView noOrderTextView, noDelverTextView;
    AllCustomerDetailsDB allCustomerDetailsDB;
    private ALodingDialog aLodingDialog;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout, swipeRefreshLayout2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_delivery);
        todaysOrderBeanList = new ArrayList<>();
        todaysOrderDeliveryDoneList = new ArrayList<>();
        todaysOutlet = findViewById(R.id.listView1);
        todayDeliveryDone = findViewById(R.id.listView2);
        submitOrderDB = new SubmitOrderDB(this);
        toolbar = findViewById(R.id.toolbar);
        outletByIdDB = new OutletByIdDB(this);
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        aLodingDialog = new ALodingDialog(this);
        noOrderTextView = findViewById(R.id.noOrderTextView1);
        noDelverTextView = findViewById(R.id.noOrderTextView2);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1); // Initialize SwipeRefreshLayout
        swipeRefreshLayout2 = findViewById(R.id.swipeRefreshLayout2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("OUTLETS");
        sharedPreferences = getSharedPreferences("OutletPrefs", Context.MODE_PRIVATE);
        getOutlets();
        getOutletsWhichDelivered();
        //   getCustomerDetails();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the data
                refreshOutlets();
            }
        });

        swipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the data
                refreshOutlets2();
            }
        });
      /*  todaysOutlet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TodaysDelivery.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_delivered, null);
                builder.setView(dialogView);

                AlertDialog alertDialog = builder.create();

                Button yesButton = dialogView.findViewById(R.id.dialogButtonYes);
                yesButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                Button noButton = dialogView.findViewById(R.id.dialogButtonNo);
                noButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        todaysOrderBeanList.get(i).setDelivered(true);
                        saveDeliveredStatus(todaysOrderBeanList.get(i).getOutletid(), true);
                        runOnUiThread(() -> {

                            todaysDeliveryAdapter.notifyDataSetChanged();
                        });
                        alertDialog.dismiss();
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                return true;

            }
        });*/
        loadDeliveredStatus();


        todaysOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Ensure the index is within bounds
                if (i < 0 || i >= todaysOrderBeanList.size()) {
                    return; // Exit early if index is out of bounds
                }

                // Show the dialog if the activity is still active
                if (!TodaysDelivery.this.isFinishing() && !TodaysDelivery.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                // Use an ExecutorService to perform intensive work in a background thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Simulate a delay if necessary
                        try {
                            Thread.sleep(1000); // Simulate work by sleeping for 1 second
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Check again if the index is valid
                        if (i < 0 || i >= todaysOrderBeanList.size()) {
                            return; // Exit early if index is out of bounds
                        }

                        // Get the selected item details
                        String outletname = todaysOrderBeanList.get(i).getOutletName();
                        String outletlocation = todaysOrderBeanList.get(i).getOutletAddress();
                        String outLetID = todaysOrderBeanList.get(i).getOutletid();
                        String outletCode = todaysOrderBeanList.get(i).getOutletCode();
                        String customerCode = todaysOrderBeanList.get(i).getCustomerCode();
                        // Clear shared preferences in the background thread
                        clearAllSharedPreferences();
                        clearAllSharedPreferences2();

                        // Prepare the intent with necessary data
                        Intent intent = new Intent(TodaysDelivery.this, TodaysOrder.class);
                        intent.putExtra("outletname", outletname);
                        intent.putExtra("outletlocation", outletlocation);
                        intent.putExtra("outletid", outLetID);
                        intent.putExtra("outletcode", outletCode);
                        intent.putExtra("customerCode", customerCode);

                        System.out.println("outlet id in todays delivery:" + outLetID);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Dismiss the dialog and start the new activity on the main thread
                                if (!TodaysDelivery.this.isFinishing() && !TodaysDelivery.this.isDestroyed()) {
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


        todayDeliveryDone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Ensure the index is within bounds
                if (i < 0 || i >= todaysOrderDeliveryDoneList.size()) {
                    return; // Exit early if index is out of bounds
                }

                // Show the dialog if the activity is still active
                if (!TodaysDelivery.this.isFinishing() && !TodaysDelivery.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                // Use an ExecutorService to perform intensive work in a background thread
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Simulate a delay if necessary
                        try {
                            Thread.sleep(1000); // Simulate work by sleeping for 1 second
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Check again if the index is valid
                        if (i < 0 || i >= todaysOrderDeliveryDoneList.size()) {
                            return; // Exit early if index is out of bounds
                        }

                        // Get the selected item details
                        String outletname = todaysOrderDeliveryDoneList.get(i).getOutletName();
                        String outletlocation = todaysOrderDeliveryDoneList.get(i).getOutletAddress();
                        String outLetID = todaysOrderDeliveryDoneList.get(i).getOutletid();
                        String outletCode = todaysOrderDeliveryDoneList.get(i).getOutletCode();
                        String customerCode = todaysOrderDeliveryDoneList.get(i).getCustomerCode();
                        // Clear shared preferences in the background thread
                        clearAllSharedPreferences();
                        clearAllSharedPreferences2();

                        // Prepare the intent with necessary data
                        Intent intent = new Intent(TodaysDelivery.this, TodaysOrder2.class);
                        intent.putExtra("outletname", outletname);
                        intent.putExtra("outletlocation", outletlocation);
                        intent.putExtra("outletid", outLetID);
                        intent.putExtra("outletcode", outletCode);
                        intent.putExtra("customerCode", customerCode);
                        System.out.println("outlet id in todays delivery:" + outLetID);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Dismiss the dialog and start the new activity on the main thread
                                if (!TodaysDelivery.this.isFinishing() && !TodaysDelivery.this.isDestroyed()) {
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


    }

    /* @Override
     protected void onResume() {
         super.onResume();
         getOutlets();
     }*/
    private void refreshOutlets() {
        // Fetch latest data and update UI
        getOutlets();

        // Stop the refreshing animation once data is loaded
        swipeRefreshLayout.setRefreshing(false);
    }

    private void refreshOutlets2() {
        // Fetch latest data and update UI
        getOutletsWhichDelivered();

        // Stop the refreshing animation once data is loaded
        swipeRefreshLayout2.setRefreshing(false);
    }

    private void loadDeliveredStatus() {
        for (TodaysOrderBean bean : todaysOrderBeanList) {
            bean.setDelivered(getDeliveredStatus(bean.getOutletid()));
        }
    }

    private void saveDeliveredStatus(String outletId, boolean isDelivered) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(outletId, isDelivered);
        editor.apply();
    }

    private boolean getDeliveredStatus(String outletId) {
        return sharedPreferences.getBoolean(outletId, false);
    }

    private void clearAllSharedPreferences2() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReturnPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("NewSalesPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


    // Inside your getOutlets() method
    public void getOutlets() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = submitOrderDB.getAllOutletID("PENDING FOR DELIVERY");
                final List<TodaysOrderBean> tempOrderBeanList = new ArrayList<>();
                final Set<String> uniqueOutletIds = new HashSet<>(); // To track unique outlet IDs

                if (cursor != null) {
                    try {
                        while (cursor.moveToNext()) {
                            String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            String status = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));

                            Cursor cursor1 = outletByIdDB.readOutletByID(outletid);
                            if (cursor1 != null) {
                                try {
                                    if (cursor1.moveToFirst()) {
                                        String outletname = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                        String outletlocation = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                                        String outletids = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ID));
                                        String outletcode = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                                        String customercode = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
                                        // Check if this outlet ID has already been added
                                        if (uniqueOutletIds.add(outletid)) { // add() returns false if the element already exists
                                            // Create the object and add it to the temporary list
                                            TodaysOrderBean todaysOrderBean = new TodaysOrderBean(outletname, outletlocation, outletids, outletcode, status, customercode);
                                            tempOrderBeanList.add(todaysOrderBean);
                                        }
                                    }
                                } finally {
                                    cursor1.close();
                                }
                            }
                        }
                    } finally {
                        cursor.close();
                    }
                } else {
                    System.out.println("No data found");
                }

                // Update UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the data has changed
                        if (!tempOrderBeanList.equals(todaysOrderBeanList)) {
                            // Update the list and sort
                            todaysOrderBeanList.clear();
                            todaysOrderBeanList.addAll(tempOrderBeanList);

                            Collections.sort(todaysOrderBeanList, new Comparator<TodaysOrderBean>() {
                                @Override
                                public int compare(TodaysOrderBean o1, TodaysOrderBean o2) {
                                    if (o1.getOrderStatus().equals("PENDING FOR DELIVERY") && o2.getOrderStatus().equals("DELIVERED")) {
                                        return -1;
                                    } else if (o1.getOrderStatus().equals("DELIVERED") && o2.getOrderStatus().equals("PENDING FOR DELIVERY")) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });

                            // Update the UI
                            if (todaysOrderBeanList.isEmpty()) {
                                todaysOutlet.setVisibility(View.GONE);
                                noOrderTextView.setVisibility(View.VISIBLE);
                            } else {
                                noOrderTextView.setVisibility(View.GONE);
                                todaysOutlet.setVisibility(View.VISIBLE);
                                if (todaysDeliveryAdapter1 == null) {
                                    todaysDeliveryAdapter1 = new TodaysOutletAdapter(TodaysDelivery.this, todaysOrderBeanList);
                                    todaysOutlet.setAdapter(todaysDeliveryAdapter1);
                                } else {
                                    todaysDeliveryAdapter1.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
            }
        }).start();  // Start the background thread
    }


    // Inside your getOutlets() method
    public void getOutletsWhichDelivered() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = submitOrderDB.getAllOutletID("DELIVERED");
                final List<TodaysOrderBean> tempOrderBeanList = new ArrayList<>();
                final Set<String> uniqueOutletIds = new HashSet<>(); // To track unique outlet IDs

                if (cursor != null) {
                    try {
                        while (cursor.moveToNext()) {
                            String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            String status = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));

                            Cursor cursor1 = outletByIdDB.readOutletByID(outletid);
                            if (cursor1 != null) {
                                try {
                                    if (cursor1.moveToFirst()) {
                                        String outletname = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                        String outletlocation = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                                        String outletids = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ID));
                                        String outletcode = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                                        String customercode = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));

                                        // Check if this outlet ID has already been added
                                        if (uniqueOutletIds.add(outletid)) { // add() returns false if the element already exists
                                            // Create the object and add it to the temporary list
                                            TodaysOrderBean todaysOrderBean = new TodaysOrderBean(outletname, outletlocation, outletids, outletcode, status, customercode);
                                            tempOrderBeanList.add(todaysOrderBean);
                                        }
                                    }
                                } finally {
                                    cursor1.close();
                                }
                            }
                        }
                    } finally {
                        cursor.close();
                    }
                } else {
                    System.out.println("No data found");
                }

                // Update UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Check if the data has changed
                        if (!tempOrderBeanList.equals(todaysOrderDeliveryDoneList)) {
                            // Update the list and sort
                            todaysOrderDeliveryDoneList.clear();
                            todaysOrderDeliveryDoneList.addAll(tempOrderBeanList);

                            Collections.sort(todaysOrderDeliveryDoneList, new Comparator<TodaysOrderBean>() {
                                @Override
                                public int compare(TodaysOrderBean o1, TodaysOrderBean o2) {
                                    if (o1.getOrderStatus().equals("PENDING FOR DELIVERY") && o2.getOrderStatus().equals("DELIVERED")) {
                                        return -1;
                                    } else if (o1.getOrderStatus().equals("DELIVERED") && o2.getOrderStatus().equals("PENDING FOR DELIVERY")) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });

                            // Update the UI
                            if (todaysOrderDeliveryDoneList.isEmpty()) {
                                todayDeliveryDone.setVisibility(View.GONE);
                                noDelverTextView.setVisibility(View.VISIBLE);
                            } else {
                                noDelverTextView.setVisibility(View.GONE);
                                todayDeliveryDone.setVisibility(View.VISIBLE);
                                if (todaysDeliveryAdapter == null) {
                                    todaysDeliveryAdapter = new TodaysOutletAdapter(TodaysDelivery.this, todaysOrderDeliveryDoneList);
                                    todayDeliveryDone.setAdapter(todaysDeliveryAdapter);
                                } else {
                                    todaysDeliveryAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
            }
        }).start();  // Start the background thread
    }

    @SuppressLint("Range")
    public void getCustomerDetails() {
        Cursor cursor = outletByIdDB.readOutletByName(outletname);
        if (cursor.getCount() == 0) {
        } else while (cursor.moveToNext()) {

            customerCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
            @SuppressLint("Range")
            String contactPerson = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CONTACT_PERSON));
            @SuppressLint("Range") String contactNumber = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_MOBILE_NUMBER));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_EMAIL));
            Cursor cursor1 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
            while (cursor1.moveToNext()) {

                customername = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                @SuppressLint("Range")
                String address = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
                @SuppressLint("Range")
                String creditLimit = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CREDIT_LIMIT));
                @SuppressLint("Range")
                String creditPeriod = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CREDIT_PERIOD));
                @SuppressLint("Range")
                String type = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_TYPE));


            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TodaysDelivery.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();  // Finish the current activity

        // Optionally clear the list here if it's no longer needed.
        todaysOrderBeanList.clear();
    }

}
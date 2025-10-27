package com.malta_mqf.malta_mobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomerDetailsOnTodaysDelivery extends AppCompatActivity {

    private static final String OUTLET_NAME_KEY = "outletname";
    private static final String OUTLET_LOCATION_KEY = "outletlocation";
    private static final String OUTLET_ID_KEY = "outletid";
    private static final String ORDER_ID_KEY = "orderid";
    private static final String CUSTOMER_CODE_KEY = "customerCode";
    private static final String CUSTOMER_NAME_KEY = "customerName";
    private static final String TRN_NO_KEY = "trn_no";
    String outletname, outletlocation, outletid, customerCode, customername, Orderid, trn_no, customeraddress;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    Toolbar toolbar;
    ALodingDialog aLodingDialog;
    TextView customerName, cust_outletName, cust_contact_num, cust_email, cust_address, cust_district, cust_credit_limit, cust_creditperiod, cust_type;
    CardView newsalecardview, newordercardview, returncardview, deliverycardview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details_on_todays_delivery);

        // Restore saved state if available
        if (savedInstanceState != null) {
            outletname = savedInstanceState.getString(OUTLET_NAME_KEY);
            outletlocation = savedInstanceState.getString(OUTLET_LOCATION_KEY);
            outletid = savedInstanceState.getString(OUTLET_ID_KEY);
            Orderid = savedInstanceState.getString(ORDER_ID_KEY);
            customerCode = savedInstanceState.getString(CUSTOMER_CODE_KEY);
            customername = savedInstanceState.getString(CUSTOMER_NAME_KEY);
            trn_no = savedInstanceState.getString(TRN_NO_KEY);
        } else {
            outletname = getIntent().getStringExtra("outletname");
            outletlocation = getIntent().getStringExtra("outletlocation");
            outletid = getIntent().getStringExtra("outletid");
            Orderid = getIntent().getStringExtra("orderid");
        }

        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);
        setupViews();
        setupListeners();
        getCustomerDetails();
    }

    private void setupViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aLodingDialog = new ALodingDialog(this);

        customerName = findViewById(R.id.customerName);
        cust_outletName = findViewById(R.id.customer_outletname);
        cust_contact_num = findViewById(R.id.contactnoDesc);
        cust_email = findViewById(R.id.emailDesc);
        cust_credit_limit = findViewById(R.id.creditdesc);
        cust_creditperiod = findViewById(R.id.creditperioddesc);
        cust_type = findViewById(R.id.cust_type_desc);
        newsalecardview = findViewById(R.id.newSaleCardView);
        newordercardview = findViewById(R.id.newOrderCardView);
        returncardview = findViewById(R.id.returnCardView);
        deliverycardview = findViewById(R.id.deliveryCardView);

        getSupportActionBar().setTitle(outletname);
    }

    private void setupListeners() {
        newsalecardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show loading dialog immediately if the activity is still active
                if (!CustomerDetailsOnTodaysDelivery.this.isFinishing() && !CustomerDetailsOnTodaysDelivery.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                // Use an ExecutorService to perform background work
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Simulate background processing (e.g., saving data, preparing the intent)
                        try {
                            Thread.sleep(1000); // Simulate work by sleeping for 2 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Prepare the intent with necessary data
                        Intent intent = new Intent(CustomerDetailsOnTodaysDelivery.this, NewSaleActivity.class);
                        intent.putExtra("outletName", outletname);
                        intent.putExtra("outletId", outletid);
                        intent.putExtra("customerCode", customerCode);
                        intent.putExtra("customerName", customername);
                        intent.putExtra("customeraddress", customeraddress);
                        intent.putExtra("orderid", Orderid);
                        intent.putExtra("trn_no", trn_no);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Safely dismiss the dialog and start the activity
                                if (!CustomerDetailsOnTodaysDelivery.this.isFinishing() && !CustomerDetailsOnTodaysDelivery.this.isDestroyed()) {
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


        returncardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();
                Intent intent = new Intent(CustomerDetailsOnTodaysDelivery.this, CustomerReturn.class);
                intent.putExtra("outletId", outletid);
                intent.putExtra("customerCode", customerCode);
                intent.putExtra("customerName", customername);
                intent.putExtra("orderid", Orderid);
                intent.putExtra("trn_no", trn_no);
                startActivity(intent);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        });
    }

    @SuppressLint("Range")
    private void getCustomerDetails() {
        Cursor cursor = outletByIdDB.readOutletByID(outletid);//always use outletid
        if (cursor.getCount() == 0) {
            return;
        } else while (cursor.moveToNext()) {
            customerCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
            String contactPerson = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CONTACT_PERSON));
            String contactNumber = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_MOBILE_NUMBER));
            String email = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_EMAIL));
            Cursor cursor1 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
            while (cursor1.moveToNext()) {
                customername = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                trn_no = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                if (trn_no == null) {
                    trn_no = "00000000000000 0";
                }

                customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
                String creditLimit = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CREDIT_LIMIT));
                String creditPeriod = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CREDIT_PERIOD));
                String type = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_TYPE));

                if (customerName != null) {
                    customerName.setText(customername);
                }
                if (cust_outletName != null) {
                    cust_outletName.setText(Orderid);
                }
                if (cust_contact_num != null) {
                    cust_contact_num.setText(contactNumber);
                }
                if (cust_email != null) {
                    cust_email.setText(email);
                }
                if (cust_credit_limit != null) {
                    cust_credit_limit.setText("Credit Limit: " + creditLimit);
                }
                if (cust_creditperiod != null) {
                    cust_creditperiod.setText("Credit Period: " + creditPeriod);
                }
                if (cust_type != null) {
                    cust_type.setText("Customer: " + type);
                }
            }
            cursor1.close();
        }
        cursor.close();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(OUTLET_NAME_KEY, outletname);
        outState.putString(OUTLET_LOCATION_KEY, outletlocation);
        outState.putString(OUTLET_ID_KEY, outletid);
        outState.putString(ORDER_ID_KEY, Orderid);
        outState.putString(CUSTOMER_CODE_KEY, customerCode);
        outState.putString(CUSTOMER_NAME_KEY, customername);
        outState.putString(TRN_NO_KEY, trn_no);
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
        Intent intent;
        String sourceActivity = getIntent().getStringExtra("sourceActivity");

        if ("TodaysOrder".equals(sourceActivity)) {
            intent = new Intent(CustomerDetailsOnTodaysDelivery.this, TodaysOrder.class);
        } else if ("TodaysOrder2".equals(sourceActivity)) {
            intent = new Intent(CustomerDetailsOnTodaysDelivery.this, TodaysOrder2.class);
        } else {
            super.onBackPressed(); // Default behavior if no sourceActivity is found
            return;
        }

        // Set the appropriate flags
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}

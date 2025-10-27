package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.CustomArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.OnlineEndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnAddItemsAdapter;
import com.malta_mqf.malta_mobile.Adapter.onlineAddProductAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.Model.AllCustomerDetails;
import com.malta_mqf.malta_mobile.Model.AllCustomerDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OutletBean;
import com.malta_mqf.malta_mobile.Model.OutletsById;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnActivity extends BaseActivity {

    public static String customerID, outletID;
    public static String customername;
    static String customercode, outletCode, customeraddress;
    static List<String> listOutletIDs = new LinkedList<>();
    AutoCompleteTextView selectCustomer, selectOutlet;
    EndsWithArrayAdapter adapter;
    OnlineEndsWithArrayAdapter onlineadapter;
    List<String> listcustomer;
    List<String> listoutlet;
    List<String> onlineOutlet;
    List<OutletBean> slelctoutlet;
    List<OutletsByIdResponse> selectedoutlet;
    List<OutletBean> onlineselectedoutlet;
    RecyclerView recyclerViewselectedoutlet;
    ReturnAddItemsAdapter addproductAdapter;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    ItemTouchHelper itemTouchHelper;
    onlineAddProductAdapter onlineAddProductAdapter;
    EndsWithArrayAdapter onlineoutletrelatedAdapter;
    ImageView searchCustomerforOutlet, searchIcon;
    Toolbar toolbar;
    OnlineEndsWithArrayAdapter onlineEndsWithArrayAdapter;
    TextView selectoutletTextview;
    LinearLayout selectOutletLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return);
        listcustomer = new LinkedList<>();
        listoutlet = new LinkedList<>();

        onlineOutlet = new LinkedList<>();
        slelctoutlet = new LinkedList<>();
        selectedoutlet = new LinkedList<>();
        onlineselectedoutlet = new LinkedList<>();
        listOutletIDs = new LinkedList<>();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectCustomer = findViewById(R.id.actv_customer);
        selectOutlet = findViewById(R.id.actv_outlet);
        selectoutletTextview = findViewById(R.id.selectOutletTextView);
        recyclerViewselectedoutlet = findViewById(R.id.lv_orders);
        searchCustomerforOutlet = findViewById(R.id.search_icon);
        selectOutletLayout = findViewById(R.id.selectoutletLayout);
        searchIcon = findViewById(R.id.search_icon2);
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RETURN WITHOUT INVOICE");


        selectCustomer.setMaxLines(1);
        selectCustomer.setMovementMethod(new ScrollingMovementMethod());

        selectOutlet.setMaxLines(1);
        selectOutlet.setMovementMethod(new ScrollingMovementMethod());


        int maxLength = 12;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);


        selectOutlet.setFilters(filters);


        selectOutlet.setEllipsize(TextUtils.TruncateAt.END);

        selectOutletLayout.setEnabled(false);
        selectOutlet.setEnabled(false);
        selectOutlet.setFocusable(false);
        selectOutlet.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


        selectoutletTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
        selectOutlet.setTextColor(getResources().getColor(R.color.listitem_gray));
        searchIcon.setColorFilter(getResources().getColor(R.color.listitem_gray));
// To ensure all child views are also disabled, iterate through them
        for (int i = 0; i < selectOutletLayout.getChildCount(); i++) {
            View child = selectOutletLayout.getChildAt(i);
            child.setEnabled(false); // Disable each child view
        }
        // getCustomerDetails();
        displayAllCustomers();

//        if (isOnline()) {
//            getCustomerDetails();
//            System.out.println("online");
//        } else {
//            displayAllCustomers();
//
//        }


        selectCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOutletLayout.setEnabled(false);
                selectOutlet.setEnabled(false);
                selectOutlet.setFocusable(false);
                selectOutlet.setFocusableInTouchMode(false);
                selectOutlet.setText("");
                selectoutletTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                selectOutlet.setTextColor(getResources().getColor(R.color.listitem_gray));
                searchIcon.setColorFilter(getResources().getColor(R.color.listitem_gray));
                selectCustomer.showDropDown();
            }
        });

        selectCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectCustomer.getText().toString().isEmpty()) {
                    Toast.makeText(ReturnActivity.this, "Customer name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = allCustomerDetailsDB.readDataByName(selectCustomer.getText().toString().trim());
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        customercode = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_CODE));
                        customername = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                        customerID = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                        customeraddress = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
                        System.out.println("Customer code in return activity items: " + customercode);
                        Log.d("customercode", customercode);
                    }
                }
                cursor.close();

                listOutletIDs.clear();
                listoutlet.clear();
                slelctoutlet.clear();
                selectedoutlet.clear();
                displayAllOutletsById(customercode);
//                if (isOnline()) {
//                    getOutletDetailsById(selectCustomer.getText().toString().trim());
//                } else {
//                    displayAllOutletsById(customercode);
//                }
                if (listoutlet.isEmpty()) {
                    // If no outlets are found, set the adapter to an empty list and hide the dropdown
                    ArrayAdapter<String> emptyAdapter = new CustomArrayAdapter(ReturnActivity.this, new ArrayList<>());
                    selectOutlet.setAdapter(emptyAdapter);
                } else {
                    // Set the adapter with the new outlet names using the custom adapter
                    CustomArrayAdapter outletAdapter = new CustomArrayAdapter(ReturnActivity.this, listoutlet);
                    selectOutlet.setAdapter(outletAdapter);
                }
            }
        });


        selectCustomer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            selectOutletLayout.setEnabled(false);
                            selectOutlet.setEnabled(false);
                            selectOutlet.setFocusable(false);
                            selectOutlet.setFocusableInTouchMode(false);
                            selectOutlet.setText("");
                            selectoutletTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                            selectOutlet.setTextColor(getResources().getColor(R.color.listitem_gray));
                            searchIcon.setColorFilter(getResources().getColor(R.color.listitem_gray));
                            selectCustomer.showDropDown();
                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });

        selectOutlet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            // Simulate pressing the Enter key
                            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER);
                            selectOutlet.dispatchKeyEvent(event);
                            selectOutlet.showDropDown();

                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });


        selectOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOutlet.showDropDown();
            }
        });

        /*selectOutlet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                }
            }
        });*/


      /*  searchCustomerforOutlet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (selectCustomer.getText().toString().isEmpty()) {
                    Toast.makeText(ReturnActivity.this, "customer name cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {

                    Cursor cursor = allCustomerDetailsDB.readDataByName(selectCustomer.getText().toString().trim());
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            customercode = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_CODE));
                            customerID = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                            customeraddress=cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));

                            Log.d("customercode", customercode);
                        }
                    }
                    // getOutletDetailsById();
                    listOutletIDs.clear();
                    listoutlet.clear();
                    slelctoutlet.clear();
                    System.out.println("selectCustomer ...: " + selectCustomer.getText().toString().trim());
                    slelctoutlet.clear();
                    if (isOnline()) {
                        getOutletDetailsById(selectCustomer.getText().toString().trim());

                        System.out.println("online");
                    } else {
                        displayAllOutletsById(customercode);
                    }
                    cursor.close();
                }
            }
        });*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    @SuppressLint("Range")
    private void onOutletItemSelected(int position) {
        OutletsByIdResponse outletsByIdResponse = new OutletsByIdResponse();
        String outletInfo = listoutlet.get(position);
        System.out.println("Infooo" + outletInfo);
        String[] outletParts = outletInfo.split(" \\(");
        String outletname = outletParts[0].trim();
        String customerName = selectCustomer.getText().toString();
        System.out.println("OutletParts......," + Arrays.toString(outletParts));
        System.out.println("Outlet NAme is........" + outletname);
        String outletcode = outletParts.length > 1 ? outletParts[1].replace(")", "").trim() : "";

        outletsByIdResponse.setCustomerName(customerName);
        outletsByIdResponse.setOutletName(outletname);

        Cursor cursor = outletByIdDB.readOutletByNameandCustomerCode(outletname, customercode);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                outletCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                outletID = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ID));
                System.out.println("OutletId in addoutlet is:......." + outletID);
            }
        }
        outletsByIdResponse.setId(outletID);
        boolean exists = false;
        for (OutletsByIdResponse outlet : selectedoutlet) {
            if (outlet.getOutletName() != null && outlet.getOutletName().equals(outletname)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            selectedoutlet.add(outletsByIdResponse);
            addproductAdapter = new ReturnAddItemsAdapter(getApplicationContext(), selectedoutlet);
            recyclerViewselectedoutlet.setAdapter(addproductAdapter);
            recyclerViewselectedoutlet.setLayoutManager(new LinearLayoutManager(ReturnActivity.this));
        }

    }


    @SuppressLint("Range")
    private void onOutletItemSelected2(int position) {

        OutletBean outletBeans = new OutletBean();
        String outletname = slelctoutlet.get(position).getOutletnames();
        System.out.println("outletNameeeee" + outletname);
        String outlids = slelctoutlet.get(position).getOutletids();

        String customername = selectCustomer.getText().toString();
        System.out.println("customerNameeeee" + customername);
        outletBeans.setCustomername(customername);
        outletBeans.setOutletnames(outletname);
        outletBeans.setOutletids(outlids);
        boolean exists = false;
        for (OutletBean outlet : onlineselectedoutlet) {
            if (outlet.getOutletnames() != null && outlet.getOutletnames().equals(outletname)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            onlineselectedoutlet.add(outletBeans);
            onlineAddProductAdapter = new onlineAddProductAdapter(getApplicationContext(), onlineselectedoutlet);
            recyclerViewselectedoutlet.setAdapter(onlineAddProductAdapter);

            recyclerViewselectedoutlet.setLayoutManager(new LinearLayoutManager(ReturnActivity.this));
        }


    }

    private void getCustomerDetails() {
        // showProgressDialog();
        String url = ApiLinks.allCustomerDetails;
        Log.d("TAG", "getCustomerDetails online: " + url);
        Call<AllCustomerDetails> allCustomerDetailsCall = apiInterface.allCustomerDetails(url);
        allCustomerDetailsCall.enqueue(new Callback<AllCustomerDetails>() {

            @Override
            public void onResponse(Call<AllCustomerDetails> call, Response<AllCustomerDetails> response) {
                // dismissProgressDialog();
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllCustomerDetails allCustomerDetails = response.body();
                    List<AllCustomerDetailsResponse> allCustomerDetailsResponses = allCustomerDetails.getActiveCustomerDetails();

                    try {
                        for (AllCustomerDetailsResponse allCustomerDetailsResponse : allCustomerDetailsResponses) {

                            //  getOutletDetailsById();
                            customercode = allCustomerDetailsResponse.getCustomerCode();
                            listcustomer.add(allCustomerDetailsResponse.getCustomerName());

                            adapter = new EndsWithArrayAdapter(ReturnActivity.this, R.layout.list_item_text, R.id.list_textView_value, listcustomer);
                            selectCustomer.setAdapter(adapter);


                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<AllCustomerDetails> call, Throwable t) {
                System.out.println("on failiure called customer");

                displayAlert("Alert", t.getMessage());
            }
        });
    }

    @SuppressLint("Range")
    private void displayAllCustomers() {

        Cursor cursor = allCustomerDetailsDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            return;
        } else while (cursor.moveToNext()) {
            listcustomer.add(cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME)));
        }


        adapter = new EndsWithArrayAdapter(ReturnActivity.this, R.layout.list_item_text, R.id.list_textView_value, listcustomer);
        selectCustomer.setAdapter(adapter);
        cursor.close();
    }


    private void getOutletDetailsById(String customerName) {
        showProgressDialog();
        String url = ApiLinks.OutletDetailsById + "?van_id=" + vanID;
        Log.d("TAG", "getOutletDetailsById: " + url);
        System.out.println("outlet url:" + url);
        Call<OutletsById> outletsByIdCall = apiInterface.outletsById(url);
        outletsByIdCall.enqueue(new Callback<OutletsById>() {
            @Override
            public void onResponse(Call<OutletsById> call, Response<OutletsById> response) {
                try {
                    if (response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("yes")) {
                        OutletsById outletsById = response.body();
                        List<OutletsByIdResponse> outletsByIdResponses = outletsById.getOutletDetailsBasOnVan();
                        int count = 0;
                        for (OutletsByIdResponse outletsByIdResponse : outletsByIdResponses) {
                            System.out.println("outletname: " + outletsByIdResponse.getOutletName());
                            System.out.println("selectCustomer: " + selectCustomer.getText().toString().trim());

                            if (customerName.equalsIgnoreCase(outletsByIdResponse.getCustomerName())) {
                                count++;
                                String outletID = outletsByIdResponse.getOutletId();
                                String outletCode = outletsByIdResponse.getOutletCode();
                                String outletName = outletsByIdResponse.getOutletName();
                                System.out.println("outletID: " + outletID);
                                System.out.println("outletCode: " + outletCode);

                                String outletnames = outletName + "(" + outletCode + ")";

                                onlineOutlet.add(outletName);
                                OutletBean outletBean = new OutletBean(outletID, outletnames);
                                slelctoutlet.add(outletBean);


                                System.out.println("Online listoutlet: " + slelctoutlet);
                            }
                            updateAdapter();
                        }


                        if (count == 0) {
                            selectOutletLayout.setEnabled(false);
                            selectOutlet.setEnabled(false);
                            selectOutlet.setFocusable(false);
                            selectOutlet.setFocusableInTouchMode(false);
                            selectOutlet.setText("");
                            selectoutletTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                            selectOutlet.setTextColor(getResources().getColor(R.color.listitem_gray));
                            searchIcon.setColorFilter(getResources().getColor(R.color.listitem_gray));
                            for (int i = 0; i < selectOutletLayout.getChildCount(); i++) {
                                View child = selectOutletLayout.getChildAt(i);
                                child.setEnabled(false); // Disable each child view
                            }
                            Toast.makeText(ReturnActivity.this, "No Outlets Present for this Customer!", Toast.LENGTH_SHORT).show();
                        }
                        dismissProgressDialog();
                        System.out.println("Online Outlets List: " + onlineOutlet);
                        System.out.println("Total size" + onlineOutlet.size());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OutletsById> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", t.getMessage());
            }
        });
    }

    private void updateAdapter() {
        if (slelctoutlet.size() >= 1) {
            selectOutletLayout.setEnabled(true);
            selectOutlet.setEnabled(true);
            selectOutlet.setFocusable(true);
            selectOutlet.setFocusableInTouchMode(true);
            selectOutlet.setText("");
            selectoutletTextview.setTextColor(getResources().getColor(R.color.black));
            selectOutlet.setTextColor(getResources().getColor(R.color.black));
            searchIcon.setColorFilter(getResources().getColor(R.color.black));
            for (int i = 0; i < selectOutletLayout.getChildCount(); i++) {
                View child = selectOutletLayout.getChildAt(i);
                child.setEnabled(true); // Enable each child view
            }
            onlineadapter = new OnlineEndsWithArrayAdapter(ReturnActivity.this, slelctoutlet);
            // Set a fixed height
            // Set dropdown height relative to the screen height


            selectOutlet.setAdapter(onlineadapter);
            onlineadapter.notifyDataSetChanged();
            // Set an OnItemClickListener to the AutoCompleteTextView
            selectOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    selectedoutlet.clear();
                    onOutletItemSelected2(position);


                }
            });
        } else {
            //  Toast.makeText(this, "No Outlets Present for this Customer!", Toast.LENGTH_SHORT).show();
        }


    }


    @SuppressLint("Range")
    private void displayAllOutletsById(String customerid) {
        showProgressDialog();
        listoutlet.clear();

        Cursor cursor = outletByIdDB.checkIfOutletExists(customerid);
        if (cursor.getCount() == 0) {
            selectOutletLayout.setEnabled(false);
            selectOutlet.setEnabled(false);
            selectOutlet.setFocusable(false);
            selectOutlet.setFocusableInTouchMode(false);
            selectOutlet.setText("");
            selectoutletTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
            selectOutlet.setTextColor(getResources().getColor(R.color.listitem_gray));
            searchIcon.setColorFilter(getResources().getColor(R.color.listitem_gray));
            for (int i = 0; i < selectOutletLayout.getChildCount(); i++) {
                View child = selectOutletLayout.getChildAt(i);
                child.setEnabled(false);
            }
            dismissProgressDialog();
            Toast.makeText(this, "No Outlets Present for this Customer!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            while (cursor.moveToNext()) {
                String outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                String outletCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                listoutlet.add(outletName + " (" + outletCode + ")");
            }
            offlineUpdateAdapter();
        }

        cursor.close();
        dismissProgressDialog();
    }


    private void offlineUpdateAdapter() {
        if (listoutlet.size() >= 1) {
            selectOutletLayout.setEnabled(true);
            selectOutlet.setEnabled(true);
            selectOutlet.setFocusable(true);
            selectOutlet.setFocusableInTouchMode(true);
            selectOutlet.setText("");
            selectoutletTextview.setTextColor(getResources().getColor(R.color.black));
            selectOutlet.setTextColor(getResources().getColor(R.color.black));
            searchIcon.setColorFilter(getResources().getColor(R.color.black));
            for (int i = 0; i < selectOutletLayout.getChildCount(); i++) {
                View child = selectOutletLayout.getChildAt(i);
                child.setEnabled(true); // Enable each child view
            }
            adapter = new EndsWithArrayAdapter(this, R.layout.list_item_text, R.id.list_textView_value, listoutlet);
            selectOutlet.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // Set an OnItemClickListener to the AutoCompleteTextView
            selectOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedoutlet.clear();
                    onOutletItemSelected(position);


                }
            });

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReturnActivity.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
        listcustomer.clear();
        listoutlet.clear();
        selectedoutlet.clear();
//        recyclerViewselectedoutlet = null;
//        addproductAdapter = null;
//        allCustomerDetailsDB = null;
//        customercode = null;
//        outletCode = null;
//        outletByIdDB = null;
//        customerID = null;
//        outletID = null;
//        customername = null;


    }
}
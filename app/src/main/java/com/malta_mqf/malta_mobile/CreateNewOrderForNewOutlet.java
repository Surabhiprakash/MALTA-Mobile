package com.malta_mqf.malta_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.google.android.material.snackbar.Snackbar;
import com.malta_mqf.malta_mobile.Adapter.CreateNewOrderForNewOutletAdapter;
import com.malta_mqf.malta_mobile.Adapter.CustomArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.OnlineEndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.onlineAddProductAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.Model.OutletBean;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CreateNewOrderForNewOutlet extends BaseActivity {
    AutoCompleteTextView selectCustomer, selectOutlet;
    EndsWithArrayAdapter adapter;
    OnlineEndsWithArrayAdapter onlineadapter;
    List<String> listcustomer ;
    List<String> listoutlet ;

    List<String> onlineOutlet ;
    List<OutletBean> slelctoutlet ;
    List<OutletsByIdResponse> selectedoutlet ;
    List<OutletBean> onlineselectedoutlet ;
    RecyclerView recyclerViewselectedoutlet;
    CreateNewOrderForNewOutletAdapter createNewOrderForNewOutletAdapter;
    AllCustomerDetailsDB allCustomerDetailsDB;
    public static String customercode, outletCode;
    OutletByIdDB outletByIdDB;
    public static String customerID, outletID ,NewOrderinvoiceNumber,lastinvoicenumber;

    ItemTouchHelper itemTouchHelper;

    public static String customerName, outletName;
    com.malta_mqf.malta_mobile.Adapter.onlineAddProductAdapter onlineAddProductAdapter;
    EndsWithArrayAdapter onlineoutletrelatedAdapter;
    ImageView searchCustomerforOutlet, searchIcon;
    Toolbar toolbar;
    OnlineEndsWithArrayAdapter onlineEndsWithArrayAdapter;
    TextView selectoutletTextview;
    LinearLayout selectOutletLayout;
    static List<String> listOutletIDs=new LinkedList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_order_for_new_outlet);

         listcustomer = new LinkedList<>();
         listoutlet = new LinkedList<>();

         onlineOutlet = new LinkedList<>();
         slelctoutlet = new LinkedList<>();
         selectedoutlet = new LinkedList<>();
        onlineselectedoutlet = new LinkedList<>();
        listOutletIDs = new LinkedList<>();

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
        getSupportActionBar().setTitle("CREATE NEW ORDER");


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
        displayAllCustomers();


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
     /*   selectCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectCustomer.getText().toString().isEmpty()) {
                    Toast.makeText(CreateNewOrderForNewOutlet.this, "Customer name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get customer name and query the database
                customerName = selectCustomer.getText().toString().trim();
                Cursor cursor = allCustomerDetailsDB.readDataByName(customerName);

                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        customercode = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_CODE));
                        customerID = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                        Log.d("customercode", customercode);
                    }
                }
                cursor.close();

                // Clear previous data to avoid showing old data
                listOutletIDs.clear();
                listoutlet.clear();
                slelctoutlet.clear();

                // Load new data for the selected customer
                displayAllOutletsById(customercode);
            }
        });*/



        selectCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectCustomer.getText().toString().isEmpty()) {
                    Toast.makeText(CreateNewOrderForNewOutlet.this, "Customer name cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get customer name and query the database
                customerName = selectCustomer.getText().toString().trim();
                Cursor cursor = allCustomerDetailsDB.readDataByName(customerName);

                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        customercode = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_CODE));
                        customerID = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                        Log.d("customercode", customercode);
                    }
                }
                cursor.close();

                // Clear previous data to avoid showing old data
                listOutletIDs.clear();
                listoutlet.clear();
                slelctoutlet.clear();
                selectedoutlet.clear();
                // Load new data for the selected customer
                displayAllOutletsById(customercode);

                // Update the existing adapter directly with the new outlet names
                if (listoutlet.isEmpty()) {
                    // If no outlets are found, set the adapter to an empty list and hide the dropdown
                    ArrayAdapter<String> emptyAdapter = new CustomArrayAdapter(CreateNewOrderForNewOutlet.this, new ArrayList<>());
                    selectOutlet.setAdapter(emptyAdapter);
                } else {
                    // Set the adapter with the new outlet names using the custom adapter
                    CustomArrayAdapter outletAdapter = new CustomArrayAdapter(CreateNewOrderForNewOutlet.this, listoutlet);
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
                    }, 100);
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


       /* searchCustomerforOutlet.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                if (selectCustomer.getText().toString().isEmpty()) {
                    Toast.makeText(CreateNewOrderForNewOutlet.this, "customer name cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {

                    Cursor cursor = allCustomerDetailsDB.readDataByName(selectCustomer.getText().toString().trim());
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            customercode = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_CODE));
                            customerID = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                            Log.d("customercode", customercode);
                        }
                    }
                    // getOutletDetailsById();
                    listOutletIDs.clear();
                    listoutlet.clear();
                    slelctoutlet.clear();
                    System.out.println("selectCustomer ...: " + selectCustomer.getText().toString().trim());
                    slelctoutlet.clear();

                    displayAllOutletsById(customercode);

                    System.out.println("online");

                    cursor.close();
                }
            }
        });*/
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


        adapter = new EndsWithArrayAdapter(CreateNewOrderForNewOutlet.this, R.layout.list_item_text, R.id.list_textView_value, listcustomer);
        selectCustomer.setAdapter(adapter);
        cursor.close();
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
                if (!outletName.isEmpty() && !outletCode.isEmpty()) {
                    listoutlet.add(outletName + " (" + outletCode + ")");
                }
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

    @SuppressLint("Range")
    private void onOutletItemSelected(int position) {
        OutletsByIdResponse outletsByIdResponse = new OutletsByIdResponse();
        String outletInfo = listoutlet.get(position);
        String[] outletParts = outletInfo.split(" \\(");
        String outletname = outletParts[0].trim();
        String outletcode = outletParts.length > 1 ? outletParts[1].replace(")", "").trim() : "";

        outletsByIdResponse.setOutletName(outletname);

        Cursor cursor = outletByIdDB.readOutletByNameandCustomerCode(outletname,customercode);;
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                outletCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                outletID = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ID));
            }
        }
        outletsByIdResponse.setId(outletID);
        outletsByIdResponse.setCustomerName(customerName);
        outletsByIdResponse.setCustomerCode(customercode);
        boolean exists = false;
        for (OutletsByIdResponse outlet : selectedoutlet) {
            if (outlet.getOutletName() != null && outlet.getOutletName().equals(outletname)) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            selectedoutlet.add(outletsByIdResponse);
            createNewOrderForNewOutletAdapter = new CreateNewOrderForNewOutletAdapter(getApplicationContext(), selectedoutlet);
            recyclerViewselectedoutlet.setAdapter(createNewOrderForNewOutletAdapter);
            recyclerViewselectedoutlet.setLayoutManager(new LinearLayoutManager(CreateNewOrderForNewOutlet.this));
        }
        cursor.close();
        initializeSwipeToDelete();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initializeSwipeToDelete() {
        if (createNewOrderForNewOutletAdapter != null) {
            itemTouchHelper = new ItemTouchHelper(new CreateNewOrderForNewOutlet.SwipeToDeleteCallback(createNewOrderForNewOutletAdapter));
            itemTouchHelper.attachToRecyclerView(recyclerViewselectedoutlet);
        }
    }


    class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        SwipeToDeleteCallback(CreateNewOrderForNewOutletAdapter createNewOrderForNewOutletAdapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();
            if (isValidItemPosition(itemPosition)) {
                // Get the values of the deleted item before removing it
                customerName = selectedoutlet.get(itemPosition).getCustomerName();  // Update this line if needed
                outletName = selectedoutlet.get(itemPosition).getOutletName();
                Log.d("TAG", "onSwiped: " + customerName + " " + outletName);

                createNewOrderForNewOutletAdapter.removeItem(String.valueOf(itemPosition), itemPosition);
                showUndoSnackbar(viewHolder, itemPosition);
            }
        }

        private boolean isValidItemPosition(int position) {
            return position >= 0 && position < selectedoutlet.size();
        }

        private void showUndoSnackbar(RecyclerView.ViewHolder viewHolder, final int itemPosition) {
            Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                    "Item deleted.", Snackbar.LENGTH_LONG);

            snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createNewOrderForNewOutletAdapter.restoreItem(itemPosition, customerName, outletName);
                }
            });

            snackbar.show();
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CreateNewOrderForNewOutlet.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(CreateNewOrderForNewOutlet.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightLabel(getString(R.string.action_delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel(getString(R.string.action_delete))
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        customercode = null;
//        outletCode = null;
//        customerName = null;
//        outletName = null;

        Intent intent = new Intent(CreateNewOrderForNewOutlet.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
        listcustomer.clear();
        listoutlet.clear();
        selectedoutlet.clear();
        slelctoutlet.clear();
        listOutletIDs.clear();

    }
}
package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.ReturnActivity.customeraddress;
import static com.malta_mqf.malta_mobile.ReturnActivity.customercode;
import static com.malta_mqf.malta_mobile.ReturnActivity.customername;
import static com.malta_mqf.malta_mobile.ReturnActivity.listOutletIDs;
import static com.malta_mqf.malta_mobile.MainActivity.userID;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderConfrimSpinnerAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnAddQtyAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetails;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetailsResponse;
import com.malta_mqf.malta_mobile.Model.AllItemDeatilsById;
import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;
import com.malta_mqf.malta_mobile.Model.OnlineProductBean;
import com.malta_mqf.malta_mobile.Model.OrderConfrimBean;
import com.malta_mqf.malta_mobile.Model.OrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Model.ReturnItemDetailsBean;
import com.malta_mqf.malta_mobile.Model.ReturnWithoutInvoiceBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnAddQtyActivity extends BaseActivity implements ReturnAddQtyAdapter.QuantityChangeListener {

    AutoCompleteTextView spinneragecny;
    AutoCompleteTextView spinnerproducts;
    SearchView searchView;
    TextView qtycount, itemcount;
    GetCusOutletAgencyProductAdapter adapter;

    ALodingDialog aLodingDialog;

    private DatePickerDialog datePickerDialog;


    private List<Map.Entry<String, String>> itemList;

    List<String> listagency ;
    List<String> listproduct ;

    ReturnAddQtyAdapter addQtyAdapter;
    RecyclerView recyclerView;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    String agencycode;
    String agencyID, productID, quantity, ItemCode;
    public static List<Map.Entry<String, String>> selectedproduct = new LinkedList<>();
    List<String> submittedorder ;
    Set<ProductInfo> productIdQty;
    ImageView searchitembyagency, searchproductIcons;
    List<String> prodqty ;
    List<String> getProdqty ;
    Button submit;
    SubmitOrderDB submitOrderDB;
    ReturnDB returnDB;
    String outlet;
    String customerCode;
    private ItemTouchHelper itemTouchHelper;
    List<String> returnreasons;
    String agency;
    List<String> onlineProductID ;
    List<String> onlineItemCode ;
    List<String> onlinelistagencyids ;
    List<String> onlineReqQtys ;
    List<OnlineProductBean> onlineProductBeanList ;
    EndsWithArrayAdapter endsWithArrayAdapter;
    TextView outletname_header, selectProductTextview;
    Toolbar toolbar;
    LinearLayout searchProductLayout;
    int totalQuantity, totalItems;
    String leadTime;
    ArrayAdapter<String > adapter1;
    String credID;
   static List<ReturnWithoutInvoiceBean> orderConfrimBeans=new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return_add_qty);
         listagency = new LinkedList<>();
         listproduct = new LinkedList<>();
         selectedproduct = new LinkedList<>();
        submittedorder = new LinkedList<>();
         productIdQty = new LinkedHashSet<>();
         prodqty = new LinkedList<>();
         getProdqty = new LinkedList<>();
         onlineProductID = new LinkedList<>();
         onlineItemCode = new LinkedList<>();
         onlinelistagencyids = new LinkedList<>();
         onlineReqQtys = new LinkedList<>();
         onlineProductBeanList = new LinkedList<>();
         orderConfrimBeans = new LinkedList<>();
            returnreasons=new LinkedList<>();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemList = new ArrayList<>();

        Toast.makeText(this, "Add Items to Return Here!", Toast.LENGTH_SHORT).show();
        spinneragecny = findViewById(R.id.spinnerLeft);
        spinnerproducts = findViewById(R.id.spinnerRight);
        searchitembyagency = findViewById(R.id.search_icon);
        recyclerView = findViewById(R.id.listaddproducts);
        searchproductIcons = findViewById(R.id.search_icon2);
        searchProductLayout = findViewById(R.id.searchproductLayout);
        selectProductTextview = findViewById(R.id.selectOutletTextView);
        searchView=findViewById(R.id.searchView);
        qtycount=findViewById(R.id.qtycount);
        itemcount=findViewById(R.id.itemcount);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        showLeadTimeDialog();
        aLodingDialog=new ALodingDialog(this);
        submit = findViewById(R.id.submitorder);
        submit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        submitOrderDB = new SubmitOrderDB(this);
        returnDB=new ReturnDB(this);
        outletname_header = findViewById(R.id.outletname_header);
        outlet = getIntent().getStringExtra("outletId");
        credID=getIntent().getStringExtra("credID");
        System.out.println("OutletID in return add qty: " + outlet);
        customerCode = getIntent().getStringExtra("customerCode");
        System.out.println("customerCode in ReturnAddQtyActivity:" + customercode);
        returnreasons.add("Expired");
        returnreasons.add("Re-usable");
        returnreasons.add("damaged");
        // outletname_header.setText();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(getIntent().getStringExtra("outletName")==null){
            getSupportActionBar().setTitle("RETURN ITEMS- ");
        }else{
            getSupportActionBar().setTitle("RETURN ITEMS- "+getIntent().getStringExtra("outletName"));

        }
        System.out.println("OutletID in add qty: " + outlet);

        spinneragecny.setMaxLines(1);
        spinneragecny.setMovementMethod(new ScrollingMovementMethod());
        spinnerproducts.setMaxLines(1);
        spinnerproducts.setMovementMethod(new ScrollingMovementMethod());

        int maxLength = 12;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);


        spinnerproducts.setFilters(filters);


        spinnerproducts.setEllipsize(TextUtils.TruncateAt.END);


        searchProductLayout.setEnabled(false);
        spinnerproducts.setEnabled(false);
        spinnerproducts.setFocusable(false);
        spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


        selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
        spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
        searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));

            displayAllAgency();


        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listproduct);
        recyclerView.setAdapter(addQtyAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (addQtyAdapter != null) {
                    addQtyAdapter.filter(newText);
                }
                return false;
            }
        });

        spinneragecny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerproducts.setEnabled(false);
                spinnerproducts.setEnabled(false);
                spinnerproducts.setFocusable(false);
                spinnerproducts.setFocusableInTouchMode(false);
                //      spinnerproducts.setText("");
                selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
                spinneragecny.showDropDown();
            }
        });




       /* searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(ReturnAddQtyActivity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();
                } else if (spinneragecny.getText().toString().trim().equalsIgnoreCase("ALL")) {
                    Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
                    List<String> agencyCodes = new LinkedList<>();
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            String agencycodes = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                            //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));
                            agencyCodes.add(agencycodes);
                        }

                    }

                        displayAllItemsByAllAgencyIDS(agencyCodes, customercode.toLowerCase());


                } else {
                    agency = spinneragecny.getText().toString().trim();
                    getProdqty.addAll(prodqty);
                    prodqty.clear();

                    new Thread(() -> {
                        Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency);
                        if (cursor != null && cursor.getCount() != 0) {
                            while (cursor.moveToNext()) {
                                agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                                //agencyID = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));
                            }
                            cursor.close();
                        }

                        runOnUiThread(() -> {
                            if (agencycode != null) {
                                Log.d("TAG", "onItemSelected: " + agencycode);

                                listproduct.clear();

                                    displayAllItemsById(agencycode, customercode);

                            } else {
                                // Handle the case where no agency was found
                                Log.d("TAG", "No agency found with the name: " + agency);
                                // Optionally display a message to the user
                            }
                        });
                    }).start();
                }
            }
        });
*/

        spinneragecny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(ReturnAddQtyActivity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();
                } else if(spinneragecny.getText().toString().trim().equalsIgnoreCase("ALL")) {
                    Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
                    List<String> agencyCodes=new LinkedList<>();
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            String  agencycodes = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                            //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));
                            agencyCodes.add(agencycodes);
                        }

                    }

                        displayAllItemsByAllAgencyIDS(agencyCodes,customercode);


                }else {
                    agency = spinneragecny.getText().toString().trim();
                    getProdqty.addAll(prodqty);
                    prodqty.clear();
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                            //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));

                        }

                        // agencyIDS.add(agencyID);
                        Log.d("TAG", "onItemSelected: " + agencycode);
                    }
                    listproduct.clear();

                        displayAllItemsById(agencycode,customercode);

                    // displayAllItemsById(agencycode);
                    cursor.close();
                }
            }
        });
        spinneragecny.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                    searchProductLayout.setEnabled(false);
                    spinnerproducts.setEnabled(false);
                    spinnerproducts.setFocusable(false);
                    spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


                    selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                    spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                    searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
                    spinneragecny.showDropDown();
                }
                }, 100); // Small delay to ensure activity is in a valid state
            }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkOrderStatusAndUpdateButton(credID)) {
                    CustomerLogger.i("checking previous credit note",credID);
                    showOrderBlockedAlert();
                    return; // Stop execution if the order is delivered with an invoice
                }
                 orderConfrimBeans.clear();

                if(selectedproduct.size()>0 ){

                    for (Map.Entry<String, String> entry : selectedproduct) {
                        // System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                        if (!entry.getValue().equals("0")) {
                            ReturnWithoutInvoiceBean orderConfrimBean = new ReturnWithoutInvoiceBean();
                            orderConfrimBean.setItemName(entry.getKey());
                            orderConfrimBean.setReturn_qty(entry.getValue());
                            orderConfrimBean.setReasonList(returnreasons);
                            orderConfrimBeans.add(orderConfrimBean);

                        }

                    }

                    Intent intent = new Intent(ReturnAddQtyActivity.this, ConfirmReturnsActivity.class);
                   intent.putExtra("customerCode",customercode);
                   intent.putExtra("outletId",outlet);
                   intent.putExtra("customeraddess",customeraddress);
                   intent.putExtra("customername",customername);
                   intent.putExtra("credID",credID);
                    // intent.putExtra("returnItemDetailsList", (Serializable) orderConfrimBeans);
                    startActivity(intent);


                }else{
                    Toast.makeText(ReturnAddQtyActivity.this, "Please add the items before confirming the order", Toast.LENGTH_SHORT).show();
                }

            }




        });
    }
    private void showOrderBlockedAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Order Blocked")
                .setMessage("This order has already been Returned and has an CredNote. You cannot proceed.If you want to create another Return to this same outlet, please create a new Return")
                .setCancelable(false) // Prevent closing by tapping outside
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(ReturnAddQtyActivity.this,StartDeliveryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
                        startActivity(i);
                        finish();
                        dialog.dismiss(); // Close the alert
                    }
                })
                .show();
    }
    private boolean checkOrderStatusAndUpdateButton(String credID) {
        try {
            System.out.println("credID: .." + credID);
            if (submitOrderDB == null) {
                submitOrderDB = new SubmitOrderDB(this);
            }

            boolean isDeliveredWithInvoice = returnDB.isCreditNoteIdPresent(credID);
            System.out.println(isDeliveredWithInvoice);
            if (isDeliveredWithInvoice) {
                runOnUiThread(() -> {
                    submit.setEnabled(false);
                    submit.setAlpha(0.5f); // Reduce opacity to indicate it's disabled
                    Toast.makeText(this, "Order is Returned and CredNote exists!", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    submit.setEnabled(true);
                    submit.setAlpha(1f); // Restore full opacity
                });
            }
            return isDeliveredWithInvoice;
        } catch (IllegalStateException e) {
            Log.e("OrderCheck", "IllegalStateException: " + e.getMessage(), e);
            Toast.makeText(this, "An error occurred while checking order status!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private void scrollToItem(String selectedItem) {
        int position = -1;
        for (int i = 0; i < listproduct.size(); i++) {
            if (listproduct.get(i).equalsIgnoreCase(selectedItem)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            final int finalPosition = position;
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    recyclerView.removeOnLayoutChangeListener(this);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.scrollToPositionWithOffset(finalPosition, 0);
                        addQtyAdapter.setSelectedPosition(finalPosition);
                    }
                }
            });
            recyclerView.requestLayout();
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

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme, // Custom theme
                onDateSetListener,
                year,
                month,
                dayOfMonth);
        datePickerDialog.setTitle("Select expected delivery date");

        // Disable current and previous dates
        Calendar minDate = Calendar.getInstance();
        minDate.set(year, month, dayOfMonth +1+ Integer.parseInt(leadTime)); // Set minimum date to the day after the current date
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatePicker datePicker = datePickerDialog.getDatePicker();
                int selectedYear = datePicker.getYear();
                int selectedMonth = datePicker.getMonth();
                int selectedDay = datePicker.getDayOfMonth();
                String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);

                // Handle the selected date (e.g., display it, use it somewhere, etc.)
                new ProcessOrderTask().execute(selectedDate);
            }
        });

        datePickerDialog.show();
    }

    private class ProcessOrderTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            aLodingDialog.show(); // Show loading dialog before starting background processing
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String selectedDate = params[0];

            if (selectedproduct.size() != 0) {
                if (listOutletIDs.size() > 1) {
                    // Process multiple outlet IDs
                    for (String outletID : listOutletIDs) {
                        boolean success = processOrder(outletID, selectedDate);
                        if (!success) {
                            return false;
                        }
                    }
                } else {
                    // Process single outlet ID
                    return processOrder(outlet, selectedDate);
                }
            } else {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            aLodingDialog.dismiss(); // Dismiss loading dialog after background processing

            if (result) {
                // Handle successful completion here
                Intent intent = new Intent(ReturnAddQtyActivity.this, AddItemsActivity.class);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                selectedproduct.clear();
                finish();
            } else {
                // Handle failure if needed
                showToastOnMainThread("Processing failed");
            }
        }
    }

    @SuppressLint({"StaticFieldLeak", "Range"})
    private boolean processOrder(final String outletID, final String selectedDate) {
        String orderID;
        String CUSTOMERCODE;
        String processedCustomerCode = processCustomerCode(customerCode);

        if (customercode == null) {
            orderID = processedCustomerCode.toUpperCase() + outletID + String.valueOf(generateRandomOrderID()) + "-M";
            CUSTOMERCODE = customerCode;
        } else {
            orderID = processCustomerCode(customercode).toUpperCase() + outletID + String.valueOf(generateRandomOrderID()) + "-M";
            CUSTOMERCODE = customercode;
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (isOnline()) {
            // Online processing
            int count = 0;
            for (OnlineProductBean onlineProductBean : onlineProductBeanList) {
                for (Map.Entry<String, String> entry : selectedproduct) {
                    if (entry.getKey().equals(onlineProductBean.getProductName()) && !entry.getValue().equals("0")) {
                        count++;
                        if (!onlineProductID.contains(onlineProductBean.getProductId())) {
                            onlineProductID.add(onlineProductBean.getProductId());
                            onlineItemCode.add(onlineProductBean.getItemCode());
                            onlinelistagencyids.add(onlineProductBean.getAgencydid());
                            onlineReqQtys.add(entry.getValue());
                        }
                    }
                }
            }
            if (count == 0) {
                showToastOnMainThread("Items Quantity cannot be 0!");
                return false;
            }

            syncOrders(orderID, outletID, dateFormat.format(date), CUSTOMERCODE, selectedDate, leadTime);
        } else {
            // Offline processing
            int count = 0;
            for (Map.Entry<String, String> entry : selectedproduct) {
                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                        ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                        agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                        if (!entry.getValue().equalsIgnoreCase("0")) {
                            count++;
                            productIdQty.add(new ProductInfo(productID, agencycode, ItemCode, entry.getValue()));
                        }
                    }
                }
                cursor.close();
            }
            if (count == 0) {
                showToastOnMainThread("Items Quantity cannot be 0!");
                return false;
            }
            if (productIdQty.size() != 0) {
                submitOrderDB.submitDetails(orderID, userID, vanID, outletID, productIdQty, "Not Synced", CUSTOMERCODE, dateFormat.format(date), selectedDate, "0");
            }
        }

        // If successful, return true
        return true;
    }

    // Method to show toast on the main thread
    private void showToastOnMainThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ReturnAddQtyActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
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

    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                // You can leave this empty if you handle the date selection in the OK button's listener
            };

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }


    private long generateRandomOrderID() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }




    @SuppressLint("Range")
    private void displayAllAgency() {
        listagency.add("ALL");
        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Agency data", Toast.LENGTH_SHORT).show();
            return;
        } else while (cursor.moveToNext()) {
            listagency.add(cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME)));
        }


        endsWithArrayAdapter = new EndsWithArrayAdapter(ReturnAddQtyActivity.this, R.layout.list_item_text, R.id.list_textView_value, listagency);
        spinneragecny.setAdapter(endsWithArrayAdapter);
        cursor.close();
    }

    @SuppressLint("Range")
    private void displayAllItemsById(String agencycode, String customerCode) {
        System.out.println("inside displayallagencybyitem:" + agencycode + customerCode);
        aLodingDialog.show();
        listproduct.clear();
        //selectedproduct.clear();
        Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agencycode, customerCode.toLowerCase(),outlet, leadTime);
        if (cursor.getCount() == 0) {
            searchProductLayout.setEnabled(false);
            spinnerproducts.setEnabled(false);
            spinnerproducts.setFocusable(false);
            spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events
            spinnerproducts.setText("");

            selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
            spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
            searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
            dismissProgressDialog();
            aLodingDialog.dismiss();
            Toast.makeText(this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
            return;
        } else while (cursor.moveToNext()) {
            listproduct.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));

            searchProductLayout.setEnabled(true);
            spinnerproducts.setEnabled(true);
            spinnerproducts.setFocusable(true);
            spinnerproducts.setFocusableInTouchMode(true); // Prevents the view from gaining focus on touch events
            spinnerproducts.setText("");

            selectProductTextview.setTextColor(getResources().getColor(R.color.black));
            spinnerproducts.setTextColor(getResources().getColor(R.color.black));
            searchproductIcons.setColorFilter(getResources().getColor(R.color.black));
            endsWithArrayAdapter = new EndsWithArrayAdapter(ReturnAddQtyActivity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
            spinnerproducts.setAdapter(endsWithArrayAdapter);
            boolean alreadyExists = false;
            for (Map.Entry<String, String> entry : selectedproduct) {
                if (entry.getKey().equals(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)))) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                selectedproduct.add(new AbstractMap.SimpleEntry<>(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)), "0"));
                selectedproduct = convertListToMapEntryList(selectedproduct);
                if (addQtyAdapter == null) {
                    addQtyAdapter = new ReturnAddQtyAdapter(ReturnAddQtyActivity.this, selectedproduct);
// In your activity where you initialize the adapter and set it to the RecyclerView
                    addQtyAdapter.setQuantityChangeListener(new ReturnAddQtyAdapter.QuantityChangeListener() {
                        @Override
                        public void onTotalQuantityChanged(int totalQuantity) {
                            // Update UI or perform actions based on totalQuantity change
                            // Example: Update TextView showing total quantity
                            qtycount.setText("#Quantity: " + totalQuantity);
                        }

                        @Override
                        public void onTotalItemChanged(int totalItems) {
                            // Update UI or perform actions based on totalItems change
                            // Example: Update TextView showing total item count
                            itemcount.setText("#Item Count: " + totalItems);
                        }
                    });
                    recyclerView.setAdapter(addQtyAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ReturnAddQtyActivity.this));
                } else {
                    addQtyAdapter.updateData(selectedproduct);
                }

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (addQtyAdapter != null) {
                            addQtyAdapter.filter(newText);
                        }
                        return false;
                    }
                });
                // Print product name and quantity
                for (Map.Entry<String, String> entry : selectedproduct) {
                    System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                    totalItems = selectedproduct.size();
                }

                // initializeSwipeToDelete();
            }

            spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressLint("Range")
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    String productName = listproduct.get(position);
                    Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                            //  ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                        }
                    }
                    boolean alreadyExists = false;
                    for (Map.Entry<String, String> entry : selectedproduct) {
                        if (entry.getKey().equals(productName)) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));
                        selectedproduct = convertListToMapEntryList(selectedproduct);

                        if (addQtyAdapter == null) {
                            addQtyAdapter = new ReturnAddQtyAdapter(ReturnAddQtyActivity.this, selectedproduct);
                            addQtyAdapter.setQuantityChangeListener(new ReturnAddQtyAdapter.QuantityChangeListener() {
                                @Override
                                public void onTotalQuantityChanged(int totalQuantity) {
                                    // Update UI or perform actions based on totalQuantity change
                                    // Example: Update TextView showing total quantity
                                    qtycount.setText("#Quantity: " + totalQuantity);
                                }

                                @Override
                                public void onTotalItemChanged(int totalItems) {
                                    // Update UI or perform actions based on totalItems change
                                    // Example: Update TextView showing total item count
                                    itemcount.setText("#Item Count: " + totalItems);
                                }
                            });                            recyclerView.setAdapter(addQtyAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ReturnAddQtyActivity.this));
                        } else {
                            addQtyAdapter.updateData(selectedproduct);
                        }


                        // Print product name and quantity
                        for (Map.Entry<String, String> entry : selectedproduct) {
                            System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                            totalItems = selectedproduct.size();
                        }

                        //  initializeSwipeToDelete();
                    }

                    cursor.close();
                }

            });
        }
        cursor.close();
        selectedproduct = convertListToMapEntryList(selectedproduct);
        if (addQtyAdapter != null) {
            addQtyAdapter.updateData(selectedproduct);
        }
        aLodingDialog.cancel();
    }

    @SuppressLint("Range")
    private void displayAllItemsByAllAgencyIDS(List<String> agencyCode, String customerCode) {
        aLodingDialog.show();
        listproduct.clear();
        //selectedproduct.clear();
        for (String agcode : agencyCode) {
            System.out.println("Lead time is: " + leadTime);
            System.out.println("agcode is: " + agcode);
            System.out.println("customerCode is: " + customerCode);
            if (agcode == null || customerCode == null || leadTime == null) {
                Log.e("DB_CALL_ABORTED", "agcode=" + agcode + ", customerCode=" + customerCode + ", leadTime=" + leadTime);
                continue;
            }
            Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agcode, customerCode,outlet, leadTime);
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    listproduct.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));
                    List<String> itemNames = new ArrayList<>();
                    for (String entry : listproduct) {
                        itemNames.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));
                    }

                    searchProductLayout.setEnabled(true);
                    spinnerproducts.setEnabled(true);
                    spinnerproducts.setFocusable(true);
                    spinnerproducts.setFocusableInTouchMode(true); // Prevents the view from gaining focus on touch events
                    spinnerproducts.setText("");

                    selectProductTextview.setTextColor(getResources().getColor(R.color.black));
                    spinnerproducts.setTextColor(getResources().getColor(R.color.black));
                    searchproductIcons.setColorFilter(getResources().getColor(R.color.black));
                    endsWithArrayAdapter = new EndsWithArrayAdapter(ReturnAddQtyActivity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
                    spinnerproducts.setAdapter(endsWithArrayAdapter);
                    boolean alreadyExists = false;
                    for (Map.Entry<String, String> entry : selectedproduct) {
                        if (entry.getKey().equals(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)))) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        selectedproduct.add(new AbstractMap.SimpleEntry<>(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)), "0"));
                        selectedproduct = convertListToMapEntryList(selectedproduct);

                        if (addQtyAdapter == null) {
                            addQtyAdapter = new ReturnAddQtyAdapter(ReturnAddQtyActivity.this, selectedproduct);
                            addQtyAdapter.setQuantityChangeListener(new ReturnAddQtyAdapter.QuantityChangeListener() {
                                @Override
                                public void onTotalQuantityChanged(int totalQuantity) {
                                    // Update UI or perform actions based on totalQuantity change
                                    // Example: Update TextView showing total quantity
                                    qtycount.setText("#Quantity: " + totalQuantity);
                                }

                                @Override
                                public void onTotalItemChanged(int totalItems) {
                                    // Update UI or perform actions based on totalItems change
                                    // Example: Update TextView showing total item count
                                    itemcount.setText("#Item Count: " + totalItems);
                                }
                            });
                            recyclerView.setAdapter(addQtyAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ReturnAddQtyActivity.this));
                        } else {
                            addQtyAdapter.updateData(selectedproduct);
                        }
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                if (addQtyAdapter != null) {
                                    addQtyAdapter.filter(newText);
                                }
                                return false;
                            }
                        });
                        for (Map.Entry<String, String> entry : selectedproduct) {
                            System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                            totalItems = selectedproduct.size();
                        }

                        // initializeSwipeToDelete();
                    }
                    spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @SuppressLint("Range")
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            String productName = listproduct.get(position);
                            Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                            if (cursor.getCount() != 0) {
                                while (cursor.moveToNext()) {
                                    productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                    //  ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                }
                            }
                            boolean alreadyExists = false;
                            for (Map.Entry<String, String> entry : selectedproduct) {
                                if (entry.getKey().equals(productName)) {
                                    alreadyExists = true;
                                    break;
                                }
                            }
                            if (!alreadyExists) {
                                selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));
                                selectedproduct = convertListToMapEntryList(selectedproduct);

                                if (addQtyAdapter == null) {
                                    addQtyAdapter = new ReturnAddQtyAdapter(ReturnAddQtyActivity.this, selectedproduct);
                                    addQtyAdapter.setQuantityChangeListener(new ReturnAddQtyAdapter.QuantityChangeListener() {
                                        @Override
                                        public void onTotalQuantityChanged(int totalQuantity) {
                                            // Update UI or perform actions based on totalQuantity change
                                            // Example: Update TextView showing total quantity
                                            qtycount.setText("#Quantity: " + totalQuantity);
                                        }

                                        @Override
                                        public void onTotalItemChanged(int totalItems) {
                                            // Update UI or perform actions based on totalItems change
                                            // Example: Update TextView showing total item count
                                            itemcount.setText("#Item Count: " + totalItems);
                                        }
                                    });
                                    recyclerView.setAdapter(addQtyAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ReturnAddQtyActivity.this));
                                } else {
                                    addQtyAdapter.updateData(selectedproduct);
                                }

                                for (Map.Entry<String, String> entry : selectedproduct) {
                                    System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                                    totalItems = selectedproduct.size();
                                }

                                // initializeSwipeToDelete();
                                // Print product name and quantity

                            }

                            cursor.close();
                        }


                    });
                }
                cursor.close();

                // Update the adapter with the new data
                selectedproduct = convertListToMapEntryList(selectedproduct);
                if (addQtyAdapter != null) {
                    addQtyAdapter.updateData(selectedproduct);
                }
                aLodingDialog.cancel();
            }
        }
    }

    private List<Map.Entry<String, String>> convertListToMapEntryList(List<Map.Entry<String, String>> list) {
        Set<String> existingKeys = new HashSet<>();
        for (Map.Entry<String, String> entry : selectedproduct) {
            existingKeys.add(entry.getKey().trim());
        }

        Log.d("convertListToMapEntryList", "Existing keys before adding new entries: " + existingKeys);

        for (Map.Entry<String, String> entry : list) {
            String keyToCheck = entry.getKey().trim();
            Log.d("convertListToMapEntryList", "Checking key: " + keyToCheck);

            if (!existingKeys.contains(keyToCheck)) {
                selectedproduct.add(new AbstractMap.SimpleEntry<>(keyToCheck, "0"));
                existingKeys.add(keyToCheck);
                Log.d("convertListToMapEntryList", "Added entry: " + keyToCheck);
            } else {
                Log.d("convertListToMapEntryList", "Entry already exists: " + keyToCheck);
            }
        }

        Log.d("convertListToMapEntryList", "Final selectedproduct list: ");
        for (Map.Entry<String, String> entry : selectedproduct) {
            Log.d("convertListToMapEntryList", "Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
        }

        return selectedproduct;
    }






    @SuppressLint({"Range", "StaticFieldLeak"})
    private void syncOrders(String orderId, String outlet, String date, String CustomerCode, String expectedDate, String leadTime) {

        //showProgressDialog();
        // Create request parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("orderid", orderId);
        params.put("user_id", userID);
        params.put("van_id", vanID);
        // Join product IDs
        String joinedProductIds = String.join(",", onlineProductID);
        params.put("item_ids", joinedProductIds);

// Join item codes
        String joinedItemCodes = String.join(",", onlineItemCode);
        params.put("item_codes", joinedItemCodes);

        String joinedAgencyIds = String.join(",", onlinelistagencyids);
        params.put("agency_id", joinedAgencyIds);
// Join quantities
      /*  StringBuilder quantitiesBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : selectedproduct) {
            if (quantitiesBuilder.length() > 0) {
                quantitiesBuilder.append(",");
            }
            quantitiesBuilder.append(entry.getValue());
        }
        String joinedQuantities = quantitiesBuilder.toString();*/
        String joinedQuantities = String.join(",", onlineReqQtys);
        params.put("quantities", joinedQuantities);

        params.put("outlet_id", outlet);
        params.put("ordered_datetime", date);
        params.put("orderStatus", "NEW ORDER");
        params.put("createdBy", userID);
        params.put("expectedDelivery", expectedDate);
        params.put("lead_time", leadTime);

        System.out.println("params" + params);
        // Create the network request
        String url = ApiLinks.submitOrder;
        Call<OrderDetailsResponse> updateCall = apiInterface.submitOrder(url, params);

        // Associate each request with the corresponding database row
        updateCall.enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                System.out.println("response code:" + response.code());
                System.out.println("status" + response.body().getStatus());
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    // Extract data from the response if needed
                    // Update the corresponding database row
                    //  Toast.makeText(AddQuantity.this, "Order Success", Toast.LENGTH_SHORT).show();
                    //     submitOrderDB.submitDetails(orderID, userID, vanID,  outletID, productIdQty,"Not Synced", dateFormat.format(date));
                    submitOrderDB.onlineSubmitOrderDetails(orderId, userID, vanID, outlet, joinedProductIds, joinedAgencyIds, joinedItemCodes, joinedQuantities, "synced", "online", CustomerCode, date, expectedDate, "0");
                    //Toast.makeText(AddQuantity.this, "Order SuccessFull "+orderId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReturnAddQtyActivity.this, AddItemsActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                displayAlert("Alert", t.getMessage());
            }
        });


    }

    private void showLeadTimeDialog() {
        // Create an AlertDialog.Builder
        List<String> leadtimes = new LinkedList<>();

        Cursor cursor = itemsByAgencyDB.getLeadTimes();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String leadTime = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_LEAD_TIME));
                if (!leadtimes.contains(leadTime)) {
                    leadtimes.add(leadTime);
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_leadtime, null);

        // Set the custom layout as the dialog's view
        builder.setView(dialogView);
        builder.setCancelable(false);
        // Get the RadioGroup from the inflated layout
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupLeadTime);

        // Dynamically create radio buttons based on the leadtimes list
        for (int i = 0; i < leadtimes.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(leadtimes.get(i));
            radioGroup.addView(radioButton);
        }

        // Add the buttons
        builder.setTitle("Choose Lead-time for the products");
        builder.setPositiveButton("OK", null); // Set to null. We override later to prevent auto-dismiss
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i = new Intent(ReturnAddQtyActivity.this, ReturnActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Create the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button click listener to prevent dismissing the dialog when leadTime is null
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                if (selectedRadioButton != null) {
                    leadTime = selectedRadioButton.getText().toString();
                    // Handle the selected lead time (e.g., display it, use it somewhere, etc.)
                    Toast.makeText(getApplicationContext(), "Selected Lead Time: " + leadTime, Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss the dialog if a lead time is selected
                } else {
                    // Show a toast message to prompt the user to select a lead time
                    Toast.makeText(getApplicationContext(), "Please select a lead time!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReturnAddQtyActivity.this, ReturnActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        listproduct.clear();
        listagency.clear();
        selectedproduct.clear();
        listOutletIDs.clear();
        itemsByAgencyDB = null;
        submitOrderDB = null;
        allAgencyDetailsDB = null;
        adapter = null;
        spinneragecny = null;
        spinnerproducts = null;
        ItemCode = null;
        agencycode = null;
        addQtyAdapter = null;
        orderConfrimBeans.clear();
        recyclerView = null;
        agencyID = null;
        productID = null;
        quantity = null;
        outlet = null;
        productIdQty = null;
        prodqty = null;
        getProdqty = null;
        submittedorder = null;
        submit = null;
        onlineProductBeanList.clear();

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
    public void onTotalQuantityChanged(int totalQuantity) {
        TextView totalQtyTextView = findViewById(R.id.qtycount);

        totalQtyTextView.setText("#Quantity: " + totalQuantity);

    }


    @Override
    public void onTotalItemChanged(int totalItems) {
        TextView itemCount = findViewById(R.id.itemcount);
        itemCount.setText("#Item Count: " + totalItems);
    }

}

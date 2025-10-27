package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.AddItemsActivity.customercode;
import static com.malta_mqf.malta_mobile.AddItemsActivity.listOutletIDs;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderConfrimSpinnerAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.OnlineProductBean;
import com.malta_mqf.malta_mobile.Model.OrderConfrimBean;
import com.malta_mqf.malta_mobile.Model.OrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OutletAssociatedSKUAgency;
import com.malta_mqf.malta_mobile.Model.OutletAssociatedSKUAgencyResponse;
import com.malta_mqf.malta_mobile.Model.OutletSkuItem;
import com.malta_mqf.malta_mobile.Model.OutletSkuResponse;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class AddQuantity extends BaseActivity implements AddQtyAdapter.QuantityChangeListener {

    public static List<Map.Entry<String, String>> selectedproduct = new LinkedList<>();
    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                // You can leave this empty if you handle the date selection in the OK button's listener
            };
    AutoCompleteTextView spinneragecny;
    AutoCompleteTextView spinnerproducts;
    SearchView searchView;
    TextView qtycount, itemcount;
    GetCusOutletAgencyProductAdapter adapter;
    ALodingDialog aLodingDialog;
    List<String> listagency;
    List<String> listproduct;
    AddQtyAdapter addQtyAdapter;
    RecyclerView recyclerView;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    String agencycode;
    String agencyID, productID, quantity, ItemCode;
    List<String> submittedorder;
    Set<ProductInfo> productIdQty;
    ImageView searchitembyagency, searchproductIcons;
    List<String> prodqty;
    List<String> getProdqty;
    Button submit;
    SubmitOrderDB submitOrderDB;
    String outlet;
    String customerCode;
    String agency;
    List<String> onlineProductID;
    List<String> onlineItemCode;
    List<String> onlineoutletassosiatedItemCode;
    List<String> onlinelistagencyids;
    List<String> onlineReqQtys;
    List<OnlineProductBean> onlineProductBeanList;
    EndsWithArrayAdapter endsWithArrayAdapter;
    TextView outletname_header, selectProductTextview;
    Toolbar toolbar;
    LinearLayout searchProductLayout;
    int totalQuantity, totalItems;
    String leadTime;
    ArrayAdapter<String> adapter1;
    List<OrderConfrimBean> orderConfrimBeans;
    private DatePickerDialog datePickerDialog;
    private Dialog dialog;
    private List<Map.Entry<String, String>> itemList;
    private ItemTouchHelper itemTouchHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quantity);
        itemList = new ArrayList<>();
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

        Toast.makeText(this, "Add Items Here!", Toast.LENGTH_SHORT).show();
        spinneragecny = findViewById(R.id.spinnerLeft);
        spinnerproducts = findViewById(R.id.spinnerRight);
        searchitembyagency = findViewById(R.id.search_icon);
        recyclerView = findViewById(R.id.listaddproducts);
        searchproductIcons = findViewById(R.id.search_icon2);
        searchProductLayout = findViewById(R.id.searchproductLayout);
        selectProductTextview = findViewById(R.id.selectOutletTextView);
        searchView = findViewById(R.id.searchView);
        qtycount = findViewById(R.id.qtycount);
        itemcount = findViewById(R.id.itemcount);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        showLeadTimeDialog();
        aLodingDialog = new ALodingDialog(this);
        submit = findViewById(R.id.submitorder);
        submit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        submitOrderDB = new SubmitOrderDB(this);
        outletname_header = findViewById(R.id.outletname_header);
        outlet = getIntent().getStringExtra("outletId");
        customerCode = getIntent().getStringExtra("customerCode");
        System.out.println("customerCode:" + customercode);
        // outletname_header.setText();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent().getStringExtra("outletName") == null) {
            getSupportActionBar().setTitle("ADD ITEMS ");
        } else {
            getSupportActionBar().setTitle("ADD ITEMS " + getIntent().getStringExtra("outletName"));

        }
        System.out.println("OutletID in add qty: " + outlet);
        //   getAllAgency();

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
        //  displayAllAgency();
        if (isOnline()) {
            getAllAgency(listOutletIDs);
            System.out.println("online");
        } else {
            displayAllAgency();
        }

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listproduct);
        recyclerView.setAdapter(addQtyAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    addQtyAdapter.filter(newText);
                } catch (NullPointerException e) {

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




        /*searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(AddQuantity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();
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
                  //  displayAllItemsByAllAgencyIDS(agencyCodes, customercode.toLowerCase());
                    if (isOnline()) {
                        getAllItemByAllAgencyId(agencyCodes);
                    } else {
                        displayAllItemsByAllAgencyIDS(agencyCodes, customercode.toLowerCase());
                    }

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
                              //  displayAllItemsById(agencycode, customercode);
                                if (isOnline()) {
                                    getAllItemById();
                                    System.out.println("online");
                                } else {
                                    displayAllItemsById(agencycode, customercode);
                                }
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
       /* spinneragecny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectedproduct.clear();
                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(AddQuantity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();
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
                    if (isOnline()) {
                        getAllItemByAllAgencyId(agencyCodes);
                    } else {
                        displayAllItemsByAllAgencyIDS(agencyCodes, customercode);
                    }

                } else {
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
                    // getAllItemById();
                    listproduct.clear();
                    if (isOnline()) {
                        getAllItemById();
                        System.out.println("online");
                    } else {
                        displayAllItemsById(agencycode, customercode);
                    }
                    // displayAllItemsById(agencycode);
                    cursor.close();
                }
            }
        });*/


        spinneragecny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(AddQuantity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();
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
                    //  displayAllItemsByAllAgencyIDS(agencyCodes,customercode);
                    if (isOnline()) {
                        getAllItemByAllAgencyId(agencyCodes, listOutletIDs);
                    } else {
                        displayAllItemsByAllAgencyIDS(agencyCodes, listOutletIDs, customercode);
                    }

                } else {
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
                    // getAllItemById();
                    listproduct.clear();
                    // displayAllItemsById(agencycode,customercode);
                    if (isOnline()) {
                        System.out.println();
                        getAllItemById(listOutletIDs);
                        System.out.println("online");
                    } else {
                        displayAllItemsById(agencycode, listOutletIDs, customercode);
                    }
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

                if (selectedproduct.size() > 0) {
                    for (Map.Entry<String, String> entry : selectedproduct) {
                        // System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                        if (!entry.getValue().equals("0") && !entry.getValue().isEmpty()) {
                            OrderConfrimBean orderConfrimBean = new OrderConfrimBean();
                            orderConfrimBean.setProductName(entry.getKey());
                            orderConfrimBean.setProductsQty(entry.getValue());
                            if (!orderConfrimBeans.contains(orderConfrimBean)) {
                                orderConfrimBeans.add(orderConfrimBean);
                            }

                        }

                    }
                    showConfirmOrders();

                } else {
                    Toast.makeText(AddQuantity.this, "Please add the items before confirming the order", Toast.LENGTH_SHORT).show();
                }

            }


        });
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
                        //  addQtyAdapter.setSelectedPosition(finalPosition);
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

    /*
        private void display() {
            Cursor cursor = submitOrderDB.readAllData();
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String productId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    System.out.println("product"+productId);
                    Cursor cursor1 = itemsByAgencyDB.readProdcutDataByproductId(productId);
                    while (cursor1.moveToNext()) {
                        @SuppressLint("Range") String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        @SuppressLint("Range") String agencyId = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                        System.out.println("productName: "+productName +"agencyId: "+agencyId);
                        Cursor cursor2 = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);
                        while (cursor2.moveToNext()) {
                            @SuppressLint("Range") String agencyName = cursor2.getString(cursor2.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));
                            System.out.println("agencyName"+agencyName);
                            System.out.println("Product Name: " + productName + ", Agency Name: " + agencyName);
                            submittedorder.add("Product Name: " + productName + ", Agency Name: " + agencyName);
                        }
                    }
                }
            }
            System.out.println("the itmes"+submittedorder);
        }*/
    private void showConfirmOrders() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Dismiss any existing dialog before showing a new one
                if (aLodingDialog != null && aLodingDialog.isShowing()) {
                    aLodingDialog.dismiss();
                }

                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) AddQuantity.this.getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);

                dialog = new Dialog(AddQuantity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm_order);

                // Set the desired width and height
                int dialogWidth = (int) (displayMetrics.widthPixels * 1.0); // 100% of screen width
                int dialogHeight = (int) (displayMetrics.heightPixels * 0.7); // 70% of screen height

                dialog.getWindow().setLayout(dialogWidth, dialogHeight);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                ListView listView = dialog.findViewById(R.id.dialog_order_items_listView);
                TextView mealService = dialog.findViewById(R.id.mealSerViceDia);
                Button confirm = dialog.findViewById(R.id.btn_confirm);
                Button edit = dialog.findViewById(R.id.btn_edit);
                confirm.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                edit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setupDatePicker();
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderConfrimBeans.clear();
                        dialog.dismiss(); // Properly dismiss the dialog
                    }
                });

                mealService.setText("CONFIRM ORDER");
                OrderConfrimSpinnerAdapter confrimOrderAdapter = new OrderConfrimSpinnerAdapter(AddQuantity.this, orderConfrimBeans);
                listView.setAdapter(confrimOrderAdapter);
                confrimOrderAdapter.notifyDataSetChanged();
                dialog.show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss(); // Dismiss dialog to prevent leaks
        }
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the default date to tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1 + Integer.parseInt(leadTime)); // Adds 1 day to the current date for default selection (tomorrow)
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH);
        int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme, // Custom theme
                onDateSetListener,
                defaultYear,  // Set default year to tomorrow
                defaultMonth, // Set default month to tomorrow
                defaultDay    // Set default day to tomorrow
        );
        datePickerDialog.setTitle("Select expected delivery date");

        // Set minimum date to today (so today is selectable)
        Calendar minDate = Calendar.getInstance();  // Get today's date
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

    @SuppressLint({"StaticFieldLeak", "Range"})
    private boolean processOrder(final String outletID, final String selectedDate) {
        String orderID;
        String CUSTOMERCODE;
        String processedCustomerCode = processCustomerCode(customerCode);

        if (customercode == null) {
            orderID = processedCustomerCode.toUpperCase() + outletID + generateRandomOrderID() + "-M";
            CUSTOMERCODE = customerCode;
        } else {
            orderID = processCustomerCode(customercode).toUpperCase() + outletID + generateRandomOrderID() + "-M";
            CUSTOMERCODE = customercode;
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (isOnline()) {
            // --- ONLINE PROCESSING ---
            int count = 0;

            for (OnlineProductBean onlineProductBean : onlineProductBeanList) {
                for (Map.Entry<String, String> entry : selectedproduct) {
                    String selectedProductName = entry.getKey();
                    String selectedQty = entry.getValue();

                    // Skip 0 or empty quantities
                    if (!selectedProductName.equals(onlineProductBean.getProductName()) ||
                            selectedQty.equals("0") || selectedQty.isEmpty()) {
                        continue;
                    }

                    // ✅ Check if the item is associated with the outlet
                    Cursor cursor = itemsByAgencyDB.checkItemAssociatedWithOutlet(outletID, onlineProductBean.getItemCode());

                    if (cursor != null && cursor.moveToFirst()) {
                        count++;
                        if (!onlineProductID.contains(onlineProductBean.getProductId())) {
                            onlineProductID.add(onlineProductBean.getProductId());
                            onlineItemCode.add(onlineProductBean.getItemCode());
                            onlinelistagencyids.add(onlineProductBean.getAgencydid());
                            onlineReqQtys.add(selectedQty);
                            System.out.println("✅ Added associated item: " + onlineProductBean.getItemCode());
                        }
                    } else {
                        // ❌ Item not associated, skip
                        System.out.println("❌ Skipped unassociated item: " + onlineProductBean.getItemCode());
                    }

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }

            if (count == 0) {
                showToastOnMainThread("No valid associated items found or quantity is 0!");
                return false;
            }

            // Proceed with syncing only if items are valid
            syncOrders(orderID, outletID, dateFormat.format(date), CUSTOMERCODE, selectedDate, leadTime);

        } else {
            // --- OFFLINE PROCESSING ---
            productIdQty.clear();
            int count = 0;
            Set<String> addedItemCodes = new HashSet<>();

            for (Map.Entry<String, String> entry : selectedproduct) {
                String selectedProductName = entry.getKey();
                String selectedQty = entry.getValue();

                if (selectedQty == null || selectedQty.trim().equals("") || selectedQty.equals("0")) {
                    System.out.println("❌ Skipped zero or empty quantity for: " + selectedProductName);
                    continue;
                }
                System.out.println("selectedProductName going inside itemdb is :" + selectedProductName);

                // ✅ Fetch single product entry from DB by name
                Cursor productCursor = itemsByAgencyDB.readProdcutDataByName(selectedProductName);
                if (productCursor != null && productCursor.moveToFirst()) {
                    String productID = productCursor.getString(productCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    String itemCode = productCursor.getString(productCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                    String agencyCode = productCursor.getString(productCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));

                    // ✅ Only process unique item codes
                    if (addedItemCodes.contains(itemCode)) {
                        System.out.println("⚠️ Skipped duplicate itemCode: " + itemCode);
                        productCursor.close();
                        continue;
                    }

                    // ✅ Check association
                    Cursor checkCursor = itemsByAgencyDB.checkItemAssociatedWithOutlet(outletID, itemCode);
                    int associationCount = (checkCursor != null) ? checkCursor.getCount() : 0;
                    System.out.println("checkCursor count for " + itemCode + " is: " + associationCount);

                    if (associationCount > 0) {
                        addedItemCodes.add(itemCode);
                        productIdQty.add(new ProductInfo(productID, agencyCode, itemCode, selectedQty));
                        System.out.println("productIdQty" + productIdQty);
                        count++;
                        System.out.println("count" + count);
                        System.out.println("✅ Added associated offline item: " + itemCode);
                    } else {
                        System.out.println("❌ Skipped unassociated offline item: " + itemCode);
                    }

                    if (checkCursor != null) checkCursor.close();
                }

                if (productCursor != null) productCursor.close();
            }

            if (count == 0) {
                showToastOnMainThread("No valid associated items found or quantity is 0!");
                return false;
            }

            if (!productIdQty.isEmpty()) {
                submitOrderDB.submitDetails(orderID, userID, vanID, outletID, productIdQty, "Not Synced",
                        CUSTOMERCODE, dateFormat.format(date), selectedDate, leadTime);
            }

        }

        return true; // ✅ success
    }

//    private boolean processOrder(final String outletID, final String selectedDate) {
//        String orderID;
//        String CUSTOMERCODE;
//        String processedCustomerCode = processCustomerCode(customerCode);
//
//        if (customercode == null) {
//            orderID = processedCustomerCode.toUpperCase() + outletID + String.valueOf(generateRandomOrderID()) + "-M";
//            CUSTOMERCODE = customerCode;
//        } else {
//            orderID = processCustomerCode(customercode).toUpperCase() + outletID + String.valueOf(generateRandomOrderID()) + "-M";
//            CUSTOMERCODE = customercode;
//        }
//
//        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        if (isOnline()) {
//            // Online processing
//           int count = 0;
//            for (OnlineProductBean onlineProductBean : onlineProductBeanList) {
//                for (Map.Entry<String, String> entry : selectedproduct) {
//                    if (entry.getKey().equals(onlineProductBean.getProductName()) && !entry.getValue().equals("0")  && !entry.getValue().isEmpty()) {
//                        count++;
//                        if (!onlineProductID.contains(onlineProductBean.getProductId())) {
//                            onlineProductID.add(onlineProductBean.getProductId());
//                            onlineItemCode.add(onlineProductBean.getItemCode());
//                            System.out.println("itemcode are "+onlineItemCode);
//                            onlinelistagencyids.add(onlineProductBean.getAgencydid());
//                            onlineReqQtys.add(entry.getValue());
//                        }
//                    }
//                }
//            }
//           /* int count = 0;
//            for (Map.Entry<String, String> entry : selectedproduct) {
//                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
//                if (cursor.getCount() != 0) {
//                    while (cursor.moveToNext()) {
//                        productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
//                        ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
//                        agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
//                        if (!entry.getValue().equalsIgnoreCase("0")) {
//                            count++;
//                            productIdQty.add(new ProductInfo(productID, agencycode, ItemCode, entry.getValue()));
//                        }
//                    }
//                }
//                cursor.close();
//            }
//            if (count == 0) {
//                showToastOnMainThread("Items Quantity cannot be 0!");
//                return false;
//            }
//*/
//            if (count == 0) {
//                showToastOnMainThread("Items Quantity cannot be 0!");
//                return false;
//            }
//            syncOrders(orderID, outletID, dateFormat.format(date), CUSTOMERCODE, selectedDate, leadTime);
//        } else {
//            // Offline processing
//            int count = 0;
//            for (Map.Entry<String, String> entry : selectedproduct) {
//                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
//                if (cursor.getCount() != 0) {
//                    while (cursor.moveToNext()) {
//                        productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
//                        ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
//                        agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
//                        if (!entry.getValue().equalsIgnoreCase("0") && !entry.getValue().isEmpty()) {
//                            count++;
//                            productIdQty.add(new ProductInfo(productID, agencycode, ItemCode, entry.getValue()));
//                        }
//                    }
//                }
//                cursor.close();
//            }
//            if (count == 0) {
//                showToastOnMainThread("Items Quantity cannot be 0!");
//                return false;
//            }
//            if (productIdQty.size() != 0) {
//                submitOrderDB.submitDetails(orderID, userID, vanID, outletID, productIdQty, "Not Synced", CUSTOMERCODE, dateFormat.format(date), selectedDate, leadTime);
//          // selectedproduct.clear();
//            }
//        }
//
//        // If successful, return true
//        return true;
//    }

    // Method to show toast on the main thread
    private void showToastOnMainThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AddQuantity.this, message, Toast.LENGTH_SHORT).show();
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

    private void getAllAgency(List<String> outletsid) {

        String outletids = TextUtils.join(",", outletsid);
        System.out.println(" outlets are in getall itembyid is :" + outletids);

        String outletEncoded = "";
        try {

            outletEncoded = URLEncoder.encode(outletids, "UTF-8");
            System.out.println(" outlets are in getall itembyid is :" + outletEncoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = ApiLinks.get_outlet_associated_skus_agency + "?ou_id=" + outletEncoded;
        Log.d("TAG", "getAllAgency: " + url);

        Call<OutletAssociatedSKUAgencyResponse> call = apiInterface.OutletAssociatedSKUAgencyResponse(url);

        call.enqueue(new Callback<OutletAssociatedSKUAgencyResponse>() {
            @Override
            public void onResponse(Call<OutletAssociatedSKUAgencyResponse> call, Response<OutletAssociatedSKUAgencyResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        OutletAssociatedSKUAgencyResponse body = response.body();

                        if ("yes".equalsIgnoreCase(body.getStatus())) {
                            List<OutletAssociatedSKUAgency> agencyList = body.getOutletAssociatedSKUAgencyList(); // ✅ change this to match your model field name
                            listagency.clear();
                            listagency.add("ALL");

                            for (OutletAssociatedSKUAgency agency : agencyList) {
                                listagency.add(agency.getAgencyName());
                            }

                            endsWithArrayAdapter = new EndsWithArrayAdapter(
                                    AddQuantity.this,
                                    R.layout.list_item_text,
                                    R.id.list_textView_value,
                                    listagency
                            );
                            spinneragecny.setAdapter(endsWithArrayAdapter);
                        } else {
                            Log.e("TAG", "API status not OK: " + body.getStatus());
                        }
                    } else {
                        Log.e("TAG", "Response failed: " + response.message());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TAG", "Exception in getAllAgency: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<OutletAssociatedSKUAgencyResponse> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", "Network error: " + t.getMessage());
            }
        });
    }

    @SuppressLint("Range")
    private void displayAllAgency() {
        listagency.add("ALL");
        for (String outlets : listOutletIDs) {
            System.out.println("display all agency is :" + outlets);
            List<String> associatedAgencyCodes = itemsByAgencyDB.outassosiatedagencies(outlets);
            if (associatedAgencyCodes.isEmpty()) {
                Toast.makeText(this, "No associated agencies for this outlet", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> agencyNames = allAgencyDetailsDB.getAgencyNamesByCodes(associatedAgencyCodes);

            if (agencyNames.isEmpty()) {
                Toast.makeText(this, "No agency data found", Toast.LENGTH_SHORT).show();
                return;
            }

            listagency.addAll(agencyNames);
            System.out.println("all agencyies from all outlets are :" + listagency);

//        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
//        if (cursor.getCount() == 0) {
//            Toast.makeText(this, "No Agency data", Toast.LENGTH_SHORT).show();
//            return;
//        } else while (cursor.moveToNext()) {
//            listagency.add(cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME)));
//        }

        }
        Set<String> uniqueAgencies = new LinkedHashSet<>(listagency);
        listagency.clear();
        listagency.addAll(uniqueAgencies);
        System.out.println("filtered agencies are :" + listagency);

        endsWithArrayAdapter = new EndsWithArrayAdapter(AddQuantity.this, R.layout.list_item_text, R.id.list_textView_value, listagency);
        spinneragecny.setAdapter(endsWithArrayAdapter);

    }

    private void getAllItemById(List<String> outletsid) {
        showProgressDialog();
        String outletids = TextUtils.join(",", outletsid);
        System.out.println(" outlets are in getall itembyid is :" + outletids);
        String agencyEncoded = "";
        String outletencoded = "";
        try {
            agencyEncoded = URLEncoder.encode(agencycode, "UTF-8");
            outletencoded = URLEncoder.encode(outletids, "UTF-8");
            System.out.println(" outlets are in getall itembyid is :" + outletencoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = ApiLinks.get_outlet_associated_skus_for_agency
                + "?outlet_id=" + outletencoded
                + "&agency_id=" + agencyEncoded;

        Log.d("TAG", "getAllItemById: " + url);

        Call<OutletSkuResponse> call = apiInterface.outletskuassosiate(url);
        call.enqueue(new Callback<OutletSkuResponse>() {
            @Override
            public void onResponse(Call<OutletSkuResponse> call, Response<OutletSkuResponse> response) {
                dismissProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    OutletSkuResponse outletSkuResponse = response.body();

                    if ("yes".equalsIgnoreCase(outletSkuResponse.getStatus())) {
                        List<OutletSkuItem> items = outletSkuResponse.getItems();
                        if (items != null && !items.isEmpty()) {
//                            onlineProductBeanList.clear();
//                            listproduct.clear();
//                            selectedproduct.clear();

                            for (OutletSkuItem item : items) {
                                if (agency.equals(item.getAgencyName())
                                        && customercode.equalsIgnoreCase(item.getCustomerCode())
                                        && leadTime.equals(item.getLeadTime())) {

                                    // Add to online product list
                                    onlineProductBeanList.add(new OnlineProductBean(
                                            item.getItemName(),
                                            item.getId(),
                                            item.getItemCode(),
                                            item.getAgencyCode()
                                    ));

                                    // Add to product names list
                                    listproduct.add(item.getItemName());

                                    // Add to selected product with quantity 0 if not already present
                                    boolean alreadyExists = false;
                                    for (Map.Entry<String, String> entry : selectedproduct) {
                                        if (entry.getKey().equals(item.getItemName())) {
                                            alreadyExists = true;
                                            break;
                                        }
                                    }
                                    if (!alreadyExists) {
                                        selectedproduct.add(new AbstractMap.SimpleEntry<>(item.getItemName(), "0"));
                                    }
                                }
                            }

                            // Update UI components
                            spinnerproducts.setEnabled(true);
                            searchProductLayout.setEnabled(true);
                            spinnerproducts.setText("");
                            selectProductTextview.setTextColor(getResources().getColor(R.color.black));
                            spinnerproducts.setTextColor(getResources().getColor(R.color.black));
                            searchproductIcons.setColorFilter(getResources().getColor(R.color.black));

                            endsWithArrayAdapter = new EndsWithArrayAdapter(AddQuantity.this,
                                    R.layout.list_item_text, R.id.list_textView_value, listproduct);
                            spinnerproducts.setAdapter(endsWithArrayAdapter);

                            // Setup RecyclerView adapter
                            if (addQtyAdapter == null) {
                                addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                addQtyAdapter.setQuantityChangeListener(AddQuantity.this);
                                recyclerView.setAdapter(addQtyAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
                            } else {
                                addQtyAdapter.updateData(selectedproduct);
                            }

                            // Spinner item click
                            spinnerproducts.setOnItemClickListener((adapterView, view, position, l) -> {
                                String productName = listproduct.get(position);

                                boolean alreadyExists = false;
                                for (Map.Entry<String, String> entry : selectedproduct) {
                                    if (entry.getKey().equals(productName)) {
                                        alreadyExists = true;
                                        break;
                                    }
                                }
                                if (!alreadyExists) {
                                    selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));
                                    addQtyAdapter.updateData(selectedproduct);
                                }
                            });

                        } else {
                            disableProductSelection();
                            Toast.makeText(AddQuantity.this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        disableProductSelection();
                        Toast.makeText(AddQuantity.this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dismissProgressDialog();
                    displayAlert("Alert", "Failed to fetch data from server");
                }
            }

            @Override
            public void onFailure(Call<OutletSkuResponse> call, Throwable t) {
                dismissProgressDialog();
                Log.d("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", t.getMessage());
            }
        });
    }

    // Helper method to disable product selection UI
    private void disableProductSelection() {
        searchProductLayout.setEnabled(false);
        spinnerproducts.setEnabled(false);
        spinnerproducts.setFocusable(false);
        spinnerproducts.setFocusableInTouchMode(false);
        spinnerproducts.setText("");

        selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
        spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
        searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
    }

    private void getAllItemByAllAgencyId(List<String> agencyco_de, List<String> outletsid) {
        showProgressDialog();
        String agencies = TextUtils.join(",", agencyco_de);
        String outletids = TextUtils.join(",", outletsid);
        System.out.println(" outlets are in getall itembyid is :" + outletids);
        String agencyEncoded = "";
        String outletEncoded = "";
        try {
            agencyEncoded = URLEncoder.encode(agencies, "UTF-8"); // encode agency name
            outletEncoded = URLEncoder.encode(outletids, "UTF-8");
            System.out.println(" outlets are in getall itembyid is :" + outletEncoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = ApiLinks.get_outlet_associated_skus_for_agency
                + "?outlet_id=" + outletEncoded
                + "&agency_id=" + agencyEncoded;
        // Call API for this URL...
        Log.d("TAG", "getAllItemById: " + url);


        Call<OutletSkuResponse> call = apiInterface.outletskuassosiate(url);
        call.enqueue(new Callback<OutletSkuResponse>() {
            @Override
            public void onResponse(Call<OutletSkuResponse> call, Response<OutletSkuResponse> response) {
                dismissProgressDialog(); // Always dismiss first

                if (!response.isSuccessful() || response.body() == null) {
                    displayAlert("Error", "Failed to get valid response from server.");
                    return;
                }

                OutletSkuResponse allItemDeatilsById = response.body();

                List<OutletSkuItem> allItemDetailResponseByIds = allItemDeatilsById.getItems();
                try {
                    int count = 0;
                    for (OutletSkuItem allItemDetailResponseById : allItemDetailResponseByIds) {
                        for (String agencycode : agencyco_de) {
                            if ((agencycode.equals(allItemDetailResponseById.getAgencyCode())) && customercode.equalsIgnoreCase(allItemDetailResponseById.getCustomerCode()) && leadTime.equals(allItemDetailResponseById.getLeadTime())) {
                                count++;
                                onlineProductBeanList.add(new OnlineProductBean(allItemDetailResponseById.getItemName(), allItemDetailResponseById.getId(), allItemDetailResponseById.getItemCode(), allItemDetailResponseById.getAgencyCode()));
                                listproduct.add(allItemDetailResponseById.getItemName());
                                searchProductLayout.setEnabled(true);
                                spinnerproducts.setEnabled(true);
                                spinnerproducts.setFocusable(true);
                                spinnerproducts.setFocusableInTouchMode(true); // Prevents the view from gaining focus on touch events
                                spinnerproducts.setText("");

                                selectProductTextview.setTextColor(getResources().getColor(R.color.black));
                                spinnerproducts.setTextColor(getResources().getColor(R.color.black));
                                searchproductIcons.setColorFilter(getResources().getColor(R.color.black));
                                endsWithArrayAdapter = new EndsWithArrayAdapter(AddQuantity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
                                spinnerproducts.setAdapter(endsWithArrayAdapter);
                                boolean alreadyExists = false;
                                for (Map.Entry<String, String> entry : selectedproduct) {
                                    if (entry.getKey().equals(allItemDetailResponseById.getItemName())) {
                                        alreadyExists = true;
                                        break;
                                    }
                                }
                                if (!alreadyExists) {
                                    selectedproduct.add(new AbstractMap.SimpleEntry<>(allItemDetailResponseById.getItemName(), "0"));
                                    selectedproduct = convertListToMapEntryList(selectedproduct);
                                    /*if (addQtyAdapter != null) {
                                        addQtyAdapter.updateData(selectedproduct);
                                    }*/
                                    if (addQtyAdapter == null) {
                                        addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                        addQtyAdapter.setQuantityChangeListener(AddQuantity.this);
                                        recyclerView.setAdapter(addQtyAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
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
                                spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @SuppressLint("Range")
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                        String productName = listproduct.get(position);
                                        Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                                        if (cursor.getCount() != 0) {
                                            while (cursor.moveToNext()) {
                                                productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
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
                                                addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                                addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter.QuantityChangeListener() {
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
                                                recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
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
                        }
                    }

                    if (count == 0) {
                        searchProductLayout.setEnabled(false);
                        spinnerproducts.setEnabled(false);
                        spinnerproducts.setFocusable(false);
                        spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events
                        spinnerproducts.setText("");

                        selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                        spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                        searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));

                        Toast.makeText(AddQuantity.this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
                    }
                    selectedproduct = convertListToMapEntryList(selectedproduct);
                    if (addQtyAdapter != null) {
                        addQtyAdapter.updateData(selectedproduct);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dismissProgressDialog();  // Ensure progress dialog is dismissed in both cases
                }
            }

            @Override
            public void onFailure(Call<OutletSkuResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", t.getMessage());
                dismissProgressDialog();  // Dismiss the progress dialog in case of failure

            }
        });
    }

    @SuppressLint("Range")
    private void displayAllItemsById(String agencycode, List<String> outletsid, String customerCode) {

        showProgressDialog();
        listproduct.clear();
        System.out.println("outlets are in displayAllItemsById" + outletsid);
        for (String outletid : outletsid) {
            System.out.println("outlet is in displayAllItemsById" + outletid);
            System.out.println("inside displayallagencybyitem:" + agencycode + customerCode + outletid);
            Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agencycode, customerCode.toLowerCase(), outletid, leadTime);
            System.out.println("agency item cursor count is " + cursor.getCount());
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
                Toast.makeText(this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();

            } else while (cursor.moveToNext()) {
                System.out.println("cursor count is: " + cursor.getCount());
                listproduct.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));

                searchProductLayout.setEnabled(true);
                spinnerproducts.setEnabled(true);
                spinnerproducts.setFocusable(true);
                spinnerproducts.setFocusableInTouchMode(true); // Prevents the view from gaining focus on touch events
                spinnerproducts.setText("");

                selectProductTextview.setTextColor(getResources().getColor(R.color.black));
                spinnerproducts.setTextColor(getResources().getColor(R.color.black));
                searchproductIcons.setColorFilter(getResources().getColor(R.color.black));
                endsWithArrayAdapter = new EndsWithArrayAdapter(AddQuantity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
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
                        addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
// In your activity where you initialize the adapter and set it to the RecyclerView
                        addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter.QuantityChangeListener() {
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
                        recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
                    } else {
                        addQtyAdapter.updateData(selectedproduct);
                    }


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
                                addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter.QuantityChangeListener() {
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
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
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
            dismissProgressDialog();
        }
    }

    @SuppressLint("Range")
    private void displayAllItemsByAllAgencyIDS(List<String> agencyCode, List<String> outletsid, String customerCode) {
        showProgressDialog();
        listproduct.clear();
        System.out.println("Lead time is: " + leadTime);
        for (String outletid : outletsid) {
            System.out.println("outlet id are: " + outletid);
            for (String agcode : agencyCode) {
                System.out.println("agency codes are :" + agcode + outletid);
                Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agcode, customerCode, outletid, leadTime);
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
                        endsWithArrayAdapter = new EndsWithArrayAdapter(AddQuantity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
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
                                addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter.QuantityChangeListener() {
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
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
                            } else {
                                addQtyAdapter.updateData(selectedproduct);
                            }

                            for (Map.Entry<String, String> entry : selectedproduct) {
                                System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                                totalItems = selectedproduct.size();
                            }

                            //  initializeSwipeToDelete();
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
                                        addQtyAdapter = new AddQtyAdapter(AddQuantity.this, selectedproduct);
                                        addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter.QuantityChangeListener() {
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
                                        recyclerView.setLayoutManager(new LinearLayoutManager(AddQuantity.this));
                                    } else {
                                        addQtyAdapter.updateData(selectedproduct);
                                    }

                                    for (Map.Entry<String, String> entry : selectedproduct) {
                                        System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                                        totalItems = selectedproduct.size();
                                    }

                                    //   initializeSwipeToDelete();
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
                    dismissProgressDialog();
                }
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
        StringBuilder quantitiesBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : selectedproduct) {
            if (quantitiesBuilder.length() > 0) {
                quantitiesBuilder.append(",");
            }
            quantitiesBuilder.append(entry.getValue());
        }
        // String joinedQuantities = quantitiesBuilder.toString();
        String joinedQuantities = String.join(",", onlineReqQtys);
        params.put("quantities", joinedQuantities);

       /* productIdsBuilder.append(productInfo.getProductID()).append(",");
        itemCodesBuilder.append(productInfo.getItemCode()).append(",");
        quantitiesBuilder.append(productInfo.getQuantity()).append(",");
        agencyIdsBuilder.append(productInfo.getAgencyCode()).append(",");*/

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
                    Intent intent = new Intent(AddQuantity.this, AddItemsActivity.class);
                    //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //  selectedproduct.clear();
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
        cursor.close();
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
                Intent i = new Intent(AddQuantity.this, AddItemsActivity.class);
                startActivity(i);
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
        listproduct.clear();
        listagency.clear();
        selectedproduct.clear();
        listOutletIDs.clear();
        if (onlineProductBeanList != null) {
            onlineProductBeanList.clear();
        }
        if (listproduct != null) {
            listproduct.clear();
        }
        if (selectedproduct != null) {
            selectedproduct.clear();
        }
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

    }

    private void initializeSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                selectedproduct.remove(position);
                addQtyAdapter.removeItem(position);
                addQtyAdapter.updateData(selectedproduct); // Ensure the adapter's data is updated
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

    /*private void showConfirmOrders() {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) AddQuantity.this.getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);

                final Dialog dialog = new Dialog(AddQuantity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm_order);

                // Set the desired width and height
                int dialogWidth = (int) (displayMetrics.widthPixels * 1.0); // 100% of screen width
                int dialogHeight = (int) (displayMetrics.heightPixels * 0.7); // 70% of screen height, or adjust as needed

                // Set the dialog dimensions and position
                dialog.getWindow().setLayout(dialogWidth, dialogHeight);
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);

                ListView listView = dialog.findViewById(R.id.dialog_order_items_listView);
                TextView mealService = dialog.findViewById(R.id.mealSerViceDia);
                Button confirm = dialog.findViewById(R.id.btn_confirm);
                Button edit = dialog.findViewById(R.id.btn_edit);
                confirm.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                edit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setupDatePicker();
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        orderConfrimBeans.clear();
                        dialog.dismiss();
                    }
                });

                mealService.setText("CONFIRM ORDER");
                OrderConfrimSpinnerAdapter confrimOrderAdapter = new OrderConfrimSpinnerAdapter(AddQuantity.this, orderConfrimBeans);
                listView.setAdapter(confrimOrderAdapter);
                confrimOrderAdapter.notifyDataSetChanged();
                dialog.show();
            }
        });
    }*/

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
                        System.out.println("for process order :" + outletID);
                        // 🧹 Clear old data before processing new outlet
                        onlineProductID.clear();
                        onlineItemCode.clear();
                        onlinelistagencyids.clear();
                        onlineReqQtys.clear();
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
                Intent intent = new Intent(AddQuantity.this, AddItemsActivity.class);
                //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();
            } else {
                // Handle failure if needed
                showToastOnMainThread("Processing failed");
            }
        }
    }

    class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        SwipeToDeleteCallback(AddQtyAdapter addproductAdapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();

            // Check if the itemPosition is valid
            if (itemPosition >= 0 && itemPosition < selectedproduct.size()) {
                // Get the values of the deleted item before removing it
                String deletedItemName = selectedproduct.get(itemPosition).getKey();
                String deletedItemQuantity = selectedproduct.get(itemPosition).getValue();

                // Call removeItem method directly
                addQtyAdapter.removeItem(itemPosition);

                // Show Snackbar with the appropriate message
                Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                        "Item deleted.", Snackbar.LENGTH_LONG);

                // Handle Snackbar action if needed
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Undo action or handle cancellation if required
                        addQtyAdapter.restoreItem(itemPosition, deletedItemName, deletedItemQuantity);
                    }
                });

                snackbar.show();
            }
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(AddQuantity.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(AddQuantity.this, R.color.recycler_view_item_swipe_right_background))
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
}

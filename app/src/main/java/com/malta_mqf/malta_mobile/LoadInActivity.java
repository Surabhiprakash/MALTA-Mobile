package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.LoadInLoadOutAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.TotalApprovedOrderBsdOnItem;
import com.malta_mqf.malta_mobile.Model.ProductBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LoadInActivity extends BaseActivity {

    static List<String> listagency;
    static List<ProductBean> finaltotal;
    SubmitOrderDB submitOrderDB;
    Toolbar toolbar;
    AutoCompleteTextView spinner;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    ApprovedOrderDB approvedOrderDB;
    GetCusOutletAgencyProductAdapter adapter;
    Set<ProductBean> productIdQty;
    Button save;
    TotalApprovedOrderBsdOnItem totalApprovedOrderBsdOnItemDB;
    StockDB stockDB;
    String agency_name;
    ImageView searchitembyagency;
    EndsWithArrayAdapter endsWithArrayAdapter;
    HashSet<String> processedAgencies;
    HashSet<String> processedProductIds;
    SearchView searchView;
    ImageView datePicker;
    DatePickerDialog datePickerDialog;
    String selectedDate;
    TextView dateTextView;
    private RecyclerView recyclerView;
    private LoadInLoadOutAdapter inventoryAdapter;
    private ALodingDialog aLodingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_in);
        listagency = new LinkedList<>();
        productIdQty = new LinkedHashSet<>();
        finaltotal = new LinkedList<>();
        processedAgencies = new HashSet<>();
        processedProductIds = new HashSet<>();
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        stockDB = new StockDB(this);
        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        approvedOrderDB = new ApprovedOrderDB(this);
        totalApprovedOrderBsdOnItemDB = new TotalApprovedOrderBsdOnItem(this);
        save = findViewById(R.id.save);
        save.setEnabled(false);
        save.setBackgroundColor(getResources().getColor(R.color.light_grey));
        searchitembyagency = findViewById(R.id.search_icon);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("LOAD IN");
        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinner);
        searchView = findViewById(R.id.searchView);
        aLodingDialog = new ALodingDialog(this);

        datePicker = findViewById(R.id.date_picker_icon);
        dateTextView = findViewById(R.id.tv_selected_date);


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        selectedDate = formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dateTextView.setText(selectedDate);

        displayInstruction();
        displayAllAgency(selectedDate);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
       /* spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (inventoryAdapter != null) {
                    inventoryAdapter.filter(newText);
                }
                return false;
            }
        });*/


        // Method to retrieve the product list

       /* searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aLodingDialog.show();
                productIdQty.clear();
                finaltotal.clear();
                agency_name = spinner.getText().toString().trim();
                System.out.println("agencyname:" + agency_name);

                if (agency_name.equals("All")) {
                    aLodingDialog.show();

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            displayAllItemsForAllAgencies();
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 3000);

                } else {
                    System.out.println(agency_name);
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency_name);
                    if (cursor.getCount() == 0) {
                        Toast.makeText(LoadInActivity.this, "No data", Toast.LENGTH_SHORT).show();
                        aLodingDialog.cancel();
                        return;
                    }

                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);

                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);
                        if (cursor1.getCount() == 0) {
                            Toast.makeText(LoadInActivity.this, "No data", Toast.LENGTH_SHORT).show();
                            aLodingDialog.cancel();
                            return;
                        }

                        while (cursor1.moveToNext()) {
                            @SuppressLint("Range")
                            String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                            @SuppressLint("Range")
                            String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                            String purchase_price = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_PURCHASE_PRICE));
                            Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonProductIDandStatus(productID, "NOT LOADED");
                            if (cursor2 != null && cursor2.getCount() > 0) {
                                while (cursor2.moveToNext()) {
                                    @SuppressLint("Range") String productId = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                    @SuppressLint("Range") String prodcutName = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                    @SuppressLint("Range") String reQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_REQUESTEDQTY));
                                    @SuppressLint("Range") String appQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_APPROVEDQTY));
                                    @SuppressLint("Range") String avlQTY = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                                    productIdQty.add(new ProductBean(productId, purchase_price, prodcutName, reQty, appQty, avlQTY));
                                }
                                cursor2.close();
                            }
                        }
                        cursor1.close();
                    }
                    cursor.close();

                    for (ProductBean bean : productIdQty) {
                        String pID = bean.getProductId();
                        String pPrice = bean.getPurchase_price();
                        String pName = bean.getProductName();
                        String qty = bean.getQuantity();
                        String appqty = bean.getApprovedqty();
                        String avlqty = bean.getDeliveryQty();
                        boolean productExists = false;

                        for (ProductBean productBean : finaltotal) {
                            if (productBean.getProductId().equals(pID)) {
                                productExists = true;
                                break;
                            }
                        }

                        if (!productExists) {
                            finaltotal.add(new ProductBean(pID, pPrice, pName, String.valueOf(qty), String.valueOf(appqty), avlqty));
                            if (finaltotal.isEmpty()) {
                                save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.light_grey));
                                save.setEnabled(false);
                            } else {
                                save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.appColorpurple));
                                save.setEnabled(true);
                            }
                            finaltotal = convertListToMapEntryList(finaltotal);
                        }
                    }

                    if (inventoryAdapter == null) {
                        inventoryAdapter = new LoadInLoadOutAdapter(finaltotal);
                        recyclerView.setLayoutManager(new LinearLayoutManager(LoadInActivity.this));
                        recyclerView.setAdapter(inventoryAdapter);
                        inventoryAdapter.notifyDataSetChanged();
                    } else {
                        inventoryAdapter.updateData(finaltotal);
                    }

                    // Add this after adapter initialization
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (inventoryAdapter != null) {
                                inventoryAdapter.filter(newText);
                            }
                            return false;
                        }
                    });

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 5000);
                }
            }
        });*/

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();
                productIdQty.clear();
                finaltotal.clear();
                agency_name = spinner.getText().toString().trim();
                System.out.println("agencyname:" + agency_name);

                if (agency_name.equals("All")) {
                    aLodingDialog.show();

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            displayAllItemsForAllAgencies(selectedDate);
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 3000);

                } else {
                    System.out.println(agency_name);
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency_name);
                    if (cursor.getCount() == 0) {
                        Toast.makeText(LoadInActivity.this, "No data", Toast.LENGTH_SHORT).show();
                        aLodingDialog.cancel();
                        return;
                    }

                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);

                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);
                        if (cursor1.getCount() == 0) {
                            Toast.makeText(LoadInActivity.this, "No data", Toast.LENGTH_SHORT).show();
                            aLodingDialog.cancel();
                            return;
                        }

                        while (cursor1.moveToNext()) {
                            @SuppressLint("Range")
                            String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                            @SuppressLint("Range")
                            String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                            @SuppressLint("Range") String purchase_price = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_PURCHASE_PRICE));
                            Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonProductIDandStatus(productID, "NOT LOADED", "PARTIALLY LOADED", selectedDate);
                            ;
                            if (cursor2 != null && cursor2.getCount() > 0) {
                                while (cursor2.moveToNext()) {
                                    @SuppressLint("Range") String productId = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                    @SuppressLint("Range") String itemCode = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CODE));
                                    @SuppressLint("Range") String prodcutName = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                    @SuppressLint("Range") String reQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_REQUESTEDQTY));
                                    @SuppressLint("Range") String appQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_APPROVEDQTY));
                                    @SuppressLint("Range") String avlQTY = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                    @SuppressLint("Range") String expectedDelivery = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_EXPECTED_DELIVERY));
                                    productIdQty.add(new ProductBean(productId, itemCode, purchase_price, prodcutName, reQty, appQty, "", expectedDelivery));
                                }
                                cursor2.close();
                            }
                        }
                        cursor1.close();
                    }
                    cursor.close();

                    for (ProductBean bean : productIdQty) {
                        String pID = bean.getProductId();
                        String pPrice = bean.getPurchase_price();
                        String pName = bean.getProductName();
                        String qty = bean.getQuantity();
                        String appqty = bean.getApprovedqty();
                        String avlqty = bean.getDeliveryQty();
                        String itemCode = bean.getItemcode();
                        String expectedDeldate = bean.getExpectedDelivery();
                        boolean productExists = false;

                        for (ProductBean productBean : finaltotal) {
                            if (productBean.getProductId().equals(pID)) {
                                productExists = true;
                                break;
                            }
                        }

                        if (!productExists) {
                            if (!qty.equals("0") && !appqty.equals("0")) {
                                finaltotal.add(new ProductBean(pID, itemCode, pPrice, pName, String.valueOf(qty), String.valueOf(appqty), avlqty, expectedDeldate));
                                if (finaltotal.isEmpty()) {
                                    save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.light_grey));
                                    save.setEnabled(false);
                                } else {
                                    save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.appColorpurple));
                                    save.setEnabled(true);
                                }
                                finaltotal = convertListToMapEntryList(finaltotal);
                            }
                        }
                    }

                    if (inventoryAdapter == null) {
                        inventoryAdapter = new LoadInLoadOutAdapter(finaltotal);
                        recyclerView.setLayoutManager(new LinearLayoutManager(LoadInActivity.this));
                        recyclerView.setAdapter(inventoryAdapter);
                        inventoryAdapter.notifyDataSetChanged();
                    } else {
                        inventoryAdapter.updateData(finaltotal);
                    }

                    // Add this after adapter initialization
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (inventoryAdapter != null) {
                                inventoryAdapter.filter(newText);
                            }
                            return false;
                        }
                    });

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 5000);
                }
            }
        });
        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint({"Range", "NotifyDataSetChanged"})
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });*/
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.showDropDown();
            }
        });

        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            spinner.showDropDown();
                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    showEnterCodeOFTheDaySpinner();
                Intent i = new Intent(LoadInActivity.this, ShowLoadinInvoice.class);
                i.putExtra("agencyname", agency_name);
                i.putExtra("expectedDelivery", selectedDate);
                startActivity(i);
            }
        });
    }

    private void displayInstruction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Instructions!!!")
                .setMessage("1. Once  you started loading with one agency kindly load the remaining agencies.\n" +
                        "To avoid loading issues.\n" +
                        "3. Click 'OK' to proceed.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEnterCodeOFTheDaySpinner() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) LoadInActivity.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(LoadInActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_code);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 100), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText remark = dialog.findViewById(R.id.dialog_enter_remarks);
        TextView mealService = dialog.findViewById(R.id.mealSerViceDia);
        Button ok = dialog.findViewById(R.id.dialog_ok_button);
        Button cancel = dialog.findViewById(R.id.dialog_cancel_button);
        mealService.setText("Enter Code of the Day");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = remark.getText().toString();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ProductBean productBean : finaltotal) {
                    String productId = productBean.getProductId();
                    String productName = productBean.getProductName();
                    String quantity = productBean.getQuantity();
                    String approvedQty = productBean.getApprovedqty();
                    String delQty = productBean.getDeliveryQty();
                    System.out.println("Product ID: " + productId);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Approved Quantity: " + approvedQty);
                    System.out.println("Delivery Quantity: " + delQty);

                    if (delQty == null || delQty.isEmpty() || delQty.equals("0")) {
                        //   totalApprovedOrderBsdOnItemDB.totalUpdateApprovedData2(vanID, productName, productId, quantity, approvedQty, approvedQty, "LOADED", dateFormat.format(date));
                    } else {
                        // totalApprovedOrderBsdOnItemDB.totalUpdateApprovedData2(vanID, productName, productId, quantity, approvedQty, delQty, "LOADED", dateFormat.format(date));
                    }
                }
                if (agency_name.equals("All")) {
                    Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);

                        // Move the cursor1 to the first row before accessing its data
                        if (!processedAgencies.contains(agencyCode)) {
                            // Add the agency code to the HashSet to mark it as processed
                            processedAgencies.add(agencyCode);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range")
                                String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                @SuppressLint("Range")
                                String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                if (!processedProductIds.contains(productID)) {
                                    // Add the product ID to the HashSet to mark it as processed
                                    processedProductIds.add(productID);
                                    Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonproductid(productID);

                                    while (cursor2.moveToNext()) {
                                        @SuppressLint("Range") String prdid = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                        @SuppressLint("Range")
                                        String prdname = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                        @SuppressLint("Range") int prdqty = cursor2.getInt(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                        System.out.println(prdid + "-" + prdname + "-" + prdqty);

                                        // Check if the product exists in the stockDB
                                        Cursor cursor3 = stockDB.readonproductid(prdid);
                                        if (cursor3.getCount() == 0) {
                                            // Product does not exist, add it to stockDB
                                            //  stockDB.stockaddApprovedDetails(vanID, prdname, prdid, prdqty);
                                        } else {
                                            // Product exists, update its details in stockDB
                                            // stockDB.stockUpdateApprovedData(vanID, prdname, prdid, prdqty);
                                        }
                                        cursor3.close(); // Close the cursor after use
                                    }
                                    cursor2.close();
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(LoadInActivity.this, LoadInventory.class);
                    startActivity(intent);
                    listagency.clear();
                    dialog.dismiss();
                } else {
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency_name);
                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);

                        // Move the cursor1 to the first row before accessing its data
                        if (!processedAgencies.contains(agencyCode)) {
                            // Add the agency code to the HashSet to mark it as processed
                            processedAgencies.add(agencyCode);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range")
                                String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                @SuppressLint("Range")
                                String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                if (!processedProductIds.contains(productID)) {
                                    // Add the product ID to the HashSet to mark it as processed
                                    processedProductIds.add(productID);
                                    Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonproductid(productID);

                                    while (cursor2.moveToNext()) {
                                        @SuppressLint("Range") String prdid = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                        @SuppressLint("Range")
                                        String prdname = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                        @SuppressLint("Range") int prdqty = cursor2.getInt(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                        System.out.println(prdid + "-" + prdname + "-" + prdqty);

                                        // Check if the product exists in the stockDB
                                        Cursor cursor3 = stockDB.readonproductid(prdid);
                                        if (cursor3.getCount() == 0) {
                                            // Product does not exist, add it to stockDB
                                            //  stockDB.stockaddApprovedDetails(vanID, prdname, prdid, prdqty);
                                        } else {
                                            // Product exists, update its details in stockDB
                                            // stockDB.stockUpdateApprovedData(vanID, prdname, prdid, prdqty);
                                        }
                                        cursor3.close(); // Close the cursor after use
                                    }
                                    cursor2.close();
                                }
                            }
                        }
                    }
                    cursor.close();
                    Intent intent = new Intent(LoadInActivity.this, LoadInventory.class);
                    startActivity(intent);
                    listagency.remove(agency_name);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @SuppressLint("Range")
    private void displayAllAgency(String selectedDate) {
        listagency.clear();
        listagency.add("All");

        Cursor cursor1 = totalApprovedOrderBsdOnItemDB.GetAgencyDataNotLoadedBYStatus("NOT LOADED", "PARTIALLY LOADED", selectedDate);
        if (cursor1 != null && cursor1.getCount() > 0) {
            HashSet<String> uniqueAgencies = new HashSet<>();

            while (cursor1.moveToNext()) {
                String agency_name = cursor1.getString(cursor1.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_AGENCY_NAME));

                if (!uniqueAgencies.contains(agency_name)) {
                    uniqueAgencies.add(agency_name);
                    listagency.add(agency_name);
                }
            }
            cursor1.close();
        } else {
            aLodingDialog.cancel();
            Toast.makeText(this, "No Agency available for Load", Toast.LENGTH_SHORT).show();
            return;
        }

        endsWithArrayAdapter = new EndsWithArrayAdapter(LoadInActivity.this, R.layout.list_item_text, R.id.list_textView_value, listagency);
        spinner.setAdapter(endsWithArrayAdapter);

    }

    @SuppressLint("Range")
    private void displayAllItemsForAllAgencies(String expectedDeliveryDate) {
        productIdQty.clear();
        finaltotal.clear();

        // Fetch items for all agencies
        Cursor allAgenciesCursor = allAgencyDetailsDB.readAllAgencyData();
        if (allAgenciesCursor.getCount() == 0) {
            aLodingDialog.cancel();
            Toast.makeText(this, "No agency data found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (allAgenciesCursor.moveToNext()) {
            String agencyCode = allAgenciesCursor.getString(allAgenciesCursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));

            // Fetch items for the current agency
            Cursor itemsCursor = itemsByAgencyDB.checkIfITemExists(agencyCode);
            if (itemsCursor.getCount() > 0) {
                while (itemsCursor.moveToNext()) {
                    String productID = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    String purchase_price = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_PURCHASE_PRICE));
                    // Fetch total approved orders for the current product and status
                    Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonProductIDandStatus(productID, "NOT LOADED", "PARTIALLY LOADED", expectedDeliveryDate);
                    if (cursor2 != null && cursor2.getCount() > 0) {
                        while (cursor2.moveToNext()) {
                            String productId = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                            String productName = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                            String itemCode = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CODE));
                            String reQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_REQUESTEDQTY));
                            String appQty = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_APPROVEDQTY));
                            String avlQTY = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                            String expectedDelivery = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_EXPECTED_DELIVERY));
                            productIdQty.add(new ProductBean(productId, itemCode, purchase_price, productName, reQty, appQty, "", expectedDelivery));
                        }
                    }

                    if (cursor2 != null) {
                        cursor2.close();
                    }
                }
            }
            if (itemsCursor != null) {
                itemsCursor.close();
            }
        }
        if (allAgenciesCursor != null) {
            allAgenciesCursor.close();
        }

        // Add all items to the final list
        for (ProductBean bean : productIdQty) {
            String pID = bean.getProductId();
            String pPrice = bean.getPurchase_price();
            String pName = bean.getProductName();
            String qty = bean.getQuantity();
            String appqty = bean.getApprovedqty();
            String avlqty = bean.getDeliveryQty();
            String itemCode = bean.getItemcode();
            String expectedDelDate = bean.getExpectedDelivery();
            boolean productExists = false;

            for (ProductBean productBean : finaltotal) {
                if (productBean.getProductId().equals(pID)) {
                    // Update quantities of existing product in finaltotal
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                if (!qty.equals("0") && !appqty.equals("0")) {

                    finaltotal.add(new ProductBean(pID, itemCode, pPrice, pName, String.valueOf(qty), String.valueOf(appqty), avlqty, expectedDelDate));
                    if (finaltotal.isEmpty()) {
                        save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.light_grey));
                        save.setEnabled(false);
                    } else {
                        save.setBackgroundColor(ContextCompat.getColor(LoadInActivity.this, R.color.appColorpurple));
                        save.setEnabled(true);
                    }
                    finaltotal = convertListToMapEntryList(finaltotal);
                }
            }
        }

        // Update the adapter
        if (inventoryAdapter == null) {


            inventoryAdapter = new LoadInLoadOutAdapter(finaltotal);
            recyclerView.setLayoutManager(new LinearLayoutManager(LoadInActivity.this));
            recyclerView.setAdapter(inventoryAdapter);
            inventoryAdapter.notifyDataSetChanged();
        } else {
            inventoryAdapter.updateData(finaltotal);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (inventoryAdapter != null) {
                    inventoryAdapter.filter(newText);
                }
                return false;
            }
        });
    }

    private List<ProductBean> convertListToMapEntryList(List<ProductBean> list) {
        Set<String> existingKeys = new HashSet<>();
        for (ProductBean entry : finaltotal) {
            existingKeys.add(entry.getProductName().trim());
        }

        Log.d("convertListToMapEntryList", "Existing keys before adding new entries: " + existingKeys);

        for (ProductBean entry : list) {
            String keyToCheck = entry.getProductName().trim();
            Log.d("convertListToMapEntryList", "Checking key: " + keyToCheck);

            if (!existingKeys.contains(keyToCheck)) {
                finaltotal.add(new ProductBean(entry.getProductId(), entry.getItemcode(), entry.getPurchase_price(), keyToCheck, entry.getQuantity(), entry.getApprovedqty(), entry.getDeliveryQty(), entry.getExpectedDelivery()));
                existingKeys.add(keyToCheck);
                Log.d("convertListToMapEntryList", "Added entry: " + keyToCheck);
            } else {
                Log.d("convertListToMapEntryList", "Entry already exists: " + keyToCheck);
            }
        }


        return finaltotal;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            recyclerView = null;
            inventoryAdapter = null;
            submitOrderDB = null;
            toolbar = null;
            spinner = null;
            allAgencyDetailsDB = null;
            itemsByAgencyDB = null;
            approvedOrderDB = null;
            listagency = null;
            adapter = null;
            productIdQty = null;
            finaltotal = null;
            listagency.clear();
            productIdQty.clear();
            finaltotal.clear();
            agency_name = null;
            processedAgencies.clear();
            processedProductIds.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(LoadInActivity.this, LoadInventory.class);
        startActivity(intent);
        finish();
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Automatically set today's date when the method is called
        if (selectedDate == null) {
            selectedDate = formatDate(year, month, dayOfMonth);
            updateDateInView(selectedDate); // Update the UI with the default date
        }

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        Calendar today = Calendar.getInstance();
                        if (selectedCalendar.before(today)) {
                            // Prevent selecting past dates
                            Toast.makeText(getApplicationContext(), "Please select today or a future date", Toast.LENGTH_SHORT).show();
                            view.updateDate(year, month, dayOfMonth);
                        } else {
                            selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                            displayAllAgency(selectedDate);
                            updateDateInView(selectedDate); // Update UI with the selected date
                        }
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    // Helper method to update the UI with the selected date
    private void updateDateInView(String date) {
        TextView dateTextView = findViewById(R.id.tv_selected_date); // Change to your TextView ID
        dateTextView.setText(date);
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
}
package com.malta_mqf.malta_mobile;


import static com.malta_mqf.malta_mobile.NewSaleActivity.totalQty;
import static com.malta_mqf.malta_mobile.Signature.SignatureActivity.REQUEST_CODE_SIGNATURE;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewSaleReceiptDemo.orderId;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.Edits;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.Adapter.CancelReasonAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithAgencyArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.NewSalesAdapter;
import com.malta_mqf.malta_mobile.Adapter.ShowOrderForInvoiceAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SellingPriceOfItemBsdCustomerDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.Model.ProductBean;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;
import java.math.BigDecimal;
import java.math.RoundingMode;
public class NewSaleActivity extends AppCompatActivity {

    RecyclerView newsalerecyclerView;
    Toolbar toolbar;
    ALodingDialog aLodingDialog;

    public static String route, vehiclenum, name, userID, vanID;
    String lastvoiceInvoicenumber;
    public static String deliveryStatus;
    AutoCompleteTextView spinner;
    String outletID, outletName, orderId, trn_no, itemName, categoryVan;
    SubmitOrderDB submitOrderDB;
    ItemsByAgencyDB itemsByAgencyDB;
    List<NewSaleBean> productInfoList;
    public static List<NewSaleBean> newSaleBeanList = new LinkedList<>();
    public static Set<NewSaleBean> newSaleBeanListSet = new LinkedHashSet<>();
    public static List<NewSaleBean> newSaleBeanListss = new LinkedList<>();
    private boolean isVerificationDialogShown = false;
    //  public static List<ShowOrderForInvoiceBean> orderToInvoice = new LinkedList<>();

    public static int totalQty, totalrecalcualtedqty = 0,totalItemsCount;
    Double TOTALVAT = 0.0, TOTALGROSS = 0.0;
    double TOTALNET = 0.0;
    StockDB stockDB;
    BigDecimal totalrecalculatedVat = BigDecimal.ZERO, totalrecalculatedNet = BigDecimal.ZERO, totalrecalculatedGross = BigDecimal.ZERO;
    // Set<NewSaleBean> newSaleBeanSet;
    NewSalesAdapter newSalesAdapter;

    private Bitmap signatureBitmap;
    public static String invoiceNumber, deliveredQty;
    public static String outletId, orderidforNewSale;

    private Button mSaveButtonPrint, mGetSignatureButton;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ImageView signatureImageView, recalculate;


    public static final int BLUETOOTH_ENABLE_REQUEST_CODE = 124;
    SellingPriceOfItemBsdCustomerDB sellingPriceOfItemBsdCustomerDB;
    public static String customerCodes, customername, customeraddress;
    Button cancel_order, refresh;
    UserDetailsDb userDetailsDb;
    SearchView searchView;

    private static final String PREFS_NAME = "InvoicePrefs";
    private static final String INVOICE_KEY = "current_invoice_number";
    private static final String DECREASE_KEY = "DECREASE_KEY";
    private static final int INVOICE_LENGTH = 7;
    private SharedPreferences sharedPreferences;
    AllCustomerDetailsDB customerDetailsDB;
    ApprovedOrderDB approvedOrderDB;
    TextView Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    public static List<NewSaleBean> extranewSaleBeanListss = new LinkedList<>();
    List<String> listextraproducts = new LinkedList<>();
    EndsWithAgencyArrayAdapter endsWithAgencyArrayAdapter;

    private SubmitOrderDB dbHelper;
    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);
        productInfoList = new LinkedList<>();
        newSaleBeanList = new LinkedList<>();
        newSaleBeanListSet = new LinkedHashSet<>();
        newSaleBeanListss = new LinkedList<>();
        cancel_order = findViewById(R.id.cancel_order);

        cancel_order.setBackgroundColor(ContextCompat.getColor(this, R.color.recycler_view_item_swipe_right_background));
        spinner = findViewById(R.id.spinneraddproduct);
        dbHelper = new SubmitOrderDB(this);
        aLodingDialog = new ALodingDialog(this);
        approvedOrderDB=new ApprovedOrderDB(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        outletID = getIntent().getStringExtra("outletid");
        System.out.println("outletid in newsale: " + outletID);
        outletName = getIntent().getStringExtra("outletname");
        customerCodes = getIntent().getStringExtra("customerCode");
        customername = getIntent().getStringExtra("customerName");
        customeraddress = getIntent().getStringExtra("customeraddress");
        System.out.println("customeraddress in the new sale activity is :" + customeraddress);
        orderId = getIntent().getStringExtra("orderid");
        trn_no = getIntent().getStringExtra("trn_no");
        System.out.println("orderid:" + orderId);
        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        userDetailsDb = new UserDetailsDb(this);
        customerDetailsDB = new AllCustomerDetailsDB(this);
        searchView = findViewById(R.id.searchView);
        stockDB = new StockDB(this);
        sellingPriceOfItemBsdCustomerDB = new SellingPriceOfItemBsdCustomerDB(this);
        newsalerecyclerView = findViewById(R.id.newSalerecyclerView);

       /* mSignaturePad = findViewById(R.id.signature_pad);
        mClearButton = findViewById(R.id.clear_button);*/
        recalculate = findViewById(R.id.recalculate);
        Total_Qty = findViewById(R.id.tvTotalQty);
        Total_Net_amt = findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt = findViewById(R.id.tvTotalVatAmt);
        Total_Amount_Payable = findViewById(R.id.tvGrossAmount);
        mSaveButtonPrint = findViewById(R.id.btn_save_print);
        mSaveButtonPrint.setEnabled(true);
        mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        mGetSignatureButton = findViewById(R.id.btn_capture);
        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        signatureImageView = findViewById(R.id.signatureImageView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        toolbar.setTitle(outletName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ORDER DETAILS");
        System.out.println("outletid is" + outletID);
        System.out.println("order is" + orderId);

        productInfoList.clear();
        newSaleBeanList.clear();
        newSaleBeanListSet.clear();
        newSaleBeanListss.clear();
        extranewSaleBeanListss.clear();
        listextraproducts.clear();

        executeMethodsSequentially(customerCodes,outletID,orderId);
        //   getNewSaleOrderDetails(outletID, orderId, "PENDING FOR DELIVERY", "DELIVERED");
        performOrderCalculationAfterRefresh();
        /* mClearButton.setOnClickListener(v -> mSignaturePad.clear());*/
        Cursor cursor3 = submitOrderDB.readAllorderDataByOutletIDAndStatus(outletID, orderId, "PENDING FOR DELIVERY", "DELIVERED");
        if (cursor3.getCount() != 0) {
            while (cursor3.moveToNext()) {
                if (cursor3.getString(cursor3.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO)) == null) {
                    Cursor cursor2 = userDetailsDb.readAllData();
                    while (cursor2.moveToNext()) {
                        route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
                        vehiclenum = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));
                        name = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_NAME));
                        userID = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_USERID));
                        vanID = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
                        lastvoiceInvoicenumber = submitOrderDB.getLastInvoiceNumber();
                        if (lastvoiceInvoicenumber == null || lastvoiceInvoicenumber.isEmpty() || lastvoiceInvoicenumber.length() > 15) {
                            lastvoiceInvoicenumber = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.INVOICE_NUMBER_UPDATING));

                        }
                        System.out.println("lastinvoice: " + lastvoiceInvoicenumber);
                        String category = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_CATEGORY));
                        String categoryVan;

                    }
//                    String routeName = String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 2));
                    String routeName = String.valueOf(route.charAt(0)) + route.substring(route.length() - 2);
                    invoiceNumber = routeName + "S" + getCurrentDate() + generateNextInvoiceNumber(lastvoiceInvoicenumber);
                    System.out.println("invoice number: " + invoiceNumber);
                    cursor2.close();
                } else {
                    Cursor cursor2 = userDetailsDb.readAllData();
                    while (cursor2.moveToNext()) {
                        route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
                        vehiclenum = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));
                        name = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_NAME));
                        userID = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_USERID));
                        vanID = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
                    }
                    invoiceNumber = cursor3.getString(cursor3.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                    System.out.println("invoice number2: " + invoiceNumber);
                    cancel_order.setBackgroundColor(ContextCompat.getColor(this, R.color.listitem_gray));
                    cancel_order.setEnabled(false);
                    cursor2.close();
                }
            }
            cursor3.close();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newSalesAdapter != null) {
                    newSalesAdapter.filter(newText);
                }
                return false;
            }
        });


        // Set the delivery quantity exceeded listener
    /*    newSalesAdapter.setOnDeliveryQuantityExceededListener(new NewSalesAdapter.OnDeliveryQuantityExceededListener() {
            @Override
            public void onDeliveryQuantityExceeded(boolean isExceeded) {
                if (isExceeded) {
                    mGetSignatureButton.setEnabled(false);
                    mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.light_grey));
                } else {
                    mGetSignatureButton.setEnabled(true);
                    mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.appColorpurple));
                }
            }
        });*/

        // newsalerecyclerView.setAdapter(newSalesAdapter);


      /*  if (signatureBitmap == null) {
            mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
            mSaveButtonPrint.setEnabled(false);
        }*/
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String prod_name = spinner.getText().toString().trim();
                String newProductName = prod_name;
                getNewSaleOrderDetails2(prod_name);
            }
        });

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.showDropDown();
            }
        });

        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    spinner.showDropDown();
                }
            }
        });
        mSaveButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkOrderStatusAndUpdateButton()) {
                    boolean hasZeroQuantity = false;

                    // Check for zero quantity before proceeding
                    for (int i = 0; i < newSalesAdapter.getItemList().size(); i++) {
                        NewSaleBean item = newSalesAdapter.getItemList().get(i);
                        if (item != null) {
                            String qty = item.getDeliveryQty();
                            if ("0".equals(qty)) { // Safe even if qty is null
                                hasZeroQuantity = true;
                                showZeroQuantityDialog(i, item);
                                break;
                            }
                        }
                    }

                    if (!hasZeroQuantity) {
                        // Show the dialog immediately if the activity is active
                        if (!NewSaleActivity.this.isFinishing() && !NewSaleActivity.this.isDestroyed()) {
                            aLodingDialog.show(); // Safely show the dialog
                        }

                        // Use an ExecutorService to perform background processing
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // Simulate work (e.g., saving data) with a short delay
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt(); // Preserve the interrupt status
                                    e.printStackTrace();
                                }

                                // Prepare the intent with necessary data
                                Intent intent = new Intent(NewSaleActivity.this, NewSaleInvoice.class);
                                intent.putExtra("orderid", orderId);
                                intent.putExtra("outletId", outletID);
                                intent.putExtra("outletName", outletName);
                                intent.putExtra("customerCode", customerCodes);
                                intent.putExtra("customerName", customername);
                                intent.putExtra("vehiclenum", vehiclenum);
                                intent.putExtra("name", name);
                                intent.putExtra("route", route);
                                intent.putExtra("customeraddress", customeraddress);
                                intent.putExtra("invoiceNo", invoiceNumber);
                                intent.putExtra("trn_no", trn_no);
                                intent.putExtra("TOTALQTY", String.valueOf(totalQty));
                                intent.putExtra("TOTALNET", String.format("%.2f", TOTALNET));
                                intent.putExtra("TOTALVAT", String.format("%.2f", TOTALVAT));
                                intent.putExtra("TOTALGROSS", String.format("%.2f", TOTALGROSS));

                                // Switch back to the main thread to update the UI
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!NewSaleActivity.this.isFinishing() && !NewSaleActivity.this.isDestroyed()) {
                                            // Dismiss the dialog safely
                                            if (aLodingDialog != null && aLodingDialog.isShowing()) {
                                                aLodingDialog.dismiss();
                                            }
                                            // Start the new activity
                                            startActivity(intent);
                                        }
                                    }
                                });

                                // Properly shut down the executor service to free resources
                                executor.shutdown();
                            }
                        });
                    }
                }
            }
        });


        cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ensure that the activity is not finishing before showing the dialog
                if (!NewSaleActivity.this.isFinishing()) {
                    aLodingDialog.show(); // Show the dialog when the button is clicked

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // Check if the dialog is still showing and the activity is not finishing
                            if (aLodingDialog.isShowing() && !NewSaleActivity.this.isFinishing()) {
                                aLodingDialog.cancel(); // Safely cancel the dialog
                            }
                        }
                    };
                    handler.postDelayed(runnable, 3000); // Delay of 3 seconds before canceling the dialog

                    // Call the method to show cancel order reasons
                    showCancelOrderReasons();
                }
            }
        });


        mGetSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
                newsalerecyclerView.smoothScrollToPosition(newSaleBeanListss.size());
                //saveSignatureToGallery(signatureBitmap, "signature");
                Intent signatureIntent = new Intent(NewSaleActivity.this, SignatureCaptureActivity.class);
                startActivityForResult(signatureIntent, REQUEST_CODE_SIGNATURE);
            }
        });

        checkAndRequestPermissions();

        // newsalerecyclerView.smoothScrollToPosition(newSaleBeanListss.size()-1);
        recalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NewSaleActivity.this.isFinishing()) {
                    aLodingDialog.show(); // Show the dialog when the button is clicked

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // Check if the dialog is still showing and the activity is not finishing
                            if (aLodingDialog.isShowing() && !NewSaleActivity.this.isFinishing()) {
                                aLodingDialog.cancel(); // Safely cancel the dialog
                            }
                        }
                    };
                    handler.postDelayed(runnable, 1000);
                    performOrderCalculationAfterRefresh();
                }

            }
        });


    }

    private void showZeroQuantityDialog(int position, NewSaleBean item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reason_input, null);
        builder.setView(dialogView);


        EditText etReason = dialogView.findViewById(R.id.etReason);
        Button btnOk = dialogView.findViewById(R.id.btnOk);
        btnOk.setEnabled(false); // âŒ Initially disabled


        // âœ… Enable "OK" button only when a reason is entered
        etReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnOk.setEnabled(!s.toString().trim().isEmpty()); // Enable only if reason is not empty
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });


        AlertDialog dialog = builder.create();


        btnOk.setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            System.out.println("reson for doing item zero is :"+reason) ;


            if (reason.isEmpty()) {
                etReason.setError("Reason cannot be empty"); // Show error if reason is empty
                return;
            }


            item.setZeroReason(reason);


            // Save the reason in the submitOrderDB
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            // Check if reason is not empty
            if (reason == null || reason.trim().isEmpty()) {
                Log.e("DB_UPDATE", "Reason is empty or null!");
                return;
            }
            // Check if OrderID is valid
            if (orderId == null) {
                Log.e("DB_UPDATE", "Order ID is null or empty!");
                return;
            }
            System.out.println("Order ID: " + orderId + ", Reason: " + reason);
            values.put("zero_reason", reason);
            int rowsAffected = db.update("my_submit_order", values, "OrderId = ?", new String[]{orderId});
            Log.d("DB_UPDATE", "Rows affected: " + rowsAffected);
            db.close();


//            // âœ… Store the reason in the item object (if applicable)
//            item.setZeroReason(reason);
            Log.d("ZeroQuantityReason", "Product: " + item.getProductName() + ", Reason: " + reason);


            dialog.dismiss(); // Close the reason dialog


            // âœ… Now, proceed with order processing (same logic as mSaveButtonPrint)
            if (!checkOrderStatusAndUpdateButton()) {
                if (!NewSaleActivity.this.isFinishing() && !NewSaleActivity.this.isDestroyed()) {
                    aLodingDialog.show(); // Show loading dialog
                }


                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        Thread.sleep(1000); // Simulate processing delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(NewSaleActivity.this, NewSaleInvoice.class);
                    intent.putExtra("orderid", orderId);
                    intent.putExtra("outletId", outletID);
                    intent.putExtra("outletName", outletName);
                    intent.putExtra("customerCode", customerCodes);
                    intent.putExtra("customerName", customername);
                    intent.putExtra("vehiclenum", vehiclenum);
                    intent.putExtra("name", name);
                    intent.putExtra("route", route);
                    intent.putExtra("customeraddress", customeraddress);
                    intent.putExtra("invoiceNo", invoiceNumber);
                    intent.putExtra("trn_no", trn_no);
                    intent.putExtra("TOTALQTY", String.valueOf(totalQty));
                    intent.putExtra("TOTALNET", String.format("%.2f", TOTALNET));
                    intent.putExtra("TOTALVAT", String.format("%.2f", TOTALVAT));
                    intent.putExtra("TOTALGROSS", String.format("%.2f", TOTALGROSS));


                    runOnUiThread(() -> {
                        if (!NewSaleActivity.this.isFinishing() && !NewSaleActivity.this.isDestroyed()) {
                            if (aLodingDialog != null && aLodingDialog.isShowing()) {
                                aLodingDialog.dismiss();
                            }
                            startActivity(intent); // Navigate to invoice screen
                        }
                    });


                    executor.shutdown();
                });
            }
        });
        dialog.show();
    }

    private void showOrderBlockedAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Order Blocked")
                .setMessage("This order has already been delivered and has an invoice. You cannot proceed.If you want to create another order to this same outlet, please create a new order")
                .setCancelable(false) // Prevent closing by tapping outside
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(NewSaleActivity.this, StartDeliveryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
                        startActivity(i);
                        finish();
                        dialog.dismiss(); // Close the alert
                    }
                })
                .show();
    }

    private boolean checkOrderStatusAndUpdateButton() {
        try {
            boolean isDeliveredWithInvoice = submitOrderDB.isOrderDeliveredWithInvoice(orderId);

            if (isDeliveredWithInvoice) {
                mSaveButtonPrint.setEnabled(false);
                Toast.makeText(this, "Order is delivered and invoice exists!", Toast.LENGTH_SHORT).show();
            } else {
                mSaveButtonPrint.setEnabled(true);
            }
            return isDeliveredWithInvoice;
        } catch (IllegalStateException e) {
            Log.e("OrderCheck", "IllegalStateException: " + e.getMessage(), e);
            Toast.makeText(this, "An error occurred while checking order status!", Toast.LENGTH_SHORT).show();
            return false; // Return a default value to prevent crashes
        }
    }
  /*  public void showProgressDialog() {
        mProgressDialog.setMessage("Loading, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (aLodingDialog != null && aLodingDialog.isShowing()) {
            aLodingDialog.dismiss();
        }
        getNewSaleOrderDetails(outletID, orderId, "PENDING FOR DELIVERY", "DELIVERED");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void showCancelOrderReasons() {
        // Create a dialog
        final Dialog dialog = new Dialog(NewSaleActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.layout_cancel_reason, null);

        // Find ListView
        ListView listView = dialogView.findViewById(R.id.list_cancel_reasons);

        // Set up the adapter
        List<String> reasons = Arrays.asList(
                "Out of Stock",
                "Customer Changed Mind",
                "Wrong Product",
                "Delayed Delivery",
                "Other"
        );
        CancelReasonAdapter adapter = new CancelReasonAdapter(this, reasons);
        listView.setAdapter(adapter);

        // Set click listeners for list items
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedReason = reasons.get(position);

            // Handle "Other" reason
            if (selectedReason.equals("Other")) {
                showCustomReasonDialog();
            } else {
                handleCancelOrder(selectedReason);
            }
            dialog.dismiss();
        });

        // Set the custom view for the dialog
        dialog.setContentView(dialogView);

        // Show the dialog
        dialog.show();
    }

    private void showCustomReasonDialog() {
        // Create a custom dialog for "Other" reason
        final Dialog customDialog = new Dialog(NewSaleActivity.this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Inflate custom layout
        View customDialogView = getLayoutInflater().inflate(R.layout.layout_other_reason_dialog, null);

        // Initialize views
        EditText etCustomReason = customDialogView.findViewById(R.id.et_custom_reason);
        Button btnCancel = customDialogView.findViewById(R.id.btn_cancel);
        Button btnOk = customDialogView.findViewById(R.id.btn_ok);

        // Set button listeners
        btnCancel.setOnClickListener(v -> customDialog.dismiss());

        btnOk.setOnClickListener(v -> {
            String customReason = etCustomReason.getText().toString().trim();
            if (!customReason.isEmpty()) {
                handleCancelOrder(customReason);
                customDialog.dismiss();
            } else {
                Toast.makeText(NewSaleActivity.this, "Please enter a reason", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the custom view for the dialog
        customDialog.setContentView(customDialogView);
        customDialog.show();
    }

    private void handleCancelOrder(String selectedReason) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Order Cancellation");
        builder.setMessage("Are you sure you want to cancel this order for reason: " + selectedReason + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            public void onClick(DialogInterface dialog, int id) {
                // Use an AsyncTask or Executor to handle the order cancellation
                new AsyncTask<Void, Void, Void>() {
                    @SuppressLint("Range")
                    @Override
                    protected Void doInBackground(Void... voids) {
                        int someValue = sharedPreferences.getInt(INVOICE_KEY, 0);

                        // Decrement the value by 1
                        someValue--;

                        // Save the decremented value back to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(INVOICE_KEY, someValue);
                        editor.apply();


                        String date = getCurrentDateTime();
                        int totalItems = 0;
                        int totalQty = 0;
                        String itemPrice = null;
                        BigDecimal amountPayableAfterRebate = BigDecimal.ZERO;
                        BigDecimal totalNet = BigDecimal.ZERO;
                        BigDecimal totalVat = BigDecimal.ZERO;
                        BigDecimal totalGross = BigDecimal.ZERO;
                        StringBuilder sellingPriceBuilder = new StringBuilder();
                        Cursor cursorA = submitOrderDB.readDataByOrderID(orderId);
                        if (cursorA.getCount() > 0) {
                            while (cursorA.moveToNext()) {
                                String itemCodes = cursorA.getString(cursorA.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                                String quantities = cursorA.getString(cursorA.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));

                                // Split itemCodes and quantities by commas
                                String[] itemCodeArray = itemCodes.split(",");
                                String[] quantityArray = quantities.split(",");

                                for (int j = 0; j < itemCodeArray.length; j++) {
                                    String productId = itemCodeArray[j];
                                    String quantityStr = quantityArray[j];
                                    totalQty += Integer.parseInt(quantityStr);
                                    totalItems++;

                                    Cursor cursorB = itemsByAgencyDB.readDataByCustomerCodeandproID(customerCodes, productId);
                                    if (cursorB.getCount() > 0) {
                                        String price = null;
                                        while (cursorB.moveToNext()) {
                                            price = cursorB.getString(cursorB.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                        }
                                        itemPrice = removeTrailingComma(sellingPriceBuilder.append(price).append(","));
                                        // Parse quantity and price with BigDecimal for precision
                                        try {
                                            BigDecimal quantity = new BigDecimal(quantityStr);
                                            BigDecimal priceValue = new BigDecimal(price);

                                            // Calculate net, VAT, and gross for this item
                                            BigDecimal itemNet = priceValue.multiply(quantity);
                                            BigDecimal itemVat = itemNet.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
                                            BigDecimal itemGross = itemNet.add(itemVat);

                                            // Accumulate the totals
                                            totalNet = totalNet.add(itemNet);
                                            totalVat = totalVat.add(itemVat);
                                            totalGross = totalGross.add(itemGross);

                                            System.out.println("Item NET: " + itemNet);
                                            System.out.println("Item VAT (truncated): " + itemVat);
                                            System.out.println("Item GROSS: " + itemGross);

                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                            System.out.println("Error parsing quantity or price: " + e.getMessage());
                                        }
                                    }
                                }

                                // Final totals for all items
                                System.out.println("TOTAL_NET: " + totalNet);
                                System.out.println("TOTAL_VAT (truncated): " + totalVat);
                                System.out.println("TOTAL_GROSS: " + totalGross);
                            }
                        }

// Process rebate
                        String rebateStr = getCustomerRebate(customerCodes);
                        BigDecimal rebate = BigDecimal.ZERO;

                        try {
                            rebate = new BigDecimal(rebateStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        BigDecimal rebateAmount = totalGross.multiply(rebate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                        amountPayableAfterRebate = totalGross.subtract(rebateAmount);

// Update the database
                        submitOrderDB.updateOrderStatusAfterCancelled(
                                outletID,
                                orderId,
                                itemPrice,
                                String.valueOf(totalQty),
                                String.valueOf(totalItems),
                                totalNet.setScale(2, RoundingMode.HALF_UP).toString(),
                                totalVat.setScale(2, RoundingMode.HALF_UP).toString(),
                                totalGross.setScale(2, RoundingMode.HALF_UP).toString(),
                                amountPayableAfterRebate.setScale(2, RoundingMode.HALF_UP).toString(),
                                selectedReason,
                                "REJECTED",
                                date
                        );
                        approvedOrderDB.updateOrderStatus(orderId,"REJECTED");
                        return null;

                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // Navigate to StartDeliveryActivity
                        Intent i = new Intent(NewSaleActivity.this, StartDeliveryActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                        // Show the Toast message on the main thread
                        Toast.makeText(NewSaleActivity.this, "Order Cancelled", Toast.LENGTH_SHORT).show();

                        // Finish the activity and return to the previous screen
                        finish();
                    }
                }.execute();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private String removeTrailingComma(StringBuilder builder) {
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }

    // New method to handle the order cancellation operation
    /*@SuppressLint("StaticFieldLeak")
    private void performOrderCalculation() {
        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("Range")
            @Override
            protected Void doInBackground(Void... voids) {

              //  List<String> deliveryQuantities = newSalesAdapter.getDeliveryQuantities();

                String date = getCurrentDateTime();
                int totalItems = 0;
                 totalrecalcualtedqty = 0;
                double amountPayableAfterRebate = 0.00;
                totalrecalculatedNet= 0.00;
                totalrecalculatedVat  = 0.00;
                totalrecalculatedGross = 0.00;

                Cursor cursorA = submitOrderDB.readDataByOrderID(orderId);
                if (cursorA.getCount() > 0) {
                    while (cursorA.moveToNext()) {
                        String itemCodes = cursorA.getString(cursorA.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                        String quantities = cursorA.getString(cursorA.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));

                        // Split itemCodes and quantities by commas
                        String[] itemCodeArray = itemCodes.split(",");
                        String[] quantityArray = quantities.split(",");


                        for (int j = 0; j < itemCodeArray.length; j++) {
                            String productId = itemCodeArray[j].trim();
                            String quantityStr = quantityArray[j];
                            totalrecalcualtedqty += Integer.parseInt(quantityStr);

                            totalItems++;

                            Cursor cursorB = itemsByAgencyDB.readDataByCustomerCode(customerCodes,productId);

                            if (cursorB.getCount() > 0) {
                                String price = null;
                                while (cursorB.moveToNext()) {
                                    price = cursorB.getString(cursorB.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                }

                                // Safely parse the quantity
                                try {
                                    double quantity = Double.parseDouble(quantityStr);
                                    double priceValue = Double.parseDouble(price);

                                    // Calculate for this item
                                    double itemNet = priceValue * quantity;
                                    double itemVat = itemNet * 0.05;
                                    double itemGross = itemNet + itemVat;

                                    // Accumulate the totals
                                    totalrecalculatedNet += itemNet;
                                    totalrecalculatedVat += itemVat;
                                    totalrecalculatedGross += itemGross;

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    System.out.println("Error parsing quantity or price: " + e.getMessage());
                                }
                            }
                        }

                        // Final totals for all items
                        Total_Qty.setText("Total Qty: " + totalrecalcualtedqty);
                        Total_Net_amt.setText("Total Net Amount: " + String.format("%.2f", totalrecalculatedNet));
                        Total_vat_amt.setText("Total VAT Amount: " + String.format("%.2f", totalrecalculatedVat));
                        Total_Amount_Payable.setText("Total Amount Payable: " + String.format("%.2f", totalrecalculatedGross));
                    }
                }

                String rebateStr = getCustomerRebate(customerCodes);
                double rebate = 0.0;

                try {
                    rebate = Double.parseDouble(rebateStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                double rebateAmount = (rebate / 100.0) * totalrecalculatedGross;
                amountPayableAfterRebate = totalrecalculatedGross - rebateAmount;

                // Update the database


                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Navigate to StartDeliveryActivity

            }
        }.execute();
    }*/

    /*@SuppressLint("StaticFieldLeak")
    private void performOrderCalculationAfterRefresh() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // Retrieve delivery quantities from the adapter
            List<String> deliveryQuantities = (newSalesAdapter != null) ? newSalesAdapter.getDeliveryQuantities() : new ArrayList<>();

            // Initialize total values for calculation
            int totalItemsCount = 0;
            totalrecalcualtedqty = 0;
            totalrecalculatedNet = BigDecimal.ZERO;
            totalrecalculatedVat = BigDecimal.ZERO;
            totalrecalculatedGross = BigDecimal.ZERO;
            BigDecimal payableAmountAfterRebate = BigDecimal.ZERO;

            // Fetch order data by Order ID
            Cursor cursorOrder = submitOrderDB.readDataByOrderID(orderId);
            if (cursorOrder != null && cursorOrder.getCount() > 0) {
                while (cursorOrder.moveToNext()) {
                    @SuppressLint("Range") String itemCodes = cursorOrder.getString(cursorOrder.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                    String[] itemCodeArray = itemCodes.split(",");
                    String[] quantityArray = deliveryQuantities.toArray(new String[0]);

                    // Calculate item totals
                    for (int i = 0; i < itemCodeArray.length; i++) {
                        String productId = itemCodeArray[i].trim();
                        String quantityStr = (i < quantityArray.length) ? quantityArray[i] : "0";

                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            totalrecalcualtedqty += quantity;
                            totalItemsCount++;

                            // Fetch product data by Customer Code and Product ID
                            Cursor cursorProduct = itemsByAgencyDB.readDataByCustomerCode(customerCodes, productId);
                            if (cursorProduct != null && cursorProduct.moveToFirst()) {
                                @SuppressLint("Range") BigDecimal price = BigDecimal.valueOf(cursorProduct.getDouble(cursorProduct.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE)));
                                BigDecimal itemNet = price.multiply(BigDecimal.valueOf(quantity));
                                BigDecimal itemVat = itemNet.multiply(BigDecimal.valueOf(0.05));
                                BigDecimal itemGross = itemNet.add(itemVat);

                                // Accumulate totals with precise two-decimal rounding
                                totalrecalculatedNet = totalrecalculatedNet.add(itemNet.setScale(2, RoundingMode.HALF_UP));
                                totalrecalculatedVat = totalrecalculatedVat.add(itemVat.setScale(2, RoundingMode.HALF_UP));
                                totalrecalculatedGross = totalrecalculatedGross.add(itemGross.setScale(2, RoundingMode.HALF_UP));
                            }
                            if (cursorProduct != null) cursorProduct.close();
                        } catch (NumberFormatException e) {
                            Log.e("CalculationError", "Error parsing quantity or price", e);
                        }
                    }
                }
                cursorOrder.close();
            }

            // Calculate rebate amount
            String rebateStr = getCustomerRebate(customerCodes);
            BigDecimal rebatePercentage = BigDecimal.ZERO;

            try {
                rebatePercentage = new BigDecimal(rebateStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            ///     BigDecimal rebateAmount = totalrecalculatedGross.multiply(rebatePercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            // payableAmountAfterRebate = totalrecalculatedGross.subtract(rebateAmount);

            // Update UI on main thread
            runOnUiThread(() -> {
                Total_Qty.setText("Total Qty: " + totalrecalcualtedqty);
                Total_Net_amt.setText("Total Net Amount: " + totalrecalculatedNet.setScale(2, RoundingMode.HALF_UP).toPlainString());
                Total_vat_amt.setText("Total VAT Amount: " + totalrecalculatedVat.setScale(2, RoundingMode.HALF_UP).toPlainString());
                Total_Amount_Payable.setText("Total Amount Payable: " + totalrecalculatedGross.setScale(2, RoundingMode.HALF_UP).toPlainString());

                if (aLodingDialog.isShowing()) {
                    aLodingDialog.cancel();
                }
            });
        });
    }*/

    @SuppressLint("Range")
    private String getCustomerRebate(String customerCode) {
        Cursor cursor = customerDetailsDB.getCustomerDetailsById(customerCode);
        String rebate = null;

        while (cursor.moveToNext()) {
            rebate = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
            if (rebate == null) {
                rebate = "0";

            }
        }

        return rebate;
    }

    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) > 21)) {
            // Move to the next day and set time to 12:00 AM
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        // Format the date and time as "dd/MMM/yyyy HH:mm:ss"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
        return formatter.format(calendar.getTime());
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
        int someValue = sharedPreferences.getInt(INVOICE_KEY, 0);

        // Decrement the value by 1
        someValue--;

        // Save the decremented value back to SharedPreferences (if needed)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEY, someValue);
        editor.apply();
        productInfoList.clear();
        newSaleBeanList.clear();
        newSaleBeanListSet.clear();
        newSaleBeanListss.clear();
        extranewSaleBeanListss.clear();
        listextraproducts.clear();
        Intent intent;
        String sourceActivity = getIntent().getStringExtra("sourceActivity");

        if ("TodaysOrder".equals(sourceActivity)) {
            intent = new Intent(NewSaleActivity.this, TodaysOrder.class);
        } else if ("TodaysOrder2".equals(sourceActivity)) {
            intent = new Intent(NewSaleActivity.this, TodaysOrder2.class);
        } else {
            super.onBackPressed(); // Default behavior if no sourceActivity is found
            return;
        }

        // Set the appropriate flags
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @SuppressLint({"NotifyDataSetChanged", "Range"})
    private void getNewSaleOrderDetails(String outletID, String orderId, String status, String status2) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<NewSaleBean> saleBeanList = loadSaleOrderDetails(outletID, orderId, status, status2);

            // Update the UI on the main thread
            mainHandler.post(() -> {
                if (saleBeanList.isEmpty()) {
                    Toast.makeText(NewSaleActivity.this, "No orders for this outlet", Toast.LENGTH_SHORT).show();
                } else {
                    newSaleBeanListss = convertListToMapEntryList(saleBeanList);

                    if (newSalesAdapter == null) {
                        newSalesAdapter = new NewSalesAdapter(newSaleBeanListss, NewSaleActivity.this);
                        newsalerecyclerView.setLayoutManager(new LinearLayoutManager(NewSaleActivity.this));
                        newsalerecyclerView.setAdapter(newSalesAdapter);
                        newsalerecyclerView.smoothScrollToPosition(newSaleBeanListss.size());
                        newSalesAdapter.notifyDataSetChanged();
                    } else {
                        newSalesAdapter.updateData(newSaleBeanListss);
                    }

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (newSalesAdapter != null) {
                                newSalesAdapter.filter(newText);
                            }
                            return false;
                        }
                    });

                    newSalesAdapter.setOnDeliveryQuantityExceededListener(isExceeded -> {
                        if (isExceeded) {
                            mGetSignatureButton.setEnabled(false);
                            mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.light_grey));
                        } else {
                            mGetSignatureButton.setEnabled(true);
                            mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.appColorpurple));
                        }
                    });
                }

                // Dismiss loading dialog
                aLodingDialog.cancel();
            });
        });
    }

    private List<NewSaleBean> convertListToMapEntryList(List<NewSaleBean> list) {
        Set<String> existingKeys = new HashSet<>();

        // First, gather existing product names in a set to avoid duplicates
        for (NewSaleBean entry : newSaleBeanListss) {
            if (entry.getProductName() != null) {
                existingKeys.add(entry.getProductName().trim());
            }
        }

        // Create a new list to add entries from the input list
        List<NewSaleBean> newEntries = new ArrayList<>();

        for (NewSaleBean entry : list) {
            String keyToCheck = (entry.getProductName() != null) ? entry.getProductName().trim() : null;
            if (keyToCheck != null && !existingKeys.contains(keyToCheck)) {
                newEntries.add(new NewSaleBean(
                        entry.getProductID(),
                        keyToCheck,
                        entry.getItemCode(),
                        entry.getBarcode(),
                        entry.getApprovedQty(),
                        entry.getSellingPrice(),
                        entry.getVanstock(),
                        entry.getUom()
                ));
                existingKeys.add(keyToCheck);
            }
        }


        // Safely add the new entries to your original list
        newSaleBeanListss.addAll(newEntries);
        return newSaleBeanListss;


    }

    @SuppressLint("Range")
    private List<NewSaleBean> loadSaleOrderDetails(String outletID, String orderId, String status, String status2) {
        List<NewSaleBean> saleBeanList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = submitOrderDB.readAllorderDataByOutletIDAndStatus(outletID, orderId, status, status2);
            if (cursor.getCount() == 0) {
                return saleBeanList;
            }

            while (cursor.moveToNext()) {
                orderidforNewSale = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                outletId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                deliveryStatus = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));

                String itemid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                String itemCodes = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                String approvedQty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));

                String[] itemIDs = itemid.split(",");
                String[] itemCodeArray = itemCodes.split(",");
                String[] approvedQtyArray = approvedQty.split(",");

                for (int i = 0; i < itemIDs.length; i++) {
                    String itemCode = itemCodeArray[i].trim();
                    Cursor cursorA = itemsByAgencyDB.readDataByCustomerCode(customerCodes, itemCode);
                    List<NewSaleBean> tempSaleBeanList = new ArrayList<>();

                    try {
                        while (cursorA.moveToNext()) {
                            String sellingPrice = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                            String itemBarcode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                            String uom = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                            String plucode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                            String itemname = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                            Cursor cursorB = stockDB.readonproductid(itemIDs[i]);
                            if (cursorB.getCount() != 0) {
                                while (cursorB.moveToNext()) {
                                    String itemsStock = cursorB.getString(cursorB.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                                    // Create a new sale bean
                                    NewSaleBean saleBean = new NewSaleBean(
                                            itemIDs[i],
                                            itemname,
                                            itemCodeArray[i],
                                            itemBarcode,
                                            plucode,
                                            approvedQtyArray[i],
                                            sellingPrice,
                                            itemsStock,
                                            uom
                                    );
                                    tempSaleBeanList.add(saleBean);
                                }
                            } else {
                                // Create a new sale bean with 0 stock
                                NewSaleBean saleBean = new NewSaleBean(
                                        itemIDs[i],
                                        itemname,
                                        itemCodeArray[i],
                                        itemBarcode,
                                        plucode,
                                        approvedQtyArray[i],
                                        sellingPrice,
                                        "0",
                                        uom
                                );
                                tempSaleBeanList.add(saleBean);
                            }
                        }

                        // After populating tempSaleBeanList, add it to saleBeanList
                        saleBeanList.addAll(tempSaleBeanList);
                    } finally {
                        if (cursorA != null && !cursorA.isClosed()) {
                            cursorA.close();
                        }
                    }
                }
            }

            // Check conditions and add items from saleBeanList to newSaleBeanListss
            for (NewSaleBean saleBean : saleBeanList) {
                // Check if the item already exists in newSaleBeanListss before adding
                boolean exists = false;
                for (NewSaleBean existingBean : newSaleBeanListss) {
                    if (existingBean.getProductID().equals(saleBean.getProductID())) {
                        exists = true;
                        break;
                    }
                }

                // If it doesn't exist, add it to newSaleBeanListss
                if (!exists) {
                    newSaleBeanListss.add(saleBean);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return newSaleBeanListss; // Return the updated newSaleBeanListss
    }


    private AlertDialog verificationDialog;

    public void showVerificationDialog(Context context) {
        // Check if the dialog has already been shown
        if (!isVerificationDialogShown) {
            verificationDialog = new AlertDialog.Builder(context)
                    .setTitle("Warning")
                    .setMessage("The delivery quantity might be exceeding the van stock, please verify before processing.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle positive button click
                        }
                    })
                    .setCancelable(false)
                    .create();

            if (!((NewSaleActivity) context).isFinishing()) {
                verificationDialog.show();
            }

            // Set the flag to true after the dialog is shown
            isVerificationDialogShown = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (verificationDialog != null && verificationDialog.isShowing()) {
            verificationDialog.dismiss();
        }
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

    private long generateInvoiceNumber() {
        long min = 100000L;  // This is the smallest 15-digit number
        long max = 999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }

    public String generateNextInvoiceNumber(String lastvoiceInvoicenumber) {
        // Assuming the lastInvoice is in the format "D3S160920240000"
        String prefix = lastvoiceInvoicenumber.substring(0, 11); // SVF180824
        String numericPart = lastvoiceInvoicenumber.substring(11); // 00001

        // Increment the numeric part
        int nextNumber = Integer.parseInt(numericPart) + 1;

        // Format the number to keep leading zeros
        String newInvoiceNumber = String.format("%04d", nextNumber);

        return newInvoiceNumber;
    }

    private long generateRandomOrderID() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SIGNATURE) {
                byte[] signatureByteArray = data.getByteArrayExtra("signatureByteArray");
                signatureBitmap = BitmapFactory.decodeByteArray(signatureByteArray, 0, signatureByteArray.length);

                // Convert the signatureBitmap to JPEG format
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //saveSignatureToGallery(signatureBitmap, "signature");              //  byte[] jpgByteArray = stream.toByteArray();

                // Set the converted signatureBitmap to the ImageView
                signatureImageView.setImageBitmap(signatureBitmap);

                mSaveButtonPrint.setEnabled(true);
                mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
            } /*else if (requestCode == REQUEST_CODE_BILL) {
                billBitmap = (Bitmap) data.getExtras().get("data");
                billImageView.setImageBitmap(billBitmap);
            }*/
        }
    }

    private void saveSignatureToGallery(Bitmap signature, String prefix) {
        if (signature != null) {
            int targetWidth = 150;
            int targetHeight = 150;

            Bitmap resizedSignature = resizeBitmap(signature, targetWidth, targetHeight);

            File signatureFile = saveBitmapToJPG(resizedSignature, prefix);
            if (signatureFile != null) {
                scanMediaFile(signatureFile);
                Toast.makeText(this, "Signature saved to gallery", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Signature bitmap is null", Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap resizeBitmap(Bitmap image, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
    }

    private File saveBitmapToJPG(Bitmap bitmap, String prefix) {
        File file = new File(getAlbumStorageDir("SignaturePad"), String.format("%s_%d.jpg", prefix, System.currentTimeMillis()));
        try (OutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void scanMediaFile(File signatureFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(signatureFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }


    private void saveImagesToGallery() {
        if (signatureBitmap != null) {
            String signatureFileName = "signature_" + UUID.randomUUID().toString() + ".jpeg";
            String billFileName = "bill_" + UUID.randomUUID().toString() + ".jpeg";

            saveImageToGallery(signatureBitmap, signatureFileName);
            //saveImageToGallery(billBitmap, billFileName);

            Toast.makeText(this, "Images saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Capture both signature and bill first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Insert the image metadata into MediaStore
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Save the bitmap to the gallery
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, null);
    }


    private boolean checkAndRequestPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
                    return false;
                }
            }
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private void getProductOnCustomerCode(String customerCode) {
        listextraproducts.clear();  // Clear previous product list

        // Step 1: Fetch products that are already in the order
        Set<String> orderedProductIDs = new HashSet<>();
        Cursor orderCursor = submitOrderDB.readAllorderDataByOutletIDAndStatus(outletID, orderId, "PENDING FOR DELIVERY", "DELIVERED");

        if (orderCursor != null && orderCursor.getCount() > 0) {
            while (orderCursor.moveToNext()) {
                @SuppressLint("Range") String orderedProductID = orderCursor.getString(orderCursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                String[] orderedProductIDsArray = orderedProductID.split(",");
                Collections.addAll(orderedProductIDs, orderedProductIDsArray);
            }
            orderCursor.close();
        }

        // Step 2: Fetch available products
        Cursor itemsCursor = itemsByAgencyDB.readDataByCustomerCodes(customerCode);
        if (itemsCursor.getCount() != 0) {
            while (itemsCursor.moveToNext()) {
                @SuppressLint("Range") String productID = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                @SuppressLint("Range") String customercode = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_CODE));

                // Skip products that are already in the order
                if (orderedProductIDs.contains(productID)) {
                    continue;
                }

                // Step 3: Check stock for the product in the van
                Cursor stockCursor = stockDB.readonproductid(productID);
                if (stockCursor != null && stockCursor.getCount() > 0) {
                    while (stockCursor.moveToNext()) {
                        @SuppressLint("Range") String productId = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_PRODUCTID));
                        @SuppressLint("Range") String avlQTY = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                        int availableStock = Integer.parseInt(avlQTY);
                        System.out.println("Available Quantity: " + availableStock);

                        if (availableStock > 0) {
                            Cursor itemcursor = itemsByAgencyDB.readProdcutDataByproductIdAndCustomerCode(customercode, productId);
                            if (itemcursor != null && itemcursor.getCount() > 0) {
                                while (itemcursor.moveToNext()) {
                                    @SuppressLint("Range") String productName = itemcursor.getString(itemcursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!listextraproducts.contains(productName)) {
                                                listextraproducts.add(productName);
                                                System.out.println("Adding product available in van: " + productName);
                                            }

                                            // Set the updated adapter
                                            endsWithAgencyArrayAdapter = new EndsWithAgencyArrayAdapter(NewSaleActivity.this,
                                                    R.layout.list_item_text, R.id.list_textView_value, listextraproducts);
                                            spinner.setAdapter(endsWithAgencyArrayAdapter);
                                        }
                                    });
                                }
                                itemcursor.close();
                            }
                        }
                    }
                    stockCursor.close();
                }
            }
        }
    }

   /* private List<NewSaleBean> AddProductsToDeliver(String productName) {
        List<NewSaleBean> saleBeanList1 = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = itemsByAgencyDB.readProdcutDataByName(productName);
            if (cursor.getCount() == 0) {
                return saleBeanList1;
            }

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String itemCodes = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                Cursor cursorA = itemsByAgencyDB.readDataByCustomerCode(customerCodes, itemCodes);
                List<NewSaleBean> tempSaleBeanList1 = new ArrayList<>();

                try {
                    while (cursorA.moveToNext()) {
                        @SuppressLint("Range") String sellingPrice = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                        @SuppressLint("Range") String itemBarcode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                        @SuppressLint("Range") String uom = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                        @SuppressLint("Range") String plucode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                        @SuppressLint("Range") String itemname = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        @SuppressLint("Range") String itemID = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));

                        Cursor cursorB = stockDB.readonproductid(itemID);
                        if (cursorB.getCount() != 0) {
                            while (cursorB.moveToNext()) {
                                @SuppressLint("Range") String itemsStock = cursorB.getString(cursorB.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                                // Create a new sale bean
                                NewSaleBean saleBean = new NewSaleBean(
                                        itemID,
                                        itemname,
                                        itemCodes,
                                        itemBarcode,
                                        plucode,
                                        "N/A",
                                        sellingPrice,
                                        itemsStock,
                                        uom
                                );
                                tempSaleBeanList1.add(saleBean);
                            }
                        } else {
                            // Create a new sale bean with 0 stock
                            NewSaleBean saleBean = new NewSaleBean(
                                    itemID,
                                    itemname,
                                    itemCodes,
                                    itemBarcode,
                                    plucode,
                                    "N/A",
                                    sellingPrice,
                                    "0",
                                    uom
                            );
                            tempSaleBeanList1.add(saleBean);
                        }
                    }

                    // After populating tempSaleBeanList, add it to saleBeanList
                    saleBeanList1.addAll(tempSaleBeanList1);
                } finally {
                    if (cursorA != null && !cursorA.isClosed()) {
                        cursorA.close();
                    }
                }
            }

            for (NewSaleBean saleBean : saleBeanList1) {
                // Check if the item already exists in newSaleBeanListss before adding
                boolean exists = false;
                for (NewSaleBean existingBean : newSaleBeanListss) {
                    if (existingBean.getProductID().equals(saleBean.getProductID())) {
                        exists = true;
                        break;
                    }
                }

                // If it doesn't exist, add it to newSaleBeanListss
                if (!exists) {
                    newSaleBeanListss.add(saleBean);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return newSaleBeanListss;

    }*/


    @SuppressLint({"NotifyDataSetChanged", "Range"})
    private void getNewSaleOrderDetails2(String productName) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<NewSaleBean> saleBeanList = AddProductsToDeliver(productName);

            // Update the UI on the main thread
            mainHandler.post(() -> {
                if (saleBeanList.isEmpty()) {
                    Toast.makeText(NewSaleActivity.this, "No orders for this outlet", Toast.LENGTH_SHORT).show();
                } else {
                    if (newSalesAdapter == null) {
                        newSalesAdapter = new NewSalesAdapter(newSaleBeanListss, NewSaleActivity.this);
                        newsalerecyclerView.setLayoutManager(new LinearLayoutManager(NewSaleActivity.this));
                        newsalerecyclerView.setAdapter(newSalesAdapter);
                    } else {
                        // Instead of replacing the list, add new items and notify adapter
                        newSalesAdapter.addItems(saleBeanList);
                    }

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (newSalesAdapter != null) {
                                newSalesAdapter.filter(newText);
                            }
                            return false;
                        }
                    });

                    newSalesAdapter.setOnDeliveryQuantityExceededListener(isExceeded -> {
                        if (isExceeded) {
                            mGetSignatureButton.setEnabled(false);
                            mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.light_grey));
                        } else {
                            mGetSignatureButton.setEnabled(true);
                            mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(NewSaleActivity.this, R.color.appColorpurple));
                        }
                    });
                }

                // Dismiss loading dialog
                aLodingDialog.cancel();
            });
        });
    }

    private List<NewSaleBean> AddProductsToDeliver(String productName) {
        List<NewSaleBean> saleBeanList1 = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = itemsByAgencyDB.readProdcutDataByName(productName);
            if (cursor.getCount() == 0) {
                return saleBeanList1;
            }

            while (cursor.moveToNext()) {
                @SuppressLint("Range") String itemCodes = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                Cursor cursorA = itemsByAgencyDB.readDataByCustomerCode(customerCodes, itemCodes);

                try {
                    while (cursorA.moveToNext()) {
                        @SuppressLint("Range") String sellingPrice = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                        @SuppressLint("Range") String itemBarcode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                        @SuppressLint("Range") String uom = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                        @SuppressLint("Range") String itemname = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        @SuppressLint("Range") String itemID = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                        @SuppressLint("Range") String plucode = cursorA.getString(cursorA.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                        Cursor cursorB = stockDB.readonproductid(itemID);
                        String itemsStock = "0"; // Default to 0 stock
                        if (cursorB.getCount() != 0) {
                            while (cursorB.moveToNext()) {
                                itemsStock = cursorB.getString(cursorB.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                            }
                        }

                        NewSaleBean productBean = new NewSaleBean(itemID, itemname, itemCodes, itemBarcode, plucode,"0", sellingPrice, itemsStock, uom);

                        // Add only if it does not already exist in the list
                        if (!isProductAlreadyAdded(productBean)) {
                            saleBeanList1.add(productBean);
                            newSaleBeanListss.add(productBean);
                            extranewSaleBeanListss.add(productBean);
                        }
                    }
                } finally {
                    if (cursorA != null && !cursorA.isClosed()) {
                        cursorA.close();
                    }
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        System.out.println("extra items : " + extranewSaleBeanListss);
        return saleBeanList1;
    }

    private boolean isProductAlreadyAdded(NewSaleBean product) {
        for (NewSaleBean existingBean : newSaleBeanListss) {
            if (existingBean.getProductID().equals(product.getProductID())) {
                return true;
            }
        }
        return false;

    }


    @SuppressLint("StaticFieldLeak")
    private void performOrderCalculationAfterRefresh() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                totalItemsCount = 0;
                totalrecalcualtedqty = 0;
                totalrecalculatedNet = BigDecimal.ZERO;
                totalrecalculatedVat = BigDecimal.ZERO;
                totalrecalculatedGross = BigDecimal.ZERO;

                Map<String, NewSaleBean> uniqueProducts = new HashMap<>();

                if (newSalesAdapter != null) {
                    for (NewSaleBean item : newSalesAdapter.getItemList()) {
                        uniqueProducts.put(item.getProductID(), item);
                    }
                }

                if (extranewSaleBeanListss != null) {
                    for (NewSaleBean item : extranewSaleBeanListss) {
                        uniqueProducts.put(item.getProductID(), item); // This will replace duplicates from adapter if same ProductID exists
                    }
                }

                for (NewSaleBean product : uniqueProducts.values()) {
                    try {
                        String productId = product.getProductID();
                        String productName = product.getProductName();
                        String deliveryQtyStr = product.getDeliveryQty();

                        if (deliveryQtyStr == null || deliveryQtyStr.isEmpty()) {
                            deliveryQtyStr = "0";
                        }

                        int quantity = Integer.parseInt(deliveryQtyStr);
                        totalrecalcualtedqty += quantity;
                        totalItemsCount++;

                        Cursor cursorProduct = null;

                        if (productId != null && !productId.isEmpty()) {
                            cursorProduct = itemsByAgencyDB.readDataByCustomerCode(customerCodes, productId);
                        }

                        if ((cursorProduct == null || !cursorProduct.moveToFirst()) && productName != null) {
                            cursorProduct = itemsByAgencyDB.readDataByCustomerCodeAndProdName(customerCodes, productName);
                        }

                        if (cursorProduct != null && cursorProduct.moveToFirst()) {
                            BigDecimal price = BigDecimal.valueOf(cursorProduct.getDouble(cursorProduct.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE)));
                            BigDecimal itemNet = price.multiply(BigDecimal.valueOf(quantity));
                            BigDecimal itemVat = itemNet.multiply(BigDecimal.valueOf(0.05));
                            BigDecimal itemGross = itemNet.add(itemVat);

                            totalrecalculatedNet = totalrecalculatedNet.add(itemNet.setScale(2, RoundingMode.HALF_UP));
                            totalrecalculatedVat = totalrecalculatedVat.add(itemVat.setScale(2, RoundingMode.HALF_UP));
                            totalrecalculatedGross = totalrecalculatedGross.add(itemGross.setScale(2, RoundingMode.HALF_UP));
                        }

                        if (cursorProduct != null) {
                            cursorProduct.close();
                        }

                    } catch (NumberFormatException e) {
                        Log.e("CalculationError", "Invalid quantity for product: " + product.getProductName(), e);
                    }
                }

                runOnUiThread(() -> {
                    Total_Qty.setText("Total Qty: " + totalrecalcualtedqty);
                    Total_Net_amt.setText("Total Net Amount: " + totalrecalculatedNet.setScale(2, RoundingMode.HALF_UP).toPlainString());
                    Total_vat_amt.setText("Total VAT Amount: " + totalrecalculatedVat.setScale(2, RoundingMode.HALF_UP).toPlainString());
                    Total_Amount_Payable.setText("Total Amount Payable: " + totalrecalculatedGross.setScale(2, RoundingMode.HALF_UP).toPlainString());

                    if (aLodingDialog.isShowing()) {
                        aLodingDialog.cancel();
                    }
                });

            } catch (Exception e) {
                Log.e("CalculationError", "Unexpected error during order calculation", e);
            }
        });

    }

    private void executeMethodsSequentially(String customerCode, String outletID, String orderId) {
        // Start the first method in a new thread
        Thread methodOneThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Perform the first method
                getProductOnCustomerCode(customerCode);  // First method

                // Once the first method is completed, execute the second method
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Perform UI operations on the main thread
                        getNewSaleOrderDetails(outletID, orderId, "PENDING FOR DELIVERY", "DELIVERED");
                    }
                });
            }
        });

        methodOneThread.start();  // Start the thread
    }
}


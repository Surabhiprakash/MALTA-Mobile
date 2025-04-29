package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.NewSaleActivity.BLUETOOTH_ENABLE_REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Dahboard.AnalysisGraph;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.DummyDb;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SellingPriceOfItemBsdCustomerDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.TotalApprovedOrderBsdOnItem;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetails;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetailsResponse;
import com.malta_mqf.malta_mobile.Model.AllCustomerDetails;
import com.malta_mqf.malta_mobile.Model.AllCustomerDetailsResponse;
import com.malta_mqf.malta_mobile.Model.AllItemDeatilsById;
import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;
import com.malta_mqf.malta_mobile.Model.ApprovedOrdersBasedOnVanId;
import com.malta_mqf.malta_mobile.Model.ApprovedOrdersDetailsBsdOnVanIdResponse;
import com.malta_mqf.malta_mobile.Model.CancelOrderResponse;
import com.malta_mqf.malta_mobile.Model.DeliveredAndReturnTransactionBean;
import com.malta_mqf.malta_mobile.Model.DeliveredOrderItemLevelDetails;
import com.malta_mqf.malta_mobile.Model.DeliveredOrderLevelDetails;
import com.malta_mqf.malta_mobile.Model.DeliveryOrderResponse;
import com.malta_mqf.malta_mobile.Model.ExtraOrderSyncResponse;
import com.malta_mqf.malta_mobile.Model.ItemWiseOrdersBasedOnVanPowiseDetails;
import com.malta_mqf.malta_mobile.Model.LoadINSyncResponse;
import com.malta_mqf.malta_mobile.Model.OrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OutletsById;
import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Model.ReturnOrderItemLevelDetails;
import com.malta_mqf.malta_mobile.Model.ReturnOrderLevelDetails;
import com.malta_mqf.malta_mobile.Model.ReturnOrderWithoutInvoiceResponse;
import com.malta_mqf.malta_mobile.Model.ReturnWithoutInvoiceDetails;
import com.malta_mqf.malta_mobile.Model.TotalItemsPerVanIdPoResponse;
import com.malta_mqf.malta_mobile.Model.TotalPerItemsByVanId;
import com.malta_mqf.malta_mobile.Model.TotalPerItemsByVanIdResponse;
import com.malta_mqf.malta_mobile.Model.VanLoadDataForVanDetails;
import com.malta_mqf.malta_mobile.Model.VanLoadDetailsBasedOnVanResponse;
import com.malta_mqf.malta_mobile.Model.VanStockDetails;
import com.malta_mqf.malta_mobile.Model.VanStockSyncResponse;
import com.malta_mqf.malta_mobile.Model.returnOrderResponse;
import com.malta_mqf.malta_mobile.Model.vanStockTransactionResponse;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.Constants;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;
import com.malta_mqf.malta_mobile.Utilities.DatabaseUtils;
import com.malta_mqf.malta_mobile.Utilities.LogcatCapture;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends BaseActivity {
    CardView orderCardView, loadUnloadCardView, startDeliverCardView,analysisGraphCardView;
    RelativeLayout relativeLayout1, relativeLayout3;
    LinearLayout linearLayout1;
    private ConnectivityReceiver connectivityReceiver;
    private ProgressDialog newProgressDialog;
    ImageView start, status;
    UserDetailsDb userDetailsDb;
    public static String name, email, empcode, vehiclenum;
    public static String userID, vanID;
    SubmitOrderDB submitOrderDB;
    ProgressDialog progressDialog;
    ALodingDialog aLodingDialog;


    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    AllAgencyDetailsDB allAgencyDetailsDB;
    DatePickerDialog datePickerDialog;
    ItemsByAgencyDB itemsByAgencyDB;
    int totalOrders = 0, totalapprovedorder = 0, totalOrderToDeliver = 0, totalitemstoSync = 0, totalreturnSync = 0, totalCancelSync = 0,totalreturnWithoutInvoiceSync=0,totalVanStockSync=0;
    ;
    ProgressDialog progressDialogs;
    boolean hasFailure = false;
    TextView userName, emailId, empCode;
    ApprovedOrderDB approvedOrderDB;
    private static final int PERMISSION_REQUEST_CODE = 123;

    TotalApprovedOrderBsdOnItem totalApprovedOrderBsdOnItemDB;
    SellingPriceOfItemBsdCustomerDB sellingPriceOfItemBsdCustomerDB;
    ReturnDB returnDB;
    StockDB stockDB;
    //  Set<ProductInfo> productIdQty = new LinkedHashSet<>();
//    String orderId, vanId, userId, outletId, productIds, ItemCodes, quantities, orderDate, orderStatus,dateTime;
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Handler handler = new Handler(Looper.getMainLooper());
    private AlertDialog currentDialog;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String LAST_CLICK_DATE = "lastClickDate";

    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 200;


    private ProgressBar progressBar;
    private TextView progressPercentage;
    private AlertDialog progressBarDialog;
    private int progressStep;
    private int syncTasksCompleted = 0;
    private final int totalSyncTasks = 4;
    private int totalExtraorderSync = 0;

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private LogcatCapture logcatCapture;
    DummyDb dbHelper ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkAndRequestPermissions()) {
            // All permissions granted, proceed with your functionality
            enableBluetoothIfNecessary();
        }
      //  System.setProperty("user.timezone", "Asia/Dubai");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      //  dbHelper = new DummyDb(this);
        //SQLiteDatabase database = dbHelper.getWritableDatabase();
        userDetailsDb = new UserDetailsDb(this);
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        approvedOrderDB = new ApprovedOrderDB(this);
        returnDB = new ReturnDB(this);
        stockDB=new StockDB(this);
        aLodingDialog = new ALodingDialog(this);
        sellingPriceOfItemBsdCustomerDB = new SellingPriceOfItemBsdCustomerDB(this);
        totalApprovedOrderBsdOnItemDB = new TotalApprovedOrderBsdOnItem(this);
        start = findViewById(R.id.start_icon);
        orderCardView = findViewById(R.id.orderscv);
        loadUnloadCardView = findViewById(R.id.load_unloadcv);
        startDeliverCardView = findViewById(R.id.startdeliverycv);
        analysisGraphCardView = findViewById(R.id.ANALYSISGRAPH);
        status = findViewById(R.id.active_icon);
        userName = findViewById(R.id.usernameTextView);
        emailId = findViewById(R.id.emailTextView);
        empCode = findViewById(R.id.empIdTextView);

        progressStep = 0;

        submitOrderDB = new SubmitOrderDB(this);
        DatabaseUtils.copyDatabaseToExternalStorage(this, "SubmitOrderDB.db"); // Replace with your actual database name
        DatabaseUtils.copyDatabaseToExternalStorage(this, "MyReturnsDB.db"); // Replace with your actual database name
        DatabaseUtils.copyDatabaseToExternalStorage(this, "approved.db"); // Replace with your actual database name
        DatabaseUtils.copyDatabaseToExternalStorage(this, "stockdb.db"); // Replace with your actual database name

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Syncing...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        orderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NewOrderActivity.class);
                startActivity(i);
            }
        });

        loadUnloadCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoadInventory.class);
                startActivity(i);
            }
        });

        startDeliverCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, StartDeliveryActivity.class);
                startActivity(i);
            }
        });

        analysisGraphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the AnalysisGraphActivity
                Intent intent = new Intent(MainActivity.this, AnalysisGraph.class);
                startActivity(intent);
            }
        });
        relativeLayout1 = findViewById(R.id.startrelative);
        linearLayout1 = findViewById(R.id.linearlayout1);
        connectivityReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);


        getActionBar();

        updateConnectionStatus();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastClickDate = prefs.getString(LAST_CLICK_DATE, "");

// Get current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        if (!currentDate.equals(lastClickDate)) {
            // Enable the start button
            start.setEnabled(true);

            // Set click listener for the start button
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    relativeLayout1.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.VISIBLE);

                    // Store current date as last clicked date
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(LAST_CLICK_DATE, currentDate);
                    editor.apply();
                }
            });
        } else {
            // Disable the start button
            start.setEnabled(false);
            relativeLayout1.setVisibility(View.GONE);
            linearLayout1.setVisibility(View.VISIBLE);
        }

        checkAndRequestPermissions();


        getUserDetails();

        submitOrderDB.deleteOldRecords();
        approvedOrderDB.deleteOldRecords();
        totalApprovedOrderBsdOnItemDB.deleteOldRecords();
        returnDB.deleteOldRecords();
        submitOrderDB.updateOrderStatus();
        approvedOrderDB.updateOrderStatus();
        totalApprovedOrderBsdOnItemDB.totaldeleteByStatusAfterSyncByExpectedDelivery();
      //  requestStoragePermission();
    //    LogcatCapture.captureLogToFile(this);

        // Initialize your custom logger
        //initializeLogger();
        if (!Settings.System.canWrite(this)) {
            requestWriteSettingsPermission();
        } else {
            // Permission already granted, proceed with your task
            proceedWithTask();
        }
    //  logcatCapture = new LogcatCapture();
        //getLifecycle().addObserver(logcatCapture);
        LogcatCapture.captureLogToFile();
        initializeLogger();
    }

/*   @Override
    protected void onStart() {
        super.onStart();
        startLogCapture();
    }

    private void startLogCapture() {
        logcatCapture.startLogCapture(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        logcatCapture.stopLogCapture();
    }*/


    private void requestWriteSettingsPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("To modify system settings, please allow this app to write system settings.")
                .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Permission denied.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void proceedWithTask() {
        // The code to modify system settings goes here
        Toast.makeText(this, "Proceeding with system settings modification...", Toast.LENGTH_SHORT).show();
    }

    private void showPermissionDeniedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied")
                .setMessage("You have denied the permission to write system settings. The app won't be able to modify system settings without this permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted
            CustomerLogger.initialize(getApplicationContext());
        }
    }
    private void initializeLogger() {
        CustomerLogger.initialize(this);
        CustomerLogger.i("MainActivity", "Logger initialized successfully");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("onBackPressed");
        orderCardView = null;
        loadUnloadCardView = null;
        startDeliverCardView = null;
        userDetailsDb = null;
        // name = null;
        email = null;
        empcode = null;
        //   userID=null;
        // vanID=null;
        submitOrderDB = null;
        allCustomerDetailsDB = null;
        outletByIdDB = null;
        allAgencyDetailsDB = null;
        itemsByAgencyDB = null;
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
   /* @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
        cardView1=null;
        cardView2=null;
        cardView3=null;
        userDetailsDb=null;
        name=null;
        email=null;
        empcode=null;
        userID=null;
      //  vanID=null;
     //   submitOrderDB=null;
        allCustomerDetailsDB=null;
        outletByIdDB=null;
        allAgencyDetailsDB=null;
        itemsByAgencyDB=null;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Handle logout action
            showLogoutConfirmationDialog();
            return true;
        } else if (id == R.id.action_sync) {
            if (isOnline()) {
                syncOrders();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        } else if (id == R.id.product_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
               /* showProgressDialogWithDuration(30000, () -> {
                    // Success callback logic
                    showAlert("Success!", "Products synced successfully");
                }, true);*/
                getCustomerDetails();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }


            // getOutletDetailsById();
            return true;
        } else if (id == R.id.action_Aprvod_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {

                setupDatePicker();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        } else if (id == R.id.action_delivered_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                DeliveredOrderSync();
                LOADSync();
                syncVanStock();
                RejectOrderSync();
                ReturnOrderSync();
                SyncExtraDeliveredOrders();
                ReturnOrderSyncWithoutInvoice();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        } else if (id == R.id.action_load_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                //setupDatePicker2();
                //  submitOrderDB.OrderdeleteAllData();
                LOADSync();
                syncVanStock();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        } else if (id == R.id.action_rejected) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                RejectOrderSync();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;

        }
        if (id == R.id.action_return_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                ReturnOrderSync();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }
        if (id == R.id.action_extra_order) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                SyncExtraDeliveredOrders();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }if (id == R.id.action_return_without_invoice_sync) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                ReturnOrderSyncWithoutInvoice();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }if(id == R.id.action_van_stock){
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                syncVanStock();
                LOADSync();
                //submitOrderDB.OrderdeleteAllData();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }
        if (id == R.id.action_Transaction) {
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {
                showAlertDialog();
                //submitOrderDB.OrderdeleteAllData();
                item.setEnabled(false);

                // Save the state in SharedPreferences
                SharedPreferences prefs = getSharedPreferences("menu_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("menu_item_disabled", true);
                editor.apply();

                return true;
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }
        if(id==R.id.action_approved_order_restore){
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {

                setupDatePicker3();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }
        if(id==R.id.action_Loadin_restore){
            if (checkServerStatus()) {
                showAlert("Warning!", "Server is down or unreachable");
            } else if (isOnline()) {

                setupDatePicker4();
            } else {
                showAlert("Warning!", "Please check your internet connection");
            }
            return true;
        }
        // If none of the above conditions match, call the superclass method
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Check if the item should be disabled based on SharedPreferences
        SharedPreferences prefs = getSharedPreferences("menu_prefs", MODE_PRIVATE);
        boolean isDisabled = prefs.getBoolean("menu_item_disabled", false);

        if (isDisabled) {
            MenuItem menuItem = menu.findItem(R.id.action_Transaction);
            if (menuItem != null) {
                menuItem.setEnabled(false);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
                .setMessage("This action will sync data from the previous 7 days. It can only be performed once after the app is re-installed.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAllDeliveredAndReturnTransaction(vanID);
                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialog2(String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning")
                .setMessage("Are you sure you want to Re-store previous orders for the date "+ date +"?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //ApprovedOrderSync(date);
                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialog3(String date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!!!")
                .setMessage("Please re-store only if you have not performed a Van Stock Sync or if you have approved orders in the backend prior to uninstalling the application.!!!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TotalItemsApprovedSync(date,"1975-08-05%2012:00:00");
                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);


                            String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                            // Handle the selected date (e.g., display it, use it somewhere, etc.)
                            Cursor cursor=userDetailsDb.readAllData();
                            while (cursor.moveToNext()){
                                @SuppressLint("Range") String lastApprovedDate=cursor.getString(cursor.getColumnIndex(UserDetailsDb.LOGIN_DATE_TIME));
                                System.out.println("LastApprovedDate is: "+lastApprovedDate);

                                lastApprovedDate=lastApprovedDate.replace(" ","%20");
                                TotalItemsApprovedSync(selectedDate,lastApprovedDate);
                            }

                    }
                },
                year,
                month,
                dayOfMonth);

        // Set default selection to tomorrow's date
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Move calendar to tomorrow
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

        datePickerDialog.show();
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

    private void setupDatePicker2() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        Calendar today = Calendar.getInstance();
                        if (selectedCalendar.before(today)) {
                            // Notify the user or adjust the selection to current date if it's before today
                            Toast.makeText(getApplicationContext(), "Please select today or a future date", Toast.LENGTH_SHORT).show();
                            // Optionally, reset the date picker to today
                            view.updateDate(year, month, dayOfMonth);
                        } else {
                            String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                            // Handle the selected date (e.g., display it, use it somewhere, etc.)
                            //LOADSync(selectedDate);
                        }
                    }
                },
                year,
                month,
                dayOfMonth);

        // Set default selection to tomorrow's date
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Move calendar to tomorrow
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

        datePickerDialog.show();
    }
    private void setupDatePicker3() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);


                        String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                        // Handle the selected date (e.g., display it, use it somewhere, etc.)
                        showAlertDialog2(selectedDate);
                    }
                },
                year,
                month,
                dayOfMonth);

        // Set default selection to tomorrow's date
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Move calendar to tomorrow
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

        datePickerDialog.show();
    }

    private void setupDatePicker4() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);


                        String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                        // Handle the selected date (e.g., display it, use it somewhere, etc.)
                        showAlertDialog3(selectedDate);
                    }
                },
                year,
                month,
                dayOfMonth);

        // Set default selection to tomorrow's date
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Move calendar to tomorrow
        datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

        datePickerDialog.show();
    }

    @SuppressLint("Range")
    private boolean checkServerStatus() {
        String serverUrl = "http://sfa.mqftrading.com:8082/"; // Replace with your actual server URL

        try {
            URL url = new URL(serverUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("HEAD");

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Server is up
                return true;
            } else {
                // Server is down or unreachable
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions, such as MalformedURLException or IOException
            return false;
        }
    }

    private void showProgressDialogWithDuration(int durationInMillis, Runnable onSuccessCallback, boolean showSuccessAlert) {
        progressDialogs = new ProgressDialog(this);
        progressDialogs.setMessage("Syncing, please wait...");
        progressDialogs.setCancelable(false);
        progressDialogs.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (progressDialogs.isShowing()) {
                progressDialogs.dismiss();

                // Execute the success callback only if the dialog was not dismissed
                if (onSuccessCallback != null && showSuccessAlert) {
                    onSuccessCallback.run();
                }
            }
        }, durationInMillis);
    }


    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform logout actions
                logout();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    private void updateConnectionStatus() {
        Drawable markerDrawable;

        if (isOnline()) {
            // Device is online
            markerDrawable = ContextCompat.getDrawable(this, R.drawable.online_marker);

            // Dismiss status after a certain delay (e.g., 2 seconds)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    status.setVisibility(View.VISIBLE);
                }
            }, 2000); // Adjust the delay as needed
        } else {
            // Device is offline
            markerDrawable = ContextCompat.getDrawable(this, R.drawable.offline_marker);

            // Make sure status is visible when offline
            status.setVisibility(View.VISIBLE);
        }

        // Set the marker drawable to the profile ImageView
        status.setBackground(markerDrawable);
    }


    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnectionStatus();
        }
    }

    @SuppressLint("Range")
    private void getUserDetails() {
        Cursor cursor = userDetailsDb.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No User Data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                name = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_NAME));
                System.out.println("Name in main is: " + name);
                email = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_EMAIL));
                empcode = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_EMP_CODE));
                vehiclenum = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));
                userID = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_USERID));
                vanID = cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
                System.out.println("vanID:"+vanID);
            }
            Log.d("UserID", userID);
            System.out.println("vehicle" + vehiclenum+"   ");
            userName.setText(name +"     "+" 17-04-2025");//check for url
            emailId.setText(email);
            empCode.setText(vehiclenum);
        }
        ///name = null;
        email = null;
        empcode = null;
        cursor.close();
    }

    private void showProgressBarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle("Please wait, data syncing...");
        progressBar = dialogView.findViewById(R.id.progressBar);
        progressPercentage = dialogView.findViewById(R.id.progressPercentage);

        progressBarDialog = builder.create();
        progressBarDialog.show();
    }

    private void updateProgressDialog() {
        progressStep += 25; // Assume 4 steps, so each step increments by 25%
        if (progressStep > 100) {
            progressStep = 100; // Ensure the progress doesn't exceed 100%
        }
        progressBar.setProgress(progressStep);
        progressPercentage.setText(progressStep + "%");
    }

    private void taskCompleted() {
        syncTasksCompleted++;
        updateProgressDialog();
        if (syncTasksCompleted >= totalSyncTasks) {
            // Delay for 1 second to show 100% before dismissing
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissProgressBarDialog();
                }
            }, 2000);
        }
    }


    private void dismissProgressBarDialog() {
        if (progressBarDialog != null && progressBarDialog.isShowing()) {
            progressBarDialog.dismiss();
        }
    }

    private void getCustomerDetails() {
        showProgressBarDialog();
        String url = ApiLinks.allCustomerDetails;
        Log.d("TAG", "getCustomerDetails: " + url);
        Call<AllCustomerDetails> allCustomerDetailsCall = apiInterface.allCustomerDetails(url);
        allCustomerDetailsCall.enqueue(new Callback<AllCustomerDetails>() {

            @Override
            public void onResponse(Call<AllCustomerDetails> call, Response<AllCustomerDetails> response) {
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllCustomerDetails allCustomerDetails = response.body();
                    List<AllCustomerDetailsResponse> allCustomerDetailsResponses = allCustomerDetails.getActiveCustomerDetails();
                    allCustomerDetailsDB.customerdeleteAllData();
                    try (Cursor cursor = allCustomerDetailsDB.readAllData()) {
                        for (AllCustomerDetailsResponse allCustomerDetailsResponse : allCustomerDetailsResponses) {
                            boolean exists = false;
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
                                @SuppressLint("Range")
                                String customerIdFromDB = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_ID));
                                if (customerIdFromDB != null && customerIdFromDB.equalsIgnoreCase(allCustomerDetailsResponse.getId())) {
                                    exists = true;
                                    break;
                                }
                                cursor.moveToNext();
                            }

                            if (exists) {
                                allCustomerDetailsDB.UpdateData(allCustomerDetailsResponse.getAddress(),
                                        allCustomerDetailsResponse.getCustomerCode().toLowerCase(),
                                        allCustomerDetailsResponse.getContactPerson(),
                                        allCustomerDetailsResponse.getMobileno(),
                                        allCustomerDetailsResponse.getCreditPeriod(),
                                        allCustomerDetailsResponse.getCustomerName(),
                                        allCustomerDetailsResponse.getCustomerType(),
                                        allCustomerDetailsResponse.getCreditLimit(),
                                        allCustomerDetailsResponse.getRebate(),
                                        allCustomerDetailsResponse.getId(),
                                        allCustomerDetailsResponse.getTrn());
                                Log.d("TAG", "onResponse: Updated");
                            } else {
                                allCustomerDetailsDB.addDetails(allCustomerDetailsResponse.getAddress(),
                                        allCustomerDetailsResponse.getCustomerCode().toLowerCase(),
                                        allCustomerDetailsResponse.getContactPerson(),
                                        allCustomerDetailsResponse.getMobileno(),
                                        allCustomerDetailsResponse.getCreditPeriod(),
                                        allCustomerDetailsResponse.getCustomerName(),
                                        allCustomerDetailsResponse.getCustomerType(),
                                        allCustomerDetailsResponse.getCreditLimit(),
                                        allCustomerDetailsResponse.getRebate(),
                                        allCustomerDetailsResponse.getId(),
                                        allCustomerDetailsResponse.getTrn());

                                Log.d("TAG", "onResponse: Inserted");
                            }
                        }

                        cursor.close();
                        updateProgressDialog(); // Update progress here
                        getOutletByIDs();
                        //getOutletDetailsById(); // Start the next operation
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    taskCompleted(); // Update progress here if the task didn't complete successfully
                }
            }

            @Override
            public void onFailure(Call<AllCustomerDetails> call, Throwable t) {
                System.out.println("on failure called customer");
                dismissProgressBarDialog();
                displayAlert("Alert", t.getMessage());
            }
        });
    }

    private void getOutletDetailsById() {
        outletByIdDB.outletdeleteAllData();
        try (Cursor cursor = allCustomerDetailsDB.readAllData()) {
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    String url = ApiLinks.OutletDetailsById + "?van_id=" + vanID;
                    Log.d("TAG", "getOutletDetailsById: " + url);

                    Call<OutletsById> outletsByIdCall = apiInterface.outletsById(url);
                    outletsByIdCall.enqueue(new Callback<OutletsById>() {
                        @Override
                        public void onResponse(Call<OutletsById> call, Response<OutletsById> response) {
                            if (response.isSuccessful() && response.body().getStatus().equalsIgnoreCase("yes")) {
                                OutletsById outletsById = response.body();
                                List<OutletsByIdResponse> outletsByIdResponses = outletsById.getOutletDetailsBasOnVan();
                                try (Cursor innerCursor = outletByIdDB.readAllData()) {
                                    for (OutletsByIdResponse outletsByIdResponse : outletsByIdResponses) {
                                        boolean exists = false;
                                        innerCursor.moveToFirst();
                                        while (!innerCursor.isAfterLast()) {
                                            @SuppressLint("Range")
                                            String outletIdFromDB = innerCursor.getString(innerCursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ID));
                                            if (outletIdFromDB != null && outletIdFromDB.equalsIgnoreCase(outletsByIdResponse.getOutletId())) {
                                                exists = true;
                                                break;
                                            }
                                            innerCursor.moveToNext();
                                        }

                                        if (exists) {
                                            outletByIdDB.UpdateoutletData(outletsByIdResponse.getRouteId(),
                                                    outletsByIdResponse.getRouteName(),
                                                    outletsByIdResponse.getVehicleNo(),
                                                    outletsByIdResponse.getId(),
                                                    outletsByIdResponse.getEmailid(),
                                                    outletsByIdResponse.getAddress(),
                                                    outletsByIdResponse.getDistrict(),
                                                    outletsByIdResponse.getContactPerson(),
                                                    outletsByIdResponse.getMobileno(),
                                                    outletsByIdResponse.getOutletName().trim(),
                                                    outletsByIdResponse.getOutletId(),
                                                    outletsByIdResponse.getCustomerCode(),
                                                    outletsByIdResponse.getOutletCode());
                                            Log.d("TAG", "onResponse: Updated Outlet");
                                        } else {
                                           /* outletByIdDB.addoutletDetails(outletsByIdResponse.getRouteId(),
                                                    outletsByIdResponse.getRouteName(),
                                                    outletsByIdResponse.getVehicleNo(),
                                                    outletsByIdResponse.getId(),
                                                    outletsByIdResponse.getEmailid(),
                                                    outletsByIdResponse.getAddress(),
                                                    outletsByIdResponse.getContactPerson(),
                                                    outletsByIdResponse.getMobileno(),
                                                    outletsByIdResponse.getOutletName(),
                                                    outletsByIdResponse.getOutletId(),
                                                    outletsByIdResponse.getCustomerCode(),
                                                    outletsByIdResponse.getOutletCode());*/
                                            Log.d("TAG", "onResponse: Inserted Outlet");
                                        }
                                    }

                                    innerCursor.close();
                                    updateProgressDialog(); // Update progress here
                                    // getAllAgency(); // Start the next operation
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                taskCompleted(); // Update progress here if the task didn't complete successfully
                            }
                        }

                        @Override
                        public void onFailure(Call<OutletsById> call, Throwable t) {
                            Log.d("TAG", "onFailure: " + t.getMessage());
                            dismissProgressBarDialog();
                            displayAlert("Alert", t.getMessage());
                        }
                    });
                }
            } else {
                taskCompleted(); // Update progress here if the task didn't complete successfully
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getOutletByIDs() {
        String url = ApiLinks.OutletDetailsById + "?van_id=" + vanID;

        executorService.execute(() -> {
            OutletsById allItemSellingPriceDetailsResponse = fetchOutletDetails(url);
            // UI updates must be posted to the main thread
            handler.post(() -> processOutletDetails(allItemSellingPriceDetailsResponse));
        });
    }

    private OutletsById fetchOutletDetails(String url) {
        System.out.println("url" + url);
        Call<OutletsById> call = apiInterface.outletsById(url);
        try {
            Response<OutletsById> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("TAG", "Outlet details request successful");
                return response.body();
            } else {
                Log.e("TAG", "Outlet details request failed");
                return null;
            }
        } catch (IOException e) {
            Log.e("TAG", "Error fetching Outlet details", e);
            return null;
        }
    }

    private void processOutletDetails(OutletsById allItemSellingPriceDetailsResponse) {
        if (allItemSellingPriceDetailsResponse != null && "yes".equalsIgnoreCase(allItemSellingPriceDetailsResponse.getStatus())) {
            List<OutletsByIdResponse> allItemSellingPriceDetails = allItemSellingPriceDetailsResponse.getOutletDetailsBasOnVan();
            outletByIdDB.outletdeleteAllData();
            int count = 0;
            outletByIdDB.insertMultipleDetails(allItemSellingPriceDetails);
           /* for (AllItemSellingPriceDetailsResponse detail : allItemSellingPriceDetails) {
                count++;

            }*/
            updateProgressDialog();
            taskCompleted(); // Indicate that the task is completed
        } else {
            // Handle case where response is not successful or status is not "yes"
            Log.e("TAG", "Failed to fetch selling price details");
            displayAlert("Error", "Failed to fetch selling price details");
        }

        getAllAgency();
    }

    private void getAllAgency() {
        String url = ApiLinks.allAgencyDetails;
        Log.d("TAG", "getAllAgency: " + url);
        Call<AllAgencyDetails> allAgencyDetailsCall = apiInterface.allAgencyDetails(url);
        allAgencyDetailsCall.enqueue(new Callback<AllAgencyDetails>() {

            @Override
            public void onResponse(Call<AllAgencyDetails> call, Response<AllAgencyDetails> response) {
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllAgencyDetails allAgencyDetails = response.body();
                    List<AllAgencyDetailsResponse> allAgencyDetailsResponses = allAgencyDetails.getActiveAgencyDetails();
                    allAgencyDetailsDB.agencydeleteAllData();
                    try (Cursor cursor = allAgencyDetailsDB.readAllAgencyData()) {
                        for (AllAgencyDetailsResponse allAgencyDetailsResponse : allAgencyDetailsResponses) {
                            boolean exists = false;
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
                                @SuppressLint("Range")
                                String agencyIdFromDB = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));
                                if (agencyIdFromDB != null && agencyIdFromDB.equalsIgnoreCase(allAgencyDetailsResponse.getId())) {
                                    exists = true;
                                    break;
                                }
                                cursor.moveToNext();
                            }

                            if (exists) {
                                allAgencyDetailsDB.UpdateAgencyData(allAgencyDetailsResponse
                                                .getAgencyCode(), allAgencyDetailsResponse.getAgencyName(),
                                        allAgencyDetailsResponse.getId());
                                Log.d("TAG", "onResponse: Updated Agency");
                            } else {
                                allAgencyDetailsDB.addAgencyDetails(allAgencyDetailsResponse
                                                .getAgencyCode(), allAgencyDetailsResponse.getAgencyName(),
                                        allAgencyDetailsResponse.getId());
                                Log.d("TAG", "onResponse: Inserted Agency");
                            }
                        }
                        cursor.close(); // Ensure cursor is closed after operation
                        updateProgressDialog(); // Update UI or progress bar
                        taskCompleted(); // Mark task completion, handle next steps
                        getAllItemBySellingPrice(); // Fetch item details after updating agency info
                    } catch (Exception e) {
                        Log.e("TAG", "Database operation failed", e);
                        displayAlert("Database Error", "Failed to update agency data.");
                    }
                } else {
                    Log.d("TAG", "No agency data found or access denied");
                    displayAlert("No Data", "No agency data available or access denied.");
                    dismissProgressBarDialog();
                }
            }

            @Override
            public void onFailure(Call<AllAgencyDetails> call, Throwable t) {
                Log.e("TAG", "Network call failed: " + t.getMessage());
                dismissProgressBarDialog();
                displayAlert("Network Error", "Failed to fetch agency details: " + t.getMessage());
            }
        });
    }


    /*private void getAllItemById() {
        //  @SuppressLint("Range") String agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
        showItemsProgressDialogs();
        String url = ApiLinks.allItemDetailsById;
        Log.d("TAG", "getAllItemById: " + url);
        Call<AllItemDeatilsById> allItemDeatilsByIdCall = apiInterface.allItemDetailsById(url);
        allItemDeatilsByIdCall.enqueue(new Callback<AllItemDeatilsById>() {
            // private boolean onFailureCalled = false; // Flag to track if onFailure has been called

            @Override
            public void onResponse(Call<AllItemDeatilsById> call, Response<AllItemDeatilsById> response) {

                if (!hasFailure && response.isSuccessful()) {
                    AllItemDeatilsById allItemDeatilsById = response.body();
                    List<AllItemDetailResponseById> allItemDetailResponseByIds = allItemDeatilsById.getActiveItemDetailsWithSellingPrice();
                    itemsByAgencyDB.itemsdeleteAllData();
                    try (Cursor innerCursor = itemsByAgencyDB.readAllData()) {
                        for (AllItemDetailResponseById allItemDetailResponseById : allItemDetailResponseByIds) {
                            boolean exists = false;
                            innerCursor.moveToFirst();
                            while (!innerCursor.isAfterLast()) {
                                @SuppressLint("Range") String itemDB = innerCursor.getString(innerCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                if (itemDB != null && itemDB.equalsIgnoreCase(allItemDetailResponseById.getId())) {
                                    exists = true;
                                    break;
                                }
                                innerCursor.moveToNext();
                            }
                            if (exists) {
                                // Update existing row
                                itemsByAgencyDB.UpdateItemData(
                                        allItemDetailResponseById.getItemName(),
                                        allItemDetailResponseById.getItemCode(),
                                        allItemDetailResponseById.getId(),
                                        allItemDetailResponseById.getUom(),
                                        allItemDetailResponseById.getItemCategoryId(),
                                        allItemDetailResponseById.getAgencyCode(),
                                        allItemDetailResponseById.getAgencyId(),
                                        allItemDetailResponseById.getCustomerCode(),
                                        allItemDetailResponseById.getCustomerName(),
                                        allItemDetailResponseById.getSellingPrice(),
                                        allItemDetailResponseById.getProductDescription()
                                );
                                Log.d("TAG", "onResponse: Updated Item");
                            } else {
                                // Insert new row
                                itemsByAgencyDB.addItemDetails(
                                        allItemDetailResponseById.getItemName(),
                                        allItemDetailResponseById.getItemCode(),
                                        allItemDetailResponseById.getId(),
                                        allItemDetailResponseById.getUom(),
                                        allItemDetailResponseById.getItemCategoryId(),
                                        allItemDetailResponseById.getAgencyCode(),
                                        allItemDetailResponseById.getAgencyId(),
                                        allItemDetailResponseById.getCustomerCode(),
                                        allItemDetailResponseById.getCustomerName(),
                                        allItemDetailResponseById.getSellingPrice(),
                                        allItemDetailResponseById.getProductDescription()
                                );
                                Log.d("TAG", "onResponse: Inserted Item");
                            }

                        }

                        // getSellingPriceDetails();
                        dismissCustomerProgressDialog();
                        dismissOutletProgressDialog();
                        dismissAgencyProgressDialog();
                        dismissItemsProgressDialog();
                    } catch (Exception e) {
                        // hasFailure = true;
                        e.printStackTrace();
                    }
                } *//*else if (!onFailureCalled) { // Ensure onFailure is only called once
                            onFailureCalled = true;
                            // Handle unsuccessful response
                        //    hasFailure = true; // Set failure flag
                            dismissProgressDialogsss();
                            displayAlert("Alert", "Failed to fetch item details");
                        }
                        int count = completedRequests.incrementAndGet();
                        if (count == totalRequests && !hasFailure) {
                            dismissProgressDialogsss();
                        }
*//*
            }

            @Override
            public void onFailure(Call<AllItemDeatilsById> call, Throwable t) {
                // Synchronize access to completedRequests
                dismissItemsProgressDialog();
                Log.d("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", t.getMessage());


            }
        });


    }*/


    private void getAllItemBySellingPrice() {
        // showSellingProgressDialogs();
        String url = ApiLinks.allItemDetailsById;
        //  showAndDismissProgressDialog();
        executorService.execute(() -> {
            AllItemDeatilsById allItemSellingPriceresponse = fetchSellingPriceOfItems(url);
            // UI updates must be posted to the main thread
            handler.post(() -> processSellingPriceOfItems(allItemSellingPriceresponse));
        });
    }

    private AllItemDeatilsById fetchSellingPriceOfItems(String url) {
        Call<AllItemDeatilsById> call = apiInterface.allItemDetailsById(url);
        try {
            Response<AllItemDeatilsById> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("TAG", "Selling price details request successful");
                return response.body();
            } else {
                Log.e("TAG", "Selling price details request failed");
                return null;
            }
        } catch (IOException e) {
            Log.e("TAG", "Error fetching selling price details", e);
            return null;
        }
    }

    private void processSellingPriceOfItems(AllItemDeatilsById allItemSellingPriceDetailsResponse) {
        dismissSellingProgressDialog(); // Dismiss progress dialog after fetching data

        if (allItemSellingPriceDetailsResponse != null && "yes".equalsIgnoreCase(allItemSellingPriceDetailsResponse.getStatus())) {
            List<AllItemDetailResponseById> allItemSellingPriceDetails = allItemSellingPriceDetailsResponse.getActiveItemDetailsWithSellingPrice();

            // Perform database operations with progress indication
            itemsByAgencyDB.itemsdeleteAllData();
            itemsByAgencyDB.insertMultipleDetails(allItemSellingPriceDetails);

            // Update UI or notify completion
            updateProgressDialog();
            taskCompleted();
            // Indicate that the task is completed
        } else {
            // Handle case where response is not successful or status is not "yes"
            Log.e("TAG", "Failed to fetch selling price details");
            displayAlert("Error", "Failed to fetch selling price details");
        }
        dismissProgressBarDialog();
    }

    private synchronized void showAndDismissProgressDialog() {
        // Dismiss any existing progress dialog
        dismissProgressDialog();

        // Show the new progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait sync in progress...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Dismiss the dialog after 15 seconds
        new Handler().postDelayed(this::dismissProgressDialog, 15000);
    }

/*    public synchronized void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }*/


    @SuppressLint({"Range", "StaticFieldLeak"})
    private void syncOrders() {
        aLodingDialog.show();
        //showProgressDialog();
        // Perform database operations asynchronously
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = submitOrderDB.readDataByProductStatus("Not Synced");
                totalOrders = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoDatasDialog());
                    // showNoDatasDialog();
                    return null;
                }

                // Initialize a list to hold batched requests
                List<Call<OrderDetailsResponse>> batchRequests = new ArrayList<>();

                // Get the total number of records for progress calculation
                int totalRequests = cursor.getCount();
                int completedRequests = 0;

                // Iterate over database records
                // Iterate over database records
                while (cursor.moveToNext()) {
                    // Extract data from the cursor
                    String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    String vanId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VANID));
                    String userId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_USERID));
                    String outletId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                    String productIds = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    String agencycode = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_AGENCYID));
                    String ItemCodes = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                    String quantities = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));
                    String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                    String expectedDate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXPECTED_DELIVERY));
                    String leadTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_LEAD_TIME));

                    // Create request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("orderid", orderId);
                    params.put("user_id", userId);
                    params.put("van_id", vanId);
                    if (productIds.endsWith(",")) {
                        String itemid = productIds.substring(0, productIds.length() - 1);
                        params.put("item_ids", itemid);
                    } else {
                        String itemid = productIds.substring(0, productIds.length());
                        params.put("item_ids", itemid);
                    }
                    if (agencycode.endsWith(",")) {
                        String agencyid = agencycode.substring(0, agencycode.length() - 1);
                        params.put("agency_id", agencyid);
                    } else {
                        String agencyid = agencycode.substring(0, agencycode.length());
                        params.put("agency_id", agencyid);
                    }

                    if (ItemCodes.endsWith(",")) {
                        String itemcode = ItemCodes.substring(0, ItemCodes.length() - 1);
                        params.put("item_codes", itemcode);
                    } else {
                        String itemcode = ItemCodes.substring(0, ItemCodes.length());
                        params.put("item_codes", itemcode);
                    }

                    if (quantities.endsWith(",")) {
                        String qtys = quantities.substring(0, quantities.length() - 1);
                        params.put("quantities", qtys);
                    } else {
                        String qtys = quantities.substring(0, quantities.length());
                        params.put("quantities", qtys);
                    }

                    params.put("outlet_id", outletId);
                    params.put("ordered_datetime", dateTime);
                    params.put("orderStatus", "NEW ORDER");
                    params.put("createdBy", userId);
                    params.put("expectedDelivery", expectedDate);
                    params.put("lead_time", leadTime);
                    // Create the network request
                    System.out.println("params" + params);
                    String url = ApiLinks.submitOrder;
                    Call<OrderDetailsResponse> updateCall = apiInterface.submitOrder(url, params);

                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<OrderDetailsResponse>() {
                        @Override
                        public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                            System.out.println("response:" + response);
                            handleResponse(response);
                            if (response.isSuccessful()) {
                                // Extract data from the response if needed
                                // Update the corresponding database row
                                Set<ProductInfo> productIdQty = new LinkedHashSet<>();
                                productIdQty.add(new ProductInfo(productIds, agencycode, ItemCodes, quantities));
                                submitOrderDB.updateOrderAfterSync(orderId, vanId, userId, outletId, productIdQty, null, null, "synced", dateTime);
                                System.out.println("Order Synced");
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                            handleFailure(t);
                        }
                    });

                }

                cursor.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
             /*   int progress = values[0];
                progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        }.execute();
    }


    // Method to update progress dialog
    private void updateProgressDialog(int progress) {
        // Update progress bar with the percentage
        progressDialog.setProgress(progress);
    }

    @SuppressLint({"Range", "StaticFieldLeak"})
    private void TotalItemsApprovedSync(String selectedDate,String logindattime) {

        // showProgressDialog();
        aLodingDialog.show();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String url = ApiLinks.totalperItemapprovedDetailsBsdOnVanIdPowise + "?van_id=" + vanID + "&expectedDelivery=" + selectedDate+"&approved_datetime="+"1975-08-01%2012:00:00";
                Log.d("TAG", "TotalItemApprovedSync: " + url);

                Call<TotalItemsPerVanIdPoResponse> logincall = apiInterface.totalperItemapprovedDetailsBsdOnVanId(url);
                logincall.enqueue(new Callback<TotalItemsPerVanIdPoResponse>() {
                    @Override
                    public void onResponse(Call<TotalItemsPerVanIdPoResponse> call, Response<TotalItemsPerVanIdPoResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String status = response.body().getStatus();


                            if ("yes".equals(status)) {
                                totalApprovedOrderBsdOnItemDB.totaldeleteByStatusAfterSync();
                                System.out.println("yessssssss");
                                TotalItemsPerVanIdPoResponse totalapprovedBasedOnVanId = response.body();
                                List<ItemWiseOrdersBasedOnVanPowiseDetails> totalapprovedBasedOnVanIdDetails = totalapprovedBasedOnVanId.getItemWiseOrdersBasedOnVanPowise();


                                totalApprovedOrderBsdOnItemDB.totaldeleteByStatus();
                                try (Cursor innercursor = totalApprovedOrderBsdOnItemDB.totalreadAllData()) {




                                    for (ItemWiseOrdersBasedOnVanPowiseDetails totalPerItemsByVanIdResponse : totalapprovedBasedOnVanIdDetails) {
                                        System.out.println("inside forrrr");
                                        String agencyCode = totalPerItemsByVanIdResponse.getAgencyCode();
                                        String agencyName=allAgencyDetailsDB.getAgencyNameByAgencyCode(agencyCode);
                                        String prodctName = totalPerItemsByVanIdResponse.getItemName();
                                        String itemCategory = totalPerItemsByVanIdResponse.getCategoryName();
                                        String itemSubCategory = totalPerItemsByVanIdResponse.getSubCategoryName();
                                        String item_id = totalPerItemsByVanIdResponse.getItemId();
                                        String itemCode = totalPerItemsByVanIdResponse.getItemCode();
                                        String reQty = totalPerItemsByVanIdResponse.getTotalOrderedqty();
                                        String approved_quantity = totalPerItemsByVanIdResponse.getTotalApprovedqty();
                                        String poReference = totalPerItemsByVanIdResponse.getPoReference();






                                        boolean exists = false;
                                        if (innercursor.moveToFirst()) {
                                            do {
                                                String dbItemId = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                                String dbApprovedQty = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_APPROVEDQTY));
                                                String dbRequestedQty = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_REQUESTEDQTY));
                                                String dbProductName = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                                String dbPoReference = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PO_REFERENCE));
                                                String dbexpectedDelivery = innercursor.getString(innercursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_EXPECTED_DELIVERY));
                             /*  if(dbItemId.equals(item_id)  && dbProductName.equals(prodctName) && !dbPoReference.equals(poReference) && dbexpectedDelivery.equals(selectedDate)){
                                   exists=true;
                                   break;
                               }*/
                                                //   exists = totalApprovedOrderBsdOnItemDB.isItemMatchWithExpectedDelivery(item_id, poReference, selectedDate);






                                            }while (innercursor.moveToNext());
                                        }


                                        // Direct insertion of new data
                                        Date date = new Date();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                      /* if (exists) {
                           System.out.println("inside update  ");
                           totalApprovedOrderBsdOnItemDB.totalUpdateApprovedData( vanID, agencyCode, prodctName, item_id,
                                   itemCategory, itemSubCategory,itemCode, Integer.parseInt(reQty), Integer.parseInt(approved_quantity),
                                   "","NOT LOADED", poReference, selectedDate, dateFormat.format(date));
                       }else {
                           System.out.println("inside add  ");


                           totalApprovedOrderBsdOnItemDB.totaladdApprovedDetails(
                                   vanID, agencyCode, prodctName, item_id, itemCode,
                                   itemCategory, itemSubCategory, reQty, approved_quantity,
                                   "",poReference,selectedDate, "NOT LOADED", dateFormat.format(date));
                       }*/
                                        totalApprovedOrderBsdOnItemDB.handleDatabaseScenarios(  vanID, agencyCode,agencyName, prodctName, item_id, itemCode,
                                                itemCategory, itemSubCategory, reQty, approved_quantity
                                                ,poReference,selectedDate);
                                        System.out.println("van id: "+vanID+" item id: "+item_id+" product name: "+prodctName+" po reference: "+poReference+" date: "+selectedDate + " approved quantity: "+approved_quantity + " requested quantity: "+reQty);
                                    }
                                    // Continue with further syncing
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    ApprovedOrderSync(selectedDate,"1975-08-05%2012:00:00");


                                }





                                System.out.println("Done...................................");
                            } else {
                                runOnUiThread(() -> {
                                    // dismissProgressDialog();
                                    Handler handler = new Handler();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            aLodingDialog.cancel();
                                        }
                                    };
                                    handler.postDelayed(runnable,3000);
                                    Toast.makeText(MainActivity.this, "PO Not Approved....", Toast.LENGTH_SHORT).show();
                                    showFailureDialog2();
                                });
                            }
                        } else {
                            //  dismissProgressDialog();
                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    aLodingDialog.cancel();
                                }
                            };
                            handler.postDelayed(runnable,3000);
                            System.out.println("Response code: " + response.code());
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                                showFailureDialog2();
                            });
                        }

                    }

                    @Override
                    public void onFailure(Call<TotalItemsPerVanIdPoResponse> call, Throwable t) {
                        handleFailure2(t);
                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                // dismissProgressDialog();
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable,3000);
            }
        }.execute();
    }
    @SuppressLint({"Range", "StaticFieldLeak"})
    private void ApprovedOrderSync(String selectedDate, String lastapproved) {
        aLodingDialog.show();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String url = ApiLinks.approvedOrderBsdOnVanWithApprovedDateTime + "?van_id=" + vanID + "&expectedDelivery=" + selectedDate + "&approved_datetime=" + lastapproved;
                Log.d("TAG", "ApprovedOrderSync: " + url);
                Cursor cursor = submitOrderDB.readDataByProductStatus("synced");
                totalapprovedorder = cursor.getCount();
                Call<ApprovedOrdersBasedOnVanId> logincall = apiInterface.approvedOrderDetailsBsdOnVanId(url);
                logincall.enqueue(new Callback<ApprovedOrdersBasedOnVanId>() {
                    @Override
                    public void onResponse(Call<ApprovedOrdersBasedOnVanId> call, Response<ApprovedOrdersBasedOnVanId> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String status = response.body().getStatus();
                            System.out.println("Status: " + status);
                            if ("yes".equals(status)) {
                                handleResponse2(response);

                                List<ApprovedOrdersDetailsBsdOnVanIdResponse> approvedOrders = response.body().getApprovedOrderDetailsBsdOnVanid();
                                if (approvedOrders == null || approvedOrders.isEmpty()) {
                                    Log.w("ApprovedOrderSync", "No orders found in response.");
                                    return;
                                }

                                try {
                                    // Use a database transaction to ensure data consistency
                                    approvedOrderDB.beginTransaction();
                                    String date=getCurrentDateInDubaiZone();
                                    Cursor innerCursor = approvedOrderDB.readAllData();
                                    Set<String> existingOrders = new HashSet<>();

                                    if (innerCursor.moveToFirst()) {
                                        do {
                                            String dbOrderId = innerCursor.getString(innerCursor.getColumnIndex(ApprovedOrderDB.COLUMN_ORDERID));
                                            String dbItemId = innerCursor.getString(innerCursor.getColumnIndex(ApprovedOrderDB.COLUMN_PRODUCTID));
                                            existingOrders.add(dbOrderId + "_" + dbItemId);
                                        } while (innerCursor.moveToNext());
                                    }
                                    innerCursor.close();

                                    for (ApprovedOrdersDetailsBsdOnVanIdResponse order : approvedOrders) {
                                        String orderKey = order.getOrderid() + "_" + order.getItemId();

                                        if (!existingOrders.contains(orderKey)) {
                                            approvedOrderDB.addApprovedDetails(
                                                    order.getOrderid(), userID, vanID, order.getItemName(), order.getItemId(),
                                                    order.getCategoryName(), order.getSubCategoryName(), order.getOrderedQty(),
                                                    order.getApprovedQty(), order.getPoReference(), order.getOutletId(),
                                                    order.getOrderStatus(), order.getApprovedDatetime(), order.getOrderedDatetime(),
                                                    order.getPorefname(), order.getPocreatedDate() ,date,selectedDate
                                            );
                                        }
                                    }
                                    if (cursor.getCount() > 0) {
                                        while (cursor.moveToNext()) {
                                            String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                                            submitOrderDB.deleteOrderById(orderId);
                                        }
                                    }
                                    approvedOrderDB.setTransactionSuccessful();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    approvedOrderDB.endTransaction();
                                }

                                AddWebOrders(selectedDate);
                                System.out.println("Approved Orders Sync Completed.");
                            } else {
                                showFailureDialog();
                            }
                        } else {
                            showFailureDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApprovedOrdersBasedOnVanId> call, Throwable t) {
                        handleFailure2(t);
                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                aLodingDialog.cancel();
            }
        }.execute();
    }


    @SuppressLint("Range")
    private void AddWebOrders(String selectedDate) {
        aLodingDialog.show(); // Show loading dialog

        Cursor cursorA = approvedOrderDB.readDataByStatus("PENDING FOR DELIVERY");
        if (cursorA.getCount() == 0) {
            cursorA.close();
            aLodingDialog.cancel();
            return;
        }

        boolean isOrderAdded = false;
        StringBuilder missingItemsMessage = new StringBuilder();
        Set<String> processedOrderIds = new HashSet<>(); // Track orders already reported as missing

        try {
            approvedOrderDB.beginTransaction(); // Begin transaction for efficiency

            while (cursorA.moveToNext()) {
                String orderIDs = cursorA.getString(cursorA.getColumnIndex(ApprovedOrderDB.COLUMN_ORDERID));

                if (orderExistsSubmitDb(orderIDs)) {
                    Log.d("AddWebOrders", "Order already exists, skipping: " + orderIDs);
                    continue;
                }

                Set<String> missingItems = new HashSet<>(); // Using HashSet to prevent duplicate missing items
                List<ProductInfo> productList = new ArrayList<>();
                Set<String> addedProductIds = new HashSet<>(); // Track added product IDs to prevent duplicates

                String orderedDateTime = "", approvedDateTime = "", approved_insert_DT = "";

                try (Cursor cursor1 = approvedOrderDB.readonOrderid(orderIDs)) {
                    if (cursor1.moveToFirst()) {
                        orderedDateTime = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_ORDERED_DT_TIME));
                        approvedDateTime = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_APPROVED_DT_TIME));
                        approved_insert_DT = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_CURRENT_DT));

                        do {
                            String productId = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PRODUCTID));
                            String productName = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PRODUCTNAME));

// Check if product exists in itemsByAgencyDB
                            try (Cursor cursor2 = itemsByAgencyDB.readProdcutDataByproductId(productId)) {
                                if (!cursor2.moveToFirst()) {
// Product not found, add to missing items list (only once due to HashSet)
                                    missingItems.add(productName);
                                } else {
// Prevent duplicate additions in productList
                                    if (!addedProductIds.contains(productId)) {
                                        addedProductIds.add(productId);

                                        ProductInfo productInfo = new ProductInfo();
                                        productInfo.setProductID(productId);
                                        productInfo.setQuantity(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_REQUESTEDQTY)));
                                        productInfo.setApprovedQty(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_APPROVEDQTY)));
                                        productInfo.setPoREFRENCE(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO)));
                                        productInfo.setPoRefName(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO_REFNAME)));
                                        productInfo.setPoRefdate(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO_CREATED_DATE)));
                                        productInfo.setItemcategory(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_ITEM_CATEGORY)));
                                        productInfo.setItemsubcategory(cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_ITEM_SUB_CATEGORY)));
                                        productInfo.setAgencyCode(cursor2.getString(cursor2.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE)));
                                        productInfo.setItemCode(cursor2.getString(cursor2.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE)));

                                        productList.add(productInfo);
                                    }
                                }
                            }

                        } while (cursor1.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!missingItems.isEmpty()) {
// Check if this order ID was already processed for missing items
                    if (!processedOrderIds.contains(orderIDs)) {
                        processedOrderIds.add(orderIDs); // Mark this order ID as processed
                        missingItemsMessage.append("Order ID: ").append(orderIDs)
                                .append("\nMissing Items: ").append(TextUtils.join(", ", missingItems)).append("\n\n");
                    }
                    continue; // Skip this order
                }

// Proceed with adding the order only if there are valid products
                if (!productList.isEmpty()) {
                    isOrderAdded = true;

                    String userId = cursorA.getString(cursorA.getColumnIndex(ApprovedOrderDB.COLUMN_USERID));
                    String vanId = cursorA.getString(cursorA.getColumnIndex(ApprovedOrderDB.COLUMN_VAN_ID));
                    String outletId = cursorA.getString(cursorA.getColumnIndex(ApprovedOrderDB.COLUMN_OUTLETID));
                    String customerCode = null;

                    try (Cursor cursorB = outletByIdDB.readOutletByID(outletId)) {
                        if (cursorB.moveToFirst()) {
                            customerCode = cursorB.getString(cursorB.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
                        }
                    }

                    submitOrderDB.submitOrderFromWebSyncApprovedDb(orderIDs, outletId, userId, vanId, customerCode, productList,
                            "PENDING FOR DELIVERY", approvedDateTime, orderedDateTime, selectedDate,approved_insert_DT);
                }
            }

            approvedOrderDB.setTransactionSuccessful();
            Log.d("AddWebOrders", "Transaction successful, orders added.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            approvedOrderDB.endTransaction();
        }

        cursorA.close();
        aLodingDialog.cancel();

// Show missing items alert if any orders were skipped
        if (missingItemsMessage.length() > 0) {
            showMissingItemsDialog(missingItemsMessage.toString());
        }

// Show success message if at least one order was added
        if (isOrderAdded) {
            showSuccessDialog2();
        }
    }



    // Function to show AlertDialog for missing items
    private void showMissingItemsDialog(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Missing Items")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    public boolean orderExistsSubmitDb(String orderId) {
        boolean exists = false;
        Cursor cursor = submitOrderDB.readDataByOrderID(orderId);
        if (cursor != null && cursor.moveToFirst()) { // Check if the cursor is not empty
            exists = true;
        }
        if (cursor != null) {
            cursor.close(); // Always close the cursor to free up resources
        }
        return exists;
    }
    public static Date addMinutesToDate(int minutes, Date beforeTime) {

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs
                + (minutes * 60000));
        return afterAddingMins;
    }
    // Method to handle network response
   /* private void handleResponse(Response<OrderDetailsResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("Submit Order onResponse called. Status: " + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    showProgressDialogs();
                    System.out.println("Successfully posted");
                    dismissProgressDialogs();
                });
            } else {
                runOnUiThread(() -> {
                    dismissProgressDialogs();
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            dismissProgressDialogs();
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }
    }*/
    @SuppressLint({"Range", "StaticFieldLeak"})
    private void DeliveredOrderSync() {

        // showProgressDialog();
        aLodingDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("OutletPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        // Perform database operations asynchronously
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = submitOrderDB.readDataByProductStatus("DELIVERED");
                totalOrderToDeliver = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoDatasDialog());
                    // showNoDatasDialog();
                    return null;
                }

                // Initialize a list to hold batched requests
                List<Call<DeliveryOrderResponse>> batchRequests = new ArrayList<>();

                // Get the total number of records for progress calculation
                int totalRequests = cursor.getCount();
                int completedRequests = 0;

                // Iterate over database records
                // Iterate over database records
                while (cursor.moveToNext()) {
                    // Extract data from the cursor
                    String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    String ItemCodes = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                    String deliveredQty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_QTY));
                    String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
                    String orderedDateTime=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                    String rebate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DISC));
                    String net = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_NET));
                    String vat_percent = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_PERCENT));
                    String Vat_amt = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_AMT));
                    String gross = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_GROSS));
                    String invoiceTotal = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT));
                    String invoicTotalPayable=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE));
                    String cust_code = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
                    String totalnetamount = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_NET_AMOUNT));
                    String totalvatamount = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_VAT_AMOUNT));
                   String comments=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_COMMENTS));
                   String refno=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REFERENCE_NO));
                   String user_id=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_USERID));
                   // Create request parameters


                    String extra_item_id=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEMCODE));
                    String extra_item_qty=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_QTY));
                    String extra_item_po_ref=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_PO_REF));
                    String extra_item_po_ref_name=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_PO_REF_NAME));
                    String extra_item_po_created_date=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_PO_REF_CREATED_DATE));
                    String extra_item_selling_price=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_SELLING_PRICE));
                    String extra_item_rebate=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_DISC));
                    String extra_item_vat=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_VAT_PERCENT));
                    String extra_item_vatamount=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_VAT_AMT));
                    String extra_item_netamount=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_NET));
                    String extra_item_itemtotalprice=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_GROSS));


                    HashMap<String, String> params = new HashMap<>();
                    params.put("orderid", orderId);
                    params.put("item_codes", ItemCodes);
                    params.put("quantities", deliveredQty);
                    params.put("delivered_datetime", dateTime);
                    params.put("ordered_datetime",orderedDateTime);
                    params.put("invoiceno", invoiceNo);
                    params.put("invoicewithoutrebate",invoiceTotal);
                    params.put("grossinvoicetotal", invoicTotalPayable);
                    params.put("totalnetamount", totalnetamount);
                    params.put("totalvatamount", totalvatamount);
                    params.put("netamount", net);
                    params.put("vatamount", Vat_amt);
// Generate rebate, vat, and selling price values based on the length of item codes
                    String[] itemCodesArray = ItemCodes.split(",");
                 /*   StringBuilder rebateBuilder = new StringBuilder();
                    StringBuilder vatBuilder = new StringBuilder();
                    StringBuilder sellingPriceBuilder = new StringBuilder();
                    StringBuilder itemtotalBuilder=new StringBuilder();
                    // Split the deliveredQty string into an array
                    String[] deliveredQtyArray = deliveredQty.split(",");*/

                 /*   for (int i = 0; i < itemCodesArray.length; i++) {
                        rebateBuilder.append(i + 1);
                        vatBuilder.append(i + 1);
                        sellingPriceBuilder.append(i + 1);

                        // Convert the individual deliveredQty to an integer
                        int qty = Integer.parseInt(deliveredQtyArray[i]);

                        itemtotalBuilder.append((i + 1) * qty);

                        if (i < itemCodesArray.length - 1) {
                            rebateBuilder.append(",");
                            vatBuilder.append(",");
                            sellingPriceBuilder.append(",");
                            itemtotalBuilder.append(",");
                        }
                    }*/
                    if(extra_item_id!=null || !extra_item_id.isEmpty()){
                        params.put("extra_item_id",extra_item_id);
                    }

                    if(extra_item_qty!=null || !extra_item_qty.isEmpty()){
                        params.put("extra_item_qty",extra_item_qty);
                    }
                    if(extra_item_po_ref!=null || !extra_item_po_ref.isEmpty()){
                        params.put("extra_item_po_ref",extra_item_po_ref);
                    }
                    if(extra_item_po_ref_name!=null || !extra_item_po_ref_name.isEmpty()){
                        params.put("extra_item_po_ref_name",extra_item_po_ref_name);
                    }
                    if(extra_item_selling_price!=null || !extra_item_selling_price.isEmpty()){
                        params.put("extra_item_selling_price",extra_item_selling_price);
                    }

                    if(extra_item_rebate!=null || !extra_item_rebate.isEmpty()){
                        params.put("extra_item_rebate",extra_item_rebate);
                    }

                    if(extra_item_vat!=null || !extra_item_vat.isEmpty()){
                        params.put("extra_item_vat",extra_item_vat);
                    }
                    if(extra_item_vatamount!=null || !extra_item_vatamount.isEmpty()){
                        params.put("extra_item_vatamount",extra_item_vatamount);
                    }
                    if(extra_item_netamount!=null || !extra_item_netamount.isEmpty()){
                        params.put("extra_item_netamount",extra_item_netamount);
                    }
                    if(extra_item_itemtotalprice!=null || !extra_item_itemtotalprice.isEmpty()){
                        params.put("extra_item_itemtotalprice",extra_item_itemtotalprice);
                    }
                    if(extra_item_po_created_date!=null || !extra_item_po_created_date.isEmpty()){
                        params.put("extra_item_po_created_date",extra_item_po_created_date);
                    }
                    StringBuilder sellingPriceBuilder = new StringBuilder();
                    for (int i = 0; i < itemCodesArray.length; i++) {
                        String itemcode = itemCodesArray[i];
                        Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(cust_code, itemcode);
                        if (cursor1.getCount() != 0) {
                            while (cursor1.moveToNext()) {
                                String selling_price = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                System.out.println("sellingpriceeeeeeeeee: " + selling_price);
                                sellingPriceBuilder.append(selling_price);
                            }
                            if (i < itemCodesArray.length - 1) {
                                sellingPriceBuilder.append(",");
                            }
                        }

                    }
                    params.put("rebate", rebate);
                    params.put("vat", vat_percent);
                    params.put("sellingprice", sellingPriceBuilder.toString());
                    params.put("itemtotalprice", gross);
                    params.put("refno",refno);
                    params.put("comments",comments);
                    params.put("delivered_userid",user_id);
                    params.put("orderStatus", "DELIVERED");

                    System.out.println("params of delivered:" + params);

                    String url = ApiLinks.deliverysyncExtraItems;
                    System.out.println("url: " + url);
                    Call<DeliveryOrderResponse> updateCall = apiInterface.DeliverOrderSubmit(url, params);
                    System.out.println("update callllllllll:" + updateCall);
                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<DeliveryOrderResponse>() {
                        @Override
                        public void onResponse(Call<DeliveryOrderResponse> call, Response<DeliveryOrderResponse> response) {
                            System.out.println("response:" + response.body());
                            deliveryhandleResponse(response);
                            String status = response.body().getStatus();
                            System.out.println("status of del" + status);
                            if ("yes".equals(status)) {
                                // Extract data from the response if needed
                                // Update the corresponding database row
                                submitOrderDB.updateOrderStatusAfterDeliver(orderId, "DELIVERY DONE");
                                System.out.println("Order DELIVERY");
                            }
                        }

                        @Override
                        public void onFailure(Call<DeliveryOrderResponse> call, Throwable t) {
                            handleDeileryFailure(t);
                        }
                    });
                }

                cursor.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
             /*   int progress = values[0];
                progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        }.execute();
    }

    @SuppressLint({"Range", "StaticFieldLeak"})
    private void LOADSync() {
        // showProgressDialog();
        aLodingDialog.show();


        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = totalApprovedOrderBsdOnItemDB.GetAgencyDataNotLoadedBYStatus("LOADED");
                totalitemstoSync = cursor.getCount();


                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoLoadInDataDialog());
                    return null;
                }




                while (cursor.moveToNext()) {
                    String vanId = cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_VAN_ID));
                    String Itemid = cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                    String orderedQty=cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_REQUESTEDQTY));
                    String Appquantities = cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_TOTAL_APPROVEDQTY));
                    String qty_on_hand = cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));//
                    String loadDateTime = cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_DATE_TIME));
                    String expectedDelivery=cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_EXPECTED_DELIVERY));
                    String poList=cursor.getString(cursor.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PO_REFERENCE));


                    StringBuilder sb = new StringBuilder();
                    String[] poListArray = poList.split(",");


                    for (int i = 0; i < poListArray.length; i++) {
                        String po = poListArray[i];
                        sb.append(po);
                        if (i < poListArray.length - 1) {
                            sb.append("&");
                        }
                    }


                    System.out.println("Po list in params  :"+poList);
                    System.out.println("SB is: "+sb);




                    HashMap<String, String> params = new HashMap<>();
                    params.put("van_id", vanId);
                    params.put("po_reference", sb.toString());
                    params.put("item_id", Itemid);
                    params.put("ordered_qty", orderedQty);
                    params.put("approved_qty", Appquantities);
                    params.put("loaded_qty", qty_on_hand);
                    params.put("loaded_date", loadDateTime);
                    params.put("expectedDelivery", expectedDelivery);




                    System.out.println("params:......."+params);
                    String url = ApiLinks.loadsyncPowise;
                    System.out.println("loadin url is: "+url);
                    Call<LoadINSyncResponse> updateCall = apiInterface.LOADinSyncPowise(url, params);
                    updateCall.enqueue(new Callback<LoadINSyncResponse>() {
                        @Override
                        public void onResponse(Call<LoadINSyncResponse> call, Response<LoadINSyncResponse> response) {
                            LoadinResponse(response);
                            System.out.println("heyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy......."+response);
                            String status = response.body().getStatus();
                            System.out.println("status of load:" + status);
                            if ("yes".equals(status)) {
                              //  totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoading(Itemid,"LOADED SYNCED");
                              //  totalApprovedOrderBsdOnItemDB.totaldeleteByStatusAfterSync();
                            }
                        }


                        @Override
                        public void onFailure(Call<LoadINSyncResponse> call, Throwable t) {
                            handleLoadinfailure(t);
                        }
                    });




                }
                cursor.close();




                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        }.execute();
    }


    @SuppressLint({"Range", "StaticFieldLeak"})
    private void ReturnOrderSync() {
        showProgressDialog();
        // Perform database operations asynchronously
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = returnDB.Returnreadonstatus("RETURNED");
                totalreturnSync = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoDatasDialog());
                    // showNoDatasDialog();
                    return null;

                }
                while (cursor.moveToNext()) {
                    String orderId = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ORDERID));
                    String ItemCodes = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ITEMCODE));
                    String returnqty = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_QTY));
                    String dateTime = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_INVOICE_NO));
                    String credID = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CREDIT_NOTE));
                    String returnnet = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_NET));
                    String returnVat_amt = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VAT_AMT));
                    String returngross = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_GROSS));
                    String creditinvoiceTotal = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT));
                   String creditinvoiceTotalPayable=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE));
                    String status = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_STATUS));
                    String returntotalnetamount = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_NET_AMOUNT));
                    String returntotalvatamount = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_VAT_AMOUNT));
                    String reason = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_REASON));
                    String refno=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_REFERENCE_NO));
                    String comments=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_COMMENTS));
                    String sellingprice=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_PRICE));
                    HashMap<String, String> params = new HashMap<>();
                    params.put("orderid", orderId);
                    params.put("invoiceno", invoiceNo);
                    if (ItemCodes.endsWith(",")) {
                        String itemid = ItemCodes.substring(0, ItemCodes.length() - 1);
                        params.put("item_codes", itemid);
                    } else {
                        String itemid = ItemCodes.substring(0, ItemCodes.length());
                        params.put("item_codes", itemid);
                    }


                    String returngrosss;
                    if (returngross.endsWith(",")) {
                        returngrosss = returngross.substring(0, returngross.length() - 1);
                    } else {
                        returngrosss = returngross;
                    }
                    params.put("returnitemtotalprice", returngrosss);


                    if (returnqty.endsWith(",")) {
                        String returnqtys = returnqty.substring(0, returnqty.length() - 1);
                        params.put("quantities", returnqtys);
                    } else {
                        String returnqtys = returnqty.substring(0, returnqty.length());
                        params.put("quantities", returnqtys);
                    }

                    params.put("returned_datetime", dateTime);
                    params.put("credit_noteid", credID);
                    params.put("creditnotetotalamt", creditinvoiceTotalPayable);
                    params.put("creditwithoutrebate",creditinvoiceTotal);
                    params.put("refno",refno);
                    params.put("comments",comments);
                    params.put("orderStatus", status);

                    params.put("returntotalnetamount", returntotalnetamount);
                    params.put("returnitemtotalprice", returngross);


                    if (returnnet.endsWith(",")) {
                        String returntotalnetamounts = returnnet.substring(0, returnnet.length() - 1);
                        params.put("returnnetamount", returntotalnetamounts);
                    } else {
                        String returntotalnetamounts = returnnet.substring(0, returnnet.length());
                        params.put("returnnetamount", returntotalnetamounts);
                    }

                    if (returnVat_amt.endsWith(",")) {
                        String returnvatamounts = returnVat_amt.substring(0, returnVat_amt.length() - 1);
                        params.put("returnvatamount", returnvatamounts);
                    } else {
                        String returnvatamounts = returnVat_amt.substring(0, returnVat_amt.length());
                        params.put("returnvatamount", returnvatamounts);
                    }

                    params.put("returntotalvatamount", returntotalvatamount);


                    if (reason.endsWith(",")) {
                        String reasons = reason.substring(0, reason.length() - 1);
                        params.put("reason", reasons);
                    } else {
                        String reasons = reason.substring(0, reason.length());
                        params.put("reason", reasons);
                    }
                    if (sellingprice.endsWith(",")) {
                        String sellingprices = sellingprice.substring(0, sellingprice.length() - 1);
                        params.put("sellingprice", sellingprices);
                    } else {
                        String sellingprices = sellingprice.substring(0, sellingprice.length());
                        params.put("sellingprice", sellingprices);
                    }

                    System.out.println("params of delivered:" + params);

                    String url = ApiLinks.returnsync;
                    System.out.println("url: " + url);
                    Call<returnOrderResponse> updateCall = apiInterface.returnOrderSubmit(url, params);
                    System.out.println("update callllllllll:" + updateCall);
                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<returnOrderResponse>() {
                        @Override
                        public void onResponse(Call<returnOrderResponse> call, Response<returnOrderResponse> response) {
                            System.out.println("response:" + response.body());
                            returnResponse(response);
                            String status = response.body().getStatus();
                            System.out.println("status of del" + status);
                            if ("yes".equals(status)) {
                                // Extract data from the response if needed
                                // Update the corresponding database row
                                returnDB.updatereturnOrderStatusAfterDeliver(invoiceNo, "RETURN DONE");
                                System.out.println("Order returned");
                            }
                        }

                        @Override
                        public void onFailure(Call<returnOrderResponse> call, Throwable t) {
                            handleDeileryFailure(t);
                        }
                    });
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
             /*   int progress = values[0];
                progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                dismissProgressDialog();
            }
        }.execute();
    }


    @SuppressLint({"Range", "StaticFieldLeak"})
    private void RejectOrderSync() {
        aLodingDialog.show();
        // Perform database operations asynchronously
        new AsyncTask<Void, Integer, Void>() {


            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = submitOrderDB.readDataByOrderStatus("REJECTED");
                totalCancelSync = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoRejectedDatasDialog());
                    // showNoDatasDialog();
                    return null;

                }
                while (cursor.moveToNext()) {
                    String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    String Userid=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_USERID));
                    String comments=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_COMMENTS));
                    String totalItems=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_ITEMS));
                    String totlaQty=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_QTY_OF_OUTLET));
                    String totalNet=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_NET_AMOUNT));
                    String totalVat=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_VAT_AMOUNT));
                    String totalGross=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT));
                    String amountPayableAfterRebate=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE));
                    HashMap<String, String> params = new HashMap<>();

                    params.put("orderid", orderId);
                    params.put("userid",Userid);
                    params.put("comments",comments);
                    params.put("totalQty",totlaQty);
                    params.put("totalItems",totalItems);
                    params.put("totalNet",totalNet);
                    params.put("totalVat",totalVat);
                    params.put("totalGross",totalGross);
                    params.put("amountPayableAfterRebate",amountPayableAfterRebate);
                    String url = ApiLinks.cancelOrdersSync;
                    System.out.println("url: " + url);
                    System.out.println("params: " + params);
                    Call<CancelOrderResponse> updateCall = apiInterface.cancelOrderSubmit(url, params);
                    System.out.println("update callllllllll:" + updateCall);
                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<CancelOrderResponse>() {
                        @Override
                        public void onResponse(Call<CancelOrderResponse> call, Response<CancelOrderResponse> response) {
                            System.out.println("response:" + response.body());
                            cancelResponse(response);
                            String status = response.body().getStatus();
                            System.out.println("status of del" + status);
                            if ("yes".equals(status)) {
                                // Extract data from the response if needed
                                // Update the corresponding database row
                               submitOrderDB.updateOrderStatusAfterDeliver(orderId, "REJECTED SYNCED");
                                System.out.println("Order returned");
                            }
                        }

                        @Override
                        public void onFailure(Call<CancelOrderResponse> iscall, Throwable t) {
                            handleCancelFailure(t);
                        }
                    });
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
         /*   int progress = values[0];
            progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);
            }
        }.execute();
    }


    @SuppressLint({"Range", "StaticFieldLeak"})
    private void SyncExtraDeliveredOrders() {
        showProgressDialog();
        // Perform database operations asynchronously
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = submitOrderDB.readDataByProductStatus("NEW ORDER DELIVERED");
                totalExtraorderSync = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showExtraNoDatasDialog());
                    // showNoDatasDialog();
                    return null;
                }

                // Get the total number of records for progress calculation
                int totalRequests = cursor.getCount();
                int completedRequests = 0;

                // Iterate over database records
                // Iterate over database records
                while (cursor.moveToNext()) {
                    // Extract data from the cursor
                    String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    String agency_id = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_AGENCYID));
                    String vanId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VANID));
                    String userId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_USERID));
                    String itemId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    String ItemCodes = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
                    String deliveredQty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_QTY));
                    String outletId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                    String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                    String rebate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DISC));
                    String net = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_NET));
                    String vat_percent = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_PERCENT));
                    String Vat_amt = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_AMT));
                    String gross = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_GROSS));
                    String invoiceTotal = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT));
                    String invoiceTotalPayable=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE));
                    String cust_code = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
                    String totalnetamount = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_NET_AMOUNT));
                    String totalvatamount = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_VAT_AMOUNT));
                    String poRefrence = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PO_REF));
                    String porefname = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PO_REF_NAME));
                    String pocreateddate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PO_CREATED_DATE));
                    String refno=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REFERENCE_NO));
                    String comments=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_COMMENTS));
                    // Create request parameters

                    HashMap<String, String> params = new HashMap<>();
                    params.put("orderid", orderId);
                    params.put("agency_id", agency_id);
                    params.put("van_id", vanId);
                    params.put("user_id", userId);
                    params.put("item_ids", itemId);
                    params.put("outlet_id", outletId);
                    params.put("item_codes", ItemCodes);
                    params.put("quantities", deliveredQty);
                    params.put("delivered_datetime", dateTime);
                    params.put("invoiceno", invoiceNo);
                    params.put("grossinvoicetotal", invoiceTotalPayable);
                    params.put("invoicewithoutrebate",invoiceTotal);
                    params.put("totalnetamount", totalnetamount);
                    params.put("totalvatamount", totalvatamount);
                    params.put("netamount", net);
                    params.put("vatamount", Vat_amt);
                    params.put("refno",refno);
                    params.put("comments",comments);
// Generate rebate, vat, and selling price values based on the length of item codes
                    String[] itemCodesArray = ItemCodes.split(",");

                    StringBuilder sellingPriceBuilder = new StringBuilder();
                    for (int i = 0; i < itemCodesArray.length; i++) {
                        String itemcode = itemCodesArray[i];
                        Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(cust_code, itemcode);
                        if (cursor1.getCount() != 0) {
                            while (cursor1.moveToNext()) {
                                String selling_price = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                System.out.println("sellingpriceeeeeeeeee: " + selling_price);
                                sellingPriceBuilder.append(selling_price);
                            }
                            if (i < itemCodesArray.length - 1) {
                                sellingPriceBuilder.append(",");
                            }
                        }

                    }
                    params.put("rebate", rebate);
                    params.put("vat", vat_percent);
                    params.put("sellingprice", sellingPriceBuilder.toString());
                    params.put("itemtotalprice", gross);
                    params.put("po_reference", poRefrence);
                    params.put("poRefName", porefname);
                    params.put("pocreated_datetime", pocreateddate);
                    params.put("approvedBy", userId);
                    params.put("orderStatus", "DELIVERED");

                    System.out.println("params of delivered:" + params);

                    String url = ApiLinks.extraordersSync;
                    System.out.println("url: " + url);
                    Call<ExtraOrderSyncResponse> updateCall = apiInterface.ExtraOrderSubmit(url, params);
                    System.out.println("update callllllllll:" + updateCall);
                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<ExtraOrderSyncResponse>() {
                        @Override
                        public void onResponse(Call<ExtraOrderSyncResponse> call, Response<ExtraOrderSyncResponse> response) {
                            System.out.println("response:" + response.body());
                            extraOrderhandleResponse(response);
                            String status = response.body().getStatus();
                            System.out.println("status of del" + status);
                            if ("yes".equals(status)) {
                                // Extract data from the response if needed
                                // Update the corresponding database row
                                submitOrderDB.updateOrderStatusAfterDeliver(orderId, "DELIVERY DONE");
                                System.out.println("Order DELIVERY");
                            }
                        }

                        @Override
                        public void onFailure(Call<ExtraOrderSyncResponse> call, Throwable t) {
                            handleExtraOrderFailure(t);
                        }
                    });
                }

                cursor.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
             /*   int progress = values[0];
                progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                dismissProgressDialog();
            }
        }.execute();
    }



    @SuppressLint("StaticFieldLeak")
    private void ReturnOrderSyncWithoutInvoice() {
        aLodingDialog.show();

        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = returnDB.Returnreadonstatus("RETURNED NO INVOICE");
                totalreturnWithoutInvoiceSync = cursor.getCount();
                if (cursor.getCount() == 0) {
                    runOnUiThread(() -> showNoReturnWithOutDatasDialog());
                    aLodingDialog.dismiss();

                    // showNoDatasDialog();
                    return null;

                }

                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String orderId = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ORDERID));
                    @SuppressLint("Range") String ItemCodes = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ITEMCODE));
                    @SuppressLint("Range") String returnqty = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_QTY));
                    @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
                    @SuppressLint("Range") String invoiceNo = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_INVOICE_NO));
                    @SuppressLint("Range") String outletId = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_OUTLETID));
                  @SuppressLint("Range") String outletcode=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_OUTLETCODE));
                    @SuppressLint("Range") String credID = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CREDIT_NOTE));
                    @SuppressLint("Range") String returnnet = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_NET));
                    @SuppressLint("Range") String returnVat_amt = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VAT_AMT));
                    @SuppressLint("Range") String returngross = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_GROSS));
                    @SuppressLint("Range") String creditinvoiceTotal = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT));
                    String creditinvoiceTotalafterRebate=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_STATUS));
                    @SuppressLint("Range") String returntotalnetamount = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_NET_AMOUNT));
                    @SuppressLint("Range") String returntotalvatamount = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_VAT_AMOUNT));
                    @SuppressLint("Range") String reason = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_REASON));
                    @SuppressLint("Range") String reference = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_REFERENCE_NO));
                    @SuppressLint("Range") String comment = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_COMMENTS));
                    @SuppressLint("Range") String customerCode = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CUSTOMER_CODE));
                    @SuppressLint("Range") String VAT = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VAT_PERCENT));
                    @SuppressLint("Range") String rebate = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DISC));
                    @SuppressLint("Range") String vanId = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VANID));
                    @SuppressLint("Range") String userid=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_USERID));
                    //String outletCode = cursor.getString(cursor.getColumnIndex(ReturnDB.Ou));
                    String[] itemCodesArray = ItemCodes.split(",");

                    StringBuilder sellingPriceBuilder = new StringBuilder();

                    for (int i = 0; i < itemCodesArray.length; i++) {
                        String itemcode = itemCodesArray[i];
                        System.out.println("customerCode....: " + customerCode);
                        System.out.println("ItemCode........: " + itemcode);
                        Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(customerCode, itemcode);
                        if (cursor1.getCount() != 0) {
                            while (cursor1.moveToNext()) {
                                String selling_price = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                System.out.println("sellingpriceeeeeeeeee: " + selling_price);
                                sellingPriceBuilder.append(selling_price);
                            }
                            if (i < itemCodesArray.length - 1) {
                                sellingPriceBuilder.append(",");
                            }
                        }

                    }
                    Cursor cursor1 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);

                    String customerName = "";
                    if (cursor1.getCount() != 0) {
                        while (cursor1.moveToNext()) {
                            customerName = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                        }
                    }

                    /*Cursor cursor2 = outletByIdDB.readOutletByID(outletId);
                    System.out.println("Outlet id in......: " + outletId);
                    String outletCode = "";
                    if (cursor2.getCount() != 0) {
                        while (cursor2.moveToNext()) {
                            outletCode = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                        }
                        System.out.println("Outlet code in......: " + outletCode);
                    }*/

                    HashMap<String, String> params = new HashMap<>();
                    params.put("customerName", customerName);
                    params.put("outletCode",outletcode);
                    params.put("van_id",vanId);
                    params.put("customerCode", customerCode);
                    params.put("rebate", rebate);
                    params.put("sellingPrice", sellingPriceBuilder.toString());
                    params.put("vat", VAT);

                    if (ItemCodes.endsWith(",")) {
                        String itemid = ItemCodes.substring(0, ItemCodes.length() - 1);
                        params.put("item_codes", itemid);
                    } else {
                        String itemid = ItemCodes.substring(0, ItemCodes.length());
                        params.put("item_codes", itemid);
                    }


                    String returngrosss;
                    if (returngross.endsWith(",")) {
                        returngrosss = returngross.substring(0, returngross.length() - 1);
                    } else {
                        returngrosss = returngross;
                    }
                    params.put("returnitemtotalprice", returngrosss);


                    if (returnqty.endsWith(",")) {
                        String returnqtys = returnqty.substring(0, returnqty.length() - 1);
                        params.put("quantities", returnqtys);
                    } else {
                        String returnqtys = returnqty.substring(0, returnqty.length());
                        params.put("quantities", returnqtys);
                    }

                    params.put("returned_datetime", dateTime);
                    params.put("credit_noteid", credID);
                    params.put("creditnotetotalamt", creditinvoiceTotalafterRebate);
                    params.put("creditwithoutrebate", creditinvoiceTotal);
                    params.put("refno", reference);
                    params.put("comments", comment);
                    params.put("returntotalnetamount", returntotalnetamount);
                    params.put("returnitemtotalprice", returngross);


                    if (returnnet.endsWith(",")) {
                        String returntotalnetamounts = returnnet.substring(0, returnnet.length() - 1);
                        params.put("returnnetamount", returntotalnetamounts);
                    } else {
                        String returntotalnetamounts = returnnet.substring(0, returnnet.length());
                        params.put("returnnetamount", returntotalnetamounts);
                    }

                    if (returnVat_amt.endsWith(",")) {
                        String returnvatamounts = returnVat_amt.substring(0, returnVat_amt.length() - 1);
                        params.put("returnvatamount", returnvatamounts);
                    } else {
                        String returnvatamounts = returnVat_amt.substring(0, returnVat_amt.length());
                        params.put("returnvatamount", returnvatamounts);
                    }

                    params.put("returntotalvatamount", returntotalvatamount);


                    if (reason.endsWith(",")) {
                        String reasons = reason.substring(0, reason.length() - 1);
                        params.put("reason", reasons);
                    } else {
                        String reasons = reason.substring(0, reason.length());
                        params.put("reason", reasons);
                    }
                        params.put("user_id",userid);

                    System.out.println("params of delivered:" + params);

                    String url = ApiLinks.returnWithoutInvoiceSync;
                    System.out.println("url: " + url);
                    Call<ReturnOrderWithoutInvoiceResponse> updateCall = apiInterface.returnOrderWithoutInvoiceSubmit(url, params);
                    System.out.println("update callllllllll:" + updateCall);
                    // Associate each request with the corresponding database row
                    updateCall.enqueue(new Callback<ReturnOrderWithoutInvoiceResponse>() {
                        @Override
                        public void onResponse(Call<ReturnOrderWithoutInvoiceResponse> call, Response<ReturnOrderWithoutInvoiceResponse> response) {
                            try {
                                if (response.body() != null) {
                                    System.out.println("response:" + response.body());
                                    returnWithoutInvoiceResponse(response);

                                    String status = response.body().getStatus();
                                    System.out.println("status of del: " + status);

                                    if ("yes".equals(status)) {
                                        // Extract data from the response if needed
                                        // Update the corresponding database row
                                        returnDB.updatereturnOrderWithoutInvoiceStatusAfterDeliver(outletId, "RETURN DONE");
                                        aLodingDialog.dismiss();
                                        System.out.println("Order returned");
                                    }
                                } else {
                                    System.err.println("Response body is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Error processing response: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<ReturnOrderWithoutInvoiceResponse> call, Throwable t) {
                            try {
                                handleReturnFailure(t);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Error handling failure: " + e.getMessage());
                            } finally {
                                aLodingDialog.dismiss();
                            }
                        }
                    });
                }
                cursor.close();
                return null;
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                // Update progress dialog with the percentage
             /*   int progress = values[0];
                progressDialog.setProgress(progress);*/
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Dismiss progress dialog when syncing is complete
                // progressDialog.dismiss(); // Remove this line to prevent auto-dismissal
                dismissProgressDialog();
            }
        }.execute();
    }

    private void getAllDeliveredAndReturnTransaction(String van_id) {
        // showSellingProgressDialogs();
        String url = ApiLinks.deliveredAndReturnTransactionSync+"?van_id="+van_id;
        System.out.println("urlllllll:"+url);

        //  showAndDismissProgressDialog();
        executorService.execute(() -> {
            DeliveredAndReturnTransactionBean deliveredAndReturnTransactionBean = fetchTransactions(url);
            // UI updates must be posted to the main thread
            handler.post(() -> processTransactionDeliveredAndReturn(deliveredAndReturnTransactionBean));
        });
        getAllVANStockTransaction(van_id);
    }

    private DeliveredAndReturnTransactionBean fetchTransactions(String url) {
        Call<DeliveredAndReturnTransactionBean> call = apiInterface.allDeliveredAndReturnTransaction(url);
        try {
            Response<DeliveredAndReturnTransactionBean> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("TAG", "Transaction details request successful");
                return response.body();
            } else {
                Log.e("TAG", "Transaction details request failed");
                return null;
            }
        } catch (IOException e) {
            Log.e("TAG", "Error fetching Transaction details", e);
            return null;
        }
    }

    private void processTransactionDeliveredAndReturn(DeliveredAndReturnTransactionBean deliveredAndReturnTransactionBean) {
        dismissSellingProgressDialog(); // Dismiss progress dialog after fetching data

        if (deliveredAndReturnTransactionBean != null && deliveredAndReturnTransactionBean.getStatus().equals("yes")) {
            List<DeliveredOrderItemLevelDetails> deliveredOrderItemLevelDetails = deliveredAndReturnTransactionBean.getDeliveredOrderItemLevelDetails();
            List<DeliveredOrderLevelDetails> deliveredOrderLevelDetails = deliveredAndReturnTransactionBean.getDeliveredOrderLevelDetails();
            List<ReturnOrderItemLevelDetails> returnOrderItemLevelDetails=deliveredAndReturnTransactionBean.getReturnOrderItemLevelDetails();
            List<ReturnOrderLevelDetails> returnOrderLevelDetails=deliveredAndReturnTransactionBean.getReturnOrderLevelDetails();
              List<ReturnWithoutInvoiceDetails> returnWithoutInvoiceDetails=deliveredAndReturnTransactionBean.getReturnWithoutInvoiceDetails();
            // Check if lists are not null and contain data
            if (deliveredOrderItemLevelDetails != null && !deliveredOrderItemLevelDetails.isEmpty()) {
                submitOrderDB.insertMultipleDetails(deliveredOrderItemLevelDetails);
            } else {
                Log.e("TAG", "DeliveredOrderItemLevelDetails is empty or null");
            }

            if (deliveredOrderLevelDetails != null && !deliveredOrderLevelDetails.isEmpty()) {
                submitOrderDB.insertMultipleDetails2(deliveredOrderLevelDetails);
            } else {
                Log.e("TAG", "DeliveredOrderLevelDetails is empty or null");
            }
            if (returnOrderItemLevelDetails != null && !returnOrderItemLevelDetails.isEmpty()) {
                returnDB.insertMultipleReturnedItems(returnOrderItemLevelDetails);
            } else {
                Log.e("TAG", "returnOrderItemLevelDetails is empty or null");
            }
            if (returnOrderLevelDetails != null && !returnOrderLevelDetails.isEmpty()) {
                returnDB.insertMultipleReturnDetails(returnOrderLevelDetails);
            } else {
                Log.e("TAG", "returnOrderLevelDetails is empty or null");
            }



            if (returnWithoutInvoiceDetails != null && !returnWithoutInvoiceDetails.isEmpty()) {
                returnDB.insertReturnWithoutInvoice(returnWithoutInvoiceDetails);
            } else {
                Log.e("TAG", "returnWithoutInvoiceDetails is empty or null");
            }
            System.out.println("Success in transaction");

            // Confirm successful transaction

        } else {
            // Handle case where response is null
            Log.e("TAG", "Failed to fetch Transaction details");
            displayAlert("Error", "Failed to fetch Transaction details");
        }
        dismissProgressBarDialog();

    }

    private void getAllVANStockTransaction(String van_id) {
        // showSellingProgressDialogs();
        String url = ApiLinks.getPreviousVanStockByVan+"?van_id="+van_id;
        System.out.println("urlllllll:"+url);

        //  showAndDismissProgressDialog();
        executorService.execute(() -> {
            vanStockTransactionResponse vanStockTransactionResponse = fetchVanStock(url);
            // UI updates must be posted to the main thread
            handler.post(() -> processTransactionVanStock(vanStockTransactionResponse));
        });
        getAllLoadInfo(van_id);
    }
    private vanStockTransactionResponse fetchVanStock(String url) {
        Call<vanStockTransactionResponse> call = apiInterface.allVanStockTransaction(url);
        try {
            Response<vanStockTransactionResponse> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("TAG", "Transaction details request successful");
                return response.body();
            } else {
                Log.e("TAG", "Transaction details request failed");
                return null;
            }
        } catch (IOException e) {
            Log.e("TAG", "Error fetching Transaction details", e);
            return null;
        }
    }
    private void processTransactionVanStock(vanStockTransactionResponse vanStockTransactionResponse) {
        dismissSellingProgressDialog(); // Dismiss progress dialog after fetching data

        if (vanStockTransactionResponse != null && vanStockTransactionResponse.getStatus().equals("yes")) {
            List<VanStockDetails> vanStockDetails = vanStockTransactionResponse.getVanStockDetails();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (vanStockDetails != null && !vanStockDetails.isEmpty()) {
                stockDB.insertMultipleDetails(vanStockDetails);
               // userDetailsDb.updateLastApprovedDate("1", dateFormat.format(date));
            } else {
                Log.e("TAG", "DeliveredOrderItemLevelDetails is empty or null");
            }

            System.out.println("Success in transaction");

            // Confirm successful transaction

        } else {
            // Handle case where response is null
            Log.e("TAG", "Failed to fetch Transaction details");
            displayAlert("Error", "Failed to fetch VAN Transaction details");
        }
        dismissProgressBarDialog();

    }


    private void getAllLoadInfo(String van_id) {
        // showSellingProgressDialogs();
        String url = ApiLinks.getPreviousLoadsByVan+"?van_id="+van_id;
        System.out.println("urlllllll:"+url);

        //  showAndDismissProgressDialog();
        executorService.execute(() -> {
            VanLoadDetailsBasedOnVanResponse VanLoadDetailsBasedOnVanResponse = fetchLoadInfo(url);
            // UI updates must be posted to the main thread
            handler.post(() -> processTransactionLoadInfo(VanLoadDetailsBasedOnVanResponse));
        });

    }
    private VanLoadDetailsBasedOnVanResponse fetchLoadInfo(String url) {
        Call<VanLoadDetailsBasedOnVanResponse> call = apiInterface.allLoadInfoTransaction(url);
        try {
            Response<VanLoadDetailsBasedOnVanResponse> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("TAG", "Transaction details request successful");
                return response.body();
            } else {
                Log.e("TAG", "Transaction details request failed");
                return null;
            }
        } catch (IOException e) {
            Log.e("TAG", "Error fetching Transaction details", e);
            return null;
        }
    }
    private void processTransactionLoadInfo(VanLoadDetailsBasedOnVanResponse VanLoadDetailsBasedOnVanResponse) {

        if (VanLoadDetailsBasedOnVanResponse != null && VanLoadDetailsBasedOnVanResponse.getStatus().equals("yes")) {
            List<VanLoadDataForVanDetails> vanLoadDetails = VanLoadDetailsBasedOnVanResponse.getVanLoadDataForVan();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (vanLoadDetails != null && !vanLoadDetails.isEmpty()) {
                totalApprovedOrderBsdOnItemDB.insertMultipleDetails(vanLoadDetails);
                // userDetailsDb.updateLastApprovedDate("1", dateFormat.format(date));

            } else {
                Log.e("TAG", "DeliveredOrderItemLevelDetails is empty or null");
            }

            System.out.println("Success in LOAD transaction");

            // Confirm successful transaction

        } else {
            // Handle case where response is null
            Log.e("TAG", "Failed to fetch Transaction details");
            displayAlert("Error", "Failed to fetch LOAD Transaction details");
        }
        dismissProgressBarDialog();

    }



    @SuppressLint("StaticFieldLeak")
    private void syncVanStock() {
        showProgressDialog();
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = stockDB.stockreadAllData();
                totalVanStockSync = cursor.getCount();
                if (totalVanStockSync == 0) {
                    runOnUiThread(() -> showNoVanStockDatasDialog());
                    return null;
                }

                StringBuilder itemCodesBuilder = new StringBuilder();
                StringBuilder qtyOnVanBuilder = new StringBuilder();

                // Accumulate data from cursor
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String itemCode = cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_ITEM_CODE));

                    @SuppressLint("Range") String qtyOnHand = cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                    itemCodesBuilder.append(itemCode).append(",");
                    qtyOnVanBuilder.append(qtyOnHand).append(",");
                }
                cursor.close();
                // Remove trailing commas after loop completion
                String itemCodes = removeTrailingComma(itemCodesBuilder);
                String qtyOnVan = removeTrailingComma(qtyOnVanBuilder);
                String deliveredDate = getCurrentDate();

                // Prepare parameters for the single network request
                HashMap<String, String> params = new HashMap<>();
                params.put("van_id", vanID);
                params.put("delivered_date", deliveredDate);
                params.put("itemCodes", itemCodes);
                params.put("quantities", qtyOnVan);

                // Send a single network request
                String url = ApiLinks.syncVanStock;
                System.out.println("vanstock params: "+params);
                Call<VanStockSyncResponse> updateCall = apiInterface.vanStockSync(url, params);
                updateCall.enqueue(new Callback<VanStockSyncResponse>() {
                    @Override
                    public void onResponse(Call<VanStockSyncResponse> call, Response<VanStockSyncResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            vanStockSyncResponse(response);
                            if ("yes".equalsIgnoreCase(response.body().getStatus())) {
                                System.out.println("Van Stock Synced Successfully");

                            }
                        } else {
                            runOnUiThread(MainActivity.this::showVanStockFailureDialog);
                        }
                    }

                    @Override
                    public void onFailure(Call<VanStockSyncResponse> call, Throwable t) {
                        runOnUiThread(MainActivity.this::showVanStockFailureDialog);
                    }

                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissProgressDialog();
            }
        }.execute();
    }
    private int successfulSyncCount = 0;

    private void handleResponse(Response<OrderDetailsResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("Message:" + response.body().getMessage());
            System.out.println("status" + status);
            System.out.println("response code: " + response.code());
            if ("yes".equalsIgnoreCase(status)) {
                runOnUiThread(() -> {
                    aLodingDialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    successfulSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulSyncCount == totalOrders) {
                        showSuccessDialog();
                        successfulSyncCount = 0; // Reset the counter
                        totalOrders = 0;
                    }
                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showFailureDialog();
                });
            }
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showFailureDialog();
            });
        }
    }

    private int successfulDeliverSyncCount = 0;

    private void deliveryhandleResponse(Response<DeliveryOrderResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    //  showProgressDialogs();
                    aLodingDialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    successfulDeliverSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulDeliverSyncCount == totalOrderToDeliver) {
                        showSuccessdeliveredDialog();
                        successfulDeliverSyncCount = 0; // Reset the counter
                        totalOrderToDeliver = 0;

                    }
                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showDeliveryFailureDialog();
                });
            }
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showDeliveryFailureDialog();
            });
        }
    }

    private int successfulLOADSyncCount = 0;

    private void LoadinResponse(Response<LoadINSyncResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            aLodingDialog.show();
            if ("yes".equals(status)) {
                successfulLOADSyncCount++;
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    if (successfulLOADSyncCount == totalitemstoSync) {
                        showSuccessloadSyncDialog();
                        successfulLOADSyncCount = 0;
                        totalitemstoSync = 0;
                    }
                   // showSuccessloadSyncDialog();
                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showLoadinFailureDialog();
                });
            }
        } else {
            runOnUiThread(() -> {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 2000);
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showLoadinFailureDialog();
            });
        }
    }

    private int successfulSyncCount2 = 0;

    private void handleResponse2(Response<ApprovedOrdersBasedOnVanId> response) {
        //showProgressDialogs();
        aLodingDialog.show();

        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            if ("yes".equals(status)) {
                successfulSyncCount2++;
                System.out.println("count of order:" + successfulSyncCount2);
                if (successfulSyncCount2 == totalapprovedorder) {
                    showSuccessDialog2();
                    successfulSyncCount2 = 0; // Reset the counter
                    totalapprovedorder = 0;
                } else {
                    Toast.makeText(MainActivity.this, "orders partially approved!", Toast.LENGTH_SHORT).show();
                }
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to sync...", Toast.LENGTH_SHORT).show();
                });
            }
        } else {
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
            });
        }

        //  dismissProgressDialogs();

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                aLodingDialog.cancel();
            }
        };
        handler.postDelayed(runnable, 3000);
    }


    private int successfulReturnSyncCount = 0;

    private void returnResponse(Response<returnOrderResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    showProgressDialogs();
                    dismissProgressDialogs();
                    successfulReturnSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulReturnSyncCount == totalreturnSync) {
                        showSuccessreturnSyncDialog();
                        successfulReturnSyncCount = 0; // Reset the counter
                        totalreturnSync = 0;
                    }
                });
            } else {
                runOnUiThread(() -> {
                    dismissProgressDialogs();
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showreturnFailureDialog();
                });
            }
        } else {
            dismissProgressDialogs();
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showreturnFailureDialog();
            });
        }
    }
    private int successfulReturnWithoutInvoiceSyncCount = 0;
    private void returnWithoutInvoiceResponse(Response<ReturnOrderWithoutInvoiceResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    showProgressDialogs();
                    dismissProgressDialogs();
                    successfulReturnWithoutInvoiceSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulReturnWithoutInvoiceSyncCount == totalreturnWithoutInvoiceSync) {
                        showSuccessreturnSyncDialog();
                        successfulReturnWithoutInvoiceSyncCount = 0; // Reset the counter
                        totalreturnWithoutInvoiceSync = 0;
                    }
                });
            } else {
                runOnUiThread(() -> {
                    dismissProgressDialogs();
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showreturnFailureDialog();
                });
            }
        } else {
            dismissProgressDialogs();
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showreturnFailureDialog();
            });
        }
    }
    private int successfulCancelSyncCount = 0;

    private void cancelResponse(Response<CancelOrderResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    aLodingDialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 3000);
                    successfulCancelSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulCancelSyncCount == totalCancelSync) {
                        showSuccessCancelSyncDialog();
                        successfulCancelSyncCount = 0; // Reset the counter
                        totalCancelSync = 0;
                    }
                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 3000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showCancelFailureDialog();
                });
            }
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showCancelFailureDialog();
            });
        }
    }

    private int successfulEXtraOrderSyncCount = 0;

    private void extraOrderhandleResponse(Response<ExtraOrderSyncResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equals(status)) {
                runOnUiThread(() -> {
                    aLodingDialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    successfulEXtraOrderSyncCount++;

                    // Check if all orders are successfully synced
                    if (successfulEXtraOrderSyncCount == totalExtraorderSync) {
                        showSuccessExtraOrderdeliveredDialog();
                        successfulEXtraOrderSyncCount = 0; // Reset the counter
                        totalExtraorderSync = 0;
                    }
                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showExtraFailureDialog();
                });
            }
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showExtraFailureDialog();
            });
        }
    }

    private int successfulVanStockSyncCount = 0;

    private void vanStockSyncResponse(Response<VanStockSyncResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            String status = response.body().getStatus();
            System.out.println("status of del" + status);
            if ("yes".equalsIgnoreCase(status)) {
                runOnUiThread(() -> {
                    aLodingDialog.show();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);


                    // Check if all orders are successfully synced

                        showSuccessVanStockDialog();


                });
            } else {
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                    Toast.makeText(MainActivity.this, "Failed to sync....", Toast.LENGTH_SHORT).show();
                    showVanStockFailureDialog();
                });
            }
        } else {
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
            System.out.println("Response code: " + response.code());
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "Failure: " + response.code(), Toast.LENGTH_SHORT).show();
                showExtraFailureDialog();
            });
        }
    }
    private void showNoVanStockDatasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Van Stock to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    private String removeTrailingComma(StringBuilder builder) {
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }

    private void showVanStockFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Van Stock")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Orders synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSuccessdeliveredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Delivered Orders synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSuccessloadSyncDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Load In synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSuccessreturnSyncDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Returned synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSuccessDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Approved Orders synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
private void showSuccessVanStockDialog(){
    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    builder.setTitle("Sync Successful")
            .setMessage("Van Stock synced successfully.")
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
}
    private void showNoDatasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Delivered Orders to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showNoReturnWithOutDatasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Return Without invoice Orders to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showNoRejectedDatasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Rejected Orders to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showExtraNoDatasDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Extra Delivered Orders to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showNoLoadInDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No LoadIn Data to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void dismissCurrentDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
    }

    private void showNoDatasDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync")
                .setMessage("No Approved Orders to sync!.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> currentDialog = null)
                .show();
    }

    private void showFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDeliveryFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Delivery Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLoadinFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to Load In items")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showreturnFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync return Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showFailureDialog2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Approved Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showNoOrdersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("No Approved Orders to Sync.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showSuccessExtraOrderdeliveredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Delivered Extra Orders synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to handle network failure
    private void handleFailure(Throwable t) {
        t.printStackTrace();
        showFailureDialog();

    }

    private void handleDeileryFailure(Throwable t) {
        t.printStackTrace();
        showDeliveryFailureDialog();

    }

    private void handleReturnFailure(Throwable t) {
        t.printStackTrace();
        showreturnFailureDialog();

    }

    private void handleLoadinfailure(Throwable t) {
        t.printStackTrace();
        showLoadinFailureDialog();

    }

    private void handleFailure2(Throwable t) {
        t.printStackTrace();
        showFailureDialog2();

    }

    private void handleCancelFailure(Throwable t) {
        t.printStackTrace();
        showreturnFailureDialog();

    }

    private void handleExtraOrderFailure(Throwable t) {
        t.printStackTrace();
        showExtraFailureDialog();

    }


    private void showProgressDialogs() {
        runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Syncing...");
                progressDialog.setCancelable(false);
            }
            progressDialog.show();
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void dismissProgressDialogs() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    public void dismissProgressDialogsss() {
        if (progressDialogs != null && progressDialogs.isShowing())
            progressDialogs.dismiss();
    }

    private void showSuccessCancelSyncDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Successful")
                .setMessage("Rejected orders synced successfully.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showCancelFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Rejected Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showExtraFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sync Failed")
                .setMessage("Failed to sync Extra Orders.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @SuppressLint("MissingPermission")
    private void enableBluetoothIfNecessary() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
        }
    }
    private boolean checkAndRequestPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                android.Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                android.Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        // Add Bluetooth permissions for API level 31 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31
            permissions = Arrays.copyOf(permissions, permissions.length + 2);
            permissions[permissions.length - 2] = android.Manifest.permission.BLUETOOTH_SCAN;
            permissions[permissions.length - 1] = android.Manifest.permission.BLUETOOTH_CONNECT;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermissionsNeeded = new ArrayList<>();

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission);
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
                return false;
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Map<String, Integer> perms = new HashMap<>();
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            boolean allPermissionsGranted = true;
            for (String permission : permissions) {
                if (perms.get(permission) != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // All permissions are granted, proceed with the functionality
            } else {
                // Notify user that some permissions were not granted
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                CustomerLogger.initialize(getApplicationContext());
            } else {
                // Permission denied
                Log.e("CustomLogger", "Permission denied to write to external storage");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Bluetooth is enabled, proceed with the functionality
            } else {
                // Notify user that Bluetooth is not enabled
            }
        }
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (Settings.System.canWrite(this)) {
                // Permission granted
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                proceedWithTask();
            } else {
                // Permission denied, show an alert or inform the user
                showPermissionDeniedAlert();
            }
        }
    }


    private void logout() {
        // Implement your logout logic here
        // For example, clear session, navigate to login screen, etc.
        mPrefs.edit().putBoolean(Constants.LOGGED_IN, false).commit();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
        userID = null;
        vanID = null;


    }
}


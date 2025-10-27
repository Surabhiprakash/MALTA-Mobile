package com.malta_mqf.malta_mobile.SewooPrinter;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.NewOrderinvoiceNumber;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALGROSS;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALGROSSAFTERREBATE;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALNET;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALQTY;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALVAT;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.customerCode;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.newOrderId;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.newOrderoutletid;
import static com.malta_mqf.malta_mobile.SewooPrinter.NewOrdrSamplePrint.newSaleBeanLists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.NewOrderInvoiceBean;
import com.malta_mqf.malta_mobile.NewSaleActivity;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.StartDeliveryActivity;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewOrderBluetoothActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 2;
    public static final String bluetoothAddressKey = "SEWOO_DEMO_BLUETOOTH_ADDRESS";
    public static final String PREFS_NAME = "BluetoothPrefs";
    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp";
    private static final String fileName = dir + "/BTPrinter";
    private static final int BT_PRINTER = 1536;
    private static final String MAC_ADDRESS_KEY = "bluetoothAddressKey";
    private static final String SAVED_BT_KEY = "savedBT";
    public static ArrayList<Activity> activity_list = new ArrayList<Activity>();
    public static List<String> orderList = new ArrayList<>();
    public static String outletname, customercode, refrenceno, Comments, trn_no, outletaddress, emirate, customeraddress, customername, vehiclenum, name, route, vanID, userID;
    static byte[] billImageDataSewoo;
    static AllCustomerDetailsDB customerDetailsDB;
    //  public static final String PREFS_NAME = "OurSavedAddress";
    static byte[] billImageData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    ArrayAdapter<String> adapter;
    boolean searchflags;
    SubmitOrderDB submitOrderDB;
    StockDB stockDB;
    UserDetailsDb userDetailsDb;
    Toolbar toolbar;
    private EditText edit_input, edit_inputPerforma;
    private Button button_connect, button_connectPerforma;
    private Button button_search, button_capture, finishButton;
    private ListView list_printer;
    private BroadcastReceiver discoveryResult;
    private BroadcastReceiver searchFinish;
    private BroadcastReceiver searchStart;
    private BroadcastReceiver connectDevice;
    private Vector<BluetoothDevice> remoteDevices;
    private BluetoothDevice btDev;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothPort bluetoothPort;
    private CheckTypesTask BTtask;
    private ExcuteDisconnectBT BTdiscon;
    private ImageView billImageView;
    private Thread btThread;
    private Bitmap billBitmap;
    private boolean disconnectflags;
    private String currentPhotoPath;
    private String str_SavedBT = "";

    private String loadSettingFromPrefs() {
        // Access SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the saved value, provide a default value if not found
        String savedBT = settings.getString(SAVED_BT_KEY, "");  // Default value is empty string

        return savedBT;
    }


    // Key for storing the value

    private void saveSettingToPrefs() {
        try {
            // Access SharedPreferences in private mode
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            // Store the value of str_SavedBT in SharedPreferences
            editor.putString(SAVED_BT_KEY, str_SavedBT);
            editor.apply();  // Asynchronously save the changes

            // Optionally, show a message or log success
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bluetooth_layout);
        Intent intent = getIntent();
        outletname = getIntent().getStringExtra("outletname");
        customercode = getIntent().getStringExtra("customerCode");
        refrenceno = getIntent().getStringExtra("referenceNo");
        Comments = getIntent().getStringExtra("comments");
        newOrderId = getIntent().getStringExtra("newOrderId");
        customeraddress = getIntent().getStringExtra("customeraddress");
        vehiclenum = intent.getStringExtra("vehiclenum");
        name = intent.getStringExtra("name");
        route = intent.getStringExtra("route");
        vanID = intent.getStringExtra("vanid");
        userID = intent.getStringExtra("userid");

        if (customeraddress.length() > 30) {
            // Find the last space within the first 30 characters
            int lastSpace = customeraddress.substring(0, 30).lastIndexOf(' ');

            // If there's a space within the first 30 characters, break there
            if (lastSpace != -1) {
                customeraddress = customeraddress.substring(0, lastSpace) + "\r\n" + " " + customeraddress.substring(lastSpace + 1);
            } else {
                // If there's no space, break at 30 characters
                customeraddress = customeraddress.substring(0, 30) + "\r\n" + customeraddress.substring(30);
            }
        }
        activity_list.add(NewOrderBluetoothActivity.this);

        // loadSettingFile();
        loadSettingFromPrefs();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("GENERATE INVOICE");
        edit_input = (EditText) findViewById(R.id.EditTextAddressBT);
        edit_inputPerforma = findViewById(R.id.EditTextAddressBTPerforma);
        button_connectPerforma = (Button) findViewById(R.id.ButtonConnectBTPerforma);
        button_connect = (Button) findViewById(R.id.ButtonConnectBT);
        button_search = (Button) findViewById(R.id.ButtonSearchBT);
        list_printer = (ListView) findViewById(R.id.ListView01);
        button_capture = (Button) findViewById(R.id.btn_capture_bill);
        button_capture.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        finishButton = (Button) findViewById(R.id.finishDelivery);
        finishButton.setEnabled(false);
        billImageView = (ImageView) findViewById(R.id.billImageView);
        submitOrderDB = new SubmitOrderDB(this);
        stockDB = new StockDB(this);
        userDetailsDb = new UserDetailsDb(this);
        customerDetailsDB = new AllCustomerDetailsDB(this);

        // Set the string to EditText

        edit_input.setText(str_SavedBT);

        outletaddress = getIntent().getStringExtra("address");
        emirate = getIntent().getStringExtra("emirate");
        customername = getIntent().getStringExtra("customername");
        String newSaleBeanListJson = getIntent().getStringExtra("newOrderInvoiceBean");
        if (newSaleBeanListJson != null) {
            Type type = new TypeToken<ArrayList<NewOrderInvoiceBean>>() {
            }.getType();
            newSaleBeanLists = new Gson().fromJson(newSaleBeanListJson, type);
        } else {
            newSaleBeanLists = new ArrayList<>();
        }
        if (outletaddress == null) {
            outletaddress = "DUBAI DESIGN DISTRICT";
        }
        if (emirate == null) {
            emirate = "DUBAI";
        }

        findViewById(R.id.btn_capture_bill).setOnClickListener(v -> {
            String fileName = "billPhoto";
            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri imagreUri = FileProvider.getUriForFile(this, "com.malta_mqf.malta_mobile.fileprovider", photoFile);
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imagreUri);
                startActivityForResult(intent1, 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // 100 for maximum quality
/*
        if(billBitmap== null){
            finishButton.setEnabled(false);
        }*/

        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //     String invoicenumber=   "INV"+outletId+ String.valueOf(generateRandomOrderID());

                String date = getCurrentDateTime();
                String processedCustomerCode = processCustomerCode(customerCode);
                //String newOrderId= processCustomerCode(customerCode)+newOrderoutletid+String.valueOf(generateorder())+"-M-EX";
                //System.out.println("Encoded is:"+ encodedBillImage);
                if (submitOrderDB.checkWhetherOrderIsDelivered(newOrderId)) {
                    Toast.makeText(NewOrderBluetoothActivity.this, "Order Delivered Successfully.", Toast.LENGTH_SHORT).show();
                    TOTALQTY = 0;
                    TOTALGROSS = BigDecimal.valueOf(0);
                    TOTALNET = BigDecimal.valueOf(0);
                    TOTALVAT = BigDecimal.valueOf(0);
                    Toast.makeText(NewOrderBluetoothActivity.this, "Order Delivered Successfully:" + newOrderId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewOrderBluetoothActivity.this, StartDeliveryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    clearAllSharedPreferences();
                    startActivity(intent);
                } else {
                    boolean isUpdated = submitOrderDB.NewOrderInsertion(newOrderId, NewOrderinvoiceNumber, userID, vanID, newOrderoutletid, newSaleBeanLists, String.valueOf(TOTALQTY), String.format("%.2f", TOTALNET), String.format("%.2f", TOTALVAT), String.format("%.2f", TOTALGROSS), String.format("%.2f", TOTALGROSSAFTERREBATE), customercode, date, refrenceno, Comments, "PENDING FOR DELIVERY ");

                    if (isUpdated) {
                        downGradeDeliveryQtyInStockDB(newOrderId);
                        TOTALQTY = 0;
                        TOTALGROSS = BigDecimal.valueOf(0);
                        TOTALNET = BigDecimal.valueOf(0);
                        TOTALVAT = BigDecimal.valueOf(0);
                        // updateInvoiceNumber(NewOrderinvoiceNumber);
                        Toast.makeText(NewOrderBluetoothActivity.this, "Order Delivered Successfully:" + newOrderId, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewOrderBluetoothActivity.this, StartDeliveryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        clearAllSharedPreferences();
                        startActivity(intent);
                        //    finishButton.setEnabled(false);
                        //  finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
                    } else {
                        Toast.makeText(NewOrderBluetoothActivity.this, " Please try again.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });




       /* if(billBitmap== null){
            finishButton.setEnabled(false);
        }*/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list_printer.setAdapter(adapter);


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        searchflags = false;
        disconnectflags = false;

        edit_input.setText(str_SavedBT);
        edit_inputPerforma.setText(str_SavedBT);
        Init_BluetoothSet();

        bluetoothPort = BluetoothPort.getInstance();
        bluetoothPort.SetMacFilter(false);//not using mac address filtering

        addPairedDevices();

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTtask = new NewOrderBluetoothActivity.CheckTypesTask();
                BTtask.execute();
            }
        });


        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String input_ip = edit_input.getText().toString();

                    if (input_ip.equals("")) {
                        alert
                                .setTitle("Error")
                                .setMessage("Input value is Null")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else if (!isValidBluetoothAddress(input_ip)) {
                        alert
                                .setTitle("Error")
                                .setMessage("Invalid Bluetooth Address")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        btConn(mBluetoothAdapter.getRemoteDevice(input_ip));
                        finishButton.setEnabled(true);
                        showExitConfirmationDialog2();
                        returnToStartDelivery();
                        finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button_connectPerforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String input_ipPerforma = edit_inputPerforma.getText().toString();

                    if (input_ipPerforma.equals("")) {
                        alert
                                .setTitle("Error")
                                .setMessage("Please Enter Bluetooth Mac Address")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else if (!isValidBluetoothAddress(input_ipPerforma)) {
                        alert
                                .setTitle("Error")
                                .setMessage("Invalid Bluetooth Address")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        btConnPerforma(mBluetoothAdapter.getRemoteDevice(input_ipPerforma));

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        list_printer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btDev = remoteDevices.elementAt(i);

                try {
                    btConn(btDev);

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedMac = settings.getString(MAC_ADDRESS_KEY, "");
        edit_input.setText(savedMac);


        edit_inputPerforma.setText(savedMac);
// Enable EditText to allow user input
        edit_input.setEnabled(true);
        edit_input.setFocusable(true);
        edit_input.setFocusableInTouchMode(true);

        edit_inputPerforma.setEnabled(true);
        edit_inputPerforma.setFocusable(true);
        edit_inputPerforma.setFocusableInTouchMode(true);

        // Add TextWatcher for MAC address input validation and saving
        edit_inputPerforma.addTextChangedListener(new TextWatcher() {
            private boolean isToastShown = false;  // To avoid repetitive toasts

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMac = s.toString().trim();
                edit_input.setText(inputMac);

                // MAC address pattern (XX:XX:XX:XX:XX:XX or XX-XX-XX-XX-XX-XX)
                String macAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

                // Validate the MAC address format
                if (inputMac.matches(macAddressPattern)) {
                    button_connectPerforma.setEnabled(true);

                    button_connectPerforma.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                    button_connect.setEnabled(true);
                    button_connect.setBackgroundColor(getResources().getColor(R.color.appColorpurple));


                    // Save the MAC address to SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MAC_ADDRESS_KEY, inputMac);
                    editor.apply();

                    isToastShown = false;  // Reset toast shown state when valid input
                } else {
                    button_connectPerforma.setEnabled(false);
                    button_connectPerforma.setBackgroundColor(getResources().getColor(R.color.listitem_gray));

                    // Show the toast once for invalid MAC address
                    if (!inputMac.isEmpty() && !isToastShown) {
                        //Toast.makeText(getApplicationContext(), "Invalid MAC Address", Toast.LENGTH_SHORT).show();
                        isToastShown = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edit_input.addTextChangedListener(new TextWatcher() {
            private boolean isToastShown = false;  // To avoid repetitive toasts

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMac = s.toString().trim();

                // MAC address pattern (XX:XX:XX:XX:XX:XX or XX-XX-XX-XX-XX-XX)
                String macAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

                // Validate the MAC address format
                if (inputMac.matches(macAddressPattern)) {
                    button_connect.setEnabled(true);
                    button_connect.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

                    // Save the MAC address to SharedPreferences
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(MAC_ADDRESS_KEY, inputMac);
                    editor.apply();

                    isToastShown = false;  // Reset toast shown state when valid input
                } else {
                    button_connect.setEnabled(false);
                    button_connect.setBackgroundColor(getResources().getColor(R.color.listitem_gray));

                    // Show the toast once for invalid MAC address
                    if (!inputMac.isEmpty() && !isToastShown) {
                        //Toast.makeText(getApplicationContext(), "Invalid MAC Address", Toast.LENGTH_SHORT).show();
                        isToastShown = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (!savedMac.isEmpty() && savedMac.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")) {
            button_connect.setEnabled(true);
            button_connect.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
            button_connectPerforma.setEnabled(true);
            button_connectPerforma.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        } else {
            /*button_connect.setEnabled(false);
            button_connect.setBackgroundColor(getResources().getColor(R.color.listitem_gray));*/
            button_connectPerforma.setEnabled(false);
            button_connectPerforma.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            if (savedMac.isEmpty()) {
                Toast.makeText(this, "Please enter a valid MAC Address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void returnToStartDelivery() {
        System.out.println("inside returntostartdelivery method new order connection screen");
        String date = getCurrentDateTime();
        String processedCustomerCode = processCustomerCode(customerCode);

//        newOrderId= processCustomerCode(customerCode)+newOrderoutletid+String.valueOf(generateorder()) + "-M-EX";
//        CustomerLogger.i("NewOrderConnectionScreen", "inside finish buttton successfully");
//        CustomerLogger.i("invoice number", NewOrderinvoiceNumber);
//        CustomerLogger.i("orderid",newOrderId);
//        CustomerLogger.i("userId", NewOrderReceiptDemo.userID);
//        CustomerLogger.i("vandID", NewOrderReceiptDemo.vanID);
//        CustomerLogger.i("customercode",customerCode);
//        CustomerLogger.i("outletid", newOrderoutletid);
//        CustomerLogger.i("NewOrderbeanList", String.valueOf(newSaleBeanListsss));
//        CustomerLogger.i("totalQty",String.format("%.2f",(double) NewOrderReceiptDemo.totalQty));
//        CustomerLogger.i("totalNetAmount",String.format("%.2f", NewOrderReceiptDemo.totalNetAmount));
//        CustomerLogger.i("totalVatAmount",String.format("%.2f", NewOrderReceiptDemo.totalVatAmount));
//        CustomerLogger.i("totalGrossAmt",String.format("%.2f", totalGrossAmt));
//        CustomerLogger.i("amountPayableAfterRebate",String.format("%.2f", NewOrderReceiptDemo.amountPayableAfterRebate));

        if (submitOrderDB.checkWhetherOrderIsDelivered(newOrderId)) {
            Toast.makeText(NewOrderBluetoothActivity.this, "Order Delivered Successfully.", Toast.LENGTH_SHORT).show();
        } else {
// Check if the order was inserted successfully
            // String newOrderId= processCustomerCode(customerCode)+newOrderoutletid+String.valueOf(generateorder())+"-M-EX";
            boolean isUpdated = submitOrderDB.NewOrderInsertion(newOrderId, NewOrderinvoiceNumber, userID, vanID, newOrderoutletid, newSaleBeanLists, String.valueOf(TOTALQTY), String.format("%.2f", TOTALNET), String.format("%.2f", TOTALVAT), String.format("%.2f", TOTALGROSS), String.format("%.2f", TOTALGROSSAFTERREBATE), customercode, date, refrenceno, Comments, "PENDING FOR DELIVERY ");
            //System.out.println("Encoded is:"+ encodedBillImage);
            if (isUpdated) {
                downGradeDeliveryQtyInStockDB(newOrderId);
                // updateInvoiceNumber(NewOrderinvoiceNumber);
                Toast.makeText(NewOrderBluetoothActivity.this, "Order Delivered Successfully:" + newOrderId, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(NewOrderBluetoothActivity.this, StartDeliveryActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                clearAllSharedPreferences();
                //startActivity(intent);
                //  finishButton.setEnabled(false);
                //finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            } else {
                Toast.makeText(NewOrderBluetoothActivity.this, " Please try again.", Toast.LENGTH_SHORT).show();

            }

        }


    }

    private void downGradeDeliveryQtyInStockDB(String orderId) {
        // Load the order list from SharedPreferences (if not already loaded)
        if (orderList == null) {
            loadOrderListFromPreferences();
        }

        // Loop through each item in the sale list
        if (!orderList.contains(orderId)) {
            orderList.add(orderId);

            for (int j = 0; j < newSaleBeanLists.size(); j++) {
                // Get the product ID from the sale list
                String productID = newSaleBeanLists.get(j).getItemId();
                String deliveryQtyStr = newSaleBeanLists.get(j).getDelqty();

                // Check if deliveryQtyStr is null or empty
                if (deliveryQtyStr == null || deliveryQtyStr.isEmpty()) {
                    Log.e("downGradeDeliveryQtyInStockDB", "Delivery quantity is null or empty for product ID: " + productID);
                    continue; // Skip this iteration if the delivery quantity is not valid
                }

                int deliveryQty;
                try {
                    // Parse the delivery quantity
                    deliveryQty = Integer.parseInt(deliveryQtyStr);
                } catch (NumberFormatException e) {
                    Log.e("downGradeDeliveryQtyInStockDB", "Invalid delivery quantity: " + deliveryQtyStr + " for product ID: " + productID, e);
                    continue; // Skip this iteration if the delivery quantity is not a valid number
                }

                // Read the current available quantity from the database based on product ID
                Cursor cursor2 = stockDB.readonproductid(productID);
                if (cursor2 != null && cursor2.getCount() > 0) {
                    while (cursor2.moveToNext()) {
                        @SuppressLint("Range")
                        int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                        // Calculate the new available quantity
                        int newAvailableQty = availableQty - deliveryQty;

                        // Ensure the new available quantity does not drop below zero
                        if (newAvailableQty < 0) {
                            newAvailableQty = 0; // Set newAvailableQty to zero if it's negative
                        }

                        // Update the database with the new available quantity
                        stockDB.updateAvailableQty(productID, newAvailableQty);
                    }
                    cursor2.close();
                } else {
                    Log.e("downGradeDeliveryQtyInStockDB", "Cursor is null or empty for product ID: " + productID);
                }
            }

            // Save the updated order list to SharedPreferences
            saveOrderListToPreferences();
        }
    }

    private boolean isValidBluetoothAddress(String address) {
        return address.matches("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showExitConfirmationDialog(); // Show the dialog when the home button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog(); // Show the dialog when the back button is pressed
    }

    private void showExitConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Hey!!! you haven't pressed finish button to finish delivery? Are you sure want to go back?")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(() -> finish());
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Use executor to handle the 'No' action
                    executorService.execute(dialog::dismiss);
                })
                .show();
    }

    private void showExitConfirmationDialog2() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Hey!!! Do not Forget to complete this delivery By Pressing Finish Button!!! ")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("OK", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(dialog::dismiss);
                })

                .show();
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

    private long generateorder() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }

    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("createaddqtypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
        java.text.SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        return formatter.format(calendar.getTime());
    }

    private void saveOrderListToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(orderList);
        editor.putString("orderList", json);
        editor.apply();
    }

    private void loadOrderListFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("orderList", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        orderList = gson.fromJson(json, type);

        if (orderList == null) {
            orderList = new ArrayList<>();
        }
    }

    private void updateInvoiceNumber(String invoicenumber) {
        if (invoicenumber != null) {
            userDetailsDb.updateLastInvoiceNumber(invoicenumber, 1);
        }
    }

    //    private void downGradeDeliveryQtyInStockDB() {
//        // Loop through each item in the sale list
//        for (int j = 0; j < newSaleBeanLists.size(); j++) {
//            // Get the product ID from the sale list
//            String productID = newSaleBeanLists.get(j).getItemId();
//            String deliveryQtyStr = newSaleBeanLists.get(j).getDelqty();
//
//            // Check if deliveryQtyStr is null or empty
//            if (deliveryQtyStr == null || deliveryQtyStr.isEmpty()) {
//                Log.e("downGradeDeliveryQtyInStockDB", "Delivery quantity is null or empty for product ID: " + productID);
//                continue; // Skip this iteration if the delivery quantity is not valid
//            }
//
//            int deliveryQty;
//            try {
//                // Parse the delivery quantity
//                deliveryQty = Integer.parseInt(deliveryQtyStr);
//            } catch (NumberFormatException e) {
//                Log.e("downGradeDeliveryQtyInStockDB", "Invalid delivery quantity: " + deliveryQtyStr + " for product ID: " + productID, e);
//                continue; // Skip this iteration if the delivery quantity is not a valid number
//            }
//
//            // Read the current available quantity from the database based on product ID
//            Cursor cursor2 = stockDB.readonproductid(productID);
//            if (cursor2 != null && cursor2.getCount() > 0) {
//                while (cursor2.moveToNext()) {
//                    @SuppressLint("Range") int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
//
//                    // Calculate the new available quantity
//                    int newAvailableQty = availableQty - deliveryQty;
//
//                    // Ensure the new available quantity does not drop below zero
//                    if (newAvailableQty < 0) {
//                        newAvailableQty = 0; // Set newAvailableQty to zero if it's negative
//                    }
//
//                    // Update the database with the new available quantity
//                    stockDB.updateAvailableQty(productID, newAvailableQty);
//                }
//                cursor2.close();
//            } else {
//                Log.e("downGradeDeliveryQtyInStockDB", "Cursor is null or empty for product ID: " + productID);
//            }
//        }
//    }
    private void upGradeDeliveryQtyInStockDB() {
        // Loop through each item in the sale list
        for (int j = 0; j < creditNotebeanList.size(); j++) {
            // Get the product ID and delivery quantity from the sale list
            String productID = creditNotebeanList.get(j).getItemName();
            int deliveryQty = Integer.parseInt(creditNotebeanList.get(j).getReturnQty());
            String reason1 = "Re-usable";
            String reason = creditNotebeanList.get(j).getRetrunreason();

            if (reason.equalsIgnoreCase(reason1)) {
                // Read the current available quantity from the database based on product ID
                Cursor cursor2 = stockDB.readonproductName(productID);

                if (cursor2 != null && cursor2.getCount() > 0) {
                    // There are records, hence the product exists in the database
                    while (cursor2.moveToNext()) {
                        @SuppressLint("Range") int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                        // Calculate the new available quantity
                        int newAvailableQty = availableQty + deliveryQty;

                        // Ensure the new available quantity does not drop below zero
                        // Ensures the quantity is at least zero

                        // Update the database with the new available quantity
                        stockDB.updateAvailabletoQty(productID, newAvailableQty);
                    }
                    cursor2.close();
                }
                if (cursor2 != null) {
                    cursor2.close();
                }
            }
        }
    }


    private void clearBtDevData() {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    private void bluetoothSetup() {
        // Initialize
        clearBtDevData();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    public void ConnectionFailedDevice() {
        // Check if the activity is finishing or destroyed before showing the dialog
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert
                    .setTitle("Alert!!")
                    .setMessage("The Bluetooth connection is lost.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    public void Init_BluetoothSet() {
        bluetoothSetup();

        connectDevice = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    //Toast.makeText(getApplicationContext(), "BlueTooth Connect", Toast.LENGTH_SHORT).show();
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    try {
                        if (bluetoothPort.isConnected())
                            bluetoothPort.disconnect();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if ((btThread != null) && (btThread.isAlive())) {
                        btThread.interrupt();
                        btThread = null;
                    }

                    ConnectionFailedDevice();

                    //Toast.makeText(getApplicationContext(), "BlueTooth Disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        };

        discoveryResult = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String key;
                boolean bFlag = true;
                BluetoothDevice btDev;
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (remoteDevice != null) {
                    if (ActivityCompat.checkSelfPermission(NewOrderBluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    int devNum = remoteDevice.getBluetoothClass().getMajorDeviceClass();

                    if (devNum != BT_PRINTER)
                        return;

                    if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    } else {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }
                    if (bluetoothPort.isValidAddress(remoteDevice.getAddress())) {
                        for (int i = 0; i < remoteDevices.size(); i++) {
                            btDev = remoteDevices.elementAt(i);
                            if (remoteDevice.getAddress().equals(btDev.getAddress())) {
                                bFlag = false;
                                break;
                            }
                        }
                        if (bFlag) {
                            remoteDevices.add(remoteDevice);
                            adapter.add(key);
                        }
                    }
                }
            }
        };

        registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        searchStart = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Toast.makeText(mainView, "블루투스 기기 검색 시작", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(searchStart, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));

        searchFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchflags = true;
            }
        };
        registerReceiver(searchFinish, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ImageView imageView = (ImageView) findViewById(R.id.billImageView);
            imageView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            billImageDataSewoo = stream.toByteArray();
            System.out.println("billImageByteArray :" + Arrays.toString(billImageDataSewoo));
            finishButton.setEnabled(true);
            finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        }
    }

    private void saveImagesToGallery() {
        if (billBitmap != null) {

            String billFileName = "bill_" + UUID.randomUUID().toString() + ".jpeg";

            saveImageToGallery(billBitmap, billFileName);

            Toast.makeText(this, "Capture both signature and bill first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, null);
    }


    private void addPairedDevices() {
        BluetoothDevice pairedDevice;
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
        Iterator<BluetoothDevice> iter = (mBluetoothAdapter.getBondedDevices()).iterator();

        String key = "";

        while (iter.hasNext()) {
            pairedDevice = iter.next();
            if (bluetoothPort.isValidAddress(pairedDevice.getAddress())) {
                int deviceNum = pairedDevice.getBluetoothClass().getMajorDeviceClass();

                if (deviceNum == BT_PRINTER) {
                    remoteDevices.add(pairedDevice);

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
                    key = pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]";
                    adapter.add(key);
                }
            }
        }
    }

    private void SearchingBTDevice() {
        adapter.clear();
        adapter.notifyDataSetChanged();

        clearBtDevData();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connBT(this).execute(btDev);
    }

    private void btConnPerforma(final BluetoothDevice btDev) throws IOException {
        new connBTPerforma().execute(btDev);
    }

    public void DisconnectDevice() {
        try {
            bluetoothPort.disconnect();

            unregisterReceiver(connectDevice);

            if ((btThread != null) && (btThread.isAlive()))
                btThread.interrupt();

            disconnectflags = true;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ExcuteDisconnect() {
        BTdiscon = new NewOrderBluetoothActivity.ExcuteDisconnectBT();
        BTdiscon.execute();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {

            if (bluetoothPort.isConnected()) {
                bluetoothPort.disconnect();
                unregisterReceiver(connectDevice);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if ((btThread != null) && (btThread.isAlive())) {
            btThread.interrupt();
            btThread = null;
        }

        unregisterReceiver(searchFinish);
        unregisterReceiver(searchStart);
        unregisterReceiver(discoveryResult);
    }

    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(NewOrderBluetoothActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Searching the Printer...");
            asyncDialog.setCancelable(false);
            asyncDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            searchflags = true;
                            if (ActivityCompat.checkSelfPermission(NewOrderBluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    });
            asyncDialog.show();
            SearchingBTDevice();
            super.onPreExecute();
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                while (true) {
                    if (searchflags)
                        break;

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (asyncDialog.isShowing())
                asyncDialog.dismiss();

            searchflags = false;
            super.onPostExecute(result);
        }


    }

    class connBT extends AsyncTask<BluetoothDevice, Void, Integer> {
        private WeakReference<Activity> activityReference;
        private ProgressDialog dialog;
        private AlertDialog.Builder alert;
        private String str_temp = "";

        // Constructor to pass the activity reference
        public connBT(Activity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = activityReference.get();
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                dialog = new ProgressDialog(activity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Connecting Device...");
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;
            try {
                bluetoothPort.connect(params[0]);

                // Permission check for Bluetooth
                if (ActivityCompat.checkSelfPermission(activityReference.get(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return null;  // Permissions missing, return early
                }

                str_temp = params[0].getAddress();
                retVal = 0;
            } catch (IOException e) {
                e.printStackTrace();
                retVal = -1;
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Activity activity = activityReference.get();

            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (result != null && result == 0) {
                    // Connection success
                    RequestHandler rh = new RequestHandler();
                    btThread = new Thread(rh);
                    btThread.start();

                    str_SavedBT = str_temp;
                    edit_input.setText(str_SavedBT);
                    saveSettingToPrefs();

                    // Register receivers for Bluetooth events
                    activity.registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                    activity.registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));

                    NewOrdrSamplePrint sp = new NewOrdrSamplePrint();
                    try {
                        sp.Print_Sample_4();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Connection failed, show error dialog
                    if (alert == null) {
                        alert = new AlertDialog.Builder(activity);
                    }
                    alert.setTitle("Alert!!")
                            .setMessage("Please, try again.")
                            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }
        }
    }

    class connBTPerforma extends AsyncTask<BluetoothDevice, Void, Integer> {
        private final ProgressDialog dialog = new ProgressDialog(NewOrderBluetoothActivity.this);
        AlertDialog.Builder alert = new AlertDialog.Builder(NewOrderBluetoothActivity.this);

        String str_tempp = "";

        @Override
        protected void onPreExecute() {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Connecting Device...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {
            Integer retVal = null;

            try {
                bluetoothPort.connect(params[0]);
                if (ActivityCompat.checkSelfPermission(NewOrderBluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null;
                }
// Assuming this is inside the method where you get str_temp
                // str_temp = params[0].getName() + "\n[" + params[0].getAddress() + "] [Connected]";
                str_tempp = params[0].getAddress();

// Store in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stored_string", str_tempp);
                editor.apply();


                retVal = Integer.valueOf(0);
            } catch (IOException e) {
                e.printStackTrace();
                retVal = Integer.valueOf(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result.intValue() == 0)    // Connection success.
            {
                RequestHandler rh = new RequestHandler();
                btThread = new Thread(rh);
                btThread.start();

                str_SavedBT = str_tempp;
                edit_inputPerforma.setText(str_SavedBT);

                saveSettingToPrefs();

                registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                // registerReceiver(connectDevice, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));


                NewOrdrSamplePrint sp1 = new NewOrdrSamplePrint();
                try {
                    sp1.Print_Sample_4Performa();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else    // Connection failed.
            {
                alert
                        .setTitle("Warning")
                        .setMessage("please,try again.")
                        .setNegativeButton("ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            super.onPostExecute(result);

        }
    }

    private class ExcuteDisconnectBT extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(NewOrderBluetoothActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Disconnecting Device...");
            asyncDialog.setCancelable(false);
            asyncDialog.show();
            super.onPreExecute();
        }

        ;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                DisconnectDevice();

                while (true) {
                    if (disconnectflags)
                        break;

                    Thread.sleep(100);

                    Intent in = new Intent(NewOrderBluetoothActivity.this, NewSaleActivity.class);
                    in.putExtra("Connection", "BlueTooth");
                    startActivity(in);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            disconnectflags = false;
            Intent in = new Intent(NewOrderBluetoothActivity.this, NewSaleActivity.class);
            in.putExtra("Connection", "BlueTooth");
            startActivity(in);
            super.onPostExecute(result);
        }

        ;
    }


}

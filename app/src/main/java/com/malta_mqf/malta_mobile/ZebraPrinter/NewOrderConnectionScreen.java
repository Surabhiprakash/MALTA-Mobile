package com.malta_mqf.malta_mobile.ZebraPrinter;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditbeanList;
import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.newSaleBeanListSet;
import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.returnItemDetailsBeanList;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALGROSS;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALGROSSAFTERREBATE;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALNET;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALQTY;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.TOTALVAT;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.newOrderId;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.orderid;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.userID;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.vanID;

import static com.malta_mqf.malta_mobile.NewOrderInvoice.Comments;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.NewOrderinvoiceNumber;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.customerCode;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.newOrderoutletid;
import static com.malta_mqf.malta_mobile.NewOrderInvoice.refrenceno;
import static com.malta_mqf.malta_mobile.NewSaleActivity.customerCodes;
import static com.malta_mqf.malta_mobile.NewSaleActivity.deliveredQty;
import static com.malta_mqf.malta_mobile.NewSaleActivity.invoiceNumber;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;

import static com.malta_mqf.malta_mobile.NewSaleActivity.orderidforNewSale;
import static com.malta_mqf.malta_mobile.NewSaleActivity.outletId;
import static com.malta_mqf.malta_mobile.NewSaleInvoice.orderToInvoice;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.amountPayableAfterRebate;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.newSaleBeanListsss;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.totalGrossAmt;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.totalNetAmount;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.totalQty;
import static com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo.totalVatAmount;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.NewSaleInvoice;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.StartDeliveryActivity;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterNetwork;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class NewOrderConnectionScreen extends AppCompatActivity implements DiscoveryHandler, EasyPermissions.PermissionCallbacks {

    protected Button testButton,captureBillButton,performaButton;
    protected Button secondTestButton,  finishButton;
    private RadioButton btRadioButton;
    private EditText macAddress,macAddressPerforma;
    ListView macAddressList;
    Toolbar toolbar;
    private Bitmap billBitmap;

    private EditText ipAddress;
    private EditText printingPortNumber;
    protected EditText statusPortNumber;
    protected LinearLayout portLayout;
    protected LinearLayout statusPortLayout;
    static byte[] billImageData;

    public static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    public static List<String> orderList = new ArrayList<>();
    public static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    public static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";
    public static final String tcpStatusPortKey = "ZEBRA_DEMO_TCP_STATUS_PORT";
    public static final String PREFS_NAME = "OurSavedAddress";


    protected List<String> discoveredPrinters = null;
    private ZebraExpandableListAdapter mExpListAdapter;
    ExpandableListView mExpListView;

    ImageView billImageView;
    private String currentPhotoPath;
    ImageView signatureImageView;

    public static final int REQUEST_CODE_BILL = 2;

    SubmitOrderDB submitOrderDB;
    ReturnDB returnDB;
    StockDB stockDB;
    UserDetailsDb userDetailsDb;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_screen);
        mExpListView = (ExpandableListView) findViewById(android.R.id.list);
        billImageView = (ImageView) this.findViewById(R.id.billImageView);
        finishButton = (Button) this.findViewById(R.id.finishDelivery);
        finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("GENERATE INVOICE");
        submitOrderDB = new SubmitOrderDB(this);
        returnDB = new ReturnDB(this);
        stockDB = new StockDB(this);
        userDetailsDb=new UserDetailsDb(this);
        captureBillButton = (Button) this.findViewById(R.id.btn_capture_bill);
        captureBillButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

        findViewById(R.id.btn_capture_bill).setOnClickListener(v -> {
            String fileName = "billPhoto";
            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);
                if (photoFile != null) {
                    currentPhotoPath = photoFile.getAbsolutePath();
                    Uri imagreUri = FileProvider.getUriForFile(this, "com.malta_mqf.malta_mobile.fileprovider", photoFile);
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent1.putExtra(MediaStore.EXTRA_OUTPUT, imagreUri);
                    startActivityForResult(intent1, 1);
                } else {
                    Toast.makeText(this, "Error creating file.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "File creation failed.", Toast.LENGTH_SHORT).show();
            }

        });
        // 100 for maximum quality

      /*  if (billBitmap == null) {
            finishButton.setEnabled(false);
        }*/

        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //     String invoicenumber=   "INV"+outletId+ String.valueOf(generateRandomOrderID());

                String date=getCurrentDateTime();
                String processedCustomerCode = processCustomerCode(customerCode);

                //String newOrderId= processCustomerCode(customerCode)+newOrderoutletid+String.valueOf(generateorder()) + "-M-EX";
                CustomerLogger.i("NewOrderConnectionScreen", "inside finish buttton successfully");
                CustomerLogger.i("invoice number", NewOrderinvoiceNumber);
                CustomerLogger.i("orderid",newOrderId);
                CustomerLogger.i("userId",userID);
                CustomerLogger.i("vandID",vanID);
                CustomerLogger.i("customercode",customerCode);
                CustomerLogger.i("outletid", newOrderoutletid);
                CustomerLogger.i("NewOrderbeanList", String.valueOf(newSaleBeanListsss));
                CustomerLogger.i("totalQty",String.format("%.2f",(double) totalQty));
                CustomerLogger.i("totalNetAmount",String.format("%.2f", totalNetAmount));
                CustomerLogger.i("totalVatAmount",String.format("%.2f",totalVatAmount));
                CustomerLogger.i("totalGrossAmt",String.format("%.2f", totalGrossAmt));
                CustomerLogger.i("amountPayableAfterRebate",String.format("%.2f", amountPayableAfterRebate));


                boolean isOrderInserted = submitOrderDB.NewOrderInsertion(newOrderId, NewOrderinvoiceNumber, userID, vanID, newOrderoutletid, newSaleBeanListsss,
                        String.valueOf(TOTALQTY), String.format("%.2f", TOTALNET), String.format("%.2f", TOTALVAT),
                        String.format("%.2f", TOTALGROSS), String.format("%.2f", TOTALGROSSAFTERREBATE), customerCode, date, refrenceno, Comments, "PENDING FOR DELIVERY");

// Check if the order was inserted successfully
                if (submitOrderDB.checkWhetherOrderIsDelivered(newOrderId)) {
                    Toast.makeText(NewOrderConnectionScreen.this, "Order Delivered Successfully.", Toast.LENGTH_SHORT).show();
                    TOTALQTY = 0;
                    TOTALGROSS = BigDecimal.valueOf(0);
                    TOTALNET = BigDecimal.valueOf(0);
                    TOTALVAT = BigDecimal.valueOf(0);
                    Toast.makeText(NewOrderConnectionScreen.this, "Order Delivered Successfully:" + newOrderId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewOrderConnectionScreen.this, StartDeliveryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    clearAllSharedPreferences();
                    startActivity(intent);
                }else {
                    if (isOrderInserted) {
                        downGradeDeliveryQtyInStockDB(newOrderId);
                        TOTALQTY = 0;
                        TOTALGROSS = BigDecimal.valueOf(0);
                        TOTALNET = BigDecimal.valueOf(0);
                        TOTALVAT = BigDecimal.valueOf(0);
                        // updateInvoiceNumber(NewOrderinvoiceNumber);
                        Toast.makeText(NewOrderConnectionScreen.this, "Order Delivered Successfully: " + newOrderId, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NewOrderConnectionScreen.this, StartDeliveryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        clearAllSharedPreferences();
                        startActivity(intent);
                        finishButton.setEnabled(false);
                        finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
                    } else {
                        // Show a toast message if insertion failed
                        Toast.makeText(NewOrderConnectionScreen.this, "Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        macAddress = findViewById(R.id.macInput);
        testButton = findViewById(R.id.testButton);
        btRadioButton = (RadioButton) this.findViewById(R.id.bluetoothRadio);
        macAddressPerforma = findViewById(R.id.macInputPerforma);
        performaButton = findViewById(R.id.btnPerforma);
// Regex for validating MAC Address (XX:XX:XX:XX:XX:XX or XX-XX-XX-XX-XX-XX)
        String macAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";

// Retrieve the MAC address from SharedPreferences
        String mac = settings.getString(bluetoothAddressKey, "");
        macAddress.setText(mac);
        macAddressPerforma.setText(mac);
// Ensure EditText is enabled and focusable
        macAddress.setEnabled(true);
        macAddress.setFocusable(true);
        macAddress.setFocusableInTouchMode(true);
        macAddress.requestFocus();  // Request focus programmatically

        macAddressPerforma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                macAddress.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
// Add the TextWatcher to macAddress
        macAddress.addTextChangedListener(new TextWatcher() {
            private boolean isToastShown = false;  // Track whether toast has been shown

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputMac = s.toString().trim();

                // Validate the MAC address format
                if (inputMac.matches(macAddressPattern)) {
                    testButton.setEnabled(true);
                    performaButton.setEnabled(true);
                    performaButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                    testButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                    isToastShown = false;  // Reset the toast shown state when valid input
                } else {
                    testButton.setEnabled(false);
                    testButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
                    performaButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
                    // Show the toast only once, not repeatedly on every character change
                    if (!inputMac.isEmpty() && !isToastShown) {
                        Toast.makeText(getApplicationContext(), "Invalid MAC Address", Toast.LENGTH_SHORT).show();
                        isToastShown = true;  // Set flag so toast shows only once
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

// Initial check if MAC address was preloaded from SharedPreferences
        if (!mac.isEmpty() && mac.matches(macAddressPattern)) {
            testButton.setEnabled(true);
            performaButton.setEnabled(true);
            testButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
            performaButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        } else {
            testButton.setEnabled(false);
            performaButton.setEnabled(false);
            performaButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            testButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            if (mac.isEmpty()) {
                Toast.makeText(this, "Please enter a valid MAC Address", Toast.LENGTH_SHORT).show();
            }
        }
        RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);

        testButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finishButton.setEnabled(true);
                showExitConfirmationDialog2();
                finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                performTest();
                System.out.println("inside test button new order connection screen");
                returnToStartDelivery();
            }
        });

        performaButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                performTestPerforma();

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.bluetoothRadio) {

                } else {

                }
            }
        });


        setProgressBarIndeterminateVisibility(true);

       /* mExpListAdapter = new ZebraExpandableListAdapter();
        mExpListView.setAdapter(mExpListAdapter);*/
    }

    private void  returnToStartDelivery() {
        System.out.println("inside returntostartdelivery method new order connection screen");
        String date=getCurrentDateTime();
        String processedCustomerCode = processCustomerCode(customerCode);

       // String newOrderId= processCustomerCode(customerCode)+newOrderoutletid+String.valueOf(generateorder()) + "-M-EX";
        CustomerLogger.i("NewOrderConnectionScreen", "inside finish buttton successfully");
        CustomerLogger.i("invoice number", NewOrderinvoiceNumber);
        CustomerLogger.i("orderid",newOrderId);
        CustomerLogger.i("userId",userID);
        CustomerLogger.i("vandID",vanID);
        CustomerLogger.i("customercode",customerCode);
        CustomerLogger.i("outletid", newOrderoutletid);
        CustomerLogger.i("NewOrderbeanList", String.valueOf(newSaleBeanListsss));
        CustomerLogger.i("totalQty",String.format("%.2f",(double) totalQty));
        CustomerLogger.i("totalNetAmount",String.format("%.2f", totalNetAmount));
        CustomerLogger.i("totalVatAmount",String.format("%.2f",totalVatAmount));
        CustomerLogger.i("totalGrossAmt",String.format("%.2f", totalGrossAmt));
        CustomerLogger.i("amountPayableAfterRebate",String.format("%.2f", amountPayableAfterRebate));

        if (submitOrderDB.checkWhetherOrderIsDelivered(newOrderId)) {
            Toast.makeText(NewOrderConnectionScreen.this, "Order Delivered Successfully.", Toast.LENGTH_SHORT).show();
        }else {
// Check if the order was inserted successfully
            boolean isOrderInserted = submitOrderDB.NewOrderInsertion(newOrderId, NewOrderinvoiceNumber, userID, vanID, newOrderoutletid, newSaleBeanListsss,
                    String.valueOf(TOTALQTY), String.format("%.2f", TOTALNET), String.format("%.2f", TOTALVAT),
                    String.format("%.2f", TOTALGROSS), String.format("%.2f", TOTALGROSSAFTERREBATE), customerCode, date, refrenceno, Comments, "PENDING FOR DELIVERY");
            System.out.println(isOrderInserted);
            if (isOrderInserted) {
                downGradeDeliveryQtyInStockDB(newOrderId);
                clearAllSharedPreferences();
                TOTALQTY = 0;
                TOTALGROSS = BigDecimal.valueOf(0);
                TOTALNET = BigDecimal.valueOf(0);
                TOTALVAT = BigDecimal.valueOf(0);
                // updateInvoiceNumber(NewOrderinvoiceNumber);
                Toast.makeText(NewOrderConnectionScreen.this, "Order Delivered Successfully: " + newOrderId, Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(NewOrderConnectionScreen.this, StartDeliveryActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//              //  clearAllSharedPreferences();
//                startActivity(intent);
                finishButton.setEnabled(false);
                finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            } else {
                // Show a toast message if insertion failed
                Toast.makeText(NewOrderConnectionScreen.this, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private long generateorder() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
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
        super.onBackPressed();
        showExitConfirmationDialog(); // Show the dialog when the back button is pressed
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
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
        new AlertDialog.Builder(this)
                .setMessage("Hey!!! Do not Forget to complete this delivery By Pressing Finish Button!!! ")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("OK", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(dialog::dismiss);
                })

                .show();
    }
    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("createaddqtypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
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
        return initials.toString().toUpperCase();
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


    private long generateRandomOrderID() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }
    private void updateInvoiceNumber(String invoicenumber){
        if(invoicenumber!=null){
            userDetailsDb.updateLastInvoiceNumber(invoicenumber,1);
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

            for (int j = 0; j < newSaleBeanListsss.size(); j++) {
                // Get the product ID from the sale list
                String productID = newSaleBeanListsss.get(j).getItemId();
                String deliveryQtyStr = newSaleBeanListsss.get(j).getDelqty();

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



/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_BILL) {
                Intent data=re
                Bundle extras = data.getExtras();
                billBitmap = (Bitmap) data.getExtras().get("data");
                billImageView.setImageBitmap(billBitmap);
                finishButton.setEnabled(true);

                // Convert the signatureBitmap to JPEG format

            }
        }
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            if (currentPhotoPath != null && !currentPhotoPath.isEmpty()) {
                bitmap = BitmapFactory.decodeFile(currentPhotoPath);

                if (bitmap != null) {
                    ImageView imageView = findViewById(R.id.billImageView);
                    imageView.setImageBitmap(bitmap);

                    // Compress bitmap to byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    billImageData = stream.toByteArray();

                    System.out.println("billImageByteArray: " + Arrays.toString(billImageData));
                } else {
                    Log.e("onActivityResult", "Failed to decode Bitmap from currentPhotoPath.");
                    // Handle the case when bitmap is null but continue enabling the finish button
                }
            } else {
                Log.e("onActivityResult", "currentPhotoPath is null or empty.");
                // Continue to allow task completion even if no image
            }

            // Enable the finish button even if no image was captured
            finishButton.setEnabled(true);
            finishButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

            // Proceed with completing the task even if no image was captured
            completeTask();
        }
    }

    private void completeTask() {
        // Your logic for completing the task here
        // This could include saving data, moving to the next activity, etc.
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

        // Insert the image metadata into MediaStore
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Save the bitmap to the gallery
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, null);
    }

    protected int desiredVisibilityForSecondTestButton() {
        return View.INVISIBLE;
    }

    protected boolean shouldAllowPortNumberEditing() {
        return true;
    }

    private void toggleEditField(EditText editText, boolean set) {


        editText.setEnabled(true);


    }

    protected boolean isBluetoothSelected() {
        return btRadioButton.isChecked();
    }

    protected String getMacAddressFieldText() {
        return macAddress.getText().toString();
    }


    protected String getTcpStatusPortNumber() {
        return statusPortNumber.getText().toString();
    }

    protected void disablePortEditBox() {
        toggleEditField(printingPortNumber, false);
    }

    public abstract void performTest();
    public abstract void performTestPerforma();
    public void performSecondTest() {

    }


    public void foundPrinter(final DiscoveredPrinter printer) {
        runOnUiThread(new Runnable() {
            public void run() {
                mExpListAdapter.addPrinterItem((DiscoveredPrinter) printer);
                System.out.println("Discovered: " + printer.getDiscoveryDataMap().toString());
                mExpListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void discoveryFinished() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(NewOrderConnectionScreen.this, " Discovered " + mExpListAdapter.getGroupCount() + " printers", Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    public void discoveryError(String message) {
        new UIHelper(this).showErrorDialogOnGuiThread(message);
    }

    public class ZebraExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<DiscoveredPrinter> printerItems;
        private ArrayList<Map<String, String>> printerSettings;

        public ZebraExpandableListAdapter() {
            printerItems = new ArrayList<DiscoveredPrinter>();
            printerSettings = new ArrayList<Map<String, String>>();
        }

        public void addPrinterItem(DiscoveredPrinter p) {
            printerItems.add(p);
            printerSettings.add(p.getDiscoveryDataMap());
        }

        public Object getChild(int groupPosition, int childPosition) {
            return printerSettings.get(groupPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) NewOrderConnectionScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView itemView = (TextView) (inflater.inflate(android.R.layout.simple_list_item_1, null));
            StringBuilder settingsTextBuilder = new StringBuilder();
            itemView.setMaxLines(printerSettings.get(groupPosition).keySet().size());
            itemView.setTextSize(14.0f);
            for (String key : printerSettings.get(groupPosition).keySet()) {
                settingsTextBuilder.append(key);
                settingsTextBuilder.append(": ");
                settingsTextBuilder.append(printerSettings.get(groupPosition).get(key));
                settingsTextBuilder.append("\n");
            }
            itemView.setText(settingsTextBuilder.toString());
            return itemView;
        }

        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        public Object getGroup(int groupPosition) {
            return printerItems.get(groupPosition);
        }

        public int getGroupCount() {
            return printerItems.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) NewOrderConnectionScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TwoLineListItem itemView = (TwoLineListItem) (inflater.inflate(android.R.layout.simple_expandable_list_item_2, null));
            if (printerItems.get(groupPosition).getDiscoveryDataMap().containsKey("DARKNESS"))
                itemView.setBackgroundColor(0xff4477ff);
            if (printerItems.get(groupPosition) instanceof DiscoveredPrinterNetwork) {
                itemView.getText1().setText(((DiscoveredPrinterNetwork) printerItems.get(groupPosition)).address);
                itemView.getText2().setText(((DiscoveredPrinterNetwork) printerItems.get(groupPosition)).getDiscoveryDataMap().get("DNS_NAME"));
            } else if (printerItems.get(groupPosition) instanceof DiscoveredPrinterBluetooth) {
                itemView.getText1().setText(((DiscoveredPrinterBluetooth) printerItems.get(groupPosition)).address);
                itemView.getText2().setText(((DiscoveredPrinterBluetooth) printerItems.get(groupPosition)).friendlyName);
            }
            return itemView;
        }

        public boolean hasStableIds() {
            return true;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }
}
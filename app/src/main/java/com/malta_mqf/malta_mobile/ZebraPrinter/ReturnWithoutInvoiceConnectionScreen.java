package com.malta_mqf.malta_mobile.ZebraPrinter;

import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.creditNotebeanList;
import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.creditbeanList;
import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.newSaleBeanListSet;
import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.returnItemDetailsBeanList;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.TOTALGROSS;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.TOTALGROSSAFTERREBATE;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.TOTALNET;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.TOTALQTY;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.TOTALVAT;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.userID;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.vanID;

import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;
import static com.malta_mqf.malta_mobile.ReturnAddQtyActivity.selectedproduct;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.credId;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.customerCode;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.outletid;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.returnComments;
import static com.malta_mqf.malta_mobile.ReturnCreditNoteWithoutInvoice.returnrefrence;
import static com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity.signatureData;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.credID;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.returnamountPayableAfterRebate;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.returntotalGrossAmt;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.returntotalNetAmount;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.returntotalQty;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.returntotalVatAmount;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.newSaleBeanListsss;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.totalGrossAmt;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.totalNetAmount;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.totalQty;
import static com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo.totalVatAmount;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.NewOrderInvoice;
import com.malta_mqf.malta_mobile.R;
import com.malta_mqf.malta_mobile.ReturnCreditNote;
import com.malta_mqf.malta_mobile.StartDeliveryActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterNetwork;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ReturnWithoutInvoiceConnectionScreen extends AppCompatActivity {
    protected Button testButton,performaButton;
    protected Button secondTestButton,captureBillButton,finishButton;
    private RadioButton btRadioButton;
    private EditText macAddress,macAddressPerforma;
    private ALodingDialog aLodingDialog;
    ListView macAddressList;
    private Bitmap billBitmap;

    private EditText ipAddress;
    private EditText printingPortNumber;
    protected EditText statusPortNumber;
    protected LinearLayout portLayout;
    protected LinearLayout statusPortLayout;
    static byte[] billImageData;

    public static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    public static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    public static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";
    public static final String tcpStatusPortKey = "ZEBRA_DEMO_TCP_STATUS_PORT";
    public static final String PREFS_NAME = "OurSavedAddress";


    protected List<String> discoveredPrinters = null;
    private ReturnWithoutInvoiceConnectionScreen.ZebraExpandableListAdapter mExpListAdapter;
    ExpandableListView mExpListView;

    ImageView billImageView;
    Toolbar toolbar;
    private String currentPhotoPath;
    ImageView signatureImageView;

    public static final int REQUEST_CODE_BILL = 2;

    SubmitOrderDB submitOrderDB;
    ReturnDB returnDB;
    StockDB stockDB;
    UserDetailsDb userDetailsDb;
    ItemsByAgencyDB itemsByAgencyDB;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int CAMERA_REQUEST_CODE = 100;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return_without_invoice_connection_screen);

        mExpListView=(ExpandableListView) findViewById(android.R.id.list);
        //captureBillButton = (Button) this.findViewById(R.id.btn_capture_bill);
        billImageView = (ImageView) this.findViewById(R.id.billImageView);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("GENERATE INVOICE");
        finishButton = (Button) this.findViewById(R.id.finishDelivery);
        finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
        finishButton.setEnabled(false);
        submitOrderDB= new SubmitOrderDB(this);
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        aLodingDialog=new ALodingDialog(this);
        returnDB=new ReturnDB(this);
        stockDB=new StockDB(this);
        userDetailsDb=new UserDetailsDb(this);
        captureBillButton=findViewById(R.id.btn_capture_bill);
        captureBillButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));

        findViewById(R.id.btn_capture_bill).setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                captureImage();
            }
        });
       /* if(billBitmap== null){
            finishButton.setEnabled(false);
        }*/

        finishButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(newSaleBeanListss.size() == 0){


                   /* Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
                    String date=getCurrentDateTime();
                    CustomerLogger.i("ReturnWithoutInvoiceConnection", "inside finishbuttton successfully");
                    CustomerLogger.i("credId",credID);
                    CustomerLogger.i("userId",userID);
                    CustomerLogger.i("vandID",vanID);
                    CustomerLogger.i("customercode",customerCode);
                    CustomerLogger.i("outletid",outletid);
                    CustomerLogger.i("creditNotebeanList", String.valueOf(creditNotebeanList));
                    CustomerLogger.i("returntotalQty",String.format("%.2f",(double)returntotalQty));
                    CustomerLogger.i("returntotalNetAmount",String.format("%.2f",returntotalNetAmount));
                    CustomerLogger.i("returntotalVatAmount",String.format("%.2f",returntotalVatAmount));
                    CustomerLogger.i("returntotalGrossAmt",String.format("%.2f",returntotalGrossAmt));
                    CustomerLogger.i("returnamountPayableAfterRebate",String.format("%.2f",returnamountPayableAfterRebate));
                    boolean isUpdated=returnDB.returnItemsWithoutInvoice(credID,userID,vanID,customerCode,outletid,creditNotebeanList,String.format("%.2f",(double)TOTALQTY),String.format("%.2f",TOTALNET),String.format("%.2f",TOTALVAT), String.format("%.2f",TOTALGROSS),String.format("%.2f",TOTALGROSSAFTERREBATE),signatureData,"RETURNED NO INVOICE",returnrefrence,returnComments,date);

                    if(isUpdated) {
                        upGradeDeliveryQtyInStockDB();
                       // updateReturnInvoiceNumber(credId);
                        Toast.makeText(ReturnWithoutInvoiceConnectionScreen.this, "Order Returned Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ReturnWithoutInvoiceConnectionScreen.this, StartDeliveryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        creditNotebeanList.clear();
                        newSaleBeanListss.clear();
                        newSaleBeanListsss.clear();
                        newSaleBeanListSet.clear();
                        creditbeanList.clear();
                        selectedproduct.clear();
                        returnItemDetailsBeanList.clear();
                        TOTALQTY=0;
                       TOTALGROSS= 0.0;
                       TOTALNET=0.0;
                       TOTALVAT=0.0;
                        clearAllSharedPreferences();
                        clearAllSharedPreferences2();
                        finish();
                        finishButton.setEnabled(false);
                        finishButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
                    }else{
                        Toast.makeText(ReturnWithoutInvoiceConnectionScreen.this, " Please try again.", Toast.LENGTH_SHORT).show();

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
                    testButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
                    performaButton.setEnabled(true);

                    performaButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
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
            testButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
            performaButton.setEnabled(false);
            performaButton.setBackgroundColor(getResources().getColor(R.color.listitem_gray));
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

        initializeLogger();
        setProgressBarIndeterminateVisibility(true);

       /* mExpListAdapter = new ZebraExpandableListAdapter();
        mExpListView.setAdapter(mExpListAdapter);*/
    }
    private void initializeLogger() {
        CustomerLogger.initialize(this);
        CustomerLogger.i("ReturnWithoutInvoiceConnection", "Logger initialized successfully");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void captureImage() {
        String fileName = "billPhoto";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File photoFile = File.createTempFile(fileName, ".jpg", storageDirectory);
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(this, "com.malta_mqf.malta_mobile.fileprovider", photoFile);
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent1, 1);
            } else {
                Toast.makeText(this, "Error creating file.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "File creation failed.", Toast.LENGTH_SHORT).show();
        }
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
       // super.onBackPressed();
        showExitConfirmationDialog(); // Show the dialog when the back button is pressed
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Hey!!! you haven't pressed finish button to finish return? Are you sure want to go back?")
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
                .setMessage("Hey!!! Do not Forget to complete this return By Pressing Finish Button!!! ")
                .setCancelable(false) // Prevents dialog from closing on outside touch
                .setPositiveButton("OK", (dialog, which) -> {
                    // Use executor to handle the 'Yes' action
                    executorService.execute(dialog::dismiss);
                })

                .show();
    }
    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("NewSalesPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    } private void clearAllSharedPreferences2() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReturnPrefs", Context.MODE_PRIVATE);
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        return formatter.format(calendar.getTime());
    }



    private void downGradeDeliveryQtyInStockDB(String orderId) {
        // Load the order list from SharedPreferences (if not already loaded)

        // Loop through each item in the sale list

            for (int j = 0; j < newSaleBeanListss.size(); j++) {
                // Get the product ID from the sale list
                String productID = newSaleBeanListss.get(j).getProductID();
                String deliveryQtyStr = newSaleBeanListss.get(j).getDeliveryQty();

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
                        @SuppressLint("Range") int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

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

    }


    private void updateReturnInvoiceNumber(String invoicenumber){
        if(invoicenumber!=null){
            userDetailsDb.updateLastRetturnInvoiceNumber(invoicenumber,1);
        }
    }

    private void upGradeDeliveryQtyInStockDB() {
        System.out.println("Starting to upgrade delivery quantity in stock database for reusable returns");

        if (creditNotebeanList == null || creditNotebeanList.isEmpty()) {
            System.out.println("No credit notes to process");
            return;
        }

        String reusableReason = "Re-usable";

        for (int j = 0; j < creditNotebeanList.size(); j++) {
            creditNotebean creditNote = creditNotebeanList.get(j);
            String productName = creditNote.getItemName();
            String reason = creditNote.getRetrunreason();
            String returnQtyStr = creditNote.getReturnQty();

            if (reason != null && reason.equalsIgnoreCase(reusableReason) && returnQtyStr != null && !returnQtyStr.isEmpty()) {
                try {
                    int deliveryQty = Integer.parseInt(returnQtyStr);
                    Cursor cursor2 = stockDB.readonproductName(productName);

                    if (cursor2 != null) {
                        try {
                            if (cursor2.getCount() > 0) {
                                System.out.println("Product exists, updating quantity");
                                while (cursor2.moveToNext()) {
                                    @SuppressLint("Range") int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                    int newAvailableQty = availableQty + deliveryQty;
                                    stockDB.updateAvailabletoQty(productName, newAvailableQty);
                                }
                            } else {
                                System.out.println("Product not found, inserting new entry");
                                Cursor itemCursor = itemsByAgencyDB.readProdcutDataByName(productName);

                                if (itemCursor != null) {
                                    try {
                                        while (itemCursor.moveToNext()) {
                                            @SuppressLint("Range") String productId = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                            @SuppressLint("Range") String itemCode = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                            @SuppressLint("Range") String itemCategory = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CATEGORY));
                                            @SuppressLint("Range") String itemSubCategory = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_SUB_CATEGORY));
                                            stockDB.insertNewProduct(vanID, productName, productId, itemCode, itemCategory, itemSubCategory, deliveryQty);
                                        }
                                    } finally {
                                        itemCursor.close();
                                    }
                                }
                            }
                        } finally {
                            cursor2.close();
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid return quantity: " + returnQtyStr + " for product: " + productName);
                } catch (Exception e) {
                    System.err.println("Error processing product: " + productName);
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Finished processing reusable returns");
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
        if ( billBitmap != null) {

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
    public abstract void performTestPerforma();
    public void performTest() {

    }

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
                Toast.makeText(ReturnWithoutInvoiceConnectionScreen.this, " Discovered " + mExpListAdapter.getGroupCount() + " printers", Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    public void discoveryError(String message) {
        new UIHelper(this).showErrorDialogOnGuiThread(message);
    }

    public abstract void onPermissionsGranted(int requestCode, @NonNull List<String> perms);

    public abstract void onPermissionsDenied(int requestCode, @NonNull List<String> perms);

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
            LayoutInflater inflater = (LayoutInflater) ReturnWithoutInvoiceConnectionScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            LayoutInflater inflater = (LayoutInflater) ReturnWithoutInvoiceConnectionScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
package com.malta_mqf.malta_mobile;


import static com.malta_mqf.malta_mobile.ReturnActivity.customername;
import static com.malta_mqf.malta_mobile.ReturnAddQtyActivity.orderConfrimBeans;
import static com.malta_mqf.malta_mobile.Signature.SignatureActivity.REQUEST_CODE_SIGNATURE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.malta_mqf.malta_mobile.Adapter.ReturnDetailsAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnWithoutInvoiceAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnedAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.Model.OrderConfrimBean;
import com.malta_mqf.malta_mobile.Model.ReturnItemDetailsBean;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConfirmReturnsActivity extends AppCompatActivity {
    String  outletid, customerCode,customeraddress;

    ALodingDialog aLodingDialog;
    Toolbar toolbar;
    String trn;

    public static String vanID,userID,route,lastreturninvoicenumber,name,vehiclenum;
    SubmitOrderDB submitOrderDB;
    UserDetailsDb userDetailsDb;
    ItemsByAgencyDB itemsByAgencyDB;
    List<String> returnreasons ;
    ListView returndetaillistview;
    ReturnWithoutInvoiceAdapter returnDetailsAdapter;

    int totalQty;
    BigDecimal NET, VAT_AMT, GROSS, TOTALVAT = BigDecimal.ZERO, TOTALGROSS = BigDecimal.ZERO;
    BigDecimal TOTALNET = BigDecimal.ZERO,TOTALGROSSAFTERREBATE=BigDecimal.ZERO;
    Button returnButton;
    AllCustomerDetailsDB allCustomerDetailsDB;
    public static Set<NewSaleBean> newSaleBeanListSet=new LinkedHashSet<>();
    public static List<NewSaleBean> creditbeanList=new LinkedList<>() ;
    public static List<ReturnItemDetailsBean> returnItemDetailsBeanList=new LinkedList<>() ;
    public static List<creditNotebean> creditNotebeanList=new LinkedList<>() ;
    public static String credID;
    private Button mSaveButtonPrint, mGetSignatureButton;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ImageView signatureImageView;
    private Bitmap signatureBitmap;
    public static final int BLUETOOTH_ENABLE_REQUEST_CODE = 124;
    ReturnDB returnDB;
    private static final String PREFS_NAMEs = "InvoicePrefss";
    private static final String INVOICE_KEYs = "current_invoice_numbers";
    private SharedPreferences sharedPreferences;
    //SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_returns);
         returnreasons = new LinkedList<>();
         newSaleBeanListSet = new LinkedHashSet<>();
         creditbeanList = new LinkedList<>();
         returnItemDetailsBeanList = new LinkedList<>();
         creditNotebeanList = new LinkedList<>();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(orderConfrimBeans.size()==0){
            showVerificationDialog(this);
        }

        sharedPreferences = getSharedPreferences(PREFS_NAMEs, Context.MODE_PRIVATE);

        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allCustomerDetailsDB=new AllCustomerDetailsDB(this);
        userDetailsDb=new UserDetailsDb(this);
        returnDB = new ReturnDB(this);
        outletid = getIntent().getStringExtra("outletId");
        customerCode = getIntent().getStringExtra("customerCode");
        customeraddress=getIntent().getStringExtra("customeraddess");
        credID=getIntent().getStringExtra("credID");
       // customername = getIntent().getStringExtra("customerName");
        System.out.println("customername in return:" + customername);
        toolbar = findViewById(R.id.toolbar);
        signatureImageView = findViewById(R.id.signatureImageView);
        returndetaillistview = findViewById(R.id.returnlistView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Return");
        aLodingDialog=new ALodingDialog(this);
        mSaveButtonPrint = findViewById(R.id.returnButton);
        mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        mGetSignatureButton = findViewById(R.id.btn_capture);
       // getOrderDetailsBsdOnInvNo(orderConfrimBeans);

        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        setupListView();
        setupGetSignatureButton();


        System.out.println("return list:" + orderConfrimBeans);


        // ReturnDetailsAdapter returnDetailsAdapter = new ReturnDetailsAdapter(this, returnItemList);
        Cursor cursor2 = userDetailsDb.readAllData();
        while (cursor2.moveToNext()) {
            route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
            vanID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
            userID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_USERID));
            /*lastreturninvoicenumber=returnDB.getLastInvoiceNumber();

            if (lastreturninvoicenumber == null || lastreturninvoicenumber.isEmpty() || lastreturninvoicenumber.length()>17) {
                lastreturninvoicenumber=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.RETURN_INVOICE_NUMBER_UPDATING));

            }System.out.println("last return invoice: "+lastreturninvoicenumber);*/
            name = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_NAME));
            vehiclenum = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));

        }
       // String routeName = String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 1));
        //credID = routeName + "R" + getCurrentDate() + generateNextInvoiceNumber(lastreturninvoicenumber);
        //System.out.println("CRED number: " + credID);
        cursor2.close();






        if(signatureBitmap== null){
            mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
            mSaveButtonPrint.setEnabled(false);
        }

        mSaveButtonPrint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                // Reset totals
                if (checkOrderStatusAndUpdateButton(credID)) {
                    showOrderBlockedAlert();
                    return; // Stop execution if the order is delivered with an invoice
                }
                totalQty = 0;
                TOTALNET = BigDecimal.ZERO;
                TOTALVAT = BigDecimal.ZERO;
                TOTALGROSS = BigDecimal.ZERO;

                // Show loading dialog
                if (!ConfirmReturnsActivity.this.isFinishing() && !ConfirmReturnsActivity.this.isDestroyed()) {
                    aLodingDialog.show();
                }

                // Clear previous lists
                creditNotebeanList.clear();
                newSaleBeanListSet.clear();
                creditbeanList.clear();

                // Perform work in background using ExecutorService
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000); // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < orderConfrimBeans.size(); i++) {
                            if (orderConfrimBeans.get(i).getReturn_qty() != null && !orderConfrimBeans.get(i).getReturn_qty().isEmpty() &&
                                    Integer.parseInt(orderConfrimBeans.get(i).getReturn_qty()) > 0) {
                                String itemName = orderConfrimBeans.get(i).getItemName();
                                String delqty = orderConfrimBeans.get(i).getReturn_qty();
                                String reason = orderConfrimBeans.get(i).getReason();

                                Cursor cursor = itemsByAgencyDB.readDataByCustomerCodeAndProdName(customerCode, itemName);
                                if (cursor.getCount() != 0) {
                                    while (cursor.moveToNext()) {
                                        String prodId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                        String prodName = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                        String itemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                        String selling_price = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                                        String uom = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                                        String itemBarcode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                                        String plucode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));

                                        // Create NewSaleBean object
                                        NewSaleBean creditNotebean = new NewSaleBean();

                                        Cursor cursor1 = allCustomerDetailsDB.readDataByCustomerCode(customerCode);
                                        if (cursor1.moveToNext()) {
                                            String disc = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
                                            trn = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                                        }

                                        creditNotebean.setProductID(prodId);
                                        creditNotebean.setProductName(prodName);
                                        creditNotebean.setItemCode(itemCode);
                                        creditNotebean.setBarcode(itemBarcode);
                                        creditNotebean.setPlucode(plucode);
                                        creditNotebean.setApprovedQty(orderConfrimBeans.get(i).getReturn_qty());
                                        creditNotebean.setSellingPrice(selling_price);
                                        creditNotebean.setUom(uom);

                                        boolean isDuplicate = false;
                                        for (NewSaleBean existingBean : newSaleBeanListSet) {
                                            if (existingBean.getProductID().equals(creditNotebean.getProductID())) {
                                                isDuplicate = true;
                                                break;
                                            }
                                        }

                                        if (!isDuplicate) {
                                            newSaleBeanListSet.add(creditNotebean);
                                        }

                                        creditNotebean creditNotebean1 = new creditNotebean();
                                        creditNotebean1.setItemName(itemName);
                                        creditNotebean1.setItemCode(itemCode);
                                        creditNotebean1.setBarcode(itemBarcode);
                                        creditNotebean1.setPlucode(plucode);
                                        creditNotebean1.setRetrunreason(reason);
                                        creditNotebean1.setDelqty(delqty);
                                        creditNotebean1.setReturnQty(orderConfrimBeans.get(i).getReturn_qty());
                                        creditNotebean1.setSellingprice(selling_price);
                                        creditNotebean1.setDisc("0.00");
                                        BigDecimal NET = BigDecimal.valueOf(Double.parseDouble(orderConfrimBeans.get(i).getReturn_qty()) * Double.parseDouble(selling_price)).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setNet(String.format("%.2f", NET));
                                        creditNotebean1.setVat_percent("5");
                                        BigDecimal VAT_AMT = NET.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setVat_amt(String.format("%.2f", VAT_AMT));
                                        BigDecimal GROSS = VAT_AMT.add( NET).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setGross(String.format("%.2f", GROSS));

                                        totalQty += Integer.parseInt(orderConfrimBeans.get(i).getReturn_qty());
                                        TOTALNET =TOTALNET.add( NET);
                                        TOTALVAT =TOTALVAT.add( VAT_AMT);
                                        TOTALGROSS =TOTALGROSS.add( GROSS);

                                        creditNotebeanList.add(creditNotebean1);
                                    }
                                }
                            }
                        }
                        String rebateStr = getCustomerRebate(customerCode);

                        double rebate = 0.0;
                        try {
                            rebate = Double.parseDouble(rebateStr);
                        } catch (NumberFormatException | NullPointerException e) {
                            e.printStackTrace(); // Handle parsing exception if necessary
                        }
                        BigDecimal rebatePercent = BigDecimal.valueOf(rebate).divide(BigDecimal.valueOf(100.0));


// Calculate rebateAmount with proper precision
                        BigDecimal rebateAmount = TOTALGROSS.multiply(rebatePercent).setScale(2, RoundingMode.HALF_UP);;//here ;

// Optionally, if you need `rebatePercent` as a double for

                        TOTALGROSSAFTERREBATE = TOTALGROSS.subtract( rebateAmount);
                        creditbeanList = new LinkedList<>(newSaleBeanListSet);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Prepare the intent with necessary data
                                Intent intent = new Intent(ConfirmReturnsActivity.this, ReturnCreditNoteWithoutInvoice.class);
                                intent.putExtra("trn", trn);
                                intent.putExtra("outletid", outletid);
                                intent.putExtra("route", route);
                                intent.putExtra("name", name);
                                intent.putExtra("vehiclenum", vehiclenum);
                                intent.putExtra("credId", credID);
                                intent.putExtra("vanid",vanID);
                                intent.putExtra("userid",userID);
                                intent.putExtra("TOTALQTY", String.valueOf(totalQty));
                                intent.putExtra("TOTALNET", String.format("%.2f", TOTALNET));
                                intent.putExtra("TOTALVAT", String.format("%.2f", TOTALVAT));
                                intent.putExtra("TOTALGROSS", String.format("%.2f", TOTALGROSS));
                                intent.putExtra("TOTALGROSSAFTERREBATE",String.valueOf(TOTALGROSSAFTERREBATE));
                                intent.putExtra("customerName", customername);
                                intent.putExtra("customerCode", customerCode);
                                intent.putExtra("customeraddress", customeraddress);

                                if (!ConfirmReturnsActivity.this.isFinishing() && !ConfirmReturnsActivity.this.isDestroyed()) {
                                    aLodingDialog.dismiss();
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        });

        mGetSignatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //saveSignatureToGallery(signatureBitmap, "signature");
                Intent signatureIntent = new Intent(ConfirmReturnsActivity.this, SignatureCaptureActivity.class);
                startActivityForResult(signatureIntent, REQUEST_CODE_SIGNATURE);
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
                        Intent i=new Intent(ConfirmReturnsActivity.this,StartDeliveryActivity.class);
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
                    mSaveButtonPrint.setEnabled(false);
                    mSaveButtonPrint.setAlpha(0.5f); // Reduce opacity to indicate it's disabled
                    Toast.makeText(this, "Order is Returned and CredNote exists!", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    mSaveButtonPrint.setEnabled(true);
                    mSaveButtonPrint.setAlpha(1f); // Restore full opacity
                });
            }
            return isDeliveredWithInvoice;
        } catch (IllegalStateException e) {
            Log.e("OrderCheck", "IllegalStateException: " + e.getMessage(), e);
            Toast.makeText(this, "An error occurred while checking order status!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    @SuppressLint("Range")
    private String getCustomerRebate(String customerCode) {
        Cursor cursor = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
        String rebate = null;

        while (cursor.moveToNext()) {
            rebate = cursor.getString(cursor.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
            if (rebate == null) {
                rebate = "0";

            }
        }

        return rebate;
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("customername in return ON RESUME:" + customername);
    }

    private long generateInvoiceNumber() {
        long min = 100000L;  // This is the smallest 15-digit number
        long max = 999999L;  // This is the largest 15-digit number
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
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        return formatter.format(calendar.getTime());
    }

    private void setupGetSignatureButton() {
        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
    }

    private void setupListView() {
        aLodingDialog.show();
        System.out.println("orderConfrimBeans size in setup:" + orderConfrimBeans.size());

        returnDetailsAdapter = new ReturnWithoutInvoiceAdapter(this, orderConfrimBeans);
        returnDetailsAdapter.setOnReturnQuantityExceededListener(new ReturnWithoutInvoiceAdapter.OnReturnQuantityExceededListener() {
            @Override
            public void onReturnQuantityExceeded(boolean isExceeded) {
                if (isExceeded) {
                    mGetSignatureButton.setEnabled(false);
                    mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(ConfirmReturnsActivity.this, R.color.light_grey));
                } else {
                    mGetSignatureButton.setEnabled(true);
                    mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(ConfirmReturnsActivity.this, R.color.appColorpurple));
                }
            }
        });
        returndetaillistview.setAdapter(returnDetailsAdapter);

        // Scroll immediately after setting the adapter
        returndetaillistview.post(new Runnable() {
            @Override
            public void run() {
                returndetaillistview.smoothScrollToPosition(returnDetailsAdapter.getCount() - 1);

                // Delay the cancellation by 3 seconds after scrolling to the end
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.dismiss();
                    }
                }, 3000); // 3000 milliseconds = 3 seconds
            }
        });
    }
    public void showVerificationDialog(Context context ) {
        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("No Items For Return!!")
                .setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     onBackPressed();
                    }
                })
                .setCancelable(false)
                .show();
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








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
/*
    public String generateNextInvoiceNumber() {
        // Get the current invoice number from SharedPreferences
        int currentInvoiceNumber = sharedPreferences.getInt(INVOICE_KEYs, 1);

        // Format the number with leading zeros (e.g., 0000001)
        String formattedInvoiceNumber = String.format("%05d", currentInvoiceNumber);

        // Increment the invoice number
        currentInvoiceNumber++;

        // Save the incremented invoice number back to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEYs, currentInvoiceNumber);
        editor.apply();

        return formattedInvoiceNumber;
    }*/
public String generateNextInvoiceNumber(String lastvoiceInvoicenumber) {
    // Assuming the lastInvoice is in the format "F1R031120240000"
    String prefix = lastvoiceInvoicenumber.substring(0, 11); // SVF180824
    String numericPart = lastvoiceInvoicenumber.substring(11); // 0001

    // Increment the numeric part
    int nextNumber = Integer.parseInt(numericPart) + 1;

    // Format the number to keep leading zeros
    String newInvoiceNumber = String.format("%04d", nextNumber);

    return newInvoiceNumber;
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConfirmReturnsActivity.this, ReturnAddQtyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        int someValue = sharedPreferences.getInt(INVOICE_KEYs, 0);

        // Decrement the value by 1
        someValue--;

        // Save the decremented value back to SharedPreferences (if needed)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEYs, someValue);
        editor.apply();
        orderConfrimBeans.clear();
     //   outletid=null;
       // customerCode=null;
       // trn=null;
        returnreasons.clear();
     //   customername=null;
        newSaleBeanListSet.clear();
        creditbeanList.clear();
        returnItemDetailsBeanList.clear();
        creditNotebeanList.clear();
       // credID=null;
    }
    }

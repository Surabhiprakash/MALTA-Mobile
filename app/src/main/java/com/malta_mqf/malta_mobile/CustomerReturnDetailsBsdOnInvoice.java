package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.Signature.SignatureActivity.REQUEST_CODE_SIGNATURE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.Adapter.ReturnAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnDetailsAdapter;
import com.malta_mqf.malta_mobile.Adapter.ReturnedAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.Model.ReturnItemDetailsBean;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CustomerReturnDetailsBsdOnInvoice extends AppCompatActivity {
    String invoicNo, orderid, outletid, customerCode;

    ALodingDialog aLodingDialog;
    Toolbar toolbar;
    String trn;

  public static   String catVan,route,lastreturninvoicenumber;
    SubmitOrderDB submitOrderDB;
    UserDetailsDb userDetailsDb;
    ItemsByAgencyDB itemsByAgencyDB;
    List<String> returnreasons ;
    ListView returndetaillistview;
    ReturnDetailsAdapter returnDetailsAdapter;
    public static String customername,customeraddress;
    int totalQty;
    BigDecimal NET, VAT_AMT, GROSS, TOTALVAT = BigDecimal.ZERO, TOTALGROSS = BigDecimal.ZERO,TOTALGROSSAFTERREBATE=BigDecimal.ZERO;
    BigDecimal TOTALNET = BigDecimal.ZERO;
    Button returnButton;
    AllCustomerDetailsDB allCustomerDetailsDB;
    public static Set<NewSaleBean> newSaleBeanListSet=new LinkedHashSet<>() ;
    public static List<NewSaleBean> creditbeanList=new LinkedList<>();
    public static List<ReturnItemDetailsBean> returnItemDetailsBeanList=new LinkedList<>();
    public static List<creditNotebean> creditNotebeanList=new LinkedList<>() ;
    public static String credID,returnuserID,returnVanID;
    private Button mSaveButtonPrint, mGetSignatureButton;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private ImageView signatureImageView;
    private Bitmap signatureBitmap;
    public static final int BLUETOOTH_ENABLE_REQUEST_CODE = 124;
    ReturnDB returnDB;
    ReturnedAdapter returnedAdapter;
    SearchView searchView;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAMEs = "InvoicePrefss";
    private static final String INVOICE_KEYs = "current_invoice_numbers";
    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_return_details);
         newSaleBeanListSet = new LinkedHashSet<>();
         creditbeanList = new LinkedList<>();
         returnItemDetailsBeanList = new LinkedList<>();
         creditNotebeanList = new LinkedList<>();
        returnreasons = new LinkedList<>();
        sharedPreferences = getSharedPreferences(PREFS_NAMEs, Context.MODE_PRIVATE);

        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allCustomerDetailsDB=new AllCustomerDetailsDB(this);
        userDetailsDb=new UserDetailsDb(this);
        returnDB = new ReturnDB(this);
        invoicNo = getIntent().getStringExtra("invoiceNo");
        orderid = getIntent().getStringExtra("orderid");
        outletid = getIntent().getStringExtra("outletId");
        customerCode = getIntent().getStringExtra("customerCode");
        System.out.println("customer code in customerReturnDetailsBASD invoice: "+customerCode);
        customername = getIntent().getStringExtra("customerName");
        System.out.println("customer NAME in customerReturnDetailsBASD invoice: "+customername);
        customeraddress=getIntent().getStringExtra("customeraddress");
        toolbar = findViewById(R.id.toolbar);
        signatureImageView = findViewById(R.id.signatureImageView);
        returndetaillistview = findViewById(R.id.returnlistView);
        searchView=findViewById(R.id.searchView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Return");
        returnreasons.add("Expired");
        returnreasons.add("Re-usable");
        returnreasons.add("damaged");
        aLodingDialog=new ALodingDialog(this);
        getOrderDetailsBsdOnInvNo(invoicNo);


            Cursor cursor2 = userDetailsDb.readAllData();
            while (cursor2.moveToNext()) {
                route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
                returnuserID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_USERID));
                returnVanID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
                lastreturninvoicenumber=returnDB.getLastInvoiceNumber();
                if (lastreturninvoicenumber == null || lastreturninvoicenumber.isEmpty() || lastreturninvoicenumber.length()>17) {
                    lastreturninvoicenumber=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.RETURN_INVOICE_NUMBER_UPDATING));

                }
            }
//            String routeName = String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 2));
        String routeName = String.valueOf(route.charAt(0)) + route.substring(route.length() - 2);
        credID = routeName + "R" + getCurrentDate() + generateNextInvoiceNumber(lastreturninvoicenumber);
        System.out.println("CRED number: " + credID);
        cursor2.close();


        mSaveButtonPrint = findViewById(R.id.returnButton);
        mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        mGetSignatureButton = findViewById(R.id.btn_capture);

        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        setupListView();
        setupGetSignatureButton();

       // ReturnDetailsAdapter returnDetailsAdapter = new ReturnDetailsAdapter(this, returnItemList);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (returnDetailsAdapter != null) {
                    returnDetailsAdapter.filter(newText);
                }
                return false;
            }
        });

        if(signatureBitmap== null){
            mSaveButtonPrint.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
            mSaveButtonPrint.setEnabled(false);
        }

        mSaveButtonPrint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                // Reset totals
                totalQty = 0;
                TOTALNET = BigDecimal.ZERO;
                TOTALVAT = BigDecimal.ZERO;
                TOTALGROSS = BigDecimal.ZERO;

                // Show loading dialog
                if (!CustomerReturnDetailsBsdOnInvoice.this.isFinishing() && !CustomerReturnDetailsBsdOnInvoice.this.isDestroyed()) {
                    aLodingDialog.show();
                }

                // Clear previous lists
                creditNotebeanList.clear();
                newSaleBeanListSet.clear();
                creditbeanList.clear();

                // Perform work in background
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Process each item
                        try {
                            Thread.sleep(2000); // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < returnItemDetailsBeanList.size(); i++) {
                            if (returnItemDetailsBeanList.get(i).getReturn_qty() != null && !returnItemDetailsBeanList.get(i).getReturn_qty().isEmpty()) {
                                String itemName = returnItemDetailsBeanList.get(i).getItemName();
                                String delqty = returnItemDetailsBeanList.get(i).getDelivered_qty();
                                String reason = returnItemDetailsBeanList.get(i).getReason();
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

                                        NewSaleBean creditNotebean = new NewSaleBean();

                                        Cursor cursor1 = allCustomerDetailsDB.readDataByName(customername);
                                        if (cursor1.moveToNext()) {
                                            String disc = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
                                            trn = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                                        }

                                        // Set values in creditNotebean
                                        creditNotebean.setProductID(prodId);
                                        creditNotebean.setProductName(prodName);
                                        creditNotebean.setItemCode(itemCode);
                                        creditNotebean.setBarcode(itemBarcode);
                                        creditNotebean.setPlucode(plucode);
                                        creditNotebean.setApprovedQty(returnItemDetailsBeanList.get(i).getReturn_qty());
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
                                        creditNotebean1.setReturnQty(returnItemDetailsBeanList.get(i).getReturn_qty());
                                        creditNotebean1.setSellingprice(selling_price);
                                        creditNotebean1.setDisc("0.00");
                                        BigDecimal NET = BigDecimal.valueOf(Double.parseDouble(returnItemDetailsBeanList.get(i).getReturn_qty()) * Double.parseDouble(selling_price)).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setNet(String.format("%.2f", NET));
                                        creditNotebean1.setVat_percent("5");
                                        BigDecimal VAT_AMT = NET.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setVat_amt(String.format("%.2f", VAT_AMT));
                                        BigDecimal GROSS = VAT_AMT.add( NET).setScale(2, RoundingMode.HALF_UP);
                                        creditNotebean1.setGross(String.format("%.2f", GROSS));

                                        totalQty += Integer.parseInt(returnItemDetailsBeanList.get(i).getReturn_qty());
                                        TOTALNET =TOTALNET.add( NET).setScale(2, RoundingMode.HALF_UP);
                                        TOTALVAT =TOTALVAT.add( VAT_AMT).setScale(2, RoundingMode.HALF_UP);
                                        TOTALGROSS =TOTALGROSS.add( GROSS).setScale(2, RoundingMode.HALF_UP);

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

                        // Prepare the intent with necessary data
                        Intent intent = new Intent(CustomerReturnDetailsBsdOnInvoice.this, ReturnCreditNote.class);
                        intent.putExtra("invoiceNo", invoicNo);
                        intent.putExtra("trn", trn);
                        intent.putExtra("orderid", orderid);
                        intent.putExtra("outletid", outletid);
                        intent.putExtra("credId", credID);
                        intent.putExtra("userid",returnuserID);
                        intent.putExtra("vanid",returnVanID);
                        intent.putExtra("TOTALQTY", String.valueOf(totalQty));
                        intent.putExtra("TOTALNET", String.format("%.2f", TOTALNET));
                        intent.putExtra("TOTALVAT", String.format("%.2f", TOTALVAT));
                        intent.putExtra("TOTALGROSS", String.format("%.2f", TOTALGROSS));
                        intent.putExtra("TOTALGROSSAFTERREBATE",String.valueOf(TOTALGROSSAFTERREBATE));
                        intent.putExtra("customerName", customername);
                        intent.putExtra("customerCode", customerCode);
                        intent.putExtra("customeraddress", customeraddress);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Safely dismiss the dialog and start the new activity
                                if (!CustomerReturnDetailsBsdOnInvoice.this.isFinishing() && !CustomerReturnDetailsBsdOnInvoice.this.isDestroyed()) {
                                    if (aLodingDialog.isShowing()) {
                                        aLodingDialog.dismiss();
                                    }
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
                Intent signatureIntent = new Intent(CustomerReturnDetailsBsdOnInvoice.this, SignatureCaptureActivity.class);
                startActivityForResult(signatureIntent, REQUEST_CODE_SIGNATURE);
            }
        });

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
    private long generateInvoiceNumber() {
        long min = 100000L;  // This is the smallest 15-digit number
        long max = 999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }

  /*  public String generateNextInvoiceNumber() {
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
      // Assuming the lastInvoice is in the format "D3S160920240000"
      String prefix = lastvoiceInvoicenumber.substring(0, 11); // SVF180824
      String numericPart = lastvoiceInvoicenumber.substring(11); // 0001

      // Increment the numeric part
      int nextNumber = Integer.parseInt(numericPart) + 1;

      // Format the number to keep leading zeros
      String newInvoiceNumber = String.format("%04d", nextNumber);

      return newInvoiceNumber;
  }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        // Check if current time is after 10:30 PM but before midnight
        if ((calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) > 30) ||
                (calendar.get(Calendar.HOUR_OF_DAY) >= 21)) {
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

    private void setupGetSignatureButton() {
        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
    }

    private void setupListView() {

            returnDetailsAdapter = new ReturnDetailsAdapter(this, returnItemDetailsBeanList);
            returnDetailsAdapter.setOnReturnQuantityExceededListener(new ReturnDetailsAdapter.OnReturnQuantityExceededListener() {
                @Override
                public void onReturnQuantityExceeded(boolean isExceeded) {
                    if (isExceeded) {
                        mGetSignatureButton.setEnabled(false);
                        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(CustomerReturnDetailsBsdOnInvoice.this, R.color.light_grey));


                    } else {
                        mGetSignatureButton.setEnabled(true);
                        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(CustomerReturnDetailsBsdOnInvoice.this, R.color.appColorpurple));
                    }
                }
            });
            returndetaillistview.setAdapter(returnDetailsAdapter);

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


    private List<ReturnItemDetailsBean> convertListToMapEntryList(List<ReturnItemDetailsBean> list) {
        Set<String> existingKeys = new HashSet<>();
        for (ReturnItemDetailsBean entry : returnItemDetailsBeanList) {
            existingKeys.add(entry.getItemName().trim());
        }

      //  Log.d("convertListToMapEntryList", "Existing keys before adding new entries: " + existingKeys);

        for (ReturnItemDetailsBean entry : list) {
            String keyToCheck = entry.getItemName().trim();
          //  Log.d("convertListToMapEntryList", "Checking key: " + keyToCheck);

            if (!existingKeys.contains(keyToCheck)) {
                returnItemDetailsBeanList.add( new ReturnItemDetailsBean(keyToCheck, entry.getDelivered_qty(),entry.getReturn_qty(), entry.getReasonList()));
                existingKeys.add(keyToCheck);
              //  Log.d("convertListToMapEntryList", "Added entry: " + keyToCheck);
            } else {
               // Log.d("convertListToMapEntryList", "Entry already exists: " + keyToCheck);
            }
        }


        return returnItemDetailsBeanList;
    }

    @SuppressLint("StaticFieldLeak")
    private void getOrderDetailsBsdOnInvNo(String invoicNo) {
        new LoadOrderDetailsTask().execute(invoicNo);
    }

    private class LoadOrderDetailsTask extends AsyncTask<String, Void, List<ReturnItemDetailsBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show loading dialog
            aLodingDialog.show();
        }

        @Override
        protected List<ReturnItemDetailsBean> doInBackground(String... params) {
            String invoicNo = params[0];
            List<ReturnItemDetailsBean> returnItemDetailsBeanList = new ArrayList<>();

            Cursor cursor = submitOrderDB.readDataByInvoiceNoAndStatus(invoicNo, "DELIVERY DONE");
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String itemids = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    @SuppressLint("Range") String customerCode = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
                    @SuppressLint("Range") String delqty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_QTY));
                    String[] itemid = itemids.split(",");
                    String[] delqtys;

                    if (delqty != null) {
                        delqtys = delqty.split(",");
                    } else {
                        delqtys = new String[0]; // Initialize delqtys as an empty array if delqty is null
                    }

                    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    List<Future<List<ReturnItemDetailsBean>>> futures = new ArrayList<>();

                    for (int i = 0; i < itemid.length; i++) {
                        final int index = i;
                        futures.add(executor.submit(() -> {
                            List<ReturnItemDetailsBean> partialList = new ArrayList<>();
                            Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCodeandproID(customerCode, itemid[index]);
                            if (cursor1.getCount() != 0) {
                                while (cursor1.moveToNext()) {
                                    @SuppressLint("Range") String itemName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                    ReturnItemDetailsBean returnItemDetailsBean = new ReturnItemDetailsBean();
                                    returnItemDetailsBean.setItemName(itemName);
                                    returnItemDetailsBean.setDelivered_qty(delqtys[index]);
                                    returnItemDetailsBean.setReasonList(returnreasons);
                                    partialList.add(returnItemDetailsBean);
                                }
                            }
                            return partialList;
                        }));
                    }

                    for (Future<List<ReturnItemDetailsBean>> future : futures) {
                        try {
                            returnItemDetailsBeanList.addAll(future.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    executor.shutdown();
                }
            }

            return convertListToMapEntryList(returnItemDetailsBeanList);
        }

        @Override
        protected void onPostExecute(List<ReturnItemDetailsBean> returnItemDetailsBeanList) {
            super.onPostExecute(returnItemDetailsBeanList);
            // Dismiss loading dialog
            aLodingDialog.cancel();

            if (returnDetailsAdapter == null) {
                returnDetailsAdapter = new ReturnDetailsAdapter(CustomerReturnDetailsBsdOnInvoice.this, returnItemDetailsBeanList);
                returndetaillistview.setAdapter(returnDetailsAdapter);
            } else {
                returnDetailsAdapter.updateData(returnItemDetailsBeanList);
            }
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (returnDetailsAdapter != null) {
                        returnDetailsAdapter.filter(newText);
                    }
                    return false;
                }
            });
            returnDetailsAdapter.setOnReturnQuantityExceededListener(new ReturnDetailsAdapter.OnReturnQuantityExceededListener() {
                @Override
                public void onReturnQuantityExceeded(boolean isExceeded) {
                    if (isExceeded) {
                        mGetSignatureButton.setEnabled(false);
                        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(CustomerReturnDetailsBsdOnInvoice.this, R.color.light_grey));
                    } else {
                        mGetSignatureButton.setEnabled(true);
                        mGetSignatureButton.setBackgroundColor(ContextCompat.getColor(CustomerReturnDetailsBsdOnInvoice.this, R.color.appColorpurple));
                    }
                }
            });
            System.out.println("return list:" + returnItemDetailsBeanList);
        }
    }



/*else while(cursorA.moveToNext()){
            @SuppressLint("Range") String productName=cursorA.getString(cursorA.getColumnIndex(ReturnDB.COLUMN_ITEMNAME));
            @SuppressLint("Range") String delqty=cursorA.getString(cursorA.getColumnIndex(ReturnDB.COLUMN_DEL_QTY));
            @SuppressLint("Range") String returnQty=cursorA.getString(cursorA.getColumnIndex(ReturnDB.COLUMN_RETURN_QTY));
            @SuppressLint("Range") String reason=cursorA.getString(cursorA.getColumnIndex(ReturnDB.COLUMN_RETURN_REASON));

            String[] itemName=productName.split(",");
            String[] delqtys=delqty.split(",");
            String[] returnqtys=returnQty.split(",");
            String[] reasons=reason.split(",");
            for(int i=0;i<itemName.length;i++){
                ReturnItemDetailsBean returnItemDetailsBean = new ReturnItemDetailsBean();
                returnItemDetailsBean.setItemName(itemName[i]);
                returnItemDetailsBean.setDelivered_qty(delqtys[i]);
                returnItemDetailsBean.setReason(reasons[i]);
                returnItemDetailsBean.setReturn_qty(returnqtys[i]);

                returnItemDetailsBeanList.add(returnItemDetailsBean);
               returnedAdapter = new ReturnedAdapter(this, returnItemDetailsBeanList);
                returndetaillistview.setAdapter(returnDetailsAdapter);
            }*/


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
        Intent intent = new Intent(CustomerReturnDetailsBsdOnInvoice.this, CustomerReturn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
        int someValue = sharedPreferences.getInt(INVOICE_KEYs, 0);

        // Decrement the value by 1
        someValue--;

        // Save the decremented value back to SharedPreferences (if needed)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEYs, someValue);
        editor.apply();
        returnItemDetailsBeanList.clear();

        invoicNo=null;
        orderid=null;
        outletid=null;
        customerCode=null;
        trn=null;
        returnreasons.clear();
        customername=null;
        newSaleBeanListSet.clear();
        creditbeanList.clear();
        returnItemDetailsBeanList.clear();
        creditNotebeanList.clear();
        credID=null;
        clearAllSharedPreferences();
    }
    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReturnPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
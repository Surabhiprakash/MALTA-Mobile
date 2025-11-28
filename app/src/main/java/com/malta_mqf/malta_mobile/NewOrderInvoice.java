package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CreateNewOrderForOutletAddQty.finalQty;
import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;
import static com.malta_mqf.malta_mobile.NewSaleActivity.totalQty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malta_mqf.malta_mobile.Adapter.CreditNoteAdapter;
import com.malta_mqf.malta_mobile.Adapter.ShowOrderForInvoiceAdapter;
import com.malta_mqf.malta_mobile.Adapter.ShowOrderForNewInvoiceAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.NewOrderInvoiceBean;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.SewooPrinter.NewOrderBluetoothActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.NewOrderReceiptDemo;
import com.malta_mqf.malta_mobile.ZebraPrinter.NewSaleReceiptDemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class NewOrderInvoice extends AppCompatActivity {
 public static   BigDecimal TOTALNET, TOTALVAT, TOTALGROSS,TOTALGROSSAFTERREBATE;
    TextView orderId, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    Toolbar toolbar;
    ListView listView;
    EditText refrence, comment;
    String catVan;

    public static int TOTALQTY;
    public static String refrenceno, Comments,outletname;

    public static String invoiceNo, orderid, credId, customerName, customerCode, newOrderoutletid, trn;

    UserDetailsDb userDetailsDb;
    Button print;
    SubmitOrderDB submitOrderDB;
    public static List<NewOrderInvoiceBean> newOrderInvoiceBean=new LinkedList<>() ;
    ItemsByAgencyDB itemsByAgencyDB;
    AllCustomerDetailsDB allCustomerDetailsDB;
    private static final String PREFS_NAME = "InvoicePrefs";
    private static final String INVOICE_KEY = "current_invoice_number";
    private SharedPreferences sharedPreferences;
    String [] customerNamearr={"Bandidos Retial LLC","Careem Network General Trading LLC","Delivery Hero Stores DB LLC"};
    private ALodingDialog aLodingDialog;
    public static String newOrderId,NewOrderinvoiceNumber,route,lastinvoicenumber,vehiclenum,name,userID,vanID;
    ApprovedOrderDB approvedOrderDB;
    OutletByIdDB outletByIdDB;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_invoice);
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        customerName = getIntent().getStringExtra("customerName");
        customerCode = getIntent().getStringExtra("customerCode");
        newOrderoutletid = getIntent().getStringExtra("outletId");
        newOrderId=getIntent().getStringExtra("newOrderId");
        NewOrderinvoiceNumber=getIntent().getStringExtra("NewOrderinvoiceNumber");

        System.out.println("newOrderId from the intent is :"+newOrderId);
        outletname=getIntent().getStringExtra("outletName");
        newOrderInvoiceBean = new LinkedList<>();
        System.out.println("outName:" + outletname + "code" + customerCode + "otid" + newOrderoutletid);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        approvedOrderDB = new ApprovedOrderDB(this);
        outletByIdDB=new OutletByIdDB(this);
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        userDetailsDb = new UserDetailsDb(this);
        submitOrderDB=new SubmitOrderDB(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CREATE ORDER "+NewOrderinvoiceNumber);
        Total_Qty = findViewById(R.id.tvTotalQty);
        Total_Net_amt = findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt = findViewById(R.id.tvTotalVatAmt);
        print = findViewById(R.id.btn_save_print);
        refrence = findViewById(R.id.etRefNo);
        comment = findViewById(R.id.etComment);
        aLodingDialog = new ALodingDialog(this);
        print.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        Total_Amount_Payable = findViewById(R.id.tvGrossAmount);


        listView = findViewById(R.id.listViewcredit);

        if (savedInstanceState != null) {
            // Restore saved data
            newOrderInvoiceBean = (List<NewOrderInvoiceBean>) savedInstanceState.getSerializable("newOrderInvoiceBean");
        }
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  saveSignatureToGallery(mSignaturePad.getSignatureBitmap(), "Signature");

                refrenceno = refrence.getText().toString().trim();
                Comments = comment.getText().toString().trim();
                if (submitOrderDB.checkDuplicateReferenceNumber(refrenceno)) {
                    // Duplicate found; exit the method
                    return;
                }
                //showAvailablePrinter();
                boolean shouldProceed = true; // Flag to control further execution

                // Check customer name and reference
                for (String s : customerNamearr) {
                    if (s.equals(customerName)) {
                        if (refrenceno.isEmpty()) {
                            refrence.setError("Reference name cannot be empty for " + s);
                            aLodingDialog.cancel();
                            shouldProceed = false; // Stop further execution
                            break; // Exit the loop
                        }
                    }
                }

                // Proceed only if the reference is not empty
                if (shouldProceed) {
                    // Show available printer
                    showAvailablePrinter();

                    // Dismiss loading dialog after a delay
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 3000);
                }

            }
        });

        displayOrdersForInvoice();


        Cursor cursor2 = userDetailsDb.readAllData();
        while (cursor2.moveToNext()) {
            route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
            vehiclenum=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));
            name=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_NAME));
            userID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_USERID));
            vanID=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VAN_ID));
//            lastinvoicenumber=submitOrderDB.getLastInvoiceNumber();
//            if (lastinvoicenumber == null || lastinvoicenumber.isEmpty() || lastinvoicenumber.length()>17) {
//                lastinvoicenumber=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.INVOICE_NUMBER_UPDATING));
//
//            }
            String category = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_CATEGORY));
            String categoryVan;

        }
//        String routeName=String.valueOf(route.charAt(0)) + String.valueOf(route.charAt(route.length() - 1));
//        NewOrderinvoiceNumber = routeName+ "S" + getCurrentDate() + generateNextInvoiceNumber(lastinvoicenumber) ;
//        System.out.println("new invoice number: "+NewOrderinvoiceNumber);
        cursor2.close();
    }

    private long generateInvoiceNumber() {
        long min = 100000L;  // This is the smallest 15-digit number
        long max = 999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }
/*
    public String generateNextInvoiceNumber() {
        // Get the current invoice number from SharedPreferences
        int currentInvoiceNumber = sharedPreferences.getInt(INVOICE_KEY, 1);

        // Format the number with leading zeros (e.g., 0000001)
        String formattedInvoiceNumber = String.format("%05d", currentInvoiceNumber);

        // Increment the invoice number
        currentInvoiceNumber++;

        // Save the incremented invoice number back to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEY, currentInvoiceNumber);
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

    @Override
    protected void onResume() {
        super.onResume();
        displayOrdersForInvoice();
    }

    @SuppressLint("Range")
    private void displayOrdersForInvoice() {
        // Clear the previous data
        newOrderInvoiceBean.clear();
        TOTALQTY= 0;
        TOTALNET = BigDecimal.ZERO;
        TOTALVAT = BigDecimal.ZERO;
        TOTALGROSS = BigDecimal.ZERO;

        // Loop through the finalQty list to get the order details
        for (int i = 0; i < finalQty.size(); i++) {
            NewOrderInvoiceBean newOrder = new NewOrderInvoiceBean();

            if (!finalQty.get(i).getDelQty().equals("0")) {
                newOrder.setDelqty(finalQty.get(i).getDelQty());

                newOrder.setItemName(finalQty.get(i).getProductName());


                String productID = finalQty.get(i).getProductID();
                newOrder.setItemId(productID);
                Cursor cursor1 = approvedOrderDB.get1PO(productID);
                List<String> poDetailsList = new ArrayList<>();
                List<String> porefnameDetailsList = new ArrayList<>();
                List<String> poCreatedDateDetailsList = new ArrayList<>();
                if (cursor1.moveToNext()) {
                    do {
                        String po_ref = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO));
                        String po_ref_name = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO_REFNAME));
                        String po_created_date = cursor1.getString(cursor1.getColumnIndex(ApprovedOrderDB.COLUMN_PO_CREATED_DATE));
                        System.out.println("in new order invoice -productId:" + productID + "po_ref:" + po_ref);
                        if (!poDetailsList.contains(po_ref)) {
                            poDetailsList.add(po_ref);
                            porefnameDetailsList.add(po_ref_name);
                            poCreatedDateDetailsList.add(po_created_date);
                        }

                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                newOrder.setPo(poDetailsList);
                newOrder.setPorefname(porefnameDetailsList);
                newOrder.setPocreateddate(poCreatedDateDetailsList);
                Cursor cursor2 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
                if (cursor2.moveToNext()) {
                    trn = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                }
                Cursor cursor = itemsByAgencyDB.readDataByCustomerCodeprodId(customerCode, productID);

                if (cursor.moveToFirst()) {

                    // Only process if the cursor has results

                    String itemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                    String sellingPrices = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                    String uom = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                    String agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                    String itemBarcode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                    String plucode=cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));

                    newOrder.setAgency_code(agencycode);
                    newOrder.setItemCode(itemCode);
                    newOrder.setBarcode(itemBarcode);
                    newOrder.setPlucode(plucode);
                    newOrder.setSellingprice(sellingPrices);

                    System.out.println(" Price is...." + sellingPrices);
                    newOrder.setUom(uom);
                    newOrder.setDisc("0.00");

                    // Calculate NET
                    BigDecimal qty = BigDecimal.valueOf(Double.parseDouble(finalQty.get(i).getDelQty()));
                    BigDecimal sellingPrice = BigDecimal.valueOf(Double.parseDouble(sellingPrices)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal NET = qty.multiply( sellingPrice).setScale(2, RoundingMode.HALF_UP);

                    // Set NET amount
                    newOrder.setNet(String.format("%.2f", NET));

                    // Calculate VAT
                    BigDecimal VAT_PERCENT = BigDecimal.valueOf(0.05); // Assuming VAT is 5%
                    BigDecimal VAT_AMT = NET.multiply( VAT_PERCENT).setScale(2, RoundingMode.HALF_UP);
                    newOrder.setVat_percent("5");
                    newOrder.setVat_amt(String.format("%.2f", VAT_AMT));

                    // Calculate GROSS amount
                    BigDecimal GROSS = NET.add( VAT_AMT);
                    newOrder.setGross(String.format("%.2f", GROSS));

                    // Update totals
                    TOTALQTY += qty.intValue();
                    TOTALNET = TOTALNET.add(NET).setScale(2, RoundingMode.HALF_UP);
                    TOTALVAT =TOTALVAT.add( VAT_AMT).setScale(2, RoundingMode.HALF_UP);
                    TOTALGROSS =TOTALGROSS.add( GROSS).setScale(2, RoundingMode.HALF_UP);
                }
                cursor.close(); // Make sure to close the cursor

                // Add the bean to the list
                newOrderInvoiceBean.add(newOrder);

            }


        }

        // Update UI with totals
        Total_Qty.setText("Total Qty: " + TOTALQTY);
        Total_Net_amt.setText("Total Net Amount: " +  TOTALNET.setScale(2, RoundingMode.HALF_UP));
        Total_vat_amt.setText("Total VAT Amount: " +  TOTALVAT.setScale(2, RoundingMode.HALF_UP));
        Total_Amount_Payable.setText("Total Amount Payable: " + TOTALGROSS.setScale(2, RoundingMode.HALF_UP));

        // Set adapter
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
        ShowOrderForNewInvoiceAdapter showOrderForNewInvoiceAdapter = new ShowOrderForNewInvoiceAdapter(this, newOrderInvoiceBean);
        listView.setAdapter(showOrderForNewInvoiceAdapter);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save orderToInvoice list
        outState.putSerializable("newOrderInvoiceBean", new LinkedList<>(newOrderInvoiceBean));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the list
        if (savedInstanceState != null) {
            newOrderInvoiceBean = (List<NewOrderInvoiceBean>) savedInstanceState.getSerializable("newOrderInvoiceBean");
            displayOrdersForInvoice();
        }
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

    private void showAvailablePrinter() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) NewOrderInvoice.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(NewOrderInvoice.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_spinner);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setTitle("Choose printer");
        TextView sewoo_print = dialog.findViewById(R.id.sewoo_print);
        TextView zeb_print = dialog.findViewById(R.id.zeb_print);
        sewoo_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {

                    /*Intent intent = new Intent(NewSaleActivity.this, Bluetooth_Activity.class);
                    startActivity(intent);
                    dialog.dismiss();*/
                String outletAddress="";
                String emirate="";
                String customeraddress="";
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customerCode);
                if(cursor1.getCount()!=0){
                    while (cursor1.moveToNext()) {
                        customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));

                    }
                }
                Cursor cursor=outletByIdDB.readOutletByName(outletname);
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()){
                        outletAddress=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));

                    }
                }
                cursor.close();

                cursor1.close();
                Intent intent = new Intent(NewOrderInvoice.this, NewOrderBluetoothActivity.class);

                intent.putExtra("referenceNo", refrenceno);//newOrderId
                intent.putExtra("comments", Comments);
                intent.putExtra("outletname",outletname);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customerName);
                intent.putExtra("address",outletAddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("newOrderInvoiceBean",new Gson().toJson(newOrderInvoiceBean));
                intent.putExtra("route",route);
                intent.putExtra("vehiclenum",vehiclenum);
                intent.putExtra("name",name);
                intent.putExtra("vanid",vanID);
                intent.putExtra("userid",userID);
                intent.putExtra("newOrderId",newOrderId);
                startActivity(intent);
                dialog.dismiss();
            }

        });

        zeb_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                String outletAddress="";
                String emirate="";
                String customeraddress="";
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customerCode);
                if(cursor1.getCount()!=0){
                    while (cursor1.moveToNext()) {
                        customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));

                    }
                }
                Cursor cursor=outletByIdDB.readOutletByName(outletname);
                if(cursor.getCount()!=0) {
                    while (cursor.moveToNext()) {
                        outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));

                    }
                }
                cursor.close();
                cursor1.close();
                Intent intent = new Intent(NewOrderInvoice.this, NewOrderReceiptDemo.class);
                intent.putExtra("referenceNo", refrenceno);
                intent.putExtra("comments", Comments);
                intent.putExtra("outletname",outletname);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customerName);
                intent.putExtra("address",outletAddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("route",route);
                intent.putExtra("vehiclenum",vehiclenum);
                intent.putExtra("name",name);
                intent.putExtra("vanid",vanID);
                intent.putExtra("userid",userID);
                intent.putExtra("newOrderId",newOrderId);
                startActivity(intent);
                dialog.dismiss();


            }
        });
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
        Intent intent = new Intent(NewOrderInvoice.this, CreateNewOrderForOutletAddQty.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        int someValue = sharedPreferences.getInt(INVOICE_KEY, 0);

        // Decrement the value by 1
        someValue--;

        // Save the decremented value back to SharedPreferences (if needed)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(INVOICE_KEY, someValue);
        editor.apply();
        creditNotebeanList.clear();
        /*refrenceno = null;
        Comments = null;
        orderid = null;
        customerName = null;
        customerCode = null;
        newOrderoutletid = null;
        trn = null;
        NewOrderinvoiceNumber = null;*/
        newOrderInvoiceBean.clear();

    }

}

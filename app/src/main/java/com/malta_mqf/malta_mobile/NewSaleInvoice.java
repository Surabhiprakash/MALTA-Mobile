package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;
import static com.malta_mqf.malta_mobile.NewSaleActivity.outletId;
import static com.malta_mqf.malta_mobile.NewSaleActivity.totalQty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.malta_mqf.malta_mobile.Adapter.ShowOrderForInvoiceAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.SewooPrinter.Bluetooth_Activity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.NewSaleReceiptDemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class NewSaleInvoice extends AppCompatActivity {
    public static BigDecimal  TOTALNET, TOTALVAT, TOTALGROSS,TOTALGROSSAFTERREBATE;
    TextView orderId, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    ListView listView;
    EditText refrence, comment;
    Toolbar toolbar;
    private ALodingDialog aLodingDialog;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    public static List<ShowOrderForInvoiceBean>  orderToInvoice = new LinkedList<>();;
    public static int TOTALQTY=0;
    public static String refrenceno, Comments;
    public static String invoiceNo, orderid, customerName, customerCode, customeraddress, outletid, trn_no,vehiclenum,name,route,userID,vanID;

    Button print;
    String[] customerNamearr = {"Bandidos Retial LLC", "Careem Network General Trading LLC", "Delivery Hero Stores DB LLC"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale_invoice);
         orderToInvoice = new LinkedList<>();

        if (savedInstanceState != null) {
            // Restore saved data
            orderToInvoice = (List<ShowOrderForInvoiceBean>) savedInstanceState.getSerializable("orderToInvoice");
        }

        // Get extras from intent
        orderid = getIntent().getStringExtra("orderid");
        customerName = getIntent().getStringExtra("customerName");
        customerCode = getIntent().getStringExtra("customerCode");
        customeraddress = getIntent().getStringExtra("customeraddress");
        outletid = getIntent().getStringExtra("outletId");
        trn_no = getIntent().getStringExtra("trn_no");
        invoiceNo=getIntent().getStringExtra("invoiceNo");
        vehiclenum=getIntent().getStringExtra("vehiclenum");
        name=getIntent().getStringExtra("name");
        route=getIntent().getStringExtra("route");
        userID=getIntent().getStringExtra("userid");
        vanID=getIntent().getStringExtra("vanid");
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);
        aLodingDialog = new ALodingDialog(this);

        setupUI();

        displayOrdersForInvoice();

        print.setOnClickListener(view -> {
            refrenceno = refrence.getText().toString().trim();
            Comments = comment.getText().toString().trim();

            if (!NewSaleInvoice.this.isFinishing() && !NewSaleInvoice.this.isDestroyed()) {
                aLodingDialog.show();
            }

            boolean shouldProceed = validateCustomerReference();

            if (shouldProceed) {
                showAvailablePrinter();
                dismissLoadingAfterDelay();
            }
        });
    }

    private void setupUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(orderid);

        orderId = findViewById(R.id.tvCreditNoteid);
        Total_Qty = findViewById(R.id.tvTotalQty);
        Total_Net_amt = findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt = findViewById(R.id.tvTotalVatAmt);
        print = findViewById(R.id.btn_save_print);
        refrence = findViewById(R.id.etRefNo);
        comment = findViewById(R.id.etComment);
        Total_Amount_Payable = findViewById(R.id.tvGrossAmount);

        print.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        listView = findViewById(R.id.listViewcredit);
        orderId.setText(orderid);
    }

    private boolean validateCustomerReference() {
        for (String s : customerNamearr) {
            if (s.equals(customerName) && refrenceno.isEmpty()) {
                refrence.setError("Reference name cannot be empty for " + s);
                if (aLodingDialog.isShowing()) {
                    aLodingDialog.cancel();
                }
                return false;
            }
        }
        return true;
    }

    private void displayOrdersForInvoice() {

        // newSaleBeanListss.clear();
        orderToInvoice.clear();
        TOTALQTY = 0;
        TOTALNET = BigDecimal.ZERO;
        TOTALVAT = BigDecimal.ZERO;
        TOTALGROSS = BigDecimal.ZERO;

        for (int i = 0; i < newSaleBeanListss.size(); i++) {

            ShowOrderForInvoiceBean showOrderForInvoiceBean = new ShowOrderForInvoiceBean();
            showOrderForInvoiceBean.setItemName(newSaleBeanListss.get(i).getProductName());
            showOrderForInvoiceBean.setItemCode(newSaleBeanListss.get(i).getItemCode());
            showOrderForInvoiceBean.setBarcode(newSaleBeanListss.get(i).getBarcode());
            showOrderForInvoiceBean.setPlucode(newSaleBeanListss.get(i).getPlucode());
            showOrderForInvoiceBean.setDelqty(newSaleBeanListss.get(i).getDeliveryQty());
            showOrderForInvoiceBean.setSellingprice(newSaleBeanListss.get(i).getSellingPrice());
            showOrderForInvoiceBean.setUom(newSaleBeanListss.get(i).getUom());

            showOrderForInvoiceBean.setDisc("0.00");



         /*   Cursor cursor2=allCustomerDetailsDB.readDataByName(customerName);
            if(cursor2.moveToNext()){
                @SuppressLint("Range")
                String discount=cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_REBATE));
                @SuppressLint("Range")
                String trn_no=cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));

                if(discount==null || discount.isEmpty()){
                    showOrderForInvoiceBean.setDisc("0.00");

                }else{
                    showOrderForInvoiceBean.setDisc(discount);

                }
            }*/

            System.out.println("Printed here deliveryqty = " + newSaleBeanListss.get(i).getDeliveryQty());
            System.out.println("Printed here Sellingprice = " + newSaleBeanListss.get(i).getSellingPrice());
            BigDecimal NET = BigDecimal.valueOf(Double.parseDouble(newSaleBeanListss.get(i).getDeliveryQty()) * Double.parseDouble(newSaleBeanListss.get(i).getSellingPrice())).setScale(2, RoundingMode.HALF_UP);
            System.out.println(NET);
            System.out.println("selling:" + newSaleBeanListss.get(i).getSellingPrice());
            showOrderForInvoiceBean.setNet(String.valueOf(NET));
            showOrderForInvoiceBean.setVat_percent("5");
            BigDecimal VAT_AMT = NET.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            showOrderForInvoiceBean.setVat_amt(String.valueOf(VAT_AMT));
            BigDecimal GROSS = VAT_AMT.add( NET);
            showOrderForInvoiceBean.setGross(String.valueOf(GROSS));

            TOTALQTY += Integer.parseInt(newSaleBeanListss.get(i).getDeliveryQty());

            TOTALNET =TOTALNET.add( NET.setScale(2, RoundingMode.HALF_UP));
            TOTALVAT =TOTALVAT.add( VAT_AMT.setScale(2, RoundingMode.HALF_UP));
            TOTALGROSS =TOTALGROSS.add( GROSS.setScale(2, RoundingMode.HALF_UP));


            orderToInvoice.add(showOrderForInvoiceBean);
        }

        Total_Qty.setText("Total Qty: " + TOTALQTY);
        Total_Net_amt.setText("Total Net Amount: " +  TOTALNET.setScale(2, RoundingMode.HALF_UP).toPlainString());
        Total_vat_amt.setText("Total VAT Amount: " + TOTALVAT.setScale(2, RoundingMode.HALF_UP).toPlainString());
        Total_Amount_Payable.setText("Total Amount Payable: " + TOTALGROSS.setScale(2, RoundingMode.HALF_UP).toPlainString());
       /* Collections.sort(orderToInvoice, new Comparator<ShowOrderForInvoiceBean>() {
            @Override
            public int compare(ShowOrderForInvoiceBean o1, ShowOrderForInvoiceBean o2) {
                // Prioritize "PENDING FOR DELIVERY" over "DELIVERED"
                if (Integer.parseInt(o1.getDelqty())>Integer.parseInt(o2.getDelqty())) {
                    return -1; // o1 comes before o2
                } else if (Integer.parseInt(o2.getDelqty())>Integer.parseInt(o1.getDelqty())) {
                    return 1; // o2 comes before o1
                }
                return 0; // Keep original order if the statuses are the same
            }
        });*/
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
        ShowOrderForInvoiceAdapter showOrderForInvoiceAdapter = new ShowOrderForInvoiceAdapter(this, orderToInvoice);
        listView.setAdapter(showOrderForInvoiceAdapter);
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
    private void dismissLoadingAfterDelay() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!NewSaleInvoice.this.isFinishing() && !NewSaleInvoice.this.isDestroyed() && aLodingDialog.isShowing()) {
                aLodingDialog.dismiss();
            }
        }, 3000);
    }

    private void showAvailablePrinter() {
        if (NewSaleInvoice.this.isFinishing() || NewSaleInvoice.this.isDestroyed()) {
            return; // Exit if the activity is finishing or destroyed
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) NewSaleInvoice.this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(NewSaleInvoice.this);
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
                // Ensure the dialog is still showing before attempting to dismiss it
                if (dialog.isShowing()) {
                    String outletName = "", outletAddress = "", emirate = "";
                    Cursor cursor = outletByIdDB.readOutletByID(outletid);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                            outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                            emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));
                        }
                    }
                    cursor.close();
                    Intent intent = new Intent(NewSaleInvoice.this, Bluetooth_Activity.class);
                    intent.putExtra("referenceNo", refrenceno);
                    intent.putExtra("comments", Comments);
                    intent.putExtra("outletname", outletName);
                    intent.putExtra("customerCode", customerCode);
                    intent.putExtra("customeraddress", customeraddress);
                    intent.putExtra("trn", trn_no);
                    intent.putExtra("address", outletAddress);
                    intent.putExtra("emirate", emirate);
                    intent.putExtra("customercode", customerName);
                    intent.putExtra("orderToInvoice", new Gson().toJson(orderToInvoice));
                    intent.putExtra("invoiceNo",invoiceNo);
                    intent.putExtra("route",route);
                    intent.putExtra("vehiclenum",vehiclenum);
                    intent.putExtra("name",name);
                    intent.putExtra("vanid",vanID);
                    intent.putExtra("userid",userID);
                    startActivity(intent);
                    dialog.dismiss();
                    aLodingDialog.cancel();
                }
            }
        });

        zeb_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                // Ensure the dialog is still showing before attempting to dismiss it
                if (dialog.isShowing()) {
                    aLodingDialog.show();

                    String outletName = "", outletAddress = "", emirate = "";
                    Cursor cursor = outletByIdDB.readOutletByID(outletid);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                            outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                            emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));
                        }
                    }
                    cursor.close();
                    Intent intent = new Intent(NewSaleInvoice.this, NewSaleReceiptDemo.class);
                    intent.putExtra("referenceNo", refrenceno);
                    intent.putExtra("comments", Comments);
                    intent.putExtra("outletname", outletName);
                    intent.putExtra("customername", customerName);
                    intent.putExtra("customeraddress", customeraddress);
                    intent.putExtra("customerCode", customerCode);
                    intent.putExtra("address", outletAddress);
                    intent.putExtra("emirate", emirate);
                    intent.putExtra("orderid",orderid);
                    intent.putExtra("outletid",outletid);
                    intent.putExtra("invoiceNo",invoiceNo);
                    intent.putExtra("route",route);
                    intent.putExtra("vehiclenum",vehiclenum);
                    intent.putExtra("name",name);
                    intent.putExtra("vanid",vanID);
                    intent.putExtra("userid",userID);
                    startActivity(intent);
                    dialog.dismiss();
                    aLodingDialog.cancel();
                }
            }
        });

        dialog.show();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save orderToInvoice list
        outState.putSerializable("orderToInvoice", new LinkedList<>(orderToInvoice));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the list
        if (savedInstanceState != null) {
            orderToInvoice = (List<ShowOrderForInvoiceBean>) savedInstanceState.getSerializable("orderToInvoice");
            displayOrdersForInvoice();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewSaleInvoice.this, NewSaleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        creditNotebeanList.clear();
        orderToInvoice.clear();
    }
}

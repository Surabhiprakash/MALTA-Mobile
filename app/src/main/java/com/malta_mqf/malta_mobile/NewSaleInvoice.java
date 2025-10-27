package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.NewSaleActivity.extranewSaleBeanListss;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.malta_mqf.malta_mobile.Adapter.ShowOrderForInvoiceAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.NewSaleBean;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;
import com.malta_mqf.malta_mobile.SewooPrinter.Bluetooth_Activity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.NewSaleReceiptDemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NewSaleInvoice extends AppCompatActivity {
    public static BigDecimal TOTALNET, TOTALVAT, TOTALGROSS, TOTALGROSSAFTERREBATE;
    public static List<ShowOrderForInvoiceBean> orderToInvoice = new LinkedList<>();
    public static List<ShowOrderForInvoiceBean> extraorderToInvoice = new LinkedList<>();
    public static int TOTALQTY = 0;
    public static String refrenceno, Comments;
    public static String invoiceNo, orderid, customerName, customerCode, customeraddress, outletid, trn_no, vehiclenum, name, route, userID, vanID;
    TextView orderId, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    ListView listView;
    EditText refrence, comment;
    ;
    Toolbar toolbar;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    ApprovedOrderDB approvedOrderDB;
    Button print;
    String[] customerNamearr = {"Bandidos Retial LLC", "Careem Network General Trading LLC", "Delivery Hero Stores DB LLC"};
    SubmitOrderDB submitOrderDB;
    private ALodingDialog aLodingDialog;
    private List<ShowOrderForInvoiceBean> originalOrderToInvoice = new LinkedList<>();

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
        System.out.println("customeraddress in new sale invoice is : " + customeraddress);
        outletid = getIntent().getStringExtra("outletId");
        trn_no = getIntent().getStringExtra("trn_no");
        invoiceNo = getIntent().getStringExtra("invoiceNo");
        vehiclenum = getIntent().getStringExtra("vehiclenum");
        System.out.println("vehiclenum in new sale invoice is : " + vehiclenum);
        name = getIntent().getStringExtra("name");
        System.out.println("name in new sale invoice is : " + name);
        route = getIntent().getStringExtra("route");
        System.out.println("route in new sale invoice is : " + route);
        userID = getIntent().getStringExtra("userid");
        vanID = getIntent().getStringExtra("vanid");
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        submitOrderDB = new SubmitOrderDB(this);
        outletByIdDB = new OutletByIdDB(this);
        aLodingDialog = new ALodingDialog(this);
        approvedOrderDB = new ApprovedOrderDB(this);
        setupUI();

        displayOrdersForInvoice();

        print.setOnClickListener(view -> {
            refrenceno = refrence.getText().toString().trim();
            Comments = comment.getText().toString().trim();
            if (submitOrderDB.checkDuplicateReferenceNumber(refrenceno)) {
                // Duplicate found; exit the method
                return;
            }


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
        getSupportActionBar().setTitle("Invoice Number :" + invoiceNo);

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
        new Thread(() -> {
            // Temporary lists to avoid modifying global variables in a background thread
            final List<ShowOrderForInvoiceBean> tempOrderList = new LinkedList<>();
            final List<ShowOrderForInvoiceBean> tempExtraOrderList = new LinkedList<>();

            int totalQty = 0;
            BigDecimal totalNet = BigDecimal.ZERO;
            BigDecimal totalVat = BigDecimal.ZERO;
            BigDecimal totalGross = BigDecimal.ZERO;

            // Process main order items
            for (NewSaleBean saleBean : newSaleBeanListss) {
                ShowOrderForInvoiceBean item = createInvoiceItem(saleBean);

                // Fetch PO details
                Cursor cursor = approvedOrderDB.get1PO(saleBean.getProductID());
                List<String> poDetailsList = new ArrayList<>();
                List<String> porefnameDetailsList = new ArrayList<>();
                List<String> poCreatedDateDetailsList = new ArrayList<>();

                if (cursor != null) {
                    try {
                        while (cursor.moveToNext()) {
                            poDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO)));
                            porefnameDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO_REFNAME)));
                            poCreatedDateDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO_CREATED_DATE)));
                        }
                    } finally {
                        cursor.close();
                    }
                }

                item.setExpo(poDetailsList);
                item.setExporefname(porefnameDetailsList);
                item.setExpocreateddate(poCreatedDateDetailsList);

                tempOrderList.add(item);

                // Accumulate totals
                totalQty += Integer.parseInt(saleBean.getDeliveryQty() == null ? "0" : saleBean.getDeliveryQty());
                totalNet = totalNet.add(new BigDecimal(item.getNet()));
                totalVat = totalVat.add(new BigDecimal(item.getVat_amt()));
                totalGross = totalGross.add(new BigDecimal(item.getGross()));
            }

            // Process extra order items
            for (NewSaleBean saleBean : extranewSaleBeanListss) {
                ShowOrderForInvoiceBean item = createInvoiceItem(saleBean);

                // Fetch PO details
                Cursor cursor = approvedOrderDB.get1PO(saleBean.getProductID());
                List<String> poDetailsList = new ArrayList<>();
                List<String> porefnameDetailsList = new ArrayList<>();
                List<String> poCreatedDateDetailsList = new ArrayList<>();

                if (cursor != null) {
                    try {
                        while (cursor.moveToNext()) {
                            poDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO)));
                            porefnameDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO_REFNAME)));
                            poCreatedDateDetailsList.add(cursor.getString(cursor.getColumnIndexOrThrow(ApprovedOrderDB.COLUMN_PO_CREATED_DATE)));
                        }
                    } finally {
                        cursor.close();
                    }
                }

                item.setExpo(poDetailsList);
                item.setExporefname(porefnameDetailsList);
                item.setExpocreateddate(poCreatedDateDetailsList);

                tempExtraOrderList.add(item);
            }

            // Ensure all final variables for lambda expression
            final int finalTotalQty = totalQty;
            final BigDecimal finalTotalNet = totalNet;
            final BigDecimal finalTotalVat = totalVat;
            final BigDecimal finalTotalGross = totalGross;
            final BigDecimal finalRebate = new BigDecimal(getCustomerRebate(customerCode)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            final BigDecimal finalTotalGrossAfterRebate = finalTotalGross.subtract(finalTotalGross.multiply(finalRebate));

            // Update UI safely on the main thread
            // Final lists for UI thread
            final List<ShowOrderForInvoiceBean> finalOrderList = new LinkedList<>(tempOrderList);
            final List<ShowOrderForInvoiceBean> finalExtraList = new LinkedList<>(tempExtraOrderList);

            runOnUiThread(() -> {
                // Assign values to global lists

                originalOrderToInvoice.clear();
                originalOrderToInvoice.addAll(finalOrderList);
                orderToInvoice.clear();
                orderToInvoice.addAll(tempOrderList);

                extraorderToInvoice.clear();
                extraorderToInvoice.addAll(finalExtraList);

                // Assign final calculated values to global variables
                TOTALQTY = finalTotalQty;
                TOTALNET = finalTotalNet;
                TOTALVAT = finalTotalVat;
                TOTALGROSS = finalTotalGross;
                TOTALGROSSAFTERREBATE = finalTotalGrossAfterRebate;

                // Update UI elements
                Total_Qty.setText("Total Qty: " + TOTALQTY);
                Total_Net_amt.setText("Total Net Amount: " + TOTALNET.toPlainString());
                Total_vat_amt.setText("Total VAT Amount: " + TOTALVAT.toPlainString());
                Total_Amount_Payable.setText("Total Amount Payable: " + TOTALGROSS.toPlainString());

                // Update adapter
                ShowOrderForInvoiceAdapter adapter = new ShowOrderForInvoiceAdapter(this, orderToInvoice);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private ShowOrderForInvoiceBean createInvoiceItem(NewSaleBean saleBean) {
        ShowOrderForInvoiceBean item = new ShowOrderForInvoiceBean();
        item.setItemName(saleBean.getProductName());
        item.setItemCode(saleBean.getItemCode());
        item.setBarcode(saleBean.getBarcode());
        item.setItemid(saleBean.getProductID());
        item.setPlucode(saleBean.getPlucode());
        item.setDelqty(saleBean.getDeliveryQty());
        item.setSellingprice(saleBean.getSellingPrice());
        item.setUom(saleBean.getUom());
        item.setDisc("0.00");

        // Set VAT Percent as 5
        item.setVat_percent("5");

        // Calculate financial details
        if (saleBean.getDeliveryQty() == null || saleBean.getDeliveryQty().isEmpty()) {
            item.setDelqty("0");
            item.setNet("0.00");
            item.setVat_amt("0.00");
            item.setGross("0.00");
        } else {
            BigDecimal net = new BigDecimal(saleBean.getDeliveryQty())
                    .multiply(new BigDecimal(saleBean.getSellingPrice()))
                    .setScale(2, RoundingMode.HALF_UP);

            // VAT amount calculation (5% VAT)
            BigDecimal vatAmt = net.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal gross = net.add(vatAmt);

            item.setNet(net.toString());
            item.setVat_amt(vatAmt.toString());
            item.setGross(gross.toString());
        }

        return item;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!originalOrderToInvoice.isEmpty()) {
            orderToInvoice.clear();
            orderToInvoice.addAll(originalOrderToInvoice);
        }
        if (listView.getAdapter() != null) {
            ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
        }
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
                    intent.putExtra("invoiceNo", invoiceNo);
                    intent.putExtra("route", route);
                    intent.putExtra("vehiclenum", vehiclenum);
                    intent.putExtra("name", name);
                    intent.putExtra("vanid", vanID);
                    intent.putExtra("userid", userID);
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
                    intent.putExtra("orderid", orderid);
                    intent.putExtra("outletid", outletid);
                    intent.putExtra("invoiceNo", invoiceNo);
                    intent.putExtra("route", route);
                    intent.putExtra("vehiclenum", vehiclenum);
                    intent.putExtra("name", name);
                    intent.putExtra("vanid", vanID);
                    intent.putExtra("userid", userID);
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
        extraorderToInvoice.clear();
    }
}

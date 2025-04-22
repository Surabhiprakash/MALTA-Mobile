package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.LoadInActivity.finaltotal;
import static com.malta_mqf.malta_mobile.LoadInActivity.listagency;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;
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

import com.malta_mqf.malta_mobile.Adapter.ShowLoadinInvoiceAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ApprovedOrderDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.DataBase.TotalApprovedOrderBsdOnItem;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.ProductBean;
import com.malta_mqf.malta_mobile.Model.ShowLoadinInvoiceBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ShowLoadinInvoice extends AppCompatActivity {
    ListView listView;
    Button save;
    TextView agencyName, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    BigDecimal TOTALNET, TOTALVAT, TOTALGROSS;
    ItemsByAgencyDB itemsByAgencyDB;
    List<ShowLoadinInvoiceBean> loadin;
    ShowLoadinInvoiceAdapter showLoadinInvoiceAdapter;
    String agencyname;
    Toolbar toolbar;
    AllAgencyDetailsDB allAgencyDetailsDB;
    TotalApprovedOrderBsdOnItem totalApprovedOrderBsdOnItemDB;
    StockDB stockDB;
    HashSet<String> processedAgencies;
    HashSet<String> processedProductIds;
    UserDetailsDb userDetailsDb;
    ApprovedOrderDB approvedOrderDB;
    String expectedDelivery;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_loadin_invoice);
         processedAgencies = new HashSet<>();
         processedProductIds = new HashSet<>();
         loadin=new LinkedList<>();
        agencyname=getIntent().getStringExtra("agencyname");
        expectedDelivery=getIntent().getStringExtra("expectedDelivery");
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        allAgencyDetailsDB=new AllAgencyDetailsDB(this);
        userDetailsDb=new UserDetailsDb(this);
        approvedOrderDB=new ApprovedOrderDB(this);
        stockDB=new StockDB(this);
        totalApprovedOrderBsdOnItemDB=new TotalApprovedOrderBsdOnItem(this);
        listView=findViewById(R.id.listViewcredit);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("AGENCY INVOICE");
        save=findViewById(R.id.btn_save_print);
        save.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));

        agencyName=findViewById(R.id.tvCreditNoteid);
        Total_Qty=findViewById(R.id.tvTotalQty);
        Total_Net_amt=findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt=findViewById(R.id.tvTotalVatAmt);
        Total_Amount_Payable=findViewById(R.id.tvGrossAmount);
        agencyName.setText(agencyname);
        getLoadinInvoice();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEnterCodeOFTheDaySpinner();
            }
        });
    }

    public void getLoadinInvoice(){
        loadin.clear();
        totalQty = 0;
        TOTALNET = BigDecimal.ZERO;
        TOTALVAT = BigDecimal.ZERO;
        TOTALGROSS = BigDecimal.ZERO;

        for (int i=0;i<finaltotal.size();i++){
            ShowLoadinInvoiceBean showLoadinInvoiceBean=new ShowLoadinInvoiceBean();
            showLoadinInvoiceBean.setProductname(finaltotal.get(i).getProductName());
            if(finaltotal.get(i).getDeliveryQty()==null || finaltotal.get(i).getDeliveryQty().isEmpty()){
                showLoadinInvoiceBean.setAvailableQty(finaltotal.get(i).getApprovedqty());
            }else {
                showLoadinInvoiceBean.setAvailableQty(finaltotal.get(i).getDeliveryQty());
            }
            String productid=finaltotal.get(i).getProductId();
            Cursor cursor=itemsByAgencyDB.readProdcutDataByproductId(productid);
            if(cursor.getCount()!=0){
                if(cursor.moveToNext()){
                   @SuppressLint("Range") String itemcode=cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                showLoadinInvoiceBean.setProductcode(itemcode);
                }
            }
            showLoadinInvoiceBean.setPurchasePrice(finaltotal.get(i).getPurchase_price());
            showLoadinInvoiceBean.setDisc("0.0");
            BigDecimal NET=BigDecimal.ZERO;
            NET = BigDecimal.valueOf(Double.parseDouble(showLoadinInvoiceBean.getAvailableQty()) * Double.parseDouble(showLoadinInvoiceBean.getPurchasePrice())).setScale(2, RoundingMode.HALF_UP);
            showLoadinInvoiceBean.setItemNet(String.format("%.2f",NET));
            showLoadinInvoiceBean.setItemVatPercent("5");
            BigDecimal VAT_AMT = NET.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            showLoadinInvoiceBean.setItemVat(String.format("%.2f",VAT_AMT));
            BigDecimal GROSS = VAT_AMT.add( NET);
            showLoadinInvoiceBean.setItemGross(String.format("%.2f",GROSS));
            totalQty+= Integer.parseInt(showLoadinInvoiceBean.getAvailableQty());
            TOTALNET =TOTALNET.add( NET.setScale(2, RoundingMode.HALF_UP));
            TOTALVAT =TOTALVAT.add( VAT_AMT.setScale(2, RoundingMode.HALF_UP));
            TOTALGROSS =TOTALGROSS.add( GROSS.setScale(2, RoundingMode.HALF_UP));
            loadin.add(showLoadinInvoiceBean);
        }
        Total_Qty.setText("Total Qty: " + totalQty);
        Total_Net_amt.setText("Total Net Amount: " + String.format("%.2f", TOTALNET));
        Total_vat_amt.setText("Total VAT Amount: " + String.format("%.2f", TOTALVAT));
        Total_Amount_Payable.setText("Total Amount Payable: " + String.format("%.2f", TOTALGROSS));

        showLoadinInvoiceAdapter=new ShowLoadinInvoiceAdapter(this,loadin);
        listView.setAdapter(showLoadinInvoiceAdapter);
        showLoadinInvoiceAdapter.notifyDataSetChanged();
    }
    private void showEnterCodeOFTheDaySpinner() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ShowLoadinInvoice.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(ShowLoadinInvoice.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_code);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 100), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText remark = dialog.findViewById(R.id.dialog_enter_remarks);
        TextView mealService = dialog.findViewById(R.id.mealSerViceDia);
        Button ok = dialog.findViewById(R.id.dialog_ok_button);
        Button cancel = dialog.findViewById(R.id.dialog_cancel_button);
        mealService.setText("Enter Code of the Day");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = remark.getText().toString();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (ProductBean productBean : finaltotal) {
                    String productId = productBean.getProductId();
                    String productName = productBean.getProductName();
                    String quantity = productBean.getQuantity();
                    String approvedQty = productBean.getApprovedqty();
                    String delQty = productBean.getDeliveryQty();
                    String itemCode=productBean.getItemcode();
                    System.out.println("Product ID: " + productId);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Approved Quantity: " + approvedQty);
                    System.out.println("Delivery Quantity: " + delQty);

                    if (delQty == null || delQty.isEmpty()) {
                        totalApprovedOrderBsdOnItemDB.totalUpdateApprovedData2(vanID, productName, productId,itemCode, quantity, approvedQty, approvedQty, "LOADED", dateFormat.format(date),expectedDelivery);
                        Cursor cursor = approvedOrderDB.getMaxApprovedDateTime();
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            @SuppressLint("Range") String max_approved_date = cursor.getString(cursor.getColumnIndex("max_date"));
                            System.out.println("Max Date is: " + max_approved_date);
                            userDetailsDb.updateLastApprovedDate("1", max_approved_date);

                            cursor.close(); // Always close the cursor to avoid memory leaks.
                        }
                    } else {
                        totalApprovedOrderBsdOnItemDB.totalUpdateApprovedData2(vanID, productName, productId,itemCode, quantity, approvedQty, delQty, "LOADED", dateFormat.format(date),expectedDelivery);
                        Cursor cursor = approvedOrderDB.getMaxApprovedDateTime();
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            @SuppressLint("Range") String max_approved_date = cursor.getString(cursor.getColumnIndex("max_date"));
                            System.out.println("Max Date is: " + max_approved_date);
                            userDetailsDb.updateLastApprovedDate("1", max_approved_date);

                            cursor.close(); // Always close the cursor to avoid memory leaks.
                        }                    }
                }
                if (agencyname.equals("All")) {
                    Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);

                        // Move the cursor1 to the first row before accessing its data
                        if (!processedAgencies.contains(agencyCode)) {
                            // Add the agency code to the HashSet to mark it as processed
                            processedAgencies.add(agencyCode);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range")
                                String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                @SuppressLint("Range")
                                String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                if (!processedProductIds.contains(productID)) {
                                    // Add the product ID to the HashSet to mark it as processed
                                    processedProductIds.add(productID);
                                    Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonProductIDandStatus(productID,"LOADED");


                                    while (cursor2.moveToNext()) {
                                        @SuppressLint("Range") String prdid = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                        @SuppressLint("Range") String itemcode=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CODE));
                                       @SuppressLint("Range") String itemcategory=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CATEGORY));
                                       @SuppressLint("Range") String itemsubcategory=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_SUB_CATEGORY));
                                        @SuppressLint("Range")
                                        String prdname = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                        @SuppressLint("Range") int prdqty = cursor2.getInt(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_TOTAL_AVAILABLE_QTY));
                                        System.out.println(prdid + "-" + prdname + "-" + prdqty);

                                        // Check if the product exists in the stockDB
                                        Cursor cursor3 = stockDB.readonproductid(prdid);
                                        if (cursor3.getCount() == 0) {
                                            // Product does not exist, add it to stockDB
                                            stockDB.stockaddApprovedDetails(vanID, prdname, prdid,itemcode,itemcategory,itemsubcategory, prdqty,"STOCK NOT SYNCED");
                                            totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoadingWithExpectedDelivery(prdid,expectedDelivery);
                                          //  totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoading2(prdid,"inserted");
                                            totalApprovedOrderBsdOnItemDB.totaldeleteByStatusPRL();
                                        } else {
                                            // Product exists, update its details in stockDB
                                            stockDB.stockUpdateApprovedData(vanID, prdname, prdid,itemcode,itemcategory,itemsubcategory, prdqty,"STOCK NOT SYNCED");
                                            totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoadingWithExpectedDelivery(prdid,expectedDelivery);
                                          //  totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoading2(prdid,"inserted");
                                            totalApprovedOrderBsdOnItemDB.totaldeleteByStatusPRL();
                                        }
                                        cursor3.close(); // Close the cursor after use
                                    }
                                    cursor2.close();
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(ShowLoadinInvoice.this, LoadInventory.class);
                    startActivity(intent);
                    listagency.clear();
                    dialog.dismiss();
                } else {
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agencyname);
                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);

                        // Move the cursor1 to the first row before accessing its data
                        if (!processedAgencies.contains(agencyCode)) {
                            // Add the agency code to the HashSet to mark it as processed
                            processedAgencies.add(agencyCode);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range")
                                String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                @SuppressLint("Range")
                                String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                if (!processedProductIds.contains(productID)) {
                                    // Add the product ID to the HashSet to mark it as processed
                                    processedProductIds.add(productID);
                                    Cursor cursor2 = totalApprovedOrderBsdOnItemDB.readonProductIDandStatus(productID,"LOADED");


                                    while (cursor2.moveToNext()) {
                                        @SuppressLint("Range") String prdid = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTID));
                                        @SuppressLint("Range") String itemcode=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CODE));
                                        @SuppressLint("Range") String itemcategory=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_CATEGORY));
                                        @SuppressLint("Range") String itemsubcategory=cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_ITEM_SUB_CATEGORY));

                                        @SuppressLint("Range")
                                        String prdname = cursor2.getString(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_PRODUCTNAME));
                                        @SuppressLint("Range") int prdqty = cursor2.getInt(cursor2.getColumnIndex(TotalApprovedOrderBsdOnItem.COLUMN_CURRENT_TOTAL_AVAILABLE_QTY));
                                        System.out.println(prdid + "-" + prdname + "-" + prdqty);

                                        // Check if the product exists in the stockDB
                                        Cursor cursor3 = stockDB.readonproductid(prdid);
                                        if (cursor3.getCount() == 0) {
                                            // Product does not exist, add it to stockDB
                                            stockDB.stockaddApprovedDetails(vanID, prdname, prdid,itemcode,itemcategory,itemsubcategory, prdqty,"STOCK NOT SYNCED");
                                            totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoadingWithExpectedDelivery(prdid,expectedDelivery);
                                          //  totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoading2(prdid,"inserted");
                                            totalApprovedOrderBsdOnItemDB.totaldeleteByStatusPRL();
                                        } else {
                                            // Product exists, update its details in stockDB
                                            stockDB.stockUpdateApprovedData(vanID, prdname, prdid,itemcode,itemcategory,itemsubcategory, prdqty,"STOCK NOT SYNCED");
                                            totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoadingWithExpectedDelivery(prdid,expectedDelivery);
                                           // totalApprovedOrderBsdOnItemDB.updateProductStatusAfterLoading2(prdid,"inserted");
                                            totalApprovedOrderBsdOnItemDB.totaldeleteByStatusPRL();
                                        }
                                        cursor3.close(); // Close the cursor after use
                                    }
                                    cursor2.close();
                                }
                            }
                        }
                    }
                    cursor.close();
                    Intent intent = new Intent(ShowLoadinInvoice.this, LoadInventory.class);
                    startActivity(intent);
                    listagency.remove(agencyname);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
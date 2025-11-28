package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.malta_mqf.malta_mobile.Adapter.ReturnAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.ReturnItemsBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class CustomerReturn extends AppCompatActivity {
    public static String customername;
    Toolbar toolbar;
    SubmitOrderDB submitOrderDB;
    String outletid, customerCode, orderid, trn_no, customeraddress, outletName;
    ReturnAdapter returnAdapter;
    ListView returnlistview;
    ALodingDialog aLodingDialog;
    List<ReturnItemsBean> returnorderinvlist;
    ReturnDB returnDB;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_return);
        returnorderinvlist = new LinkedList<>();

        submitOrderDB = new SubmitOrderDB(this);
        returnDB = new ReturnDB(this);
        outletByIdDB = new OutletByIdDB(this);
        allCustomerDetailsDB = new AllCustomerDetailsDB(this);
        returnlistview = findViewById(R.id.listCustomerReturn);
        aLodingDialog = new ALodingDialog(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Return");
        /*outletid=getIntent().getStringExtra("outletId");
        customerCode=getIntent().getStringExtra("customerCode");
        customeraddress=getIntent().getStringExtra("customeraddress");
        customername = getIntent().getStringExtra("customerName");
        trn_no=getIntent().getStringExtra("trn_no");*/

        //    orderid=getIntent().getStringExtra("orderid");
        try {
            getOrderIDandInvoiceNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        returnlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();
                String status = returnorderinvlist.get(i).getStatus();

                String invNo = returnorderinvlist.get(i).getInvoice_no();
                String orderid = returnorderinvlist.get(i).getOrder_id();
                String customerCode = returnorderinvlist.get(i).getCustomercode();
                String customername = returnorderinvlist.get(i).getCustomername();
                String customeraddress = returnorderinvlist.get(i).getCustomeraddress();
                String outletid = returnorderinvlist.get(i).getOutletid();
                Intent j = new Intent(CustomerReturn.this, CustomerReturnDetailsBsdOnInvoice.class);
                j.putExtra("invoiceNo", invNo);
                j.putExtra("orderid", orderid);
                j.putExtra("outletId", outletid);
                j.putExtra("customerCode", customerCode);
                j.putExtra("customerName", customername);
                j.putExtra("customeraddress", customeraddress);
                startActivity(j);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        // Dismiss the ProgressDialog once the new activity is started
                        aLodingDialog.cancel();
                    }
                }, 200);

            }


        });
    }


    @SuppressLint("Range")
    private void getOrderIDandInvoiceNumber() {
        returnorderinvlist.clear(); // Clear the list before populating it

        Cursor cursor = null;
        try {
            cursor = submitOrderDB.readDataByStatus("DELIVERY DONE");

            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, "No Orders to return!!!!!", Toast.LENGTH_SHORT).show();
                return; // Exit early if no data is found
            }

            while (cursor.moveToNext()) {
                String orderId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                String invoiceNumber = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                String deliveryDate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));

                Cursor cursor1 = null;
                try {
                    cursor1 = returnDB.readoninvno(invoiceNumber);

                    if (cursor1 == null || cursor1.getCount() == 0) {
                        outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                        System.out.println("outletid: " + outletid);
                        Cursor cursor3 = null;
                        try {
                            cursor3 = outletByIdDB.readOutletByID(outletid);
                            if (cursor3 != null && cursor3.moveToFirst()) {
                                outletName = cursor3.getString(cursor3.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                customerCode = cursor3.getString(cursor3.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
                                System.out.println("customerCode1:" + customerCode);

                                System.out.println("outletname" + outletName);
                            }
                        } finally {
                            if (cursor3 != null) {
                                cursor3.close();
                            }
                        }
                        Cursor cursor2 = null;
                        try {
                            cursor2 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
                            if (cursor2 != null && cursor2.moveToFirst()) {
                                customername = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                                customeraddress = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
                                trn_no = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                            }
                        } finally {
                            if (cursor2 != null) {
                                cursor2.close();
                            }
                        }

                        addReturnItem(orderId, outletName, outletid, invoiceNumber, deliveryDate, customername, customerCode, customeraddress, trn_no, "N/A");
                    } else {
                        outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                        while (cursor1.moveToNext()) {

                            Cursor cursor3 = null;
                            try {
                                cursor3 = outletByIdDB.readOutletByID(outletid);
                                if (cursor3 != null && cursor3.moveToFirst()) {
                                    outletName = cursor3.getString(cursor3.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                    customerCode = cursor3.getString(cursor3.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CUSTOMER_CODE));
                                    System.out.println("customerCode2:" + customerCode);

                                    System.out.println("outletnameeeeee" + outletName);
                                }
                            } finally {
                                if (cursor3 != null) {
                                    cursor3.close();
                                }
                            }
                            Cursor cursor2 = null;
                            try {
                                cursor2 = allCustomerDetailsDB.getCustomerDetailsById(customerCode);
                                if (cursor2 != null && cursor2.moveToFirst()) {
                                    customername = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_CUSTOMER_NAME));
                                    customeraddress = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));
                                    trn_no = cursor2.getString(cursor2.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                                }
                            } finally {
                                if (cursor2 != null) {
                                    cursor2.close();
                                }
                            }
                            String status = cursor1.getString(cursor1.getColumnIndex(ReturnDB.COLUMN_STATUS));
                            addReturnItem(orderId, outletName, outletid, invoiceNumber, deliveryDate, customername, customerCode, customeraddress, trn_no, status);
                        }
                    }
                } finally {
                    if (cursor1 != null) {
                        cursor1.close();
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        Collections.sort(returnorderinvlist, new Comparator<ReturnItemsBean>() {
            @Override
            public int compare(ReturnItemsBean o1, ReturnItemsBean o2) {
                return o2.getInvoice_no().compareTo(o1.getInvoice_no()); // Descending Order
            }
        });
        // Initialize the adapter once with the full list after all items are added
        if (returnAdapter == null) {
            returnAdapter = new ReturnAdapter(this, returnorderinvlist);
            returnlistview.setAdapter(returnAdapter);
        } else {
            returnAdapter.notifyDataSetChanged();
        }
    }

    private void addReturnItem(String orderId, String outletName, String outletid, String invoiceNumber, String deliveryDate, String customername, String customerCode, String customeraddress, String trn, String status) {
        for (ReturnItemsBean bean : returnorderinvlist) {
            if (bean.getInvoice_no().equals(invoiceNumber)) {
                return; // Skip adding if this item already exists
            }
        }
        ReturnItemsBean returnItemsBean = new ReturnItemsBean();
        returnItemsBean.setOrder_id(orderId);
        returnItemsBean.setInvoice_no(invoiceNumber);
        returnItemsBean.setDate(deliveryDate);
        returnItemsBean.setStatus(status);
        returnItemsBean.setOutletname(outletName);
        returnItemsBean.setOutletid(outletid);
        returnItemsBean.setCustomername(customername);
        returnItemsBean.setCustomeraddress(customeraddress);
        returnItemsBean.setCustomercode(customerCode);
        returnItemsBean.setTrn(trn);
        returnorderinvlist.add(returnItemsBean);
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
        Intent intent = new Intent(CustomerReturn.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
        outletid = null;
        customerCode = null;
        returnorderinvlist.clear();
        clearAllSharedPreferences();

    }

    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReturnPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
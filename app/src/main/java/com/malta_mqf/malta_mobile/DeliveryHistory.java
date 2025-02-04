package com.malta_mqf.malta_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.malta_mqf.malta_mobile.Adapter.DeliveryhistoryAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.deliveryhistorybean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import org.spongycastle.cms.PasswordRecipientId;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DeliveryHistory extends AppCompatActivity {
    ListView listdeliveryHistory;
    DeliveryhistoryAdapter adapter;
    SubmitOrderDB submitOrderDB;
    Toolbar toolbar;
    String invoiceNo;
    ALodingDialog aLodingDialog;
    String  customerName,outletName,outletcode;
    ItemsByAgencyDB itemsByAgencyDB;
    OutletByIdDB outletByIdDB;
    List<deliveryhistorybean> listdeliveryhistory;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);
         listdeliveryhistory=new LinkedList<>();
        toolbar = findViewById(R.id.toolbar);
        aLodingDialog=new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DELIVERY History");
        listdeliveryHistory=findViewById(R.id.listDeliveryHistory);
        submitOrderDB=new SubmitOrderDB(this);
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        outletByIdDB=new OutletByIdDB(this);
        getOrdersDeliveredBasedOnStatus("DELIVERY DONE","REJECTED","DELIVERED","NEW ORDER DELIVERED","REJECTED SYNCED");

        listdeliveryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();

                String invOrOrderno=listdeliveryhistory.get(i).getInvoiceOrOrderID();
                String outletname=listdeliveryhistory.get(i).getOutletName();
                Intent intent=new Intent(DeliveryHistory.this,DeliveryHistoryDetails.class);
                intent.putExtra("invOrOrderno",invOrOrderno);
                intent.putExtra("outletname",outletname);
                intent.putExtra("outletcode",outletcode);
                startActivity(intent);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable,2000);
            }
        });
    }
    @SuppressLint("Range")
    private void getOrdersDeliveredBasedOnStatus(String status1, String status2,String status3,String status4,String status5) {
        Cursor cursor = submitOrderDB.getOrdersBasedOnDeliveryStatus(status1, status2,status3,status4,status5);
        if (cursor.getCount() == 0) {
            // Handle case where no orders are found
            // You can show a message or perform any other appropriate action
        } else {
            while (cursor.moveToNext()) {
                try {
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                    String deliveryDateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
                    String status=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                    String outletid=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));


                    Cursor cursor2=outletByIdDB.readOutletByID(outletid);
                    if(cursor2.getCount()!=0){
                        while (cursor2.moveToNext()){
                            outletName=cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                            outletcode=cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                        }
                    }
                  //  Intent intent = new Intent(NewSaleInvoice.this, NewSaleReceiptDemo.class);
                    /*intent.putExtra("referenceNo", refrenceno);
                    intent.putExtra("comments",Comments);
                    intent.putExtra("outletname",outletName);
                    startActivity(intent*/
                    String customerCode=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
                    System.out.println("Customercdde os"+customerCode);
                    Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCodes(customerCode);
                    if (cursor1.getCount() != 0) {
                        while (cursor1.moveToNext()) {
                          customerName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_NAME));
                          System.out.println("CustomerName in this is" + customerName);

                        }

                        }

                    if (invoiceNo == null) {
                        invoiceNo = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    }

                    deliveryhistorybean deliveryHistoryBean = new deliveryhistorybean();
                    deliveryHistoryBean.setInvoiceOrOrderID(invoiceNo);
                    deliveryHistoryBean.setDatetime(deliveryDateTime);
                    deliveryHistoryBean.setStatus(status);
                    deliveryHistoryBean.setCustomer(customerName);
                    deliveryHistoryBean.setOutletName(outletName);
                    deliveryHistoryBean.setOutletcode(outletcode);
                    listdeliveryhistory.add(deliveryHistoryBean);
                } catch (Exception e) {
                    // Handle any exceptions that may occur during data retrieval
                    e.printStackTrace();
                }
            }
            cursor.close();
            // Set up the adapter and populate the ListView outside the loop for efficiency
            Collections.sort(listdeliveryhistory);
            adapter = new DeliveryhistoryAdapter(DeliveryHistory.this, listdeliveryhistory);
            listdeliveryHistory.setAdapter(adapter);
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

        invoiceNo = null;
        customerName = null;
        listdeliveryhistory.clear();
        Intent intent = new Intent(DeliveryHistory.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
    }
}
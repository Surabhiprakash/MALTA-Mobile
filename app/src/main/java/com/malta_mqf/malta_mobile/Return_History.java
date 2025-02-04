package com.malta_mqf.malta_mobile;
import java.util.Collections;
import java.util.List;
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

import com.malta_mqf.malta_mobile.Adapter.ReturnHistoryAdapter;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;

import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.Model.ReturnHistoryBean;

import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import java.util.LinkedList;

public class Return_History extends AppCompatActivity {
    ListView listViewReturnHistory;
    ReturnHistoryAdapter adapter;
    ReturnDB returnDB;
    Toolbar toolbar;
    String invoiceNo;
    ALodingDialog aLodingDialog;
    String  customerName,outletName;
    ItemsByAgencyDB itemsByAgencyDB;
    OutletByIdDB outletByIdDB;
    List<ReturnHistoryBean> listReturnHistory=new LinkedList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history);
        toolbar = findViewById(R.id.toolbar);
        aLodingDialog=new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RETURN HISTORY");
        listViewReturnHistory=findViewById(R.id.listDeliveryHistory);
        returnDB=new ReturnDB(this);
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        outletByIdDB=new OutletByIdDB(this);
        getOrdersReturnedBasedOnStatus("RETURNED","RETURNED NO INVOICE","RETURN DONE");

        listViewReturnHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();

                String invOrOrderno=listReturnHistory.get(i).getInvoiceOrOrderID();
                String creditid=listReturnHistory.get(i).getCreditNoteID();
                String outletNameee=listReturnHistory.get(i).getOutletName();
                Intent intent=new Intent(Return_History.this, ReturnHistoryDetails.class);
                intent.putExtra("invOrOrderno",invOrOrderno);
                intent.putExtra("outletname",outletNameee);
                intent.putExtra("creditid",creditid);
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
    private void getOrdersReturnedBasedOnStatus(String status1, String status2, String status3) {
        Cursor cursor = returnDB.getOrdersBasedOnReturnStatus(status1, status2,status3);
        if (cursor.getCount() == 0) {
            // Handle case where no orders are found
            // You can show a message or perform any other appropriate action
        } else {
            while (cursor.moveToNext()) {
                try {
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_INVOICE_NO));
                    String deliveryDateTime = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
                    String status=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_STATUS));
                    String outletid=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_OUTLETID));
                    String creditid=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CREDIT_NOTE));

                    Cursor cursor2=outletByIdDB.readOutletByID(outletid);
                    if(cursor2.getCount()!=0){
                        while (cursor2.moveToNext()){
                            outletName=cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        }
                    }
                    //  Intent intent = new Intent(NewSaleInvoice.this, NewSaleReceiptDemo.class);
                    /*intent.putExtra("referenceNo", refrenceno);
                    intent.putExtra("comments",Comments);
                    intent.putExtra("outletname",outletName);
                    startActivity(intent*/
                    String customerCode=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CUSTOMER_CODE));
                    System.out.println("Customercdde os"+customerCode);
                    Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCodes(customerCode);
                    if (cursor1.getCount() != 0) {
                        while (cursor1.moveToNext()) {
                            customerName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_NAME));
                            System.out.println("CustomerName in this is" + customerName);

                        }

                    }

                    if (invoiceNo == null) {
                        invoiceNo = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CREDIT_NOTE));
                    }

                    ReturnHistoryBean returnHistoryBean = new ReturnHistoryBean();
                    returnHistoryBean.setInvoiceOrOrderID(invoiceNo);
                    returnHistoryBean.setDatetime(deliveryDateTime);
                    returnHistoryBean.setStatus(status);
                    returnHistoryBean.setCustomer(customerName);
                    returnHistoryBean.setOutletName(outletName);
                    returnHistoryBean.setCreditNoteID(creditid);
                    listReturnHistory.add(returnHistoryBean);
                } catch (Exception e) {
                    // Handle any exceptions that may occur during data retrieval
                    e.printStackTrace();
                }
            }
            // Set up the adapter and populate the ListView outside the loop for efficiency
            Collections.sort(listReturnHistory);
            adapter = new ReturnHistoryAdapter(Return_History.this, listReturnHistory);
            listViewReturnHistory.setAdapter(adapter);
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
        listReturnHistory.clear();
        Intent intent = new Intent(Return_History.this, StartDeliveryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();  // Finish TodaysDelivery activity
    }

}
package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditbeanList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Toast;


import com.google.gson.Gson;
import com.malta_mqf.malta_mobile.Adapter.DeliveryHistoryDetailsAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity;
import com.malta_mqf.malta_mobile.SewooPrinter.ReturnHistoryBluetoothActivity;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo2;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReturnHistoryReceiptDemo;

import java.util.LinkedList;
import java.util.List;

public class ReturnHistoryDetails extends AppCompatActivity {
    ReturnDB returnDB;
    public   static String invNoOrOrderId;
    ItemsByAgencyDB itemsByAgencyDB;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    Toolbar toolbar;
    ListView listView;
    private String customer_code;
    DeliveryHistoryDetailsAdapter deliveryHistoryDetailsAdapter;
    public static String outletname, customername, totalqty, totalnet, totalvat, toaltamountpayable,returnTrn,route,creditIdNo;
    TextView outletName, CustomerName, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    EditText etreference, etcomments;
    Button reprintInvoice;
    UserDetailsDb userDetailsDb;

    public static String reference,comments;
    public static List<DeliveryHistoryDeatilsBean> returnHistoryDetailsList = new LinkedList<>();

    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history_details);
        returnDB = new ReturnDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allCustomerDetailsDB=new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);

        userDetailsDb=new UserDetailsDb(this);
        invNoOrOrderId = getIntent().getStringExtra("invOrOrderno");
        outletname=getIntent().getStringExtra("outletname");
        creditIdNo=getIntent().getStringExtra("creditid");

        System.out.println("invOrOrderno: "+invNoOrOrderId);
        System.out.println("outletname: "+outletname);
        System.out.println("creditid: "+creditIdNo);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(creditIdNo);

        outletName = findViewById(R.id.tvOutletName);
        CustomerName = findViewById(R.id.tvCustomerName);
        Total_Qty = findViewById(R.id.tvTotalQty);
        Total_Net_amt = findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt = findViewById(R.id.tvTotalVatAmt);
        Total_Amount_Payable = findViewById(R.id.tvGrossAmount);

        etreference=findViewById(R.id.etRefNo);
        etcomments=findViewById(R.id.etComment);
        listView = findViewById(R.id.listView);
        reprintInvoice=findViewById(R.id.btn_reprint_invoice);
        outletName.setText(outletname);

        getOrdersReturnedBasedOninvOrOrderno(creditIdNo);


        Cursor cursor=userDetailsDb.readAllData();
        while (cursor.moveToNext()){
            route=cursor.getString(cursor.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
        }

        reprintInvoice.setOnClickListener(v -> {
            reference=etreference.getText().toString();
            comments=etcomments.getText().toString();
            showAvailablePrinters();
        });
    }

    private void showAvailablePrinters() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ReturnHistoryDetails.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(ReturnHistoryDetails.this);
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
                String outletAddress="";
                String emirate="";
                String outletcode="";
                String customeraddress="";
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customer_code);
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
                        outletcode=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                    }
                }
                Intent intent = new Intent(ReturnHistoryDetails.this, ReturnHistoryBluetoothActivity.class);
                intent.putExtra("outletname",outletname);
                intent.putExtra("outletAddress",outletAddress);
                intent.putExtra("outletCode",outletcode);
                intent.putExtra("emirate",emirate);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customerCode",customer_code);
                intent.putExtra("reference",reference);
                intent.putExtra("comments",comments);
                intent.putExtra("returnTrn",returnTrn);
                intent.putExtra("creditIdNo",creditIdNo);
                intent.putExtra("customername",customername);
                intent.putExtra("deliveryHistoryDetailsList",new Gson().toJson(returnHistoryDetailsList));

                startActivity(intent);
                dialog.dismiss();
            }

        } );

        zeb_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                String outletAddress="";
                String emirate="";
                String outletcode="";
                String customeraddress="";
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customer_code);
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
                        outletcode=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));

                    }
                }

                Intent intent = new Intent(ReturnHistoryDetails.this, ReturnHistoryReceiptDemo.class);
                intent.putExtra("outletname",outletname);
                intent.putExtra("outletCode",outletcode);
                intent.putExtra("outletAddress",outletAddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customername);
                intent.putExtra("customerCode",customer_code);
                intent.putExtra("creditIdNo",creditIdNo);
                startActivity(intent);
                dialog.dismiss();


            }
        });
        dialog.show();
    }


    @SuppressLint("Range")
    private void getOrdersReturnedBasedOninvOrOrderno(String creditIdNo) {
        returnHistoryDetailsList.clear();
        Cursor cursor = returnDB.getOrdersBasedOnCreditOrderId(creditIdNo);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No details found for this order or invoice no", Toast.LENGTH_SHORT).show();
            return; // Exit the method early if no orders are found
        }

        while (cursor.moveToNext()) {
            String orderid = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ORDERID));
            String invoiceno = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_INVOICE_NO));
            String outletid = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_OUTLETID));
            String refrenceno=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_REFERENCE_NO));
            String Comments=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_COMMENTS));
            String dateTime=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
            if(refrenceno==null){
                etreference.setText("N/A");
            }else{
                etreference.setText(refrenceno.toString().trim());
            }if(Comments==null){
                etcomments.setText("N/A");
            }else{
                etcomments.setText(Comments.toString().trim());
            }
            customer_code = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CUSTOMER_CODE));
            System.out.println("customnercode"+customer_code);
            String itemCode = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_ITEMCODE));
            String price=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_PRICE));
            String delqty = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_RETURN_QTY));
            String rebate = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DISC));
            String NET = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_NET));
            String VATpercentage = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VAT_PERCENT));
            String VAT_AMT = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_VAT_AMT));
            String GROSS = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_GROSS));
            totalqty = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_QTY_OF_OUTLET));
            totalnet = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_NET_AMOUNT));
            totalvat = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_VAT_AMOUNT));
            toaltamountpayable = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT));
            String[] itemcodes = itemCode.split(",");
            String[] delqtys;
            String[] rebates, NETS, VATpercentages, VAT_amt, grosss,prices;


            if (rebate != null) {
                rebates = rebate.split(",");
            } else {
                rebates = new String[0];
            }


            if (NET != null) {
                NETS = NET.split(",");
            } else {
                NETS = new String[0];
            }
            if (VATpercentage != null) {
                VATpercentages = VATpercentage.split(",");
            } else {
                VATpercentages = new String[0];
            }
            if (VAT_AMT != null) {
                VAT_amt = VAT_AMT.split(",");
            } else {
                VAT_amt = new String[0];
            }
            if (GROSS != null) {
                grosss = GROSS.split(",");
            } else {
                grosss = new String[0];
            }


            if (delqty != null) {
                delqtys = delqty.split(",");
            } else {
                delqtys = new String[0]; // Initialize delqtys as an empty array if delqty is null
            }

            if(price!=null){
                prices=price.split(",");
            }else{
                prices=new String[0];
            }
            for (int i = 0; i < itemcodes.length; i++) {
                System.out.println("cODES"+itemcodes[i]);
                Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(customer_code, itemcodes[i]);
                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {
                        String itemName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        System.out.println("itemName"+itemName);
                        customername = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_NAME));
                        System.out.println("customnername"+customername);
                       // String priceperqty = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                        String itemBarcode=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));

                        String plucode=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                        String uom=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                        DeliveryHistoryDeatilsBean deliveryHistoryDeatilsBean = new DeliveryHistoryDeatilsBean();
                        deliveryHistoryDeatilsBean.setItemname(itemName);
                        deliveryHistoryDeatilsBean.setItemCode(itemcodes[i]);
                        deliveryHistoryDeatilsBean.setPrice(prices[i]);
                        deliveryHistoryDeatilsBean.setBarcode(itemBarcode);
                        deliveryHistoryDeatilsBean.setPlucode(plucode);
                        deliveryHistoryDeatilsBean.setUom(uom);
                        deliveryHistoryDeatilsBean.setDateTime(dateTime);
                        if (i < delqtys.length && delqtys[i] != null) {
                            deliveryHistoryDeatilsBean.setDelqty(delqtys[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setDelqty("N/A");
                        }

                        if(i < rebates.length && rebates[i] != null){
                            deliveryHistoryDeatilsBean.setDisc(rebates[i]);
                        }else{
                            deliveryHistoryDeatilsBean.setDisc("N/A");
                        }

                        if (i < NETS.length && NETS[i] != null) {
                            deliveryHistoryDeatilsBean.setNet(NETS[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setNet("N/A");
                        }
                        if (i < VATpercentages.length && VATpercentages[i] != null) {
                            deliveryHistoryDeatilsBean.setVatpencet(VATpercentages[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setVatpencet("N/A");
                        }
                        if (i < VAT_amt.length && VAT_amt[i] != null) {
                            deliveryHistoryDeatilsBean.setVat(VAT_amt[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setVat("N/A");
                        }
                        if (i < grosss.length && grosss[i] != null) {
                            deliveryHistoryDeatilsBean.setGross(grosss[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setGross("N/A");
                        }

                        returnHistoryDetailsList.add(deliveryHistoryDeatilsBean);
                        deliveryHistoryDetailsAdapter = new DeliveryHistoryDetailsAdapter(ReturnHistoryDetails.this, returnHistoryDetailsList);
                        listView.setAdapter(deliveryHistoryDetailsAdapter);
                    }
                }

                Cursor cursor3= allCustomerDetailsDB.readDataByName(customername);
                while(cursor3.moveToNext()){
                    returnTrn=cursor3.getString(cursor3.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                }
            }
        }

        // Set outlet name, customer name, and total amounts
        outletName.setText(outletname);
        CustomerName.setText(customername);
        System.out.println("Total qty in return history "+totalqty);
        Total_Qty.setText(totalqty != null ? "Total Qty: " + totalqty : "Total Qty: N/A");
        Total_Net_amt.setText(totalnet != null ? "Total Net Amount: " + totalnet : "Total Net Amount: N/A");
        Total_vat_amt.setText(totalvat != null ? "Total VAT Amount: " + totalvat : "Total VAT Amount: N/A");
        Total_Amount_Payable.setText(toaltamountpayable != null ? "Gross Amount Payable: " + toaltamountpayable : "Gross Amount Payable: N/A");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            //deliveryHistoryDetailsList.clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnHistoryDetailsList.clear();
        invNoOrOrderId=null;
        outletname=null;
        customername=null;
        totalqty=null;
        totalnet=null;
        totalvat=null;
        toaltamountpayable=null;
        returnTrn=null;
        reference=null;
        comments=null;


    }
}
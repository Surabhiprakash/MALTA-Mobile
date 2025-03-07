package com.malta_mqf.malta_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.DeliveryHistoryDetailsAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllCustomerDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.DeliveryHistoryDeatilsBean;
import com.malta_mqf.malta_mobile.Model.InvoiceDetailsByIdResponse;
import com.malta_mqf.malta_mobile.Model.InvoiceDetailsByInvoiceNumber;
import com.malta_mqf.malta_mobile.SewooPrinter.DeliveryHistoryBluetooth_Activity;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryHistoryDetails extends BaseActivity {
    SubmitOrderDB submitOrderDB;
  public   static String invNoOrOrderId,route,vehiclenum,name;
    ItemsByAgencyDB itemsByAgencyDB;
    AllCustomerDetailsDB allCustomerDetailsDB;
    OutletByIdDB outletByIdDB;
    Toolbar toolbar;
   // String customerName;
    ListView listView;
    private String customer_code;
    DeliveryHistoryDetailsAdapter deliveryHistoryDetailsAdapter;
    public static String outletname,outletcode,barcode, customername, totalqty, totalnet, totalvat, toaltamountpayable,returnTrn;
    TextView outletName, CustomerName, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    EditText etreference, etcomments;
    Button reprintInvoice;
    UserDetailsDb userDetailsDb;
  public static String reference,comments;
   public static List<DeliveryHistoryDeatilsBean>  deliveryHistoryDetailsList = new LinkedList<>();

    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history_details);
     //   deliveryHistoryDetailsList = new LinkedList<>();

        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allCustomerDetailsDB=new AllCustomerDetailsDB(this);
        outletByIdDB = new OutletByIdDB(this);
        userDetailsDb=new UserDetailsDb(this);

        invNoOrOrderId = getIntent().getStringExtra("invOrOrderno");
        outletname=getIntent().getStringExtra("outletname");
        outletcode=getIntent().getStringExtra("outletCode");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(invNoOrOrderId);
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
        System.out.println("invoice number: "+ invNoOrOrderId);
        if (isOnline()) {
            getOnlineDeliveryHistoryDetailsByInvoiceNo(invNoOrOrderId);
        } else {
            getOrdersDeliveredBasedOninvOrOrderno(invNoOrOrderId);
        }

        Cursor cursor2=userDetailsDb.readAllData();
        while (cursor2.moveToNext()) {
            route = cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_ROUTE));
            vehiclenum=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_VEHICLE_NUM));
            name=cursor2.getString(cursor2.getColumnIndex(UserDetailsDb.COLUMN_NAME));
        }
        cursor2.close();
        reprintInvoice.setOnClickListener(v -> {
            reference=etreference.getText().toString();
            comments=etcomments.getText().toString();
            showAvailablePrinters();
        });
    }

    private void showAvailablePrinters() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) DeliveryHistoryDetails.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(DeliveryHistoryDetails.this);
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
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customer_code);
                if(cursor1.getCount()!=0){
                    while (cursor1.moveToNext()) {
                        customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));

                    }
                }
                Cursor cursor=outletByIdDB.readOutletByOutletCode(outletcode);
                if(cursor.getCount()!=0) {
                    while (cursor.moveToNext()) {
                        outletAddress =  cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));

                    }
                }
                cursor.close();
                cursor1.close();
                Intent intent = new Intent(DeliveryHistoryDetails.this, DeliveryHistoryBluetooth_Activity.class);
                intent.putExtra("outletname",outletname);
                intent.putExtra("customerCode",customer_code);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("address",outletAddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("customername",customername);
                System.out.println("customername in the delivery history detail:"+customername);
                intent.putExtra("vehiclenum",vehiclenum);
                intent.putExtra("name",name);
                intent.putExtra("deliveryHistoryDetailsList",new Gson().toJson(deliveryHistoryDetailsList));

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
                String customeraddress="";
                System.out.println("customer code in the zeb printer"+ customer_code);
                Cursor cursor1=allCustomerDetailsDB.getCustomerDetailsById(customer_code);
                System.out.println(cursor1.getCount());
                if(cursor1.getCount()!=0){
                    while (cursor1.moveToNext()) {
                        customeraddress = cursor1.getString(cursor1.getColumnIndex(AllCustomerDetailsDB.COLUMN_ADDRESS));

                    }
                }
                System.out.println("outlet code in zeb printer is :" +" "+outletcode);
                Cursor cursor=outletByIdDB.readOutletByOutletCode(outletcode);
                if(cursor.getCount()!=0) {
                    while (cursor.moveToNext()) {
                        outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));

                    }
                }
                cursor.close();
                cursor1.close();
                Intent intent = new Intent(DeliveryHistoryDetails.this, ReceiptDemo2.class);
                intent.putExtra("outletname",outletname);
                intent.putExtra("customerCode",customer_code);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customername);
                System.out.println("customername in the delivery history detail:"+customername);
                intent.putExtra("address",outletAddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("vehiclenum",vehiclenum);
                intent.putExtra("name",name);
                startActivity(intent);
                dialog.dismiss();


            }
        });
        dialog.show();
    }

    @SuppressLint("Range")
    private void getOrdersDeliveredBasedOninvOrOrderno(String invoiceNo) {
        deliveryHistoryDetailsList.clear();
        Cursor cursor = submitOrderDB.getOrdersBasedOnInvNoOrOrderId(invoiceNo);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No details found for this order or invoice no", Toast.LENGTH_SHORT).show();
            return; // Exit the method early if no orders are found
        }

        while (cursor.moveToNext()) {
            String orderid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
            String invoiceno = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
            String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
            String refrenceno=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REFERENCE_NO));
            String comments=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_COMMENTS));
            String dateTime=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
            if(refrenceno==null){
                etreference.setText("N/A");
            }else{
                etreference.setText(refrenceno.toString().trim());
            }if(comments==null){
                etcomments.setText("N/A");
            }else{
                etcomments.setText(comments.toString().trim());
            }
            customer_code = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
            System.out.println("customnercode"+customer_code);
            String itemCode = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ITEMCODE));
            String extra_itemcode=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEMCODE));
            if(extra_itemcode==null){
                extra_itemcode="";
            }
            String delqty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_QTY));
            if(delqty==null){
                String appqty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));
                delqty=appqty;
            }
            String extra_delqty=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_QTY));
            if(extra_delqty==null){
                extra_delqty="";
            }
            String sellingprice=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_SELLING_PRICE));
            String extra_priceperqty=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_ITEM_SELLING_PRICE));
            if(extra_priceperqty==null){
                extra_priceperqty="";
            }
            String rebate = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DISC));
            String extra_rebate=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_DISC));
            if(extra_rebate==null){
                extra_rebate="";
            }
            String NET = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_NET));
            String extra_net=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_NET));
            if(extra_net==null){
                extra_net="";
            }
            String VATpercentage = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_PERCENT));
            String extra_VAT_percentage=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_VAT_PERCENT));
            if(extra_VAT_percentage==null){
                extra_VAT_percentage="";
            }
            String VAT_AMT = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_VAT_AMT));
            String extra_vat_amt=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_VAT_AMT));
            System.out.println("extra vat amt: "+extra_vat_amt);
            if(extra_vat_amt==null){
                extra_vat_amt="";
            }
            String GROSS = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_GROSS));
            String extra_gross=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_EXTRA_GROSS));
            if(extra_gross==null){
                extra_gross="";
            }
            totalqty = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_QTY_OF_OUTLET));
            totalnet = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_NET_AMOUNT));
            totalvat = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_VAT_AMOUNT));
            toaltamountpayable = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT));
            String[] itemcodes = itemCode.split(",");
            String[] extra_itemcodes = extra_itemcode.split(",");
            System.out.println("itemcodes: "+itemcodes.toString());
            String[] delqtys;
            String[] rebates, NETS, VATpercentages, VAT_amt, grosss,sellingPrice,extra_delqtys,extra_priceperqtys,extra_rebates,extra_nets,extra_VAT_percentages,extra_vat_amts,extra_grosss;


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

            if(sellingprice !=null){
                sellingPrice=sellingprice.split(",");
            }else{
                sellingPrice=new String[0];
            }

            if (extra_rebate != null) {
                extra_rebates = extra_rebate.split(",");
            } else {
                extra_rebates = new String[0];
            }

            if (extra_net != null) {
                extra_nets = extra_net.split(",");
            } else {
                extra_nets = new String[0];
            }
            if (extra_VAT_percentage != null) {
                extra_VAT_percentages = VATpercentage.split(",");
            } else {
                extra_VAT_percentages = new String[0];
            }

            if (extra_vat_amt != null) {
                extra_vat_amts = extra_vat_amt.split(",");
            } else {
                extra_vat_amts = new String[0];
            }


            if (extra_gross != null) {
                extra_grosss = extra_gross.split(",");
            } else {
                extra_grosss = new String[0];
            }
            if (extra_delqty != null) {
                extra_delqtys = extra_delqty.split(",");
            } else {
                extra_delqtys = new String[0]; // Initialize delqtys as an empty array if delqty is null
            }
            if (extra_priceperqty != null) {
                extra_priceperqtys = extra_priceperqty.split(",");
            } else {
                extra_priceperqtys = new String[0];
                // Initialize sellingPrices as an empty array if priceperqty is null
            }
            for (int i = 0; i < itemcodes.length; i++) {
                System.out.println("CODES"+itemcodes[i]);
                System.out.println("customer_code: "+customer_code.toLowerCase(Locale.ROOT));
                Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(customer_code.toLowerCase(), itemcodes[i]);
                if (cursor1.getCount() != 0) {
                    System.out.println("heyeyeyeyeyeye");
                    while (cursor1.moveToNext()) {
                        String itemName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        System.out.println("itemName"+itemName);
                        customername = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_NAME));
                        System.out.println("customername"+customername);
                        //  String priceperqty = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_SELLING_PRICE));
                        String itemBarcode=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                        String plucode=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                        String uom=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                        DeliveryHistoryDeatilsBean deliveryHistoryDeatilsBean = new DeliveryHistoryDeatilsBean();
                        deliveryHistoryDeatilsBean.setItemname(itemName);
                        deliveryHistoryDeatilsBean.setItemCode(itemcodes[i]);
                        deliveryHistoryDeatilsBean.setPrice(sellingPrice[i]);
                        deliveryHistoryDeatilsBean.setBarcode(itemBarcode);
                        deliveryHistoryDeatilsBean.setPlucode(plucode);
                        deliveryHistoryDeatilsBean.setUom(uom);
                        deliveryHistoryDeatilsBean.setDeliveryDateTime(dateTime);
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

                        deliveryHistoryDetailsList.add(deliveryHistoryDeatilsBean);

                    }
                }


           /* Cursor cursor2 = outletByIdDB.readOutletByID(outletid);

            if (cursor2.getCount() != 0) {

                while (cursor2.moveToNext()) {

                    outletname = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));

                }
            }*/
                System.out.println("customernameeeeee : "+customername);
                Cursor cursor3= allCustomerDetailsDB.readDataByCustomerCode(customer_code);
                while(cursor3.moveToNext()){
                    returnTrn=cursor3.getString(cursor3.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                }
                cursor1.close();
                cursor3.close();
            }

            for (int i = 0; i < extra_itemcodes.length; i++) {
                System.out.println("cODES" + extra_itemcodes[i]);
                Cursor cursor1 = itemsByAgencyDB.readDataByCustomerCode(customer_code, extra_itemcodes[i]);
                if (cursor1.getCount() != 0) {
                    while (cursor1.moveToNext()) {
                        String itemName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        System.out.println("itemName" + itemName);
                        customername = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_NAME));
                        System.out.println("customnername is" + customername);
                        String itemBarcode = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_BARCODE));
                        String plucode=cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_PLUCODE));
                        String uom = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_UOM));
                        DeliveryHistoryDeatilsBean deliveryHistoryDeatilsBean = new DeliveryHistoryDeatilsBean();
                        deliveryHistoryDeatilsBean.setItemname(itemName);
                        deliveryHistoryDeatilsBean.setItemCode(extra_itemcodes[i]);
                        deliveryHistoryDeatilsBean.setPrice(extra_priceperqtys[i]);
                        deliveryHistoryDeatilsBean.setBarcode(itemBarcode);
                        deliveryHistoryDeatilsBean.setPlucode(plucode);
                        deliveryHistoryDeatilsBean.setDeliveryDateTime(dateTime);
                        deliveryHistoryDeatilsBean.setUom(uom);
                        if (i < extra_delqtys.length && extra_delqtys[i] != null) {
                            deliveryHistoryDeatilsBean.setDelqty(extra_delqtys[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setDelqty("N/A");
                        }

                        if (i < extra_rebates.length && extra_rebates[i] != null) {
                            deliveryHistoryDeatilsBean.setDisc(extra_rebates[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setDisc("N/A");
                        }

                        if (i < extra_nets.length && extra_nets[i] != null) {
                            deliveryHistoryDeatilsBean.setNet(extra_nets[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setNet("N/A");
                        }
                        if (i < extra_VAT_percentages.length && extra_VAT_percentages[i] != null) {
                            deliveryHistoryDeatilsBean.setVatpencet(extra_VAT_percentages[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setVatpencet("5");
                        }
                        if (i < extra_vat_amts.length && extra_vat_amts[i] != null) {
                            deliveryHistoryDeatilsBean.setVat(extra_vat_amts[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setVat("N/A");
                        }
                        if (i < extra_grosss.length && extra_grosss[i] != null) {
                            deliveryHistoryDeatilsBean.setGross(extra_grosss[i]);
                        } else {
                            deliveryHistoryDeatilsBean.setGross("N/A");
                        }

                        deliveryHistoryDetailsList.add(deliveryHistoryDeatilsBean);

                    }
                }


            }

            deliveryHistoryDetailsAdapter = new DeliveryHistoryDetailsAdapter(DeliveryHistoryDetails.this, deliveryHistoryDetailsList);
            listView.setAdapter(deliveryHistoryDetailsAdapter);
        }
        cursor.close();
        // Set outlet name, customer name, and total amounts
        outletName.setText(outletname);
        CustomerName.setText(customername);
        System.out.println("customercode in the deliveryhistorydetails is : "+CustomerName);
        Total_Qty.setText(totalqty != null ? "Total Qty: " + totalqty : "Total Qty: N/A");
        Total_Net_amt.setText(totalnet != null ? "Total Net Amount: " + totalnet : "Total Net Amount: N/A");
        Total_vat_amt.setText(totalvat != null ? "Total VAT Amount: " + totalvat : "Total VAT Amount: N/A");
        Total_Amount_Payable.setText(toaltamountpayable != null ? "Gross Amount Payable: " + toaltamountpayable : "Gross Amount Payable: N/A");
    }


    private void getOnlineDeliveryHistoryDetailsByInvoiceNo(String invoiceNumber) {
        deliveryHistoryDetailsList.clear();
        String url = ApiLinks.getInvoiceDetailsByInvoiceNo + "?invoiceno=" + invoiceNumber;
        System.out.println("url: " + url);

        // Asynchronous call to fetch data
        Call<InvoiceDetailsByIdResponse> call = apiInterface.getInvoiceDetails(url);
        call.enqueue(new Callback<InvoiceDetailsByIdResponse>() {

            @SuppressLint("Range")
            @Override
            public void onResponse(Call<InvoiceDetailsByIdResponse> call, Response<InvoiceDetailsByIdResponse> response) {
                System.out.println("I am in response");

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("yes")) {

                    InvoiceDetailsByIdResponse allOrderDetailsResponse = response.body();
                    System.out.println(allOrderDetailsResponse.toString());
                    List<InvoiceDetailsByInvoiceNumber> allDelivery = allOrderDetailsResponse.getIndividualPoDetails();

                    // Prepare variables
                    String totalNet = allOrderDetailsResponse.getTotalnetamount();
                    String totalVat = allOrderDetailsResponse.getTotalvatamount();
                    String totalGrossWithoutRebate = allOrderDetailsResponse.getInvoicewithoutrebate();
                    String outletname = allOrderDetailsResponse.getOutletName();
                    customername = allOrderDetailsResponse.getCustomerName();
                    System.out.println("customer name in deliveryhistory details in online method :"+customername);
                    String refrenceno = allOrderDetailsResponse.getRefno();
                    String Comments = allOrderDetailsResponse.getComments();

                    System.out.println("outletcode is :"+outletcode);
                    int returnTotalQty = 0;

                    Map<String, String> productNameMap = new HashMap<>();
                    Map<String, String> productUomMap = new HashMap<>();

                    // Batch fetch data for each item in IndividualPoDetails
                    for (InvoiceDetailsByInvoiceNumber deliveryInfo : allDelivery) {
                        String itemCode = deliveryInfo.getItemCode();
                        String itemName = deliveryInfo.getItemName();
                        outletcode=deliveryInfo.getOutletCode();
                       // barcode=deliveryInfo.getBarcode();

                        productNameMap.put(itemCode, itemName);
                        productUomMap.put(itemCode, "PCS"); // Assuming UOM, customize as per actual data
                    }
                    if (refrenceno == null) {
                        etreference.setText("N/A");
                    } else {
                        etreference.setText(refrenceno.toString().trim());
                    }
                    if (Comments == null) {
                        etcomments.setText("N/A");
                    } else {
                        etcomments.setText(Comments.toString().trim());
                    }
                    // Process return data and populate deliveryHistoryDetailsList
                    for (InvoiceDetailsByInvoiceNumber deliverydetailsInfo : allDelivery) {
                        String deliveredQty = deliverydetailsInfo.getDeliveredQty();
                        String itemCode = deliverydetailsInfo.getItemCode();
                        customer_code = deliverydetailsInfo.getCustomerCode();
                        if (customer_code != null && !customer_code.isEmpty()) {
                            customer_code = customer_code.substring(0, 1).toLowerCase() + customer_code.substring(1);
                        }

                        returnTotalQty += Integer.parseInt(deliveredQty);

                        // Create and populate DeliveryHistoryDetailsBean
                        DeliveryHistoryDeatilsBean returnHistoryBean = new DeliveryHistoryDeatilsBean();
                        returnHistoryBean.setOutletName(outletname);
                        returnHistoryBean.setItemname(productNameMap.get(itemCode));
                        returnHistoryBean.setUom(productUomMap.get(itemCode));
                        returnHistoryBean.setItemCode(itemCode);
                        returnHistoryBean.setDelqty(deliveredQty);
                        returnHistoryBean.setPrice(deliverydetailsInfo.getSellingPrice());
                        returnHistoryBean.setDisc(deliverydetailsInfo.getRebate());
                        returnHistoryBean.setNet(deliverydetailsInfo.getNetamount());
                        returnHistoryBean.setVat(deliverydetailsInfo.getVatamount());
                        returnHistoryBean.setGross(deliverydetailsInfo.getItemtotal());
                        returnHistoryBean.setDeliveryDateTime(deliverydetailsInfo.getDeliveredDatetime());
                        returnHistoryBean.setBarcode(deliverydetailsInfo.getBarcode().equals("null") ? "             " : String.valueOf(deliverydetailsInfo.getBarcode()));
                        System.out.println("barcode is :" + (deliverydetailsInfo.getBarcode().equals("null") ? "             " : String.valueOf(deliverydetailsInfo.getBarcode())));
                        returnHistoryBean.setPlucode(deliverydetailsInfo.getPlucode().equals("null")? "     " : String.valueOf(deliverydetailsInfo.getPlucode()));
                        System.out.println("plucode is :" + (deliverydetailsInfo.getPlucode().equals("null") ? "     " : String.valueOf(deliverydetailsInfo.getPlucode())));

                        // Add the bean to the list
                        deliveryHistoryDetailsList.add(returnHistoryBean);
                    }

                    // Set UI elements with parsed data
                    outletName.setText(outletname);
                    CustomerName.setText(customername);
                    Total_Qty.setText("Total Qty: " + returnTotalQty);
                    Total_Net_amt.setText("Total Net Amount: " + totalNet);
                    Total_vat_amt.setText("Total VAT Amount: " + totalVat);
                    Total_Amount_Payable.setText("Gross Amount Payable: " + totalGrossWithoutRebate);

                    // Set the adapter with the final data list
                    DeliveryHistoryDetailsAdapter deliveryHistoryDetailsAdapter = new DeliveryHistoryDetailsAdapter(DeliveryHistoryDetails.this, deliveryHistoryDetailsList);
                    listView.setAdapter(deliveryHistoryDetailsAdapter);

                    // Fetch and display TRN if available
                    Cursor cursor3 = allCustomerDetailsDB.readDataByName(customername);
                    if (cursor3.moveToFirst()) {
                        returnTrn = cursor3.getString(cursor3.getColumnIndex(AllCustomerDetailsDB.COLUMN_TRN));
                        //etTrn.setText(returnTrn != null ? returnTrn : "N/A");
                    }
                    cursor3.close();
                } else {
                    displayAlert("Alert", "Failed to fetch data or invalid response.");
                }
            }

            @Override
            public void onFailure(Call<InvoiceDetailsByIdResponse> call, Throwable t) {
                displayAlert("Alert", t.getMessage());
            }
        });
    }


    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
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
        deliveryHistoryDetailsList.clear();

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
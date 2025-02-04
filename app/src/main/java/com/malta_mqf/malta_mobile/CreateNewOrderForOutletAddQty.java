package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CreateNewOrderForNewOutlet.customerName;
import static com.malta_mqf.malta_mobile.CreateNewOrderForNewOutlet.customercode;
import static com.malta_mqf.malta_mobile.Signature.SignatureActivity.REQUEST_CODE_SIGNATURE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.Adapter.CreateAddQtyAdapter;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.StockAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNewOrderForOutletAddQty extends AppCompatActivity {
    private RecyclerView recyclerView;
    Toolbar toolbar;
    private Bitmap signatureBitmap;

    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    StockDB stockDB;
    CreateAddQtyAdapter createAddQtyAdapter;
    Set<StockBean> productIdQty ;
    public static List<StockBean> finalQty;

    Button captureSign,btn_next;
    private ImageView signatureImageView;
    ALodingDialog aLodingDialog;

    String outletid,outletname,customerName,customerCode;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_order_for_outlet_add_qty);
        productIdQty = new LinkedHashSet<>();
        finalQty=new LinkedList<>();

        aLodingDialog=new ALodingDialog(this);

        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        stockDB = new StockDB(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SELECT PRODUCTS");
        recyclerView = findViewById(R.id.recyclerView);
        captureSign=findViewById(R.id.btn_capture_sign);
        btn_next=findViewById(R.id.btn_select_printer);
        signatureImageView=findViewById(R.id.signatureImageView);

        outletid=getIntent().getStringExtra("outletId");
         customerName=getIntent().getStringExtra("customerName");
         customerCode=getIntent().getStringExtra("customerCode");
        outletname=getIntent().getStringExtra("outletName");
        System.out.println("outletname in create order: "+outletname);
        System.out.println("customername in create order: "+customerName);
        System.out.println("customerCode in create order: "+customerCode);
        captureSign.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        btn_next.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        displayAllItems();


        captureSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();
                recyclerView.smoothScrollToPosition(finalQty.size());
                //saveSignatureToGallery(signatureBitmap, "signature");
                Intent signatureIntent = new Intent(CreateNewOrderForOutletAddQty.this, SignatureCaptureActivity.class);
                startActivityForResult(signatureIntent, REQUEST_CODE_SIGNATURE);
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

        if(signatureBitmap== null){
            btn_next.setBackgroundColor(ContextCompat.getColor(this, R.color.light_grey));
            btn_next.setEnabled(false);
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the dialog immediately if the activity is active
                if (!CreateNewOrderForOutletAddQty.this.isFinishing() && !CreateNewOrderForOutletAddQty.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                // Use an ExecutorService to perform intensive work
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Perform intensive work here (e.g., preparing intents, etc.)
                        // Simulate a delay if necessary
                        try {
                            Thread.sleep(2000); // Simulate work by sleeping for 2 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Prepare the intent with necessary data
                        Intent intent = new Intent(CreateNewOrderForOutletAddQty.this, NewOrderInvoice.class);
                        intent.putExtra("outletId", outletid);
                        intent.putExtra("outletName", outletname);
                        intent.putExtra("customerName", customerName);
                        intent.putExtra("customerCode", customerCode);

                        // Switch back to the main thread to update the UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Dismiss the dialog only if it's showing and the activity is still active
                                if (!CreateNewOrderForOutletAddQty.this.isFinishing() && !CreateNewOrderForOutletAddQty.this.isDestroyed()) {
                                    if (aLodingDialog.isShowing()) {
                                        aLodingDialog.dismiss(); // Safely dismiss the dialog
                                    }
                                    // Start the new activity
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        });


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

                btn_next.setEnabled(true);
                btn_next.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
            } /*else if (requestCode == REQUEST_CODE_BILL) {
                billBitmap = (Bitmap) data.getExtras().get("data");
                billImageView.setImageBitmap(billBitmap);
            }*/
        }
    }

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    private void displayAllItems() {
        productIdQty.clear();
        finalQty.clear();
        System.out.println("customer code in create orderfor outlet addd qty: "+customerCode);
            Cursor itemsCursor = itemsByAgencyDB.readDataByCustomerCodes(customerCode);
            if (itemsCursor.getCount() > 0) {
                while (itemsCursor.moveToNext()) {
                    String productID = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    String customercode=itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_CUSTOMER_CODE));
                    System.out.println("product id in createneworder: "+productID);
                    System.out.println("customercodename id in createneworder: "+customercode);
                    Cursor stockCursor = stockDB.readonproductid(productID);
                    if (stockCursor != null && stockCursor.getCount() > 0) {
                        while (stockCursor.moveToNext()) {
                            String productId = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_PRODUCTID));
                            String avlQTY = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                            Cursor itemcursor=itemsByAgencyDB.readDataByCustomerCodeprodId(customercode,productId);
                            if(itemcursor!=null && itemcursor.getCount()>0){
                                while(itemcursor.moveToNext()){
                                    String productName = itemcursor.getString(itemcursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                    if(!avlQTY.equals("0")){
                                        productIdQty.add(new StockBean(productId, productName, avlQTY));

                                    }
                                }
                                itemcursor.close();
                            }
                        }
                        stockCursor.close();
                    }
                }
            }
            itemsCursor.close();


        for (StockBean bean : productIdQty) {
            String pID = bean.getProductID();
            String pName = bean.getProductName();
            String avlqty = bean.getQty();
            boolean productExists = false;

            for (StockBean productBean : finalQty) {
                if (productBean.getProductID().equals(pID)) {
                    // Update quantities of existing product in finaltotal
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                // If product does not exist in finaltotal, add it
                    finalQty.add(new StockBean(pID, pName, avlqty));
            }
        }

        createAddQtyAdapter = new CreateAddQtyAdapter(this,finalQty);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateNewOrderForOutletAddQty.this));
        recyclerView.setAdapter(createAddQtyAdapter);
        createAddQtyAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(CreateNewOrderForOutletAddQty.this, CreateNewOrderForNewOutlet.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        productIdQty.clear();
        finalQty.clear();
        outletid="";
        outletname="";



    }
}
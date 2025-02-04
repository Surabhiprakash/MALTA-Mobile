package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditNotebeanList;
import static com.malta_mqf.malta_mobile.CustomerReturnDetailsBsdOnInvoice.creditbeanList;
import static com.malta_mqf.malta_mobile.Signature.SignatureActivity.REQUEST_CODE_SIGNATURE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.Adapter.CreditNoteAdapter;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.SewooPrinter.ReturnBluetooth_Activity;
import com.malta_mqf.malta_mobile.Signature.SignatureCaptureActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReceiptDemo;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReturnSalesReceiptDemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class ReturnCreditNote extends AppCompatActivity {
  public static  double Creditnote,totalqty,totalnet,totalvat,toaltamountpayable,TOTALNET,TOTALVAT,TOTALGROSS,TOTALGROSSAFTERREBATE;
    TextView creditnoteId,Total_Qty,Total_Net_amt,Total_vat_amt,Total_Amount_Payable;
    Toolbar toolbar;
    ListView listView;
    EditText refrence,comment;
    public static String returnrefrence,returnComments;


  public static   int TOTALQTY;
 public static String invoiceNo,orderid,credId,customerName,customerCode,customeraddress,outletid,trn,returnUserID,returnVanID;
    CreditNoteAdapter creditNoteAdapter;
    OutletByIdDB outletByIdDB;
   Button print;
    private ALodingDialog aLodingDialog;
    String [] customerNamearr={"Bandidos Retial LLC","Careem Network General Trading LLC","Delivery Hero Stores DB LLC"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_credit_note);
        if (savedInstanceState != null) {
            restoreFromInstanceState(savedInstanceState);
        }
        invoiceNo=getIntent().getStringExtra("invoiceNo");
        credId=getIntent().getStringExtra("credId");
        orderid=getIntent().getStringExtra("orderid");
        TOTALQTY= Integer.parseInt((getIntent().getStringExtra("TOTALQTY")));
        TOTALNET= Double.parseDouble(getIntent().getStringExtra("TOTALNET"));
        TOTALVAT= Double.parseDouble(getIntent().getStringExtra("TOTALVAT"));
        TOTALGROSS= Double.parseDouble(getIntent().getStringExtra("TOTALGROSS"));
        TOTALGROSSAFTERREBATE=Double.parseDouble(getIntent().getStringExtra("TOTALGROSSAFTERREBATE"));
        trn=getIntent().getStringExtra("trn");
        customerName=getIntent().getStringExtra("customerName");
        customerCode=getIntent().getStringExtra("customerCode");
        System.out.println("Customer code: "+customerCode);
        customeraddress=getIntent().getStringExtra("customeraddress");
        outletid=getIntent().getStringExtra("outletid");
        returnUserID=getIntent().getStringExtra("userid");
        returnVanID=getIntent().getStringExtra("vanid");
        System.out.println("outletid in return creditnote: "+outletid);
        aLodingDialog = new ALodingDialog(this);
        outletByIdDB=new OutletByIdDB(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(invoiceNo);
        creditnoteId=findViewById(R.id.tvCreditNoteid);
        Total_Qty=findViewById(R.id.tvTotalQty);
        Total_Net_amt=findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt=findViewById(R.id.tvTotalVatAmt);
        print=findViewById(R.id.btn_save_print);
        refrence=findViewById(R.id.etRefNo);
        comment=findViewById(R.id.etComment);
        print.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        Total_Amount_Payable=findViewById(R.id.tvGrossAmount);
        creditnoteId.setText(credId);
        Total_Qty.setText("Total Qty: "+TOTALQTY);
        Total_Net_amt.setText("Total Net: "+TOTALNET);
        Total_vat_amt.setText("Total Vat: "+TOTALVAT);
        Total_Amount_Payable.setText("Total amount payable: "+TOTALGROSS);



        listView=findViewById(R.id.listViewcredit);


        creditNoteAdapter=new CreditNoteAdapter(this,creditNotebeanList);
        listView.setAdapter(creditNoteAdapter);


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  saveSignatureToGallery(mSignaturePad.getSignatureBitmap(), "Signature");
                returnrefrence=refrence.getText().toString().trim();
                returnComments=comment.getText().toString().trim();
                boolean shouldProceed = true; // Flag to control further execution

                // Check customer name and reference
                for (String s : customerNamearr) {
                    if (s.equals(customerName)) {
                        if (returnrefrence.isEmpty()) {
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
               // showAvailablePrinter();

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        creditNoteAdapter=new CreditNoteAdapter(this,creditNotebeanList);
        listView.setAdapter(creditNoteAdapter);
    }

    private void showAvailablePrinter() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ReturnCreditNote.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(ReturnCreditNote.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_spinner);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.setTitle("Choose printer");
        TextView sewoo_print = dialog.findViewById(R.id.sewoo_print);
        TextView zeb_print = dialog.findViewById(R.id.zeb_print);
        sewoo_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    /*Intent intent = new Intent(NewSaleActivity.this, Bluetooth_Activity.class);
                    startActivity(intent);
                    dialog.dismiss();*/
                System.out.println("creditbeanList"+creditNotebeanList);
                String outletName="",outletaddress="",emirate="";
                Cursor cursor=outletByIdDB.readOutletByID(outletid);
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()){
                        outletName=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        outletaddress=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));
                    }
                }
                cursor.close();
                Intent intent = new Intent(ReturnCreditNote.this, ReturnBluetooth_Activity.class);
                intent.putExtra("referenceNo", returnrefrence);
                intent.putExtra("comments",returnComments);
                intent.putExtra("outletname",outletName);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customerName);
                intent.putExtra("address",outletaddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("userid",returnUserID);
                intent.putExtra("vanid",returnVanID);
                intent.putExtra("creditBeanList",new Gson().toJson(creditbeanList));
                startActivity(intent);
                dialog.dismiss();
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        aLodingDialog.cancel();
                    }
                };
                handler.postDelayed(runnable, 3000);

            }

        } );

        zeb_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {


                String outletName="",outletaddress="",emirate="";
                Cursor cursor=outletByIdDB.readOutletByID(outletid);
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()){
                        outletName=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        outletaddress=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate=cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));
                    }
                }
                cursor.close();
                Intent intent = new Intent(ReturnCreditNote.this, ReturnSalesReceiptDemo.class);
                intent.putExtra("referenceNo", returnrefrence);
                intent.putExtra("comments",returnComments);
                intent.putExtra("outletname",outletName);
                intent.putExtra("customerCode",customerCode);
                intent.putExtra("customeraddress",customeraddress);
                intent.putExtra("customername",customerName);
                intent.putExtra("address",outletaddress);
                intent.putExtra("emirate",emirate);
                intent.putExtra("userid",returnUserID);
                intent.putExtra("vanid",returnVanID);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        String creditNoteBeanListJson = gson.toJson(creditNotebeanList);
        outState.putString("creditNoteBeanList", creditNoteBeanListJson);
        outState.putString("returnReference", returnrefrence);
        outState.putString("returnComments", returnComments);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreFromInstanceState(savedInstanceState);
    }

    private void restoreFromInstanceState(Bundle savedInstanceState) {
        Gson gson = new Gson();
        String creditNoteBeanListJson = savedInstanceState.getString("creditNoteBeanList");
        if (creditNoteBeanListJson != null) {
            Type type = new TypeToken<List<creditNotebean>>() {}.getType();
            creditNotebeanList = gson.fromJson(creditNoteBeanListJson, type);
        }
        returnrefrence = savedInstanceState.getString("returnReference");
        returnComments = savedInstanceState.getString("returnComments");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   creditNotebeanList.clear();
        Intent intent = new Intent(ReturnCreditNote.this, CustomerReturnDetailsBsdOnInvoice.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
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
}
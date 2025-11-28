package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.creditNotebeanList;
import static com.malta_mqf.malta_mobile.ConfirmReturnsActivity.creditbeanList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malta_mqf.malta_mobile.Adapter.CreditNoteAdapter;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.Model.creditNotebean;
import com.malta_mqf.malta_mobile.SewooPrinter.ReturnWithoutInvoiceBluetoothActivity;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.ZebraPrinter.ReturnWithoutInvoiceReceiptDemo;

import java.lang.reflect.Type;
import java.util.List;

public class ReturnCreditNoteWithoutInvoice extends AppCompatActivity {
    public static double Creditnote, totalqty, totalnet, totalvat, toaltamountpayable, TOTALNET, TOTALVAT, TOTALGROSS, TOTALGROSSAFTERREBATE;
    public static String returnrefrence, returnComments, route, name, vehiclenum, vanID, userID;
    public static int TOTALQTY;
    public static String credId, customerName, customerCode, outletid, trn, customeraddress;
    TextView creditnoteId, Total_Qty, Total_Net_amt, Total_vat_amt, Total_Amount_Payable;
    Toolbar toolbar;
    ListView listView;
    EditText refrence, comment;
    CreditNoteAdapter creditNoteAdapter;
    OutletByIdDB outletByIdDB;
    Button print;
    String[] customerNamearr = {"Bandidos Retial LLC", "Adnoc Distribution", "Delivery Hero Stores DB LLC"};
    ALodingDialog aLodingDialog;
    ReturnDB returnDB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_return_credit_without_invoice);
        restoreCreditNoteBeanList(this);
        if (savedInstanceState != null) {
            restoreFromInstanceState(savedInstanceState);
        }
        credId = getIntent().getStringExtra("credId");
        route = getIntent().getStringExtra("route");
        vanID = getIntent().getStringExtra("vanid");
        userID = getIntent().getStringExtra("userid");
        name = getIntent().getStringExtra("name");
        vehiclenum = getIntent().getStringExtra("vehiclenum");
        System.out.println("route in credit" + route);
        TOTALQTY = Integer.parseInt((getIntent().getStringExtra("TOTALQTY")));
        TOTALNET = Double.parseDouble(getIntent().getStringExtra("TOTALNET"));
        TOTALVAT = Double.parseDouble(getIntent().getStringExtra("TOTALVAT"));
        TOTALGROSS = Double.parseDouble(getIntent().getStringExtra("TOTALGROSS"));
        TOTALGROSSAFTERREBATE = Double.parseDouble(getIntent().getStringExtra("TOTALGROSSAFTERREBATE"));
        trn = getIntent().getStringExtra("trn");
        customerName = getIntent().getStringExtra("customerName");
        System.out.println("customernameeeee: " + customerName);
        System.out.println("vehicle number: " + vehiclenum);
        System.out.println("nameeeeeeeeee: " + name);
        customerCode = getIntent().getStringExtra("customerCode");
        outletid = getIntent().getStringExtra("outletid");
        customeraddress = getIntent().getStringExtra("customeraddress");
        outletByIdDB = new OutletByIdDB(this);
        aLodingDialog = new ALodingDialog(this);
        toolbar = findViewById(R.id.toolbar);
        returnDB = new ReturnDB(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RETURN CREDIT NOTE -" + credId);
        creditnoteId = findViewById(R.id.tvCreditNoteid);
        Total_Qty = findViewById(R.id.tvTotalQty);
        Total_Net_amt = findViewById(R.id.tvTotalNetAmount);
        Total_vat_amt = findViewById(R.id.tvTotalVatAmt);
        print = findViewById(R.id.btn_save_print);
        refrence = findViewById(R.id.etRefNo);
        comment = findViewById(R.id.etComment);
        print.setBackgroundColor(ContextCompat.getColor(this, R.color.appColorpurple));
        Total_Amount_Payable = findViewById(R.id.tvGrossAmount);
        creditnoteId.setText(credId);
        Total_Qty.setText("Total Qty: " + TOTALQTY);
        Total_Net_amt.setText("Total Net: " + TOTALNET);
        Total_vat_amt.setText("Total Vat: " + TOTALVAT);
        Total_Amount_Payable.setText("Total amount payable: " + TOTALGROSS);


        listView = findViewById(R.id.listViewcredit);


        creditNoteAdapter = new CreditNoteAdapter(this, creditNotebeanList);
        listView.setAdapter(creditNoteAdapter);


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  saveSignatureToGallery(mSignaturePad.getSignatureBitmap(), "Signature");
                returnrefrence = refrence.getText().toString().trim();
                returnComments = comment.getText().toString().trim();
                if (returnDB.checkDuplicateReferenceNumber(returnrefrence)) {
                    // Duplicate found; exit the method
                    return;
                }
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


    private void showAvailablePrinter() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) ReturnCreditNoteWithoutInvoice.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(ReturnCreditNoteWithoutInvoice.this);
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
                String outletName = "";
                String outletAddress = "";
                String emirate = "";
                String outletCode = "";

                Cursor cursor = outletByIdDB.readOutletByID(outletid);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        outletCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                        outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));
                    }
                }
                cursor.close();
                Intent intent = new Intent(ReturnCreditNoteWithoutInvoice.this, ReturnWithoutInvoiceBluetoothActivity.class);
                intent.putExtra("route", route);
                intent.putExtra("name", name);
                intent.putExtra("credID", credId);
                intent.putExtra("vehiclenum", vehiclenum);
                intent.putExtra("referenceNo", returnrefrence);
                intent.putExtra("comments", returnComments);
                intent.putExtra("outletname", outletName);
                intent.putExtra("outletCode", outletCode);
                intent.putExtra("outletAddress", outletAddress);
                intent.putExtra("emirate", emirate);
                intent.putExtra("customerCode", customerCode);
                intent.putExtra("customeraddress", customeraddress);
                intent.putExtra("customername", customerName);
                intent.putExtra("creditBeanList", new Gson().toJson(creditbeanList));
                intent.putExtra("vanid", vanID);
                intent.putExtra("userid", userID);
                startActivity(intent);
                dialog.dismiss();
            }

        });

        zeb_print.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {


                String outletName = "";
                String outletAddress = "";
                String emirate = "";
                String outletCode = "";
                System.out.println("outletid is.." + outletid);
                Cursor cursor = outletByIdDB.readOutletByID(outletid);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        outletCode = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                        outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        outletAddress = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_ADDRESS));
                        emirate = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_DISTRICT));

                    }
                }
                cursor.close();
                Intent intent = new Intent(ReturnCreditNoteWithoutInvoice.this, ReturnWithoutInvoiceReceiptDemo.class);
                intent.putExtra("route", route);
                intent.putExtra("credID", credId);
                intent.putExtra("vanid", vanID);
                intent.putExtra("userid", userID);
                intent.putExtra("referenceNo", returnrefrence);
                intent.putExtra("comments", returnComments);
                intent.putExtra("outletname", outletName);
                intent.putExtra("outletCode", outletCode);
                intent.putExtra("outletAddress", outletAddress);
                intent.putExtra("emirate", emirate);
                intent.putExtra("customerCode", customerCode);
                intent.putExtra("customername", customerName);
                intent.putExtra("customeraddress", customeraddress);
                intent.putExtra("outletid", outletid);
                intent.putExtra("vehiclenum", vehiclenum);
                intent.putExtra("name", name);
                startActivity(intent);
                dialog.dismiss();


            }
        });
        dialog.show();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the list as JSON
        Gson gson = new Gson();
        String creditNoteBeanListJson = gson.toJson(creditNotebeanList);
        outState.putString("creditNoteBeanList", creditNoteBeanListJson);

        // Save other fields if necessary
        outState.putString("returnReference", returnrefrence);
        outState.putString("returnComments", returnComments);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreFromInstanceState(savedInstanceState);
    }

    private void restoreFromInstanceState(Bundle savedInstanceState) {
        // Restore the list from saved state
        String creditNoteBeanListJson = savedInstanceState.getString("creditNoteBeanList");
        if (creditNoteBeanListJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<creditNotebean>>() {
            }.getType();
            creditNotebeanList = gson.fromJson(creditNoteBeanListJson, type);
        }

        // Restore other fields if needed
        returnrefrence = savedInstanceState.getString("returnReference");
        returnComments = savedInstanceState.getString("returnComments");
    }

    private void saveCreditNoteBeanList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String creditNoteBeanListJson = gson.toJson(creditNotebeanList);
        editor.putString("creditNoteBeanList", creditNoteBeanListJson);
        editor.apply();
    }

    private void restoreCreditNoteBeanList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String creditNoteBeanListJson = sharedPreferences.getString("creditNoteBeanList", null);
        if (creditNoteBeanListJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<creditNotebean>>() {
            }.getType();
            creditNotebeanList = gson.fromJson(creditNoteBeanListJson, type);
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
        //  creditNotebeanList.clear();
        Intent intent = new Intent(ReturnCreditNoteWithoutInvoice.this, ConfirmReturnsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

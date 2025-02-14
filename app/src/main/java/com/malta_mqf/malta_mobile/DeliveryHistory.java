package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.DeliveryhistoryAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.InvoiceDetailsByIdResponse;
import com.malta_mqf.malta_mobile.Model.InvoiceDetailsByInvoiceNumber;
import com.malta_mqf.malta_mobile.Model.OnlinePreviousInvoiceResponse;
import com.malta_mqf.malta_mobile.Model.deliveryhistorybean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.ApiClient;
import com.malta_mqf.malta_mobile.Utilities.ApiInterFace;

import org.spongycastle.cms.PasswordRecipientId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryHistory extends BaseActivity {
    ListView listdeliveryHistory;
    DeliveryhistoryAdapter adapter;
    SubmitOrderDB submitOrderDB;
    Toolbar toolbar;
    String invoiceNo;
    ProgressDialog progressDialog;
    ApiInterFace apiInterface;
    ALodingDialog aLodingDialog;
    String  customerName,outletName,outletCode;
    ItemsByAgencyDB itemsByAgencyDB;
    OutletByIdDB outletByIdDB;
    private Calendar calendar;
    private CardView dateSelectionLayout;
    private EditText etFromDate, etToDate;
    String selectedFromDate, selectedToDate;
    TextView tvNoDataFound;
    Button btnGetReturnHistory;
    List<deliveryhistorybean> listdeliveryhistory;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_history);
        apiInterface = ApiClient.getClient().create(ApiInterFace.class); // Ensure this is initialized
         listdeliveryhistory=new LinkedList<>();
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        dateSelectionLayout = findViewById(R.id.dateSelectionLayoutCardView);
        btnGetReturnHistory = findViewById(R.id.btnGet);
        tvNoDataFound=findViewById(R.id.tvNoDeliveryHistory);
        toolbar = findViewById(R.id.toolbar);
        aLodingDialog=new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DELIVERY History");
        listdeliveryHistory=findViewById(R.id.listDeliveryHistory);
        submitOrderDB=new SubmitOrderDB(this);
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        outletByIdDB=new OutletByIdDB(this);
        calendar = Calendar.getInstance();

        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate));

        etFromDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Safely compare using null check
                if (selectedFromDate != null && !selectedFromDate.equals(etFromDate.getText().toString())) {
                    showChangeDateConfirmationDialog(etFromDate); // Confirm change for From Date
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Empty
            }
        });

        etToDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Safely compare using null check
                if (selectedToDate != null && !selectedToDate.equals(etToDate.getText().toString())) {
                    showChangeDateConfirmationDialog(etToDate); // Confirm change for To Date
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Empty
            }
        });

        if(!isOnline()){

        }

        btnGetReturnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show(); // Show loading dialog

                if (isOnline()) {
                    System.out.println("get onlinereturn is called");

                    if (selectedFromDate == null || selectedFromDate.isEmpty() ||
                            selectedToDate == null || selectedToDate.isEmpty()) {
                        showAlert("Please select from date and to date");

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            aLodingDialog.dismiss();
                        }, 5000); // Dismiss after 5 seconds
                        return;
                    }

                    Log.d("DATE_CHECK", "From: " + selectedFromDate + ", To: " + selectedToDate);
                    getPreviousInvoicesOfOutletsByVan(selectedFromDate, selectedToDate, vanID);

                } else {
                    getOrdersDeliveredBasedOnStatus("DELIVERY DONE", "REJECTED", "DELIVERED",
                            "NEW ORDER DELIVERED", "REJECTED SYNCED", selectedFromDate, selectedToDate);
                    System.out.println(isOnline());
                }

                // Ensure loading stays for at least 5 seconds
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    aLodingDialog.dismiss();
                }, 3000);
            }
        });





        listdeliveryHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();

                String invOrOrderno=listdeliveryhistory.get(i).getInvoiceOrOrderID();
                String outletNameee=listdeliveryhistory.get(i).getOutletName();
                Intent intent=new Intent(DeliveryHistory.this,DeliveryHistoryDetails.class);
                intent.putExtra("invOrOrderno",invOrOrderno);
                intent.putExtra("outletname",outletNameee);
                intent.putExtra("outletCode",outletCode);
                System.out.println("invOrOrderno"+invOrOrderno);
                System.out.println("outletname"+outletNameee);
                System.out.println("outletCode  tooo next page "+outletCode);
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

    private void showChangeDateConfirmationDialog(final EditText editText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to change the date?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Update the date and call getInvoiceNumber
                        String selectedDate = editText.getText().toString();
                        if (editText == etFromDate) {
                            selectedFromDate = selectedDate;
                        } else if (editText == etToDate) {
                            selectedToDate = selectedDate;
                        }
                        System.out.println("Selected From Date: " + selectedFromDate);
                        System.out.println("Selected To Date: " + selectedToDate);
                        if(isOnline()){
                            getPreviousInvoicesOfOutletsByVan(selectedFromDate, selectedToDate, vanID);
                        } else {
                            getOrdersDeliveredBasedOnStatus("DELIVERY DONE", "REJECTED", "DELIVERED",
                                    "NEW ORDER DELIVERED", "REJECTED SYNCED", selectedFromDate, selectedToDate);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getPreviousInvoicesOfOutletsByVan(String fromDate,String toDate, String vanId) {
        progressDialog = new ProgressDialog(DeliveryHistory.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = ApiLinks.getPreviousInvoiceOutletsByVan +"?fromdate="+fromDate+"&todate="+toDate+"&van_id="+vanId;

        Call<OnlinePreviousInvoiceResponse> getDetails = apiInterface.getPreviousInvoiceByVanId(url);
        getDetails.enqueue(new Callback<OnlinePreviousInvoiceResponse>() {
            @SuppressLint("Range")
            @Override
            public void onResponse(Call<OnlinePreviousInvoiceResponse> call, Response<OnlinePreviousInvoiceResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    OnlinePreviousInvoiceResponse onlineReturnInfoResponse = response.body();
                    if (onlineReturnInfoResponse != null && "Success".equals(onlineReturnInfoResponse.getMessage())) {
                        List<String> data = onlineReturnInfoResponse.getReturnsInvoicesInfo();
                        listdeliveryhistory.clear();

                        for (String entry : data) {
                            String[] parts = entry.split(",");
                            if (parts.length == 3) {
                                String outletCode = parts[0];
                                String invoiceNumberId = parts[1];
                                String deliveredDateTime = parts[2];

                                Cursor cursor2 = outletByIdDB.readOutletByOutletCode(outletCode);
                                if (cursor2 != null && cursor2.getCount() != 0) {
                                    while (cursor2.moveToNext()) {
                                        outletName = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                        outletCode = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                                        System.out.println("outletname is :" + outletName + " " + "outletcode is : " + outletName);
                                    }
                                    cursor2.close();
                                }

                                String url = ApiLinks.getInvoiceDetailsByInvoiceNo + "?invoiceno=" + invoiceNumberId;
                                System.out.println("url: " + url);

                                // Asynchronous call to fetch data
                                Call<InvoiceDetailsByIdResponse> call2 = apiInterface.getInvoiceDetails(url);
                                String finalOutletCode = outletCode;
                                call2.enqueue(new Callback<InvoiceDetailsByIdResponse>() {

                                    @SuppressLint("Range")
                                    @Override
                                    public void onResponse(Call<InvoiceDetailsByIdResponse> call, Response<InvoiceDetailsByIdResponse> response) {
                                        System.out.println("I am in response");

                                        if (response.isSuccessful() && response.body() != null &&
                                                response.body().getStatus().equalsIgnoreCase("yes")) {

                                            InvoiceDetailsByIdResponse allOrderDetailsResponse = response.body();
                                            List<InvoiceDetailsByInvoiceNumber> allDelivery = allOrderDetailsResponse.getIndividualPoDetails();

                                            // Prepare variables
                                            String totalNet = allOrderDetailsResponse.getTotalnetamount();
                                            String totalVat = allOrderDetailsResponse.getTotalvatamount();
                                            String totalGrossWithoutRebate = allOrderDetailsResponse.getInvoicewithoutrebate();
                                            String outletname = allOrderDetailsResponse.getOutletName();
                                            String customerName = allOrderDetailsResponse.getCustomerName();
                                            String refrenceno = allOrderDetailsResponse.getRefno();
                                            String Comments = allOrderDetailsResponse.getComments();
                                            int returnTotalQty = 0;

                                            deliveryhistorybean deliveryHistoryBean = new deliveryhistorybean();
                                            deliveryHistoryBean.setOutletName(outletname + "(" + finalOutletCode + ")");
                                            System.out.println("outletname and outletcode is :" + outletname + " " + finalOutletCode);
                                            deliveryHistoryBean.setInvoiceOrOrderID(invoiceNumberId);
                                            System.out.println("invoice number is :" + invoiceNo);
                                            deliveryHistoryBean.setReferenceNo(refrenceno);
                                            System.out.println("reference number is :" + refrenceno);
                                            deliveryHistoryBean.setTotalAmount(totalGrossWithoutRebate);
                                            System.out.println("totalGrossWithoutRebate  is :" + totalGrossWithoutRebate);
                                            deliveryHistoryBean.setStatus("DELIVERY DONE");
                                            deliveryHistoryBean.setDatetime(deliveredDateTime);
                                            System.out.println("deliveredDateTime is : " + deliveredDateTime);
                                            listdeliveryhistory.add(deliveryHistoryBean);
                                            // Sort delivery history by date after adding each item
                                            sortDeliveryHistoryByDate();

                                            // Update the adapter on the main thread
                                            runOnUiThread(() -> {
                                                adapter = new DeliveryhistoryAdapter(DeliveryHistory.this, listdeliveryhistory);
                                                listdeliveryHistory.setAdapter(adapter);

                                                // Update the visibility of "No Data" message
                                                tvNoDataFound.setVisibility(listdeliveryhistory.isEmpty() ? View.VISIBLE : View.GONE);
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<InvoiceDetailsByIdResponse> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<OnlinePreviousInvoiceResponse> call, Throwable t) {
                // Handle the error
                displayAlert("Alert", t.getMessage());
                Log.e("API Error", "Failed to retrieve data", t);
            }
        });
    }


    private void sortDeliveryHistoryByDate() {
        Collections.sort(listdeliveryhistory, new Comparator<deliveryhistorybean>() {
            @Override
            public int compare(deliveryhistorybean o1, deliveryhistorybean o2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    return sdf.parse(o1.getDatetime()).compareTo(sdf.parse(o2.getDatetime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }


    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        // Get current date
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format selected date and display it in the EditText
                    String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                    editText.setText(selectedDate);

                    // Assign the selected date to the correct String variable
                    if (editText == etFromDate) {
                        selectedFromDate = selectedDate;
                    } else if (editText == etToDate) {
                        selectedToDate = selectedDate;
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }
    private String formatDate(int year, int month, int day) {
        // Format the date as "MM/dd/yyyy"
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return sdf.format(selectedDate.getTime());
    }
    private void showAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryHistory.this);
        builder.setTitle("Error");
        builder.setMessage(s);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    @SuppressLint("Range")
    private void getOrdersDeliveredBasedOnStatus(String status1, String status2,String status3,String status4,String status5,String fromDate,String toDate) {
        listdeliveryhistory.clear();

        toDate = toDate + " 23:59:59";  // Set the end date to 23:59:59 of the same day
        fromDate = fromDate + " 00:00:00"; // Set the start date to 00:00:00
        Cursor cursor = submitOrderDB.getOrdersBasedOnDeliveryStatus(status1, status2,status3,status4,status5,fromDate,toDate);
      System.out.println(status1 +status2+status3+status4+status5+fromDate+toDate);
      System.out.println(cursor.getCount());
        if (cursor.getCount() == 0) {
            // Handle case where no orders are found
            // You can show a message or perform any other appropriate action
        } else {
            System.out.println("inside else block getOrdersDeliveredBasedOnStatus");
            while (cursor.moveToNext()) {
                try {
                    System.out.println("inside else block getOrdersDeliveredBasedOnStatus2");
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_INVOICE_NO));
                    System.out.println("invoiceNo :"+invoiceNo );
                    String deliveryDateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
                    System.out.println("deliveryDateTime:"+deliveryDateTime);
                    String status=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                    System.out.println("status:"+status);
                    String outletid=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                    System.out.println("outletid:"+outletid);
//                    String totalAmt=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_TOTAL_GROSS_AMOUNT_WITHOUT_REBATE));
//                    System.out.println("totalAmt:"+totalAmt);
//                    String reference=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REFERENCE));
//                    System.out.println("reference:"+reference);

                    Cursor cursor2=outletByIdDB.readOutletByID(outletid);
                    if(cursor2.getCount()!=0){
                        while (cursor2.moveToNext()){
                            outletName=cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                            outletCode=cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                            System.out.println("inside else block cursor2"+outletName+" "+outletCode);

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
                    deliveryHistoryBean.setOutletName(outletName+"("+outletCode+")");
                // deliveryHistoryBean.setReferenceNo(reference);
//                    deliveryHistoryBean.setTotalAmount(totalAmt);
                    listdeliveryhistory.add(deliveryHistoryBean);
                } catch (Exception e) {
                    // Handle any exceptions that may occur during data retrieval
                    e.printStackTrace();
                }
            }
            // Set up the adapter and populate the ListView outside the loop for efficiency
            //sortDeliveryHistoryByDate();
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
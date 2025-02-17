package com.malta_mqf.malta_mobile;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.ReturnHistoryAdapter;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;

import com.malta_mqf.malta_mobile.DataBase.ReturnDB;
import com.malta_mqf.malta_mobile.DataBase.UserDetailsDb;
import com.malta_mqf.malta_mobile.Model.AllReturnOrderDetails;
import com.malta_mqf.malta_mobile.Model.AllReturnOrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OnlineReturnInfoResponse;
import com.malta_mqf.malta_mobile.Model.ReturnHistoryBean;

import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;
import com.malta_mqf.malta_mobile.Utilities.ApiClient;
import com.malta_mqf.malta_mobile.Utilities.ApiInterFace;

import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Return_History extends BaseActivity {
    ListView listViewReturnHistory;
    ReturnHistoryAdapter adapter;
    ReturnDB returnDB;
    Toolbar toolbar;
    String invoiceNo;
    private EditText etFromDate, etToDate;
    private CardView cardViewHeader;
    private Calendar calendar;
    String selectedFromDate, selectedToDate;
    UserDetailsDb userDetailsDb;
    Button btnGetReturnHistory;
    TextView tvNoDataFound;
    ALodingDialog aLodingDialog;
    ProgressDialog progressDialog;
    String  customerName,outletName,outletCode;
    ItemsByAgencyDB itemsByAgencyDB;
    OutletByIdDB outletByIdDB;
    List<ReturnHistoryBean> listReturnHistory=new LinkedList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history);
        toolbar = findViewById(R.id.toolbar);
        etFromDate = findViewById(R.id.etFromDate);
        etToDate = findViewById(R.id.etToDate);
        tvNoDataFound = findViewById(R.id.tvNoReturnHistory);
        cardViewHeader = findViewById(R.id.cardViewHeader);
        btnGetReturnHistory = findViewById(R.id.btnGet);
        userDetailsDb = new UserDetailsDb(this);
        aLodingDialog=new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RETURN HISTORY");
        listViewReturnHistory=findViewById(R.id.listDeliveryHistory);
        returnDB=new ReturnDB(this);
        itemsByAgencyDB=new ItemsByAgencyDB(this);
        outletByIdDB=new OutletByIdDB(this);
        calendar = Calendar.getInstance();

        // Set onClick listeners for both date fields
        etFromDate.setOnClickListener(v -> showDatePickerDialog(etFromDate));
        etToDate.setOnClickListener(v -> showDatePickerDialog(etToDate));
       // getOrdersReturnedBasedOnStatus("RETURNED","RETURNED NO INVOICE","RETURN DONE");

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
                    getOrdersReturnedBasedOnStatusOnline(selectedFromDate, selectedToDate, vanID);

                } else {
                    getOrdersReturnedBasedOnStatus("RETURNED", "RETURNED NO INVOICE", "RETURN DONE", selectedFromDate, selectedToDate);
                }

                // Ensure loading stays for at least 5 seconds
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    aLodingDialog.dismiss();
                }, 3000);
            }
        });



        if(isOnline()){
            listViewReturnHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    aLodingDialog.show();

                    String creditid = listReturnHistory.get(i).getCreditNoteID();
                    String outletNameee = listReturnHistory.get(i).getOutletName();
                    Intent intent = new Intent(Return_History.this, ReturnHistoryDetails.class);
                    intent.putExtra("outletname", outletNameee);
                    intent.putExtra("creditid", creditid);
                    startActivity(intent);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                }

            });
        }else{
            listViewReturnHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    aLodingDialog.show();

                    String invOrOrderno = listReturnHistory.get(i).getInvoiceOrOrderID();
                    String creditid = listReturnHistory.get(i).getCreditNoteID();
                    String outletNameee = listReturnHistory.get(i).getOutletName();
                    Intent intent = new Intent(Return_History.this, ReturnHistoryDetails.class);
                    intent.putExtra("invOrOrderno", invOrOrderno);
                    intent.putExtra("outletname", outletNameee);
                    intent.putExtra("creditid", creditid);
                    startActivity(intent);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                }
            });
        }
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
                            getOrdersReturnedBasedOnStatusOnline(selectedFromDate, selectedToDate, vanID);
                        } else {
                            getOrdersReturnedBasedOnStatus("RETURNED", "RETURN DONE", "RETURNED NO INVOICE",  selectedFromDate, selectedToDate);
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

    private void getOrdersReturnedBasedOnStatusOnline(String fromDate, String toDate, String vanId) {
        listReturnHistory.clear();
        progressDialog = new ProgressDialog(Return_History.this);
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = ApiLinks.onlineReturnDetails + "?fromdate=" + fromDate + "&todate=" + toDate + "&van_id=" + vanId;
        System.out.println("url: " + url);
        Call<OnlineReturnInfoResponse> call = apiInterface.allReturnOrderDetailsByVanId(url);
        call.enqueue(new Callback<OnlineReturnInfoResponse>() {

            @SuppressLint("Range")
            @Override
            public void onResponse(Call<OnlineReturnInfoResponse> call, Response<OnlineReturnInfoResponse> response) {
                System.out.println("I am in response");

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getStatus().equalsIgnoreCase("yes")) {

                    OnlineReturnInfoResponse allOrderDetailsResponse = response.body();
                    System.out.println("allOrderDetailsResponse: success");
                    List<String> allReturn = allOrderDetailsResponse.getReturnsOutletsInfo();
                    System.out.println("allReturn: " + allReturn);

                    // Process each return info
                    for (String returnInfo : allReturn) {
                        // Split each item by comma
                        System.out.println("for");
                        String[] parts = returnInfo.split(",");
                        if (parts.length == 3) {
                            String outletCode = parts[0];
                            String creditNoteId = parts[1];
                            String returnedDateTime = parts[2];

                            System.out.println("outletCode: " + outletCode + " creditNoteId: " + creditNoteId);

                            // Retrieve outlet name based on outletCode
                            Cursor cursor2 = outletByIdDB.readOutletByOutletCode(outletCode);
                            if (cursor2 != null && cursor2.getCount() != 0) {
                                while (cursor2.moveToNext()) {
                                    outletName = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                    outletCode = cursor2.getString(cursor2.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_CODE));
                                }
                                cursor2.close();
                            }

                            String currentOutletName = outletName; // Helper method to fetch outlet name
                            System.out.println("outlet name is :" + currentOutletName);

                            // Fetch referenceNo and totalAmount using creditNoteId
                            String apiUrl = ApiLinks.allOnlineReturnDetails + "?credit_note_id=" + creditNoteId;
                            Call<AllReturnOrderDetailsResponse> referenceCall = apiInterface.allReturnOrderDetails(apiUrl);
                            String finalOutletCode = outletCode;
                            referenceCall.enqueue(new Callback<AllReturnOrderDetailsResponse>() {
                                @Override
                                public void onResponse(Call<AllReturnOrderDetailsResponse> call, Response<AllReturnOrderDetailsResponse> referenceResponse) {
                                    if (referenceResponse.isSuccessful() && referenceResponse.body() != null) {
                                        List<AllReturnOrderDetails> allReturnDetails = referenceResponse.body().getReturnsInfo();
                                        // Extract referenceNo and totalAmount from the first item (if there's only one)
                                        if (allReturnDetails != null && !allReturnDetails.isEmpty()) {
                                            AllReturnOrderDetails returnDetails = allReturnDetails.get(0);
                                            String referenceNo = returnDetails.getRefno();
                                            String totalAmount = returnDetails.getReturntotalnetamount();

                                            ReturnHistoryBean returnHistoryBean = new ReturnHistoryBean();
                                            returnHistoryBean.setCreditNoteID(creditNoteId);
                                            returnHistoryBean.setStatus("RETURN DONE");
                                            returnHistoryBean.setOutletName(currentOutletName + "(" + finalOutletCode + ")");
                                            returnHistoryBean.setDatetime(returnedDateTime);
                                            returnHistoryBean.setReferenceNo(referenceNo);
                                            returnHistoryBean.setTotalAmt(totalAmount);

                                            listReturnHistory.add(returnHistoryBean);
                                            // Once all data is fetched, sort and set the adapter
                                            sortDeliveryHistoryByDate();
                                            System.out.println("list size: " + listReturnHistory.size());
                                            System.out.println("list: " + listReturnHistory);

                                            // Refresh the adapter after adding each item
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<AllReturnOrderDetailsResponse> call, Throwable t) {
                                    System.err.println("Error fetching reference details: " + t.getMessage());
                                }
                            });
                        }
                    }


                    adapter = new ReturnHistoryAdapter(Return_History.this, listReturnHistory);
                    listViewReturnHistory.setAdapter(adapter);
                    progressDialog.dismiss();
                }

               /*if (listReturnHistory.isEmpty()) {
                   tvNoDataFound.setVisibility(View.VISIBLE);
               } else {
                   tvNoDataFound.setVisibility(View.GONE);
               }*/
            }

            @Override
            public void onFailure(Call<OnlineReturnInfoResponse> call, Throwable t) {
                displayAlert("Alert", t.getMessage());
                progressDialog.dismiss();
            }
        });
    }


    private String getOutletName(String outletCode) {
        String outletName = "Unknown Outlet";
        Cursor cursor = outletByIdDB.readOutletByOutletCode(outletCode);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                outletName = cursor.getString(cursor.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
            }
            cursor.close();
        }
        return outletName;
    }

    private void sortDeliveryHistoryByDate() {
        Collections.sort(listReturnHistory, new Comparator<ReturnHistoryBean>() {
            @Override
            public int compare(ReturnHistoryBean o1, ReturnHistoryBean o2) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date date1 = sdf.parse(o1.getDatetime());
                    Date date2 = sdf.parse(o2.getDatetime());

                    // First, sort by date (newest first)
                    int dateComparison = date2.compareTo(date1);
                    if (dateComparison != 0) {
                        return dateComparison;
                    }

                    // If dates are the same, sort by Credit Note ID in descending order
                    return o2.getCreditNoteID().compareTo(o1.getCreditNoteID());

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }



    private void showAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Return_History.this);
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

    private void showDatePickerDialog(final EditText editText) {
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
    @SuppressLint("Range")
    private void getOrdersReturnedBasedOnStatus(String status1, String status2, String status3,String fromDate,String toDate) {
        listReturnHistory.clear();

        toDate = toDate + " 23:59:59";  // Set the end date to 23:59:59 of the same day
        fromDate = fromDate + " 00:00:00"; // Set the start date to 00:00:00
        Cursor cursor = returnDB.getOrdersBasedOnReturnStatus(status1, status2,status3,fromDate,toDate);
        System.out.println(status1 +status2+status3+fromDate+toDate);
        System.out.println(cursor.getCount());
        if (cursor.getCount() == 0) {
            // Handle case where no orders are found
            // You can show a message or perform any other appropriate action
        } else {
            System.out.println("inside else block getOrdersDeliveredBasedOnStatus");
            while (cursor.moveToNext()) {
                try {
                    String invoiceNo = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_INVOICE_NO));
                    String deliveryDateTime = cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_DATE_TIME));
                    String status=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_STATUS));
                    String outletid=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_OUTLETID));
                    String creditid=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_CREDIT_NOTE));
//                    String returnReference=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_REFERENCE_NO));
//                    String totalAmount=cursor.getString(cursor.getColumnIndex(ReturnDB.COLUMN_TOTAL_GROSS_AMOUNT_WITHOUT_REBATE));
//                    System.out.println("totalAmount.."+totalAmount);
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
                    returnHistoryBean.setOutletName(outletName+"("+outletCode+")");
                    returnHistoryBean.setCreditNoteID(creditid);
//                    returnHistoryBean.setTotalAmt(totalAmount);
//                    returnHistoryBean.setReferenceNo(returnReference);
                    listReturnHistory.add(returnHistoryBean);
                } catch (Exception e) {
                    // Handle any exceptions that may occur during data retrieval
                    e.printStackTrace();
                }
            }
            // ✅ Call sorting method before updating the adapter
            sortDeliveryHistoryByDate();

            // ✅ Refresh the adapter
            runOnUiThread(() -> {
                adapter = new ReturnHistoryAdapter(Return_History.this, listReturnHistory);
                listViewReturnHistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
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
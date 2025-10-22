package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.AddItemsActivity.customercode;
import static com.malta_mqf.malta_mobile.AddItemsActivity.listOutletIDs;
import static com.malta_mqf.malta_mobile.AddItemsActivity.outletID;
import static com.malta_mqf.malta_mobile.MainActivity.userID;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter2;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderConfrimSpinnerAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetails;
import com.malta_mqf.malta_mobile.Model.AllAgencyDetailsResponse;
import com.malta_mqf.malta_mobile.Model.AllItemDeatilsById;
import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;
import com.malta_mqf.malta_mobile.Model.OnlineProductBean;
import com.malta_mqf.malta_mobile.Model.OrderConfrimBean;
import com.malta_mqf.malta_mobile.Model.OrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepeatAddQuantity extends BaseActivity  implements AddQtyAdapter2.QuantityChangeListener  {
    AutoCompleteTextView spinneragecny;
    AutoCompleteTextView   spinnerproducts;
    ArrayAdapter<String > adapter1;

    GetCusOutletAgencyProductAdapter adapter;
    List<String> listagency;
    List<String> listproduct;
    AddQtyAdapter2 addQtyAdapter;
    TextView qtycount, itemcount;

    RecyclerView recyclerView;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    String agencycode;
    String agency;
    String agencyID,productID,quantity,ItemCode;
    List<Map.Entry<String, String>> selectedproduct ;
    List<String> submittedorder;
    private DatePickerDialog datePickerDialog;
    Set<ProductInfo> productIdQty ;

    List<String> onlineProductID;
    List<String> onlineItemCode;
    List<String> onlinelistagencyids;
    List<String> onlineReqQtys;
    List<String> prodqty;
    List<String> getProdqty;
    Button submit;
    SubmitOrderDB submitOrderDB;
    String outletid;
    EndsWithArrayAdapter endsWithArrayAdapter;
    TextView outletname_header,selectProductTextview;
    Toolbar toolbar;
    ImageView searchitembyagency,searchproductIcons;
    private ItemTouchHelper itemTouchHelper;
    List<OnlineProductBean> onlineProductBeanList=new LinkedList<>();

    LinearLayout searchProductLayout;
    String agencyIds[];
    String leadTime;
    SearchView searchview;
    @SuppressLint({"MissingInflatedId", "Range"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_add_quantity);
         listagency=new LinkedList<>();
         listproduct=new LinkedList<>();
         selectedproduct = new LinkedList<>();
         submittedorder=new LinkedList<>();
         productIdQty = new LinkedHashSet<>();

         onlineProductID=new LinkedList<>();
         onlineItemCode=new LinkedList<>();
         onlinelistagencyids=new LinkedList<>();
         onlineReqQtys=new LinkedList<>();
         prodqty=new LinkedList<>();
         getProdqty=new LinkedList<>();
         onlineProductBeanList=new LinkedList<>();


        Toast.makeText(this, "Repeat Items Here!", Toast.LENGTH_SHORT).show();
        spinneragecny = findViewById(R.id.spinnerLeft);
        spinnerproducts = findViewById(R.id.spinnerRight);
        recyclerView = findViewById(R.id.listaddproducts);
        searchitembyagency = findViewById(R.id.search_icon);
        searchProductLayout = findViewById(R.id.searchproductLayout);
        searchproductIcons = findViewById(R.id.search_icon2);
        selectProductTextview = findViewById(R.id.selectOutletTextView);
        searchview=findViewById(R.id.searchView);
        qtycount=findViewById(R.id.qtycount);
        itemcount=findViewById(R.id.itemcount);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        submit = findViewById(R.id.submitorder);
        submit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        outletname_header = findViewById(R.id.outletname_header);
        submitOrderDB = new SubmitOrderDB(this);


        outletid = getIntent().getStringExtra("outletId");

        Log.e("outletid", outletid);
        // outletname_header.setText();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("REPEAT ORDER-" + getIntent().getStringExtra("outletName"));

        spinneragecny.setMaxLines(1);
        spinneragecny.setMovementMethod(new ScrollingMovementMethod());
        spinnerproducts.setMaxLines(1);
        spinnerproducts.setMovementMethod(new ScrollingMovementMethod());

        showLeadTimeDialog();
        int maxLength = 12;

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength);


        spinnerproducts.setFilters(filters);


        spinnerproducts.setEllipsize(TextUtils.TruncateAt.END);


        searchProductLayout.setEnabled(false);
        spinnerproducts.setEnabled(false);
        spinnerproducts.setFocusable(false);
        spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


        selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
        spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
        searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
        //RepeatProducts(outletid);
        //   getAllAgency();
        displayAllAgency();
        searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                String agency = spinneragecny.getText().toString().trim();
                getProdqty.addAll(prodqty);
                prodqty.clear();
                Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));

                    }

                    // agencyIDS.add(agencyID);
                    Log.d("TAG", "onItemSelected: " + agencycode);
                }
                // getAllItemById();
                listproduct.clear();
                if (isOnline()) {
                    getAllItemById(agency);
                    System.out.println("online");
                } else {
                    displayAllItemsById(agencycode, customercode.toLowerCase());
                }
                // displayAllItemsById(agencycode);
                cursor.close();
            }
        });

        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listproduct);
        recyclerView.setAdapter(addQtyAdapter);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (addQtyAdapter != null) {
                    addQtyAdapter.filter(newText);
                }
                return false;
            }
        });


        new FetchAgencyDataTask().execute();
      /*  for(int i=0;i<agencyIds.length;i++) {
            String agencyId = agencyIds[i];
            ;
            Cursor cursor = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {

                  String  agency=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));
                    System.out.println("Agency namessssssssss:"+agency);
                    if (isOnline()) {
                        getAllItemById(agency);
                        System.out.println("online");
                    }
                }
            }
        }*/
       /* spinneragecny.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @SuppressLint("Range")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(AddQuantity.this, listagency.get(position), Toast.LENGTH_SHORT).show();
                String agency = listagency.get(position);
                //agencyIDS.clear();
                getProdqty.addAll(prodqty);
                prodqty.clear();
                Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));

                    }

                    // agencyIDS.add(agencyID);
                    Log.d("TAG", "onItemSelected: " + agencycode);
                }
                // getAllItemById();
                displayAllItemsById(agencycode);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here
            }
        });

        spinnerproducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("Range")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String productName = listproduct.get(position);
                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                        //  ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                    }
                }
                boolean alreadyExists = false;
                for (Map.Entry<String, String> entry : selectedproduct) {
                    if (entry.getKey().equals(productName)) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));

                    if (addQtyAdapter == null) {
                        addQtyAdapter = new AddQtyAdapter(RepeatAddQuantity.this, selectedproduct);
                        recyclerView.setAdapter(addQtyAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RepeatAddQuantity.this));
                    } else {
                        addQtyAdapter.notifyItemInserted(selectedproduct.size() - 1);
                    }


                    // Print product name and quantity
                    for (Map.Entry<String, String> entry : selectedproduct) {
                        System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());

                    }

                    initializeSwipeToDelete();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing here
            }
        });

*/
        spinneragecny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchProductLayout.setEnabled(false);
                spinnerproducts.setEnabled(false);
                spinnerproducts.setFocusable(false);
                spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


                selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
                spinneragecny.showDropDown();
            }
        });

        spinneragecny.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                    searchProductLayout.setEnabled(false);
                    spinnerproducts.setEnabled(false);
                    spinnerproducts.setFocusable(false);
                    spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events


                    selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                    spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                    searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
                    spinneragecny.showDropDown();
                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });



        spinneragecny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinneragecny.getText().toString().isEmpty()) {
                    Toast.makeText(RepeatAddQuantity.this, "Agency cannot be empty!", Toast.LENGTH_SHORT).show();

                } else {
                    agency = spinneragecny.getText().toString().trim();
                    getProdqty.addAll(prodqty);
                    prodqty.clear();
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                            //agencyID=cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));

                        }

                        // agencyIDS.add(agencyID);
                        Log.d("TAG", "onItemSelected: " + agencycode);
                    }
                    // getAllItemById();
                    listproduct.clear();
                    if (isOnline()) {
                        getAllItemById(agency);
                        System.out.println("online");
                    } else {
                        displayAllItemsById(agencycode, customercode);
                    }
                    // displayAllItemsById(agencycode);
                    cursor.close();
                }
            }
        });

        spinnerproducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerproducts.showDropDown();
            }
        });

        spinnerproducts.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                            spinnerproducts.showDropDown();
                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupDatePicker();
            }

        });


    }


    private class FetchAgencyDataTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create and show the progress dialog
            progressDialog = new ProgressDialog(RepeatAddQuantity.this);
            progressDialog.setMessage("Loading......");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int i = 0; i < agencyIds.length; i++) {
                    String agencyId = agencyIds[i];
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            @SuppressLint("Range") String agency = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));
                            if (isOnline()) {
                                getAllItemById(agency);
                            }
                        }
                    }
                    cursor.close();
                }
            }catch (Exception e){

            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            // Dismiss the progress dialog
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            // Update the UI after the background task is complete
        }
    }
    @Override
    public void onTotalQuantityChanged(int totalQuantity) {
        TextView totalQtyTextView = findViewById(R.id.qtycount);

        totalQtyTextView.setText("#Quantity: " + totalQuantity);

    }
    @Override
    public void onTotalItemChanged(int totalItems) {
        TextView itemCount = findViewById(R.id.itemcount);
        if (addQtyAdapter != null) {
            itemCount.setText("#Item Count: " + addQtyAdapter.getItemCount());
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




    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
    private long generateRandomOrderID() {
        long min = 10000000000L;  // This is the smallest 15-digit number
        long max = 99999999999L;  // This is the largest 15-digit number
        long random = (long) (Math.random() * (max - min + 1)) + min;
        return random;
    }
    private String processCustomerCode(String customerCode) {
        if (customerCode == null || customerCode.isEmpty()) {
            return "";
        }
        String[] words = customerCode.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
            }
        }
        return initials.toString();
    }

        private void getAllAgency() {
        // showProgressDialog();
        String url = ApiLinks.allAgencyDetails;
        Log.d("TAG", "getAllAgency: "+url);
        Call<AllAgencyDetails> allAgencyDetailsCall = apiInterface.allAgencyDetails(url);
        allAgencyDetailsCall.enqueue(new Callback<AllAgencyDetails>() {

            @Override
            public void onResponse(Call<AllAgencyDetails> call, Response<AllAgencyDetails> response) {
                // dismissProgressDialog();
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllAgencyDetails allAgencyDetails = response.body();
                    List<AllAgencyDetailsResponse> allAgencyDetailsResponses = allAgencyDetails.getActiveAgencyDetails();
                    try  {
                        for (AllAgencyDetailsResponse allAgencyDetailsResponse : allAgencyDetailsResponses) {
                            listagency.add(allAgencyDetailsResponse.getAgencyName());
                            endsWithArrayAdapter=new EndsWithArrayAdapter(RepeatAddQuantity.this,R.layout.list_item_text,R.id.list_textView_value,listagency);
                            spinneragecny.setAdapter(endsWithArrayAdapter);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllAgencyDetails> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());

                displayAlert("Alert", t.getMessage());
            }
        });
    }

    @SuppressLint("Range")
    private void displayAllAgency(){

        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Agency data", Toast.LENGTH_SHORT).show();
            return;
        }else while (cursor.moveToNext()) {
            listagency.add(cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME)));
        }


        endsWithArrayAdapter=new EndsWithArrayAdapter(RepeatAddQuantity.this,R.layout.list_item_text,R.id.list_textView_value,listagency);
        spinneragecny.setAdapter(endsWithArrayAdapter);
        cursor.close();
    }

    private void getAllItemById(String agency) {
        String url = ApiLinks.allItemDetailsById;
        Log.d("TAG", "getAllItemById: " + url);
        System.out.println("agency: "+agency +" "+"customercode: "+customercode + " "+leadTime);

        Call<AllItemDeatilsById> allItemDeatilsByIdCall = apiInterface.allItemDetailsById(url);
        allItemDeatilsByIdCall.enqueue(new Callback<AllItemDeatilsById>() {
            @Override
            public void onResponse(Call<AllItemDeatilsById> call, Response<AllItemDeatilsById> response) {
                AllItemDeatilsById allItemDeatilsById = response.body();
                List<AllItemDetailResponseById> allItemDetailResponseByIds = allItemDeatilsById.getActiveItemDetailsWithSellingPrice();

                try {
                    int count = 0;
                    onlineProductBeanList.clear(); // Clear the list before adding new items
                    listproduct.clear(); // Clear the list before adding new items
                    System.out.println("agency2: "+agency +" "+"customercode: "+customercode + " "+leadTime);
                    for (AllItemDetailResponseById itemDetail : allItemDetailResponseByIds) {
                        System.out.println("agencyName: "+itemDetail.getAgencyName()+" "+"customercode: "+itemDetail.getCustomerCode()+"leac tie:"+itemDetail.getLead_time());
                        if (agency.equalsIgnoreCase(itemDetail.getAgencyName()) && customercode.equalsIgnoreCase(itemDetail.getCustomerCode()) && leadTime.equalsIgnoreCase(itemDetail.getLead_time())) {
                            count++;
                            System.out.println("counttttttt:"+count);
                            onlineProductBeanList.add(new OnlineProductBean(itemDetail.getItemName(), itemDetail.getId(), itemDetail.getItemCode(), itemDetail.getAgencyCode()));
                            listproduct.add(itemDetail.getItemName());
                        }
                    }

                    if (count == 0) {
                        searchProductLayout.setEnabled(false);
                        spinnerproducts.setEnabled(false);
                        spinnerproducts.setFocusable(false);
                        spinnerproducts.setFocusableInTouchMode(false);
                        spinnerproducts.setText("");

                        selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
                        spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
                        searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));

                        Toast.makeText(RepeatAddQuantity.this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();

                    } else {
                        searchProductLayout.setEnabled(true);
                        spinnerproducts.setEnabled(true);
                        spinnerproducts.setFocusable(true);
                        spinnerproducts.setFocusableInTouchMode(true);
                        spinnerproducts.setText("");

                        selectProductTextview.setTextColor(getResources().getColor(R.color.black));
                        spinnerproducts.setTextColor(getResources().getColor(R.color.black));
                        searchproductIcons.setColorFilter(getResources().getColor(R.color.black));

                        endsWithArrayAdapter = new EndsWithArrayAdapter(RepeatAddQuantity.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
                        spinnerproducts.setAdapter(endsWithArrayAdapter);

                        spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                String productName = listproduct.get(position);
                                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                                if (cursor != null && cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                    // ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                    cursor.close();
                                }

                                boolean alreadyExists = false;
                                for (Map.Entry<String, String> entry : selectedproduct) {
                                    if (entry.getKey().equals(productName)) {
                                        alreadyExists = true;
                                        break;
                                    }
                                }

                                if (!alreadyExists) {
                                    selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));
                                    selectedproduct = convertListToMapEntryList(selectedproduct);

                                    if (addQtyAdapter == null) {

                                        addQtyAdapter = new AddQtyAdapter2(RepeatAddQuantity.this, selectedproduct);
                                       // addQtyAdapter.setQuantityChangeListener((AddQtyAdapter2.QuantityChangeListener) RepeatAddQuantity.this);
                                        addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter2.QuantityChangeListener() {
                                            @Override
                                            public void onTotalQuantityChanged(int totalQuantity) {
                                                // Update UI or perform actions based on totalQuantity change
                                                // Example: Update TextView showing total quantity
                                                qtycount.setText("#Quantity: " + totalQuantity);
                                            }

                                            @Override
                                            public void onTotalItemChanged(int totalItems) {
                                                // Update UI or perform actions based on totalItems change
                                                // Example: Update TextView showing total item count
                                                itemcount.setText("#Item Count: " + totalItems);
                                            }
                                        });
                                        recyclerView.setAdapter(addQtyAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(RepeatAddQuantity.this));
                                    } else {
                                        addQtyAdapter.updateData(selectedproduct);
                                    }

                                   // initializeSwipeToDelete();
                                }
                            }
                        });
                    }

                    selectedproduct = convertListToMapEntryList(selectedproduct);
                    if (addQtyAdapter != null) {
                        addQtyAdapter.updateData(selectedproduct);
                    }
                    dismissProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AllItemDeatilsById> call, Throwable t) {
                Log.d("TAG", "onFailure: " + t.getMessage());
                displayAlert("Alert", t.getMessage());
                dismissProgressDialog();
            }
        });
    }


    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the default date to tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1+Integer.parseInt(leadTime)); // Adds 1 day to the current date for default selection (tomorrow)
        int defaultYear = calendar.get(Calendar.YEAR);
        int defaultMonth = calendar.get(Calendar.MONTH);
        int defaultDay = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme, // Custom theme
                onDateSetListener,
                defaultYear,  // Set default year to tomorrow
                defaultMonth, // Set default month to tomorrow
                defaultDay    // Set default day to tomorrow
        );
        datePickerDialog.setTitle("Select expected delivery date");

        // Set minimum date to today (so today is selectable)
        Calendar minDate = Calendar.getInstance();  // Get today's date
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int selectedYear = datePicker.getYear();
                int selectedMonth = datePicker.getMonth();
                int selectedDay = datePicker.getDayOfMonth();
                String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);
                // Handle the selected date (e.g., display it, use it somewhere, etc.)
                // Toast.makeText(MainActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();

                if (selectedproduct.size() != 0) {
                    // Initialize a calendar instance
                    String orderID;
                    String CUSTOMERCODE;
                    String processedCustomerCode = processCustomerCode(customercode);

                    if (customercode == null) {
                        orderID = processedCustomerCode.toUpperCase() + outletid + String.valueOf(generateRandomOrderID()) + "-M";
                        CUSTOMERCODE = processedCustomerCode;                        CUSTOMERCODE = customercode;
                    } else {
                        if (outletID == null) {
                            orderID = processedCustomerCode.toUpperCase() + outletid + String.valueOf(generateRandomOrderID()) + "-M";
                            CUSTOMERCODE = customercode;
                        } else {
                            orderID = processedCustomerCode.toUpperCase() + outletid + String.valueOf(generateRandomOrderID()) + "-M";
                            CUSTOMERCODE = customercode;
                        }


                    }


                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (isOnline()) {
                        System.out.println("The repeat order size is:  " + selectedproduct.size());


                        //  Set<String> uniqueEntries = new HashSet<>();

                        if (onlineProductBeanList.size() == 0) {
                            Set<String> uniqueKeys = new HashSet<>(); // Track unique entries for the first case

                            for (Map.Entry<String, String> entry : selectedproduct) {
                                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
                                if (cursor.getCount() != 0) {
                                    while (cursor.moveToNext()) {
                                        String productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                        String itemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                        String agencyCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));

                                        String uniqueKey = productID + "|" + itemCode + "|" + agencyCode;

                                        // Check if the unique key already exists in the HashSet
                                        if (!uniqueKeys.contains(uniqueKey)) {
                                            uniqueKeys.add(uniqueKey); // Add the unique key to the HashSet

                                            // Add the data to the lists
                                            onlineProductID.add(productID);
                                            onlineItemCode.add(itemCode);
                                            onlinelistagencyids.add(agencyCode);
                                            onlineReqQtys.add(entry.getValue());
                                        }
                                    }
                                }
                                cursor.close();
                            }
                        } else {
                            Set<String> uniqueEntries = new HashSet<>(); // Track unique entries for the second case

                            for (OnlineProductBean onlineProductBean : onlineProductBeanList) {
                                for (Map.Entry<String, String> entry : selectedproduct) {
                                    if (entry.getKey().equals(onlineProductBean.getProductName())) {
                                        String uniqueKey = onlineProductBean.getProductId() + "|" + onlineProductBean.getItemCode() + "|" + onlineProductBean.getAgencydid() + "|" + entry.getValue();

                                        // Check if this combination has already been added to prevent duplicates
                                        if (!uniqueEntries.contains(uniqueKey)) {
                                            uniqueEntries.add(uniqueKey);  // Add to set to track unique combinations

                                            // Add the data to the lists
                                            onlineProductID.add(onlineProductBean.getProductId());
                                            onlineItemCode.add(onlineProductBean.getItemCode());
                                            onlinelistagencyids.add(onlineProductBean.getAgencydid());
                                            onlineReqQtys.add(entry.getValue());
                                        }
                                    }
                                }
                            }

                            // Additionally, make sure to add entries from `selectedproduct` that are not in `onlineProductBeanList`
                            for (Map.Entry<String, String> entry : selectedproduct) {
                                boolean found = false;
                                for (OnlineProductBean onlineProductBean : onlineProductBeanList) {
                                    if (entry.getKey().equals(onlineProductBean.getProductName())) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (!found) {
                                    Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
                                    if (cursor.getCount() != 0) {
                                        while (cursor.moveToNext()) {
                                            String productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                            String itemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                            String agencyCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));

                                            String uniqueKey = productID + "|" + itemCode + "|" + agencyCode;

                                            if (!uniqueEntries.contains(uniqueKey)) {
                                                uniqueEntries.add(uniqueKey);

                                                onlineProductID.add(productID);
                                                onlineItemCode.add(itemCode);
                                                onlinelistagencyids.add(agencyCode);
                                                onlineReqQtys.add(entry.getValue());
                                            }
                                        }
                                    }
                                    cursor.close();
                                }
                            }
                        }


                        // Convert sets back to lists
//                        onlineProductID = new ArrayList<>(onlineProductIDsSet);
//                        onlineItemCode = new ArrayList<>(onlineItemCodesSet);
//                        onlinelistagencyids = new ArrayList<>(onlineListAgencyIdsSet);
//                        onlineReqQtys = new ArrayList<>(onlineReqQtysSet);

                        syncOrders(orderID, dateFormat.format(date), CUSTOMERCODE,selectedDate);
                    } else {
                        for (Map.Entry<String, String> entry : selectedproduct) {
                            Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
                            if (cursor.getCount() != 0) {
                                while (cursor.moveToNext()) {
                                    productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                                    ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                                    agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                                    productIdQty.add(new ProductInfo(productID, agencycode, ItemCode, entry.getValue()));
                                }
                            }
                            cursor.close();
                        }

                        if (outletID == null) {
                            submitOrderDB.submitDetails(orderID, userID, vanID, outletid, productIdQty, "Not Synced", CUSTOMERCODE, dateFormat.format(date),selectedDate,leadTime);

                        } else {
                            submitOrderDB.submitDetails(orderID, userID, vanID, outletID, productIdQty, "Not Synced", CUSTOMERCODE, dateFormat.format(date),selectedDate,leadTime);
                        }
                        if (customercode == null) {
                            Intent intent = new Intent(RepeatAddQuantity.this, DeliveryActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(RepeatAddQuantity.this, AddItemsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(RepeatAddQuantity.this, "Order Cannot be created, Add items!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        datePickerDialog.show();
    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                // You can leave this empty if you handle the date selection in the OK button's listener
            };

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }
    @SuppressLint("Range")
    private void displayAllItemsById(String agencycode, String cust_code) {
        listproduct.clear();
        Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agencycode, cust_code.toLowerCase(),outletid, leadTime);
        if (cursor.getCount() == 0) {
            // Disable UI elements when no products are found
            searchProductLayout.setEnabled(false);
            spinnerproducts.setEnabled(false);
            spinnerproducts.setFocusable(false);
            spinnerproducts.setFocusableInTouchMode(false);
            spinnerproducts.setText("");

            selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
            spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
            searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));
            dismissProgressDialog();
            Toast.makeText(this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            listproduct.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));
        }

        // Enable UI elements when products are found
        searchProductLayout.setEnabled(true);
        spinnerproducts.setEnabled(true);
        spinnerproducts.setFocusable(true);
        spinnerproducts.setFocusableInTouchMode(true);
        spinnerproducts.setText("");

        selectProductTextview.setTextColor(getResources().getColor(R.color.black));
        spinnerproducts.setTextColor(getResources().getColor(R.color.black));
        searchproductIcons.setColorFilter(getResources().getColor(R.color.black));

        // Set up adapter for spinner
        endsWithArrayAdapter = new EndsWithArrayAdapter(this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
        spinnerproducts.setAdapter(endsWithArrayAdapter);
        spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String productName = listproduct.get(position);
                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(productName);
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    }
                }

                boolean alreadyExists = false;
                for (Map.Entry<String, String> entry : selectedproduct) {
                    if (entry.getKey().equals(productName)) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    selectedproduct.add(new AbstractMap.SimpleEntry<>(productName, "0"));
                    selectedproduct = convertListToMapEntryList(selectedproduct);

                    if (addQtyAdapter == null) {
                        addQtyAdapter = new AddQtyAdapter2(RepeatAddQuantity.this, selectedproduct);
                     //   addQtyAdapter.setQuantityChangeListener((AddQtyAdapter2.QuantityChangeListener) RepeatAddQuantity.this);
                        addQtyAdapter.setQuantityChangeListener(new AddQtyAdapter2.QuantityChangeListener() {
                            @Override
                            public void onTotalQuantityChanged(int totalQuantity) {
                                // Update UI or perform actions based on totalQuantity change
                                // Example: Update TextView showing total quantity
                                qtycount.setText("#Quantity: " + totalQuantity);
                            }

                            @Override
                            public void onTotalItemChanged(int totalItems) {
                                // Update UI or perform actions based on totalItems change
                                // Example: Update TextView showing total item count
                                itemcount.setText("#Item Count: " + totalItems);
                            }
                        });
                        recyclerView.setAdapter(addQtyAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RepeatAddQuantity.this));
                    } else {
                        addQtyAdapter.updateData(selectedproduct);
                    }

                    // Debugging log
                    for (Map.Entry<String, String> entry : selectedproduct) {
                        Log.d("ProductList", "Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
                    }

                    //initializeSwipeToDelete();
                }

                cursor.close();
            }
        });

        cursor.close();
        selectedproduct = convertListToMapEntryList(selectedproduct);
        if (addQtyAdapter != null) {
            addQtyAdapter.updateData(selectedproduct);
        }
        dismissProgressDialog();
    }


    private void showLeadTimeDialog() {
        // Create an AlertDialog.Builder
        List<String> leadtimes = new LinkedList<>();

        Cursor cursor = itemsByAgencyDB.getLeadTimes();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String leadTime = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_LEAD_TIME));
                if (!leadtimes.contains(leadTime)) {
                    leadtimes.add(leadTime);
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout/view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_leadtime, null);

        // Set the custom layout as the dialog's view
        builder.setView(dialogView);
        builder.setCancelable(false);
        // Get the RadioGroup from the inflated layout
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupLeadTime);

        // Dynamically create radio buttons based on the leadtimes list
        for (int i = 0; i < leadtimes.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(leadtimes.get(i));
            radioGroup.addView(radioButton);
        }

        // Add the buttons
        builder.setTitle("Choose Lead-time for the products");
        builder.setPositiveButton("OK", null); // Set to null. We override later to prevent auto-dismiss
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i = new Intent(RepeatAddQuantity.this, AddItemsActivity.class);
                startActivity(i);
            }
        });

        // Create the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the positive button click listener to prevent dismissing the dialog when leadTime is null
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = dialogView.findViewById(selectedId);

                if (selectedRadioButton != null) {
                    leadTime = selectedRadioButton.getText().toString();
                    // Handle the selected lead time (e.g., display it, use it somewhere, etc.)
                    RepeatProducts(outletid,leadTime);
                    Toast.makeText(getApplicationContext(), "Selected Lead Time: " + leadTime, Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss the dialog if a lead time is selected
                } else {
                    // Show a toast message to prompt the user to select a lead time
                    Toast.makeText(getApplicationContext(), "Please select a lead time!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint({"Range", "StaticFieldLeak"})
    private void syncOrders(String orderId,String date,String CustomerCode,String selectedDate) {

        //showProgressDialog();
        // Create request parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("orderid", orderId);
        params.put("user_id", userID);
        params.put("van_id", vanID);
        // Join product IDs
        String joinedProductIds = String.join(",", onlineProductID);
        params.put("item_ids", joinedProductIds);

// Join item codes
        String joinedItemCodes = String.join(",", onlineItemCode);
        params.put("item_codes", joinedItemCodes);

        String joinedAgencyIds = String.join(",", onlinelistagencyids);
        params.put("agency_id", joinedAgencyIds);
// Join quantities
        /*StringBuilder quantitiesBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : selectedproduct) {
            if (quantitiesBuilder.length() > 0) {
                quantitiesBuilder.append(",");
            }
            quantitiesBuilder.append(entry.getValue());
        }*/
        String joinedQuantities=String.join(",",onlineReqQtys);
        params.put("quantities", joinedQuantities);



        params.put("outlet_id", outletid);
        params.put("ordered_datetime", date);
        params.put("orderStatus", "NEW ORDER");
        params.put("createdBy", userID);
        params.put("expectedDelivery",selectedDate);
        params.put("lead_time",leadTime);
        System.out.println("params"+params);
        // Create the network request
        String url = ApiLinks.submitOrder;
        Call<OrderDetailsResponse> updateCall = apiInterface.submitOrder(url, params);

        // Associate each request with the corresponding database row
        updateCall.enqueue(new Callback<OrderDetailsResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsResponse> call, Response<OrderDetailsResponse> response) {
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    // Extract data from the response if needed
                    // Update the corresponding database row
                    //  Toast.makeText(AddQuantity.this, "Order Success", Toast.LENGTH_SHORT).show();
                    //     submitOrderDB.submitDetails(orderID, userID, vanID,  outletID, productIdQty,"Not Synced", dateFormat.format(date));
                    submitOrderDB.onlineSubmitOrderDetails(orderId, userID, vanID,outletid,joinedProductIds,joinedAgencyIds,joinedItemCodes,joinedQuantities,"synced","online",CustomerCode,date,selectedDate,"0");
                    //Toast.makeText(AddQuantity.this, "Order SuccessFull "+orderId, Toast.LENGTH_SHORT).show();
                    onlineProductID.clear();
                    onlinelistagencyids.clear();
                    onlineItemCode.clear();
                    onlineReqQtys.clear();
                    Intent intent=new Intent(RepeatAddQuantity.this,AddItemsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsResponse> call, Throwable t) {
                displayAlert("Alert", t.getMessage());
            }
        });


    }
    private void RepeatProducts(String outletId, String leadTime) {
        selectedproduct.clear();
        Cursor cursor = null;
        try {
            cursor = submitOrderDB.readLatestDataByOutletID(outletId, leadTime);
            if (cursor != null && cursor.getCount() > 0) {
                Map<String, String> latestQuantities = new HashMap<>();

                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String productIdString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    @SuppressLint("Range") String quantityString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));
                    @SuppressLint("Range") String agencyIdString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_AGENCYID));
                    String[] productIds = productIdString.split(",");
                    String[] quantities = quantityString.split(",");
                    agencyIds = agencyIdString.split(",");

                    for (int i = 0; i < productIds.length; i++) {
                        String productId = productIds[i].trim();
                        String quantity = quantities[i].trim();
                        latestQuantities.put(productId, quantity);
                    }
                }

                for (Map.Entry<String, String> entry : latestQuantities.entrySet()) {
                    String productId = entry.getKey();
                    String quantity = entry.getValue();
                    Cursor cursor1 = null;
                    try {
                        cursor1 = itemsByAgencyDB.readDataByCustomerCodeandproID(customercode, productId);
                        if (cursor1 != null && cursor1.getCount() > 0) {
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range") String itemName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                                selectedproduct.add(new AbstractMap.SimpleEntry<>(itemName, quantity));
                            }
                        }
                    } finally {
                        if (cursor1 != null) {
                            cursor1.close();
                        }
                    }
                }

                addQtyAdapter = new AddQtyAdapter2(this, selectedproduct);
                addQtyAdapter.setQuantityChangeListener(RepeatAddQuantity.this);
                recyclerView.setAdapter(addQtyAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
               // initializeSwipeToDelete();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<Map.Entry<String, String>> convertListToMapEntryList(List<Map.Entry<String, String>> list) {
        Set<String> existingKeys = new HashSet<>();
        for (Map.Entry<String, String> entry : selectedproduct) {
            existingKeys.add(entry.getKey().trim());
        }

        Log.d("convertListToMapEntryList", "Existing keys before adding new entries: " + existingKeys);

        for (Map.Entry<String, String> entry : list) {
            String keyToCheck = entry.getKey().trim();
            Log.d("convertListToMapEntryList", "Checking key: " + keyToCheck);

            if (!existingKeys.contains(keyToCheck)) {
                selectedproduct.add(new AbstractMap.SimpleEntry<>(keyToCheck, "0"));
                existingKeys.add(keyToCheck);
                Log.d("convertListToMapEntryList", "Added entry: " + keyToCheck);
            } else {
                Log.d("convertListToMapEntryList", "Entry already exists: " + keyToCheck);
            }
        }

        Log.d("convertListToMapEntryList", "Final selectedproduct list: ");
        for (Map.Entry<String, String> entry : selectedproduct) {
            Log.d("convertListToMapEntryList", "Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());
        }

        return selectedproduct;
    }
    private void initializeSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
               // addQtyAdapter.removeItem( position);
                selectedproduct.remove(position);
                addQtyAdapter.updateData(selectedproduct);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        SwipeToDeleteCallback(AddQtyAdapter2 addproductAdapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();

            // Check if the itemPosition is valid
            if (itemPosition >= 0 && itemPosition < selectedproduct.size()) {
                // Get the values of the deleted item before removing it
                String deletedItemName = selectedproduct.get(itemPosition).getKey();
                String deletedItemQuantity = selectedproduct.get(itemPosition).getValue();

                // Call removeItem method directly
                addQtyAdapter.removeItem( itemPosition);
            //    addQtyAdapter.updateData(selectedproduct);
                // Show Snackbar with the appropriate message
                Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                        "Item deleted.", Snackbar.LENGTH_LONG);

                // Handle Snackbar action if needed
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Undo action or handle cancellation if required
                        addQtyAdapter.restoreItem(itemPosition, deletedItemName,deletedItemQuantity);
                    }
                });

                snackbar.show();
            }
        }






        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(RepeatAddQuantity.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(RepeatAddQuantity.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightLabel(getString(R.string.action_delete))
                    .setSwipeRightLabelColor(Color.WHITE)
                    .addSwipeLeftLabel(getString(R.string.action_delete))
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        listagency.clear();
        listproduct.clear();
        selectedproduct.clear();
        submittedorder.clear();
        productIdQty.clear();
        onlineProductID.clear();
        onlineItemCode.clear();
        onlinelistagencyids.clear();
        onlineReqQtys.clear();
        prodqty.clear();
        getProdqty.clear();
        onlineProductBeanList.clear();
        leadTime=null;
        outletid=null;
        listagency.clear();
        listproduct.clear();
        selectedproduct.clear();
        spinneragecny=null;
        spinnerproducts=null;
        adapter=null;
        listagency=null;
        listproduct=null;
        addQtyAdapter=null;
        recyclerView=null;
        allAgencyDetailsDB=null;
        itemsByAgencyDB=null;
        agencycode=null;
        agencyID=null;
        productID=null;
        quantity=null;
        ItemCode=null;
        selectedproduct=null; ;
        submittedorder=null;
        productIdQty=null ;
        prodqty=null;
        getProdqty  =null;
        submit=null;
        submitOrderDB=null;
    }

}
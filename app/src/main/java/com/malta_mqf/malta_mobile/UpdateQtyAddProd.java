package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.MainActivity.userID;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter;
import com.malta_mqf.malta_mobile.Adapter.AddQtyAdapter2;
import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdateQtyAddProd extends AppCompatActivity implements AddQtyAdapter2.QuantityChangeListener {
    AutoCompleteTextView spinneragecny, spinnerproducts;
    GetCusOutletAgencyProductAdapter adapter;
    List<String> listagency;
    List<String> listproduct;
    AddQtyAdapter2 addQtyAdapter;
    RecyclerView recyclerView;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    String agencycode;
    String agencyID, productID, quantity, ItemCode;
    List<Map.Entry<String, String>> selectedproduct;
    List<String> submittedorder;

    Set<ProductInfo> productIdQty;


    List<String> prodqty;
    List<String> getProdqty;
    Button submit;
    SubmitOrderDB submitOrderDB;
    String orderID, outletid, cus_code, leadTime;
    EndsWithArrayAdapter endsWithArrayAdapter;
    TextView outletname_header, selectProductTextview;
    Toolbar toolbar;
    ImageView searchitembyagency, searchproductIcons;
    LinearLayout searchProductLayout;
    TextView qtycount, itemcount;
    SearchView searchView;
    private ItemTouchHelper itemTouchHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_qty_add_prod);
        listagency = new LinkedList<>();
        listproduct = new LinkedList<>();
        selectedproduct = new LinkedList<>();
        submittedorder = new LinkedList<>();

        productIdQty = new LinkedHashSet<>();


        prodqty = new LinkedList<>();
        getProdqty = new LinkedList<>();
        spinneragecny = findViewById(R.id.spinnerLeft);
        spinnerproducts = findViewById(R.id.spinnerRight);
        recyclerView = findViewById(R.id.listaddproducts);
        searchproductIcons = findViewById(R.id.search_icon2);
        searchProductLayout = findViewById(R.id.searchproductLayout);
        selectProductTextview = findViewById(R.id.selectOutletTextView);
        searchView = findViewById(R.id.searchView);
        qtycount = findViewById(R.id.qtycount);
        itemcount = findViewById(R.id.itemcount);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        submit = findViewById(R.id.submitorder);
        submitOrderDB = new SubmitOrderDB(this);
        submit.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        outletname_header = findViewById(R.id.outletname_header);
        searchitembyagency = findViewById(R.id.search_icon);

        orderID = getIntent().getStringExtra("orderID");
        System.out.println("OrderID in repeat add qty: " + orderID);
        outletname_header.setText(getIntent().getStringExtra("orderID"));
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("UPDATE ORDER");
        spinneragecny.setMaxLines(1);
        spinneragecny.setMovementMethod(new ScrollingMovementMethod());
        spinnerproducts.setMaxLines(1);
        spinnerproducts.setMovementMethod(new ScrollingMovementMethod());


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
        RepeatProducts(orderID);
        //   getAllAgency();
        displayAllAgency();


        searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                String agency = spinneragecny.getText().toString().trim();
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
                    Log.d("agencycode", agencycode);
                }
                cursor.close();
                // getAllItemById();
                displayAllItemsById(agencycode, cus_code, leadTime);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addQtyAdapter.filter(newText);
                return false;
            }
        });

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

        spinnerproducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerproducts.showDropDown();
            }
        });

        spinnerproducts.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    spinnerproducts.showDropDown();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                //  String orderID = customercode + outletID + String.valueOf(generateRandomOrderID());
                System.out.println("update order products after submit:" + selectedproduct);
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                // Iterate over selected products
                for (Map.Entry<String, String> entry : selectedproduct) {
                    Cursor cursor = itemsByAgencyDB.readProdcutDataByName(entry.getKey());
                    if (cursor.getCount() != 0) {
                        while (cursor.moveToNext()) {
                            productID = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                            ItemCode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_CODE));
                            agencycode = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                            if (!entry.getValue().equals("0") || entry.getValue().isEmpty()) {
                                productIdQty.add(new ProductInfo(productID, agencycode, ItemCode, entry.getValue()));

                            }
                        }
                    }
                    cursor.close();
                }

                submitOrderDB.updateOrder(orderID, userID, vanID, outletid, productIdQty, null, null, "Not Synced", dateFormat.format(date));
                Intent intent = new Intent(UpdateQtyAddProd.this, ModifyOrder.class);
                startActivity(intent);
                finish();
                orderID = null;
                outletid = null;

                /*for (int i=0;i<10000;i++){
                    submitOrderDB.submitDetails("OrderID0AQM3"+i, userID, "van01"+i,  outletID+i, productIdQty, dateFormat.format(date));
                }*/
                //  display();
            }
        });
    }

    @Override
    public void onTotalQuantityChanged(int totalQuantity) {
        TextView totalQtyTextView = findViewById(R.id.qtycount);

        totalQtyTextView.setText("#Quantity: " + totalQuantity);

    }

    @Override
    public void onTotalItemChanged(int totalItems) {
        TextView itemCount = findViewById(R.id.itemcount);
        itemCount.setText("#Item Count: " + addQtyAdapter.getItemCount());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

   /* private void display() {
        Cursor cursor = submitOrderDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String productId = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                System.out.println("product"+productId);
                Cursor cursor1 = itemsByAgencyDB.readProdcutDataByproductId(productId);
                while (cursor1.moveToNext()) {
                    @SuppressLint("Range") String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                    @SuppressLint("Range") String agencyId = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));
                    System.out.println("productName: "+productName +"agencyId: "+agencyId);
                    Cursor cursor2 = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);
                    while (cursor2.moveToNext()) {
                        @SuppressLint("Range") String agencyName = cursor2.getString(cursor2.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));
                        System.out.println("agencyName"+agencyName);
                        System.out.println("Product Name: " + productName + ", Agency Name: " + agencyName);
                        submittedorder.add("Product Name: " + productName + ", Agency Name: " + agencyName);
                    }
                }
            }
        }
        cursor.close();
        System.out.println("the itmes"+submittedorder);
    }*/


    private int generateRandomOrderID() {
        int min = 1;
        int max = 100000;
        int random = (int) (Math.random() * (max - min + 1)) + min;
        return random;
    }
    /*    private void getAllAgency(){
            showProgressDialog();
            String url = ApiLinks.allAgencyDetails;

            Call<AllAgencyDetails> allAgencyDetailsCall = apiInterface.allAgencyDetails(url);
            allAgencyDetailsCall.enqueue(new Callback<AllAgencyDetails>() {

                @Override
                public void onResponse(Call<AllAgencyDetails> call, Response<AllAgencyDetails> response) {
                    dismissProgressDialog();
                    if (response.body().getStatus().equalsIgnoreCase("yes")) {
                        AllAgencyDetails allAgencyDetails = response.body();
                        List<AllAgencyDetailsResponse> allAgencyDetailsResponses = allAgencyDetails.getAgencyDetails();
                        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();

                        for (AllAgencyDetailsResponse allAgencyDetailsResponse : allAgencyDetailsResponses) {
                            boolean exists = false;
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
                                @SuppressLint("Range")
                                String customerIdFromDB = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_ID));
                                if (customerIdFromDB != null && customerIdFromDB.equalsIgnoreCase(allAgencyDetailsResponse.getId())) {
                                    exists = true;
                                    break;
                                }
                                cursor.moveToNext();
                            }

                            if (exists) {
                                // Update existing row
                                allAgencyDetailsDB.UpdateAgencyData(
                                        allAgencyDetailsResponse.getAgencyCode(),
                                        allAgencyDetailsResponse.getAgencyName(),
                                        allAgencyDetailsResponse.getId());
                                System.out.println("Updated row");
                            } else {
                                // Insert new row
                                allAgencyDetailsDB.addAgencyDetails(
                                        allAgencyDetailsResponse.getAgencyCode(),
                                        allAgencyDetailsResponse.getAgencyName(),
                                        allAgencyDetailsResponse.getId());
                                System.out.println("Inserted new row");
                            }
                        }
                    }
                }


                @Override
                public void onFailure(Call<AllAgencyDetails> call, Throwable t) {
                    dismissProgressDialog();
                    // displayAlert("Alert", t.getMessage());
                }
            });

        }*/

    @SuppressLint("Range")
    private void displayAllAgency() {

        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            return;
        } else while (cursor.moveToNext()) {
            listagency.add(cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME)));
        }


        endsWithArrayAdapter = new EndsWithArrayAdapter(UpdateQtyAddProd.this, R.layout.list_item_text, R.id.list_textView_value, listagency);
        spinneragecny.setAdapter(endsWithArrayAdapter);
        cursor.close();
    }


    /* private void getAllItemById(){
         showProgressDialog();
         listproduct.clear();
         prodqty.clear();
         Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
         if (cursor.getCount() != 0) {
             while (cursor.moveToNext()) {
               String  agencycode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));


                 String url = ApiLinks.allItemDetailsById + "?agency_id=" + agencycode;
         System.out.println("url : " + url);
         Call<AllItemDeatilsById> allItemDeatilsByIdCall = apiInterface.allItemDetailsById(url);
         allItemDeatilsByIdCall.enqueue(new Callback<AllItemDeatilsById>() {

             @Override
             public void onResponse(Call<AllItemDeatilsById> call, Response<AllItemDeatilsById> response) {
                 dismissProgressDialog();
                 if (response.body().getStatus().equalsIgnoreCase("yes")) {
                     AllItemDeatilsById allItemDeatilsById = response.body();
                     List<AllItemDetailResponseById> allItemDetailResponseByIds = allItemDeatilsById.getItemDetails();
                         Cursor cursor = itemsByAgencyDB.readAllData();

                     for (AllItemDetailResponseById allItemDetailResponseById : allItemDetailResponseByIds) {
                         boolean exists = false;
                         cursor.moveToFirst();
                         while (!cursor.isAfterLast()) {
                             @SuppressLint("Range")
                             String itemDB = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                             if (itemDB != null && itemDB.equalsIgnoreCase(allItemDetailResponseById.getId())) {
                                 exists = true;
                                 break;
                             }
                             cursor.moveToNext();
                         }

                         if (exists) {
                             // Update existing row
                             //  String itemname,String itemcode,String itemId,String itemcatgid,String productdecp
                             itemsByAgencyDB.UpdateItemData(
                                     allItemDetailResponseById.getItemName(),
                                     allItemDetailResponseById.getItemCode(),
                                     allItemDetailResponseById.getId(),
                                     allItemDetailResponseById.getItemCategoryId(),
                                     allItemDetailResponseById.getAgencyCode(),
                                     allItemDetailResponseById.getAgencyId(),
                                     allItemDetailResponseById.getProductDescription()
                             );
                             System.out.println("Updated row");
                         } else {
                             // Insert new row
                             itemsByAgencyDB.addItemDetails(
                                     allItemDetailResponseById.getItemName(),
                                     allItemDetailResponseById.getItemCode(),
                                     allItemDetailResponseById.getId(),
                                     allItemDetailResponseById.getItemCategoryId(),
                                     allItemDetailResponseById.getAgencyCode(),
                                     allItemDetailResponseById.getAgencyId(),
                                     allItemDetailResponseById.getProductDescription()
                             );
                             System.out.println("Inserted new row");
                         }
                     }
                 }

             }


             @Override
             public void onFailure(Call<AllItemDeatilsById> call, Throwable t) {
                 dismissProgressDialog();
                 // displayAlert("Alert", t.getMessage());
             }
         });
             }
         }
     }
 */
    @SuppressLint("Range")
    private void displayAllItemsById(String agencycode, String cus_code, String leadTime) {

        listproduct.clear();
        Cursor cursor = itemsByAgencyDB.checkIfItemExistsByCustomerCodeAndLeadTime(agencycode, cus_code, outletid, leadTime);
        if (cursor.getCount() == 0) {
            searchProductLayout.setEnabled(false);
            spinnerproducts.setEnabled(false);
            spinnerproducts.setFocusable(false);
            spinnerproducts.setFocusableInTouchMode(false); // Prevents the view from gaining focus on touch events
            spinnerproducts.setText("");

            selectProductTextview.setTextColor(getResources().getColor(R.color.listitem_gray));
            spinnerproducts.setTextColor(getResources().getColor(R.color.listitem_gray));
            searchproductIcons.setColorFilter(getResources().getColor(R.color.listitem_gray));

            Toast.makeText(this, "No Products Found for this Agency!", Toast.LENGTH_SHORT).show();
            return;
        } else while (cursor.moveToNext()) {
            listproduct.add(cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME)));


            searchProductLayout.setEnabled(true);
            spinnerproducts.setEnabled(true);
            spinnerproducts.setFocusable(true);
            spinnerproducts.setFocusableInTouchMode(true); // Prevents the view from gaining focus on touch events
            spinnerproducts.setText("");

            selectProductTextview.setTextColor(getResources().getColor(R.color.black));
            spinnerproducts.setTextColor(getResources().getColor(R.color.black));
            searchproductIcons.setColorFilter(getResources().getColor(R.color.black));
            endsWithArrayAdapter = new EndsWithArrayAdapter(UpdateQtyAddProd.this, R.layout.list_item_text, R.id.list_textView_value, listproduct);
            spinnerproducts.setAdapter(endsWithArrayAdapter);
            spinnerproducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @SuppressLint("Range")
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
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
                        selectedproduct = convertListToMapEntryList(selectedproduct);
                        if (addQtyAdapter == null) {
                            addQtyAdapter = new AddQtyAdapter2(UpdateQtyAddProd.this, selectedproduct);
                            //  addQtyAdapter.setQuantityChangeListener(UpdateQtyAddProd.this);
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
                            recyclerView.setLayoutManager(new LinearLayoutManager(UpdateQtyAddProd.this));
                        } else {
                            addQtyAdapter.updateData(selectedproduct);
                        }


                        // Print product name and quantity
                        for (Map.Entry<String, String> entry : selectedproduct) {
                            System.out.println("Product Name: " + entry.getKey() + ", Quantity: " + entry.getValue());

                        }

                        // initializeSwipeToDelete();
                    }

                    cursor.close();
                }

            });
        }
        cursor.close();
        selectedproduct = convertListToMapEntryList(selectedproduct);
        if (addQtyAdapter != null) {
            addQtyAdapter.updateData(selectedproduct);
        }
    }

    @SuppressLint("Range")
    private void RepeatProducts(String orderID) {
        Cursor cursor = null;
        try {
            cursor = submitOrderDB.readDataByOrderID(orderID);
            if (cursor != null && cursor.getCount() > 0) {
                Map<String, String> latestQuantities = new HashMap<>(); // Map to store the latest quantity for each product ID

                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String productIdString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_PRODUCTID));
                    @SuppressLint("Range") String quantityString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));
                    outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                    cus_code = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_CUSTOMER_CODE_AFTER_DELIVER));
                    leadTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_LEAD_TIME));
                    System.out.println("outletid..............." + outletid);
                    String[] productIds = productIdString.split(","); // Split the comma-separated product IDs
                    String[] quantities = quantityString.split(","); // Split the comma-separated quantities

                    // Iterate over each product ID and quantity pair
                    for (int i = 0; i < productIds.length; i++) {
                        String productId = productIds[i].trim(); // Remove leading/trailing whitespace
                        String quantity = quantities[i].trim(); // Remove leading/trailing whitespace

                        // Update the latest quantity for the current product ID
                        latestQuantities.put(productId, quantity);
                    }
                }

                // Add the latest quantity for each product to the selectedproduct list
                for (Map.Entry<String, String> entry : latestQuantities.entrySet()) {
                    String productId = entry.getKey();
                    String quantity = entry.getValue();
                    Cursor cursor1 = null;
                    try {
                        cursor1 = itemsByAgencyDB.readDataByCustomerCodeandproID(cus_code, productId);
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


                addQtyAdapter = new AddQtyAdapter2(UpdateQtyAddProd.this, selectedproduct);
                addQtyAdapter.setQuantityChangeListener(UpdateQtyAddProd.this);
                recyclerView.setAdapter(addQtyAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(UpdateQtyAddProd.this));

                //  initializeSwipeToDelete();
            } else {
                Toast.makeText(this, "There are no products for this outlet to repeat the order", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any potential exceptions here
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        System.out.println("update order products:" + selectedproduct);
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
                selectedproduct.remove(position);
                addQtyAdapter.updateData(selectedproduct);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        listagency.clear();
        listproduct.clear();
        selectedproduct.clear();
        submittedorder.clear();
        productIdQty.clear();
        prodqty.clear();
        getProdqty.clear();
        spinneragecny = null;
        spinnerproducts = null;
        adapter = null;
        listagency = null;
        listproduct = null;
        addQtyAdapter = null;
        recyclerView = null;
        allAgencyDetailsDB = null;
        itemsByAgencyDB = null;
        agencycode = null;
        agencyID = null;
        productID = null;
        quantity = null;
        ItemCode = null;
        selectedproduct = null;
        submittedorder = null;
        productIdQty = null;
        prodqty = null;
        getProdqty = null;
        submit = null;
        submitOrderDB = null;
    }

    class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        SwipeToDeleteCallback(AddQtyAdapter addproductAdapter) {
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
                addQtyAdapter.removeItem(itemPosition);

                // Show Snackbar with the appropriate message
                Snackbar snackbar = Snackbar.make(viewHolder.itemView,
                        "Item deleted.", Snackbar.LENGTH_LONG);

                // Handle Snackbar action if needed
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Undo action or handle cancellation if required
                        addQtyAdapter.restoreItem(itemPosition, deletedItemName, deletedItemQuantity);
                    }
                });

                snackbar.show();
            }
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(UpdateQtyAddProd.this, R.color.recycler_view_item_swipe_right_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(UpdateQtyAddProd.this, R.color.recycler_view_item_swipe_right_background))
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

}
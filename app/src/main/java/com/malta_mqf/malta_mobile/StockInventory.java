package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Adapter.EndsWithArrayAdapter;
import com.malta_mqf.malta_mobile.Adapter.GetCusOutletAgencyProductAdapter;
import com.malta_mqf.malta_mobile.Adapter.StockAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.Model.StockBean;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StockInventory extends AppCompatActivity {
    private RecyclerView recyclerView;
    Toolbar toolbar;
    AutoCompleteTextView spinner;
    GetCusOutletAgencyProductAdapter adapter;
    Set<StockBean> productIdQty;
    List<StockBean> finaltotal;

    private ALodingDialog aLodingDialog;
    AllAgencyDetailsDB allAgencyDetailsDB;
    ItemsByAgencyDB itemsByAgencyDB;
    List<String> listagency;
    //  TotalApprovedOrderBsdOnItem totalApprovedOrderBsdOnItemDB;
    StockAdapter stockAdapter;
    StockDB stockDB;
    ImageView searchitembyagency;
    EndsWithArrayAdapter endsWithArrayAdapter;
    SearchView searchView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_inventory);
         productIdQty = new LinkedHashSet<>();
         finaltotal=new LinkedList<>();
        listagency=new LinkedList<>();

        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        aLodingDialog=new ALodingDialog(this);
        stockDB = new StockDB(this);
        //  totalApprovedOrderBsdOnItemDB = new TotalApprovedOrderBsdOnItem(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("VAN STOCK");
        recyclerView = findViewById(R.id.recyclerView);
        searchitembyagency=findViewById(R.id.search_icon);
        searchView = findViewById(R.id.searchView);
        spinner = findViewById(R.id.spinner);

        displayAllAgency();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(stockAdapter!=null){
                    stockAdapter.filter(newText);
                }

                return false;
            }
        });
       /* searchitembyagency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aLodingDialog.show();
                productIdQty.clear();
                finaltotal.clear();
                String agency_name = spinner.getText().toString().trim();
                if (agency_name.equals("All")) {
                    // If "All Agencies" is selected, display items for all agencies


                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            displayAllItems();
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable,3000);
                } else {
                    System.out.println(agency_name);
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency_name);
                    if (cursor.getCount() == 0) {
                        aLodingDialog.cancel();
                        Toast.makeText(StockInventory.this, "No data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);
                        if (cursor1.getCount() == 0) {
                            aLodingDialog.cancel();
                            Toast.makeText(StockInventory.this, "No data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Move the cursor1 to the first row before accessing its data
                        while (cursor1.moveToNext()) {
                            @SuppressLint("Range")
                            String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                            @SuppressLint("Range")
                            String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));

                            Cursor cursor2 = stockDB.readonproductid(productID);
                            if (cursor2 != null && cursor2.getCount() > 0) {
                                // Move the cursor2 to the first row before accessing its data
                                while (cursor2.moveToNext()) {
                                    @SuppressLint("Range") String productId = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_PRODUCTID));
                                    @SuppressLint("Range") String prodcutName = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_PRODUCTNAME));
                                    @SuppressLint("Range") String avlQTY = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                    productIdQty.add(new StockBean(productId, prodcutName, avlQTY));

                                }
                                cursor2.close();
                            }
                        }
                        cursor1.close();
                    }
                    cursor.close();


                    for (StockBean bean : productIdQty) {
                        String pID = bean.getProductID();
                        String pName = bean.getProductName();
                        String avlqty = bean.getQty();
                        boolean productExists = false;

                        for (StockBean productBean : finaltotal) {
                            if (productBean.getProductID().equals(pID)) {
                                // Update quantities of existing product in finaltotal
                                productExists = true;
                                break;
                            }
                        }

                        if (!productExists) {
                            // If product does not exist in finaltotal, add it
                            finaltotal.add(new StockBean(pID, pName, avlqty));
                            finaltotal = convertListToMapEntryList(finaltotal);
                        }
                    }
                    Collections.sort(finaltotal, new Comparator<StockBean>() {
                        @Override
                        public int compare(StockBean o1, StockBean o2) {
                            // Prioritize "PENDING FOR DELIVERY" over "DELIVERED"
                            if (Integer.parseInt(o1.getQty())>Integer.parseInt(o2.getQty())) {
                                return -1; // o1 comes before o2
                            } else if (Integer.parseInt(o2.getQty())>Integer.parseInt(o1.getQty())) {
                                return 1; // o2 comes before o1
                            }
                            return 0; // Keep original order if the statuses are the same
                        }
                    });

                    // Initialize the adapter outside of the loops
                    if(stockAdapter==null) {
                        stockAdapter = new StockAdapter(finaltotal);

                        recyclerView.setLayoutManager(new LinearLayoutManager(StockInventory.this));
                        recyclerView.setAdapter(stockAdapter);
                        stockAdapter.notifyDataSetChanged();
                    }else{
                        stockAdapter.updateData(finaltotal);
                    }
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable,2000);

                }
            }
        });*/

        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                aLodingDialog.show();
                productIdQty.clear();
                finaltotal.clear();
                String agency_name = spinner.getText().toString().trim();
                if (agency_name.equals("All")) {
                    // If "All Agencies" is selected, display items for all agencies


                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            displayAllItems();
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable,3000);
                } else {
                    System.out.println(agency_name);
                    Cursor cursor = allAgencyDetailsDB.readAgencyDataByName(agency_name);
                    if (cursor.getCount() == 0) {
                        aLodingDialog.cancel();
                        Toast.makeText(StockInventory.this, "No data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Move the cursor to the first row before accessing its data
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range")
                        String agencyCode = cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
                        System.out.println(agencyCode);
                        Cursor cursor1 = itemsByAgencyDB.checkIfITemExists(agencyCode);
                        if (cursor1.getCount() == 0) {
                            aLodingDialog.cancel();
                            Toast.makeText(StockInventory.this, "No data", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Move the cursor1 to the first row before accessing its data
                        while (cursor1.moveToNext()) {
                            @SuppressLint("Range")
                            String productName = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                            @SuppressLint("Range")
                            String productID = cursor1.getString(cursor1.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));

                            Cursor cursor2 = stockDB.readonproductid(productID);
                            if (cursor2 != null && cursor2.getCount() > 0) {
                                // Move the cursor2 to the first row before accessing its data
                                while (cursor2.moveToNext()) {
                                    @SuppressLint("Range") String productId = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_PRODUCTID));
                                    @SuppressLint("Range") String prodcutName = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_PRODUCTNAME));
                                    @SuppressLint("Range") String avlQTY = cursor2.getString(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                                    productIdQty.add(new StockBean(productId, prodcutName, avlQTY));

                                }
                                cursor2.close();
                            }
                        }
                        cursor1.close();
                    }
                    cursor.close();


                    for (StockBean bean : productIdQty) {
                        String pID = bean.getProductID();
                        String pName = bean.getProductName();
                        String avlqty = bean.getQty();
                        boolean productExists = false;

                        for (StockBean productBean : finaltotal) {
                            if (productBean.getProductID().equals(pID)) {
                                // Update quantities of existing product in finaltotal
                                productExists = true;
                                break;
                            }
                        }

                        if (!productExists) {
                            // If product does not exist in finaltotal, add it
                            finaltotal.add(new StockBean(pID, pName, avlqty));
                            finaltotal = convertListToMapEntryList(finaltotal);
                        }
                    }
                    Collections.sort(finaltotal, new Comparator<StockBean>() {
                        @Override
                        public int compare(StockBean o1, StockBean o2) {
                            // Prioritize "PENDING FOR DELIVERY" over "DELIVERED"
                            if (Integer.parseInt(o1.getQty())>Integer.parseInt(o2.getQty())) {
                                return -1; // o1 comes before o2
                            } else if (Integer.parseInt(o2.getQty())>Integer.parseInt(o1.getQty())) {
                                return 1; // o2 comes before o1
                            }
                            return 0; // Keep original order if the statuses are the same
                        }
                    });

                    // Initialize the adapter outside of the loops
                    if(stockAdapter==null) {
                        stockAdapter = new StockAdapter(finaltotal);

                        recyclerView.setLayoutManager(new LinearLayoutManager(StockInventory.this));
                        recyclerView.setAdapter(stockAdapter);
                        stockAdapter.notifyDataSetChanged();
                    }else{
                        stockAdapter.updateData(finaltotal);
                    }
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            aLodingDialog.cancel();
                        }
                    };
                    handler.postDelayed(runnable,2000);

                }
            }
        });

        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.showDropDown();
            }
        });

        spinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.isShown()) {
                    v.postDelayed(() -> {
                        if (!isFinishing() && !isDestroyed()) {
                    spinner.showDropDown();
                        }
                    }, 100); // Small delay to ensure activity is in a valid state
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("Range")
    private void displayAllAgency(){
        listagency.clear();
       listagency.add("All");
        Cursor cursor = allAgencyDetailsDB.readAllAgencyData();
        if (cursor.getCount() == 0) {
            aLodingDialog.cancel();
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }else while (cursor.moveToNext()) {
            listagency.add(cursor.getString(cursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME)));
        }


        endsWithArrayAdapter=new EndsWithArrayAdapter(StockInventory.this,R.layout.list_item_text,R.id.list_textView_value,listagency);
        spinner.setAdapter(endsWithArrayAdapter);
        cursor.close();
    }


    @SuppressLint("Range")
    private void displayAllItems() {
        productIdQty.clear();
        finaltotal.clear();
        Cursor allAgenciesCursor = allAgencyDetailsDB.readAllAgencyData();
        if (allAgenciesCursor.getCount() == 0) {
            aLodingDialog.cancel();
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        while (allAgenciesCursor.moveToNext()) {
            String agencyCode = allAgenciesCursor.getString(allAgenciesCursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_CODE));
            Cursor itemsCursor = itemsByAgencyDB.checkIfITemExists(agencyCode);
            if (itemsCursor.getCount() > 0) {
                while (itemsCursor.moveToNext()) {
                    String productID = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    Cursor stockCursor = stockDB.readonproductid(productID);
                    if (stockCursor != null && stockCursor.getCount() > 0) {
                        while (stockCursor.moveToNext()) {
                            String productId = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_PRODUCTID));
                            String productName = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_PRODUCTNAME));
                            String avlQTY = stockCursor.getString(stockCursor.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
                            productIdQty.add(new StockBean(productId, productName, avlQTY));
                        }
                        stockCursor.close();
                    }
                }
            }
            itemsCursor.close();
        }
        allAgenciesCursor.close();

        for (StockBean bean : productIdQty) {
            String pID = bean.getProductID();
            String pName = bean.getProductName();
            String avlqty = bean.getQty();
            boolean productExists = false;

            for (StockBean productBean : finaltotal) {
                if (productBean.getProductID().equals(pID)) {
                    // Update quantities of existing product in finaltotal
                    productExists = true;
                    break;
                }
            }

            if (!productExists) {
                // If product does not exist in finaltotal, add it
                finaltotal.add(new StockBean(pID, pName, avlqty));
                finaltotal = convertListToMapEntryList(finaltotal);
            }
        }
        Collections.sort(finaltotal, new Comparator<StockBean>() {
            @Override
            public int compare(StockBean o1, StockBean o2) {
                // Prioritize "PENDING FOR DELIVERY" over "DELIVERED"
                if (Integer.parseInt(o1.getQty())>Integer.parseInt(o2.getQty())) {
                    return -1; // o1 comes before o2
                } else if (Integer.parseInt(o2.getQty())>Integer.parseInt(o1.getQty())) {
                    return 1; // o2 comes before o1
                }
                return 0; // Keep original order if the statuses are the same
            }
        });
        if(stockAdapter==null) {
            stockAdapter = new StockAdapter(finaltotal);
            recyclerView.setLayoutManager(new LinearLayoutManager(StockInventory.this));
            recyclerView.setAdapter(stockAdapter);
            stockAdapter.notifyDataSetChanged();
        }else{
            stockAdapter.updateData(finaltotal);
        }
    }
    private List<StockBean> convertListToMapEntryList(List<StockBean> list) {
        Set<String> existingKeys = new HashSet<>();
        for (StockBean entry : finaltotal) {
            existingKeys.add(entry.getProductName().trim());
        }

        Log.d("convertListToMapEntryList", "Existing keys before adding new entries: " + existingKeys);

        for (StockBean entry : list) {
            String keyToCheck = entry.getProductName().trim();
            Log.d("convertListToMapEntryList", "Checking key: " + keyToCheck);

            if (!existingKeys.contains(keyToCheck)) {
                finaltotal.add(new StockBean(entry.getProductID(), keyToCheck,entry.getDelQty()));
                existingKeys.add(keyToCheck);
                Log.d("convertListToMapEntryList", "Added entry: " + keyToCheck);
            } else {
                Log.d("convertListToMapEntryList", "Entry already exists: " + keyToCheck);
            }
        }


        return finaltotal;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        productIdQty.clear();
        finaltotal.clear();
        listagency.clear();

    }
}
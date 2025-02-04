package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB.COLUMN_PRODUCTID;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.Adapter.UpdateOrderAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.Order_history;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UpdateOrderActivity extends AppCompatActivity {

    private ImageView btnSelectDate;
    private DatePickerDialog datePickerDialog;
    private ListView listViewUpdateOrder;
    private OrderAdapter orderAdapter;
    private ListView listUpdate;
    private TextView selectedDateTextView;

    private SubmitOrderDB submitOrderDB;
    private ItemsByAgencyDB itemsByAgencyDB;
    private AllAgencyDetailsDB allAgencyDetailsDB;

    private String itemName;
    public String selectedProductID;

    private String orderID;
    private String qty;
    private String productID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_order);

        initializeViews();
        setupDatePicker();

        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);
    }

    private void initializeViews() {
        btnSelectDate = findViewById(R.id.imageViewSelectDate_update);
        listViewUpdateOrder = findViewById(R.id.listviewUpdateOrder);
        listViewUpdateOrder.setOnItemClickListener(this::onOrderItemClick);

        selectedDateTextView = findViewById(R.id.selectedDateTextView1);
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                this,
                onDateSetListener,
                year,
                month,
                dayOfMonth);

        // Set the maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set the minimum date to the day before yesterday
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        btnSelectDate.setOnClickListener(v -> datePickerDialog.show());
    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                String selectedDate = formatDate(year, monthOfYear, dayOfMonth);
                selectedDateTextView.setText(selectedDate);
                fetchOrderHistory(selectedDate);
            };

    private void fetchOrderHistory(String selectedDate) {
        Set<Order_history> ordersForDate = getOrdersForDate(selectedDate,"Not Synced");

        List<Order_history> orders = new LinkedList<>(ordersForDate);

        if (orders.isEmpty()) {
            // Customize the behavior when there are no orders
            showNoOrdersMessage();
        } else {
            // There are orders, proceed with displaying them
            orderAdapter = new OrderAdapter(this, orders);
            listViewUpdateOrder.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
        }
    }
    private void showNoOrdersMessage() {

        TextView noOrdersTextView = findViewById(R.id.noOrdersTextView);
        noOrdersTextView.setVisibility(View.VISIBLE);
        noOrdersTextView.setText("No Orders Found for Selected Date");

        // You can also hide the ListView or perform any other UI customization
        listViewUpdateOrder.setVisibility(View.GONE);
    }

    private Set<Order_history> getOrdersForDate(String selectedDate,String status) {
        Set<Order_history> orderList = new LinkedHashSet<>();
        Cursor cursor = submitOrderDB.readAllData();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                @SuppressLint("Range") String status1=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                String orderDate = dateTime.substring(0, 10);

                if (orderDate.equals(selectedDate) && status1.equals(status)) {
                    @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));

                    Order_history order = new Order_history();
                    order.setOrder_no(orderID);
                    order.setDate(date_time);
                    orderList.add(order);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        return orderList;
    }

    private void onOrderItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        orderID = orderAdapter.getItem(position).getOrder_no();
      /*  productID = orderAdapter.getItem(position).getProductID();
        List<Order_history> orderDetails = retrieveOrderDetails(orderID);
        System.out.println("orderId is: " + orderID);
        System.out.println("productID is: " + productID);
        showCompleteOrder(orderDetails);*/

        Intent intent=new Intent(UpdateOrderActivity.this,UpdateQtyAddProd.class);
        intent.putExtra("orderID",orderID);
        startActivity(intent);
    }

    @SuppressLint("Range")
    private List<Order_history> retrieveOrderDetails(String orderID) {
        Cursor cursor = submitOrderDB.readDataByOrderID(orderID);
        List<Order_history> submittedOrder = new ArrayList<>();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String productIdString = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
                String qtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));

                // Split the product IDs and quantities into arrays
                String[] productIds = productIdString.split(",");
                String[] qtyArray = qtyString.split(",");

                // Fetch agency details from other tables
                for (int i = 0; i < productIds.length; i++) {
                    String productId = productIds[i].trim();
                    String qty = qtyArray[i].trim();

                    // Fetch item details from other tables
                    Cursor itemCursor = itemsByAgencyDB.readProdcutDataByproductId(productId);

                    if (itemCursor.moveToFirst()) {
                        String itemName = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
                        String agencyId = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));

                        // Fetch agency name
                        Cursor agencyCursor = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);

                        if (agencyCursor.moveToFirst()) {
                            String agencyName = agencyCursor.getString(agencyCursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));

                            // Create Order_history object and add it to the list
                            Order_history order_history = new Order_history();
                            order_history.setAgencyName(agencyName);
                            order_history.setItemName(itemName);
                            order_history.setQty(qty);

                            submittedOrder.add(order_history);
                        }

                        agencyCursor.close();
                    }

                    itemCursor.close();
                }
            }
        }
       // System.out.println("submittedOrder now  is: " + submittedOrder);
        return submittedOrder;
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private void showCompleteOrder(List<Order_history> submittedOrder) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) UpdateOrderActivity.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(UpdateOrderActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_update_quantity);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

     ListView   listUpdate = dialog.findViewById(R.id.recyclerViewupdate);

        listUpdate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View listItem = adapterView.getChildAt(i);
                EditText editTextQty = listItem.findViewById(R.id.qnty); // Assuming R.id.qnty is the id of your EditText

                if (editTextQty != null) {
                    String qtyText = editTextQty.getText().toString();
                    if (!qtyText.isEmpty()) {
                        int qty = Integer.parseInt(qtyText);
                        System.out.println("qty is: " + qty);
                    } else {
                        System.out.println("Qty is empty");
                    }
                } else {
                    System.out.println("EditText is null");
                }
            }
        });





        Button updateButton = dialog.findViewById(R.id.updateButton);
        Button okButton = dialog.findViewById(R.id.okButton);
        EditText editText = dialog.findViewById(R.id.qnty);
        okButton.setOnClickListener(view -> dialog.dismiss());

        updateButton.setOnClickListener(view -> {
            if (submittedOrder != null && !submittedOrder.isEmpty()) {
                Order_history firstOrder = submittedOrder.get(0);

                if (firstOrder != null) {


                    // Update the quantity in the adapter
                  //  productID = firstOrder.getProductID(); // Set the productID
                  //  submitOrderDB.updateQuantityByProductIDAndOrderID(orderID, productID, editText.getText().toString().trim());
                  //  Log.d("UpdateOrderActivity", "Updating order: " + orderID + " ProductID: "+ productID + " with quantity: " + firstOrder.getQty());

                }

                // Update the quantity in the SubmitOrderDB only once
              /*  boolean isUpdated = submitOrderDB.updateQuantityByProductIDAndOrderID(orderID, submittedOrder.get(0).getProductID(), submittedOrder.get(0).getQty());

                if (isUpdated) {

                    Toast.makeText(UpdateOrderActivity.this, "Quantity Updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateOrderActivity.this, "Failed to Update Quantity", Toast.LENGTH_SHORT).show();
                }*/

                Log.d("UpdateOrderActivity", "The List is: " + submittedOrder);


                dialog.dismiss();
            }
        });




        UpdateOrderAdapter updateOrderAdapter = new UpdateOrderAdapter(this, submittedOrder);
        listUpdate.setAdapter(updateOrderAdapter);


        dialog.show();
    }


    @SuppressLint("Range")
    private void getItemCodeForItem(String itemName) {
        Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String productId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                productID = productId;
                Log.d("UpdateOrderActivity", "ProductId is: " + productId);
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            Log.d("UpdateOrderActivity", "No data found for Item " + itemName);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
         btnSelectDate=null;
         datePickerDialog=null;
        listViewUpdateOrder=null;
         orderAdapter=null;
        listUpdate=null;
        selectedDateTextView=null;
        submitOrderDB=null;
        itemsByAgencyDB=null;
        allAgencyDetailsDB=null;
        itemName=null;
         selectedProductID=null;
       orderID=null;
        qty=null;
        productID=null;
        Intent intent=new Intent(UpdateOrderActivity.this,ModifyOrder.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

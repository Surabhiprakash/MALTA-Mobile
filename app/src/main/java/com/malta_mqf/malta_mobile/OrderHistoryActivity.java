package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB.COLUMN_PRODUCTID;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.OnlineOrderHistoryAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderHistoryAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.AllOrderDetails;
import com.malta_mqf.malta_mobile.Model.AllOrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OnlineOrderHistoryBean;
import com.malta_mqf.malta_mobile.Model.OrderDetailBasedOnOrderId;
import com.malta_mqf.malta_mobile.Model.OrderDetailsBasedOnOrderIdResponse;
import com.malta_mqf.malta_mobile.Model.Order_history;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends BaseActivity {
    public String selectedProductID;
    OrderHistoryAdapter orderAdapter1;
    String itemName;
    String orderID;
    String productID, itemId;
    String orderIdNo;
    List<Order_history> allOrders1;
    List<OnlineOrderHistoryBean> orderDetailsList;
    OnlineOrderHistoryAdapter onlineOrderHistoryAdapter;
    private ImageView btnSelectDate;
    private DatePickerDialog datePickerDialog;
    private ListView listViewOrderHistory;
    private OrderAdapter orderAdapter;
    private TextView selectedDateTextView;
    private SubmitOrderDB submitOrderDB;
    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                String selectedDate = formatDate(year, monthOfYear, dayOfMonth);
                selectedDateTextView.setText(selectedDate);
                fetchOrderHistory(selectedDate);
            };
    private ItemsByAgencyDB itemsByAgencyDB;
    private AllAgencyDetailsDB allAgencyDetailsDB;
    private Set<Order_history> orderHistorySet = new LinkedHashSet<>();
    private List<Order_history> submittedOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history2);
        allOrders1 = new LinkedList<>();
        orderDetailsList = new LinkedList<>();

        initializeViews();
        setupDatePicker();

        submitOrderDB = new SubmitOrderDB(this);
        itemsByAgencyDB = new ItemsByAgencyDB(this);
        allAgencyDetailsDB = new AllAgencyDetailsDB(this);


    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();


            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void initializeViews() {
        btnSelectDate = findViewById(R.id.imageViewSelectDatehistory);
        listViewOrderHistory = findViewById(R.id.recyclerViewOrderHistory);
        listViewOrderHistory.setOnItemClickListener(this::onOrderItemClick);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
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

        btnSelectDate.setOnClickListener(v -> datePickerDialog.show());
    }

    private void fetchOrderHistory(String selectedDate) {
        Log.d("TAG", "fetchOrderHistory: " + selectedDate);
        if (isOnline()) {
            getAllOrderDetails();
        } else {
            Set<Order_history> ordersForDate = getOrdersForDate(selectedDate, "PENDING FOR DELIVERY");
            List<Order_history> orders = new LinkedList<>(ordersForDate);
            orderAdapter = new OrderAdapter(this, orders);
            listViewOrderHistory.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
        }
    }

    private void getAllOrderDetails() {
        allOrders1.clear();
        String url = ApiLinks.allOrderDetails;
        Call<AllOrderDetailsResponse> call = apiInterface.allOrderDetails(url);
        call.enqueue(new Callback<AllOrderDetailsResponse>() {

            @Override
            public void onResponse(Call<AllOrderDetailsResponse> call, Response<AllOrderDetailsResponse> response) {

                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllOrderDetailsResponse allOrderDetailsResponse = response.body();
                    List<AllOrderDetails> allOrders = allOrderDetailsResponse.getAllOrderDetails();
                    String dateTime = getCurrentDate();

                    for (AllOrderDetails order : allOrders) {
                        // orderIdNo = order.getOrderid();
                        String orderID = order.getOrderid();
                        String vanid = order.getVanId();
                        if (vanid.equalsIgnoreCase(vanID)) {
                            allOrders1.add(new Order_history(orderID, dateTime));
                        }

                    }
                    if (allOrders1.size() > 0) {

                        orderAdapter = new OrderAdapter(OrderHistoryActivity.this, allOrders1);
                        listViewOrderHistory.setAdapter(orderAdapter);

                    }

                    System.out.println("all orders are: " + allOrders);


                }

            }


            @Override
            public void onFailure(Call<AllOrderDetailsResponse> call, Throwable t) {
                displayAlert("Alert", t.getMessage());

            }
        });

    }


    private void getOrdersForOrderID(String orderId) {
        orderDetailsList.clear();
        System.out.println("We are in getOrdersForOrderID");
        String url = ApiLinks.itemDetailsBsdOnOrderId + "?orderid=" + orderId;
        Call<OrderDetailsBasedOnOrderIdResponse> call = apiInterface.orderDetailBasedOnOrderId(url);
        call.enqueue(new Callback<OrderDetailsBasedOnOrderIdResponse>() {
            @Override
            public void onResponse(Call<OrderDetailsBasedOnOrderIdResponse> call, Response<OrderDetailsBasedOnOrderIdResponse> response) {
                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    System.out.println("haejhdbhbjhb" + response.body().getStatus());
                    OrderDetailsBasedOnOrderIdResponse orderDetailsBasedOnOrderIdResponse = response.body();
                    List<OrderDetailBasedOnOrderId> orderDetails = orderDetailsBasedOnOrderIdResponse.getApprovedOrderDetailsBsdOnOrderid();
                    //List<Order_history> processedOrderDetails = new ArrayList<>();

                    for (OrderDetailBasedOnOrderId order : orderDetails) {
                        String orderID = order.getOrderid();
                        String agencyName = order.getAgencyName();
                        String itemName = order.getItemName();
                        String qty = order.getOrderedQty();
                        String approvedQty = order.getApprovedQty();
                        orderDetailsList.add(new OnlineOrderHistoryBean(orderID, agencyName, itemName, qty, approvedQty));
                    }


                    OnlineshowCompleteOrder(orderDetailsList);


                } else {
                    // Handle other response errors (e.g., network issues)
                    System.out.println("Response error: " + response.message());
                    System.out.println("HTTP status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderDetailsBasedOnOrderIdResponse> call, Throwable t) {
                // Handle failure (e.g., network failure, server error)
                System.out.println("Request failed: " + t.getMessage());
            }
        });
    }

    private Set<Order_history> getOrdersForDate(String selectedDate, String status) {
        Log.d("TAG", "getOrdersForDate: " + selectedDate);
        Set<Order_history> orderList = new LinkedHashSet<>();

        Cursor cursor = submitOrderDB.readAllData();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_DATE_TIME));
                @SuppressLint("Range") String status1 = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
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
        productID = orderAdapter.getItem(position).getProductID();
        List<Order_history> orderDetails = retrieveOrderDetails(orderID);
        System.out.println("orderId is: " + orderID);
        System.out.println("productID is: " + productID);
        if (isOnline()) {
            getOrdersForOrderID(orderID);
        } else {
            //showCompleteOrder(orderDetails);
        }

    }

    @SuppressLint("Range")
    private List<Order_history> retrieveOrderDetails(String orderID) {
        Cursor cursor = submitOrderDB.readDataByOrderIDAndStatus(orderID, "PENDING FOR DELIVERY");
        List<Order_history> submittedOrder = new ArrayList<>();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                String productIdString = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
                String qtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));
                String approvedQtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));
                // Split the product IDs and quantities into arrays
                String[] productIds = productIdString.split(",");
                String[] qtyArray = qtyString.split(",");
                String[] approvedQtyArray = approvedQtyString.split(",");
                // Fetch agency details from other tables
                for (int i = 0; i < productIds.length; i++) {
                    String productId = productIds[i].trim();
                    String qty = qtyArray[i].trim();
                    String approvedQty = approvedQtyArray[i].trim();
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
                            order_history.setApproved_qty(approvedQty);
                            submittedOrder.add(order_history);
                        }

                        agencyCursor.close();
                    }

                    itemCursor.close();
                }
            }
        }
        cursor.close();
        Log.d("TAG", "retrieveOrderDetails: " + submittedOrder);
        return submittedOrder;
    }


    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    /*private void showCompleteOrder(List<Order_history> submittedOrder) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) OrderHistoryActivity.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(OrderHistoryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderhistorydetails2);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        ListView listView = dialog.findViewById(R.id.recyclerView);


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Order_history selectedItem = submittedOrder.get(position);
            orderIdNo = selectedItem.getOrder_no();
            itemName = selectedItem.getItemName();
            selectedProductID = selectedItem.getProductID(); // Assuming there is a getProductID() method

            getItemCodeForItem(itemName);

            itemName=null;
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Order_history selectedItem = submittedOrder.get(position);
            itemName = selectedItem.getItemName();


            showDeleteConfirmationDialog(position, submittedOrder);
            itemName=null;
            return true;
        });



      // orderDetailSet.addAll(submittedOrder);
     //   order_histories = new ArrayList<>(orderDetailSet);
        orderAdapter1 = new OrderHistoryAdapter(OrderHistoryActivity.this, submittedOrder);
        listView.setAdapter(orderAdapter1);
       // orderHistorySet.clear();
      //  order_histories.clear();
        dialog.show();
    }*/
    private void OnlineshowCompleteOrder(List<OnlineOrderHistoryBean> submittedOrder) {
        System.out.println("OnlineshowCompleteOrder: " + submittedOrder);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) OrderHistoryActivity.this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(OrderHistoryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderhistorydetails2);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        ListView listView = dialog.findViewById(R.id.recyclerView);


        // orderDetailSet.addAll(submittedOrder);
        //   order_histories = new ArrayList<>(orderDetailSet);
        onlineOrderHistoryAdapter = new OnlineOrderHistoryAdapter(OrderHistoryActivity.this, submittedOrder);
        listView.setAdapter(onlineOrderHistoryAdapter);
        // orderHistorySet.clear();
        //  order_histories.clear();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(int position, List<Order_history> submittedOrder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderHistoryActivity.this);
        alertDialogBuilder.setTitle("Delete Item");
        alertDialogBuilder.setMessage("Are you sure you want to delete this item?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked Yes, delete the item
                Order_history selectedItem = submittedOrder.get(position);
                String itemName = selectedItem.getItemName();

                // Get the item code using ItemsByAgencyDB
                ItemsByAgencyDB itemsByAgencyDB = new ItemsByAgencyDB(OrderHistoryActivity.this);
                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

                if (cursor != null && cursor.moveToFirst()) {
                    // Assuming "COLUMN_ITEM_CODE" is the column name for item code
                    itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    cursor.close();
                    // Now you have the item code, you can use it as needed

                    // Continue with the deletion logic or other actions...
                    // Do not delete the item here; instead, pass the position and item code to deleteSelectedItem
                    deleteSelectedItem(position, submittedOrder, itemId);
                    itemId = null;
                } else {
                    // Handle the case where item code is not found
                    System.out.println("ItemId not found for item: " + itemName);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void deleteOrder(String orderID) {
        boolean isDeleted = submitOrderDB.deleteOrder(orderID);

        if (isDeleted) {
            Toast.makeText(OrderHistoryActivity.this, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
            fetchOrderHistory(selectedDateTextView.getText().toString());
        } else {
            Toast.makeText(OrderHistoryActivity.this, "Failed to Cancel Order", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("Range")
    private void getItemCodeForItem(String itemName) {
        Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming "COLUMN_ITEM_ID" is the column name for item code
            itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
            cursor.close();

            itemId = null;
        } else {
            System.out.println("No data found for Item " + itemName);
        }
    }


    private void deleteSelectedItem(int position, List<Order_history> submittedOrder, String itemCode) {

        if (submittedOrder != null && !submittedOrder.isEmpty() && position < submittedOrder.size()) {


            if (itemId.equals(itemCode)) {
                submittedOrder.remove(position);

                submitOrderDB.deleteItemByProductIDAndOrderID(itemId, orderID);

                Toast.makeText(OrderHistoryActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                orderAdapter1.notifyDataSetChanged();
            } else {
                Toast.makeText(OrderHistoryActivity.this, "ItemCode does not match selected item's ID", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        btnSelectDate = null;
        datePickerDialog = null;
        listViewOrderHistory = null;
        orderAdapter = null;
        selectedDateTextView = null;
        submitOrderDB = null;
        itemsByAgencyDB = null;
        allAgencyDetailsDB = null;
        orderHistorySet = null;
        submittedOrderList = null;
        orderAdapter1 = null;
        itemName = null;
        orderID = null;
        productID = null;
        itemId = null;
        selectedProductID = null;
    }
}
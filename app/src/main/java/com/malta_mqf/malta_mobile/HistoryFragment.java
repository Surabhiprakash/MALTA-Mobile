package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB.COLUMN_PRODUCTID;
import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.API.ApiLinks;
import com.malta_mqf.malta_mobile.Adapter.OnlineOrderHistoryAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderHistoryAdapter;
import com.malta_mqf.malta_mobile.Adapter.RecyclerItemClickListener;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.AllOrderDetails;
import com.malta_mqf.malta_mobile.Model.AllOrderDetailsResponse;
import com.malta_mqf.malta_mobile.Model.OnlineOrderHistoryBean;
import com.malta_mqf.malta_mobile.Model.OrderDetailBasedOnOrderId;
import com.malta_mqf.malta_mobile.Model.OrderDetailsBasedOnOrderIdResponse;
import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.Utilities.ApiClient;
import com.malta_mqf.malta_mobile.Utilities.ApiInterFace;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {
    private ImageView btnSelectDate,ivIndicator;
    private DatePickerDialog datePickerDialog;
    private ListView listViewOrderHistory;
    private OrderAdapter orderAdapter;
    private TextView selectedDateTextView;

    private SubmitOrderDB submitOrderDB;
    private ApiInterFace apiInterface;
    private ItemsByAgencyDB itemsByAgencyDB;
    private AllAgencyDetailsDB allAgencyDetailsDB;
    private OutletByIdDB outletByIdDB;
    private Set<Order_history> orderHistorySet = new LinkedHashSet<>();
    private List<Order_history> submittedOrderList = new ArrayList<>();
    private OrderHistoryAdapter orderAdapter1;
    private String itemName;
    private String orderID;
    private String productID, itemId;
    private String orderIdNo;
    private String selectedProductID;
    private List<Order_history> allOrders1 ;
    private List<OnlineOrderHistoryBean> orderDetailsList;
    private OnlineOrderHistoryAdapter onlineOrderHistoryAdapter;
    ProgressDialog mProgressDialog;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_history2, container, false);
        initializeViews(view);
        setupDatePicker();
        openIndicator();
         allOrders1 = new LinkedList<>();
         orderDetailsList = new LinkedList<>();
        submitOrderDB = new SubmitOrderDB(requireContext());
        itemsByAgencyDB = new ItemsByAgencyDB(requireContext());
        outletByIdDB=new OutletByIdDB(requireContext());
        allAgencyDetailsDB = new AllAgencyDetailsDB(requireContext());
        apiInterface = ApiClient.getClient().create(ApiInterFace.class);
        mProgressDialog = new ProgressDialog(requireContext());

        return view;

    }



    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void initializeViews(View view) {
        btnSelectDate = view.findViewById(R.id.imageViewSelectDatehistory);
        listViewOrderHistory = view.findViewById(R.id.recyclerViewOrderHistory);
        listViewOrderHistory.setOnItemClickListener(this::onOrderItemClick);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
        ivIndicator=view.findViewById(R.id.ivindicator);
    }

    private void setupDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                requireContext(),
                onDateSetListener,
                year,
                month,
                dayOfMonth);

        btnSelectDate.setOnClickListener(v -> datePickerDialog.show());
    }


    private void openIndicator(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_orderstatus_indicator);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);
       ivIndicator.setOnClickListener(view ->dialog.show() );


    }

    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                String selectedDate = formatDate(year, monthOfYear, dayOfMonth);
                selectedDateTextView.setText(selectedDate);
                fetchOrderHistory(selectedDate);
            };

    private void fetchOrderHistory(String selectedDate) {
        Log.d("TAG", "fetchOrderHistory: " + selectedDate);
        Set<Order_history> ordersForDate = getOrdersForDate(selectedDate);
        List<Order_history> orders = new LinkedList<>(ordersForDate);

        if (orders.isEmpty()) {
            showNoOrdersMessage();
        } else {
            if (orderAdapter == null) {
                orderAdapter = new OrderAdapter(requireContext(), orders);
                listViewOrderHistory.setAdapter(orderAdapter);
            } else {
                orderAdapter.clear();  // Clear the previous data
                orderAdapter.addAll(orders);  // Add the new data
                orderAdapter.notifyDataSetChanged();  // Notify the adapter to refresh
            }
            listViewOrderHistory.setVisibility(View.VISIBLE);  // Ensure the ListView is visible
            getView().findViewById(R.id.noOrdersTextView).setVisibility(View.GONE);  // Hide the no orders message
        }
    }

    private void showNoOrdersMessage() {
        TextView noOrdersTextView = getView().findViewById(R.id.noOrdersTextView);
        noOrdersTextView.setVisibility(View.VISIBLE);
        noOrdersTextView.setText("No Orders Found for Selected Date");

        listViewOrderHistory.setVisibility(View.GONE);
    }

    private Set<Order_history> getOrdersForDate(String selectedDate) {
        Log.d("TAG", "getOrdersForDate: " + selectedDate);
        Set<Order_history> orderList = new LinkedHashSet<>();

        Cursor cursor = submitOrderDB.readAllData();

        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {
                    @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                    if (dateTime == null) {
                        @SuppressLint("Range") String approvedDateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_ORDER_TIME));
                        String orderDate1 = approvedDateTime.substring(0, 10);
                        if (orderDate1.equals(selectedDate)) {
                            @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                            @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_ORDER_TIME));
                            @SuppressLint("Range") String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                            Cursor cursor1 = outletByIdDB.readOutletByID(outletid);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range") String outletName = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                Order_history order = new Order_history();
                                order.setOrder_no(orderID);
                                order.setDate(date_time);
                                order.setOutletname(outletName);
                                order.setOrderstatus(status);
                                orderList.add(order);
                            }
                            cursor1.close();  // Close the inner cursor
                        }
                    } else {
                        @SuppressLint("Range") String status1 = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                        String orderDate = dateTime.substring(0, 10);
                        if (orderDate.equals(selectedDate)) {
                            @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                            @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                            @SuppressLint("Range") String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                            Cursor cursor1 = outletByIdDB.readOutletByID(outletid);
                            while (cursor1.moveToNext()) {
                                @SuppressLint("Range") String outletName = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                Order_history order = new Order_history();
                                order.setOrder_no(orderID);
                                order.setDate(date_time);
                                order.setOutletname(outletName);
                                order.setOrderstatus(status);
                                orderList.add(order);
                            }
                            cursor1.close();  // Close the inner cursor
                        }
                    }
                } while (cursor.moveToNext());

                cursor.close();  // Close the outer cursor
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        return orderList;
    }

    private void onOrderItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        orderID = orderAdapter.getItem(position).getOrder_no();
        productID = orderAdapter.getItem(position).getProductID();
        showProgressDialog();
        executorService.execute(() -> {
            List<Order_history> orderDetails = retrieveOrderDetails(orderID);
            System.out.println("ordered after synced:" + orderDetails);
            requireActivity().runOnUiThread(() -> {
                dismissProgressDialog();
                showCompleteOrder(orderDetails);
            });
        });
    }


    private void getAllOrderDetails(String date) {
        allOrders1.clear();
        String url = ApiLinks.allOrderDetails;
        Call<AllOrderDetailsResponse> call = apiInterface.allOrderDetails(url);
        call.enqueue(new Callback<AllOrderDetailsResponse>() {

            @Override
            public void onResponse(Call<AllOrderDetailsResponse> call, Response<AllOrderDetailsResponse> response) {

                if (response.body().getStatus().equalsIgnoreCase("yes")) {
                    AllOrderDetailsResponse allOrderDetailsResponse = response.body();
                    List<AllOrderDetails> allOrders = allOrderDetailsResponse.getAllOrderDetails();
                    // String dateTime = getCurrentDate();

                    for (AllOrderDetails order : allOrders) {
                        // orderIdNo = order.getOrderid();
                        String orderID = order.getOrderid();
                        String vanid=order.getVanId();
                        String dateTime=order.getOrderDateTime().substring(0,10);
                        if (vanid.equalsIgnoreCase(vanID)&& date.equalsIgnoreCase(dateTime)) {
                            allOrders1.add(new Order_history(orderID, dateTime));
                        }

                    }
                    if(allOrders1.size()>0){

                        orderAdapter = new OrderAdapter(requireContext(), allOrders1);
                        listViewOrderHistory.setAdapter(orderAdapter);

                    }

                    System.out.println("all orders are: " + allOrders);




                }

            }


            @Override
            public void onFailure(Call<AllOrderDetailsResponse> call, Throwable t) {
                // displayAlert("Alert", t.getMessage());

            }
        });

    }


    private void getOrdersForOrderID(String orderId) {
        orderDetailsList.clear();
        System.out.println("We are in getOrdersForOrderID");
        String url = ApiLinks.itemDetailsBsdOnOrderId+"?orderid="+orderId;
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
                        String qty =order.getOrderedQty();
                        String approvedQty = order.getApprovedQty();
                        orderDetailsList.add(new OnlineOrderHistoryBean(orderID, agencyName, itemName, qty, approvedQty));
                    }


                    OnlineshowCompleteOrder(orderDetailsList);


                } else {
                    // Handle other response errors (e.g., network issues)
                    System.out.println("Response error: " + response.message());
                    System.out.println("HTTP status code: " + response.code());                }
            }

            @Override
            public void onFailure(Call<OrderDetailsBasedOnOrderIdResponse> call, Throwable t) {
                // Handle failure (e.g., network failure, server error)
                System.out.println("Request failed: " + t.getMessage());
            }
        });
    }
   /* private Set<Order_history> getOrdersForDate(String selectedDate) {
        Log.d("TAG", "getOrdersForDate: "+selectedDate);
        Set<Order_history> orderList = new LinkedHashSet<>();

        Cursor cursor = submitOrderDB.readAllData();

        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {

                    @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                    if(dateTime==null){
                        @SuppressLint("Range") String approvedDateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_ORDER_TIME));
                        String orderDate1  = approvedDateTime.substring(0, 10);
                        if (orderDate1.equals(selectedDate)) {
                            @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                            @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_ORDER_TIME));
                            @SuppressLint("Range") String outletid=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            @SuppressLint("Range") String status=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                            System.out.println("status:"+status);

                            Cursor cursor1=outletByIdDB.readOutletByID(outletid);
                            while (cursor1.moveToNext()){
                                @SuppressLint("Range") String outletName=cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                System.out.println("outletnameeee:"+outletName);
                                Order_history order = new Order_history();
                                order.setOrder_no(orderID);
                                order.setDate(date_time);
                                order.setOutletname(outletName);
                                order.setOrderstatus(status);
                                orderList.add(order);
                            }


                        }
                    }else {
                        @SuppressLint("Range") String status1 = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                        //  System.out.println("dateeeeeTimeeee: " + dateTime);
                        String orderDate = dateTime.substring(0, 10);

                        if (orderDate.equals(selectedDate)) {
                            @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                            @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));

                            @SuppressLint("Range") String outletid=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                            @SuppressLint("Range") String status=cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                            System.out.println("status:"+status);
                            Cursor cursor1=outletByIdDB.readOutletByID(outletid);
                            while (cursor1.moveToNext()){
                                @SuppressLint("Range") String outletName=cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                                System.out.println("outletnameeee:"+outletName);
                                Order_history order = new Order_history();
                                order.setOrder_no(orderID);
                                order.setDate(date_time);
                                order.setOutletname(outletName);
                                order.setOrderstatus(status);
                                orderList.add(order);
                            }
                        }
                    }

                } while (cursor.moveToNext());

                cursor.close();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        return orderList;
    }

    private void onOrderItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        orderID = orderAdapter.getItem(position).getOrder_no();
        productID = orderAdapter.getItem(position).getProductID();
        showProgressDialog();
        executorService.execute(() -> {
            List<Order_history> orderDetails = retrieveOrderDetails(orderID);
            System.out.println("ordered after synced:"+orderDetails);
            requireActivity().runOnUiThread(() -> {
                dismissProgressDialog();
                showCompleteOrder(orderDetails);
            });
        });
    }*/

    @SuppressLint("Range")
    private List<Order_history> retrieveOrderDetails(String orderID) {
        List<Order_history> submittedOrder = new ArrayList<>();
        Cursor cursor = submitOrderDB.readDataByOrderID(orderID);

        if (cursor.getCount() == 0) {
            cursor.close();
            return submittedOrder;
        }

        while (cursor.moveToNext()) {
            String productIdString = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
            String qtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_REQUESTED_QTY));
            String approvedQtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_APPROVED_QTY));
            String deliveredQtyString = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_DELIVERED_QTY));


            String[] productIds = productIdString.split(",");
            String[] qtyArray = (qtyString != null && !qtyString.isEmpty()) ? qtyString.split(",") : new String[productIds.length];
            String[] approvedQtyArray = (approvedQtyString != null && !approvedQtyString.isEmpty()) ? approvedQtyString.split(",") : new String[productIds.length];
            String[] deliveredQtyArray = (deliveredQtyString != null && !deliveredQtyString.isEmpty()) ? deliveredQtyString.split(",") : new String[productIds.length];
            if(qtyArray.length!=productIds.length){
                Arrays.fill(qtyArray, "N/A");
            }
            if (approvedQtyArray.length != productIds.length) {
                Arrays.fill(approvedQtyArray, "N/A");  // Fill the whole array with "N/A" if lengths do not match
            }

            if (deliveredQtyArray.length != productIds.length) {
                Arrays.fill(deliveredQtyArray, "N/A");  // Fill the whole array with "N/A" if lengths do not match
            }
            if (productIds.length != qtyArray.length) {
                Log.e("TAG", "Array lengths do not match: productIds.length = " + productIds.length +
                        ", qtyArray.length = " + qtyArray.length + ", approvedQtyArray.length = " + approvedQtyArray.length);
                continue;
            }

            for (int i = 0; i < productIds.length; i++) {
                String productId = productIds[i].trim();
                String qty = "N/A";
                String approvedQty = "N/A";  // Default value
                String deliveredQty = "N/A";  // Default value
                if(qtyArray != null && i < qtyArray.length && qtyArray[i] != null){
                    qty = qtyArray[i].trim();
                }

// Check if approvedQtyArray is not null and i is within its bounds
                if (approvedQtyArray != null && i < approvedQtyArray.length && approvedQtyArray[i] != null) {
                    approvedQty = approvedQtyArray[i].trim();
                }

                if(deliveredQtyArray!=null && i < deliveredQtyArray.length && deliveredQtyArray[i] != null){
                    deliveredQty = deliveredQtyArray[i].trim();
                }
                Order_history order_history = fetchOrderHistoryDetails(productId, qty, approvedQty,deliveredQty);
                if (order_history != null) {
                    submittedOrder.add(order_history);
                }
            }

        }
        cursor.close();
        return submittedOrder;
    }

    @SuppressLint("Range")
    private Order_history fetchOrderHistoryDetails(String productId, String qty, String approvedQty,String deliverqty) {
        Cursor itemCursor = itemsByAgencyDB.readProdcutDataByproductId(productId);

        if (itemCursor.moveToFirst()) {
            String itemName = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
            String agencyId = itemCursor.getString(itemCursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_AGENCY_CODE));

            Cursor agencyCursor = allAgencyDetailsDB.readAgencyDataByagencyID(agencyId);

            if (agencyCursor.moveToFirst()) {
                String agencyName = agencyCursor.getString(agencyCursor.getColumnIndex(AllAgencyDetailsDB.COLUMN_AGENCY_NAME));

                Order_history order_history = new Order_history();
                order_history.setAgencyName(agencyName);
                order_history.setItemName(itemName);
                order_history.setQty(qty);
                order_history.setApproved_qty(approvedQty);
                order_history.setDeliveryQty(deliverqty);
                agencyCursor.close();
                itemCursor.close();
                return order_history;
            }

            agencyCursor.close();
        }

        itemCursor.close();
        return null;
    }

    public void showProgressDialog() {
        requireActivity().runOnUiThread(() -> {
            mProgressDialog.setMessage("Loading, please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        });
    }

    public void dismissProgressDialog() {
        requireActivity().runOnUiThread(() -> {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        });
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

    private void showCompleteOrder(List<Order_history> submittedOrder) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderhistorydetails2);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        Button button=dialog.findViewById(R.id.cancel);
        button.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        OrderHistoryAdapter orderAdapter = new OrderHistoryAdapter(requireContext(), submittedOrder);
        recyclerView.setAdapter(orderAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(requireContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Order_history selectedItem = submittedOrder.get(position);
                orderIdNo = selectedItem.getOrder_no();
                itemName = selectedItem.getItemName();
                selectedProductID = selectedItem.getProductID(); // Assuming there is a getProductID() method

                getItemCodeForItem(itemName);
                itemName = null;
            }

            @Override
            public void onLongItemClick(View view, int position) {
                Order_history selectedItem = submittedOrder.get(position);
                itemName = selectedItem.getItemName();
                showDeleteConfirmationDialog(position, submittedOrder);
                itemName = null;
            }
        }));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void OnlineshowCompleteOrder(List<OnlineOrderHistoryBean> submittedOrder) {
        System.out.println("OnlineshowCompleteOrder: "+submittedOrder);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.orderhistorydetails2);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        ListView listView = dialog.findViewById(R.id.recyclerView);



        // orderDetailSet.addAll(submittedOrder);
        //   order_histories = new ArrayList<>(orderDetailSet);
        onlineOrderHistoryAdapter = new OnlineOrderHistoryAdapter(requireContext(), submittedOrder);
        listView.setAdapter(onlineOrderHistoryAdapter);
        onlineOrderHistoryAdapter.notifyDataSetChanged();
        // orderHistorySet.clear();
        //  order_histories.clear();
        dialog.show();
    }
    private void showDeleteConfirmationDialog(int position, List<Order_history> submittedOrder) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
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
                ItemsByAgencyDB itemsByAgencyDB = new ItemsByAgencyDB(requireContext());
                Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

                if (cursor != null && cursor.moveToFirst()) {
                    // Assuming "COLUMN_ITEM_CODE" is the column name for item code
                    itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                    cursor.close();
                    // Now you have the item code, you can use it as needed

                    // Continue with the deletion logic or other actions...
                    // Do not delete the item here; instead, pass the position and item code to deleteSelectedItem
                    deleteSelectedItem(position, submittedOrder, itemId);
                    itemId=null;
                    dialogInterface.dismiss();
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
            Toast.makeText(requireContext(), "Order Cancelled Successfully", Toast.LENGTH_SHORT).show();
            fetchOrderHistory(selectedDateTextView.getText().toString());
        } else {
            Toast.makeText(requireContext(), "Failed to Cancel Order", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("Range")
    private void getItemCodeForItem(String itemName) {
        Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming "COLUMN_ITEM_ID" is the column name for item code
            itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
            cursor.close();

            itemId=null;
        } else {
            System.out.println("No data found for Item " + itemName);
        }
    }



    private void deleteSelectedItem(int position, List<Order_history> submittedOrder, String itemCode) {

        if (submittedOrder != null && !submittedOrder.isEmpty() && position < submittedOrder.size()) {




            if (itemId.equals(itemCode)) {
                submittedOrder.remove(position);

                submitOrderDB.deleteItemByProductIDAndOrderID(itemId,orderID);

                Toast.makeText(requireContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
               // orderAdapter1.notifyDataSetChanged();
               if(onlineOrderHistoryAdapter!=null){
                   onlineOrderHistoryAdapter.notifyDataSetChanged();
               }

            } else {
                Toast.makeText(requireContext(), "ItemCode does not match selected item's ID", Toast.LENGTH_SHORT).show();
            }
        }
    }



    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        btnSelectDate=null;
        datePickerDialog=null;
        listViewOrderHistory=null;
        orderAdapter=null;
        selectedDateTextView=null;
        submitOrderDB=null;
        itemsByAgencyDB=null;
        allAgencyDetailsDB=null;
        orderHistorySet = null;
        submittedOrderList = null;
        orderAdapter1=null;
        itemName=null;
        orderID=null;
        productID=null;
        itemId=null;
        selectedProductID=null;
    }*/
}
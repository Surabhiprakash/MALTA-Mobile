package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB.COLUMN_PRODUCTID;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.malta_mqf.malta_mobile.Adapter.OrderAdapter;
import com.malta_mqf.malta_mobile.Adapter.OrderDetailsSpinnerAdapter;
import com.malta_mqf.malta_mobile.DataBase.AllAgencyDetailsDB;
import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.DataBase.OutletByIdDB;
import com.malta_mqf.malta_mobile.DataBase.SubmitOrderDB;
import com.malta_mqf.malta_mobile.Model.Order_history;
import com.malta_mqf.malta_mobile.Utilities.RecyclerViewSwipeDecorator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CancelFragment extends Fragment {

    private ImageView btnSelectDate;
    private DatePickerDialog datePickerDialog;
    private ListView listViewOrderHistory;
    private OrderAdapter orderAdapter;
    private TextView selectedDateTextView;
    private ItemTouchHelper itemTouchHelper;
    private SubmitOrderDB submitOrderDB;
    private ItemsByAgencyDB itemsByAgencyDB;
    private RecyclerView recyclerView;
    private OrderDetailsSpinnerAdapter orderAdapter1;
    private AllAgencyDetailsDB allAgencyDetailsDB;
    private OutletByIdDB outletByIdDB;
    private final DatePickerDialog.OnDateSetListener onDateSetListener =
            (view, year, monthOfYear, dayOfMonth) -> {
                String selectedDate = formatDate(year, monthOfYear, dayOfMonth);
                selectedDateTextView.setText(selectedDate);
                fetchOrderHistory(selectedDate);
            };
    private List<Order_history> orderDetails;
    private String itemName;
    private String orderID;
    private String productID, itemId;
    private String selectedProductID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cancel_order, container, false);
        initializeViews(view);
        setupDatePicker();

        submitOrderDB = new SubmitOrderDB(requireContext());
        itemsByAgencyDB = new ItemsByAgencyDB(requireContext());
        allAgencyDetailsDB = new AllAgencyDetailsDB(requireContext());
        outletByIdDB = new OutletByIdDB(requireContext());

        return view;
    }

    private void initializeViews(View view) {
        btnSelectDate = view.findViewById(R.id.imageViewSelectDate);
        listViewOrderHistory = view.findViewById(R.id.recyclerViewOrderHistory);
        listViewOrderHistory.setOnItemClickListener(this::onOrderItemClick);
        selectedDateTextView = view.findViewById(R.id.selectedDateTextView);
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

        // Set the maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set the minimum date to the day before yesterday
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        btnSelectDate.setOnClickListener(v -> datePickerDialog.show());
    }

    private void fetchOrderHistory(String selectedDate) {
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

        // You can also hide the ListView or perform any other UI customization
        listViewOrderHistory.setVisibility(View.GONE);
    }


    private Set<Order_history> getOrdersForDate(String selectedDate) {
        Set<Order_history> orderList = new LinkedHashSet<>();

        Cursor cursor = submitOrderDB.readDataByProductStatus("Not Synced");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));
                String orderDate = dateTime.substring(0, 10);

                if (orderDate.equals(selectedDate)) {
                    @SuppressLint("Range") String orderID = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERID));
                    @SuppressLint("Range") String date_time = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_ORDERED_DATE_TIME));

                    @SuppressLint("Range") String outletid = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_OUTLETID));
                    @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(SubmitOrderDB.COLUMN_STATUS));
                    System.out.println("status:" + status);

                    Cursor cursor1 = outletByIdDB.readOutletByID(outletid);
                    while (cursor1.moveToNext()) {
                        @SuppressLint("Range") String outletName = cursor1.getString(cursor1.getColumnIndex(OutletByIdDB.COLUMN_OUTLET_NAME));
                        System.out.println("outletnameeee:" + outletName);
                        Order_history order = new Order_history();
                        order.setOrder_no(orderID);
                        order.setDate(date_time);
                        order.setOutletname(outletName);
                        order.setOrderstatus(status);
                        orderList.add(order);
                    }
                }

            } while (cursor.moveToNext());

            cursor.close();
        }

        return orderList;
    }

    private void onOrderItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Order_history selectedOrder = orderAdapter.getItem(position);

        if (selectedOrder != null) {
            orderID = selectedOrder.getOrder_no();
            productID = selectedOrder.getProductID();
            orderDetails = retrieveOrderDetails(orderID);
            Log.d("TAG", "Order ID: " + orderID);
            Log.d("TAG", "Product ID: " + productID);
            Log.d("TAG", "Order Details: " + orderDetails);
            showCompleteOrder(orderDetails);
        } else {

            Log.d("TAG", "Selected order is null");
        }
    }

    @SuppressLint("Range")
    private List<Order_history> retrieveOrderDetails(String orderID) {
        Cursor cursor = submitOrderDB.readDataByOrderID(orderID);
        List<Order_history> submittedOrder = new ArrayList<>();

        if (cursor.getCount() == 0) {
            Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show();
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
        cursor.close();
        Log.d("TAG", "Submitted Order: " + submittedOrder);
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

    private void showCompleteOrder(List<Order_history> submittedOrder) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.order_history_details);
        dialog.getWindow().setLayout(((displayMetrics.widthPixels / 100) * 90), LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);


        recyclerView = dialog.findViewById(R.id.recyclerView);

        // Assuming you have already set the layout manager for recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        orderAdapter1 = new OrderDetailsSpinnerAdapter(requireContext(), submittedOrder);

        recyclerView.setAdapter(orderAdapter1);
        initializeSwipeToDelete(orderAdapter);


        orderAdapter1.setOnItemClickListener((position, selectedItem) -> {
            itemName = selectedItem.getItemName();
            selectedProductID = selectedItem.getProductID(); // Assuming there is a getProductID() method

            getItemCodeForItem(itemName);

        });

     /*   orderAdapter1.setOnItemLongClickListener((position, selectedItem) -> {
            itemName = selectedItem.getItemName();
            System.out.println("ItemName is: " + itemName);

            showDeleteConfirmationDialog(position, submittedOrder);

            return true;
        });*/

        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        cancelButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        Button okButton = dialog.findViewById(R.id.okButton);
        okButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        TextView OrderID = dialog.findViewById(R.id.textViewOrderId);
        OrderID.setText("OrderID: " + orderID);
        okButton.setOnClickListener(view -> dialog.dismiss());

        cancelButton.setOnClickListener(view -> {
            if (submittedOrder != null && !submittedOrder.isEmpty()) {
                Order_history firstOrder = submittedOrder.get(0);
                if (firstOrder != null) {
                    Log.d("OrderHistoryActivity", "Deleting Order with ID: " + orderID);
                    deleteOrder(orderID);
                }
            }
        });

        dialog.show();
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
        } else {
            System.out.println("No data found for Item " + itemName);
        }
    }


    private void deleteSelectedItem(int position, List<Order_history> submittedOrder, String itemCode) {
        if (submittedOrder != null && !submittedOrder.isEmpty() && position < submittedOrder.size()) {
            Order_history selectedItem = submittedOrder.get(position);

            if (selectedItem != null) {
                String itemName = selectedItem.getItemName();

                // Retrieve the item code for the selected item
                String itemId = getItemIdForItem(itemName);

                // Add a null check for itemId
                if (itemId != null && itemId.equals(itemCode)) {
                    submittedOrder.remove(position);
                    submitOrderDB.deleteItemByProductIDAndOrderID(itemId, orderID);
                    Toast.makeText(requireContext(), "Item deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "ItemCode does not match selected item's ID", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Method to retrieve itemId for a given itemName
    @SuppressLint("Range")
    private String getItemIdForItem(String itemName) {
        Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming "COLUMN_ITEM_ID" is the column name for item code
            return cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
        } else {
            return null;
        }

    }


    protected void initializeSwipeToDelete(OrderAdapter orderAdapter) {
        if (orderAdapter != null) {
            itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(orderAdapter));
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }


    class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {


        private OrderAdapter adapter;

        SwipeToDeleteCallback(OrderAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();

            if (isValidPosition(itemPosition)) {
                if (!orderDetails.isEmpty()) {
                    String itemName = orderDetails.get(itemPosition).getItemName();
                    ItemsByAgencyDB itemsByAgencyDB = new ItemsByAgencyDB(requireContext());
                    Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") String itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                        cursor.close();

                        // Delayed removal to avoid issues with RecyclerView
                        new Handler().postDelayed(() -> {
                            // Ensure the item position is still valid before proceeding
                            if (isValidPosition(itemPosition) && orderDetails.size() > itemPosition) {
                                // Update dataset first
                                deleteSelectedItem(itemPosition, orderDetails, itemId);

                                // Notify the adapter about the specific change
                                orderAdapter1.notifyItemRemoved(itemPosition);
                                orderAdapter1.notifyItemRangeChanged(itemPosition, orderDetails.size());

                                showItemDeletedSnackbar(viewHolder.itemView);
                            }
                        }, 100); // Adjust the delay time as needed
                    } else {
                        handleItemCodeNotFound(itemName);
                    }
                }
            }
        }


        private boolean isValidPosition(int position) {
            return !orderDetails.isEmpty() && position >= 0 && position < orderDetails.size();
        }

        private void handleItemCodeNotFound(String itemName) {
            Log.d("ItemCodeNotFound", "Item code not found for item: " + itemName);
            // Handle the case where item code is not found
            // You may want to show a message or take appropriate action
        }

        private void showItemDeletedSnackbar(View view) {
           /* Snackbar snackbar = Snackbar.make(view, "Item deleted.", Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_delete,o
                // Undo action or handle cancellation if required
                // orderAdapter.restoreItem(itemPosition, agencyName, productName, deletedItemQuantity);
            });
            snackbar.show();*/
        }


      /*  private void showDeleteConfirmationDialog(int position, List<Order_history> submittedOrder) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CancelOrder.this);
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
                    ItemsByAgencyDB itemsByAgencyDB = new ItemsByAgencyDB(CancelOrder.this);
                    Cursor cursor = itemsByAgencyDB.readProdcutDataByName(itemName);

                    if (cursor != null && cursor.moveToFirst()) {
                        // Assuming "COLUMN_ITEM_CODE" is the column name for item code
                        itemId = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_ID));
                        cursor.close();
                        // Now you have the item code, you can use it as needed


                        // Continue with the deletion logic or other actions...
                        // Do not delete the item here; instead, pass the position and item code to deleteSelectedItem
                        deleteSelectedItem(position, submittedOrder, itemId);
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
        }*/

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.recycler_view_item_swipe_right_background))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_white_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.recycler_view_item_swipe_right_background))
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
 /*   @Override
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
        orderAdapter1=null;
        itemName=null;
        orderID=null;
        productID=null;
        itemId=null;
        selectedProductID=null;

        Intent intent=new Intent(requireContext(),ModifyOrder.class);
        startActivity(intent);
        requireActivity().finish();

    }*/

}

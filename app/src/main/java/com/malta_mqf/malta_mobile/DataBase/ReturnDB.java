package com.malta_mqf.malta_mobile.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.google.gson.annotations.SerializedName;
import com.malta_mqf.malta_mobile.Model.ReturnOrderItemLevelDetails;
import com.malta_mqf.malta_mobile.Model.ReturnOrderLevelDetails;
import com.malta_mqf.malta_mobile.Model.ReturnWithoutInvoiceDetails;
import com.malta_mqf.malta_mobile.Model.creditNotebean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReturnDB  extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "MyReturnsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_returns";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_ORDERID = "OrderId";
    public static final String COLUMN_USERID = "UserId";
    public static final String COLUMN_VANID = "VanId";
     public static final String COLUMN_CUSTOMER_CODE="CustomerCode";
    public static final String COLUMN_OUTLETID = "OutletId";
    public static final String COLUMN_OUTLETCODE = "OutletCode";
    public static final String COLUMN_AGENCYID = "AgencyId";
    public static final String COLUMN_PRODUCTID = "productId";
    public static final String COLUMN_ITEMNAME = "itemName";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_ITEMCODE = "itemCode";

    public static final String COLUMN_RETURN_QTY = "RETURN_Qty";
    public static final String COLUMN_DEL_QTY="del_qty";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_DATE_TIME = "DateTime";
    public static final String COLUMN_INVOICE_NO = "invoiceNo";
    public static final String COLUMN_SIGNATURE="sign";
    public static final String COLUMN_INVOICE_BILL="bill";
    public static final String COLUMN_CREDIT_NOTE = "creditNote";
    public static final String COLUMN_TOTAL_QTY_OF_OUTLET = "totalQty";
    public static final String COLUMN_TOTAL_NET_AMOUNT = "totalNetAmount";
    public static final String COLUMN_TOTAL_VAT_AMOUNT = "totlatvatAmount";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT = "totalGrossAmt";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE="totalGrossPayable";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT_WITHOUT_REBATE = "totalGrossAmtWithoutRebate";
    public static final String COLUMN_DISC = "disc";
    public static final String COLUMN_NET = "NET";
    public static final String COLUMN_VAT_PERCENT = "VAT";
    public static final String COLUMN_VAT_AMT = "Vat_amt";
    public static final String COLUMN_GROSS = "ItemsTotal_Gross";
    public static final String COLUMN_RETURN_REASON="Return_reason";
    public static final String COLUMN_REFERENCE_NO="reference_no";
    public static final String COLUMN_COMMENTS="comments";
    SQLiteDatabase db;
    public ReturnDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ORDERID + " TEXT, " +
                        COLUMN_USERID + " TEXT, " +
                        COLUMN_VANID + " TEXT, " +
                        COLUMN_CUSTOMER_CODE + " TEXT, " +
                        COLUMN_OUTLETID + " TEXT, " +
                        COLUMN_OUTLETCODE + " TEXT, " +
                        COLUMN_PRODUCTID + " TEXT, " +
                        COLUMN_AGENCYID + " TEXT, " +
                        COLUMN_ITEMNAME + " TEXT, " +  // Added column for item name
                        COLUMN_PRICE + " TEXT, " +  // Added column for price
                        COLUMN_ITEMCODE + " TEXT, " +
                        COLUMN_DEL_QTY + " TEXT, " +
                        COLUMN_RETURN_QTY + " TEXT, " +
                        COLUMN_STATUS + " TEXT, " +
                        COLUMN_INVOICE_NO + " TEXT, " +
                        COLUMN_CREDIT_NOTE + " TEXT, " +
                        COLUMN_DISC + " TEXT, " +
                        COLUMN_NET + " TEXT, " +
                        COLUMN_VAT_PERCENT + " TEXT, " +
                        COLUMN_VAT_AMT + " TEXT, " +
                        COLUMN_GROSS + " TEXT, " +
                        COLUMN_TOTAL_QTY_OF_OUTLET + " TEXT, " +
                        COLUMN_TOTAL_NET_AMOUNT + " TEXT, " +
                        COLUMN_TOTAL_VAT_AMOUNT + " TEXT, " +
                        COLUMN_TOTAL_GROSS_AMOUNT + " TEXT, " +
                        COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE + " TEXT, "+
                        COLUMN_RETURN_REASON + " TEXT, " +
                        COLUMN_SIGNATURE + " TEXT, " +
                        COLUMN_INVOICE_BILL + " TEXT, " +
                        COLUMN_REFERENCE_NO + " TEXT, " +
                        COLUMN_COMMENTS + " TEXT, " +
                        COLUMN_DATE_TIME + " TEXT ); ";

        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean returnItems(String orderID, String invoiceno, String creditNoteId, String userid, String vanid, String customerCode, String outID, List<creditNotebean> creditNotebeanListList, String totalQty, String totalNet, String totalVat, String totalGross, String totalGrosspayable, byte[] billImage, String status, String reference, String comments, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String referenceCheckQuery = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_REFERENCE_NO + " = ?";
        Cursor cursor = db.rawQuery(referenceCheckQuery, new String[]{reference});

// Allow storage even if the reference is empty
        if (!TextUtils.isEmpty(reference) && cursor.moveToFirst()) {
            // Reference number already exists
            Toast.makeText(context, "Reference number already exists. Please use a unique reference number.", Toast.LENGTH_LONG).show();
            cursor.close();
            return false;
        }

        cursor.close();
        // Basic validations for required fields
        if (isInvalid(orderID, "Order ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(invoiceno, "Invoice Number is missing. Please try again from beginning.")) return false;
        if (isInvalid(creditNoteId, "Credit Note ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(userid, "User ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(vanid, "Van ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(customerCode, "Customer Code is missing. Please try again from beginning.")) return false;
        if (isInvalid(outID, "Outlet ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalQty, "Total Quantity is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalNet, "Total Net Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalVat, "Total VAT Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalGross, "Total Gross Amount is missing. Please try again from beginning.")) return false;

        // Prepare string builders for concatenated fields
        StringBuilder itemNamesBuilder = new StringBuilder();
        StringBuilder priceBuilder = new StringBuilder();
        StringBuilder discBuilder = new StringBuilder();
        StringBuilder netBuilder = new StringBuilder();
        StringBuilder vatPerBuilder = new StringBuilder();
        StringBuilder vatAmtBuilder = new StringBuilder();
        StringBuilder grossBuilder = new StringBuilder();
        StringBuilder itemCodeBuilder = new StringBuilder();
        StringBuilder delquantities = new StringBuilder();
        StringBuilder returnquantitiesBuilder = new StringBuilder();
        StringBuilder reasonsBuilder = new StringBuilder();

        // Validate each item in the creditNotebeanListList
        int size = creditNotebeanListList.size();
        for (int i = 0; i < size; i++) {
            creditNotebean productInfo = creditNotebeanListList.get(i);

            // Validate fields in creditNotebean
            if (isInvalid(productInfo.getItemName(), "Item Name is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getSellingprice(), "Selling Price is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getDisc(), "Discount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getNet(), "Net Amount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getVat_percent(), "VAT Percentage is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getVat_amt(), "VAT Amount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getGross(), "Gross Amount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getDelqty(), "Delivered Quantity is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getReturnQty(), "Return Quantity is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getRetrunreason(), "Return Reason is missing. Please try again from beginning.")) return false;
            if (isInvalid(productInfo.getItemCode(), "Item Code is missing. Please try again from beginning.")) return false;

            // Append values to respective builders
            itemNamesBuilder.append(productInfo.getItemName());
            priceBuilder.append(productInfo.getSellingprice());
            discBuilder.append(productInfo.getDisc());
            netBuilder.append(productInfo.getNet());
            vatPerBuilder.append(productInfo.getVat_percent());
            vatAmtBuilder.append(productInfo.getVat_amt());
            grossBuilder.append(productInfo.getGross());
            delquantities.append(productInfo.getDelqty());
            returnquantitiesBuilder.append(productInfo.getReturnQty());
            reasonsBuilder.append(productInfo.getRetrunreason());
            itemCodeBuilder.append(productInfo.getItemCode());

            // Add a comma between values unless it's the last item
            if (i < size - 1) {
                itemNamesBuilder.append(",");
                priceBuilder.append(",");
                discBuilder.append(",");
                netBuilder.append(",");
                vatPerBuilder.append(",");
                vatAmtBuilder.append(",");
                grossBuilder.append(",");
                delquantities.append(",");
                returnquantitiesBuilder.append(",");
                reasonsBuilder.append(",");
                itemCodeBuilder.append(",");
            }
        }

        // Validate concatenated string lengths
        int itemCodeLength = itemCodeBuilder.toString().split(",").length;
        if (!isLengthValid(itemNamesBuilder.toString(), itemCodeLength, "Mismatch in number of item names. Please try again.")) return false;
        if (!isLengthValid(priceBuilder.toString(), itemCodeLength, "Mismatch in number of selling prices. Please try again.")) return false;
        if (!isLengthValid(discBuilder.toString(), itemCodeLength, "Mismatch in number of discounts. Please try again.")) return false;
        if (!isLengthValid(netBuilder.toString(), itemCodeLength, "Mismatch in number of net amounts. Please try again.")) return false;
        if (!isLengthValid(vatPerBuilder.toString(), itemCodeLength, "Mismatch in number of VAT percentages. Please try again.")) return false;
        if (!isLengthValid(vatAmtBuilder.toString(), itemCodeLength, "Mismatch in number of VAT amounts. Please try again.")) return false;
        if (!isLengthValid(grossBuilder.toString(), itemCodeLength, "Mismatch in number of gross amounts. Please try again.")) return false;
        if (!isLengthValid(delquantities.toString(), itemCodeLength, "Mismatch in number of delivered quantities. Please try again.")) return false;
        if (!isLengthValid(returnquantitiesBuilder.toString(), itemCodeLength, "Mismatch in number of return quantities. Please try again.")) return false;
        if (!isLengthValid(reasonsBuilder.toString(), itemCodeLength, "Mismatch in number of return reasons. Please try again.")) return false;

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_INVOICE_NO, invoiceno);
        cv.put(COLUMN_CREDIT_NOTE, creditNoteId);
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_CUSTOMER_CODE, customerCode);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_ITEMNAME, itemNamesBuilder.toString());
        cv.put(COLUMN_PRICE, priceBuilder.toString());
        cv.put(COLUMN_DISC, discBuilder.toString());
        cv.put(COLUMN_NET, netBuilder.toString());
        cv.put(COLUMN_VAT_PERCENT, vatPerBuilder.toString());
        cv.put(COLUMN_VAT_AMT, vatAmtBuilder.toString());
        cv.put(COLUMN_GROSS, grossBuilder.toString());
        cv.put(COLUMN_ITEMCODE, itemCodeBuilder.toString());
        cv.put(COLUMN_DEL_QTY, delquantities.toString());
        cv.put(COLUMN_RETURN_QTY, returnquantitiesBuilder.toString());
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalQty);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNet);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVat);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, totalGross);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, totalGrosspayable !=null ? totalGrosspayable : "N/A");
        cv.put(COLUMN_INVOICE_BILL, billImage);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_RETURN_REASON, reasonsBuilder.toString());
        cv.put(COLUMN_REFERENCE_NO, TextUtils.isEmpty(reference) ? "" : reference);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_DATE_TIME, dateTime);

        // Insert the entry into the database
        long result = db.insert(TABLE_NAME, null, cv);

        // Check insertion result
        if (result == -1) {
            Toast.makeText(context, "Please try again!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "Return Successful: " + orderID, Toast.LENGTH_SHORT).show();
            return true;  // Indicate success
        }
    }public boolean returnItemsWithoutInvoice(
            String creditNoteId, String userid, String vanid, String customerCode, String outID,String outletCode,
            List<creditNotebean> creditNotebeanListList, String totalQty, String totalNet,
            String totalVat, String totalGross, String totalGrosspayable, byte[] signatureImage,
            String status, String reference, String Comments, String dateTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Check for duplicate reference number
        String referenceCheckQuery = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_REFERENCE_NO + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(referenceCheckQuery, new String[]{reference});
            if (!TextUtils.isEmpty(reference) && cursor.moveToFirst()) {
                Toast.makeText(context, "Reference number already exists. Please use a unique reference number.", Toast.LENGTH_LONG).show();
                return false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // Validate required fields
        if (isInvalid(creditNoteId, "Credit Note ID is missing.")) return false;
        if (isInvalid(userid, "User ID is missing.")) return false;
        if (isInvalid(vanid, "Van ID is missing.")) return false;
        if (isInvalid(customerCode, "Customer Code is missing.")) return false;
        if (isInvalid(outID, "Outlet ID is missing.")) return false;
        if(isInvalid(outletCode,"OutletCode is missing.")) return false;
        if (isInvalid(totalQty, "Total Quantity is missing.")) return false;
        if (isInvalid(totalNet, "Total Net Amount is missing.")) return false;
        if (isInvalid(totalVat, "Total VAT Amount is missing.")) return false;
        if (isInvalid(totalGross, "Total Gross Amount is missing.")) return false;

        // Remove duplicates based on itemCode
        creditNotebeanListList = removeDuplicates(creditNotebeanListList);

        // Prepare concatenated values using String.join()
        String itemNames = creditNotebeanListList.stream().map(creditNotebean::getItemName).collect(Collectors.joining(","));
        String price = creditNotebeanListList.stream().map(creditNotebean::getSellingprice).collect(Collectors.joining(","));
        String disc = creditNotebeanListList.stream().map(creditNotebean::getDisc).collect(Collectors.joining(","));
        String net = creditNotebeanListList.stream().map(creditNotebean::getNet).collect(Collectors.joining(","));
        String vatPer = creditNotebeanListList.stream().map(creditNotebean::getVat_percent).collect(Collectors.joining(","));
        String vatAmt = creditNotebeanListList.stream().map(creditNotebean::getVat_amt).collect(Collectors.joining(","));
        String gross = creditNotebeanListList.stream().map(creditNotebean::getGross).collect(Collectors.joining(","));
        String itemCodes = creditNotebeanListList.stream().map(creditNotebean::getItemCode).collect(Collectors.joining(","));
        String delQuantities = creditNotebeanListList.stream().map(creditNotebean::getDelqty).collect(Collectors.joining(","));
        String returnQuantities = creditNotebeanListList.stream().map(creditNotebean::getReturnQty).collect(Collectors.joining(","));
        String reasons = creditNotebeanListList.stream().map(creditNotebean::getRetrunreason).collect(Collectors.joining(","));

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_INVOICE_NO, "N/A");
        cv.put(COLUMN_ORDERID, "N/A");
        cv.put(COLUMN_CREDIT_NOTE, creditNoteId);
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_CUSTOMER_CODE, customerCode);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_OUTLETCODE,outletCode);
        cv.put(COLUMN_ITEMNAME, itemNames);
        cv.put(COLUMN_PRICE, price);
        cv.put(COLUMN_DISC, disc);
        cv.put(COLUMN_NET, net);
        cv.put(COLUMN_VAT_PERCENT, vatPer);
        cv.put(COLUMN_VAT_AMT, vatAmt);
        cv.put(COLUMN_GROSS, gross);
        cv.put(COLUMN_ITEMCODE, itemCodes);
        cv.put(COLUMN_DEL_QTY, delQuantities);
        cv.put(COLUMN_RETURN_QTY, returnQuantities);
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalQty);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNet);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVat);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, totalGross);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, totalGrosspayable != null ? totalGrosspayable : "N/A");
        cv.put(COLUMN_SIGNATURE, signatureImage);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_RETURN_REASON, reasons);
        cv.put(COLUMN_REFERENCE_NO, TextUtils.isEmpty(reference) ? "" : reference);
        cv.put(COLUMN_COMMENTS, Comments);
        cv.put(COLUMN_DATE_TIME, dateTime);

        long result = db.insert(TABLE_NAME, null, cv);
        db.close(); // Close the database

        if (context != null) {
            Toast.makeText(context, result == -1 ? "Failed!" : "Return Successful", Toast.LENGTH_SHORT).show();
        }

        return result != -1;
    }

    // Helper method to remove duplicates
    private List<creditNotebean> removeDuplicates(List<creditNotebean> creditNotebeanList) {
        Map<String, creditNotebean> uniqueItems = new LinkedHashMap<>();
        for (creditNotebean item : creditNotebeanList) {
            uniqueItems.put(item.getItemCode(), item);
        }
        return new ArrayList<>(uniqueItems.values());
    }


    // Helper method to validate input
    private boolean isInvalid(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    // Helper method to validate length of concatenated strings
    private boolean isLengthValid(String value, int expectedLength, String errorMessage) {
        String[] elements = value.split(",");

        // Filter out empty strings
        List<String> filteredElements = new ArrayList<>();
        for (String element : elements) {
            if (!element.trim().isEmpty()) {  // Ignore empty elements
                filteredElements.add(element);
            }
        }

        // Check the length of the filtered list
        if (filteredElements.size() != expectedLength) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void updateReturn(String orderID, String userid, String vanid, String customerCode, String outID, List<creditNotebean> creditNotebeanListList, String totalQty, String net, String disc, String vat_amt, String vat_percent, String totalNet, String totalVat, String gross, String totalGross, String status, String reason, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        StringBuilder itemNamesBuilder = new StringBuilder();
        StringBuilder priceBuilder = new StringBuilder();
        StringBuilder discBuilder = new StringBuilder();
        StringBuilder netBuilder = new StringBuilder();
        StringBuilder vatPerBuilder = new StringBuilder();
        StringBuilder vatAmtBuilder = new StringBuilder();
        StringBuilder grossBuilder = new StringBuilder();
        StringBuilder itemCodeBuilder = new StringBuilder();
        StringBuilder returnquantitiesBuilder = new StringBuilder();
        StringBuilder reasonsBuilder = new StringBuilder();


        for (creditNotebean productInfo : creditNotebeanListList) {
            itemNamesBuilder.append(productInfo.getItemName()).append(",");
            priceBuilder.append(productInfo.getSellingprice()).append(",");
            discBuilder.append(productInfo.getDisc()).append(",");
            netBuilder.append(productInfo.getNet()).append(",");
            vatPerBuilder.append(productInfo.getVat_percent()).append(",");
            vatAmtBuilder.append(productInfo.getVat_amt()).append(",");
            grossBuilder.append(productInfo.getGross()).append(",");
            returnquantitiesBuilder.append(productInfo.getReturnQty()).append(",");
            reasonsBuilder.append(productInfo.getRetrunreason()).append(",");
            itemCodeBuilder.append(productInfo.getItemCode()).append(",");
        }
        // Remove the last comma from each string builder
        String itemNames = itemNamesBuilder.length() > 0 ? itemNamesBuilder.substring(0, itemNamesBuilder.length() - 1) : "";
        String price = priceBuilder.length() > 0 ? priceBuilder.substring(0, priceBuilder.length() - 1) : "";
        String quantities = returnquantitiesBuilder.length() > 0 ? returnquantitiesBuilder.substring(0, returnquantitiesBuilder.length() - 1) : "";
        String discs = discBuilder.length() > 0 ? discBuilder.substring(0, discBuilder.length() - 1) : "";
        String nets = netBuilder.length() > 0 ? netBuilder.substring(0, netBuilder.length() - 1) : "";
        String vats = vatAmtBuilder.length() > 0 ? vatAmtBuilder.substring(0, vatAmtBuilder.length() - 1) : "";
        String vatpers = vatPerBuilder.length() > 0 ? vatPerBuilder.substring(0, vatPerBuilder.length() - 1) : "";
        String reasonns = reasonsBuilder.length() > 0 ? reasonsBuilder.substring(0, reasonsBuilder.length() - 1) : "";
        String itemcodes= itemCodeBuilder.length() > 0 ? itemCodeBuilder.substring(0, itemCodeBuilder.length() - 1) : "";
        String grosss= grossBuilder.length() > 0 ? grossBuilder.substring(0, grossBuilder.length() - 1) : "";

        //    String ApprovedQTyStr = ApprovedQtyBuilder.length() > 0 ? ApprovedQtyBuilder.substring(0, ApprovedQtyBuilder.length() - 1) : "";
        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_CUSTOMER_CODE, customerCode);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_ITEMNAME, itemNames); // Concatenated item names
        cv.put(COLUMN_PRICE, price); // Concatenated prices
        cv.put(COLUMN_DISC, discs); // Concatenated discounts
        cv.put(COLUMN_NET, nets); // Concatenated net amounts
        cv.put(COLUMN_VAT_PERCENT, vatpers); // Concatenated VAT percentages
        cv.put(COLUMN_VAT_AMT, vats); // Concatenated VAT amounts
        cv.put(COLUMN_GROSS, grosss); // Concatenated gross amounts
        cv.put(COLUMN_ITEMCODE, itemcodes); // Concatenated item codes
        cv.put(COLUMN_RETURN_QTY, quantities); // Concatenated quantities
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNet);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVat);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, totalGross);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_RETURN_REASON, reasonns);
        cv.put(COLUMN_DATE_TIME, dateTime);
        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
            Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOldRecords() {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE datetime(" + COLUMN_DATE_TIME +
                ") <= datetime('now', '-7 days')";

        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL(query);
    }



    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


    public Cursor readoninvno(String invno){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_INVOICE_NO + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{invno});
        }
        return cursor;
    }

    public Cursor getOrdersBasedOnReturnStatus(String status1, String status2, String status3, String fromDate, String toDate) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use getReadableDatabase() if you're only performing a read operation

        try {
            // Query to get orders with status "status1", "status2", or "status3" within the date range
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE (" + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=?)" +
                    " AND " + COLUMN_DATE_TIME + " >= ? AND " +
                    COLUMN_DATE_TIME + " <= ?";
            return db.rawQuery(query, new String[]{status1, status2, status3, fromDate, toDate});
        } catch (Exception e) {
            // Handle any exceptions that may occur during query execution
            e.printStackTrace();
            return null; // Return null to indicate an error occurred
        }
    }
    public Cursor getOrdersBasedOnCreditOrderId(String creditNoteId) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use getReadableDatabase() for read operations

        try {
            // Query to find orders where the credit note ID matches the provided creditNoteId
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_CREDIT_NOTE + "=?";
            return db.rawQuery(query, new String[]{creditNoteId}); // Supply creditNoteId for the query parameter
        } catch (Exception e) {
            // Handle any exceptions that may occur during query execution
            e.printStackTrace();
            return null; // Return null to indicate an error occurred
        }
    }
    public Cursor Returnreadonstatus(String status){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }

    public void updatereturnOrderStatusAfterDeliver(String invoiceno, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        // Define the selection criteria (where clause)
        String selection = COLUMN_INVOICE_NO + " = ?";
        String[] selectionArgs = {invoiceno};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            //  Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
            //  Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
    public void updatereturnOrderWithoutInvoiceStatusAfterDeliver(String outletid, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        // Define the selection criteria (where clause)
        String selection = COLUMN_OUTLETID + " = ?";
        String[] selectionArgs = {outletid};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            //  Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
            //  Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("Range")
    public String getLastInvoiceNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastInvoiceNumber = "";

        // Query to extract numeric portion from the invoice number and find the max
        String query = "SELECT " + COLUMN_CREDIT_NOTE + " FROM " + TABLE_NAME +
                " ORDER BY CAST(SUBSTR(" + COLUMN_CREDIT_NOTE + ", 12) AS INTEGER) DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            lastInvoiceNumber = cursor.getString(cursor.getColumnIndex(COLUMN_CREDIT_NOTE));  // Get the last invoice number
        }

        cursor.close();
        return lastInvoiceNumber;
    }
    public void insertMultipleReturnedItems(List<ReturnOrderItemLevelDetails> deliveredItemsList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Map<String, Integer> orderQtyMap = new HashMap<>();
            for (ReturnOrderItemLevelDetails deliveredItem : deliveredItemsList) {
                String orderid = deliveredItem.getOrderid();
                String reutnedqtyStr=deliveredItem.getReturnedQty();
                int deliveredQty = 0;

                if(reutnedqtyStr!=null && !reutnedqtyStr.isEmpty()){
                    try{
                        deliveredQty=Integer.parseInt(reutnedqtyStr);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                // Get the existing totalQty for this orderid, or 0 if it's not present
                int totalQty = orderQtyMap.getOrDefault(orderid, 0);

                // Add current deliveredQty to totalQty
                totalQty += deliveredQty;

                // Update the map with the new totalQty
                orderQtyMap.put(orderid, totalQty);

                addReturnItemsTransaction(db,
                        deliveredItem.getReason(),
                        deliveredItem.getItemName(),
                        deliveredItem.getItem_id(),
                        deliveredItem.getReturnedDatetime(),
                        deliveredItem.getReturnitmetotalprice(),
                        deliveredItem.getReturnvatamount(),
                        deliveredItem.getOrderid(),
                        deliveredItem.getInvoiceno(),
                        deliveredItem.getItemCode(),
                        deliveredItem.getReturnedQty(),
                        deliveredItem.getSellingPrice(),
                        deliveredItem.getReturnnetamount(),
                        "0.00",
                        String.valueOf(totalQty)
                );

                // Optionally: update progress after each insertion, if necessary
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    @SuppressLint("Range")
    private void addReturnItemsTransaction(SQLiteDatabase db, String reason,String itemname,String itemid, String returnedDatetime, String returnitmetotalprice, String returnvatamount, String orderid, String invoiceno, String itemCode, String returnedQty,String sellingprice, String returnnetamount,String rebate,String totalQty) {

        // Fetch the existing data for the given orderid or invoiceno
        String[] columns = {COLUMN_RETURN_REASON,COLUMN_ITEMNAME,COLUMN_PRODUCTID,COLUMN_DISC, COLUMN_GROSS,COLUMN_ITEMCODE,COLUMN_RETURN_QTY,COLUMN_PRICE,COLUMN_NET,COLUMN_VAT_AMT};

        Cursor cursor = db.query(TABLE_NAME, columns, COLUMN_ORDERID + "=? AND " + COLUMN_INVOICE_NO + "=?",
                new String[]{orderid, invoiceno}, null, null, null);

        // Initialize StringBuilder for each field
        StringBuilder reasonBuilder = new StringBuilder();
        StringBuilder itemnameBuilder = new StringBuilder();
        StringBuilder itemidBuilder = new StringBuilder();
        StringBuilder GrossBuilder = new StringBuilder();
        StringBuilder itemCodeBuilder = new StringBuilder();
        StringBuilder returnqtyBuilder = new StringBuilder();
        StringBuilder netbuilder = new StringBuilder();
        StringBuilder vatamountbuilder = new StringBuilder();
        StringBuilder rebateBuilder = new StringBuilder();
        StringBuilder returnsellingpricebuilder=new StringBuilder();

        // Check if the record already exists
        if (cursor.moveToFirst()) {
            // If data exists for this orderId/invoiceNo, append it to existing values
            reasonBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_RETURN_REASON))).append(",");
            itemnameBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_ITEMNAME))).append(",");
            itemidBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID))).append(",");
            vatamountbuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_VAT_AMT))).append(",");
            GrossBuilder.append(cursor.getString(cursor.getColumnIndex( COLUMN_GROSS))).append(",");
            itemCodeBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_ITEMCODE))).append(",");
            returnqtyBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_RETURN_QTY))).append(",");
            netbuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_NET))).append(",");
            rebateBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_DISC))).append(",");
            returnsellingpricebuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PRICE))).append(",");
        }

        // Append new values
        reasonBuilder.append(reason).append(",");
        itemnameBuilder.append(itemname).append(",");
        itemidBuilder.append(itemid).append(",");
        vatamountbuilder.append(returnvatamount).append(",");
        GrossBuilder.append(returnitmetotalprice).append(",");
        netbuilder.append(returnnetamount).append(",");
        itemCodeBuilder.append(itemCode).append(",");
        returnqtyBuilder.append(returnedQty).append(",");
        rebateBuilder.append(rebate).append(",");
        returnsellingpricebuilder.append(sellingprice).append(",");

        // Remove trailing commas from all builders
        reason = removeTrailingComma(reasonBuilder);
        itemname=removeTrailingComma(itemnameBuilder);
        itemid=removeTrailingComma(itemidBuilder);
        returnvatamount = removeTrailingComma(vatamountbuilder);
        returnnetamount = removeTrailingComma(netbuilder);
        returnitmetotalprice = removeTrailingComma(GrossBuilder);
        itemCode = removeTrailingComma(itemCodeBuilder);
        returnedQty = removeTrailingComma(returnqtyBuilder);
        rebate = removeTrailingComma(rebateBuilder);
        sellingprice=removeTrailingComma(returnsellingpricebuilder);

        // ContentValues for updating the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_GROSS, returnitmetotalprice != null ? returnitmetotalprice : "");  // Default to empty string if null
        contentValues.put(COLUMN_VAT_AMT, returnvatamount != null ? returnvatamount : "");
        contentValues.put(COLUMN_RETURN_REASON, reason != null ? reason : "");
        contentValues.put(COLUMN_NET, returnnetamount != null ? returnnetamount : "");
        contentValues.put(COLUMN_ORDERID, orderid != null ? orderid : "");
        contentValues.put(COLUMN_INVOICE_NO, invoiceno != null ? invoiceno : "");
        contentValues.put(COLUMN_ITEMCODE, itemCode != null ? itemCode : "");
        contentValues.put(COLUMN_RETURN_QTY, returnedQty != null ? returnedQty : "");
        contentValues.put(COLUMN_ITEMNAME,itemname !=null ? itemname :"");
        contentValues.put(COLUMN_PRODUCTID,itemid !=null ? itemid : "");
        contentValues.put(COLUMN_DISC, rebate != null ? rebate : "0.00");
        contentValues.put(COLUMN_PRICE,sellingprice!=null ? sellingprice : "0.00");
        contentValues.put(COLUMN_TOTAL_QTY_OF_OUTLET,totalQty);

        // Insert or update the database
        if (cursor.moveToFirst()) {
            // If record exists, update
            db.update(TABLE_NAME, contentValues, COLUMN_ORDERID + "=? AND " + COLUMN_INVOICE_NO + "=?", new String[]{orderid, invoiceno});
        } else {
            // If no record exists, insert a new row
            db.insert(TABLE_NAME, null, contentValues);
        }

        cursor.close();
    }
    public void insertMultipleReturnDetails(List<ReturnOrderLevelDetails> returnDetailsList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ReturnOrderLevelDetails returnDetails : returnDetailsList) {

                addReturnItemsOrderLevelTransactions(db,
                        returnDetails.getReturnedDatetime(),
                        returnDetails.getOutletId(),returnDetails.getRefno(),
                        returnDetails.getComments(), returnDetails.getReturntotalnetamount(),
                        returnDetails.getReturntotalvatamount(), returnDetails.getOrderid(),
                        returnDetails.getInvoiceno(), returnDetails.getReturnedDate(),
                        returnDetails.getCreditNoteid(), returnDetails.getCreditnotetotalamount(),
                        returnDetails.getCreditwthoutrebate(), returnDetails.getReturncount(),returnDetails.getCustomerCode());

                // Optionally: update progress after each insertion
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void addReturnItemsOrderLevelTransactions(SQLiteDatabase db, String returnedDatetime,String outletid, String refno, String comments,
                                           String returntotalnetamount, String returntotalvatamount, String orderid,
                                           String invoiceno, String returnedDate, String creditNoteid,
                                           String creditnotetotalamount, String creditwthoutrebate, String returncount,String customercode) {

        // Fetch the existing data for the given orderid or invoiceno



        // ContentValues for updating the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE_TIME, returnedDatetime != null ? returnedDatetime : "");
        contentValues.put(COLUMN_OUTLETID,outletid!=null  ? outletid : "");
        contentValues.put(COLUMN_REFERENCE_NO, (refno != null && !refno.isEmpty()) ? refno : "");
        contentValues.put(COLUMN_COMMENTS, (comments != null && !comments.isEmpty()) ? comments : "");
        contentValues.put(COLUMN_TOTAL_NET_AMOUNT, returntotalnetamount != null ? returntotalnetamount : "");
        contentValues.put(COLUMN_TOTAL_VAT_AMOUNT, returntotalvatamount != null ? returntotalvatamount : "");
        contentValues.put(COLUMN_ORDERID, orderid != null ? orderid : "");
       // contentValues.put(COLUMN_INVOICE_NO, invoiceno != null ? invoiceno : "");
        contentValues.put(COLUMN_CREDIT_NOTE, creditNoteid != null ? creditNoteid : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, creditnotetotalamount != null ? creditnotetotalamount : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT, creditwthoutrebate != null ? creditwthoutrebate : "");
       contentValues.put(COLUMN_CUSTOMER_CODE,customercode.toLowerCase());
        contentValues.put(COLUMN_STATUS,"RETURN DONE");
        // Insert or update the database
        db.update(TABLE_NAME, contentValues, COLUMN_INVOICE_NO + " = ?", new String[]{invoiceno});

    }


    public void insertReturnWithoutInvoice(List<ReturnWithoutInvoiceDetails> returnWithoutInvoiceList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Map<String, Integer> orderQtyMap = new HashMap<>();
            for (ReturnWithoutInvoiceDetails returnDetails : returnWithoutInvoiceList) {
                String orderid = returnDetails.getCreditNoteid();

                // Handle potential null or empty deliveredQty values
                String deliveredQtyStr = returnDetails.getReturnedQty();
                int deliveredQty = 0; // Default value

                // Check if deliveredQty is not null and not empty before parsing
                if (deliveredQtyStr != null && !deliveredQtyStr.isEmpty()) {
                    try {
                        deliveredQty = Integer.parseInt(deliveredQtyStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace(); // Log the error and keep deliveredQty as 0
                    }
                }

                // Get the existing totalQty for this orderid, or 0 if it's not present
                int totalQty = orderQtyMap.getOrDefault(orderid, 0);

                // Add current deliveredQty to totalQty
                totalQty += deliveredQty;

                // Update the map with the new totalQty
                orderQtyMap.put(orderid, totalQty);


                addReturnWithoutInvoiceTransaction(db,
                        returnDetails.getReason(),
                        returnDetails.getItemName(),
                        returnDetails.getItem_id(),
                        returnDetails.getAgencyCode(),
                        returnDetails.getOutletId(),// reason for return
                        returnDetails.getReturnedDatetime(),          // return datetime
                        returnDetails.getRefno(),                    // reference number
                        returnDetails.getComments(),                 // comments
                        returnDetails.getCustomerName(),             // customer name
                        returnDetails.getReturntotalnetamount(),      // total net amount of return
                        returnDetails.getReturntotalvatamount(),      // total VAT amount of return
                        returnDetails.getSellingPrice(),             // selling price of item
                        returnDetails.getCustomerCode(),             // customer code
                        returnDetails.getVanId(),                    // van ID
                        returnDetails.getOutletId(),               // outlet code
                        returnDetails.getOrderid(),                  // order ID
                        returnDetails.getInvoiceno(),                // invoice number
                        returnDetails.getReturnedDate(),             // returned date
                        returnDetails.getItemCode(),                 // item code
                        returnDetails.getReturnedQty(),              // quantity returned
                        returnDetails.getReturnitemtotalprice(),     // total price for returned item
                        returnDetails.getReturnnetamount(),          // net amount for return
                        returnDetails.getReturnvatamount(),          // VAT amount for return
                        returnDetails.getVat(),                      // VAT percentage
                        returnDetails.getCreditNoteid(),             // credit note ID
                        returnDetails.getCreditnotetotalamount(),    // total amount of credit note
                        returnDetails.getCreditwthoutrebate(),       // credit without rebate
                        returnDetails.getReturncount() ,
                        "0.00",
                        String.valueOf(totalQty)// count of returns
                );
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    @SuppressLint("Range")
    private void addReturnWithoutInvoiceTransaction(SQLiteDatabase db, String reason,String itemname,String itemid,String agencyid,String outletid, String returnedDatetime, String refno, String comments,
                                                    String customerName, String returntotalnetamount, String returntotalvatamount,
                                                    String sellingPrice, String customerCode, String vanId, String outletCode,
                                                    String orderid, String invoiceno, String returnedDate, String itemCode, String returnedQty,
                                                    String returnitemtotalprice, String returnnetamount, String returnvatamount,
                                                    String vat, String creditNoteid, String creditnotetotalamount, String creditwthoutrebate,
                                                    String returncount,String rebate,String totalQty) {
        String[] columns = {COLUMN_RETURN_REASON,COLUMN_ITEMNAME,COLUMN_PRODUCTID,COLUMN_AGENCYID,COLUMN_DISC, COLUMN_PRICE,COLUMN_ITEMCODE,COLUMN_RETURN_QTY,COLUMN_NET,COLUMN_VAT_AMT,COLUMN_VAT_PERCENT,COLUMN_GROSS};

        Cursor cursorA = db.query(TABLE_NAME, columns, COLUMN_CREDIT_NOTE + "=?",
                new String[]{creditNoteid}, null, null, null);


        StringBuilder itemnameBuilder = new StringBuilder();
        StringBuilder itemidBuilder = new StringBuilder();
        StringBuilder agencyidBuilder = new StringBuilder();
        StringBuilder returnReasonBuilder = new StringBuilder();
        StringBuilder sellingPriceBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder returnedQtyBuilder = new StringBuilder();
        StringBuilder netAmountBuilder = new StringBuilder();
        StringBuilder itemtotalpricebuilder=new StringBuilder();
        StringBuilder vatAmountBuilder = new StringBuilder();
        StringBuilder vatPercentBuilder=new StringBuilder();
        StringBuilder rebateBuilder = new StringBuilder();
        if (cursorA.moveToFirst()) {
            // If data exists for this orderId/invoiceNo, append it to existing values
            itemnameBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_ITEMNAME))).append(",");
            itemidBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_PRODUCTID))).append(",");
            agencyidBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_AGENCYID))).append(",");
            returnReasonBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_RETURN_REASON))).append(",");
            sellingPriceBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_PRICE))).append(",");
            itemCodesBuilder.append(cursorA.getString(cursorA.getColumnIndex( COLUMN_ITEMCODE))).append(",");
            returnedQtyBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_RETURN_QTY))).append(",");
            netAmountBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_NET))).append(",");
            vatAmountBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_VAT_AMT))).append(",");
            vatPercentBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_VAT_PERCENT))).append(",");
            itemtotalpricebuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_GROSS))).append(",");
            rebateBuilder.append(cursorA.getString(cursorA.getColumnIndex(COLUMN_DISC))).append(",");

        }

        itemnameBuilder.append(itemname).append(",");
        itemidBuilder.append(itemid).append(",");
        agencyidBuilder.append(agencyid).append(",");
        returnReasonBuilder.append(reason).append(",");
        sellingPriceBuilder.append(sellingPrice).append(",");
        itemCodesBuilder.append(itemCode).append(",");
        returnedQtyBuilder.append(returnedQty).append(",");
        netAmountBuilder.append(returnnetamount).append(",");
        vatAmountBuilder.append(returnvatamount).append(",");
        itemtotalpricebuilder.append(returnitemtotalprice).append(",");
        vatPercentBuilder.append(vat).append(",");
        rebateBuilder.append(rebate).append(",");


        // Remove trailing commas from all builders
        itemname=removeTrailingComma(itemnameBuilder);
        itemid=removeTrailingComma(itemidBuilder);
        agencyid=removeTrailingComma(agencyidBuilder);
        reason = removeTrailingComma(returnReasonBuilder);
        sellingPrice = removeTrailingComma(sellingPriceBuilder);
        itemCode = removeTrailingComma(itemCodesBuilder);
        returnedQty = removeTrailingComma(returnedQtyBuilder);
        returnnetamount=removeTrailingComma(netAmountBuilder);
        returnitemtotalprice = removeTrailingComma(itemtotalpricebuilder);
        returnvatamount = removeTrailingComma(vatAmountBuilder);
        vat=removeTrailingComma(vatPercentBuilder);
        rebate = removeTrailingComma(rebateBuilder);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ITEMNAME,itemname !=null ? itemname : "");
        contentValues.put(COLUMN_PRODUCTID,itemid !=null ? itemid : "");
        contentValues.put(COLUMN_AGENCYID,agencyid !=null ? agencyid : "");
        contentValues.put(COLUMN_RETURN_REASON, reason != null ? reason : "");
        contentValues.put(COLUMN_OUTLETID,outletid!=null ? outletid:"");
        contentValues.put(COLUMN_DATE_TIME, returnedDatetime != null ? returnedDatetime : "");
        contentValues.put(COLUMN_REFERENCE_NO, refno != null ? refno : "");
        contentValues.put(COLUMN_COMMENTS, comments != null ? comments : "");

        contentValues.put(COLUMN_TOTAL_NET_AMOUNT, returntotalnetamount != null ? returntotalnetamount : "");
        contentValues.put(COLUMN_TOTAL_VAT_AMOUNT, returntotalvatamount != null ? returntotalvatamount : "");
        contentValues.put(COLUMN_PRICE, sellingPrice != null ? sellingPrice : "");
        contentValues.put(COLUMN_CUSTOMER_CODE, customerCode.toLowerCase() != null ? customerCode.toLowerCase() : "");
        contentValues.put(COLUMN_VANID, vanId != null ? vanId : "");
        contentValues.put(COLUMN_OUTLETID, outletCode != null ? outletCode : "");

        contentValues.put(COLUMN_ORDERID, orderid != null ? orderid : "");
        contentValues.put(COLUMN_INVOICE_NO, invoiceno != null ? invoiceno : "");
        contentValues.put(COLUMN_ITEMCODE, itemCode != null ? itemCode : "");
        contentValues.put(COLUMN_RETURN_QTY, returnedQty != null ? returnedQty : "");
        contentValues.put(COLUMN_GROSS, returnitemtotalprice != null ? returnitemtotalprice : "");
        contentValues.put(COLUMN_NET, returnnetamount != null ? returnnetamount : "");
        contentValues.put(COLUMN_VAT_AMT, returnvatamount != null ? returnvatamount : "");
        contentValues.put(COLUMN_VAT_PERCENT, vat != null ? vat : "");
        contentValues.put(COLUMN_CREDIT_NOTE, creditNoteid != null ? creditNoteid : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, creditnotetotalamount != null ? creditnotetotalamount : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT, creditwthoutrebate != null ? creditwthoutrebate : "");
       contentValues.put(COLUMN_TOTAL_QTY_OF_OUTLET,totalQty);
        contentValues.put(COLUMN_DISC, rebate != null ? rebate : "0.00");
        contentValues.put(COLUMN_STATUS,"RETURN DONE");

        // Insert into the database
        if (cursorA.moveToFirst()) {
            // If record exists, update
            db.update(TABLE_NAME, contentValues,   COLUMN_CREDIT_NOTE + "= ?", new String[]{creditNoteid});
        } else {
            // If no record exists, insert a new row
            db.insert(TABLE_NAME, null, contentValues);
        }

    }

    private String removeTrailingComma(StringBuilder builder) {
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }


    public double getTotalReturnAmountByDate(String startDate, String endDate, @Nullable String outletId) {
        double totalReturnAmount = 0.0; // Change from String to double
        System.out.println("startDate: " + startDate + ", endDate: " + endDate + ", outletId: " + outletId);
        // Ensure the database connection is open
        if (db == null || !db.isOpen()) {
            Log.e("ReturnDB", "Database is not open or not initialized.");
            db = this.getWritableDatabase();
        }

        // Base query
        String query = "SELECT SUM(" + COLUMN_TOTAL_NET_AMOUNT + ") FROM " + TABLE_NAME +
                " WHERE " + COLUMN_DATE_TIME + " BETWEEN ? AND ?";

        List<String> args = new ArrayList<>();
        args.add(startDate);
        args.add(endDate);

        // Add optional outletId filter
        if (outletId != null && !outletId.isEmpty()) {
            query += " AND " + COLUMN_OUTLETID + " = ?";
            args.add(outletId);
        }

        Log.d("SQL Query", "Query: " + query + ", Params: " + args);

        Cursor cursor = null;
        try {
            // Execute the query with the parameters
            cursor = db.rawQuery(query, args.toArray(new String[0]));
            if (cursor != null && cursor.moveToFirst()) {
                // Fix: Use column index 0 since SUM() returns a single value
                totalReturnAmount = cursor.isNull(0) ? 0.0 : cursor.getDouble(0);
            }
        } catch (SQLiteException e) {
            Log.e("SQL Error", "Error executing query: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return totalReturnAmount;
    }

    public boolean isCreditNoteIdPresent(String creditnoteid) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_CREDIT_NOTE + " = ?;";

        SQLiteDatabase db = getReadableDatabase(); // Ensure this points to your database instance
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{creditnoteid});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                return count > 0; // If count > 0, creditnoteid exists
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        } finally {
            if (cursor != null) {
                cursor.close(); // Always close the cursor to avoid memory leaks
            }
            db.close(); // Close the database connection
        }
        return false; // Default to false if an error occurs or no match is found
    }

    public boolean checkDuplicateReferenceNumber(String reference) {

        if (TextUtils.isEmpty(reference)) {
            // Return false for empty reference
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String referenceCheckQuery = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_REFERENCE_NO + " = ?";
        Cursor cursor = db.rawQuery(referenceCheckQuery, new String[]{reference});
        boolean exists = cursor.moveToFirst(); // Check if the query returned a result
        cursor.close();

        if (exists) {
            // Reference number already exists
            Toast.makeText(context, "Reference number already exists. Please use a unique reference number.", Toast.LENGTH_LONG).show();
        }

        return exists;
    }

    public Map<String, Double> getReturnsByDateRangeAndOutlet(String fromDate, String toDate, @Nullable String outletId) {
        Map<String, Double> returnsMap = new HashMap<>();

        // Ensure the database is open before querying
        if (db == null || !db.isOpen()) {
            db = getReadableDatabase();
        }

        StringBuilder queryBuilder = new StringBuilder();
    /*queryBuilder.append("SELECT SUBSTR(")
            .append(COLUMN_DATE_TIME)
            .append(", 1, 10) AS date, SUM(")
            .append(COLUMN_TOTAL_NET_AMOUNT)
            .append(") AS total_returns FROM ")
            .append(TABLE_NAME)
            .append(" WHERE ")
            .append(COLUMN_DATE_TIME)
            .append(" BETWEEN ? AND ?");*/


        queryBuilder.append("SELECT SUBSTR(")
                .append(COLUMN_DATE_TIME)
                .append(", 1, 10) AS date, SUM(")
                .append(COLUMN_TOTAL_NET_AMOUNT)
                .append(") AS total_returns FROM ")
                .append(TABLE_NAME)
                .append(" WHERE ")
                .append(COLUMN_DATE_TIME)
                .append(" BETWEEN ? AND ?")
                .append(" AND UPPER(")
                .append(COLUMN_CUSTOMER_CODE)
                .append(") NOT IN ('UCS')");


        List<String> args = new ArrayList<>();
        args.add(fromDate);
        args.add(toDate);

        // Add optional outlet filter
        if (outletId != null && !outletId.isEmpty()) {
            queryBuilder.append(" AND ")
                    .append(COLUMN_OUTLETID)
                    .append(" = ?");
            args.add(outletId);
        }

        queryBuilder.append(" GROUP BY SUBSTR(")
                .append(COLUMN_DATE_TIME)
                .append(", 1, 10)")
                .append(" ORDER BY SUBSTR(")
                .append(COLUMN_DATE_TIME)
                .append(", 1, 10) ASC");

        String query = queryBuilder.toString();

        // Debugging Logs
        Log.d("SQL Query", "Executing: " + query + " with params: " + args);

        try (Cursor cursor = db.rawQuery(query, args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                returnsMap.put(cursor.getString(0), cursor.getDouble(1));
            }
        } catch (Exception e) {
            Log.e("DB Error", "Error fetching returns: " + e.getMessage());
        }

        Log.d("Returns Map", "Return Map: " + returnsMap);
        return returnsMap;
    }

    public Cursor getReusableReturnsByDateRange(String fromDate, String toDate, String outletId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Correct SQL query
        String query = "SELECT " + COLUMN_DATE_TIME + ", " + COLUMN_RETURN_REASON + ", " + COLUMN_NET +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_DATE_TIME + " BETWEEN ? AND ? " +
                "AND " + COLUMN_RETURN_REASON + " LIKE ? " +
                "AND UPPER(" + COLUMN_CUSTOMER_CODE + ") NOT IN ('UCS')";

        // Check if outletId is provided (non-null and non-empty)
        if (outletId != null && !outletId.isEmpty()) {
            query += " AND " + COLUMN_OUTLETID + " = ?";
            return db.rawQuery(query, new String[]{fromDate, toDate, "%Re-usable%", outletId});
        } else {
            return db.rawQuery(query, new String[]{fromDate, toDate, "%Re-usable%"});
        }
    }

    public Cursor getReturnsTotalByReusable(String fromDate, String toDate, String outletId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_NET + ", " + COLUMN_RETURN_REASON +
                " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_RETURN_REASON + " LIKE ?" +
                " AND " + COLUMN_DATE_TIME + " BETWEEN ? AND ?";

        List<String> args = new ArrayList<>();
        args.add("%Re-usable%");

        // Ensure full-day range is covered if dates are the same or even if different
        String fullFromDate = fromDate + " 00:00:00";
        String fullToDate = toDate + " 23:59:59";
        args.add(fullFromDate);
        args.add(fullToDate);

        if (outletId != null && !outletId.isEmpty()) {
            query += " AND " + COLUMN_OUTLETID + " = ?";
            args.add(outletId);
        }

        return db.rawQuery(query, args.toArray(new String[0]));
    }
}


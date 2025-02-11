package com.malta_mqf.malta_mobile.DataBase;

import static pub.devrel.easypermissions.RationaleDialogFragment.TAG;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;
import com.malta_mqf.malta_mobile.Model.DeliveredOrderItemLevelDetails;
import com.malta_mqf.malta_mobile.Model.DeliveredOrderLevelDetails;
import com.malta_mqf.malta_mobile.Model.NewOrderInvoiceBean;
import com.malta_mqf.malta_mobile.Model.ProductInfo;
import com.malta_mqf.malta_mobile.Model.ShowOrderForInvoiceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.http.PUT;

public class SubmitOrderDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "SubmitOrderDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_submit_order";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_ORDERID = "OrderId";
    public static final String COLUMN_USERID = "UserId";
    public static final String COLUMN_VANID = "VanId";
    // private static final String COLUMN_CUSTOMERID="CustomerId";
    public static final String COLUMN_OUTLETID = "OutletId";

    public static final String COLUMN_REFERENCE="Reference";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT_WITHOUT_REBATE = "Total_Gross_Amount_Without_Rebate";
    public static final String COLUMN_AGENCYID="AgencyId";
    public static final String COLUMN_PRODUCTID = "productId";
    public static final String COLUMN_ITEMCODE = "itemCode";
    public static final String COLUMN_ITEM_CATEGORY="item_category";
    public static final String COLUMN_ITEM_SUB_CATEGORY="item_sub_category";
    public static final String COLUMN_REQUESTED_QTY = "ReQ_Qty";
    public static final String COLUMN_APPROVED_QTY = "APPROVED_Qty";
    public static final String COLUMN_DELIVERED_QTY = "DELIVERD_Qty";
    public static final String COLUMN_STATUS = "Status";
    public static final String COLUMN_ORDERED_DATE_TIME = "DateTime";
    public static final String COLUMN_APPROVED_ORDER_TIME="app_dateTime";
    public static final String COLUMN_DELIVERED_DATE_TIME="del_dateTime";
    public static final String COLUMN_ISONLINE = "isOnline";
    public static final String COLUMN_INVOICE_NO="invoiceNo";
    public static final String COLUMN_SIGNATURE="sign";
    public static final String COLUMN_INVOICE_BILL="bill";
    public static final String COLUMN_TOTAL_QTY_OF_OUTLET="totalQty";
    public static final String COLUMN_TOTAL_ITEMS="total_items";
    public static final String COLUMN_TOTAL_NET_AMOUNT="totalNetAmount";
    public static final String COLUMN_TOTAL_VAT_AMOUNT="totlatvatAmount";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT="totalGrossAmt";
    public static final String COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE="totalGrossAmtPayable";
    public static final String COLUMN_PO_REF="PO_REFERENCE";
    public static final String COLUMN_PO_REF_NAME="PO_REFERENCE_NAME";
    public static final String COLUMN_PO_CREATED_DATE="PO_CREATED_DATE";
    public static final String COLUMN_DISC="disc";
    public static final String COLUMN_NET="NET";
    public static final String COLUMN_VAT_PERCENT="VAT";
    public static final String COLUMN_VAT_AMT="Vat_amt";
    public static final String COLUMN_GROSS="ItemsTotal_Gross";

    public static final String COLUMN_EXPECTED_DELIVERY="Expected_delivery";
    public static final String COLUMN_LEAD_TIME="Lead_time";
    public static final String COLUMN_CUSTOMER_CODE_AFTER_DELIVER="get_customer_after_deliver";
    public static final String COLUMN_REFERENCE_NO="Refrenceno";
    public static final String COLUMN_COMMENTS="Comments";
    public static final String COLUMN_SELLING_PRICE="selling_Price";
    SQLiteDatabase db;
    public SubmitOrderDB(@Nullable Context context) {
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
                        COLUMN_OUTLETID + " TEXT, " +
                        COLUMN_PRODUCTID + " TEXT, " +
                        COLUMN_AGENCYID + " TEXT, " +
                        COLUMN_ITEMCODE + " TEXT, " +
                        COLUMN_SELLING_PRICE + " TEXT, " +
                        COLUMN_ITEM_CATEGORY + " TEXT, " +
                        COLUMN_ITEM_SUB_CATEGORY + " TEXT, " +
                        COLUMN_REQUESTED_QTY + " TEXT, " +
                        COLUMN_APPROVED_QTY + " TEXT, " +
                        COLUMN_DELIVERED_QTY + " TEXT, " +
                        COLUMN_STATUS + " TEXT, " +
                        COLUMN_ISONLINE + " TEXT, " +
                        COLUMN_INVOICE_NO + " TEXT, "+
                        COLUMN_INVOICE_BILL + " TEXT, "+
                        COLUMN_DISC + " TEXT, "+
                        COLUMN_NET + " TEXT,"+
                        COLUMN_VAT_PERCENT + " TEXT,"+
                        COLUMN_VAT_AMT + " TEXT,"+
                        COLUMN_GROSS + " TEXT,"+
                        COLUMN_TOTAL_ITEMS + " TEXT,"+
                        COLUMN_TOTAL_QTY_OF_OUTLET + " TEXT, "+
                        COLUMN_TOTAL_NET_AMOUNT + " TEXT, "+
                        COLUMN_TOTAL_VAT_AMOUNT + " TEXT, "+
                        COLUMN_TOTAL_GROSS_AMOUNT + " TEXT,"+
                        COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE + " TEXT,"+
                        COLUMN_SIGNATURE + " TEXT, "+
                        COLUMN_PO_REF + " TEXT, "+
                        COLUMN_PO_REF_NAME + " TEXT, "+
                        COLUMN_PO_CREATED_DATE + " TEXT, "+
                        COLUMN_CUSTOMER_CODE_AFTER_DELIVER + " TEXT, "+
                        COLUMN_ORDERED_DATE_TIME + " TEXT,"+
                        COLUMN_EXPECTED_DELIVERY + " TEXT,"+
                        COLUMN_LEAD_TIME + " TEXT,"+
                        COLUMN_REFERENCE_NO + " TEXT,"+
                        COLUMN_COMMENTS + " TEXT,"+
                        COLUMN_APPROVED_ORDER_TIME + " TEXT,"+
                        COLUMN_DELIVERED_DATE_TIME + " TEXT ); ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void submitDetails(String orderID, String userid, String vanid, String outID, Set<ProductInfo> productIdQty, String status, String customerCode, String dateTime, String expectedDate, String leadTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Initialize strings to hold concatenated data
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();
        StringBuilder agencyIdsBuilder = new StringBuilder();

        // Iterate over each product info object in the set
        for (ProductInfo productInfo : productIdQty) {
            // Append product ID, item code, and quantity to their respective string builders
            productIdsBuilder.append(productInfo.getProductID()).append(",");
            itemCodesBuilder.append(productInfo.getItemCode()).append(",");
            quantitiesBuilder.append(productInfo.getQuantity()).append(",");
            agencyIdsBuilder.append(productInfo.getAgencyCode()).append(",");
        }

        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_PRODUCTID, productIdsBuilder.toString()); // Concatenated product IDs
        cv.put(COLUMN_AGENCYID, agencyIdsBuilder.toString());
        cv.put(COLUMN_ITEMCODE, itemCodesBuilder.toString()); // Concatenated item codes
        cv.put(COLUMN_REQUESTED_QTY, quantitiesBuilder.toString()); // Concatenated quantities
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER, customerCode);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);
        cv.put(COLUMN_EXPECTED_DELIVERY, expectedDate);
        cv.put(COLUMN_LEAD_TIME, leadTime);

        // Insert the entry into the database
        long result = db.insert(TABLE_NAME, null, cv);

        // Show toast message based on insertion result on the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (result == -1) {
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Order Successful: " + orderID, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onlineSubmitOrderDetails(String orderID, String userid, String vanid, String outID,String productid,String agencycode,String itemcodes,String productsQTY, String status,String isOnline,String customercode, String dateTime,String expectedDate,String leadTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Initialize strings to hold concatenated data
     /*   StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();

        // Iterate over each product info object in the set
        for (ProductInfo productInfo : productIdQty) {
            // Append product ID, item code, and quantity to their respective string builders
            productIdsBuilder.append(productInfo.getProductID()).append(",");
            itemCodesBuilder.append(productInfo.getItemCode()).append(",");
            quantitiesBuilder.append(productInfo.getQuantity()).append(",");
        }*/

        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_PRODUCTID, productid); // Concatenated product IDs
        cv.put(COLUMN_AGENCYID, agencycode);
        cv.put(COLUMN_ITEMCODE, itemcodes); // Concatenated item codes
        cv.put(COLUMN_REQUESTED_QTY, productsQTY); // Concatenated quantities
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_ISONLINE, isOnline);
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER,customercode);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);
        cv.put(COLUMN_EXPECTED_DELIVERY,expectedDate);
        cv.put(COLUMN_LEAD_TIME,leadTime);

        // Insert the entry into the database
        long result = db.insert(TABLE_NAME, null, cv);

        // Show toast message based on insertion result
        if (result == -1) {
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Order Successfull:" + orderID, Toast.LENGTH_SHORT).show();
        }
    }




    public boolean NewOrderInsertion(String orderId, String invoicNum, String userId, String vanId, String outletId, List<NewOrderInvoiceBean> list, String totalqty, String totalNetAmnt, String totalVatAmt, String Total_gross_amt, String Total_gross_amt_payable, String customer_code_bsd_price, String dateTime, String reference, String comments, String status) {
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
        if (isInvalid(orderId, "Order ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(invoicNum, "Invoice Number is missing. Please try again from beginning.")) return false;
        if (isInvalid(userId, "User ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(vanId, "Van ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(outletId, "Outlet ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalqty, "Total Quantity is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalNetAmnt, "Total Net Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalVatAmt, "Total VAT Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(Total_gross_amt, "Total Gross Amount is missing. Please try again from beginning.")) return false;
      //  if (isInvalid(Total_gross_amt_payable, "Total Gross Amount Payable is missing. Please try again from beginning.")) return false;
        if (isInvalid(customer_code_bsd_price, "Customer Code is missing. Please try again from beginning.")) return false;
        if (isInvalid(dateTime, "Date Time is missing. Please try again from beginning.")) return false;
        if (isInvalid(status, "Status is missing. Please try again from beginning.")) return false;

        // Append new product IDs and approved quantities to the existing ones
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder deliveredQtyBuilder = new StringBuilder();
        StringBuilder porefrencebuilder = new StringBuilder();
        StringBuilder porefnamebuilder = new StringBuilder();
        StringBuilder pocreateddatebuilder = new StringBuilder();
        StringBuilder agencyBuilder = new StringBuilder();
        StringBuilder itemCodeBuilder = new StringBuilder();
        StringBuilder discBuilder = new StringBuilder();
        StringBuilder netBuilder = new StringBuilder();
        StringBuilder vatBuilder = new StringBuilder();
        StringBuilder vatamtBuilder = new StringBuilder();
        StringBuilder grossBuilder = new StringBuilder();
        StringBuilder sellingpricebuilder=new StringBuilder();


        for (NewOrderInvoiceBean productId : list) {
            if (isInvalid(productId.getItemId(), "Item ID is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getDelqty(), "Delivered quantity is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getAgency_code(), "Agency code is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getItemCode(), "Item code is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getDisc(), "Discount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getNet(), "Net value is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getVat_percent(), "VAT percent is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getVat_amt(), "VAT amount is missing. Please try again from beginning.")) return false;
            if (isInvalid(productId.getGross(), "Gross value is missing. Please try again from beginning.")) return false;
            if(isInvalid(productId.getSellingprice(),"selling price is missing please try gain from beginning.")) return false;

            productIdsBuilder.append(productId.getItemId()).append(",");
            deliveredQtyBuilder.append(productId.getDelqty()).append(",");
            agencyBuilder.append(productId.getAgency_code()).append(",");
            itemCodeBuilder.append(productId.getItemCode()).append(",");
            discBuilder.append(productId.getDisc()).append(",");
            porefrencebuilder.append(formatListToString(productId.getPo())).append(",");
            porefnamebuilder.append(formatListToString(productId.getPorefname())).append(",");
            pocreateddatebuilder.append(formatListToString(productId.getPocreateddate())).append(",");
            netBuilder.append(productId.getNet()).append(",");
            vatBuilder.append(productId.getVat_percent()).append(",");
            vatamtBuilder.append(productId.getVat_amt()).append(",");
            grossBuilder.append(productId.getGross()).append(",");
            sellingpricebuilder.append(productId.getSellingprice()).append(",");
        }

        // Remove trailing commas from the built strings
        String productId = removeTrailingComma(productIdsBuilder);
        String DeliveredQty = removeTrailingComma(deliveredQtyBuilder);
        String agencyCode = removeTrailingComma(agencyBuilder);
        String poref = removeTrailingComma(porefrencebuilder);
        String porefname = removeTrailingComma(porefnamebuilder);
        String pocreateddate = removeTrailingComma(pocreateddatebuilder);
        String itemCode = removeTrailingComma(itemCodeBuilder);
        String discount = removeTrailingComma(discBuilder);
        String net = removeTrailingComma(netBuilder);
        String vat = removeTrailingComma(vatBuilder);
        String vatamt = removeTrailingComma(vatamtBuilder);
        String gross = removeTrailingComma(grossBuilder);
        String sellingprice=removeTrailingComma(sellingpricebuilder);

        // Validate the lengths of all concatenated strings
        int itemCodeLength = productId.split(",").length;
        if (!isLengthValid(DeliveredQty, itemCodeLength, "Mismatch in number of delivered quantities. Please try again.")) return false;
        if (!isLengthValid(agencyCode, itemCodeLength, "Mismatch in number of agency codes. Please try again.")) return false;
        if (!isLengthValid(itemCode, itemCodeLength, "Mismatch in number of item codes. Please try again.")) return false;
        if (!isLengthValid(discount, itemCodeLength, "Mismatch in number of discount values. Please try again.")) return false;
        if (!isLengthValid(net, itemCodeLength, "Mismatch in number of net values. Please try again.")) return false;
        if (!isLengthValid(vat, itemCodeLength, "Mismatch in number of VAT percentages. Please try again.")) return false;
        if (!isLengthValid(vatamt, itemCodeLength, "Mismatch in number of VAT amounts. Please try again.")) return false;
        if (!isLengthValid(gross, itemCodeLength, "Mismatch in number of gross values. Please try again.")) return false;
        if(!isLengthValid(sellingprice,itemCodeLength,"Mismatch in number of selling price values,Please try again.")) return false;
        // Create ContentValues to store the updated data in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderId);
        cv.put(COLUMN_INVOICE_NO, invoicNum);
        cv.put(COLUMN_USERID, userId);
        cv.put(COLUMN_VANID, vanId);
        cv.put(COLUMN_OUTLETID, outletId);
        cv.put(COLUMN_PRODUCTID, productId);
        cv.put(COLUMN_DELIVERED_QTY, DeliveredQty);
        cv.put(COLUMN_AGENCYID, agencyCode);
        cv.put(COLUMN_PO_REF, poref);
        cv.put(COLUMN_PO_REF_NAME, porefname);
        cv.put(COLUMN_PO_CREATED_DATE, pocreateddate);
        cv.put(COLUMN_ITEMCODE, itemCode);
        cv.put(COLUMN_DISC, discount);
        cv.put(COLUMN_NET, net);
        cv.put(COLUMN_VAT_PERCENT, vat);
        cv.put(COLUMN_VAT_AMT, vatamt);
        cv.put(COLUMN_GROSS, gross);
        cv.put(COLUMN_SELLING_PRICE,sellingprice);
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalqty);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNetAmnt);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVatAmt);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, Total_gross_amt);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, Total_gross_amt_payable !=null ? Total_gross_amt_payable : "N/A");
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER, customer_code_bsd_price);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);
        cv.put(COLUMN_APPROVED_ORDER_TIME, dateTime);
        cv.put(COLUMN_DELIVERED_DATE_TIME, dateTime);
        cv.put(COLUMN_REFERENCE_NO, TextUtils.isEmpty(reference) ? "" : reference);
        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_STATUS, "NEW ORDER DELIVERED");

        long result = db.insert(TABLE_NAME, null, cv);

        // Return true if insertion was successful, false otherwise
        return result != -1;
    }

    @SuppressLint("Range")
    public String getProductNameById(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String productName = null;
        Cursor cursor = null;

        try {
            cursor = db.query(ItemsByAgencyDB.TABLE_NAME,
                    new String[]{ItemsByAgencyDB.COLUMN_ITEM_NAME},
                    ItemsByAgencyDB.COLUMN_ITEM_ID + "=?",
                    new String[]{productId},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                productName = cursor.getString(cursor.getColumnIndex(ItemsByAgencyDB.COLUMN_ITEM_NAME));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return productName;
    }



    public void updateOrder(String orderID, String userid, String vanid, String outID, Set<ProductInfo> productIdQty, Set<String> ApprovedQTy, String delQty, String status, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Initialize strings to hold concatenated data
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();
        StringBuilder agencyBuilder = new StringBuilder();
        // Iterate over each product info object in the set
        for (ProductInfo productInfo : productIdQty) {
            // Append product ID, item code, and quantity to their respective string builders
            productIdsBuilder.append(productInfo.getProductID()).append(",");
            itemCodesBuilder.append(productInfo.getItemCode()).append(",");
            quantitiesBuilder.append(productInfo.getQuantity()).append(",");
            agencyBuilder.append(productInfo.getAgencyCode()).append(",");
        }

        /*for (String ApprovedQty : ApprovedQTy) {
            // Append product ID, item code, and quantity to their respective string builders
            ApprovedQtyBuilder.append(ApprovedQty).append(",");
        }*/
        // Remove the last comma from each string builder
        String productIds = productIdsBuilder.length() > 0 ? productIdsBuilder.substring(0, productIdsBuilder.length() - 1) : "";
        String itemCodes = itemCodesBuilder.length() > 0 ? itemCodesBuilder.substring(0, itemCodesBuilder.length() - 1) : "";
        String quantities = quantitiesBuilder.length() > 0 ? quantitiesBuilder.substring(0, quantitiesBuilder.length() - 1) : "";
        String agencyIds = agencyBuilder.length() > 0 ? agencyBuilder.substring(0, agencyBuilder.length() - 1) : "";
    //    String ApprovedQTyStr = ApprovedQtyBuilder.length() > 0 ? ApprovedQtyBuilder.substring(0, ApprovedQtyBuilder.length() - 1) : "";
        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_PRODUCTID, productIds); // Concatenated product IDs
        cv.put(COLUMN_AGENCYID, agencyIds);
        cv.put(COLUMN_ITEMCODE, itemCodes); // Concatenated item codes
        cv.put(COLUMN_REQUESTED_QTY, quantities); // Concatenated quantities
        cv.put(COLUMN_APPROVED_QTY, String.valueOf(ApprovedQTy));
        cv.put(COLUMN_DELIVERED_QTY, delQty);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);

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

    public void updateOrderAfterSync(String orderID, String userid, String vanid, String outID, Set<ProductInfo> productIdQty, Set<ProductInfo> ApprovedQTy, String delQty, String status, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Initialize strings to hold concatenated data
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();
        StringBuilder agencyBuilder = new StringBuilder();
        //  StringBuilder ApprovedQtyBuilder = new StringBuilder();
        // Iterate over each product info object in the set
        for (ProductInfo productInfo : productIdQty) {
            // Append product ID, item code, and quantity to their respective string builders
            productIdsBuilder.append(productInfo.getProductID()).append(",");
            itemCodesBuilder.append(productInfo.getItemCode()).append(",");
            quantitiesBuilder.append(productInfo.getQuantity()).append(",");
            agencyBuilder.append(productInfo.getAgencyCode()).append(",");
        }


        // Remove the last comma from each string builder
        String productIds = productIdsBuilder.length() > 0 ? productIdsBuilder.substring(0, productIdsBuilder.length() - 1) : "";
        String itemCodes = itemCodesBuilder.length() > 0 ? itemCodesBuilder.substring(0, itemCodesBuilder.length() - 1) : "";
        String quantities = quantitiesBuilder.length() > 0 ? quantitiesBuilder.substring(0, quantitiesBuilder.length() - 1) : "";
        String agencyIds = agencyBuilder.length() > 0 ? agencyBuilder.substring(0, agencyBuilder.length() - 1) : "";
        //  String ApprovedQTyStr = ApprovedQtyBuilder.length() > 0 ? ApprovedQtyBuilder.substring(0, ApprovedQtyBuilder.length() - 1) : "";
        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_PRODUCTID, productIds); // Concatenated product IDs
        cv.put(COLUMN_AGENCYID, agencyIds);
        cv.put(COLUMN_ITEMCODE, itemCodes); // Concatenated item codes
        cv.put(COLUMN_REQUESTED_QTY, quantities); // Concatenated quantities
        cv.put(COLUMN_APPROVED_QTY, String.valueOf(ApprovedQTy));
        cv.put(COLUMN_DELIVERED_QTY, delQty);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);

        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

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
    public void updateOrderStatusAfterDeliver(String orderID, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

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

    public void updateOrderStatusAfterCancel(String outletid,String orderid, String status,String reasons,String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DELIVERED_DATE_TIME,date);
        cv.put(COLUMN_COMMENTS,reasons);
        // Define the selection criteria (where clause)
        String selection = COLUMN_OUTLETID + " = ? AND " + COLUMN_ORDERID + " =?";
        String[] selectionArgs = {outletid,orderid};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
         //   Toast.makeText(context, "Order Cancelled Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
         //   Toast.makeText(context, "Failed To Cancel Order! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateOrderStatusAfterCancelled(String outletid,String orderid,String sellingprice,String totalqty,String totalItems,String totalNet,String totalVat,String totalGrossWithoutRebate,String totalGrossWithRebate,String reason, String status,String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalqty);
        cv.put(COLUMN_TOTAL_ITEMS, totalItems);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNet);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVat);
        cv.put(COLUMN_SELLING_PRICE,sellingprice);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, totalGrossWithoutRebate);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, totalGrossWithRebate);
        cv.put(COLUMN_COMMENTS, reason);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DELIVERED_DATE_TIME,dateTime);

        // Define the selection criteria (where clause)
        String selection = COLUMN_OUTLETID + " = ? AND " + COLUMN_ORDERID + " =?";
        String[] selectionArgs = {outletid,orderid};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
          //  Toast.makeText(context, "Order Cancelled Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
            //Toast.makeText(context, "Failed To Cancel Order! ", Toast.LENGTH_SHORT).show();
        }
    }
    // Method to update order after syncing approved data in the database
    public void updateOrderAfterSyncApprovedDb(String orderID, List<ProductInfo> productIds, String status, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Append new product IDs and approved quantities to the existing ones
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder reqQtyBuilder=new StringBuilder();
        StringBuilder approvedQtyBuilder = new StringBuilder();
        StringBuilder porefrencebuilder=new StringBuilder();
        StringBuilder porefrencenamebuilder=new StringBuilder();
        StringBuilder pocreateddatebuilder=new StringBuilder();
        StringBuilder agencyBuilder=new StringBuilder();
        StringBuilder itemCodeBuilder=new StringBuilder();
        StringBuilder itemcategorybuilder=new StringBuilder();
        StringBuilder itemsubcategorybuilder=new StringBuilder();

        for (ProductInfo productId : productIds) {
            productIdsBuilder.append(productId.getProductID()).append(",");
            reqQtyBuilder.append(productId.getQuantity()).append(",");
            approvedQtyBuilder.append(productId.getApprovedQty()).append(",");
            porefrencebuilder.append(productId.getPoREFRENCE()).append(",");
            porefrencenamebuilder.append(productId.getPoRefName()).append(",");
            pocreateddatebuilder.append(productId.getPoRefdate()).append(",");
            agencyBuilder.append(productId.getAgencyCode()).append(",");
            itemCodeBuilder.append(productId.getItemCode()).append(",");
            itemcategorybuilder.append(productId.getItemcategory()).append(",");
            itemsubcategorybuilder.append(productId.getItemsubcategory()).append(",");
        }


        String productId = productIdsBuilder.length() > 0 ? productIdsBuilder.substring(0, productIdsBuilder.length() - 1) : "";
        String RequestedQty = reqQtyBuilder.length() > 0 ? reqQtyBuilder.substring(0, reqQtyBuilder.length() - 1) : "";
        String approvedQty = approvedQtyBuilder.length() > 0 ? approvedQtyBuilder.substring(0, approvedQtyBuilder.length() - 1) : "";
        String poref=porefrencebuilder.length() > 0 ? porefrencebuilder.substring(0, porefrencebuilder.length() - 1) : "";
        String porefname=porefrencenamebuilder.length() > 0 ? porefrencenamebuilder.substring(0, porefrencenamebuilder.length() - 1) : "";
        String pocreateddate=pocreateddatebuilder.length() > 0 ? pocreateddatebuilder.substring(0, pocreateddatebuilder.length() - 1) : "";
        String agencyCode=agencyBuilder.length() > 0 ? agencyBuilder.substring(0, agencyBuilder.length() - 1) : "";
        String itemCode=itemCodeBuilder.length()> 0 ? itemCodeBuilder.substring(0,itemCodeBuilder.length()-1) : "";
        String itemCategory=itemcategorybuilder.length()>0 ? itemcategorybuilder.substring(0,itemcategorybuilder.length()-1) : "";
        String itemSubCategory=itemsubcategorybuilder.length()>0 ? itemsubcategorybuilder.substring(0,itemsubcategorybuilder.length()-1):"";
        // Create ContentValues to store the updated data in the database
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCTID, productId);
        cv.put(COLUMN_AGENCYID,agencyCode);
        cv.put(COLUMN_ITEMCODE,itemCode);
        cv.put(COLUMN_ITEM_CATEGORY,itemCategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY,itemSubCategory);
        cv.put(COLUMN_REQUESTED_QTY,RequestedQty);
        cv.put(COLUMN_APPROVED_QTY, approvedQty);
        if(poref==null){
            cv.put(COLUMN_PO_REF,"NO PO");
        }else {
            cv.put(COLUMN_PO_REF, poref);
        }
        cv.put(COLUMN_PO_REF_NAME,porefname);
        cv.put(COLUMN_PO_CREATED_DATE,pocreateddate);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_APPROVED_ORDER_TIME, dateTime);

        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            // Update Success
            // Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // Update Failed
            // Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }


    public void submitOrderFromWebSyncApprovedDb(String orderID,String outletID,String userID,String vanid,String customercode, List<ProductInfo> productIds, String status, String dateTime,String orderedDatetime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Append new product IDs and approved quantities to the existing ones
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder reqQtyBuilder=new StringBuilder();
        StringBuilder approvedQtyBuilder = new StringBuilder();
        StringBuilder porefrencebuilder=new StringBuilder();
        StringBuilder porefrencenamebuilder=new StringBuilder();
        StringBuilder pocreateddatebuilder=new StringBuilder();
        StringBuilder agencyBuilder=new StringBuilder();
        StringBuilder itemCodeBuilder=new StringBuilder();
        StringBuilder itemcategorybuilder=new StringBuilder();
        StringBuilder itemsubcategorybuilder=new StringBuilder();
        for (ProductInfo productId : productIds) {
            productIdsBuilder.append(productId.getProductID()).append(",");
            reqQtyBuilder.append(productId.getQuantity()).append(",");
            approvedQtyBuilder.append(productId.getApprovedQty()).append(",");
            porefrencebuilder.append(productId.getPoREFRENCE()).append(",");
            porefrencenamebuilder.append(productId.getPoRefName()).append(",");
            pocreateddatebuilder.append(productId.getPoRefdate()).append(",");
            agencyBuilder.append(productId.getAgencyCode()).append(",");
            itemCodeBuilder.append(productId.getItemCode()).append(",");
            itemcategorybuilder.append(productId.getItemcategory()).append(",");
            itemsubcategorybuilder.append(productId.getItemsubcategory()).append(",");
        }


        String productId = productIdsBuilder.length() > 0 ? productIdsBuilder.substring(0, productIdsBuilder.length() - 1) : "";
        String RequestedQty = reqQtyBuilder.length() > 0 ? reqQtyBuilder.substring(0, reqQtyBuilder.length() - 1) : "";
        String approvedQty = approvedQtyBuilder.length() > 0 ? approvedQtyBuilder.substring(0, approvedQtyBuilder.length() - 1) : "";
        String poref=porefrencebuilder.length() > 0 ? porefrencebuilder.substring(0, porefrencebuilder.length() - 1) : "";
        String porefname=porefrencenamebuilder.length() > 0 ? porefrencenamebuilder.substring(0, porefrencenamebuilder.length() - 1) : "";
        String pocreateddate=pocreateddatebuilder.length() > 0 ? pocreateddatebuilder.substring(0, pocreateddatebuilder.length() - 1) : "";
        String agencyCode=agencyBuilder.length() > 0 ? agencyBuilder.substring(0, agencyBuilder.length() - 1) : "";
        String itemCode=itemCodeBuilder.length()> 0 ? itemCodeBuilder.substring(0,itemCodeBuilder.length()-1) : "";
        String itemCategory=itemcategorybuilder.length()>0 ? itemcategorybuilder.substring(0,itemcategorybuilder.length()-1) : "";
        String itemSubCategory=itemsubcategorybuilder.length()>0 ? itemsubcategorybuilder.substring(0,itemsubcategorybuilder.length()-1):"";

        // Create ContentValues to store the updated data in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID,orderID);
        cv.put(COLUMN_USERID,userID);
        cv.put(COLUMN_VANID,vanid);
        cv.put(COLUMN_OUTLETID,outletID);
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER,customercode);
        cv.put(COLUMN_PRODUCTID, productId);
        cv.put(COLUMN_AGENCYID,agencyCode);
        cv.put(COLUMN_ITEMCODE,itemCode);
        cv.put(COLUMN_ITEM_CATEGORY,itemCategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY,itemSubCategory);
        cv.put(COLUMN_REQUESTED_QTY,RequestedQty);
        cv.put(COLUMN_APPROVED_QTY, approvedQty);
        if(poref==null){
            cv.put(COLUMN_PO_REF,"NO PO");
        }else {
            cv.put(COLUMN_PO_REF, poref);
        }

        cv.put(COLUMN_PO_REF_NAME,porefname);
        cv.put(COLUMN_PO_CREATED_DATE,pocreateddate);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_APPROVED_ORDER_TIME, dateTime);
        cv.put(COLUMN_ORDERED_DATE_TIME,orderedDatetime);

        // Define the selection criteria (where clause)
        long result = db.insert(TABLE_NAME, null, cv);
        // Show toast message based on update result
        if (result == -1) {
            // Update Success
            // Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // Update Failed
            // Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateDBAfterDelivery(String orderid,String outletId, String invoiceNumber, List<String> delivQty, byte[] signatureImage, byte[] billImage, List<String> disc, List<String> net, List<String> vat_per, List<String> vat_amt, List<String> gross, String totalqty, String totalNetAmnt, String totalVatAmt, String Total_gross_amt, String customer_code_bsd_price, String dateTime, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder discBuilder = new StringBuilder();
        StringBuilder netBuilder = new StringBuilder();
        StringBuilder vatBuilder = new StringBuilder();
        StringBuilder vatAmountbuilder = new StringBuilder();
        StringBuilder grossBuilder = new StringBuilder();
        StringBuilder delqtyBuilder=new StringBuilder();
        for (int i = 0; i < net.size(); i++) {
            discBuilder.append(disc.get(i)).append(",");
            netBuilder.append(net.get(i)).append(",");
            vatBuilder.append(vat_per.get(i)).append(",");
            vatAmountbuilder.append(vat_amt.get(i)).append(",");
            grossBuilder.append(gross.get(i)).append(",");

            delqtyBuilder.append(delivQty.get(i)).append(",");
        }

        String discs = discBuilder.length() > 0 ? discBuilder.substring(0, discBuilder.length() - 1) : "";
        String nets = netBuilder.length() > 0 ? netBuilder.substring(0, netBuilder.length() - 1) : "";
        String vatpers = vatBuilder.length() > 0 ? vatBuilder.substring(0, vatBuilder.length() - 1) : "";
        String vatamts = vatAmountbuilder.length() > 0 ? vatAmountbuilder.substring(0, vatAmountbuilder.length() - 1) : "";
        String grosss = grossBuilder.length() > 0 ? grossBuilder.substring(0, grossBuilder.length() - 1) : "";
        String delivQtys=delqtyBuilder.length() > 0 ? delqtyBuilder.substring(0, delqtyBuilder.length() - 1) : "";
        // Create ContentValues
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID,orderid);
        cv.put(COLUMN_OUTLETID, outletId);
        cv.put(COLUMN_INVOICE_NO, invoiceNumber);
        cv.put(COLUMN_DELIVERED_QTY, delivQtys);
        cv.put(COLUMN_SIGNATURE, signatureImage);
        cv.put(COLUMN_INVOICE_BILL, billImage);
        cv.put(COLUMN_DISC, discs);
        cv.put(COLUMN_NET, nets);
        cv.put(COLUMN_VAT_PERCENT, vatpers);
        cv.put(COLUMN_VAT_AMT, vatamts);
        cv.put(COLUMN_GROSS, grosss);
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalqty);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNetAmnt);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVatAmt);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, Total_gross_amt);
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER, customer_code_bsd_price);
        cv.put(COLUMN_DELIVERED_DATE_TIME, dateTime);
        cv.put(COLUMN_STATUS, "DELIVERED");

        String selection = COLUMN_ORDERID + " = ? AND " + COLUMN_OUTLETID + " = ? AND " + COLUMN_STATUS + " = ?";        String[] selectionArgs = {orderid,outletId, status};
        db.update(TABLE_NAME, cv, selection, selectionArgs);
    }

    public boolean updateDBAfterDelivery2(String orderid, String outletId, String invoiceNumber, List<ShowOrderForInvoiceBean> showOrderForInvoiceBeanList, String totalqty, String totalNetAmnt, String totalVatAmt, String Total_gross_amt, String Total_gross_amt_payable, String customer_code_bsd_price, String dateTime, String refrence, String comments, String status, String[] itemcodearray) {
        SQLiteDatabase db = this.getWritableDatabase();
        String referenceCheckQuery = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_REFERENCE_NO + " = ?";
        Cursor cursor = db.rawQuery(referenceCheckQuery, new String[]{refrence});

// Allow storage even if the reference is empty
        if (!TextUtils.isEmpty(refrence) && cursor.moveToFirst()) {
            // Reference number already exists
            Toast.makeText(context, "Reference number already exists. Please use a unique reference number.", Toast.LENGTH_LONG).show();
            cursor.close();
            return false;
        }

        cursor.close();

        // StringBuilder objects to build concatenated strings for each attribute
        StringBuilder discBuilder = new StringBuilder();
        StringBuilder netBuilder = new StringBuilder();
        StringBuilder vatBuilder = new StringBuilder();
        StringBuilder vatAmountBuilder = new StringBuilder();
        StringBuilder grossBuilder = new StringBuilder();
        StringBuilder delQtyBuilder = new StringBuilder();
        StringBuilder sellingpriceBuilder=new StringBuilder();

        // Basic validations for required fields
        if (isInvalid(orderid, "Order ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(outletId, "Outlet ID is missing. Please try again from beginning.")) return false;
        if (isInvalid(invoiceNumber, "Invoice Number is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalqty, "Total Quantity is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalNetAmnt, "Total Net Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(totalVatAmt, "Total VAT Amount is missing. Please try again from beginning.")) return false;
        if (isInvalid(Total_gross_amt, "Total Gross Amount is missing. Please try again from beginning.")) return false;
      //  if (isInvalid(Total_gross_amt_payable, "Total Gross Amount Payable is missing. Please try again from beginning.")) return false;
        if (isInvalid(customer_code_bsd_price, "Customer Code is missing. Please try again from beginning.")) return false;

        // Build concatenated strings for each field in showOrderForInvoiceBeanList
        for (ShowOrderForInvoiceBean bean : showOrderForInvoiceBeanList) {
            if (isInvalid(bean.getDisc(), "Discount value is missing. Please try again from beginning.")) return false;
            discBuilder.append(bean.getDisc()).append(",");

            if (isInvalid(bean.getNet(), "Net value is missing. Please try again from beginning.")) return false;
            netBuilder.append(bean.getNet()).append(",");

            if (isInvalid(bean.getVat_percent(), "VAT percent is missing. Please try again from beginning.")) return false;
            vatBuilder.append(bean.getVat_percent()).append(",");

            if (isInvalid(bean.getVat_amt(), "VAT amount is missing. Please try again from beginning.")) return false;
            vatAmountBuilder.append(bean.getVat_amt()).append(",");

            if (isInvalid(bean.getGross(), "Gross value is missing. Please try again from beginning.")) return false;
            grossBuilder.append(bean.getGross()).append(",");

            if (isInvalid(bean.getDelqty(), "Delivered quantity is missing. Please try again from beginning.")) return false;
            delQtyBuilder.append(bean.getDelqty()).append(",");

            if(isInvalid(bean.getSellingprice(),"Selling price is missing. Please try again from beginning")) return false;
            sellingpriceBuilder.append(bean.getSellingprice()).append(",");

        }

        // Remove trailing commas from the built strings
        String discs = removeTrailingComma(discBuilder);
        String nets = removeTrailingComma(netBuilder);
        String vatpers = removeTrailingComma(vatBuilder);
        String vatamts = removeTrailingComma(vatAmountBuilder);
        String grosss = removeTrailingComma(grossBuilder);
        String delivQtys = removeTrailingComma(delQtyBuilder);
        String sellingprice=removeTrailingComma(sellingpriceBuilder);

        // Validate the lengths of all concatenated strings against itemcodearray length
        int itemCodeLength = itemcodearray.length;

        if (!isLengthValid(discs, itemCodeLength, "Mismatch in number of discount values. Please try again.")) return false;
        if (!isLengthValid(nets, itemCodeLength, "Mismatch in number of net values. Please try again.")) return false;
        if (!isLengthValid(vatpers, itemCodeLength, "Mismatch in number of VAT percentages. Please try again.")) return false;
        if (!isLengthValid(vatamts, itemCodeLength, "Mismatch in number of VAT amounts. Please try again.")) return false;
        if (!isLengthValid(grosss, itemCodeLength, "Mismatch in number of gross values. Please try again.")) return false;
        if (!isLengthValid(delivQtys, itemCodeLength, "Mismatch in number of delivered quantities. Please try again.")) return false;
        if(!isLengthValid(sellingprice,itemCodeLength,"Mismatch in number of delivered quantities. Please try again.")) return false;
        // Create ContentValues and update the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderid);
        cv.put(COLUMN_OUTLETID, outletId);
        cv.put(COLUMN_INVOICE_NO, invoiceNumber);
        cv.put(COLUMN_DELIVERED_QTY, delivQtys);
        cv.put(COLUMN_SELLING_PRICE,sellingprice);
        cv.put(COLUMN_DISC, discs);
        cv.put(COLUMN_NET, nets);
        cv.put(COLUMN_VAT_PERCENT, vatpers);
        cv.put(COLUMN_VAT_AMT, vatamts);
        cv.put(COLUMN_GROSS, grosss);
        cv.put(COLUMN_TOTAL_QTY_OF_OUTLET, totalqty);
        cv.put(COLUMN_TOTAL_NET_AMOUNT, totalNetAmnt);
        cv.put(COLUMN_TOTAL_VAT_AMOUNT, totalVatAmt);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT, Total_gross_amt);
        cv.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE, Total_gross_amt_payable!=null ? Total_gross_amt_payable : "N/A");
        cv.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER, customer_code_bsd_price);
        cv.put(COLUMN_DELIVERED_DATE_TIME, dateTime);


        cv.put(COLUMN_REFERENCE_NO, TextUtils.isEmpty(refrence) ? "" : refrence);


        cv.put(COLUMN_COMMENTS, comments);
        cv.put(COLUMN_STATUS, "DELIVERED");

        String selection = COLUMN_ORDERID + " = ? AND " + COLUMN_OUTLETID + " = ? AND " + COLUMN_STATUS + " = ?";
        String[] selectionArgs = {orderid, outletId, status};

        // Perform the update and check the result
        int rowsUpdated = db.update(TABLE_NAME, cv, selection, selectionArgs);
        return rowsUpdated > 0;  // Return true if at least one row was updated, otherwise false

    }

    // Helper method to validate input
    private boolean isInvalid(String value, String errorMessage) {
        if (value == null || value.isEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    public boolean isReferenceNumberExists(String reference) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String query = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_REFERENCE_NO + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{reference});
            boolean exists = cursor.moveToFirst(); // Check if any result exists
            cursor.close();
            return exists;
        } finally {
            // Do not close the database here if it is managed externally
        }
    }

    // Helper method to remove trailing comma
    private String removeTrailingComma(StringBuilder builder) {
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1) : "";
    }

    // Helper method to validate length of concatenated strings
    // Updated Helper method to validate length of concatenated strings
    private boolean isLengthValid(String value, int expectedLength, String errorMessage) {
        // Split the string by comma
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
    private String formatListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";  // Return empty string if list is null or empty
        }
        return String.join(",", list);  // Join list elements with commas
    }

 /*   public void updateOrderAfterSyncApprovedDb(String orderID, String productIds,String Approvedqty, String status, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
       cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_APPROVED_PRODUCTIDS, productIds);
        cv.put(COLUMN_APPROVED_QTY, Approvedqty);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_DATE_TIME, dateTime);
        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = { orderID };

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            // Update Success
            // Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // Update Failed
            // Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }*/

    // Utility method to remove trailing comma from a string
    private String removeTrailingComma(String str) {
        if (str.endsWith(",")) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void updateApprovedOrderAfterSync(String orderID, String userid, String vanid, String outID, Set<ProductInfo> productIdQty, Set<ProductInfo> approvedQty, String delQty, String status, String dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Initialize strings to hold concatenated data
        StringBuilder productIdsBuilder = new StringBuilder();
        StringBuilder itemCodesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();
        StringBuilder approvedQtyBuilder = new StringBuilder(); // New StringBuilder for approved quantities

        // Iterate over each product info object in the set
        for (ProductInfo productInfo : productIdQty) {
            // Append product ID, item code, and quantity to their respective string builders
            productIdsBuilder.append(productInfo.getProductID()).append(",");
            itemCodesBuilder.append(productInfo.getItemCode()).append(",");
            quantitiesBuilder.append(productInfo.getQuantity()).append(",");
        }

        // Concatenate existing approved quantities in reverse order
        String existingApprovedQtyStr = getExistingApprovedQty(db, orderID);
        if (!existingApprovedQtyStr.isEmpty()) {
            String[] existingApprovedQtys = existingApprovedQtyStr.split(",");
            for (int i = existingApprovedQtys.length - 1; i >= 0; i--) {
                String existingApprovedQty = existingApprovedQtys[i];
                if (existingApprovedQty != null && !existingApprovedQty.isEmpty() && !existingApprovedQty.equals("null")) {
                    approvedQtyBuilder.append(existingApprovedQty).append(",");
                }
            }
        }

        // Iterate over each product info object in the set of newly approved quantities
        for (ProductInfo newlyApprovedProduct : approvedQty) {
            // Append approved quantity to the string builder
            String newlyApprovedQty = newlyApprovedProduct.getApprovedQty();
            if (newlyApprovedQty != null && !newlyApprovedQty.isEmpty() && !newlyApprovedQty.equals("null")) {
                approvedQtyBuilder.append(newlyApprovedQty).append(",");
            }
        }

        // Remove the last comma from each string builder
        String productIds = productIdsBuilder.length() > 0 ? productIdsBuilder.substring(0, productIdsBuilder.length() - 1) : "";
        String itemCodes = itemCodesBuilder.length() > 0 ? itemCodesBuilder.substring(0, itemCodesBuilder.length() - 1) : "";
        String quantities = quantitiesBuilder.length() > 0 ? quantitiesBuilder.substring(0, quantitiesBuilder.length() - 1) : "";
        String approvedQtyStr = approvedQtyBuilder.length() > 0 ? approvedQtyBuilder.substring(0, approvedQtyBuilder.length() - 1) : "";

        // Create ContentValues to store in the database
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERID, userid);
        cv.put(COLUMN_VANID, vanid);
        cv.put(COLUMN_OUTLETID, outID);
        cv.put(COLUMN_PRODUCTID, productIds); // Concatenated product IDs
        cv.put(COLUMN_ITEMCODE, itemCodes); // Concatenated item codes
        cv.put(COLUMN_REQUESTED_QTY, quantities); // Concatenated quantities
        cv.put(COLUMN_APPROVED_QTY, approvedQtyStr);
        cv.put(COLUMN_DELIVERED_QTY, delQty);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);

        // Define the selection criteria (where clause)
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

        // Update the entry in the database
        int count = db.update(TABLE_NAME, cv, selection, selectionArgs);

        // Show toast message based on update result
        if (count > 0) {
            // Update Success
            // Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
        } else {
            // If no rows were updated, it means the orderID does not exist in the table.
            // You can choose to handle this case differently, e.g., insert a new record or show a different message.
            // Toast.makeText(context, "Update Failed: Order ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to retrieve existing approved quantities for a given orderID
    @SuppressLint("Range")
    private String getExistingApprovedQty(SQLiteDatabase db, String orderID) {
        String[] columns = {COLUMN_APPROVED_QTY};
        String selection = COLUMN_ORDERID + " = ?";
        String[] selectionArgs = {orderID};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        String existingApprovedQtyStr = "";

        if (cursor.moveToFirst()) {
            existingApprovedQtyStr = cursor.getString(cursor.getColumnIndex(COLUMN_APPROVED_QTY));
        }

        cursor.close();
        return existingApprovedQtyStr != null ? existingApprovedQtyStr : "";
    }

    public Cursor readDataByOrderStatus(String status){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }



    public void deleteOldRecords() {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE datetime(" + COLUMN_ORDERED_DATE_TIME +
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

    public Cursor getAllOutletIDs(String status, String status2) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + COLUMN_OUTLETID + ", " + COLUMN_STATUS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{status, status2});
        return cursor;
    }

    public Cursor getAllOutletID(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT " + COLUMN_OUTLETID + ", " + COLUMN_STATUS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{status});
        return cursor;
    }

    public Cursor readDataByProductIDAndStatus(String searchID, String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database to retrieve rows where the searchID is among the productIDs
        // For single IDs or multiple comma-separated IDs
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " LIKE ?" + " AND " + COLUMN_STATUS + " = ?" + " GROUP BY " + COLUMN_PRODUCTID + " ORDER BY " + COLUMN_PRODUCTID + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchID + "%", status});

        // If no data is found for the comma-separated searchID, try fetching data for individual IDs
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            String[] individualIds = searchID.split(",");
            StringBuilder unionQuery = new StringBuilder();
            for (int i = 0; i < individualIds.length; i++) {
                if (i > 0) {
                    unionQuery.append(" UNION ");
                }
                unionQuery.append("SELECT * FROM ").append(TABLE_NAME)
                        .append(" WHERE ").append(COLUMN_PRODUCTID).append(" = ?")
                        .append(" AND ").append(COLUMN_STATUS).append(" = ?");
            }
            cursor = db.rawQuery(unionQuery.toString(), concatenateArrays(individualIds, new String[individualIds.length], status));
        }

        return cursor;
    }

    private String[] concatenateArrays(String[] array1, String[] array2, String commonValue) {
        String[] concatenatedArray = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, concatenatedArray, 0, array1.length);
        System.arraycopy(array2, 0, concatenatedArray, array1.length, array2.length);
        for (int i = 0; i < concatenatedArray.length; i += 2) {
            concatenatedArray[i] = concatenatedArray[i].trim(); // Remove leading/trailing spaces
            concatenatedArray[i + 1] = commonValue;
        }
        return concatenatedArray;
    }



    public Cursor readDataByProductStatus(String status){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }
    @SuppressLint("Range")
    public Cursor readDataByProductStatusAndOrderIdAndProductId(String orderid, String itemid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?" +
                " AND " + COLUMN_PRODUCTID + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{orderid, "%" + itemid + "%"});
        return cursor;
    }



    public Cursor readLatestDataByOutletID(String outletId, String leadTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch data
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_LEAD_TIME + " = ? AND " + COLUMN_ID +
                " IN (SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_NAME +
                " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_LEAD_TIME + " = ?" +
                " GROUP BY " + COLUMN_OUTLETID + ", " + COLUMN_LEAD_TIME + ")" +
                " ORDER BY " + COLUMN_ORDERED_DATE_TIME + " DESC";

        // Execute the query with the outlet ID and lead time as parameters
        return db.rawQuery(query, new String[]{outletId, leadTime, outletId, leadTime});
    }



    public Cursor readDataByOrderID(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id});
        }
        return cursor;
    }
    public Cursor readDataByInvoicNo(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_INVOICE_NO + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id});
        }
        return cursor;
    }
    public boolean checkIFExistsOrderID(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id});
        }
        return true;
    }
   public Cursor readDataByOrderIDAndStatus(String id, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Updated query to include a check for status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ? AND " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{id, status});
        }
        return cursor;
    }

    public Cursor readDataByOutletsIDAndStatus(String id, String status, String status2) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Updated query with proper grouping of status conditions
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND (" + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?)";

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{id, status, status2});
        }
        return cursor;
    }

    public Cursor readDataByOutletsIDAndStatus2(String id, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Updated query to include a check for status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_STATUS + " = ? " ;

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{id, status});
        }
        return cursor;
    }

    public Cursor readDataByStatus( String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Updated query to include a check for status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " +  COLUMN_STATUS + " = ? " ;

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{ status});
        }
        return cursor;
    }
    public Cursor readDataByOutletsIDOrderIDAndStatus(String id,String orderid, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Updated query to include a check for status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_ORDERID + " = ? AND " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{id, status});
        }
        return cursor;
    }
    public Cursor readDataByInvoiceNoAndStatus(String id, String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Updated query to include a check for status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_INVOICE_NO + " = ? AND " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;
        if (db != null) {
            // Include the status in the query parameters
            cursor = db.rawQuery(query, new String[]{id, status});
        }
        return cursor;
    }
   /*public Cursor readDataByOuletIDAndStatus(String id, String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch data with the given outlet ID and status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_STATUS + " = ? AND " +
                COLUMN_ID + " IN (SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND " +
                COLUMN_STATUS + " = ? GROUP BY " + COLUMN_OUTLETID + ") ORDER BY " + COLUMN_DATE_TIME + " DESC";

        Cursor cursor = null;
        if (db != null) {
            // Execute the query with the outlet ID and status as parameters
            cursor = db.rawQuery(query, new String[]{id, status, id, status});
        }
        return cursor;
    }*/
    public Cursor readDataByOutletIDAndStatus(String id, String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch the latest order by time with the given outlet ID and status
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_STATUS + " = ? " +
                "ORDER BY " + COLUMN_APPROVED_ORDER_TIME + " DESC LIMIT 1";

        Cursor cursor = null;
        if (db != null) {
            // Execute the query with the outlet ID and status as parameters
            cursor = db.rawQuery(query, new String[]{id, status});
        }
        return cursor;
    }
    public Cursor readAllorderDataByOutletIDAndStatus(String outletid, String orderid, String status, String status2) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Construct the query to fetch the latest order by time with the given outlet ID and status
        String query = "SELECT DISTINCT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_OUTLETID + " = ? AND " + COLUMN_ORDERID + " = ? " +
                " AND (" + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?) " ;

        Cursor cursor = null;
        if (db != null) {
            // Execute the query with the outlet ID, order ID, and statuses as parameters
            cursor = db.rawQuery(query, new String[]{outletid, orderid, status, status2});
        }
        return cursor;
    }

    public boolean deleteOrder(String orderID) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ORDERID + "=?", new String[]{orderID});
            Log.d("SubmitOrderDB", "Rows deleted: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SubmitOrderDB", "Error deleting order: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }
    @SuppressLint("Range")
    public boolean deleteItemByProductIDAndOrderID(String productID, String orderID) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Retrieve the existing data for the specified order ID
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?", new String[]{orderID});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    String userId = cursor.getString(cursor.getColumnIndex(COLUMN_USERID));
                    String vanId = cursor.getString(cursor.getColumnIndex(COLUMN_VANID));
                    String outletId = cursor.getString(cursor.getColumnIndex(COLUMN_OUTLETID));
                    String productIDs = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
                    String itemCodes = cursor.getString(cursor.getColumnIndex(COLUMN_ITEMCODE));
                    String quantities = cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTED_QTY));
                    String dateTime = cursor.getString(cursor.getColumnIndex(COLUMN_ORDERED_DATE_TIME));

                    // Split concatenated strings into arrays
                    String[] productIdArray = productIDs.split(",");
                    String[] itemCodeArray = itemCodes.split(",");
                    String[] quantityArray = quantities.split(",");

                    // Find and remove the productID and its associated quantity
                    for (int i = 0; i < productIdArray.length; i++) {
                        if (productIdArray[i].equals(productID)) {
                            // Remove the productID and its associated data
                            productIdArray = removeElement(productIdArray, i);
                            itemCodeArray = removeElement(itemCodeArray, i);
                            quantityArray = removeElement(quantityArray, i);
                            break;  // Stop searching once found
                        }
                    }

                    // Update the database with the modified data
                    updateOrderData(db, id, orderID, userId, vanId, outletId,
                            joinArray(productIdArray), joinArray(itemCodeArray), joinArray(quantityArray), dateTime);
                } while (cursor.moveToNext());

                cursor.close();
            }

            return true;  // Deletion successful
        } catch (Exception e) {
            Log.e("SubmitOrderDB", "Error deleting item: " + e.getMessage(), e);
            return false;
        } finally {
            db.close();
        }
    }



    private String[] removeElement(String[] array, int index) {
        if (index < 0 || index >= array.length) {
            return array;
        }

        String[] newArray = new String[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);

        return newArray;
    }
    private String joinArray(String[] array) {
        return TextUtils.join(",", array);
    }


    private void updateOrderData(SQLiteDatabase db, int id, String orderID, String userId, String vanId, String outletId,
                                 String productIDs, String itemCodes, String quantities, String dateTime) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ORDERID, orderID);
        cv.put(COLUMN_USERID, userId);
        cv.put(COLUMN_VANID, vanId);
        cv.put(COLUMN_OUTLETID, outletId);
        cv.put(COLUMN_PRODUCTID, productIDs);
        cv.put(COLUMN_ITEMCODE, itemCodes);
        cv.put(COLUMN_REQUESTED_QTY, quantities);
        cv.put(COLUMN_ORDERED_DATE_TIME, dateTime);

        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    @SuppressLint("Range")
    public boolean updateQuantityByProductIDAndOrderID(String productID, String orderID, String newQuantity) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Retrieve the existing data for the specified order ID
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?", new String[]{orderID});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String productIdString = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
                    String quantityString = cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTED_QTY));

                    String[] productIds = productIdString.split(",");
                    String[] quantities = quantityString.split(",");

                    // Find the index of the product ID in the array
                    int index = -1;
                    for (int i = 0; i < productIds.length; i++) {
                        if (productIds[i].equals(productID)) {
                            index = i;
                            break;
                        }
                    }

                    // If the product ID is found, update its quantity
                    if (index != -1) {
                        quantities[index] = newQuantity; // Update the quantity at the found index

                        // Join the arrays back into strings
                        String newProductIdString = TextUtils.join(",", productIds);
                        String newQuantityString = TextUtils.join(",", quantities);

                        // Update the row in the database with the new quantity
                        ContentValues cv = new ContentValues();
                        cv.put(COLUMN_PRODUCTID, newProductIdString);
                        cv.put(COLUMN_REQUESTED_QTY, newQuantityString);

                        db.update(TABLE_NAME, cv, COLUMN_ORDERID + " = ? AND " + COLUMN_PRODUCTID + " = ?", new String[]{orderID, productID});
                    }
                } while (cursor.moveToNext());

                cursor.close();
            }

            return true;  // Update successful
        } catch (Exception e) {
            Log.e("SubmitOrderDB", "Error updating quantity: " + e.getMessage(), e);
            return false;
        } finally {
            db.close();
        }
    }



    @SuppressLint("Range")
    private void updateOrderData(SQLiteDatabase db, int id, String orderID, Cursor cursor) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REQUESTED_QTY, cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTED_QTY)));

        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public void OrderdeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }

    public Cursor getOrdersBasedOnDeliveryStatus(String status1, String status2, String status3, String status4, String status5, String fromDate, String toDate) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use getReadableDatabase() if you're only performing a read operation

        try {
            // Query to get orders with status "status1", "status2", "status3", "status4", or "status5"
            // and filter by date range
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE (" + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=? OR " + COLUMN_STATUS + "=?)" +
                    " AND " + COLUMN_DELIVERED_DATE_TIME + " >= ?" +
                    " AND " + COLUMN_DELIVERED_DATE_TIME + " <= ?";

            // Execute the query with the provided parameters
            return db.rawQuery(query, new String[]{status1, status2, status3, status4, status5, fromDate, toDate});
        } catch (Exception e) {
            // Handle any exceptions that may occur during query execution
            e.printStackTrace();
            return null; // Return null to indicate an error occurred
        }
    }


    public Cursor getOrdersBasedOnInvNoOrOrderId(String invOrOrderId) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use getReadableDatabase() for read operations

        try {
            // Query to find orders where either the order ID or invoice number matches invOrOrderId
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + "=? OR " + COLUMN_INVOICE_NO + "=?";
            return db.rawQuery(query, new String[]{invOrOrderId, invOrOrderId}); // Supply invOrOrderId for both parameters
        } catch (Exception e) {
            // Handle any exceptions that may occur during query execution
            e.printStackTrace();
            return null; // Return null to indicate an error occurred
        }
    }
    @SuppressLint("Range")
    public String getLastInvoiceNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        String lastInvoiceNumber = "";

        // Query to extract numeric portion from the invoice number and find the max
        String query = "SELECT " + COLUMN_INVOICE_NO + " FROM " + TABLE_NAME +
                " ORDER BY CAST(SUBSTR(" + COLUMN_INVOICE_NO + ", 12) AS INTEGER) DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            lastInvoiceNumber = cursor.getString(cursor.getColumnIndex(COLUMN_INVOICE_NO));  // Get the last invoice number
        }

        cursor.close();
        return lastInvoiceNumber;
    }


    public void insertMultipleDetails(List<DeliveredOrderItemLevelDetails> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Map to store totalQty for each orderid
            Map<String, Integer> orderQtyMap = new HashMap<>();

            for (DeliveredOrderItemLevelDetails data : dataList) {
                String orderid = data.getOrderid();

                // Handle potential null or empty deliveredQty values
                String deliveredQtyStr = data.getDeliveredQty();
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

                // Call addDeliveredItemsTransaction with updated totalQty
                addDeliveredItemsTransaction(
                        db,
                        data.getItemtotal(),
                        data.getVatamount(),
                        data.getPoReference(),
                        data.getPoRefName(),
                        data.getPocreatedDatetime(),
                        data.getOrderedQty(),
                        data.getApprovedQty(),
                        data.getNetamount(),
                        data.getItemId(),
                        data.getAgencyId(),
                        data.getRebate(),
                        orderid,
                        data.getInvoiceno(),
                        data.getItemCode(),
                        data.getSellingPrice(),
                        String.valueOf(deliveredQty),  // Use the deliveredQty here
                        data.getVat(),
                        String.valueOf(totalQty)        // Pass the accumulated totalQty for this orderid
                );
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    private void addDeliveredItemsTransaction(SQLiteDatabase db, String itemtotal, String vatamount, String poReference, String poRefName,
                                              String pocreatedDatetime, String orderedQty, String approvedQty, String netamount,
                                              String itemId, String agencyId, String rebate, String orderid, String invoiceno,
                                              String itemCode,String sellingprice, String deliveredQty, String vat,String totalQty) {

        // Fetch the existing data for the given orderid or invoiceno
        String[] columns = {COLUMN_GROSS, COLUMN_VAT_AMT, COLUMN_PO_REF, COLUMN_PO_REF_NAME, COLUMN_PO_CREATED_DATE,
                COLUMN_REQUESTED_QTY, COLUMN_APPROVED_QTY, COLUMN_NET, COLUMN_PRODUCTID, COLUMN_AGENCYID,
                COLUMN_DISC, COLUMN_ITEMCODE,COLUMN_SELLING_PRICE, COLUMN_DELIVERED_QTY, COLUMN_VAT_PERCENT};

        Cursor cursor = db.query(TABLE_NAME, columns, COLUMN_ORDERID + "=? AND " + COLUMN_INVOICE_NO + "=?",
                new String[]{orderid, invoiceno}, null, null, null);

        // Initialize StringBuilder for each field
        StringBuilder itemtotalbuilder = new StringBuilder();
        StringBuilder vatamountbuilder = new StringBuilder();
        StringBuilder poRefBuilder = new StringBuilder();
        StringBuilder poRefNameBuilder = new StringBuilder();
        StringBuilder poCreatedDateBuilder = new StringBuilder();
        StringBuilder orderQtyBuilder = new StringBuilder();
        StringBuilder approvedQtyBuilder = new StringBuilder();
        StringBuilder netamountBuilder = new StringBuilder();
        StringBuilder itemIdBuilder = new StringBuilder();
        StringBuilder agencyIdBuilder = new StringBuilder();
        StringBuilder rebateBuilder = new StringBuilder();
        StringBuilder itemCodeBuilder = new StringBuilder();
        StringBuilder delQtyBuilder = new StringBuilder();
        StringBuilder vatBuilder = new StringBuilder();
        StringBuilder sellingpricebuilder=new StringBuilder();

        // Check if the record already exists

        if (cursor.moveToFirst()) {
            do {
                // Accumulate existing delivered quantities
             // If data exists, append it to existing values

                itemtotalbuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_GROSS))).append(",");
                vatamountbuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_VAT_AMT))).append(",");
                poRefBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PO_REF))).append(",");
                poRefNameBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PO_REF_NAME))).append(",");
                poCreatedDateBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PO_CREATED_DATE))).append(",");
                orderQtyBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTED_QTY))).append(",");
                approvedQtyBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_APPROVED_QTY))).append(",");
                netamountBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_NET))).append(",");
                itemIdBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID))).append(",");
                agencyIdBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_AGENCYID))).append(",");
                rebateBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_DISC))).append(",");
                itemCodeBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_ITEMCODE))).append(",");
                sellingpricebuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_SELLING_PRICE))).append(",");
                delQtyBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERED_QTY))).append(",");
                vatBuilder.append(cursor.getString(cursor.getColumnIndex(COLUMN_VAT_PERCENT))).append(",");
            } while (cursor.moveToNext()); // Move to the next row
        }

        // Append new values
        itemtotalbuilder.append(itemtotal).append(",");
        vatamountbuilder.append(vatamount).append(",");
        poRefBuilder.append(poReference).append(",");
        poRefNameBuilder.append(poRefName).append(",");
        poCreatedDateBuilder.append(pocreatedDatetime).append(",");
        orderQtyBuilder.append(orderedQty).append(",");
        approvedQtyBuilder.append(approvedQty).append(",");
        netamountBuilder.append(netamount).append(",");
        itemIdBuilder.append(itemId).append(",");
        agencyIdBuilder.append(agencyId).append(",");
        rebateBuilder.append(rebate).append(",");
        itemCodeBuilder.append(itemCode).append(",");
        sellingpricebuilder.append(sellingprice).append(",");
        delQtyBuilder.append(deliveredQty).append(",");
        vatBuilder.append(vat).append(",");



        // Remove trailing commas from all builders
        itemtotal = removeTrailingComma(itemtotalbuilder);
        vatamount = removeTrailingComma(vatamountbuilder);
        poReference = removeTrailingComma(poRefBuilder);
        poRefName = removeTrailingComma(poRefNameBuilder);
        pocreatedDatetime = removeTrailingComma(poCreatedDateBuilder);
        orderedQty = removeTrailingComma(orderQtyBuilder);
        approvedQty = removeTrailingComma(approvedQtyBuilder);
        netamount = removeTrailingComma(netamountBuilder);
        itemId = removeTrailingComma(itemIdBuilder);
        agencyId = removeTrailingComma(agencyIdBuilder);
        rebate = removeTrailingComma(rebateBuilder);
        itemCode = removeTrailingComma(itemCodeBuilder);
        sellingprice=removeTrailingComma(sellingpricebuilder);
        deliveredQty = removeTrailingComma(delQtyBuilder);
        vat = removeTrailingComma(vatBuilder);







        // ContentValues for updating the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_GROSS, itemtotal != null ? itemtotal : "");  // Default to empty string if null
        contentValues.put(COLUMN_VAT_AMT, vatamount != null ? vatamount : "");
        contentValues.put(COLUMN_PO_REF, poReference != null ? poReference : "");
        contentValues.put(COLUMN_PO_REF_NAME, poRefName != null ? poRefName : "");
        contentValues.put(COLUMN_PO_CREATED_DATE, pocreatedDatetime != null ? pocreatedDatetime : "");
        contentValues.put(COLUMN_REQUESTED_QTY, orderedQty != null ? orderedQty : "");
        contentValues.put(COLUMN_APPROVED_QTY, approvedQty != null ? approvedQty : "");
        contentValues.put(COLUMN_NET, netamount != null ? netamount : "");
        contentValues.put(COLUMN_PRODUCTID, itemId != null ? itemId : "");
        contentValues.put(COLUMN_AGENCYID, agencyId != null ? agencyId : "");
        contentValues.put(COLUMN_DISC, rebate != null ? rebate : "0.00");
        contentValues.put(COLUMN_ORDERID, orderid != null ? orderid : "");
        contentValues.put(COLUMN_INVOICE_NO, invoiceno != null ? invoiceno : "");
        contentValues.put(COLUMN_ITEMCODE, itemCode != null ? itemCode : "");
        contentValues.put(COLUMN_SELLING_PRICE,sellingprice!=null ? sellingprice : "");
        contentValues.put(COLUMN_DELIVERED_QTY, deliveredQty != null ? deliveredQty : "");
        contentValues.put(COLUMN_VAT_PERCENT, vat != null ? vat : "");
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

    // Utility method to remove the trailing comma


    public void insertMultipleDetails2(List<DeliveredOrderLevelDetails> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (DeliveredOrderLevelDetails data : dataList) {

                updateDeliveredItemsTransaction(db, data.getTotalvatanount(), data.getCommets(), data.getTotalnetamount(),
                        data.getInvoicetotal(),data.getInvoicewithoutrebate(), data.getRefno(), data.getOutletCode(), data.getOutletId(),
                        data.getOrderid(), data.getInvoiceno(), data.getOrderedDatetime(), data.getDeliveredDatetime(),data.getCustomerCode());

                // Update progress after each insertion, if needed
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void updateDeliveredItemsTransaction(SQLiteDatabase db, String totalVatAmount, String comments, String totalNetAmount,
                                                 String invoiceTotal,String invoicewithoutrebate, String refNo, String outletCode, String outletId,
                                                 String orderId, String invoiceNo, String orderedDatetime, String deliveredDatetime,String customercode) {

        // ContentValues for updating the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TOTAL_VAT_AMOUNT, totalVatAmount != null ? totalVatAmount : "");  // Use empty string if null
        contentValues.put(COLUMN_COMMENTS, comments != null ? comments : "");
        contentValues.put(COLUMN_TOTAL_NET_AMOUNT, totalNetAmount != null ? totalNetAmount : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT, invoicewithoutrebate != null ? invoicewithoutrebate : "");
        contentValues.put(COLUMN_TOTAL_GROSS_AMOUNT_PAYABLE,invoiceTotal !=null ? invoiceTotal : "");
        contentValues.put(COLUMN_REFERENCE_NO, refNo != null ? refNo : "");
        contentValues.put(COLUMN_OUTLETID, outletId != null ? outletId : "");
        contentValues.put(COLUMN_ORDERID, orderId != null ? orderId : "");
        contentValues.put(COLUMN_ORDERED_DATE_TIME, orderedDatetime != null ? orderedDatetime : "");
        contentValues.put(COLUMN_DELIVERED_DATE_TIME, deliveredDatetime != null ? deliveredDatetime : "");
        contentValues.put(COLUMN_CUSTOMER_CODE_AFTER_DELIVER,customercode.toLowerCase()!=null ? customercode.toLowerCase():"");
        contentValues.put(COLUMN_STATUS,"DELIVERY DONE");

        // Update the record where the invoice number matches
        db.update(TABLE_NAME, contentValues, COLUMN_INVOICE_NO + " = ?", new String[]{invoiceNo});

    }

    public boolean deleteOrders(String orderid, String outletid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Use the correct column names for OrderId and OutletId
        return db.delete(TABLE_NAME, COLUMN_ORDERID + " = ? AND " + COLUMN_OUTLETID + " = ?", new String[]{orderid, outletid}) > 0;
    }

    public void deleteOrderById(String orderId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete row if order_id exists
        String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " =?";
        SQLiteStatement stmt = db.compileStatement(deleteQuery);
        stmt.bindString(1, orderId);  // Binding the order_id parameter to the query (using bindString for String)

        stmt.executeUpdateDelete();

        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }


    public int getOrderCountByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME
                + " WHERE DATE(" + COLUMN_ORDERED_DATE_TIME + ") = ?"
                + " AND OrderId LIKE '%-M'";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public int getDeliveredOrderCountByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME
                + " WHERE DATE(" + COLUMN_DELIVERED_DATE_TIME + ") = ?"
                + " AND " + COLUMN_ORDERID + " LIKE '%-M'"
                + " AND " + COLUMN_STATUS + " IN ('DELIVERY DONE', 'DELIVERED')";

        Cursor cursor = db.rawQuery(query, new String[]{date});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    public int getMExOrderCountByDate(String startdate, String enddate ) {
        SQLiteDatabase db = this.getReadableDatabase();

     /*String query = "SELECT COUNT(*) FROM " + TABLE_NAME
             + " WHERE " + COLUMN_DELIVERED_DATE_TIME + " BETWEEN ? AND ?"
             + " AND " + COLUMN_EXTRA_STATUS + " = ?";*/
        String query = "SELECT COUNT(*) FROM my_submit_order"
                + " WHERE DateTime >= ?"
                + " AND DateTime <= ?"
                + " AND OrderId LIKE ?";


        Cursor cursor = db.rawQuery(query, new String[]{startdate, enddate,  "%-M-EX"});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        Log.d("DatabaseQuery", "Executed Query: " + query);
        Log.d("DatabaseResult", "Order Count: " + count);

        cursor.close();
        return count;
    }


    public int getInvoiceCountForDeliveredOrders(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) " +
                "FROM " + TABLE_NAME + " " +
                "WHERE strftime('%Y-%m-%d', " + COLUMN_DELIVERED_DATE_TIME + ") = ? " +
                "AND " + COLUMN_INVOICE_NO + " IS NOT NULL " +
                "AND " + COLUMN_STATUS + " IN ('DELIVERY DONE', 'DELIVERED') " +
                "AND " + COLUMN_ORDERID + " LIKE '%-M'";

        // Prepare the query dynamically to include multiple status values ('DELIVERY DONE' or 'DELIVERED')
    /*String query = "SELECT COUNT(*) " +
            "FROM " + TABLE_NAME + " " +
            "WHERE strftime('%Y-%m-%d', " + COLUMN_DELIVERED_DATE_TIME + ") = ? " +
            "AND " + COLUMN_INVOICE_NO + " IS NOT NULL " +
            "AND " + COLUMN_STATUS + " IN ('DELIVERY DONE', 'DELIVERED')";

     */

        // Modify the query to count distinct INVOICE_NO for the given date and statuses
    /*String query = "SELECT COUNT(DISTINCT " + COLUMN_INVOICE_NO + ") FROM " + TABLE_NAME
            + " WHERE (COLUMN_STATUS = 'DELIVERY DONE' OR COLUMN_STATUS = 'DELIVERED') "
            + "AND " + COLUMN_DELIVERED_DATE_TIME + " >= ?";  // Using COLUMN_DELIVERED_DATE_TIME for the date filter

     */

        // Execute the query with the provided date parameter
        //Cursor cursor = null;
        Cursor cursor = db.rawQuery(query, new String[]{date});
        int count = 0;

        try {
            cursor = db.rawQuery(query, new String[]{date});

            // Check if the query returned any result
            if (cursor != null && cursor.moveToFirst()) {
                // Return the distinct invoice count for the given date
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            // Log any errors that occur during the query execution
            e.printStackTrace();
        } finally {
            // Close the cursor to avoid memory leaks
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }



    public double getTotalGrossAmountByStatusForDate(String date, String status) {
        double totalAmount = 0.0;

        // Query to get the sum of Total_Gross_Amount_Without_Rebate for the given date and status
        String query = "SELECT SUM(" + COLUMN_TOTAL_GROSS_AMOUNT + ") FROM " + TABLE_NAME
                + " WHERE DATE(" + COLUMN_ORDERED_DATE_TIME + ") = ?"
                + " AND " + COLUMN_STATUS + " = ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{date, status});
            if (cursor != null && cursor.moveToFirst()) {
                totalAmount = cursor.getDouble(0); // Get the sum value
            }
        } catch (Exception e) {
            Log.e("TAG", "Error in getTotalGrossAmountByStatusForDate: ", e);
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor to prevent memory leaks
            }
        }

        return totalAmount;
    }
}

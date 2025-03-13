package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.malta_mqf.malta_mobile.Model.VanLoadDataForVanDetails;

import java.util.List;

public class TotalApprovedOrderBsdOnItem extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="totalapproved.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="my_approved";
    private static final String  COLUMN_ID="_id";
    public static final String COLUMN_VAN_ID="vanId";
    public static final String COLUMN_PRODUCTNAME="ProductName";
    public static final String COLUMN_PRODUCTID="productId";
    public static final String COLUMN_TOTAL_REQUESTEDQTY="Total_ReQ_Qty";
    public static final String COLUMN_TOTAL_APPROVEDQTY="Total_APPROVED_Qty";
    public static final String COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND="Total_Available_Qty_On_Hand";
    public static final String COLUMN_STATUS="Status";
    public static final String COLUMN_STATUS2="insertedORnotinsertedTOStockDB";
    public static final String COLUMN_DATE_TIME="date_time";
    public static final String COLUMN_AGENCY_CODE="Agency_Code";
    public static final String COLUMN_ITEM_CODE="item_code";
    public static final String COLUMN_ITEM_CATEGORY="item_category";
    public static final String COLUMN_ITEM_SUB_CATEGORY="item_sub_category";
    public static final String COLUMN_CURRENT_REQUESTEDQTY="current_Req_Qty";
    public static final String COLUMN_CURRENT_APPROVEDQTY="current_Approved_qty";
    public static final String COLUMN_EXPECTED_DELIVERY="expected_delivery";
    public static final String COLUMN_PO_REFERENCE="po_refrence";
    public static final String COLUMN_CURRENT_TOTAL_AVAILABLE_QTY="current_Available_total_qty";

    public TotalApprovedOrderBsdOnItem (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE my_approved (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Adding an ID column as a primary key
                COLUMN_VAN_ID + " TEXT, " + COLUMN_AGENCY_CODE + " TEXT, " + COLUMN_PRODUCTNAME + " TEXT, " + COLUMN_PRODUCTID + " TEXT, " + COLUMN_ITEM_CATEGORY + " TEXT, " + COLUMN_ITEM_SUB_CATEGORY + " TEXT, " + COLUMN_ITEM_CODE + " TEXT, " + COLUMN_CURRENT_REQUESTEDQTY + " INTEGER, " + COLUMN_TOTAL_REQUESTEDQTY + " INTEGER, " + COLUMN_CURRENT_APPROVEDQTY + " INTEGER, " + COLUMN_TOTAL_APPROVEDQTY + " INTEGER, " + COLUMN_CURRENT_TOTAL_AVAILABLE_QTY + " TEXT, " + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " TEXT, " + COLUMN_PO_REFERENCE + " TEXT, " + COLUMN_EXPECTED_DELIVERY + " TEXT, " + COLUMN_STATUS + " TEXT, " + COLUMN_STATUS2 + " TEXT, " + COLUMN_DATE_TIME + " TEXT, " + "UNIQUE(" + COLUMN_EXPECTED_DELIVERY + ", " + COLUMN_ITEM_CODE + ", " + COLUMN_STATUS + ")" + // Adding the unique constraint
                ");";


        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

  /*  public void addApprovedDetails(String orderid, String userid, String vanid, String prdtid, String approvedid, String status, String dttime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the order ID already exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?", new String[]{orderid});
        if (cursor.getCount() > 0) {
            // Order ID exists, retrieve existing values and append new ones
            cursor.moveToFirst();
            @SuppressLint("Range") String existingPrdtid = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCTID));
           @SuppressLint("Range") String existingApprovedid = cursor.getString(cursor.getColumnIndex(COLUMN_APPROVEDQTY));

           //  Append the new product ID and approved quantity to the existing values
            prdtid = existingPrdtid + "," + prdtid;
            approvedid = existingApprovedid + "," + approvedid;

            // Update the database entry with the new values
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ORDERID, orderid);
            cv.put(COLUMN_USERID, userid);
            cv.put(COLUMN_VAN_ID, vanid);
            cv.put(COLUMN_PRODUCTID, prdtid);
            cv.put(COLUMN_APPROVEDQTY, approvedid);
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_APPROVED_DT_TIME, dttime);

            int count = db.update(TABLE_NAME, cv, COLUMN_ORDERID + " = ?", new String[]{orderid});
            if (count > 0) {
                // Update Success
                // Toast.makeText(context, "Update Success!", Toast.LENGTH_SHORT).show();
            } else {
                // Update Failed
                // Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Order ID does not exist, insert new entry
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ORDERID, orderid);
            cv.put(COLUMN_USERID, userid);
            cv.put(COLUMN_VAN_ID, vanid);
            cv.put(COLUMN_PRODUCTID, prdtid);
            cv.put(COLUMN_APPROVEDQTY, approvedid);
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_APPROVED_DT_TIME, dttime);

            long result = db.insert(TABLE_NAME, null, cv);
            if (result == -1) {
                // Insert Failed
                // Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
            } else {
                // Insert Success
                // Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
            }
        }

        // Close the cursor and database connection
        cursor.close();
        db.close();
    }*/

    public void totaladdApprovedDetails( String vanid,String prouctname, String prdtid,String itemcode,String itemCategory,String itemsubcatory,String reqQty, String approvedid,String avalqty,String status,String dateTime,String agencycode){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_PRODUCTNAME,prouctname);
        cv.put(COLUMN_PRODUCTID,prdtid);
        cv.put(COLUMN_TOTAL_REQUESTEDQTY,reqQty);
        cv.put(COLUMN_TOTAL_APPROVEDQTY,approvedid);
        cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND,avalqty);
        cv.put(COLUMN_STATUS,status);
        cv.put(COLUMN_DATE_TIME,dateTime);
        cv.put(COLUMN_AGENCY_CODE,agencycode);
        cv.put(COLUMN_ITEM_CODE,itemcode);
        cv.put(COLUMN_ITEM_CATEGORY,itemCategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY,itemsubcatory);
        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
        }
    }
    public void totalUpdateApprovedData(String vanid,String prouctname, String prdtid,String itemcode,String itemcategory,String itemsubcategory,String reqQty, String approvedid,String avalqty,String status,String dateTime,String agencycode){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_PRODUCTNAME,prouctname);
        cv.put(COLUMN_PRODUCTID,prdtid);
        cv.put(COLUMN_TOTAL_REQUESTEDQTY,reqQty);
        cv.put(COLUMN_TOTAL_APPROVEDQTY,approvedid);
        cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND,avalqty);
        cv.put(COLUMN_STATUS,status);
        cv.put(COLUMN_DATE_TIME,dateTime);
        cv.put(COLUMN_AGENCY_CODE,agencycode);
        cv.put(COLUMN_ITEM_CODE,itemcode);
        cv.put(COLUMN_ITEM_CATEGORY,itemcategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY,itemsubcategory);
        long result= db.update(TABLE_NAME,cv,COLUMN_PRODUCTID + "=?",new String[] {prdtid});
        if(result==-1){
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            //  Toast.makeText(context," Updated Successfully!",Toast.LENGTH_SHORT).show();
        }
    }
    public void totalUpdateApprovedData2(String vanid, String prouctname, String prdtid, String itemCode, String reqQty, String approvedid, String avalqty, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Query the current available quantity for the product
            String query = "SELECT " + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ? AND (" + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?)";
            Cursor cursor = db.rawQuery(query, new String[]{prdtid, "NOT LOADED", "PARTIALLY LOADED"});

            int currentAvalQty = 0;
            if (cursor != null && cursor.moveToFirst()) {
                // Get the current available quantity
                currentAvalQty = cursor.getInt(0);
                cursor.close();
            }

            // Add the requested quantity to the current available quantity
            int updatedAvalQty = currentAvalQty + Integer.parseInt(avalqty);

            // Prepare the ContentValues with the updated available quantity
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_VAN_ID, vanid);
            cv.put(COLUMN_PRODUCTNAME, prouctname);
            cv.put(COLUMN_PRODUCTID, prdtid);
            cv.put(COLUMN_ITEM_CODE, itemCode);
            cv.put(COLUMN_CURRENT_REQUESTEDQTY, reqQty);
            cv.put(COLUMN_CURRENT_APPROVEDQTY, approvedid);
            cv.put(COLUMN_CURRENT_TOTAL_AVAILABLE_QTY, avalqty);
            cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, updatedAvalQty); // Update with the new quantity
            cv.put(COLUMN_STATUS, "LOADED"); // Set the status to "LOADED"
            cv.put(COLUMN_DATE_TIME, date);

            // Update the row in the database
            long result = db.update(TABLE_NAME, cv, COLUMN_PRODUCTID + " = ? AND (" + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?)", new String[]{prdtid, "NOT LOADED", "PARTIALLY LOADED"});

            if (result > 0) {
                System.out.println("Updated successfully. Rows affected: " + result);
            } else {
                System.out.println("No matching rows found to update.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        } finally {
            db.close(); // Ensure the database is closed
        }
    }
    public void totalUpdateApproveditems(String vanid,String prouctname, String reqQty, String approvedid,String avalqty){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_PRODUCTNAME,prouctname);
        cv.put(COLUMN_TOTAL_REQUESTEDQTY,reqQty);
        cv.put(COLUMN_TOTAL_APPROVEDQTY,approvedid);
        cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND,avalqty);
        long result= db.update(TABLE_NAME,cv,COLUMN_PRODUCTID + "=?",new String[] {prouctname});
        if(result==-1){
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            //  Toast.makeText(context," Updated Successfully!",Toast.LENGTH_SHORT).show();
        }
    }
    public Cursor totalreadAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    public Cursor readonproductid(String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return cursor;
    }
    public Cursor readonProductIDandStatus(String productid,String status){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?" + " AND " + COLUMN_STATUS + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{productid,status});
        }
        return cursor;
    }

    public Cursor readonProductIDandStatus(String productid, String status1, String status2) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?" + " AND " + COLUMN_STATUS + " = ?" + " OR " + COLUMN_STATUS + " = ?";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{productid, status1, status2});
        }
        return cursor;
    }
    public Cursor GetProductDataNotLoadedBYStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Aggregated query to get the sum of quantities and the latest date_time
        String query = "SELECT " +
                COLUMN_PRODUCTNAME + ", " +
                COLUMN_VAN_ID + ", " +
                COLUMN_PRODUCTID + ", " +
                "SUM(" + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + ") AS Total_Qty_On_Hand, " +
                "SUM(" + COLUMN_TOTAL_APPROVEDQTY + ") AS Total_Approved_Qty, " +
                "MAX(" + COLUMN_DATE_TIME + ") AS datetime " +
                "FROM " + TABLE_NAME + " " +
                "WHERE " + COLUMN_STATUS + " = ? " +
                "GROUP BY " + COLUMN_PRODUCTNAME + ", " + COLUMN_VAN_ID + ", " + COLUMN_PRODUCTID;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }

    public Cursor GetAgencyDataNotLoadedBYStatus(String status){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }
    public Cursor GetAgencyDataNotLoadedBYStatus(String status, String status2) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            // Construct query
            String query = "SELECT DISTINCT " + COLUMN_AGENCY_CODE +
                    " FROM " + TABLE_NAME +
                    " WHERE (" + COLUMN_STATUS + " = ? OR " + COLUMN_STATUS + " = ?)";
            // Execute query
            cursor = db.rawQuery(query, new String[]{status, status2});
        } else {
            // Log error for debugging
            Log.e("DatabaseError", "Database is not initialized");
        }
        return cursor;
    }


    public void totaldeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }
    public void totaldeleteByStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete records where status is 'NOT LOADED'
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 'NOT LOADED'");

        // Optionally, reset the auto-increment counter if all records are deleted
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        }
        cursor.close();

        db.close();
    }
    public void deleteOldRecords() {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE datetime(" + COLUMN_DATE_TIME +
                ") <= datetime('now', '-7 days')";

        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL(query);
    }


    public void totaldeleteByStatusAfterSync() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete records where status is 'NOT LOADED'
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 'LOADED SYNCED'");

        // Optionally, reset the auto-increment counter if all records are deleted
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        }
        cursor.close();

        db.close();
    }

    public void updateProductStatusAfterLoading(String prodID, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        // Define the selection criteria (where clause)
        String selection = COLUMN_PRODUCTID + " = ?";
        String[] selectionArgs = {prodID};

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


    public void updateProductStatusAfterLoading2(String prodID, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        // Define the selection criteria (where clause)
        String selection = COLUMN_PRODUCTID + " = ?";
        String[] selectionArgs = {prodID};

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

    public void updateProductStatusAfterLoading(String prodID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CURRENT_REQUESTEDQTY, 0);
        cv.put(COLUMN_CURRENT_APPROVEDQTY, 0);
        cv.put(COLUMN_CURRENT_TOTAL_AVAILABLE_QTY, "");

        // Define the selection criteria (where clause)
        String selection = COLUMN_PRODUCTID + " = ?";
        String[] selectionArgs = {prodID};

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
    public void insertMultipleDetails(List<VanLoadDataForVanDetails> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); // Start the outer transaction
        try {
            for (VanLoadDataForVanDetails data : dataList) {
                String itemCode = data.getItemCode();
                String item_id = data.getItemId();
                String requested_quantity = data.getOrderedQty();
                String approved_quantity = data.getApprovedQty();
                String load_quantity = data.getLoadedQty();
                String poReference = data.getPoReference();
                String itemName = data.getItemName();
                String loadedDate = data.getLoadedDate();
                String loadStatus = data.getLoadStatus();
                String expected_delivery = data.getExpectedDelivery();

                String poReferenceArray = poReference.replace("&", ",");

                // Insert the data into the database
                insertVanLoadData(db, itemCode, item_id, requested_quantity, approved_quantity, load_quantity, poReferenceArray, itemName, loadedDate, loadStatus, expected_delivery);
            }
            db.setTransactionSuccessful(); // Mark transaction successful
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); // End the outer transaction
            db.close(); // Close the database
        }
    }
    public void totaldeleteByStatusPRL() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete records where status is 'NOT LOADED'
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 'PRL'" );

        // Optionally, reset the auto-increment counter if all records are deleted
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        }
        cursor.close();

        db.close();
    }
    private void insertVanLoadData(SQLiteDatabase db, String itemCode, String item_id, String requested_quantity, String approved_quantity, String load_quantity, String poReference, String itemName, String loadedDate, String loadStatus, String expected_delivery) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ITEM_CODE, itemCode);
            cv.put(COLUMN_PRODUCTID, item_id);
            cv.put(COLUMN_TOTAL_REQUESTEDQTY, requested_quantity);
            cv.put(COLUMN_TOTAL_APPROVEDQTY, approved_quantity);
            cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, load_quantity);
            cv.put(COLUMN_PO_REFERENCE, poReference);
            cv.put(COLUMN_PRODUCTNAME, itemName);
            cv.put(COLUMN_DATE_TIME, loadedDate);
            cv.put(COLUMN_STATUS, loadStatus);
            cv.put(COLUMN_EXPECTED_DELIVERY, expected_delivery);

            long result = db.insert(TABLE_NAME, null, cv);

            if (result == -1) {
                System.out.println("Insert failed!");
            } else {
                System.out.println("Insert successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void handleDatabaseScenarios(String vanID, String agencyCode, String prodctName, String item_id, String itemCode, String itemCategory, String itemSubCategory, String reQty, String approved_quantity, String poReference, String selectedDate) {
        SQLiteDatabase db = null;
        try {
            // Open the database in write mode
            db = getWritableDatabase();
            db.beginTransaction();


            // Check if an entry with "PRL" status and the same PO already exists
            String checkPRLQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_EXPECTED_DELIVERY +
                    " = ? AND " + COLUMN_ITEM_CODE + " = ? AND "
                    + COLUMN_STATUS + " = 'PRL' AND INSTR(" + COLUMN_PO_REFERENCE + ", ?) > 0";
            SQLiteStatement checkPRLStmt = db.compileStatement(checkPRLQuery);
            checkPRLStmt.bindString(1, selectedDate);
            checkPRLStmt.bindString(2, itemCode);
            checkPRLStmt.bindString(3, poReference);


            long prlCount = checkPRLStmt.simpleQueryForLong();


            // If a matching "PRL" record with the same PO exists, skip this insertion
            if (prlCount > 0) {
                db.setTransactionSuccessful(); // No changes needed, just end the transaction
                return;
            }


            // Proceed with normal insertion or updates
            String checkExistQuery = "SELECT COUNT(*) FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_EXPECTED_DELIVERY + " = ? AND " + COLUMN_ITEM_CODE + " = ? AND " + COLUMN_STATUS + " != 'PRL'";
            SQLiteStatement checkExistStmt = db.compileStatement(checkExistQuery);
            checkExistStmt.bindString(1, selectedDate);
            checkExistStmt.bindString(2, itemCode);


            long count = checkExistStmt.simpleQueryForLong();


            if (count > 0) {
                // Update existing record
                String updateQuery = "UPDATE " + TABLE_NAME + " SET " + COLUMN_TOTAL_REQUESTEDQTY + " = " + COLUMN_TOTAL_REQUESTEDQTY + " + ?, " + COLUMN_CURRENT_REQUESTEDQTY + " = " + COLUMN_CURRENT_REQUESTEDQTY + " + ?, "
                        + COLUMN_TOTAL_APPROVEDQTY + " = " + COLUMN_TOTAL_APPROVEDQTY + " + ?, "
                        + COLUMN_CURRENT_APPROVEDQTY + " = " + COLUMN_CURRENT_APPROVEDQTY + " + ?, "
                        + COLUMN_PO_REFERENCE + " = CASE WHEN INSTR(" + COLUMN_PO_REFERENCE + ", ?) = 0 "
                        + "THEN " + COLUMN_PO_REFERENCE + " || ',' || ? ELSE " + COLUMN_PO_REFERENCE + " END, "
                        + COLUMN_STATUS + " = CASE WHEN " + COLUMN_STATUS + " = 'LOADED' THEN 'PARTIALLY LOADED' ELSE " + COLUMN_STATUS + " END "
                        + "WHERE " + COLUMN_EXPECTED_DELIVERY + " = ? AND " + COLUMN_ITEM_CODE + " = ? AND " + COLUMN_STATUS + " != 'PRL';";


                SQLiteStatement updateStmt = db.compileStatement(updateQuery);
                updateStmt.bindString(1, reQty);
                updateStmt.bindString(2, reQty);
                updateStmt.bindString(3, approved_quantity);
                updateStmt.bindString(4, approved_quantity);
                updateStmt.bindString(5, poReference);
                updateStmt.bindString(6, poReference);
                updateStmt.bindString(7, selectedDate);
                updateStmt.bindString(8, itemCode);
                updateStmt.execute();
            } else {
                // Insert a new record
                String insertQuery = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_VAN_ID + ", " + COLUMN_EXPECTED_DELIVERY + ", " + COLUMN_ITEM_CODE + ", " + COLUMN_PRODUCTID + ", " + COLUMN_ITEM_CATEGORY + ", " + COLUMN_ITEM_SUB_CATEGORY + ", " + COLUMN_AGENCY_CODE + ", " + COLUMN_PRODUCTNAME + ", " + COLUMN_TOTAL_REQUESTEDQTY + ", " + COLUMN_CURRENT_REQUESTEDQTY + ", " + COLUMN_TOTAL_APPROVEDQTY + ", " + COLUMN_CURRENT_APPROVEDQTY + ", " + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + ", " + COLUMN_STATUS + ", " + COLUMN_PO_REFERENCE + ") "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'NOT LOADED', ?);";


                SQLiteStatement insertStmt = db.compileStatement(insertQuery);
                insertStmt.bindString(1, vanID);
                insertStmt.bindString(2, selectedDate);
                insertStmt.bindString(3, itemCode);
                insertStmt.bindString(4, item_id);
                insertStmt.bindString(5, itemCategory);
                insertStmt.bindString(6, itemSubCategory);
                insertStmt.bindString(7, agencyCode);
                insertStmt.bindString(8, prodctName);
                insertStmt.bindString(9, reQty);
                insertStmt.bindString(10, reQty);
                insertStmt.bindString(11, approved_quantity);
                insertStmt.bindString(12, approved_quantity);
                insertStmt.bindString(13, "");
                insertStmt.bindString(14, poReference);
                insertStmt.execute();
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
            public boolean hasPendingAgencies(SQLiteDatabase db) {
        String query = "SELECT DISTINCT " + COLUMN_AGENCY_CODE +
                " FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 'not inserted'";

        Cursor cursor = db.rawQuery(query, null);
        boolean hasPending = cursor.getCount() > 0;
        cursor.close();
        return hasPending;
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Incomplete Loading!!!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

}

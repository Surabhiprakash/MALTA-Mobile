package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApprovedOrderDB extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME="approved.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="my_approved";
    private static final String  COLUMN_ID="_id";
    public static final String COLUMN_USERID="UserId";
    public static final String COLUMN_VAN_ID="vanId";
    public static final String COLUMN_ORDERID="OrderId";
    public static final String COLUMN_PRODUCTNAME="ProductName";
    public static final String COLUMN_PRODUCTID="productId";
    public static final String COLUMN_REQUESTEDQTY="ReQ_Qty";
    public static final String COLUMN_APPROVEDQTY="APPROVED_Qty";
    public static final String COLUMN_PO="PO_reference";
    public static final String COLUMN_PO_REFNAME="PO_Ref_name";
    public static final String COLUMN_PO_CREATED_DATE="PO_created_date";
    public static final String COLUMN_ITEM_CATEGORY="item_category";
    public static final String COLUMN_ITEM_SUB_CATEGORY="item_sub_category";
    public static final String COLUMN_STATUS="Status";
    public static final String COLUMN_APPROVED_DT_TIME="APPROVED_DT_TIME";
    public static final String COLUMN_ORDERED_DT_TIME="Order_DT_Time";
    public static final String COLUMN_OUTLETID="OUTLET_ID";
    public static final String COLUMN_CURRENT_DT="insert_date_time";
    public static final String COLUMN_EXPECTED_DELIVERY="Expected_delivery";
    public ApprovedOrderDB (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ORDERID + " TEXT, " +
                        COLUMN_USERID + " TEXT, " +
                        COLUMN_VAN_ID + " TEXT, " +
                        COLUMN_PRODUCTNAME + " TEXT, " +
                        COLUMN_PRODUCTID + " TEXT, " +
                        COLUMN_ITEM_CATEGORY+ " TEXT, " +
                        COLUMN_ITEM_SUB_CATEGORY+ " TEXT, " +
                        COLUMN_REQUESTEDQTY + " TEXT, " +
                        COLUMN_APPROVEDQTY + " TEXT, " +
                        COLUMN_PO + " TEXT, " +
                        COLUMN_STATUS + " TEXT, " +
                        COLUMN_OUTLETID + " TEXT, " +
                        COLUMN_PO_REFNAME + " TEXT, " +
                        COLUMN_PO_CREATED_DATE + " TEXT, " +
                        COLUMN_CURRENT_DT + " TEXT, " +
                        COLUMN_EXPECTED_DELIVERY + " TEXT, "+
                        COLUMN_ORDERED_DT_TIME + " TEXT, " +
                        COLUMN_APPROVED_DT_TIME+ " TEXT ); " ;

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void beginTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.endTransaction();
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

    public long addApprovedDetails(String orderid, String userid, String vanid, String prouctname, String prdtid, String itemcategory, String itemsubcategory, String reqQty, String approvedid, String po_ref, String outletid, String status, String dttime, String orderedtime, String po_ref_name, String po_created_date, String cureent_date_time, String expected_delivery){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ORDERID,orderid);
        cv.put(COLUMN_USERID,userid);
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_PRODUCTNAME,prouctname);
        cv.put(COLUMN_PRODUCTID,prdtid);
        cv.put(COLUMN_REQUESTEDQTY,reqQty);
        cv.put(COLUMN_APPROVEDQTY,approvedid);
        cv.put(COLUMN_PO,po_ref);
        cv.put(COLUMN_OUTLETID,outletid);
        cv.put(COLUMN_STATUS,status);
        cv.put(COLUMN_APPROVED_DT_TIME,dttime);
        cv.put(COLUMN_ORDERED_DT_TIME,orderedtime);
        cv.put(COLUMN_PO_REFNAME,po_ref_name);
        cv.put(COLUMN_PO_CREATED_DATE,po_created_date);
        cv.put(COLUMN_ITEM_CATEGORY,itemcategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY,itemsubcategory);
        cv.put(COLUMN_CURRENT_DT,cureent_date_time);
        cv.put(COLUMN_EXPECTED_DELIVERY,expected_delivery);
        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
        }
        return result;
    }
    public boolean updateOrderStatus(String orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase(); // Use writable database for update
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status); // Replace COLUMN_STATUS with your actual status column name

        // Update query
        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ORDERID + " = ?", new String[]{orderId});

        db.close(); // Close database connection
        return rowsAffected > 0; // Return true if at least one row is updated
    }


    public Cursor readDataByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_STATUS + " = ? " +
                " ORDER BY " + COLUMN_ITEM_CATEGORY + ", " + COLUMN_ITEM_SUB_CATEGORY;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }

    public void UpdateApprovedData(String orderid, String userid, String vanid,String productname,String reQty, String prdtid, String approvedid,String po_ref,String porefname,String pocreateddate,String outletid ,String status, String dttime){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ORDERID,orderid);
        cv.put(COLUMN_USERID,userid);
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_PRODUCTNAME,productname);
        cv.put(COLUMN_PRODUCTID,prdtid);
        cv.put(COLUMN_REQUESTEDQTY,reQty);
        cv.put(COLUMN_APPROVEDQTY,approvedid);
        cv.put(COLUMN_PO,po_ref);
        cv.put(COLUMN_PO_REFNAME,porefname);
        cv.put(COLUMN_PO_CREATED_DATE,pocreateddate);
        cv.put(COLUMN_OUTLETID,outletid);
        cv.put(COLUMN_STATUS,status);
        cv.put(COLUMN_APPROVED_DT_TIME,dttime);

        long result= db.update(TABLE_NAME,cv,COLUMN_ORDERID + "=?",new String[] {orderid});
        if(result==-1){
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
          //  Toast.makeText(context," Updated Successfully!",Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteOldRecords() {
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE datetime(" + COLUMN_ORDERED_DT_TIME +
                ") <= datetime('now', '-7 days')";

        SQLiteDatabase database = this.getReadableDatabase();
        database.execSQL(query);
    }

    public void updateOrderStatus() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update the status to 'REJECTED' and set comments to 'UNDELIVERED WHEN EXPECTED'
        String updateQuery = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_STATUS + " = 'REJECTED' " +
                " WHERE " + COLUMN_STATUS + " = 'PENDING FOR DELIVERY' " +
                " AND DATE(" + COLUMN_EXPECTED_DELIVERY + ") < DATE('now', '-1 day', 'localtime')";

        SQLiteStatement stmt = db.compileStatement(updateQuery);
        stmt.executeUpdateDelete();

        db.close();
    }
    public Cursor get1PO(String productid) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to retrieve the latest PO entry for the given productid
        String query = "SELECT " + COLUMN_PO + ", " + COLUMN_PO_REFNAME + ", " + COLUMN_PO_CREATED_DATE + " " +
                "FROM " + TABLE_NAME + " " +
                "WHERE " + COLUMN_PRODUCTID + " = ? " +
                "ORDER BY " + COLUMN_PO_CREATED_DATE + " DESC " +
                "LIMIT 1";

        Cursor cursor = null;

        if (db != null) {
            cursor = db.rawQuery(query, new String[]{productid});

            if (cursor != null && cursor.getCount() == 0) {
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                MatrixCursor matrixCursor = new MatrixCursor(
                        new String[]{COLUMN_PO, COLUMN_PO_REFNAME, COLUMN_PO_CREATED_DATE});
                matrixCursor.addRow(new Object[]{"NA", "NA", currentDate});
                cursor.close();
                cursor = matrixCursor;
            }
        }

        return cursor;
    }
    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_ITEM_SUB_CATEGORY;;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    public Cursor readonOrderid(String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?"+ " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_ITEM_SUB_CATEGORY;;
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return cursor;
    }
    public Cursor getPO(String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return cursor;
    }
    public boolean checkIFEXistsOrderid(String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return true;
    }
    public Cursor readonOrderidandproductid(String orderid,String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?" + " AND " + COLUMN_PRODUCTID + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{orderid,productid});
        }
        return cursor;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }

    public Cursor getMaxApprovedDateTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COLUMN_APPROVED_DT_TIME + ") AS max_date FROM " + TABLE_NAME;
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}

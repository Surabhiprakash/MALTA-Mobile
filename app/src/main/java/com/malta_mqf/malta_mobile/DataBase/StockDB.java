package com.malta_mqf.malta_mobile.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.VanStockDetails;
import com.malta_mqf.malta_mobile.Model.VanStockUnloadModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class StockDB extends SQLiteOpenHelper {

    public static final String COLUMN_VAN_ID = "vanId";
    public static final String COLUMN_TO_VAN_ID = "to_vanId";
    public static final String COLUMN_FROM_VAN_ID = "from_vanId";
    public static final String COLUMN_PRODUCTNAME = "ProductName";
    public static final String COLUMN_PRODUCTID = "productId";
    public static final String COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND = "Total_Available_Qty_On_Hand";
    public static final String COLUMN_ITEM_CODE = "item_code";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ITEM_CATEGORY = "item_category";
    public static final String COLUMN_ITEM_SUB_CATEGORY = "item_sub_category";
    public static final String COLUMN_UNLOAD_DATE = "unload_date";
    private static final String DATABASE_NAME = "stockdb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_approved";
    private static final String COLUMN_ID = "_id";
    private static final String TABLE_TRANSFER_HISTORY = "transferhistorytable";
    private static final String TABLE_RECIVE_HISTORY = "receivehistorytable";
    private final Context context;

    public StockDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_VAN_ID + " TEXT, " +
                        COLUMN_PRODUCTNAME + " TEXT, " +
                        COLUMN_PRODUCTID + " TEXT, " +
                        COLUMN_ITEM_CODE + " TEXT, " +
                        COLUMN_ITEM_CATEGORY + " TEXT, " +
                        COLUMN_ITEM_SUB_CATEGORY + " TEXT, " +
                        COLUMN_STATUS + " TEXT, " +
                        COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " INTEGER); ";

        db.execSQL(query);


        String CREATE_TRANSFER_HISTORY_TABLE = "CREATE TABLE " + TABLE_TRANSFER_HISTORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VAN_ID + " TEXT,"
                + COLUMN_TO_VAN_ID + " TEXT,"
                + COLUMN_PRODUCTNAME + " TEXT, "
                + COLUMN_PRODUCTID + " TEXT, "
                + COLUMN_ITEM_CATEGORY + " TEXT, "
                + COLUMN_ITEM_SUB_CATEGORY + " TEXT, "
                + COLUMN_ITEM_CODE + " TEXT, "
                + COLUMN_UNLOAD_DATE + " TEXT, "
                + COLUMN_STATUS + " TEXT, "
                + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " INTEGER)";

        db.execSQL(CREATE_TRANSFER_HISTORY_TABLE);

        String CREATE_RECEIVE_HISTORY_TABLE = "CREATE TABLE " + TABLE_RECIVE_HISTORY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_VAN_ID + " TEXT,"
                + COLUMN_FROM_VAN_ID + " TEXT,"
                + COLUMN_PRODUCTNAME + " TEXT, "
                + COLUMN_PRODUCTID + " TEXT, "
                + COLUMN_ITEM_CATEGORY + " TEXT, "
                + COLUMN_ITEM_SUB_CATEGORY + " TEXT, "
                + COLUMN_ITEM_CODE + " TEXT, "
                + COLUMN_UNLOAD_DATE + " TEXT, "
                + COLUMN_STATUS + " TEXT, "
                + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " INTEGER)";


        db.execSQL(CREATE_RECEIVE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSFER_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIVE_HISTORY);
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
    public void insertTransferHistory(String vanID, String to_van_id, String productName, String productId, String itemCode, String itemCategory, String itemSubcategory, int availableQty, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VAN_ID, vanID);
        values.put(COLUMN_TO_VAN_ID, to_van_id);
        values.put(COLUMN_PRODUCTNAME, productName);
        values.put(COLUMN_PRODUCTID, productId);
        values.put(COLUMN_ITEM_CATEGORY, itemCategory);
        values.put(COLUMN_ITEM_SUB_CATEGORY, itemSubcategory);
        values.put(COLUMN_ITEM_CODE, itemCode);
        values.put(COLUMN_UNLOAD_DATE, date);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, availableQty);

        db.insert(TABLE_TRANSFER_HISTORY, null, values);
        db.close();
    }

    public void insertReceiveHistory(String vanID, String from_vanid, String productName, String productId, String itemCode, String itemCategory, String itemSubcategory, int availableQty, String status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VAN_ID, vanID);
        values.put(COLUMN_FROM_VAN_ID, from_vanid);
        values.put(COLUMN_PRODUCTNAME, productName);
        values.put(COLUMN_PRODUCTID, productId);
        values.put(COLUMN_ITEM_CATEGORY, itemCategory);
        values.put(COLUMN_ITEM_SUB_CATEGORY, itemSubcategory);
        values.put(COLUMN_ITEM_CODE, itemCode);
        values.put(COLUMN_UNLOAD_DATE, date);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, availableQty);

        db.insert(TABLE_RECIVE_HISTORY, null, values);
        db.close();
    }

    public void stockaddApprovedDetails(String vanid, String prouctname, String prdtid, String itemcode, String itemcategory, String itemsubcategory, int avalqty, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VAN_ID, vanid);
        cv.put(COLUMN_PRODUCTNAME, prouctname);
        cv.put(COLUMN_PRODUCTID, prdtid);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_CATEGORY, itemcategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY, itemsubcategory);
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, avalqty);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
        }
    }

    public void stockUpdateApprovedData(String vanid, String productname, String prdtid, String itemcode, String itemcategory, String itemsubcategory, int avalqty, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?", new String[]{prdtid});
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VAN_ID, vanid);
        cv.put(COLUMN_PRODUCTNAME, productname);
        cv.put(COLUMN_PRODUCTID, prdtid);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_CATEGORY, itemcategory);
        cv.put(COLUMN_ITEM_SUB_CATEGORY, itemsubcategory);
        cv.put(COLUMN_STATUS, status);

        // Check if the product exists
        if (cursor.moveToFirst()) {
            // Product exists, get the current quantity
            @SuppressLint("Range") int existingQty = cursor.getInt(cursor.getColumnIndex(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));
            int newTotalQty = existingQty + avalqty; // Calculate the new total quantity
            cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, newTotalQty);

            // Perform the update
            long result = db.update(TABLE_NAME, cv, COLUMN_PRODUCTID + "=?", new String[]{prdtid});
            if (result <= 0) {
                // Handle failure (No rows were updated due to some error)
            } else {
                // Handle success (Rows were successfully updated)
            }
        } else {
            // Product does not exist, insert as new
            cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, avalqty);
            long result = db.insert(TABLE_NAME, null, cv);
            if (result == -1) {
                // Handle failure
            } else {
                // Handle success
            }
        }
        cursor.close(); // Always close the cursor
    }

    public void stockUpdateApproveditems(String vanid, String prouctname, String avalqty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_VAN_ID, vanid);
        cv.put(COLUMN_PRODUCTNAME, prouctname);

        cv.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, avalqty);
        long result = db.update(TABLE_NAME, cv, COLUMN_PRODUCTID + "=?", new String[]{prouctname});
        if (result == -1) {
            //Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(context," Updated Successfully!",Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor stockreadAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readonproductid(String productid) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTID + " = ?" + " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_ITEM_SUB_CATEGORY;
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return cursor;
    }

    public Cursor readonproductStatus(String status) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = ?";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{status});
        }
        return cursor;
    }

    public Cursor readonproductName(String productid) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PRODUCTNAME + " = ?";
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{productid});
        }
        return cursor;
    }

    public void updateAvailableQty(String productId, int newAvailableQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, newAvailableQty);

        // Updating row
        db.update(TABLE_NAME, values, COLUMN_PRODUCTID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public void updateAvailabletoQty(String itemName, int newAvailableQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, newAvailableQty);

        // Updating row
        db.update(TABLE_NAME, values, COLUMN_PRODUCTNAME + " = ?", new String[]{String.valueOf(itemName)});
        db.close();
    }

    public void insertNewProduct(String vanId, String itemName, String productId, String itemCode, String category, String subCategory, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VAN_ID, vanId);
        values.put(COLUMN_PRODUCTNAME, itemName);
        values.put(COLUMN_PRODUCTID, productId);
        values.put(COLUMN_ITEM_CODE, itemCode);
        values.put(COLUMN_ITEM_CATEGORY, category);
        values.put(COLUMN_ITEM_SUB_CATEGORY, subCategory);
        values.put(COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND, quantity);

        // Inserting a new row
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /*public Cursor readonOrderidandproductid(String orderid,String productid){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ORDERID + " = ?" + " AND " + COLUMN_PRODUCTID + " = ?";
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, new String[]{orderid,productid});
        }
        return cursor;
    }*/
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

    public void insertMultipleDetails(List<VanStockDetails> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Map to store totalQty for each orderid
            Map<String, Integer> orderQtyMap = new HashMap<>();

            for (VanStockDetails data : dataList) {


                // Call addDeliveredItemsTransaction with updated totalQty
                stockaddApprovedDetails(data.getVanId(), data.getItemName(), data.getItem_id(), data.getItemCode(), data.getItemcategory(), data.getItemsubcategory(), Integer.parseInt(data.getQuantities()), "STOCK SYNCED");

            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void stockdeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }


    public List<VanStockUnloadModel> getVanStockItems() {
        List<VanStockUnloadModel> vanStockList = new ArrayList<>();
        Set<String> uniqueItems = new HashSet<>(); // To store unique product IDs

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND + " > 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String productId = cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_PRODUCTID));

                // Check if the product ID is already added
                if (!uniqueItems.contains(productId)) {
                    VanStockUnloadModel item = new VanStockUnloadModel();
                    item.setVanId(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_VAN_ID)));
                    item.setProductName(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_PRODUCTNAME)));
                    item.setProductId(productId);
                    item.setItemCategory(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_ITEM_CATEGORY)));
                    item.setItemSubcategory(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_ITEM_SUB_CATEGORY)));
                    item.setItemCode(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_ITEM_CODE)));
                    item.setAvailableQty(Integer.parseInt(cursor.getString(cursor.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND))));

                    // Set current date and time as unload date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());
                    item.setUnloadDate(currentDateAndTime);

                    item.setStatus("UNLOADED");

                    vanStockList.add(item);
                    uniqueItems.add(productId); // Add to set to avoid duplicates
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return vanStockList;
    }

}



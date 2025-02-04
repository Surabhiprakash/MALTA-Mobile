package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.AllItemSellingPriceDetailsResponse;

import java.util.List;

public class SellingPriceOfItemBsdCustomerDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="SellingPriceDetails.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="Selling_price_of_item_bsd_customer";
    private static final String  COLUMN_NO="_no";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_CUSTOMER_NAME="CustomerName";
    public static final String COLUMN_CUSTOMER_CODE="CustomerCode";
    private static final String COLUMN_ITEM_CODE="ItemCode";
    private static final String COLUMN_ITEM_NAME="ItemName";
    public static final String COLUMN_SELLING_PRICE="SellingPrice";




    public SellingPriceOfItemBsdCustomerDB (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NO +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ID + " TEXT, " +
                        COLUMN_CUSTOMER_NAME + " TEXT, " +
                        COLUMN_CUSTOMER_CODE + " TEXT, " +
                        COLUMN_ITEM_CODE + " TEXT, " +
                        COLUMN_ITEM_NAME + " TEXT, " +
                        COLUMN_SELLING_PRICE + " TEXT  ) ; ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public void insertMultipleDetails(List<AllItemSellingPriceDetailsResponse> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            for (AllItemSellingPriceDetailsResponse data : dataList) {

                addDetails(db, data.getId(), data.getCustomerName(), data.getCustomerCode(), data.getItemCode(), data.getItemName(),data.getSellingPrice());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

  /*  private void addDetails(SQLiteDatabase db, String id, String customerName, String customerCode, String itemCode, String itemName, String count) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("customerName", customerName);
        values.put("customerCode", customerCode);
        values.put("itemCode", itemCode);
        values.put("itemName", itemName);
        values.put("count", count);
        db.insert("table_name", null, values);
    }*/

        public void addDetails(SQLiteDatabase db, String id, String customername, String customercode, String itemcode, String itemname, String sellingprice) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_CUSTOMER_NAME, customername);
        cv.put(COLUMN_CUSTOMER_CODE, customercode);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_NAME, itemname);
        cv.put(COLUMN_SELLING_PRICE, sellingprice);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            // Log failure or handle it according to your needs
        } else {
            // Optionally log success
        }
    }
    public void UpdateData(String id, String customername, String customercode, String itemcode, String itemname, String sellingprice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, id);
        cv.put(COLUMN_CUSTOMER_NAME, customername);
        cv.put(COLUMN_CUSTOMER_CODE, customercode);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_NAME, itemname);
        cv.put(COLUMN_SELLING_PRICE, sellingprice);


        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{id});
        if (result == -1) {
            //   Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
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

    public Cursor readDataByCustomerCode(String customerCode, String itemCode)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ? AND " + COLUMN_ITEM_CODE + " = ?";
            cursor = db.rawQuery(query, new String[]{customerCode, itemCode});
        }

        return cursor;
    }
    public Cursor readDataByITemCode( String itemCode)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_CODE + " = ?";
            cursor = db.rawQuery(query, new String[]{ itemCode});
        }

        return cursor;
    }
    public void sellingPricedeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }



}

package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;

import java.util.List;

public class ItemsByAgencyDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="ItemsByAgencyDB.db";
    private static final int DATABASE_VERSION=1;
    public static final String TABLE_NAME="my_items_by_agency";
    private static final String  COLUMN_NO="_no";
    public static final String COLUMN_ITEM_NAME="ItemName";
    public static final String COLUMN_ITEM_CODE="ItemCode";
    public static final String COLUMN_ITEM_ID="ItemId";
    public static final String COLUMN_ITEM_UOM="ItemUOM";
    public static final String COLUMN_ITEM_CATEGORY_ID="CategoryId";
    public static final String COLUMN_ITEM_AGENCY_CODE="AgencyCode";
    public static final String COLUMN_ITEM_AGENCY_ID="ItemsAgencyId";
    public static final String COLUMN_PRODUCT_DESCRIPTION="ProductDescription";
    public static final String COLUMN_CUSTOMER_CODE="customer_code";
    public static final String COLUMN_CUSTOMER_NAME="customer_name";
    public static final String COLUMN_SELLING_PRICE="selling_price";
    public static final String COLUMN_PURCHASE_PRICE="purchase_price";
    public static final String COLUMN_BARCODE="Barcode";
    public static final String COLUMN_PLUCODE="plucode";
    public static final String COLUMN_LEAD_TIME="Lead_time";
    public static final String COLUMN_ITEM_CATEGORY="Item_Category";
    public static final String COLUMN_SUB_CATEGORY="Item_Sub_Category";



    public ItemsByAgencyDB (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NO +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ITEM_NAME + " TEXT, " +
                        COLUMN_ITEM_CODE + " TEXT, " +
                        COLUMN_ITEM_ID + " TEXT, " +
                        COLUMN_ITEM_UOM + " TEXT, " +
                        COLUMN_ITEM_CATEGORY_ID + " TEXT, " +
                        COLUMN_ITEM_AGENCY_CODE + " TEXT, " +
                        COLUMN_ITEM_AGENCY_ID + " TEXT, " +
                        COLUMN_CUSTOMER_CODE + " TEXT, "+
                        COLUMN_CUSTOMER_NAME + " TEXT, "+
                        COLUMN_PURCHASE_PRICE + " TEXT, "+
                        COLUMN_SELLING_PRICE + " TEXT, "+
                        COLUMN_BARCODE + " TEXT, "+
                        COLUMN_LEAD_TIME + " TEXT, "+
                        COLUMN_PLUCODE + " TEXT, "+
                        COLUMN_ITEM_CATEGORY + " TEXT, "+
                        COLUMN_SUB_CATEGORY + " TEXT, "+
                        COLUMN_PRODUCT_DESCRIPTION + " TEXT ); " ;

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

   /* public void insertMultipleDetails(List<AllItemDetailResponseById> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            for (AllItemDetailResponseById data : dataList) {

               // addItemDetails(data.getItemName(), data.getItemCode(), data.getId(), data.getUom(), data.getItemCategoryId(),data.getAgencyCode(),data.getAgencyId(),data.getCustomerCode(),data.getCustomerName(),data.getSellingPrice(),data.getProductDescription());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }*/
    public void insertMultipleDetails(List<AllItemDetailResponseById> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (AllItemDetailResponseById data : dataList) {

                addItemDetails(db,data.getItemName(), data.getItemCode(), data.getId(), data.getUom(), data.getItemCategoryId(),data.getAgencyCode(),data.getAgencyId(),data.getCustomerCode().toLowerCase(),data.getCustomerName(),data.getPurchasePrice(),data.getSellingPrice(),data.getProductDescription(),data.getBarcode(),data.getPlu_code(),data.getLead_time(),data.getItemCategory(),data.getSubCategory());

                // Update progress after each insertion
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void addItemDetails( SQLiteDatabase db, String itemname,String itemcode,String itemId,String uom,String itemcatgid,String agencycode,String itemagencyId,String customercode,String customername,String pruchase_price,String selling_price,String productdecp,String barcode,String plu_code,String leadTime,String itemCategory,String itemSubCategory){
        //SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ITEM_NAME, itemname);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_ID, itemId);
        cv.put(COLUMN_ITEM_UOM, uom);
        cv.put(COLUMN_ITEM_CATEGORY_ID, itemcatgid);
        cv.put(COLUMN_ITEM_AGENCY_CODE, agencycode);
        cv.put(COLUMN_ITEM_AGENCY_ID, itemagencyId);
        cv.put(COLUMN_CUSTOMER_CODE,customercode);
        cv.put(COLUMN_CUSTOMER_NAME,customername);
        cv.put(COLUMN_PURCHASE_PRICE,pruchase_price);
        cv.put(COLUMN_SELLING_PRICE,selling_price);
        cv.put(COLUMN_PRODUCT_DESCRIPTION, productdecp);
        cv.put(COLUMN_BARCODE,barcode);
        cv.put(COLUMN_LEAD_TIME,leadTime);
        cv.put(COLUMN_PLUCODE,plu_code);
        cv.put(COLUMN_ITEM_CATEGORY,itemCategory);
        cv.put(COLUMN_SUB_CATEGORY,itemSubCategory);
        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
         //   Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
         //   Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
        }
    }
    public void UpdateItemData(String itemname,String itemcode,String itemId,String uom,String itemcatgid,String agencycode,String itemagencyId,String customercode,String customername,String selling_price,String productdecp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ITEM_NAME, itemname);
        cv.put(COLUMN_ITEM_CODE, itemcode);
        cv.put(COLUMN_ITEM_ID, itemId);
        cv.put(COLUMN_ITEM_UOM, uom);
        cv.put(COLUMN_ITEM_CATEGORY_ID, itemcatgid);
        cv.put(COLUMN_ITEM_AGENCY_CODE, agencycode);
        cv.put(COLUMN_ITEM_AGENCY_ID, itemagencyId);
        cv.put(COLUMN_CUSTOMER_CODE,customercode);
        cv.put(COLUMN_CUSTOMER_NAME,customername);
        cv.put(COLUMN_SELLING_PRICE,selling_price);
        cv.put(COLUMN_PRODUCT_DESCRIPTION, productdecp);

        long result = db.update(TABLE_NAME, cv, COLUMN_ITEM_ID + "=?", new String[]{itemId});
        if (result == -1) {
          //  Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
         //   Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
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

    public Cursor checkIfITemExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_AGENCY_CODE + " = ?"+ " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_SUB_CATEGORY;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id});
        }
        return cursor;
    }
    public Cursor checkIfITemExistsBsdCusomterCode(String id,String customercode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_AGENCY_CODE + " = ?" + " AND " + COLUMN_CUSTOMER_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id,customercode});
        }
        return cursor;
    }
    public Cursor checkIfItemExistsByCustomerCodeAndLeadTime(String id, String customerCode, String leadTime) {
        // Validate inputs to ensure they are not null
        if (id == null || customerCode == null || leadTime == null) {
            throw new IllegalArgumentException("id, customerCode, and leadTime must not be null");
        }

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ITEM_AGENCY_CODE + " = ?" +
                " AND LOWER(" + COLUMN_CUSTOMER_CODE + ") = LOWER(?)" +
                " AND " + COLUMN_LEAD_TIME + " = ?" +
                " ORDER BY " + COLUMN_ITEM_CATEGORY + ", " + COLUMN_SUB_CATEGORY;
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id, customerCode.toLowerCase(), leadTime});
        }
        return cursor;
    }


    public Cursor readProdcutDataByName(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_NAME + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
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

    public Cursor readDataByCustomerCodeprodId(String customerCode, String itemid)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ? AND " + COLUMN_ITEM_ID + " = ?" + " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_SUB_CATEGORY;
            cursor = db.rawQuery(query, new String[]{customerCode, itemid});
        }

        return cursor;
    }
    public Cursor readDataByCustomerCodes(String customerCode)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ?"+ " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_SUB_CATEGORY;
            cursor = db.rawQuery(query, new String[]{customerCode});
        }

        return cursor;
    }


    public Cursor readDataByCustomerCodeAndProdName(String customerCode, String itemName)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ? AND " + COLUMN_ITEM_NAME+ " = ?";
            cursor = db.rawQuery(query, new String[]{customerCode, itemName});
        }

        return cursor;
    }


    public Cursor readProdcutDataByproductId(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_ID + " = ?"+ " ORDER BY " + COLUMN_ITEM_CATEGORY + " ," + COLUMN_SUB_CATEGORY;;

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }
    public Cursor readDataByCustomerCodeandproID(String customerCode, String prodID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE LOWER(" + COLUMN_CUSTOMER_CODE + ") = LOWER(?) AND " + COLUMN_ITEM_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{customerCode.toLowerCase(), prodID});
        }

        return cursor;
    }

    public void itemsdeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }

    public Cursor getLeadTimes(){
        String query = "SELECT " + COLUMN_LEAD_TIME +" FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }


    public Cursor readProdcutDataByItemCode(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ITEM_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public Cursor readProdcutDataByproductIdAndCustomerCode(String customerCode,String itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ? AND " + COLUMN_ITEM_ID + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{customerCode, itemId});
        }
        return cursor;
    }
}

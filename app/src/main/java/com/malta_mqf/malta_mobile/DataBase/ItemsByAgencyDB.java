package com.malta_mqf.malta_mobile.DataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.AllItemDetailResponseById;
import com.malta_mqf.malta_mobile.Model.ListCustomerNonreturnableSkus;
import com.malta_mqf.malta_mobile.Model.OutletSKUs;

import java.util.ArrayList;
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

    public static final String TABLE_OUTLET_SKUS = "my_outlet_item";
    public static final String COLUMN_OUTLET_ID = "OUTLET_ID";
    public static final String COLUMN_OUTLET_ITEM_ID = "ITEM_ID";

    public static final String TABLE_NON_RETURNABLE_SKUS = "non_returnable_skus";
    public static final String COLUMN_NON_RETURNABLE_CUSTOMER_ID = "non_returnable_customer_id";
    public static final String COLUMN_NON_RETURNABLE_CUSTOMER_CODE = "non_returnable_customer_code";
    public static final String COLUMN_NON_RETURNABLE_ITEM_ID = "non_returnable_item_id";

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


        String createOutletItemTable = "CREATE TABLE IF NOT EXISTS " +
                TABLE_OUTLET_SKUS +
                " (" + COLUMN_OUTLET_ID + " TEXT, " +
                COLUMN_OUTLET_ITEM_ID + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_OUTLET_ID + ", "
                + COLUMN_OUTLET_ITEM_ID + ") );";

        String createNonReturnableSkusTable = "CREATE TABLE " +
                TABLE_NON_RETURNABLE_SKUS + " (" +
                COLUMN_NON_RETURNABLE_CUSTOMER_ID + " TEXT, " +
                COLUMN_NON_RETURNABLE_CUSTOMER_CODE + " TEXT, " +
                COLUMN_NON_RETURNABLE_ITEM_ID + " TEXT, " +
                "PRIMARY KEY (" + COLUMN_NON_RETURNABLE_CUSTOMER_ID + ", " +
                COLUMN_NON_RETURNABLE_ITEM_ID + "," + COLUMN_NON_RETURNABLE_CUSTOMER_CODE + " ) );";



        db.execSQL(query);
        db.execSQL(createOutletItemTable);
        db.execSQL(createNonReturnableSkusTable);
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
    public Cursor checkIfItemExistsByCustomerCodeAndLeadTime(
            String id, String customerCode, String outletId, String leadTime) {

        // Validate inputs
        if (id == null || customerCode == null || outletId == null || leadTime == null) {
            throw new IllegalArgumentException("id, customerCode, outletId, and leadTime must not be null");
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // Query with JOIN
        String query = "SELECT i.* FROM " + TABLE_NAME + " i" +
                " INNER JOIN " + TABLE_OUTLET_SKUS + " o ON i." + COLUMN_ITEM_ID + " = o." + COLUMN_OUTLET_ITEM_ID +
                " WHERE i." + COLUMN_ITEM_AGENCY_CODE + " = ?" +
                " AND LOWER(i." + COLUMN_CUSTOMER_CODE + ") = LOWER(?)" +
                " AND o." + COLUMN_OUTLET_ID + " = ?" +
                " AND i." + COLUMN_LEAD_TIME + " = ?" +
                " ORDER BY i." + COLUMN_ITEM_CATEGORY + ", i." + COLUMN_SUB_CATEGORY;

        System.out.println(query);
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id, customerCode.toLowerCase(), outletId, leadTime});
        }
        return cursor;
    }

    public Cursor readProductsByCustomerExcludingNonReturnable(String agencyCode, String customerCode, String leadTime , String noncustomerCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
//            String query = "SELECT i.* " +
//                    "FROM " + TABLE_NAME + " i " +
//                    "WHERE i.AgencyCode = ? " +
//                    "  AND LOWER(i.customer_code) = LOWER(?) " +
//                    "  AND i.Lead_time = ? " +
//                    "  AND NOT EXISTS (" +
//                    "      SELECT 1 " +
//                    "      FROM " + TABLE_NON_RETURNABLE_SKUS + " o " +
//                    "      WHERE o.non_returnable_customer_code = i.customer_code " +
//                    "        AND o.non_returnable_item_id = i.ItemId" +
//                    "  ) " +
//                    "ORDER BY i.Item_Category, i.Item_Sub_Category";


            String query = "SELECT DISTINCT i.* " +
                    "FROM " + TABLE_NAME + " i " +
                    "WHERE i." + COLUMN_ITEM_AGENCY_CODE + " = ? " +
                    "AND LOWER(i." + COLUMN_CUSTOMER_CODE + ") = LOWER(?) " +
                    "AND i."+ COLUMN_LEAD_TIME+" = ?"+
                    "AND i." + COLUMN_ITEM_ID + " NOT IN ( " +
                    "    SELECT o." + COLUMN_NON_RETURNABLE_ITEM_ID + " " +
                    "    FROM " + TABLE_NON_RETURNABLE_SKUS + " o " +
                    "    WHERE LOWER(o." + COLUMN_NON_RETURNABLE_CUSTOMER_CODE + ") = LOWER(?) " +
                    ")";
            System.out.println("non_returnale query"+query);
            cursor = db.rawQuery(query, new String[]{agencyCode, customerCode, leadTime ,noncustomerCode});
        }
        System.out.println("the query output"+cursor);
        return cursor;
    }
    public Cursor readProductsByCustomerExcludingNonReturnable(String customerCode, String itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT i.* " +
                    "FROM " + TABLE_NAME + " i " +
                    "WHERE LOWER(i.customer_code) = LOWER(?) " +
                    "  AND i.ItemId = ? " +
                    "  AND NOT EXISTS (" +
                    "      SELECT 1 " +
                    "      FROM " + TABLE_NON_RETURNABLE_SKUS + " o " +
                    "      WHERE LOWER(o.non_returnable_customer_code) = LOWER(i.customer_code) " +
                    "        AND o.non_returnable_item_id = i.ItemId" +
                    "  ) " +
                    "ORDER BY i.Item_Category, i.Item_Sub_Category";

            cursor = db.rawQuery(query, new String[]{customerCode, itemId});
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
    public Cursor readDataByCustomerCodes(String customerCode, String outletId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (customerCode == null || outletId == null) {
            throw new IllegalArgumentException("customerCode and outletId must not be null");
        }

        if (db != null) {
            String query = "SELECT i.* FROM " + TABLE_NAME + " i " +
                    "INNER JOIN " + TABLE_OUTLET_SKUS + " o " +
                    "ON i." + COLUMN_ITEM_ID + " = o." + COLUMN_OUTLET_ITEM_ID + " " +
                    "WHERE LOWER(i." + COLUMN_CUSTOMER_CODE + ") = LOWER(?) " +
                    "AND o." + COLUMN_OUTLET_ID + " = ? " +
                    "ORDER BY i." + COLUMN_ITEM_CATEGORY + ", i." + COLUMN_SUB_CATEGORY;

            cursor = db.rawQuery(query, new String[]{customerCode, outletId});
        }

        return cursor;
    }



    public Cursor readDataByCustomerCodeAndProdName(String customerCode, String itemName)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_CUSTOMER_CODE + " = ? AND " + COLUMN_ITEM_NAME+ " = ?";
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

    public void insertMultipleOutletSkus(List<OutletSKUs> outletSKUList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_OUTLET_SKUS + " (" +
                COLUMN_OUTLET_ID + ", " + COLUMN_OUTLET_ITEM_ID + ") VALUES (?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);

        db.beginTransaction();
        try {
            for (OutletSKUs sku : outletSKUList) {
                String outletId = sku.getOutletId();
                String itemIds = sku.getItemId(); // This is comma-separated
                String[] itemIdArray = itemIds.split(","); // Split by comma

                for (String itemId : itemIdArray) {
                    stmt.clearBindings();
                    stmt.bindString(1, outletId.trim()); // Remove extra spaces
                    stmt.bindString(2, itemId.trim());
                    stmt.executeInsert();
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }



    public void deleteAllOutletSkus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OUTLET_SKUS, null, null);
        db.close();
    }


    public void insertMultipleNonReturnableSkus(List<ListCustomerNonreturnableSkus> nonReturnableSKUList) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NON_RETURNABLE_SKUS + " (" +
                COLUMN_NON_RETURNABLE_CUSTOMER_ID + ", " +
                COLUMN_NON_RETURNABLE_CUSTOMER_CODE + ", " +
                COLUMN_NON_RETURNABLE_ITEM_ID + ") VALUES (?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);

        db.beginTransaction();
        try {
            for (ListCustomerNonreturnableSkus sku : nonReturnableSKUList) {
                String customerId = sku.getCustomerId();
                String customerCode = sku.getCustomer_code(); // single code
                String itemIds = sku.getItemId(); // comma-separated IDs
                String[] itemIdArray = itemIds.split(",");

                for (String itemId : itemIdArray) {
                    stmt.clearBindings();
                    stmt.bindString(1, customerId.trim());
                    stmt.bindString(2, customerCode.trim());
                    stmt.bindString(3, itemId.trim());
                    stmt.executeInsert();
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }



    public void deleteAllNonReturnableSkus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NON_RETURNABLE_SKUS, null, null);
        db.close();
    }


    public Cursor checkItemAssociatedWithOutlet(String outletId, String itemCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isAssociated = false;

        String query = "SELECT DISTINCT i.ItemCode " +
                "FROM my_items_by_agency i " +
                "JOIN my_outlet_item o ON i.ItemId = o.ITEM_ID " +
                "WHERE o.OUTLET_ID = ? AND i.ItemCode = ?";

        Log.d("checkItemAssociation", "Query: " + query + " | outletId=" + outletId + " | itemCode=" + itemCode);

        // Execute query safely
        return db.rawQuery(query, new String[]{outletId, itemCode});
    }

    @SuppressLint("Range")
    public  List<String> outassosiatedagencies(String outletID) {

        List<String> agencyCodes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT i.AgencyCode " +
                "FROM my_items_by_agency i " +
                "JOIN my_outlet_item o ON i.ItemId = o.ITEM_ID " +
                "WHERE o.OUTLET_ID = ?";

        Cursor cursor = db.rawQuery(query, new String[]{outletID});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                agencyCodes.add(cursor.getString(cursor.getColumnIndex("AgencyCode")));
            }
            cursor.close();
        }

        return agencyCodes;
    }


    @SuppressLint("Range")
    public List<String> getItemNamesByIds(List<String> invalidItems) {
        List<String> itemNames = new ArrayList<>();

        if (invalidItems == null || invalidItems.isEmpty()) {
            return itemNames;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        // Build placeholders (?, ?, ?, ...)
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < invalidItems.size(); i++) {
            placeholders.append("?");
            if (i < invalidItems.size() - 1) placeholders.append(",");
        }

        // Correct query: get ItemName by matching ItemId in list
        String query = "SELECT DISTINCT i.ItemName " +
                "FROM my_items_by_agency i " +
                "WHERE i.ItemId IN (" + placeholders.toString() + ")";

        // Convert list to String array
        String[] args = invalidItems.toArray(new String[0]);

        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex("ItemName");
            while (cursor.moveToNext()) {
                itemNames.add(cursor.getString(nameIndex));
            }
            cursor.close();
        }

        return itemNames;
    }
}

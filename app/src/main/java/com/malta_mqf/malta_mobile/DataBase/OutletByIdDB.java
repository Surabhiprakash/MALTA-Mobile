package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.OutletsByIdResponse;

import java.util.ArrayList;
import java.util.List;

public class OutletByIdDB extends SQLiteOpenHelper {
    public static final String COLUMN_OUTLET_ADDRESS = "OutletAddress";
    public static final String COLUMN_OUTLET_DISTRICT = "District";
    public static final String COLUMN_OUTLET_CONTACT_PERSON = "ContactPerson";
    public static final String COLUMN_OUTLET_MOBILE_NUMBER = "MobileNumber";
    public static final String COLUMN_OUTLET_EMAIL = "Email";
    public static final String COLUMN_OUTLET_NAME = "OutletName";
    public static final String COLUMN_OUTLET_ID = "OutletId";
    public static final String COLUMN_OUTLET_CODE = "OutletCode";
    public static final String COLUMN_OUTLET_CUSTOMER_CODE = "CustomerId";
    private static final String DATABASE_NAME = "OutletById.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_outlet_by_id";
    private static final String COLUMN_NO = "_no";
    private static final String COLUMN_ROUTE_ID = "RouteId";
    private static final String COLUMN_OUTLET_ROUTE_NAME = "RouteName";
    private static final String COLUMN_VECHILE_NUMBER = "VechileNumber";
    private static final String COLUMN_VAN_ID = "VanId";
    private Context context;

    public OutletByIdDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ROUTE_ID + " TEXT, " +
                        COLUMN_OUTLET_ROUTE_NAME + " TEXT, " +
                        COLUMN_VECHILE_NUMBER + " TEXT, " +
                        COLUMN_VAN_ID + " TEXT, " +
                        COLUMN_OUTLET_EMAIL + " TEXT, " +
                        COLUMN_OUTLET_ADDRESS + " TEXT, " +
                        COLUMN_OUTLET_DISTRICT + " TEXT, " +
                        COLUMN_OUTLET_CONTACT_PERSON + " TEXT, " +
                        COLUMN_OUTLET_MOBILE_NUMBER + " TEXT, " +
                        COLUMN_OUTLET_NAME + " TEXT, " +
                        COLUMN_OUTLET_ID + " TEXT, " +
                        COLUMN_OUTLET_CUSTOMER_CODE + " TEXT, " +
                        COLUMN_OUTLET_CODE + " TEXT ); ";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addoutletDetails(SQLiteDatabase db, String routeid, String routeName, String vehicleno, String vanid, String email, String address, String district, String contperson, String mobile, String outletname, String outid, String customerid, String outletcode) {
        // SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ROUTE_ID, routeid);
        cv.put(COLUMN_OUTLET_ROUTE_NAME, routeName);
        cv.put(COLUMN_VECHILE_NUMBER, vehicleno);
        cv.put(COLUMN_VAN_ID, vanid);
        cv.put(COLUMN_OUTLET_EMAIL, email);
        cv.put(COLUMN_OUTLET_ADDRESS, address);
        cv.put(COLUMN_OUTLET_DISTRICT, district);

        cv.put(COLUMN_OUTLET_CONTACT_PERSON, contperson);
        cv.put(COLUMN_OUTLET_MOBILE_NUMBER, mobile);
        cv.put(COLUMN_OUTLET_NAME, outletname);
        cv.put(COLUMN_OUTLET_ID, outid);
        cv.put(COLUMN_OUTLET_CUSTOMER_CODE, customerid);
        cv.put(COLUMN_OUTLET_CODE, outletcode);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            // Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateoutletData(String routeid, String routeName, String vehicleno, String vanid, String email, String address, String district, String contperson, String mobile, String outletname, String outid, String customerid, String outletcode) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ROUTE_ID, routeid);
        cv.put(COLUMN_OUTLET_ROUTE_NAME, routeName);
        cv.put(COLUMN_VECHILE_NUMBER, vehicleno);
        cv.put(COLUMN_VAN_ID, vanid);
        cv.put(COLUMN_OUTLET_EMAIL, email);
        cv.put(COLUMN_OUTLET_ADDRESS, address);
        cv.put(COLUMN_OUTLET_DISTRICT, district);
        cv.put(COLUMN_OUTLET_CONTACT_PERSON, contperson);
        cv.put(COLUMN_OUTLET_MOBILE_NUMBER, mobile);
        cv.put(COLUMN_OUTLET_NAME, outletname);
        cv.put(COLUMN_OUTLET_ID, outid);
        cv.put(COLUMN_OUTLET_CUSTOMER_CODE, customerid);
        cv.put(COLUMN_OUTLET_CODE, outletcode);

        long result = db.update(TABLE_NAME, cv, COLUMN_OUTLET_ID + "=?", new String[]{outid});
        if (result == -1) {
            // Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getOutletNameById(String outletid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_OUTLET_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_ID + " = ?", new String[]{outletid});
    }

    public String getOutletNameById2(String outletId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Correct SQL Query with proper concatenation
        String query = "SELECT " + COLUMN_OUTLET_NAME + " FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{outletId});
        String outletName = null;

        if (cursor.moveToFirst()) {
            outletName = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return outletName;
    }

    public Cursor checkIfOutletExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_CUSTOMER_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{id});
        }
        return cursor;
    }

    public Cursor readOutletByName(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_NAME + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public Cursor readOutletByOutletCode(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public Cursor readOutletByNameandCustomerCode(String outletName, String customerCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_NAME + " = ? AND " + COLUMN_OUTLET_CUSTOMER_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{outletName, customerCode});
        }
        return cursor;
    }


    public Cursor readOutletByID(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_OUTLET_ID + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }


    public void insertMultipleDetails(List<OutletsByIdResponse> dataList) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {

            for (OutletsByIdResponse data : dataList) {

                addoutletDetails(db, data.getRouteId(),
                        data.getRouteName(),
                        data.getVehicleNo(),
                        data.getId(),
                        data.getEmailid(),
                        data.getAddress(),
                        data.getDistrict(),
                        data.getContactPerson(),
                        data.getMobileno(),
                        data.getOutletName(),
                        data.getOutletId(),
                        data.getCustomerCode().toLowerCase(),
                        data.getOutletCode());


            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void outletdeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }

    public List<String> getAllUniqueOutletIds() {
        List<String> uniqueOutletIds = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_OUTLET_ID + " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String outletId = cursor.getString(0);
            uniqueOutletIds.add(outletId);
        }

        cursor.close();
        db.close();

        return uniqueOutletIds;
    }
}

package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AllAgencyDetailsDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="AllAgencyDetails.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="my_all_agency";
    private static final String  COLUMN_NO="_no";

    public static final String COLUMN_AGENCY_NAME="AgencyName";

    public static final String COLUMN_AGENCY_ID="AgencyId";
    public static final String COLUMN_AGENCY_CODE="AgencyCode";

    public  AllAgencyDetailsDB (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NO +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_AGENCY_CODE + " TEXT, " +
                        COLUMN_AGENCY_NAME + " TEXT, " +
                        COLUMN_AGENCY_ID+ " TEXT  ) ; " ;

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addAgencyDetails(String agencycode,String agencyName,String agencyId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_AGENCY_CODE, agencycode);
        cv.put(COLUMN_AGENCY_NAME, agencyName);
        cv.put(COLUMN_AGENCY_ID, agencyId);



        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
            // Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            //  Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
        }
    }
    public void UpdateAgencyData(String agencycode,String agencyName,String agencyId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_AGENCY_CODE, agencycode);
        cv.put(COLUMN_AGENCY_NAME, agencyName);
        cv.put(COLUMN_AGENCY_ID, agencyId);

        long result = db.update(TABLE_NAME, cv, COLUMN_AGENCY_ID + "=?", new String[]{agencyId});
        if (result == -1) {
            // Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        } else {
            //  Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }


    public Cursor readAllAgencyData () {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readAgencyDataByName(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_AGENCY_NAME + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public Cursor readAgencyDataByagencyID(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_AGENCY_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }
    public void agencydeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }



    public String getAgencyNameByAgencyCode(String agencyCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String agencyName = null; // Variable to store the retrieved agency name

        try {
            // Query to select the agency name where the agency code matches
            String query = "SELECT " + COLUMN_AGENCY_NAME + " FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_AGENCY_CODE + " = ?";

            // Execute the query with parameter substitution
            cursor = db.rawQuery(query, new String[]{agencyCode});

            // Check if data is found
            if (cursor != null && cursor.moveToFirst()) {
                agencyName = cursor.getString(0); // Get the first column (agency name)
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor to prevent memory leaks
            }
            if (db != null) {
                db.close(); // Close the database connection
            }
        }

        return agencyName; // Return the retrieved agency name (or null if not found)
    }
}


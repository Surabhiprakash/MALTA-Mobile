package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.malta_mqf.malta_mobile.Model.CodesWithAgency;

import java.util.List;

public class UserDetailsDb extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="UserDetails.db";
    private static final String TABLE_NAME_CODES_WITH_AGENCY="my_codes_with_agency";
    public static final String CODES_OF_THE_DAY="security_codes";
    public static final String AGENCY_CODES="agency_codes";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="my_users";
    private static final String  COLUMN_ID="_id";
    public static final String COLUMN_USERID="UserId";
    public static final String COLUMN_CATEGORY="Category";
    public static final String COLUMN_VAN_ID="vanId";
    public static final String COLUMN_ROUTE="Route";
    public static final String COLUMN_NAME="Name";
    public static final String COLUMN_EMAIL="Email";
    public static final String COLUMN_MOBILE_NUM="Mobile";
    public static final String COLUMN_ROLE="Role";
    public static final String COLUMN_VEHICLE_NUM="Vehicleno";
    public static final String COLUMN_EMP_CODE="EmpCode";
    public static final String LOGIN_DATE_TIME="logindatetime";
    public static final String INVOICE_NUMBER_UPDATING="invoice_number_updating";//invoice number retrive purpose
    public static final String RETURN_INVOICE_NUMBER_UPDATING="return_invoice_number_updating";//return number retrive  purpose

    public static final String INVOICE_NO_PATTERN="Invoice_Pattern";
    public static final String CREDIT_NO_PATTERN="Credit_Pattern";
    public static final String SALES_YTD="Sales_YTD";
    public static final String SALES_MTD="Sales_MTD";

    public UserDetailsDb (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME + " TEXT, " +
                        COLUMN_USERID + " TEXT, " +
                        COLUMN_CATEGORY + " TEXT, " +
                        COLUMN_VAN_ID + " TEXT, " +
                        COLUMN_ROUTE+ " TEXT, " +
                        COLUMN_EMAIL + " TEXT, " +
                        COLUMN_MOBILE_NUM + " TEXT, " +
                        COLUMN_ROLE + " TEXT, " +
                        COLUMN_VEHICLE_NUM + " TEXT, " +
                        LOGIN_DATE_TIME + " TEXT," +
                        INVOICE_NUMBER_UPDATING + " TEXT, "+
                        RETURN_INVOICE_NUMBER_UPDATING + " TEXT,"+
                        SALES_MTD + " TEXT,"+
                        SALES_YTD + " TEXT,"+
                        COLUMN_EMP_CODE+ " TEXT ); " ;

        db.execSQL(query);

        String code_table=  "CREATE TABLE " + TABLE_NAME_CODES_WITH_AGENCY + " (" + COLUMN_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AGENCY_CODES + " TEXT, " +
                CODES_OF_THE_DAY + " TEXT ); " ;
       db.execSQL(code_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addDetails(String name,String userid,String category,String vanid,String route,String email,String mobile,String role,String vehilceno,String empcode,String logindatetime,String invoicenumber,String returninvoicenumber){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_NAME,name);
        cv.put(COLUMN_USERID,userid);
        cv.put(COLUMN_CATEGORY,category);
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_ROUTE,route);
        cv.put(COLUMN_EMAIL,email);
        cv.put(COLUMN_MOBILE_NUM,mobile);
        cv.put(COLUMN_ROLE,role);
        cv.put(COLUMN_VEHICLE_NUM,vehilceno);
        cv.put(COLUMN_EMP_CODE,empcode);
        cv.put(LOGIN_DATE_TIME,logindatetime);
        cv.put(INVOICE_NUMBER_UPDATING,invoicenumber);
        cv.put(RETURN_INVOICE_NUMBER_UPDATING,returninvoicenumber);
        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
        }
    }
    public void UpdateData(String id,String name,String userid,String category,String vanid,String route,String email,String mobile,String role,String vehilceno,String empcode,String logindatetime){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ID,id);
        cv.put(COLUMN_NAME,name);
        cv.put(COLUMN_USERID,userid);
        cv.put(COLUMN_CATEGORY,category);
        cv.put(COLUMN_VAN_ID,vanid);
        cv.put(COLUMN_ROUTE,route);
        cv.put(COLUMN_EMAIL,email);
        cv.put(COLUMN_MOBILE_NUM,mobile);
        cv.put(COLUMN_ROLE,role);
        cv.put(COLUMN_VEHICLE_NUM,vehilceno);
        cv.put(COLUMN_EMP_CODE,empcode);
        cv.put(LOGIN_DATE_TIME,logindatetime);
       // cv.put(INVOICE_NUMBER_UPDATING,invoicenumber);
        //cv.put(RETURN_INVOICE_NUMBER_UPDATING,returninvoicenumber);
        long result= db.update(TABLE_NAME,cv,COLUMN_ID + "=?",new String[] {id});
        if(result==-1){
            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context," Updated Successfully!",Toast.LENGTH_SHORT).show();
        }
    }
    public void updateLastInvoiceNumber(String newInvoiceNumber, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Use the correct column name
        values.put(INVOICE_NUMBER_UPDATING, newInvoiceNumber);

        // Update the row where the column "id" matches the provided id
        long result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        // Check if the update was successful
        if (result == -1) {
            // Handle failure
            Log.d("DB", "Update failed");
        } else {
            // Handle success
            Log.d("DB", "Update successful");
        }
    }
    public void updateLastRetturnInvoiceNumber(String newInvoiceNumber, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Use the correct column name
        values.put(RETURN_INVOICE_NUMBER_UPDATING, newInvoiceNumber);

        // Update the row where the column "id" matches the provided id
        long result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

        // Check if the update was successful
        if (result == -1) {
            // Handle failure
            Log.d("DB", "Update failed");
        } else {
            // Handle success
            Log.d("DB", "Update successful");
        }
    }
    public void updateLastApprovedDate(String id, String newDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(LOGIN_DATE_TIME, newDate);

        // Update the specific row based on the ID
        long result = db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[] {id});
        if (result == -1) {
            // Toast.makeText(context, "Failed to update the last approved date!", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(context, "Last approved date updated successfully!", Toast.LENGTH_SHORT).show();
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

    public void updateSalesYtdAndSalesMtd(String salesYtd, String salesMtd) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SALES_YTD, salesYtd);
        cv.put(SALES_MTD, salesMtd);

        // This updates ALL rows in the table
        db.update(TABLE_NAME, cv, null, null);
    }

    public Cursor readSalesYtdAndSalesMtd() {
        String query = "SELECT " + SALES_YTD + ", " + SALES_MTD + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public String getVanId() {
        String vanId = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Fetch only one row with van_id
            cursor = db.query(TABLE_NAME, new String[]{COLUMN_VAN_ID}, null, null, null, null, null, "1");

            if (cursor != null && cursor.moveToFirst()) {
                vanId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VAN_ID));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Optional: Log this using your logger
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return vanId;
    }

    public void insertCodesOfTheDay(List<CodesWithAgency> codes) {
        System.out.println("codes: " + codes);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String query = "INSERT INTO " + TABLE_NAME_CODES_WITH_AGENCY + " (" + CODES_OF_THE_DAY + ", " + AGENCY_CODES + ") VALUES (?, ?)";
            SQLiteStatement statement = db.compileStatement(query);

            for (CodesWithAgency item : codes) {
                statement.clearBindings();
                statement.bindString(1, item.getSecretCode());  // Assuming getter method
                statement.bindString(2, item.getAgencyCode());     // Assuming getter method
                statement.executeInsert();
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean isCodeValid(String enteredCode, String agencyCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + TABLE_NAME_CODES_WITH_AGENCY +
                        " WHERE LOWER(" + CODES_OF_THE_DAY + ") = LOWER(?) AND " + AGENCY_CODES + " = ? LIMIT 1",
                new String[]{enteredCode, agencyCode}
        );

        boolean isValid = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        return isValid;
    }
}

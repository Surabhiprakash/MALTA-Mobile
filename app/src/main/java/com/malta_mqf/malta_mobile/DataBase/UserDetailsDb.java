package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class UserDetailsDb extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="UserDetails.db";
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
                        COLUMN_EMP_CODE+ " TEXT ); " ;

        db.execSQL(query);
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

}

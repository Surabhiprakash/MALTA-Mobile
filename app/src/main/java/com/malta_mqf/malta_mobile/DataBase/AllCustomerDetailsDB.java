package com.malta_mqf.malta_mobile.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AllCustomerDetailsDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME="AllCustomerDetails.db";
    private static final int DATABASE_VERSION=1;
    private static final String TABLE_NAME="my_all_customers";
    private static final String  COLUMN_NO="_no";
    public static final String COLUMN_ADDRESS="Address";
    public static final String COLUMN_CUSTOMER_CODE="CustomerCode";
    public static final String COLUMN_TRN="Trn_Number";
    public static final String COLUMN_CONTACT_PERSON="ContactPerson";
    public static final String COLUMN_MOBILE_NUM="mobile";
    public static final String COLUMN_CREDIT_PERIOD="CreditPeriod";
    public static final String COLUMN_CUSTOMER_NAME="CustomerName";
    public static final String COLUMN_CUSTOMER_TYPE="CustomerType";
    public static final String COLUMN_CREDIT_LIMIT="CreditLimit";
    public static final String COLUMN_REBATE="Rebate";
    public static final String COLUMN_CUSTOMER_ID="CustomerId";


    public AllCustomerDetailsDB (@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query=
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NO +  " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ADDRESS + " TEXT, " +
                        COLUMN_CUSTOMER_CODE + " TEXT, " +
                        COLUMN_CONTACT_PERSON + " TEXT, " +
                        COLUMN_TRN + " TEXT, " +
                        COLUMN_MOBILE_NUM + " TEXT, " +
                        COLUMN_CREDIT_PERIOD + " TEXT, " +
                        COLUMN_CUSTOMER_NAME + " TEXT, " +
                        COLUMN_CUSTOMER_TYPE + " TEXT, " +
                        COLUMN_CREDIT_LIMIT+ " TEXT ," +
                        COLUMN_REBATE+ " TEXT ," +
                        COLUMN_CUSTOMER_ID+ " TEXT  ) ; " ;

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addDetails(String address,String customercode,String contactperson,String mobile,String creditperiod,String customername,String customertype,String creditlimit,String rebate,String customerid,String trn){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(COLUMN_ADDRESS,address);
        cv.put(COLUMN_CUSTOMER_CODE,customercode);
        cv.put(COLUMN_CONTACT_PERSON,contactperson);
        cv.put(COLUMN_MOBILE_NUM,mobile);
        cv.put(COLUMN_CREDIT_PERIOD,creditperiod);
        cv.put(COLUMN_CUSTOMER_NAME,customername);
        cv.put(COLUMN_CUSTOMER_TYPE,customertype);
        cv.put(COLUMN_CREDIT_LIMIT,creditlimit);
        cv.put(COLUMN_REBATE,rebate);
        cv.put(COLUMN_CUSTOMER_ID,customerid);
        cv.put(COLUMN_TRN,trn);
     ;


        long result= db.insert(TABLE_NAME,null,cv);
        if(result==-1){
          //  Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
        }else{
          //  Toast.makeText(context,"Success!",Toast.LENGTH_SHORT).show();
        }
    }
    public void UpdateData(String address, String customercode, String contactperson, String mobile, String creditperiod, String customername, String customertype, String creditlimit,String rebate, String customerid,String trn) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_CUSTOMER_CODE, customercode);
        cv.put(COLUMN_CONTACT_PERSON, contactperson);
        cv.put(COLUMN_MOBILE_NUM, mobile);
        cv.put(COLUMN_CREDIT_PERIOD, creditperiod);
        cv.put(COLUMN_CUSTOMER_NAME, customername);
        cv.put(COLUMN_CUSTOMER_TYPE, customertype);
        cv.put(COLUMN_CREDIT_LIMIT, creditlimit);
        cv.put(COLUMN_REBATE, rebate);
        cv.put(COLUMN_CUSTOMER_ID, customerid);
        cv.put(COLUMN_TRN,trn);

        long result = db.update(TABLE_NAME, cv, COLUMN_CUSTOMER_ID + "=?", new String[]{customerid});
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

    public Cursor readDataByName(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_NAME + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public Cursor getCustomerDetailsById(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }

    public void customerdeleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        // Reset the auto-increment counter for the table
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE_NAME + "'");
        db.close();
    }

    public Cursor readDataByCustomerCode(String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CUSTOMER_CODE + " = ?";

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, new String[]{value});
        }
        return cursor;
    }
}

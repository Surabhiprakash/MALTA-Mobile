package com.malta_mqf.malta_mobile.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DummyDb extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    // Table Creation Statements
    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE);";

    private static final String CREATE_ORDERS_TABLE =
            "CREATE TABLE orders (" +
                    "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "order_date TEXT NOT NULL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id));";

    // Constructor
    public DummyDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute SQL statements to create tables
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS orders");
        db.execSQL("DROP TABLE IF EXISTS users");
        // Create tables again
        onCreate(db);
    }
}


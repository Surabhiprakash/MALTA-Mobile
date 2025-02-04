package com.malta_mqf.malta_mobile.DataBase;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DataBaseManager {
    private static DataBaseManager instance;
    private final SellingPriceOfItemBsdCustomerDB dbHelper;
    private final AtomicInteger openCounter = new AtomicInteger();
    private SQLiteDatabase database;

    private DataBaseManager(SellingPriceOfItemBsdCustomerDB dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static synchronized void initializeInstance(SellingPriceOfItemBsdCustomerDB dbHelper) {
        if (instance == null) {
            instance = new DataBaseManager(dbHelper);
        }
    }

    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DataBaseManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if (openCounter.incrementAndGet() == 1) {
            // Opening new database connection
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized void closeDatabase() {
        if (openCounter.decrementAndGet() == 0) {
            // Closing the database connection
            if (database != null && database.isOpen()) {
                database.close();
            }
        }
    }
}


package com.malta_mqf.malta_mobile.Utilities;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatabaseUtils {

    public static void copyDatabaseToExternalStorage(Context context, String dbName) {
        // Path to the database file in internal storage
        File dbFile = context.getDatabasePath(dbName); // Replace with your database name

        // Path to the external storage where the DB file will be copied
        File externalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dbName);

        try (FileInputStream fis = new FileInputStream(dbFile);
             FileOutputStream fos = new FileOutputStream(externalFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            Log.d("DatabaseUtils", "Database copied successfully to: " + externalFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("DatabaseUtils", "Error copying database: " + e.getMessage());
        }
    }
}
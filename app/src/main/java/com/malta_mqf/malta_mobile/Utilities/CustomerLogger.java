package com.malta_mqf.malta_mobile.Utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerLogger {

    private static final String LOG_FILE_NAME = "app_log.txt";
    private static File logFile;
    private static Context appContext;

    // Initialize log file in the Downloads directory
    public static void initialize(Context context) {
        appContext = context;
        if (isExternalStorageWritable()) {
            logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
            Log.i("CustomLogger", "Log file path: " + logFile.getAbsolutePath());
        }

        // Set custom UncaughtExceptionHandler for catching unhandled exceptions
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logException(throwable);
        });
    }


    // Check if external storage is writable
    private static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Log message to both Logcat and file
    public static void logToFile(String level, String tag, String message) {
        LogToFile(level, tag, message);
    }

    private static void LogToFile(String level, String tag, String message) {
        if (logFile != null) {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                writer.append(timestamp)
                        .append(" ")
                        .append(level)
                        .append("/")
                        .append(tag)
                        .append(": ")
                        .append(message)
                        .append("\n");
            } catch (IOException e) {
                Log.e("CustomLogger", "Failed to log to file", e);
            }
        }
    }

    // Log uncaught exceptions to the file
    private static void logException(Throwable throwable) {
        if (logFile != null) {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                writer.append(timestamp).append(" ERROR/UncaughtException: ").append(throwable.toString()).append("\n");
                for (StackTraceElement element : throwable.getStackTrace()) {
                    writer.append("\tat ").append(element.toString()).append("\n");
                }
            } catch (IOException e) {
                Log.e("CustomLogger", "Failed to log exception to file", e);
            }
        }
    }

    // Extend Android Log methods to also log to file automatically
    public static void d(String tag, String message) {
        Log.d(tag, message);
        logToFile("DEBUG", tag, message);
    }

    public static void i(String tag, String message) {
        if (message != null) {
            Log.i(tag, message);
            logToFile("tag", tag, message);
        } else {
            Log.i(tag, "No message available");
        }


    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
        logToFile("ERROR", tag, message);
    }

    public static void w(String tag, String message) {
        Log.w(tag, message);
        logToFile("WARN", tag, message);
    }
}


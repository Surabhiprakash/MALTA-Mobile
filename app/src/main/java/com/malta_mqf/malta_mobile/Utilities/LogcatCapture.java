package com.malta_mqf.malta_mobile.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LogcatCapture {

    private static final long MAX_LOG_SIZE = 2 * 1024 * 1024; // 2 MB in bytes

    // Method to start capturing Logcat logs and saving them into a file in the Downloads folder
    public static void captureLogToFile() {
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File logFile = new File(downloadsDir, "logcat_capture.txt");

            // Check if the log file exists and exceeds the max size
            if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
                // If the file is larger than 2MB, clear it
                clearLogFile(logFile);
            }

            // Command to capture Logcat output into the file
            String[] cmd = {"logcat", "-f", logFile.getAbsolutePath(), "*:V"};  // *:V captures all verbosity levels
            Runtime.getRuntime().exec(cmd);  // Execute the command to start capturing Logcat output

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to clear the content of the log file
    private static void clearLogFile(File logFile) throws IOException {
        if (logFile.exists()) {
            boolean deleted = logFile.delete();  // Delete the old log file
            if (deleted) {
                logFile.createNewFile();  // Create a new empty log file
            }
        }
    }
}
package com.malta_mqf.malta_mobile.Utilities;

import android.app.Application;

import java.util.TimeZone;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Set the default time zone to Dubai for the whole app
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dubai"));
    }
}

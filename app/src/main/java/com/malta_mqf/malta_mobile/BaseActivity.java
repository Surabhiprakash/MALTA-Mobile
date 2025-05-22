package com.malta_mqf.malta_mobile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.malta_mqf.malta_mobile.Utilities.ApiClient;
import com.malta_mqf.malta_mobile.Utilities.ApiInterFace;
import com.malta_mqf.malta_mobile.Utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class BaseActivity extends AppCompatActivity {
    SharedPreferences mPrefs;
    public ApiInterFace apiInterface;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();
    }

    private void initialize() {
      //  TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dubai"));
       // System.setProperty("user.timezone", "Asia/Dubai");

        mPrefs = getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterFace.class);
        mProgressDialog = new ProgressDialog(BaseActivity.this);

    }

 /*   public void  setupToolBar(String title, boolean back) {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().show();
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if (back)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(title);
    }*/
 public static String getCurrentDateInDubaiZone() {
     // Create a SimpleDateFormat object with the desired format
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

     // Set the time zone to Dubai
     TimeZone dubaiTimeZone = TimeZone.getTimeZone("Asia/Dubai");
     sdf.setTimeZone(dubaiTimeZone);


     // Get the current date and time
     Date now = new Date();

     // Return the formatted date in Dubai time zone
     return sdf.format(now);
 }
    public void showToast(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    public void displayAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public void displayAlertAndFinish(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((AppCompatActivity)context).finishAffinity();
                startActivity(new Intent(context, MainActivity.class));
            }
        });
        builder.create();
        builder.show();
    }

    public void displayAlertAndJustFinish(Context context, String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((AppCompatActivity)context).finish();
            }
        });
        builder.create();
        builder.show();
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        long lastSavedDate = sharedPreferences.getLong("lastSavedDate", 0);

        // Get the current date in milliseconds
        long currentDate = System.currentTimeMillis();

        // Check if the lastSavedDate is not set (meaning the app is newly installed)
        if (lastSavedDate == 0) {
            // Save the current date as the last saved date
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("lastSavedDate", currentDate);
            editor.apply();
        } else if (!isSameDay(lastSavedDate, currentDate)) {
            // If the date has changed, restart the app
            restartApp();
        }
    }

    // Helper method to check if two dates are on the same day
    private boolean isSameDay(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    // Method to restart the app
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Save the current date in milliseconds
        editor.putLong("lastSavedDate", System.currentTimeMillis());
        editor.apply();
    }
    public void showProgressDialog() {
        mProgressDialog.setMessage("Loading, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    public void showOutletProgressDialogs() {
        mProgressDialog.setMessage("Syncing Outlet Data, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissOutletProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public void showAgencyProgressDialogs() {
        mProgressDialog.setMessage("Syncing Agency Data, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissAgencyProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public void showItemsProgressDialogs() {
        mProgressDialog.setMessage("Syncing Items Data, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissItemsProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public void showCustomerProgressDialogs() {
        mProgressDialog.setMessage("Syncing Customer Data, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissCustomerProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    public void showSellingProgressDialogs() {
        mProgressDialog.setMessage("Syncing Price Data, please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void dismissSellingProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                this.recreate();
                return true;
            case R.id.refresh:
              *//*  finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
               // overridePendingTransition(0, 0);*//*
                this.recreate();
                break;
            case R.id.logout:
                mPrefs.edit().putBoolean(Constants.LOGGED_IN, false).commit();
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public static String getCurrentDateTimeInDubaiZone() {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Set the time zone to Dubai
        TimeZone dubaiTimeZone = TimeZone.getTimeZone("Asia/Dubai");
        sdf.setTimeZone(dubaiTimeZone);


        // Get the current date and time
        Date now = new Date();

        // Return the formatted date in Dubai time zone
        return sdf.format(now);
    }

}

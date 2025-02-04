package com.malta_mqf.malta_mobile.SewooPrinter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;

public class BaseActivity extends Activity {

    public static ArrayList<Activity> activity_list = new ArrayList<Activity>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}

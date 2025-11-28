package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

public class NewOrderActivity extends AppCompatActivity {

    CardView ordercv, orderhistorycv;
    Toolbar toolbar;

    ALodingDialog aLodingDialog;
    TextView toolbarText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ordercv = findViewById(R.id.newOrdercv);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ORDERS");
        orderhistorycv = findViewById(R.id.orderHistorycv);
        aLodingDialog = new ALodingDialog(this);
        ordercv.setOnClickListener(view -> {
            aLodingDialog.show();
            Intent i = new Intent(this, AddItemsActivity.class);
            startActivity(i);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);
        });

        orderhistorycv.setOnClickListener(view -> {
            aLodingDialog.show();
            Intent intent = new Intent(NewOrderActivity.this, ModifyOrder.class);
            startActivity(intent);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable, 2000);

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
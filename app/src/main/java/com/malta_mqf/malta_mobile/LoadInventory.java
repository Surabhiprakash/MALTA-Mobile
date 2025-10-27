package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class LoadInventory extends AppCompatActivity {

    CardView loadInventorycv, unloadInventorycv, stockInventrycv, stockTransfercv, stockReceivecv;
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_inventory);
        loadInventorycv = findViewById(R.id.loadInventorycv);
        unloadInventorycv = findViewById(R.id.unloadInventorycv);
        stockInventrycv = findViewById(R.id.stockInventorycv);
        toolbar = findViewById(R.id.toolbar);
        stockTransfercv = findViewById(R.id.stockSendcv);
        stockReceivecv = findViewById(R.id.stockReceivecv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("LOAD/UNLOAD");
        loadInventorycv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoadInventory.this, LoadInActivity.class);
                startActivity(intent);

            }
        });

        unloadInventorycv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        stockInventrycv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoadInventory.this, StockInventory.class);
                startActivity(intent);
            }
        });


        stockTransfercv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoadInventory.this, StockTransfer.class);
                startActivity(intent);
            }
        });


        stockReceivecv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoadInventory.this, StockReceive.class);
                startActivity(intent);
            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
package com.malta_mqf.malta_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.malta_mqf.malta_mobile.Dahboard.AnalysisGraph;
import com.malta_mqf.malta_mobile.Dahboard.newAnalysisGraph;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

public class dashboard_analysis extends AppCompatActivity {
    CardView Newanalysis,Oldanalysis;
    Toolbar toolbar;

    ALodingDialog aLodingDialog;
    TextView toolbarText;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_analysis);
        Newanalysis = findViewById(R.id.newanalysis);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DASHBOARD");
        Oldanalysis = findViewById(R.id.oldanalysis);
        aLodingDialog=new ALodingDialog(this);
        Newanalysis.setOnClickListener(view -> {
            aLodingDialog.show();
            Intent i=new Intent(this, newAnalysisGraph.class);
            startActivity(i);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable,2000);
        });

        Oldanalysis.setOnClickListener(view -> {
            aLodingDialog.show();
            Intent intent = new Intent(dashboard_analysis.this, AnalysisGraph.class);
            startActivity(intent);
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    aLodingDialog.cancel();
                }
            };
            handler.postDelayed(runnable,2000);

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
        Intent intent = new Intent(dashboard_analysis.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
    }
}
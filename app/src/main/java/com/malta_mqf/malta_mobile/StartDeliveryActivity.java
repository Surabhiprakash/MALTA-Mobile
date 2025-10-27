package com.malta_mqf.malta_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartDeliveryActivity extends AppCompatActivity {

    CardView todaysCardview, deliveryhistory, createNewOrder, returnWithInvoice, returnWithoutInvoice, returnHistory;
    Toolbar toolbar;

    private ALodingDialog aLodingDialog;
    private boolean isNavigating = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_delivery);
        todaysCardview = findViewById(R.id.todaysDeliverycv);
        deliveryhistory = findViewById(R.id.deliveryhistroycv);
        createNewOrder = findViewById(R.id.createNewOrder);
        aLodingDialog = new ALodingDialog(this);
        returnWithInvoice = findViewById(R.id.returnwithInvoice);
        returnWithoutInvoice = findViewById(R.id.returnOrder);
        returnHistory = findViewById(R.id.returnHistory);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("DELIVERY");

        // Flag to prevent multiple navigation actions

        todaysCardview.setOnClickListener(view -> {
            if (isNavigating) return;  // Prevent further clicks if navigation is in progress

            isNavigating = true;  // Set flag to true when navigating

            // Ensure activity is still running before showing the dialog
            if (!StartDeliveryActivity.this.isFinishing() && !StartDeliveryActivity.this.isDestroyed()) {
                aLodingDialog.show(); // Safely show the dialog
            }

            // Use ExecutorService for background processing
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Simulate background processing
                    try {
                        Thread.sleep(2000);  // Simulate work by sleeping for 2 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Run on UI thread to update UI and start new activity
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!StartDeliveryActivity.this.isFinishing() && !StartDeliveryActivity.this.isDestroyed()) {
                                Intent intent = new Intent(StartDeliveryActivity.this, TodaysDelivery.class);
                                startActivity(intent);

                                // Dismiss the dialog safely
                                if (aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                            }
                            isNavigating = false;  // Reset the flag after navigation is complete
                        }
                    });
                }
            });
        });

// Dismiss the dialog in onDestroy or onPause to prevent memory leaks


        deliveryhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNavigating) return;  // Prevent further clicks if navigation is in progress

                isNavigating = true;
                if (!StartDeliveryActivity.this.isFinishing() && !StartDeliveryActivity.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);  // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartDeliveryActivity.this, DeliveryHistory.class);
                                startActivity(intent);

                                if (!isFinishing() && aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                                isNavigating = false;  // Reset the flag after navigation is complete
                            }
                        });
                    }
                });
            }
        });

        createNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNavigating) return;  // Prevent further clicks if navigation is in progress

                isNavigating = true;
                if (!StartDeliveryActivity.this.isFinishing() && !StartDeliveryActivity.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }
                clearAllSharedPreferences();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);  // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartDeliveryActivity.this, CreateNewOrderForNewOutlet.class);
                                startActivity(intent);

                                if (!isFinishing() && aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                                isNavigating = false;  // Reset the flag after navigation is complete
                            }
                        });
                    }
                });
            }
        });

        returnWithInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNavigating) return;  // Prevent further clicks if navigation is in progress

                isNavigating = true;
                aLodingDialog.show();
                clearAllSharedPreferences();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);  // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartDeliveryActivity.this, CustomerReturn.class);
                                startActivity(intent);

                                if (!isFinishing() && aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                                isNavigating = false;  // Reset the flag after navigation is complete
                            }
                        });
                    }
                });
            }
        });

        returnWithoutInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNavigating) return;  // Prevent further clicks if navigation is in progress

                isNavigating = true;
                aLodingDialog.show();
                clearAllSharedPreferences();

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);  // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartDeliveryActivity.this, ReturnActivity.class);
                                startActivity(intent);

                                if (!isFinishing() && aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                                isNavigating = false;  // Reset the flag after navigation is complete
                            }
                        });
                    }
                });
            }
        });
        returnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNavigating) return;  // Prevent further clicks if navigation is in progress

                isNavigating = true;
                if (!StartDeliveryActivity.this.isFinishing() && !StartDeliveryActivity.this.isDestroyed()) {
                    aLodingDialog.show(); // Safely show the dialog
                }

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);  // Simulate work by sleeping for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(StartDeliveryActivity.this, Return_History.class);
                                startActivity(intent);

                                if (!isFinishing() && aLodingDialog != null && aLodingDialog.isShowing()) {
                                    aLodingDialog.cancel();
                                }
                                isNavigating = false;  // Reset the flag after navigation is complete
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (aLodingDialog != null && aLodingDialog.isShowing()) {
            aLodingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aLodingDialog != null && aLodingDialog.isShowing()) {
            aLodingDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        Intent intent = new Intent(StartDeliveryActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);  // Ensure proper behavior
        startActivity(intent);
        finish();
    }

    private void clearAllSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("createaddqtypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
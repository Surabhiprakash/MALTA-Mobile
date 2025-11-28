package com.malta_mqf.malta_mobile;

import static com.malta_mqf.malta_mobile.MainActivity.vanID;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.malta_mqf.malta_mobile.Adapter.StockReceiveAdapter;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.Model.VanStockUnloadModel;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockReceive extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StockReceiveAdapter adapter;
    private final List<VanStockUnloadModel> scannedStockList = new ArrayList<>();
    private StockDB dbHelper;
    private Toolbar toolbar;
    private SearchView searchView;
    private Button btnScanQR, btnLoadStock;
    private ImageView qrImageView;
    private ALodingDialog aLodingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_receive);

        toolbar = findViewById(R.id.toolbar);
        aLodingDialog = new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RECEIVE STOCK");

        recyclerView = findViewById(R.id.recyclerViewScannedStock);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnLoadStock = findViewById(R.id.btnLoadStock);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new StockDB(this);

        adapter = new StockReceiveAdapter(this, scannedStockList);
        recyclerView.setAdapter(adapter);

        btnScanQR.setOnClickListener(v -> scanQRCode());

        btnLoadStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannedStockList.size() == 0) {
                    Toast.makeText(StockReceive.this, "First scan the QR to add the stock", Toast.LENGTH_SHORT).show();
                } else {
                    generateQR();
                }
            }
        });
    }

    private void generateQR() {
        try {
            String qrData = "SUCCESS" + " " + vanID;
            Log.d("StockTransfer", "QR Data: " + qrData);

            // 🔹 Generate QR Code
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(qrData, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // 🔹 Show QR in Dialog
            showQRCodeDialog(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void showQRCodeDialog(Bitmap qrBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.popup_qr_code, null);
        builder.setView(dialogView);

        ImageView qrImageView = dialogView.findViewById(R.id.qrImageView);
        Button btnClosePopup = dialogView.findViewById(R.id.btnClosePopup);

        qrImageView.setImageBitmap(qrBitmap);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                loadStockToDatabase();
            }
        });

        dialog.show();
    }

    private void scanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a QR Code");
        integrator.setOrientationLocked(true); // Lock orientation to portrait
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(PortraitCaptureActivity.class); // Set custom activity
        integrator.initiateScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            processScannedData(result.getContents());
        } else {
            Toast.makeText(this, "No QR code scanned", Toast.LENGTH_SHORT).show();
        }
    }


    private void processScannedData(String scannedData) {
        System.out.println("scanned data: " + scannedData);
        try {
            scannedStockList.clear(); // Clear previous scan data
            JSONArray jsonArray = new JSONArray(scannedData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String productName = jsonObject.getString("p");
                String productId = jsonObject.getString("pid");
                String itemCategory = jsonObject.getString("c");
                String itemSubcategory = jsonObject.getString("sc");
                String itemCode = jsonObject.getString("ic");
                String unloadDate = jsonObject.getString("ud");
                String status = jsonObject.getString("s");
                int availableQty = jsonObject.getInt("aq");
                String from_vanId = jsonObject.getString("v");


                // Assuming unloadReason is not available in JSON, setting a default value
                String unloadReason = "N/A";

                scannedStockList.add(new VanStockUnloadModel(
                        vanID, from_vanId, productName, productId, itemCategory, itemSubcategory,
                        itemCode, unloadDate, unloadReason, status, availableQty
                ));
            }

            Log.d("QRScanner", "Parsed Data: " + scannedStockList.toString());
            adapter.updateList(scannedStockList);
            Toast.makeText(this, "Stock scanned successfully", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            Log.e("QRScanner", "Error parsing JSON", e);
            Toast.makeText(this, "Invalid QR data format", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadStockToDatabase() {
        if (scannedStockList.isEmpty()) {
            Toast.makeText(this, "No stock to add", Toast.LENGTH_SHORT).show();
            return;
        }


        for (VanStockUnloadModel item : scannedStockList) {
            dbHelper.stockUpdateApprovedData(vanID, item.getProductName(), item.getProductId(), item.getItemCode(), item.getItemCategory(), item.getItemSubcategory(), item.getAvailableQty(), item.getStatus());
            dbHelper.insertReceiveHistory(vanID, item.getFrom_vanid(), item.getProductName(), item.getProductId(), item.getItemCode(), item.getItemCategory(), item.getItemSubcategory(), item.getAvailableQty(), item.getStatus(), item.getUnloadDate());
        }

        Toast.makeText(this, "Stock added successfully", Toast.LENGTH_SHORT).show();
        scannedStockList.clear();
        adapter.updateList(scannedStockList);
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

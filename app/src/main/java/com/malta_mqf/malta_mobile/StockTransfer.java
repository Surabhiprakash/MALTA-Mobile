package com.malta_mqf.malta_mobile;



import static com.malta_mqf.malta_mobile.MainActivity.vanID;
import static com.malta_mqf.malta_mobile.NewSaleActivity.newSaleBeanListss;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.malta_mqf.malta_mobile.Adapter.StockTransferAdapter;
import com.malta_mqf.malta_mobile.DataBase.StockDB;
import com.malta_mqf.malta_mobile.Model.VanStockUnloadModel;
import com.malta_mqf.malta_mobile.Utilities.ALodingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StockTransfer extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StockTransferAdapter adapter;
    private List<VanStockUnloadModel> vanStockList;
    private StockDB stockDB;
    private Toolbar toolbar;
    private SearchView searchView;
    private Button btnGenerateQR, scanQR;
    private ImageView qrImageView;
    private ALodingDialog aLodingDialog;
    private ProgressDialog progressDialog;
    String qrData;
    public static List<String> prodIdlist = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_transfer);

        toolbar = findViewById(R.id.toolbar);
        aLodingDialog = new ALodingDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("TRANSFER STOCK");

        recyclerView = findViewById(R.id.recyclerViewVanStock);
        searchView = findViewById(R.id.searchView);
        btnGenerateQR = findViewById(R.id.btnGenerateQR);
        qrImageView = findViewById(R.id.qrImageView);
        scanQR = findViewById(R.id.btnScanQR);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stockDB = new StockDB(this);
        vanStockList = stockDB.getVanStockItems();

        adapter = new StockTransferAdapter(this, vanStockList);
        recyclerView.setAdapter(adapter);

        btnGenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generateQRCode();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        if (!vanStockList.isEmpty()) {
            adapter.updateList(vanStockList);
        } else {
            Toast.makeText(this, "No items available in van stock", Toast.LENGTH_SHORT).show();
        }

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });
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
            String[] result=scannedData.split(" ");
            if (Objects.equals(result[0], "SUCCESS")) {
                System.out.println("successsss");
                for (VanStockUnloadModel item : vanStockList) {
                    System.out.println("item"+item.getProductId()+" "+item.getUnloadQty());
                    if (item.getUnloadQty() != null && !item.getUnloadQty().isEmpty()) {
                        int unloadQty = Integer.parseInt(item.getUnloadQty().trim());
                        if (unloadQty > 0) {
                            System.out.println(item.getProductId()+"     "+item.getUnloadQty());
                            downGradeDeliveryQtyInStockDB(item.getProductId(),item.getUnloadQty());
                            stockDB.insertTransferHistory(vanID,result[1],item.getProductName(),item.getProductId(),item.getItemCode(),item.getItemCategory(),item.getItemSubcategory(),Integer.parseInt(item.getUnloadQty()),item.getStatus(),item.getUnloadDate());
                        }
                    }
                }
                Toast.makeText(this, "Stock Transfer Successfully", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Log.e("QRScanner", "Error parsing JSON", e);
            Toast.makeText(this, "Invalid QR data format", Toast.LENGTH_SHORT).show();
        }
    }
    private void generateQRCode() {
        try {
            if (vanStockList.isEmpty()) {
                Toast.makeText(this, "No stock items available to generate QR", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONArray jsonArray = new JSONArray();
            for (VanStockUnloadModel item : vanStockList) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (item.getUnloadQty() != null && !item.getUnloadQty().isEmpty()) {
                        int unloadQty = Integer.parseInt(item.getUnloadQty().trim());
                        if (unloadQty > 0) {
                            jsonObject.put("v", vanID);
                            jsonObject.put("p", item.getProductName());
                            jsonObject.put("pid", item.getProductId());
                            jsonObject.put("c", item.getItemCategory());
                            jsonObject.put("sc", item.getItemSubcategory());
                            jsonObject.put("ic", item.getItemCode());
                            jsonObject.put("ud", item.getUnloadDate());
                            jsonObject.put("ur", item.getUnloadReason());
                            jsonObject.put("s", item.getStatus());
                            jsonObject.put("aq", unloadQty);
                            jsonArray.put(jsonObject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String qrData = jsonArray.toString();
            if (qrData.isEmpty() || qrData.equals("[]")) {
                Toast.makeText(this, "Enter Qty to generate QR", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("StockTransfer", "QR Data: " + qrData);

            // Generate QR Code with low error correction
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Reduce error correction level

            BitMatrix bitMatrix = multiFormatWriter.encode(qrData, BarcodeFormat.QR_CODE, 400, 400, hints);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Show QR in Dialog
            showQRCodeDialog(bitmap);

        } catch (WriterException e) {
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void showQRCodeDialog(Bitmap qrBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.transfer_popup_qr_code, null);
        builder.setView(dialogView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView qrImageView = dialogView.findViewById(R.id.qrImageView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnClosePopup = dialogView.findViewById(R.id.btnClosePopup);

        qrImageView.setImageBitmap(qrBitmap);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPopup(Bitmap qrBitmap) {
        // 🔹 Inflate the popup layout
        android.view.LayoutInflater inflater = (android.view.LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        android.view.View popupView = inflater.inflate(R.layout.popup_qr_code, null);

        // 🔹 Create the popup window
        android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // 🔹 Set QR Code to ImageView
        ImageView qrImageView = popupView.findViewById(R.id.qrImageView);
        qrImageView.setImageBitmap(qrBitmap);

        // 🔹 Close button functionality
        Button btnClosePopup = popupView.findViewById(R.id.btnClosePopup);
        btnClosePopup.setOnClickListener(v -> popupWindow.dismiss());

        // 🔹 Show popup at center of screen
        popupWindow.showAtLocation(findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);
    }


    private void downGradeDeliveryQtyInStockDB(String prodId, String unloadQty) {
        // Check if prodId is already processed


            // Validate unloadQty before parsing
            if (unloadQty == null || unloadQty.trim().isEmpty()) {
                Log.e("downGradeDeliveryQtyInStockDB", "Unload quantity is null or empty for product ID: " + prodId);
                return; // Exit method if unloadQty is invalid
            }

            int deliveryQty;
            try {
                // Parse the delivery quantity
                deliveryQty = Integer.parseInt(unloadQty.trim());
            } catch (NumberFormatException e) {
                Log.e("downGradeDeliveryQtyInStockDB", "Invalid delivery quantity: " + unloadQty + " for product ID: " + prodId, e);
                return; // Exit the method if parsing fails
            }

            // Read the current available quantity from the database
            Cursor cursor2 = stockDB.readonproductid(prodId);
            if (cursor2 != null) {
                try {
                    if (cursor2.getCount() > 0) {
                        while (cursor2.moveToNext()) {
                            @SuppressLint("Range") int availableQty = cursor2.getInt(cursor2.getColumnIndex(StockDB.COLUMN_T0TAl_AVLAIBLE_QTY_ON_HAND));

                            // Calculate the new available quantity
                            int newAvailableQty = Math.max(0, availableQty - deliveryQty); // Ensure it's not negative

                            // Update the database
                            stockDB.updateAvailableQty(prodId, newAvailableQty);
                        }
                    } else {
                        Log.e("StockTransfer", "Cursor is empty for product ID: " + prodId);
                    }
                } finally {
                    cursor2.close(); // Ensure the cursor is always closed
                }
            } else {
                Log.e("StockTransfer", "Cursor is null for product ID: " + prodId);
            }

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



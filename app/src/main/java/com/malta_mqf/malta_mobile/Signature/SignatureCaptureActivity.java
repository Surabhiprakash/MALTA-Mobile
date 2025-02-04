package com.malta_mqf.malta_mobile.Signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.malta_mqf.malta_mobile.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SignatureCaptureActivity extends AppCompatActivity {

    private SignaturePad signatureView;
  public static   byte[] signatureData;
   public static String encodedSignatureImage;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_capture);

        signatureView = findViewById(R.id.signatureView);
        Button saveButton = findViewById(R.id.saveSignatureButton);
        saveButton.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        Button clearButton = findViewById(R.id.clearSignatureButton);
        clearButton.setBackgroundColor(getResources().getColor(R.color.Red));
        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.appColorpurple));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SIGN BELOW");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signatureView.getSignatureBitmap();

                // Step 1: Resize the bitmap (start with a scaled-down version)
                int targetWidth = signatureBitmap.getWidth() / 2;  // Resize to half the width
                int targetHeight = signatureBitmap.getHeight() / 2; // Resize to half the height
                Bitmap resizedSignatureBitmap = Bitmap.createScaledBitmap(signatureBitmap, targetWidth, targetHeight, true);

                // Step 2: Compress and reduce quality progressively until the size is < 1MB
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                int quality = 100; // Start with maximum quality
                resizedSignatureBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream); // Initial compression

                // Convert the compressed bitmap to byte array
                signatureData = stream.toByteArray();

                // Step 3: Reduce quality progressively if the size exceeds 1MB
                while (signatureData.length >= 1024 * 1024) { // While the size is 1MB or more
                    stream.reset(); // Clear the ByteArrayOutputStream
                    quality -= 10; // Reduce quality by 10%
                    resizedSignatureBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream); // Compress again with reduced quality
                    signatureData = stream.toByteArray(); // Get the byte array again

                    if (quality <= 10) { // Minimum quality threshold to avoid over-compression
                        break; // Stop if quality drops too low
                    }
                }

                // Step 4: Convert the byte array to a Base64 encoded string
                encodedSignatureImage = Base64.encodeToString(signatureData, Base64.DEFAULT);

                // Log the final size and quality used for debugging purposes
                Log.d("SignatureSize", "Final Signature byte array size: " + signatureData.length + " bytes, Quality: " + quality);

                // Step 5: Pass the byte array through the Intent
                Intent resultIntent = new Intent();
                resultIntent.putExtra("signatureByteArray", signatureData);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clear();
            }
        });
    }
    private void saveSignatureToGallery(Bitmap signature, String prefix) {
        int targetWidth = 15;
        int targetHeight = 15;

        Bitmap resizedSignature = resizeBitmap(signature, targetWidth, targetHeight);

        File signatureFile = saveBitmapToJPG(resizedSignature, prefix);
        if (signatureFile != null) {
            scanMediaFile(signatureFile);
            Toast.makeText(this, "Signature saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
           // Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, true);
    }

    private File saveBitmapToJPG(Bitmap bitmap, String prefix) {
        File file = new File(getAlbumStorageDir("SignaturePad"), String.format("%s_%d.jpg", prefix, System.currentTimeMillis()));
        try (OutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void scanMediaFile(File signatureFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(signatureFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
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

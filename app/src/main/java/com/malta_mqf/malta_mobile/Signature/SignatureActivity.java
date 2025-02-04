package com.malta_mqf.malta_mobile.Signature;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.malta_mqf.malta_mobile.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class SignatureActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SIGNATURE = 1;
    public static final int REQUEST_CODE_BILL = 2;

    private ImageView signatureImageView;
    private ImageView billImageView;

    private Bitmap signatureBitmap;
    private Bitmap billBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        // Request necessary permissions (WRITE_EXTERNAL_STORAGE and CAMERA)
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                0);

        signatureImageView = findViewById(R.id.signatureImageView);
        billImageView = findViewById(R.id.billImageView);

        Button captureSignatureButton = findViewById(R.id.captureSignatureButton);
        Button captureBillButton = findViewById(R.id.captureBillButton);
        Button saveButton = findViewById(R.id.saveButton);

        captureSignatureButton.setOnClickListener(v -> {
            Intent signatureIntent = new Intent(this, SignatureCaptureActivity.class);
            startActivityForResult(signatureIntent, REQUEST_CODE_SIGNATURE);
        });

        captureBillButton.setOnClickListener(v -> {

        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveSignatureToGallery(signatureBitmap, "signature");              //  byte[] jpgByteArray = stream.toByteArray();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SIGNATURE) {
                byte[] signatureByteArray = data.getByteArrayExtra("signatureByteArray");
                signatureBitmap = BitmapFactory.decodeByteArray(signatureByteArray, 0, signatureByteArray.length);

                // Convert the signatureBitmap to JPEG format
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //saveSignatureToGallery(signatureBitmap, "signature");              //  byte[] jpgByteArray = stream.toByteArray();

                // Set the converted signatureBitmap to the ImageView
                signatureImageView.setImageBitmap(signatureBitmap);
            } else if (requestCode == REQUEST_CODE_BILL) {
                billBitmap = (Bitmap) data.getExtras().get("data");
                billImageView.setImageBitmap(billBitmap);
            }
        }
    }

    private void saveSignatureToGallery(Bitmap signature, String prefix) {
        int targetWidth = 150;
        int targetHeight = 150;

        Bitmap resizedSignature = resizeBitmap(signature, targetWidth, targetHeight);

        File signatureFile = saveBitmapToJPG(resizedSignature, prefix);
        if (signatureFile != null) {
            scanMediaFile(signatureFile);
            Toast.makeText(this, "Signature saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
          //  Toast.makeText(this, "Failed to save signature", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap image, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
    }

    private File saveBitmapToJPG(Bitmap bitmap, String prefix) {
        File file = new File(getAlbumStorageDir("SignaturePad"), String.format("%s_%d.jpg", prefix, System.currentTimeMillis()));
        try (OutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
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

    private void saveImagesToGallery() {
        if (signatureBitmap != null && billBitmap != null) {
            String signatureFileName = "signature_" + UUID.randomUUID().toString() + ".jpeg";
            String billFileName = "bill_" + UUID.randomUUID().toString() + ".jpeg";

            saveImageToGallery(signatureBitmap, signatureFileName);
            saveImageToGallery(billBitmap, billFileName);

            Toast.makeText(this, "Images saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Capture both signature and bill first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Insert the image metadata into MediaStore
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Save the bitmap to the gallery
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, fileName, null);
    }
}

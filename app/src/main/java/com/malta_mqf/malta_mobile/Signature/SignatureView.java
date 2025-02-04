package com.malta_mqf.malta_mobile.Signature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {

    private Path signaturePath;
    private Paint signaturePaint;
    private Bitmap signatureBitmap;
    private Canvas signatureCanvas;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        signaturePath = new Path();
        signaturePaint = new Paint();
        signaturePaint.setAntiAlias(true);
        signaturePaint.setStrokeWidth(5f); // Adjust as needed
        signaturePaint.setStyle(Paint.Style.STROKE);
        signaturePaint.setStrokeJoin(Paint.Join.ROUND);
        signaturePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Calculate the desired bitmap dimensions based on screen density (DPI)
        float density = getResources().getDisplayMetrics().density;
        int desiredWidth = (int) (width / density);
        int desiredHeight = (int) (height / density);

        // Create a new Bitmap with the desired resolution
        signatureBitmap = Bitmap.createBitmap(desiredWidth, desiredHeight, Bitmap.Config.ARGB_8888);
        signatureCanvas = new Canvas(signatureBitmap);
        signatureCanvas.scale(density, density); // Scale the canvas
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(signatureBitmap, 0, 0, signaturePaint);
        canvas.drawPath(signaturePath, signaturePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                signaturePath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                signaturePath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                signatureCanvas.drawPath(signaturePath, signaturePaint);
                signaturePath.reset();
                break;
        }
        invalidate();
        return true;
    }
}

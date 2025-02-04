package com.malta_mqf.malta_mobile.ZebraPrinter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;

public class UIHelper {
    private ProgressDialog loadingDialog;
    private WeakReference<Activity> activityRef;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public UIHelper(Activity activity) {
        this.activityRef = new WeakReference<>(activity);
    }

    private Activity getActivity() {
        return activityRef.get();
    }

    // Method to show loading dialog
    public void showLoadingDialog(final String message) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            mainHandler.post(() -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    if (loadingDialog == null) { // Ensure loadingDialog is only created once
                        loadingDialog = ProgressDialog.show(activity, "", message, true, false);
                    }
                }
            });
        }
    }

    // Method to update the loading dialog message
    public void updateLoadingDialog(final String message) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            mainHandler.post(() -> {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.setMessage(message);
                }
            });
        }
    }

    // Check if the loading dialog is active
    public boolean isDialogActive() {
        return loadingDialog != null && loadingDialog.isShowing();
    }

    // Method to dismiss the loading dialog safely
    public void dismissLoadingDialog() {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            mainHandler.post(() -> {
                if (loadingDialog != null) {
                    // Check if dialog is currently showing and dismiss it
                    try {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    } catch (Exception e) {
                        // Catch any unexpected errors, such as when activity is destroyed
                        e.printStackTrace();
                    } finally {
                        loadingDialog = null; // Reset to avoid future null pointer issues
                    }
                }
            });
        }
    }

    // Method to show an error dialog
    public void showErrorDialog(final String errorMessage) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            new AlertDialog.Builder(activity)
                    .setMessage(errorMessage)
                    .setTitle("Error")
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    // Method to show an error dialog on the UI thread
    public void showErrorDialogOnGuiThread(final String errorMessage) {
        Activity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            mainHandler.post(() -> {
                if (!activity.isFinishing() && !activity.isDestroyed()) {
                    new AlertDialog.Builder(activity)
                            .setMessage(errorMessage)
                            .setTitle("Error")
                            .setPositiveButton("OK", (dialog, id) -> {
                                dialog.dismiss();
                                dismissLoadingDialog();
                            })
                            .create()
                            .show();
                }
            });
        }
    }

    // Add this method to ensure dialog is dismissed when the activity is destroyed
    public void onDestroy() {
        dismissLoadingDialog();
    }
}

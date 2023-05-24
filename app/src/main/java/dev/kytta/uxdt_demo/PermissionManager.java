package dev.kytta.uxdt_demo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class PermissionManager {
    private static final String TAG = "PermissionManager";

    private final ComponentActivity activity;

    private ActivityResultLauncher<String> requestPermissionLauncher;


    public PermissionManager(ComponentActivity activity) {
        this.activity = activity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.i(TAG, "Permission is granted");
                } else {
                    Log.w(TAG, "Permission is not granted");
                }
            });
        }
    }

    public void requestPostNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "We are not on Android 13+, so we don't need to request permission to show notifications.");
            return;
        }

        Log.i(TAG, "Android 13+ detected. Requesting permission to show notifications...");
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "We can show notifications");
        } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Log.i(TAG, "We should show rationale for notification permission");
            showPostNotificationsPermissionRationale();
        } else {
            Log.i(TAG, "We should request notification permission");
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    /**
     * Requests permission to record audio.
     *
     * @return true if user's decision is final, false otherwise
     */
    public boolean requestRecordAudioPermission() {
        Log.i(TAG, "Requesting permission to record audio...");
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "We can record audio");
            return true;
        } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            Log.i(TAG, "We should show rationale for microphone permission");
            showRecordAudioPermissionRationale();
            return true;
        } else {
            Log.i(TAG, "We should request microphone permission");
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            return false;
        }
    }

    private void showPostNotificationsPermissionRationale() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "We are not on Android 13+, so we don't need to show rationale for notification permission.");
            return;
        }

        Log.i(TAG, "We should show rationale");
        showSnackbar("You will not see recording status notifications unless you grant the permission.");
    }

    private void showRecordAudioPermissionRationale() {
        showSnackbar("You will not be able to test microphone functionality unless you grant the permission.");
    }

    private void showSnackbar(String message) {
        Snackbar.make(activity.findViewById(R.id.root),
                        message,
                        Snackbar.LENGTH_LONG)
                .setAction("Grant permission", view -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(intent);
                })
                .show();
    }
}

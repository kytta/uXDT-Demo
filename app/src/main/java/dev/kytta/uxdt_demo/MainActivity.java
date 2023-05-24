package dev.kytta.uxdt_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import dev.kytta.uxdt_demo.collect.GyroscopeService;
import dev.kytta.uxdt_demo.collect.MicrophoneService;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    private LinearLayout mainLayout;

    private SwitchCompat microphoneSwitch;
    private SwitchCompat gyroscopeSwitch;

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.root);

        Availability availability = new Availability(this);

        permissionManager = new PermissionManager(this);
        permissionManager.requestPostNotificationsPermission();

        microphoneSwitch = findViewById(R.id.microphone_switch);
        if (availability.isMicrophoneAvailable()) {
            microphoneSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    boolean stopAsking;
                    do {
                        stopAsking = permissionManager.requestRecordAudioPermission();
                    } while (!stopAsking);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        startRecordingService();
                    } else {
                        compoundButton.setChecked(false);
                    }
                } else {
                    stopRecordingService();
                }
            });
        } else {
            microphoneSwitch.setEnabled(false);
        }

        gyroscopeSwitch = findViewById(R.id.gyroscope_switch);
        if (availability.isGyroscopeAvailable()) {
            gyroscopeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked) {
                    startGyroscopeService();
                } else {
                    stopGyroscopeService();
                }
            });
        } else {
            gyroscopeSwitch.setEnabled(false);
        }
    }

    private void getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    162);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 162) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // allow
                Log.i(TAG, "We can send notifications");
            } else {
                //deny
                Log.w(TAG, "We cannot send notifications");
            }
        }

    }

    private void requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(gyroscopeSwitch,
                            "Microphone permission is required to record audio",
                            Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view -> ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1))
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private void startRecordingService() {
        Intent serviceIntent = new Intent(this, MicrophoneService.class);
        startService(serviceIntent);
    }

    private void stopRecordingService() {
        Intent serviceIntent = new Intent(this, MicrophoneService.class);
        serviceIntent.setAction("ACTION_STOP_RECORDING");
        startService(serviceIntent);
    }

    private void startGyroscopeService() {
        Intent serviceIntent = new Intent(this, GyroscopeService.class);
        startService(serviceIntent);
    }

    private void stopGyroscopeService() {
        Intent serviceIntent = new Intent(this, GyroscopeService.class);
        serviceIntent.setAction("ACTION_STOP_COLLECTING");
        startService(serviceIntent);
    }

    private void onNotificationPermissionResult(Boolean isGranted) {
        if (isGranted) {
            Log.i(TAG, "We can show notifications");
        }
        Snackbar.make(mainLayout,
                        "You will not see recording status notifications unless you grant the permission.",
                        Snackbar.LENGTH_LONG)
                .setAction("Grant", view -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                    MainActivity.this.startActivity(intent);
                })
                .show();
    }
}

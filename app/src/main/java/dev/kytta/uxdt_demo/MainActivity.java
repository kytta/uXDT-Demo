package dev.kytta.uxdt_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private SwitchCompat recordingSwitch;
    private SwitchCompat gyroscopeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 42);
            } else {
                // TODO: do nothing?
            }
        }

        recordingSwitch = findViewById(R.id.recording_switch);
        recordingSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestMicrophonePermission();
                    compoundButton.setChecked(false);
                } else startRecordingService();
            } else {
                stopRecordingService();
            }
        });

        gyroscopeSwitch = findViewById(R.id.gyroscope_switch);
        gyroscopeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                startGyroscopeService();
            } else {
                stopGyroscopeService();
            }
        });
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
}

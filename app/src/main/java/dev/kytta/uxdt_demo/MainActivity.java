package dev.kytta.uxdt_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import dev.kytta.uxdt_demo.collect.AccelerometerService;
import dev.kytta.uxdt_demo.collect.GyroscopeService;
import dev.kytta.uxdt_demo.collect.MicrophoneService;
import dev.kytta.uxdt_demo.collect.Status;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Setting up activity...");

        Availability availability = new Availability(this);

        permissionManager = new PermissionManager(this);
        permissionManager.requestPostNotificationsPermission();

        SwitchCompat microphoneSwitch = findViewById(R.id.microphone_switch);
        SwitchCompat gyroscopeSwitch = findViewById(R.id.gyroscope_switch);
        SwitchCompat accelerometerSwitch = findViewById(R.id.accelerometer_switch);
        SwitchCompat gyroscopeBackgroundSwitch = findViewById(R.id.gyroscope_bg_switch);
        SwitchCompat accelerometerBackgroundSwitch = findViewById(R.id.accelerometer_bg_switch);

        microphoneSwitch.setChecked(MicrophoneService.isRunning());

        gyroscopeSwitch.setEnabled(GyroscopeService.getStatus() != Status.RUNNING_IN_BACKGROUND);
        gyroscopeSwitch.setChecked(GyroscopeService.getStatus() == Status.RUNNING_IN_FOREGROUND);
        gyroscopeBackgroundSwitch.setEnabled(GyroscopeService.getStatus() != Status.RUNNING_IN_FOREGROUND);
        gyroscopeBackgroundSwitch.setChecked(GyroscopeService.getStatus() == Status.RUNNING_IN_BACKGROUND);

        accelerometerSwitch.setEnabled(AccelerometerService.getStatus() != Status.RUNNING_IN_BACKGROUND);
        accelerometerSwitch.setChecked(AccelerometerService.getStatus() == Status.RUNNING_IN_FOREGROUND);
        accelerometerBackgroundSwitch.setEnabled(AccelerometerService.getStatus() != Status.RUNNING_IN_FOREGROUND);
        accelerometerBackgroundSwitch.setChecked(AccelerometerService.getStatus() == Status.RUNNING_IN_BACKGROUND);

        if (availability.isMicrophoneAvailable()) {
            microphoneSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent service = new Intent(this, MicrophoneService.class);
                if (isChecked) {
                    boolean stopAsking;
                    do {
                        stopAsking = permissionManager.requestRecordAudioPermission();
                    } while (!stopAsking);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        startForegroundService(service);
                    } else {
                        compoundButton.setChecked(false);
                    }
                } else {
                    stopService(service);
                }
            });
        } else {
            microphoneSwitch.setEnabled(false);
        }

        if (availability.isGyroscopeAvailable()) {
            gyroscopeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent service = new Intent(this, GyroscopeService.class);
                if (isChecked) {
                    startForegroundService(service);
                } else {
                    stopService(service);
                }
            });
            gyroscopeBackgroundSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent service = new Intent(this, GyroscopeService.class);
                service.setAction(Constants.ACTION_RUN_IN_BACKGROUND);
                if (isChecked) {
                    startService(service);
                } else {
                    stopService(service);
                }
            });
        } else {
            gyroscopeSwitch.setEnabled(false);
            gyroscopeBackgroundSwitch.setEnabled(false);
        }


        if (availability.isAccelerometerAvailable()) {
            accelerometerSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent service = new Intent(this, AccelerometerService.class);
                if (isChecked) {
                    startForegroundService(service);
                } else {
                    stopService(service);
                }
            });
            accelerometerBackgroundSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Intent service = new Intent(this, AccelerometerService.class);
                service.setAction(Constants.ACTION_RUN_IN_BACKGROUND);
                if (isChecked) {
                    startService(service);
                } else {
                    stopService(service);
                }
            });
        } else {
            accelerometerSwitch.setEnabled(false);
            accelerometerBackgroundSwitch.setEnabled(false);
        }
    }
}

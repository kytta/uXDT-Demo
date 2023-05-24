package dev.kytta.uxdt_demo.collect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import dev.kytta.uxdt_demo.Constants;
import dev.kytta.uxdt_demo.MainActivity;
import dev.kytta.uxdt_demo.R;

public class AccelerometerService extends Service implements SensorEventListener {
    private final static String TAG = "AccelerometerService";

    private static final int NOTIFICATION_ID = 3;
    private SensorManager sensorManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        startCollectingData();
        startForeground(NOTIFICATION_ID, createNotification());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCollectingData();
    }

    private void startCollectingData() {
        Log.d(TAG, "startCollectingData()");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometerSensor == null) {
            // This should never happen, but just in case...
            Log.w(TAG, "Accelerometer sensor is not available on this device.");
            return;
        }

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopCollectingData() {
        Log.d(TAG, "stopCollectingData()");
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, Constants.RECORDING_CHANNEL_ID)
                .setContentTitle(getString(R.string.collecting))
                .setContentText(getString(R.string.notif_accelerometer))
                .setSmallIcon(R.drawable.ic_accelerometer)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.accelerometer_ticker))
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Process accelerometer data or simply discard it
        Log.d(TAG, "Accelerometer data: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}

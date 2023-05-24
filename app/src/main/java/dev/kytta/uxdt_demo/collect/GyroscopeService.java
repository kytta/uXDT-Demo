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

import androidx.core.app.NotificationCompat;

import dev.kytta.uxdt_demo.Constants;
import dev.kytta.uxdt_demo.MainActivity;
import dev.kytta.uxdt_demo.R;

public class GyroscopeService extends Service implements SensorEventListener {
    private final static String TAG = "GyroscopeService";

    private static final int NOTIFICATION_ID = 2;
    private SensorManager sensorManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

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
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            // This should never happen, but just in case...
            Log.w(TAG, "Gyroscope sensor is not available on this device.");
            return;
        }

        Log.i(TAG, "Gyroscope sampling rate: " + 1000000 / gyroscopeSensor.getMinDelay() + " Hz");
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

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
                .setContentText(getString(R.string.notif_gyroscope))
                .setSmallIcon(R.drawable.ic_gyroscope)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.gyroscope_ticker))
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Process the gyroscope data here or simply discard it
        Log.d(TAG, "Gyroscope data: " + sensorEvent.values[0] + ", " + sensorEvent.values[1] + ", " + sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Not used
    }
}

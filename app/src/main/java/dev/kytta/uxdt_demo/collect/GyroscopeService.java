package dev.kytta.uxdt_demo.collect;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import dev.kytta.uxdt_demo.MainActivity;
import dev.kytta.uxdt_demo.R;

public class GyroscopeService extends Service implements SensorEventListener {
    private final String TAG = "GyroscopeService";

    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "gyroscope_collecting_status";
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private boolean isCollectingData = false;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Gyroscope Status", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Will show a persistent notification when the app is collecting the gyroscope data.");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("ACTION_STOP_COLLECTING")) {
                Log.d(TAG, "ACTION_STOP_COLLECTING received");
                stopCollectingData();
                stopSelf();
                return START_NOT_STICKY;
            }
        }

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
        Log.d(TAG, "startCollectingData() called");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            // This should never happen, but just in case...
            Log.w(TAG, "Gyroscope sensor is not available on this device.");
            return;
        }

        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

        isCollectingData = true;
    }

    private void stopCollectingData() {
        Log.d(TAG, "stopCollectingData() called");

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        isCollectingData = false;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Collecting...")
                .setContentText("The app is currently collecting gyroscope data.")
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true);

        return builder.build();
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

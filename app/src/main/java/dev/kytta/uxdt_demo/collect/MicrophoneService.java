package dev.kytta.uxdt_demo.collect;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import dev.kytta.uxdt_demo.Constants;
import dev.kytta.uxdt_demo.MainActivity;
import dev.kytta.uxdt_demo.R;

public class MicrophoneService extends Service {
    private static final String TAG = "MicrophoneService";

    private static final int NOTIFICATION_ID = 1;
    private AudioRecord audioRecord;
    private static boolean recording = false;
    private static Status status = Status.NOT_RUNNING;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        startRecording();
        startForeground(NOTIFICATION_ID, createNotification());

        return START_STICKY;
    }

    public static boolean isRunning() {
        return status != Status.NOT_RUNNING;
    }

    public static Status getStatus() {
        return status;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stopRecording();
    }

    private void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; abort
            stopSelf();
            return;
        }

        int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();

        recording = true;
        status = Status.RUNNING_IN_FOREGROUND;

        new Thread(() -> {
            short[] buffer = new short[bufferSize];
            while (recording) {
                audioRecord.read(buffer, 0, bufferSize);
                // Process the audio data here or simply discard it
            }
            audioRecord.stop();
            audioRecord.release();
        }).start();
    }

    private void stopRecording() {
        recording = false;
        status = Status.NOT_RUNNING;
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, Constants.RECORDING_CHANNEL_ID)
                .setContentTitle(getString(R.string.listening))
                .setContentText(getString(R.string.notif_listening))
                .setSmallIcon(R.drawable.ic_microphone)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.listening_ticker))
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

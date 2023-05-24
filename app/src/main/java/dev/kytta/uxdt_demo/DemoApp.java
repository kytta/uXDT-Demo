package dev.kytta.uxdt_demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel =
                new NotificationChannel(Constants.RECORDING_CHANNEL_ID, getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string.notification_channel_description));
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}

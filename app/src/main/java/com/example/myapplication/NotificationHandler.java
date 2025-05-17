package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "estate_notification_channel";
    private static final String TAG = "NotificationHandler";

    private Context mContext;
    private NotificationManager mManager;

    public NotificationHandler(Context c) {
        Log.d(TAG, "Initializing NotificationHandler...");
        this.mContext = c;

        try {
            this.mManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            createChannel();
        } catch (Exception e) {
            Log.e(TAG, "Error while initializing NotificationHandler: ", e);
        }
    }

    private void createChannel(){
        Log.d(TAG, "createChannel called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Android 8.0+, creating channel...");

            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, // 🔧 Helyes konstans használat
                        "Real Estate Notification",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setLightColor(Color.RED);
                channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
                channel.setDescription("Real Estate Notification");

                mManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created successfully");

            } catch (Exception e) {
                Log.e(TAG, "Failed to create notification channel: ", e);
            }
        } else {
            Log.d(TAG, "Android verzió < 8.0, nem kell channel");
        }
    }

    public void send(String message){
        Log.d(TAG, "send() called with message: " + message);

        try {
            Intent intent = new Intent(mContext, RealEstateListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    mContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Fontos Android 12+
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setContentTitle("Real Estate Notification")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.baseline_house_24)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            mManager.notify(NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Notification sent successfully");

        } catch (Exception e) {
            Log.e(TAG, "Failed to send notification: ", e);
        }
    }

    public void cancel(){
        mManager.cancel(NOTIFICATION_ID);
    }
}

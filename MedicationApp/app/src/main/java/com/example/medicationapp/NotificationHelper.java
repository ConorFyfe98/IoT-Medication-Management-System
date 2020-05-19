package com.example.medicationapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channeID";
    public static final String channelName = "channel Name";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        // Create a channel for version Oreo or higher. Not required if lower version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create new channel, set id, name and level of importance
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        // If manager is null create a new one, if not use current manager
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String message) {

        // Set text to the dark green
        int darkGreen = Color.parseColor("#48871C");

        // Set pendingintent for user clicking notification, opening home page
        PendingIntent mainActivityIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);



        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                // Set large icon, small icon, title, text, color, priority
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_alarm)
                .setColor(darkGreen)
                .setPriority(Notification.PRIORITY_MAX) //Max priority as medication is time crucial

                .setContentIntent(mainActivityIntent)// When notification is clicked open home page
                .setAutoCancel(true); // When notification is clicked remove it.
    }
}


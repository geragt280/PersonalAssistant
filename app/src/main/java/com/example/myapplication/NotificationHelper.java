package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.view.SoundEffectConstants;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String channel1ID = "channel1ID";
    public static final String channel1Name = "Channel 1";

    public static final String channel2ID = "channel2ID";
    public static final String channel2Name = "Channel 2";

    NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannels();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void  createChannels(){

        NotificationChannel channel = new NotificationChannel("1", "channelName", NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);

        NotificationChannel channel1 = new NotificationChannel(channel1ID, channel1Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.colorPrimary);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2 = new NotificationChannel(channel2ID, channel2Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.colorPrimary);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel2);

    }

    public NotificationManager getManager()
    {
        if (notificationManager == null)
        {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }



    public NotificationCompat.Builder getChannel1Notification()  {
        return new NotificationCompat.Builder(getApplicationContext(),channel1ID).setSmallIcon(R.drawable.ic_sleepicon);
    }
    public NotificationCompat.Builder getChannel2Notification(String title, String message)  {
        return new NotificationCompat.Builder(getApplicationContext(),channel1ID).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_alarm);
    }

    public NotificationCompat.Builder getChannelNotification() {


        return new NotificationCompat.Builder(getApplicationContext(), "1")
                .setSmallIcon(R.drawable.ic_alarm);
    }
}


package com.example.myapplication;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ActionProvider;
import android.widget.Toast;
import android.R.raw;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class AlertReceiver extends BroadcastReceiver {
    
    private static final String ACTION_DISMISS = "Dismiss";
    Context privateContext;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        privateContext = context;
        try {
            String type = intent.getStringExtra("notificationType");
            Intent eventMainIntent = new Intent(context, MainEvent.class);
            String subject = intent.getStringExtra("eventSubject");
            int code = intent.getIntExtra("requestCode", 0);

            assert type != null;
            if (type.equals("Alarm") && code != 0)
            {
                String ringtoneUri = intent.getStringExtra("ringtoneUrl");
                Uri alarmUri;
                if (ringtoneUri != null){
                    alarmUri = Uri.parse(ringtoneUri);
                }
                else{
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                }
                Information.gRingtone = RingtoneManager.getRingtone(context, alarmUri);
                boolean playAccess = intent.getBooleanExtra("AlarmAccess", false);
                if (playAccess) {
                    Information.gRingtone.play();
                } else {
                    Information.gRingtone.stop();
                }
                Intent stopAlarm = new Intent(context, AlertReceiver.class).setAction(ACTION_DISMISS).putExtra("notificationType", "Alarm");
                PendingIntent stopAlarmIntent = PendingIntent.getActivity(context, code, stopAlarm, 0);

                PendingIntent contentIntent = PendingIntent.getActivity(context, code, eventMainIntent, 0);
                NotificationHelper notificationHelper = new NotificationHelper(context);
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification().setContentTitle("Event Alarm").setAutoCancel(true).setContentText(subject).setCategory(NotificationCompat.CATEGORY_ALARM).setContentIntent(contentIntent)
                        .addAction(R.drawable.cancel_icon, "Dismiss", stopAlarmIntent);
                notificationHelper.getManager().notify(1, nb.build());
                if (subject != null)
                    EndEvent(subject);
            } else if (type.equals("Alert") && code != 0) {
                NotificationHelper notificationHelper = new NotificationHelper(context);
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification().setContentTitle("Upcoming Event").setAutoCancel(true).setContentText(subject).setCategory(NotificationCompat.CATEGORY_MESSAGE);
                notificationHelper.getManager().notify(1, nb.build());
            }
            else if (type.equals("FoodAlert") && code != 0){
                NotificationHelper notificationHelper = new NotificationHelper(context);
                NotificationCompat.Builder nb = notificationHelper.getChannelNotification().setContentTitle("Personal Schedule").setAutoCancel(true).setContentText(subject).setCategory(NotificationCompat.CATEGORY_MESSAGE);
                notificationHelper.getManager().notify(1, nb.build());

                try {
                    MealDialogAlertBuilder(subject);
                }
                catch (Exception e){
                    Toast.makeText(privateContext, "Error in AlertBuilder. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else if (type.equals("EventLocationService")){
                Intent LocationServiceIntent = new Intent(context,LocationBackgroundService.class);
                String location = intent.getStringExtra("eventLocation");
                LocationServiceIntent.putExtra("location",location);
                //context.startService(LocationServiceIntent);
                context.startForegroundService(LocationServiceIntent);
            }

            else{
                Toast.makeText(context, "Something is not according to our conditions.\n while generating Notification.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(context, "AlertReceiver(onCreate). Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void MealDialogAlertBuilder(String occasion)
    {
        Information.gOccasion = occasion;
        Intent i = new Intent("android.intent.action.MAIN");
        i.setClass(privateContext, DialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        privateContext.startActivity(i);

    }

    void EndEvent(String subject){
        try {
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            String log = subject + "/" + date.toString() + "/" + c.get(Calendar.DAY_OF_WEEK) + "/" + c.get(Calendar.DAY_OF_MONTH);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("users/" + Information.gusername + "/eventLogs/");
            LogHelperClass LogHelperClass = new LogHelperClass(log);
            databaseReference.child(subject).setValue(LogHelperClass);
        }catch (Exception e){
            Toast.makeText(privateContext, "AlertReceiver(EndEvent). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Error in EndEvent's Add log. Error: " + e.getMessage());
        }
        try{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Information.gusername).child("events").child(subject);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(privateContext, "I tried to delete.", Toast.LENGTH_SHORT).show();
                        //snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Toast.makeText(privateContext, "Error in EndEvent's Delete Event. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}


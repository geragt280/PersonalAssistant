package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    ArrayList<EventHelperClass> eventList;
    ArrayList<Calendar> calendars;
    Context personalContext;
    Intent personalIntent;
    int todayEvent=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //Your code once the alarm is set off goes here
        //You can use an intent filter to filter the specified intent
        personalContext = context;
        personalIntent = intent;
        eventList = new ArrayList<EventHelperClass>();
        calendars = new ArrayList<Calendar>();

        String type = intent.getStringExtra("ServiceType");

        if (type != null){
            if (type.equals("SleepService")){
                SleepCheckService();
            }
        }
        else {
            countEventsForToday();
        }
    }

    void countEventsForToday() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        collecteventsubject((Map<String, Object>) snapshot.getValue());
                        if (eventList.size() > 0) {
                            for (int i = 0; i < eventList.size(); i++) {
                                Random rdm = new Random();
                                int num = rdm.nextInt() / 1000000;
                                num = num * num;
                                Information.galarmRequest_Code = "" + num;
                                Calendar c = Calendar.getInstance();
                                String[] arrDate = eventList.get(i).eventdate.split("/");
                                String[] arrTime = eventList.get(i).eventtime.split(":");
                                c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
                                c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
                                c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
                                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
                                c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
                                c.set(Calendar.SECOND, 0);
                                Date date = java.util.Calendar.getInstance().getTime();
                                if (c.get(Calendar.DAY_OF_MONTH) == date.getDate()) {
                                    todayEvent += 1;
                                }
                            }
                        }
                        Intent eventMainIntent = new Intent(personalContext, MainEvent.class);
                        String subject = "You have " + todayEvent + " Events for Today.";
                        int code = personalIntent.getIntExtra("requestCode", 0);
                        Information.galarmRequest_Code = "" + code;
                        PendingIntent contentIntent = PendingIntent.getActivity(personalContext, code, eventMainIntent, 0);
                        NotificationHelper notificationHelper = new NotificationHelper(personalContext);
                        NotificationCompat.Builder nb = notificationHelper.getChannelNotification().setContentTitle("Today's Events").setAutoCancel(true).setContentText(subject).setCategory(NotificationCompat.CATEGORY_MESSAGE).setContentIntent(contentIntent);
                        notificationHelper.getManager().notify(1, nb.build());
                    }
                } catch (Exception e) {
                    Toast.makeText(personalContext, "Error In AlarmReceiver " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void SleepCheckService(){
        Intent LocationServiceIntent = new Intent(personalContext,SleepBackgroundService.class);
        LocationServiceIntent.putExtra("sleepingTime", Information.gSleepingTime);
        LocationServiceIntent.putExtra("sleepingHours", Information.gsleepTime);
        //context.startService(LocationServiceIntent);
        personalContext.startForegroundService(LocationServiceIntent);
    }

    private ArrayList<EventHelperClass> collecteventsubject(Map<String, Object> users) {
        eventList = new ArrayList<EventHelperClass>();
        EventHelperClass eventHelperClass;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            eventHelperClass = new EventHelperClass();
            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            eventHelperClass.eventsubject = (String) singleUser.get("eventsubject");
            eventHelperClass.eventdate = (String) singleUser.get("eventdate");
            eventHelperClass.eventtime = (String) singleUser.get("eventtime");
            eventList.add(eventHelperClass);
        }
        return eventList;
    }
    void startAlarm(Calendar c, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) personalContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(personalContext, AlertReceiver.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("eventSubject", Information.geventsubject);
        intent.putExtra("AlarmAccess", true);
        intent.putExtra("notificationType", "Alert");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(personalContext, requestCode, intent, 0);
        if (c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,1000,c.getTimeInMillis(),pendingIntent);*/
    }
}

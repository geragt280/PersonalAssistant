package com.example.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;


import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.*;

public class SleepBackgroundService extends Service {

    @Override
    public void onDestroy() {
        if (Information.gTotalInterruptionTime > 60) {
            Log.d("TAG", "Total interruption time is greater then 1 hour. i.e: " + Information.gTotalInterruptionTime);
            Information.gRequireRescheduling = true;
            Information.gReschedulingType = "Sleep Time Rescheduling";
        } else if (Information.gTotalInterruptionTime > 10) {
            Log.d("TAG", "Total interruption time is greater then 10 minutes. i.e: " + Information.gTotalInterruptionTime);
            Information.gRequireRescheduling = true;
            Information.gReschedulingType = "Sleep Time Rescheduling";
        }

        super.onDestroy();
    }

    @Override
    public void onCreate() {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                NotificationHelper notificationHelper = new NotificationHelper(this);
                Notification nb = notificationHelper.getChannel1Notification().setContentTitle("Sleepy Hour Started")
                        .setContentText("Sweet dreams ♥♥♥").build();

                startForeground(1, nb);
            }
            //Information.gStartTime = System.currentTimeMillis();
            //Toast.makeText(this, "SleepBackgroundService(onCreate)", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error in Service onCreate. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onCreate();

    }
    Handler handler;
    boolean exist = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            handler = new Handler() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public void handleMessage(@NonNull Message msg) {
                    checkActivity();
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    boolean isScreenOn = pm.isInteractive();
                    if (isScreenOn) {
                        if (Information.gInterruptionStartTime == 0) {
                            Information.gInterruptionStartTime = System.currentTimeMillis();
                            Toast.makeText(SleepBackgroundService.this, "Time save start of interruption.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Information.gInterruptionStartTime != 0 && Information.gInterruptionEndTime == 0){
                            Information.gInterruptionEndTime = System.currentTimeMillis();
                            long difference = (Information.gInterruptionEndTime - Information.gInterruptionStartTime);
                            long differenceInMins = (difference / 1000) / 60;
                            Information.gTotalInterruptionTime = Information.gTotalInterruptionTime + differenceInMins;
                            Information.gInterruptionEndTime = 0;
                            Information.gInterruptionStartTime = 0;
                            Log.d("TAG","Interruption time: " + difference + " Converted: " + differenceInMins);
                        }
                    }
                    Log.d("TAG", "Screen on: " + isScreenOn);
                    //Toast.makeText(SleepBackgroundService.this, "5 secs has passed." , Toast.LENGTH_SHORT).show();
                    super.handleMessage(msg);
                }
            };

            new Thread(new Runnable(){
                public void run() {
                    // TODO Auto-generated method stub
                    while(exist)
                    {
                        try {
                            Thread.sleep(5000);
                            handler.sendEmptyMessage(0);

                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }
            }).start();
            //Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        } catch (Exception E) {
            Toast.makeText(this, "SleepBackgroundService(IBinder). Error: " + E.getMessage(), Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    Calendar createSleepingHours(int hours,String date) {
        Calendar c = Calendar.getInstance();
        String[] arrDate = date.split("/");
        c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
        c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
        c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c;
    }

    void checkActivity(){
        Calendar currentTime = Calendar.getInstance();
        Date d = currentTime.getTime();
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(d);
        Date x = calendar3.getTime();
        String time = Information.gSleepingTime;
        String[] SleepingTime = time.split(":");
        String timeSleep = Information.gsleepTime;
        Calendar startTime = createSleepingHoursTill(time, 0, DateToday());
        Calendar endTime = createSleepingHoursTill(time, parseInt(timeSleep), DateToday());
        if (x.after(startTime.getTime()) && x.before(endTime.getTime())) {
            Log.d("TAG", "Time for sleeping in schedule. Current Time is = " + x.getHours() + ":" + x.getMinutes());
            //Toast.makeText(Information.gContext, "Time for sleeping in schedule. Current Time is = " + x.getHours() + ":" + x.getMinutes(), Toast.LENGTH_SHORT).show();
        } else if (parseInt(SleepingTime[0]) > 12) {
            startTime = createSleepingHours(Integer.parseInt(SleepingTime[0]), DateToday());
            Calendar till12 = createSleepingHoursTill("0:0", 24, DateToday());
            if (x.after(startTime.getTime()) && x.before(till12.getTime())) {
                Log.d("TAG", "Time for sleeping in schedule. Current Time is = " + x.getHours() + ":" + x.getMinutes());
                //Toast.makeText(Information.gContext, "Time for sleeping in schedule. Current Time is = " + x.getHours() + ":" + x.getMinutes(), Toast.LENGTH_SHORT).show();
            } else {
                exist =false;
                stopSelf();
            }
        } else {
            exist=false;
            stopSelf();
        }
    }

    Calendar createSleepingHoursTill(String time, int sleepingHours, String date) {
        Calendar c = Calendar.getInstance();
        String[] arrDate = date.split("/");
        String[] arrTime = time.split(":");
        c.set(Calendar.YEAR, parseInt(arrDate[2]));
        c.set(Calendar.MONTH, parseInt(arrDate[0]));
        c.set(Calendar.DATE, parseInt(arrDate[1]));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, parseInt(arrTime[1]));
        c.set(Calendar.SECOND, 0);
        if (12 < parseInt(arrTime[0])) {
            arrTime[0] = "" + (parseInt(arrTime[0]) - 24);
        }
        c.add(Calendar.HOUR_OF_DAY, parseInt(arrTime[0]));
        c.add(Calendar.HOUR_OF_DAY, sleepingHours);
        return c;
    }

    String DateToday(){
        String date = "", month = "", year = "";
        Date da_te = Calendar.getInstance().getTime();
        String dateToday = da_te.toString();
        String[] arr = dateToday.split(" ");
        if (arr[1].equals("January") || arr[1].equals("Jan")) {
            month = "0";
        } else if (arr[1].equals("February") || arr[1].equals("Feb")) {
            month = "1";
        } else if (arr[1].equals("March") || arr[1].equals("Mar")) {
            month = "2";
        } else if (arr[1].equals("April") || arr[1].equals("Apr")) {
            month = "3";
        } else if (arr[1].equals("May")) {
            month = "4";
        } else if (arr[1].equals("June") || arr[1].equals("Jun")) {
            month = "5";
        } else if (arr[1].equals("July") || arr[1].equals("Jul")) {
            month = "6";
        } else if (arr[1].equals("August") || arr[1].equals("Aug")) {
            month = "7";
        } else if (arr[1].equals("September") || arr[1].equals("Sept")) {
            month = "8";
        } else if (arr[1].equals("October") || arr[1].equals("Oct")) {
            month = "9";
        } else if (arr[1].equals("November") || arr[1].equals("Nov")) {
            month = "10";
        } else if (arr[1].equals("December") || arr[1].equals("Dec")) {
            month = "11";
        }
        date = arr[2];
        year = arr[5];
        return month + "/" + date + "/" + year;
    }

}

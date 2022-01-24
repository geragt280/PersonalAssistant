package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Recommendations {

    String Hour, Minutes, Day, Date;
    Context privateContext;

    public void BreakFastRecommendation(String time, Context context) {
        privateContext = context;
        String[] arr = time.split(":");
        Hour = arr[0];
        Minutes = arr[1];

        int DATA_FETCHER_RC = 111;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) privateContext.getSystemService(privateContext.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Log.d("Calender Instance time:", calendar.DATE + "/" + calendar.MONTH + "/" + calendar.YEAR + "Time: " + calendar.HOUR + ":" + calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(Minutes));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Intent intent = new Intent(privateContext, AlertReceiver.class);
        intent.putExtra("notificationType", "FoodAlert");
        intent.putExtra("eventSubject", "Breakfast Time Coming Soon.");
        intent.putExtra("requestCode", DATA_FETCHER_RC);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    void cancelBreakFastRecommendationAlarm() {
        int DATA_FETCHER_RC = 111;
        AlarmManager alarmManager = (AlarmManager) privateContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(privateContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, 0);

        alarmManager.cancel(pendingIntent);


    }

    public void LunchRecommendation(String time, Context context) {
        privateContext = context;
        String[] arr = time.split(":");
        Hour = arr[0];
        Minutes = arr[1];

        int DATA_FETCHER_RC = 222;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) privateContext.getSystemService(privateContext.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Log.d("Calender Instance time:", calendar.DATE + "/" + calendar.MONTH + "/" + calendar.YEAR + "Time: " + calendar.HOUR + ":" + calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(Minutes));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Intent intent = new Intent(privateContext, AlertReceiver.class);
        intent.putExtra("notificationType", "FoodAlert");
        intent.putExtra("eventSubject", "Lunch Time Coming Soon.");
        intent.putExtra("requestCode", DATA_FETCHER_RC);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    void cancelLunchRecommendationAlarm() {
        int DATA_FETCHER_RC = 222;
        AlarmManager alarmManager = (AlarmManager) privateContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(privateContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, 0);
        alarmManager.cancel(pendingIntent);


    }

    public void DinnerRecommendation(String time, Context context) {
        privateContext = context;
        String[] arr = time.split(":");
        Hour = arr[0];
        Minutes = arr[1];

        int DATA_FETCHER_RC = 333;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) privateContext.getSystemService(privateContext.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Log.d("Calender Instance time:", calendar.DATE + "/" + calendar.MONTH + "/" + calendar.YEAR + "Time: " + calendar.HOUR + ":" + calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(Minutes));
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, -15);

        Intent intent = new Intent(privateContext, AlertReceiver.class);
        intent.putExtra("notificationType", "FoodAlert");
        intent.putExtra("eventSubject", "Dinner Time Coming Soon.");
        intent.putExtra("requestCode", DATA_FETCHER_RC);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    void cancelDinnerRecommendationAlarm() {
        int DATA_FETCHER_RC = 333;
        AlarmManager alarmManager = (AlarmManager) privateContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(privateContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void TeaCoffeeRecommendation(String teaCoffeeInformation, Context context) {
        String[] TC = teaCoffeeInformation.split(" ");
        if (TC[0].equals("Tea") || TC[0].equals("Coffee")) {
            privateContext = context;

            String[] arr;
            if (TC.length >= 8) {
                arr = TC[8].split(":");
                setAlarm(arr[0], arr[1], 515, TC[0]);
            }
            if (TC.length >= 6) {
                arr = TC[6].split(":");
                setAlarm(arr[0], arr[1], 525, TC[0]);
            }
            if (TC.length >= 4) {
                arr = TC[4].split(":");
                setAlarm(arr[0],arr[1],535, TC[0]);
            }
            if (TC.length >= 2) {
                arr = TC[2].split(":");
                setAlarm(arr[0],arr[1],545, TC[0]);
            }
        }
    }

    void cancelTeaCoffeeRecommendation(String teaCoffeeInformation){
        String[] TC = teaCoffeeInformation.split(" ");
        if (TC.length >= 8) {
            cancelAlarm(515);
        }
        if (TC.length >= 6) {
            cancelAlarm(525);
        }
        if (TC.length >= 4) {
            cancelAlarm(535);
        }
        if (TC.length >= 2) {
            cancelAlarm(545);
        }
    }

    void cancelAlarm(int RC){
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Information.gContext, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }

    void setAlarm(String Hour, String Minutes, int DATA_FETCHER_RC, String DrinkType) {
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) privateContext.getSystemService(privateContext.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Log.d("Calender Instance time:", calendar.DATE + "/" + calendar.MONTH + "/" + calendar.YEAR + "Time: " + calendar.HOUR + ":" + calendar.MINUTE);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(Minutes));
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(privateContext, AlertReceiver.class);
        intent.putExtra("notificationType", "FoodAlert");
        intent.putExtra("eventSubject", "Its " + DrinkType + " Time.");
        intent.putExtra("requestCode", DATA_FETCHER_RC);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(privateContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void SleepRecommendation(String sleepingTime, String sleepingHours) {
        try {
            if (sleepingTime.equals(""))
                return;
            String[] arr = sleepingTime.split(":");
            Hour = arr[0];
            Minutes = arr[1];

            int DATA_FETCHER_RC = 1222;
            //Create an alarm manager
            AlarmManager mAlarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            Log.d("Calender Instance time:", calendar.DATE + "/" + calendar.MONTH + "/" + calendar.YEAR + "Time: " + calendar.HOUR + ":" + calendar.MINUTE);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(Hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(Minutes));
            calendar.set(Calendar.SECOND, 0);

            Intent intent = new Intent(Information.gContext, AlarmReceiver.class);
            intent.putExtra("ServiceType", "SleepService");
            intent.putExtra("requestCode", DATA_FETCHER_RC);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } catch (Exception e) {
            Toast.makeText(Information.gContext, "Recommendation(SleepRecommendation). Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(Information.gContext, "Recommendation(SleepRecommendation). Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void cancelSleepRecommendation() {
        int DATA_FETCHER_RC = 1222;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Information.gContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }

    public void AppointmentRecommendation() {

    }
}

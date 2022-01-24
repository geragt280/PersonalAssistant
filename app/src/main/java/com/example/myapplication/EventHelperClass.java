package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventHelperClass {

    public String eventsubject,eventtime,eventdate,eventdescription,eventlocationname, eventAlarmRequestCode, eventRingtone;
    public float eventlocationlongitude, eventlocationlatitude;
    FirebaseDatabase database;
    DatabaseReference reference;

    public EventHelperClass() {
    }

    public EventHelperClass(String geventsubject, String geventtime ,String geventdate, String geventdescription, String geventlocation , float longitude, float latitude, String requestCode, String ringtone) {
        this.  eventsubject= geventsubject;
        this.  eventtime=geventtime;
        this.    eventdescription= geventdescription;
        this.  eventdate=geventdate;
        this.eventlocationname=geventlocation;
        this.eventAlarmRequestCode = requestCode;
        eventlocationlatitude = latitude;
        eventlocationlongitude = longitude;
        this.eventRingtone = ringtone;
    }

    public void SetEvent(String eventSubject, String eventTime ,String eventDate, String eventDescription, String eventLocation , float longitude, float latitude, String requestCode, String ringtone){
        try{
            database = FirebaseDatabase.getInstance();
            //  myRef2 = database2.getReference("users");
            reference = database.getReference("users/" + Information.gusername + "/events/");
            EventHelperClass eventHelperClass = new EventHelperClass(eventSubject, eventTime, eventDate, eventDescription, eventLocation, longitude, latitude, requestCode, ringtone);
            reference.child(eventSubject).setValue(eventHelperClass);

            //// Setting alarm code

            Calendar c = Calendar.getInstance();
            String[] arrDate = Information.geventdate.split("/");
            String[] arrTime = Information.geventtime.split(":");
            c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
            c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
            c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
            c.set(Calendar.SECOND, 0);
            startAlarm(c, Integer.parseInt(requestCode));
            if (Information.gReschedulingType.equals(""))
                startAlarmService(c, Integer.parseInt(requestCode));

        }catch (Exception e){
            Toast.makeText(Information.gContext, "EventHelperClass(SetEvent). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void startAlarm(Calendar c, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Information.gContext, AlertReceiver.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("eventSubject", Information.geventsubject);
        intent.putExtra("AlarmAccess", true);
        intent.putExtra("notificationType", "Alarm");
        intent.putExtra("ringtoneUrl", Information.gringToneUrl);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, requestCode, intent, 0);
        if (c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,1000,c.getTimeInMillis(),pendingIntent);*/
    }

    void startAlarmService(Calendar c, int requestCode){
        requestCode++;
        c.add(Calendar.MINUTE, -15);
        AlarmManager alarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Information.gContext, AlertReceiver.class);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("eventLocation", Information.glocationlatitude + "," + Information.glocationlongitude);
        intent.putExtra("notificationType", "EventLocationService");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, requestCode, intent, 0);
        if (c.before(Calendar.getInstance()))
            c.add(Calendar.DATE, 1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //Toast.makeText(this, "Event Planned Time: " + c.getTime(), Toast.LENGTH_SHORT).show();

    }
}

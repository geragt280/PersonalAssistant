package com.example.myapplication;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class LocationBackgroundService extends Service implements LocationListener {

    String locationLat,locationLng;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "onBind.", Toast.LENGTH_SHORT).show();
        try{


        }catch (Exception e){
            Toast.makeText(this, "Error in Service onBind. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    LocationManager locationManager;

    Location currentLocation;



    @Override
    public void onCreate() {
        Toast.makeText(this, "onCreate.", Toast.LENGTH_SHORT).show();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                String CHANNEL_ID = "my_channel_01";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("LocationBackgroundService")
                        .setContentText("Working..").build();

                startForeground(1, notification);
            }
            
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Information.gRingtone = RingtoneManager.getRingtone(this, alarmUri);

        }catch (Exception e){
            Toast.makeText(this, "Error in Service onCreate. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        super.onCreate();
    }

    double radian(double x) {
        return x * Math.PI / 180;
    };

    double DistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {
        int R = 6378137; // Earth’s mean radius in meter
        double dLat = radian(lat2 - lat1);
        double dLong = radian(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(radian(lat1)) * Math.cos(radian(lat2)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter
    };

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Main barbad hogya hun. ", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    String location;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            Toast.makeText(this, "onStartCommand. ", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return START_STICKY;
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);

            location = intent.getStringExtra("location");
            String[] locationArr = location.split(",");
            locationLat = locationArr[0];
            locationLng = locationArr[1];

            

        }catch (Exception e){
            Toast.makeText(this, "Error in Service onStartCommand. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return Service.START_STICKY;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try{
            double distance;
            currentLocation = location;
            //Toast.makeText(this, "onStartCommand. LatLng: " + location, Toast.LENGTH_SHORT).show();
            if (locationLat != null && locationLng != null) {
                distance = DistanceBetweenTwoPoints(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.parseDouble(locationLat), Double.parseDouble(locationLng));
            } else {
                distance = 0;
            }
            Toast.makeText(this, "Distance: " + distance, Toast.LENGTH_SHORT).show();
            if (distance >= 100 && !Information.gRingtone.isPlaying()) {
                Information.gRingtone.play();
            } else if (distance < 100) {
                Information.gRingtone.stop();
                locationManager.removeUpdates(this);
                locationManager = null;
                stopSelf();

            }
        }catch (Exception e){
            Toast.makeText(this, "Error in Service onLocationChanged. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}

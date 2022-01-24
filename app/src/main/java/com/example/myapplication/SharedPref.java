package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static final String PREF_NAME = "chatroom.shared.pref";
    public static final String PREF_KEY = "chatroom.shared.username";
    public static final String PREF_KEY_recent_schedule_time = "fyp.shared.recent.time";
    public static final String PREF_KEY_recent_schedule_name = "fyp.shared.recent.name";
    public static final String PREF_KEY_upcoming_schedule_time = "fyp.shared.upcoming.time";
    public static final String PREF_KEY_upcoming_schedule_name = "fyp.shared.upcoming.name";
    public static final String PREF_KEY_home_location = "fyp.shared.home.location";
    public static final String PREF_KEY_current_location = "fyp.shared.current.location";


    public SharedPref() {
    }

    public void save(Context context, String text) {
        SharedPreferences sharePref;
        SharedPreferences.Editor editor;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharePref.edit();
        editor.putString(PREF_KEY,text);
        editor.apply();
    }

    public String getData(Context context) {
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY,null);
        return text;
    }

    public void saveScheduleTimeForWidget(Context context, String recentName, String recentTime, String upcomingName, String upcomingTime){
        SharedPreferences sharePref;
        SharedPreferences.Editor editor;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharePref.edit();
        editor.putString(PREF_KEY_recent_schedule_time,recentTime);
        editor.putString(PREF_KEY_recent_schedule_name,recentName);
        editor.putString(PREF_KEY_upcoming_schedule_time,upcomingTime);
        editor.putString(PREF_KEY_upcoming_schedule_name,upcomingName);
        editor.apply();
    }

    public void saveHomeLocation(Context context, String locationText) {
        SharedPreferences sharePref;
        SharedPreferences.Editor editor;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharePref.edit();
        editor.putString(PREF_KEY_home_location,locationText);
        editor.apply();
    }

    public String getPREF_KEY_home_location(Context context)
    {
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_home_location,null);
        return text;
    }

    public void saveCurrentLocation(Context context, String locationText) {
        SharedPreferences sharePref;
        SharedPreferences.Editor editor;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharePref.edit();
        editor.putString(PREF_KEY_current_location,locationText);
        editor.apply();
    }

    public String getPREF_KEY_current_location(Context context)
    {
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_current_location,null);
        return text;
    }

    public String getPREF_KEY_recent_schedule_time(Context context){
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_recent_schedule_time,null);
        return text;
    }

    public String getPREF_KEY_recent_schedule_name(Context context){
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_recent_schedule_name,null);
        return text;
    }

    public String getPREF_KEY_upcoming_schedule_time(Context context){
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_upcoming_schedule_time,null);
        return text;
    }

    public String getPREF_KEY_upcoming_schedule_name(Context context){
        SharedPreferences sharePref;
        String text;
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        text = sharePref.getString(PREF_KEY_upcoming_schedule_name,null);
        return text;
    }

}

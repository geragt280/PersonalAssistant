package com.example.myapplication;

import android.content.Context;
import android.media.Ringtone;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Information {
    public static String gname, ggender, gusername, gpw, gteatime, gemail, gphoneNo, gbirthDate, gsleepTime , glunchTime, gbreakfastTime, gdinner, gSleepingTime, galarmRequest_Code;
    public static String gringToneUrl;
    public static boolean update = false, gVoiceActive = false, firstTimeLogin = true, gRequireRescheduling = false, gFlag = false;
    public static Ringtone gRingtone;
    public static String gCurrentCoordinates, gReschedulingType;
    public static String geventsubject, geventtime, geventdate, geventdiscription, geventlocation, gEventVoiceDate, gEventVoiceTime;
    public static String gAppointmentSubject, gAppointmentTime, gAppointmentDate, gAppointmentDescription, gAppointmentType, getAppointmentDay;
    public static float glocationlatitude, glocationlongitude;
    public static Context gContext;
    public static String gOccasion, gActivityType;
    public static TextToSpeech mtts;
    public static ArrayList<LogHelperClass> logsList = new ArrayList<LogHelperClass>();
    public static ArrayList<String> gEventSubjectString = new ArrayList<String>();
    public static int counter=0, todayResult=0, gTemp=0, gTotalResultPoints;
    public static Scheduler gTodayScheduler, gSomedaySchedule;
    public static ArrayList<Calendar> gFreeTimeCalender = new ArrayList<Calendar>(), gAllCalenderArray = new ArrayList<Calendar>();
    public static ArrayList<String> gFreeTimeCalenderString = new ArrayList<String>();
    public static long gStartTime = 0, gEndTime = 0, gInterruptionStartTime = 0, gInterruptionEndTime = 0, gTotalInterruptionTime = 0;
    public static FragmentManager gFragment;

    public static void speak(String text){
        try{
            mtts.setPitch(1f);
            mtts.setSpeechRate(0.8f);
            mtts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
        }catch (Exception e){

        }
    }

}

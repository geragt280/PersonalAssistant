package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.se.omapi.Session;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.common.base.Objects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.mortbay.jetty.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class MainEvent extends AppCompatActivity implements LocationListener, SelectSingleItemDialog.SingleChoiceListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    Button button_v_today_event, AddEvent, AddAppointment, viewevent, MapRedirectButton, Today_Progress;
    TextView tv_currentTime, tv_upcomingTime, tv_recent_name, tv_upcoming_name;
    private static final String TAG = "MyActivity";
    ArrayList<EventHelperClass> eventList;
    public static ArrayList<EventHelperClass> todaysEventList;
    ArrayList<Calendar> calendars;
    public static int todayEvent;
    private boolean mLocationPermissionGranted = false;
    Toolbar toolbar1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    ImageButton mSpeakBtn;
    UserHelperClass userHelperClass = new UserHelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_event);

        userHelperClass.getUserInformation(Information.gusername);

        //counting today events.
        countTodayEvent();

        if (Information.firstTimeLogin){
            try {
                Information.firstTimeLogin = false;
                Information.speak("Welcome! " + Information.gusername);
                Information.gContext = this;
                Information.gFragment = getSupportFragmentManager();
                //checking map services code start
                if (checkMapServices()) {
                    if (mLocationPermissionGranted) {
                        getLastKnownLocation();
                    } else {
                        getLocationPermission();
                    }
                }
                //Alert for Total events of day
                createAlertForEveryDay();
                doFirstTimeOpenThingsWithDelay();

            } catch (Exception e) {
                Toast.makeText(this, "error in login startup. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        //do every time with delay
        doEveryTimeThingsWithDelay();



        toolbar1 = findViewById(R.id.toolbaar);
        setSupportActionBar(toolbar1);
        try {
            if (Information.gVoiceActive){
                Information.speak("Do you want to change location?");
                Thread.sleep(2000);
                startTakingVoiceWithADelay();
            }

            mSpeakBtn = (ImageButton) findViewById(R.id.button_voice_activation);
            mSpeakBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    {
                        Information.gVoiceActive = true;
                        Information.speak("Hello sir how can i help you.");
                        startTakingVoiceWithADelay();
                    }
                }
            });

            if (Information.gRingtone != null) {
                Information.gRingtone.stop();
                Information.gRingtone = null;
            }

            //Make Schedule For Today Events
            Information.gTodayScheduler = new Scheduler();

            //Make Schedule For Tomorrow
            Information.gSomedaySchedule = new Scheduler(DateTomorrow());

            MapRedirectButton = findViewById(R.id.button_redirect_to_maps);
            MapRedirectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RedirectSuggestToMaps();
                }
            });

            //used buttons and utilities initialization
            button_v_today_event = findViewById(R.id.button_view_todays_event);
            button_v_today_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainEvent.this, ViewTodaysEvents.class);
                    startActivity(i);
                }
            });

            viewevent = findViewById(R.id.button_view_event);
            viewevent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Information.gActivityType = "Event";
                    Intent intent = new Intent(MainEvent.this, EventListing.class);
                    startActivity(intent);
                }
            });

            AddAppointment = findViewById(R.id.button_add_appiontments);
            AddAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Information.gActivityType = "Appointment";
                    Information.update = false;
                    Intent i = new Intent(MainEvent.this, NewEvent.class);
                    startActivity(i);
                }
            });
            AddEvent = findViewById(R.id.button_add_event);
            AddEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEvent();
                }
            });

            Today_Progress = findViewById(R.id.btn_today_progress);
            Today_Progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProgressDialog();
                }
            });

            tv_currentTime = findViewById(R.id.tv_current_time);
            tv_upcomingTime = findViewById(R.id.tv_up_coming_time);
            tv_recent_name = findViewById(R.id.tv_recent_name);
            tv_upcoming_name = findViewById(R.id.tv_upcoming_name);

            SharedPref sp = new SharedPref();

            String recentName = sp.getPREF_KEY_recent_schedule_name(this),recentTime = sp.getPREF_KEY_recent_schedule_time(this),upcomingName = sp.getPREF_KEY_upcoming_schedule_name(this), upcomingTime = sp.getPREF_KEY_upcoming_schedule_time(this);
            if (recentName != null && recentTime != null || upcomingName != null || upcomingTime != null){
                tv_currentTime.setText(recentTime);
                tv_recent_name.setText(recentName);
                tv_upcoming_name.setText(upcomingName);
                tv_upcomingTime.setText(upcomingTime);
            }

            //  user_update_label = findViewById(R.id.login_user);
            // user_update_label.setText("Welcome : " + Information.gusername);
            eventList = new ArrayList<EventHelperClass>();
            todaysEventList = new ArrayList<EventHelperClass>();
            calendars = new ArrayList<Calendar>();

        } catch (
                Exception e) {
            Toast.makeText(this, "Error in EventMain. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    Dialog myDialog;
    void openProgressDialog(){

        FirebaseDatabase databaseTotalPoints = FirebaseDatabase.getInstance();
        DatabaseReference referenceTotalPoints = databaseTotalPoints.getReference("users/"+Information.gusername+"/totalResultingPoints/");
        referenceTotalPoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    final Integer value = snapshot.getValue(Integer.class);
                    if (isNumeric(value.toString())){
                        Information.gTotalResultPoints = value;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase databaseResultingPoints = FirebaseDatabase.getInstance();
        DatabaseReference referenceResultingPoints = databaseResultingPoints.getReference("users/"+Information.gusername+"/resultingPoints/");
        referenceResultingPoints.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    final Integer value = snapshot.getValue(Integer.class);
                    if (isNumeric(value.toString())){
                        Information.todayResult = value;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myDialog = new Dialog(this);
        makeProgressWithADelay();
    }

    void startTakingVoiceWithADelay(){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //this method will be called after 2 seconds
                startVoiceInput();
            }
        };
        final Handler h = new Handler();
        h.removeCallbacks(runnable); // cancel the running action (the
        // hiding process)
        h.postDelayed(runnable, 2000);
    }

    void makeProgressWithADelay(){
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //this method will be called after 2 seconds
                myDialog.setContentView(R.layout.progress_dialog);
                LinearLayout layout = (LinearLayout) myDialog.findViewById(R.id.score_layout);
                ImageView ImgView = (ImageView) myDialog.findViewById(R.id.progress_image);
                TextView txtClose =(TextView) myDialog.findViewById(R.id.txtclose);
                TextView txtProgressDetails =(TextView) myDialog.findViewById(R.id.progress_tv);
                TextView scoreTextView =(TextView) myDialog.findViewById(R.id.score_tv);
                txtClose.setText("X");
                String total;
                if (Information.todayResult != 0 && Information.gTotalResultPoints != 0){
                    float num1 = Information.todayResult;
                    float num2 = Information.gTotalResultPoints;
                    float flu = (num1/num2)*100;
                    total = "" + (flu);
                    if (flu<50){
                        layout.setBackgroundColor(Color.RED);
                        ImgView.setImageResource(R.drawable.notgoodimage);
                        txtProgressDetails.setText("Today's progress was unsatisfactory.");
                    }
                    scoreTextView.setText("Total Performance is " + total + "%");
                    //Toast.makeText(MainEvent.this, Information.todayResult+","+Information.gTotalResultPoints, Toast.LENGTH_SHORT).show();
                }
                Button btnOk = (Button) myDialog.findViewById(R.id.btn_follow);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
            }
        };
        final Handler h = new Handler();
        h.removeCallbacks(runnable); // cancel the running action (the
        // hiding process)
        h.postDelayed(runnable, 3000);
    }

    void doFirstTimeOpenThingsWithDelay(){
        try {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (Information.gSleepingTime != null){
                        //this method will be called after 2 seconds
                        Toast.makeText(MainEvent.this, "I am inside here after delay. sleeping time is " + Information.gSleepingTime + " sleeping hours are " + Information.gsleepTime, Toast.LENGTH_LONG).show();
                        Recommendations rec = new Recommendations();
                        rec.SleepRecommendation(Information.gSleepingTime, Information.gsleepTime);

                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.SECOND, 0);
                        if (c1.get(Calendar.MINUTE) >= 30)
                        {
                            c1.add(Calendar.HOUR_OF_DAY,1);
                            c1.set(Calendar.MINUTE,0);
                        }
                        else
                            c1.set(Calendar.MINUTE, 30);
                        Calendar c2 = Calendar.getInstance();
                        c2.set(Calendar.HOUR_OF_DAY, 0);
                        c2.set(Calendar.MINUTE, 0);
                        c2.set(Calendar.SECOND, 0);
                        c2.add(Calendar.DATE, 1);

                        boolean exist = Information.gTodayScheduler.makeFreeTimeSchedule(c1,0);
                        while (true){
                            Date x = c2.getTime();
                            if (x.after(c1.getTime())){
                                if (exist){
                                    c1.add(Calendar.HOUR_OF_DAY,1);
                                }else {
                                    c1.add(Calendar.MINUTE,30);
                                }
                                exist = Information.gTodayScheduler.makeFreeTimeSchedule(c1,0);
                            }
                            else
                                break;
                        }
                    }
                    Recommendations rec = new Recommendations();
                    if (Information.gbreakfastTime != null && Information.glunchTime != null && Information.gdinner != null){
                        Toast.makeText(MainEvent.this, "Meal Recommendation Started", Toast.LENGTH_SHORT).show();
                        rec.BreakFastRecommendation(Information.gbreakfastTime, MainEvent.this);
                        rec.LunchRecommendation(Information.glunchTime, MainEvent.this);
                        rec.DinnerRecommendation(Information.gdinner, MainEvent.this);
                        rec.TeaCoffeeRecommendation(Information.gteatime, MainEvent.this);
                    }
                }
            };
            final Handler h = new Handler();
            h.removeCallbacks(runnable); // cancel the running action (the
            // hiding process)
            h.postDelayed(runnable, 5000);
        }catch (Exception e){
            Toast.makeText(this, "MainEvent(doFirstTimeWithDelay)", Toast.LENGTH_SHORT).show();
        }
    }

    void setMainScreenWidgetTime(){
        try {
            ArrayList<String> tempStrArr = new ArrayList<String>();

            int count = 0;
            for (int i=0; i<Information.gAllCalenderArray.size(); i++){
                if (count > 1){
                    break;
                }
                if (Information.gAllCalenderArray.get(i).getTime().after(Calendar.getInstance().getTime())){
                    tempStrArr.add(Information.gEventSubjectString.get(i));
                    count++;
                }

            }

            String[] recent = tempStrArr.get(0).split(" : ");
            String[] upcoming = tempStrArr.get(1).split(" : ");

            String[] time = recent[1].split(":");

            time[0] = giveDoubleDigit(time[0]);
            time[1] = giveDoubleDigit(time[1]);

            String recentName = recent[0];
            String recentTime = time[0]+":"+time[1];

            tv_currentTime.setText(time[0]+":"+time[1]);

            time = upcoming[1].split(":");

            time[0] = giveDoubleDigit(time[0]);
            time[1] = giveDoubleDigit(time[1]);

            SharedPref sp = new SharedPref();//   upcomingName,  upcomingTime,  afterthatName, afterthatTime
            sp.saveScheduleTimeForWidget(this, recentName, recentTime, upcoming[0], time[0]+":"+time[1]);

            tv_upcomingTime.setText(time[0]+":"+time[1]);
            tv_recent_name.setText(recent[0]);
            tv_upcoming_name.setText(upcoming[0]);
        }catch (Exception E){
            Toast.makeText(this, "MainEvent(setMainScreenWidgetTime). Error: " + E.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    String giveDoubleDigit(String stringNum){
        int num = Integer.parseInt(stringNum);
        if (num < 10){
            return "0" + stringNum;
        }
        return stringNum;
    }

    void doEveryTimeThingsWithDelay() {
        try {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Information.gTodayScheduler.allActivityCalender();
                    setMainScreenWidgetTime();
                    if (Information.gRequireRescheduling){
                        if (Information.gReschedulingType.equals("Sleep Time Rescheduling")){
                            OpenRescheduleDialog();
                            Information.gRequireRescheduling = false;
                        }
                    }
                }
            };
            final Handler h = new Handler();
            h.removeCallbacks(runnable); // cancel the running action (the
            // hiding process)
            h.postDelayed(runnable, 5000);
        }catch (Exception e){
            Toast.makeText(this, "MainEvent(doEveryTimeWithDelay)", Toast.LENGTH_SHORT).show();
        }
    }

    String DateTomorrow(){
        String date = "", month = "", year = "";
        Date da_te = Calendar.getInstance().getTime();
        String dateToday = da_te.toString();
        String[] arr = dateToday.split(" ");
        month = getMonthNumber(arr);
        date = arr[2];
        year = arr[5];
        return month + "/" + (Integer.parseInt(date)+1) + "/" + year;
    }

    String DateToday(){
        String date = "", month = "", year = "";
        Date da_te = Calendar.getInstance().getTime();
        String dateToday = da_te.toString();
        String[] arr = dateToday.split(" ");
        month = getMonthNumber(arr);
        date = arr[2];
        year = arr[5];
        return month + "/" + date + "/" + year;
    }

    String getDateNumber(String[] arr) {
        String date;
        if (arr[0].equals("1st")) {
            date = "1";
        } else if (arr[0].equals("2nd")) {
            date = "2";
        } else if (arr[0].equals("3rd")) {
            date = "3";
        } else if (arr[0].equals("4th")) {
            date = "4";
        } else if (arr[0].equals("5th")) {
            date = "5";
        } else if (arr[0].equals("6th")) {
            date = "6";
        } else if (arr[0].equals("7th")) {
            date = "7";
        } else if (arr[0].equals("8th")) {
            date = "8";
        } else if (arr[0].equals("9th")) {
            date = "9";
        } else if (arr[0].equals("10th")) {
            date = "10";
        } else if (arr[0].equals("11th")) {
            date = "11";
        } else if (arr[0].equals("12th")) {
            date = "12";
        } else if (arr[0].equals("13th")) {
            date = "13";
        } else if (arr[0].equals("14th")) {
            date = "14";
        } else if (arr[0].equals("15th")) {
            date = "15";
        } else if (arr[0].equals("16th")) {
            date = "16";
        } else if (arr[0].equals("17th")) {
            date = "17";
        } else if (arr[0].equals("18th")) {
            date = "18";
        } else if (arr[0].equals("19th")) {
            date = "19";
        } else if (arr[0].equals("20th")) {
            date = "20";
        } else if (arr[0].equals("21st")) {
            date = "21";
        } else if (arr[0].equals("22nd")) {
            date = "22";
        } else if (arr[0].equals("23rd")) {
            date = "23";
        } else if (arr[0].equals("24th")) {
            date = "24";
        } else if (arr[0].equals("25th")) {
            date = "25";
        } else if (arr[0].equals("26th")) {
            date = "26";
        } else if (arr[0].equals("27th")) {
            date = "27";
        } else if (arr[0].equals("28th")) {
            date = "28";
        } else if (arr[0].equals("29th")) {
            date = "29";
        } else if (arr[0].equals("30th")) {
            date = "30";
        } else date = "";
        return date;
    }

    String getMonthNumber(String[] arr){
        String month;
        if (arr[1].equals("January") || arr[1].equals("Jan")) {
            month = "0";
        } else if (arr[1].equals("February") || arr[1].equals("Feb")) {
            month = "1";
        } else if (arr[1].equals("March") || arr[1].equals("Mar")) {
            month = "2";
        } else if (arr[1].equals("April") || arr[1].equals("Apr")) {
            month = "3";
        } else if (arr[1].equals("May") || arr[1].equals("Mets") || arr[1].equals("Mats")) {
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
        else month = "";
        return month;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final SharedPref sp = new SharedPref();
        switch (item.getItemId()) {
            case R.id.check_schedule: {
                Intent i = new Intent(MainEvent.this, ScheduleLists.class);
                startActivity(i);
                break;
            }
            case R.id.settings: {
                Toast.makeText(this, "Make app setting layout for me.", Toast.LENGTH_SHORT).show();
                OpenRescheduleDialog();
                break;
            }
            case R.id.logout: {
                sp.save(MainEvent.this, null);
                Information.speak("you have been logout to this account.! sign-up and create new account");
                Intent intent = new Intent(MainEvent.this, LoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.edit: {
                Toast.makeText(getApplicationContext(), "this is your settings", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainEvent.this, SettingActivity.class);
                startActivity(i);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void countTodayEvent() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        todayEvent = 0;
                        collectEventSubject((Map<String, Object>) snapshot.getValue());
                        if (eventList.size() > 0) {
                            for (int i = 0; i < eventList.size(); i++) {
                                Random rdm = new Random();
                                int num = rdm.nextInt() / 1000000;
                                num = num * num;
                                Information.galarmRequest_Code = "" + num;
                                Calendar c = Calendar.getInstance();
                                try {
                                    Information.geventdate = eventList.get(i).eventdate;
                                    Information.geventtime = eventList.get(i).eventtime;
                                } catch (Exception e) {
                                    Toast.makeText(MainEvent.this, "Error in making arr time and date.", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    String[] arrDate = Information.geventdate.split("/");
                                    String[] arrTime = Information.geventtime.split(":");
                                    c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
                                    c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
                                    c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
                                    c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
                                    c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
                                    c.set(Calendar.SECOND, 0);
                                } catch (Exception e) {
                                    Toast.makeText(MainEvent.this, "Error in saving arr into calender.", Toast.LENGTH_SHORT).show();
                                }
                                //Toast.makeText(MainEvent.this, c.get(Calendar.DATE)+","+date.getDate(), Toast.LENGTH_SHORT).show();
                                Date date = Calendar.getInstance().getTime();
                                if (c.get(Calendar.DATE) == date.getDate()) {
                                    //Toast.makeText(MainEvent.this, "Mene + + kra ha ", Toast.LENGTH_SHORT).show();
                                    EventHelperClass today_event = eventList.get(i);
                                    todaysEventList.add(today_event);
                                    todayEvent++;
                                    continue;
                                }
                            }
                            //Toast.makeText(MainEvent.this, "We have " + todayEvent + " Events for today.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(MainEvent.this, "Error in Counting errors. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    viewevent.setVisibility(View.INVISIBLE);
                    button_v_today_event.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    void RedirectSuggestToMaps() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=fun+places+with+family"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error in MainEvent(RedirectToMaps) Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void OpenRescheduleDialog(){
        DialogFragment singleChoiceDialog = new SelectSingleItemDialog();
        singleChoiceDialog.setCancelable(false);
        singleChoiceDialog.show(getSupportFragmentManager(), "Personal Assistant");

    }


    private void openEvent() {
        Information.gActivityType = "Event";
        Information.update = false;
        Intent intent = new Intent(MainEvent.this, NewEvent.class);
        startActivity(intent);
    }

    //Check location enabled button starts here
    private boolean checkMapServices() {
        if (isServicesOK()) {
            //Toast.makeText(this, "Google Service OK", Toast.LENGTH_SHORT).show();
            if (isMapsEnabled()) {
                //Toast.makeText(this, "Map Enabled", Toast.LENGTH_SHORT).show();
                return true;
            } else
                Toast.makeText(this, "Map Not Enabled", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Google Service Not OK", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean isServicesOK() {
        Log.d("TAG", "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainEvent.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d("TAG", "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d("TAG", "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainEvent.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    LocationManager locationManager;

    void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called.");
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);
        } catch (Exception e) {
            Toast.makeText(this, "Error in getting last known location. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Information.gCurrentCoordinates = location.getLatitude() + "," + location.getLongitude();
        SharedPref sp = new SharedPref();
        sp.saveCurrentLocation(this, Information.gCurrentCoordinates);
        if (sp.getPREF_KEY_home_location(this)==null){
            androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to save this location as your home location?");
            alertDialogBuilder.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            try {
                                SharedPref sp = new SharedPref();
                                sp.saveHomeLocation(MainEvent.this, Information.gCurrentCoordinates);
                            } catch (Exception e) {
                                Toast.makeText(MainEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainEvent.this, "We will do it later.", Toast.LENGTH_LONG).show();
                }
            });
            androidx.appcompat.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        //Toast.makeText(this, "Current Location: " + Information.gCurrentCoordinates, Toast.LENGTH_SHORT).show();
        locationManager.removeUpdates(this);
        locationManager = null;
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    String currentSay="";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionGranted) {
                    getLastKnownLocation();
                } else {
                    getLocationPermission();
                }
            }
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    currentSay = result.get(0);
                }

                if ((currentSay.equals("Set An Event") || currentSay.equals("set an event")) || Information.counter > 0) {
                    if (Information.counter == 1) {
                        //save event subject
                        Information.geventsubject = currentSay;
                        Information.speak("Did I get it right?");
                        //Toast.makeText(this, "Did I get it right?", Toast.LENGTH_SHORT).show();
                        startTakingVoiceWithADelay();
                        Information.counter++;
                    }
                    if (Information.counter < 1) {
                        Information.counter++;
                        //ask for event subject and save it.
                        Information.speak("What is your event subject");
                        //Toast.makeText(this, "What is your event subject", Toast.LENGTH_SHORT).show();
                        startTakingVoiceWithADelay();
                    }
                    if (currentSay.equals("Yes") || currentSay.equals("yes") || Information.counter > 2) {
                        // Here we will set event subject.
                        //Toast.makeText(this, "Count: " + counter, Toast.LENGTH_SHORT).show();
                        if (Information.counter == 3) {
                            //save date here
                            saveDateWithVoice(currentSay);
                            Information.speak("Did I get the date right?");
                            //Toast.makeText(this, "Did I get the date right?", Toast.LENGTH_SHORT).show();
                            startTakingVoiceWithADelay();
                            Information.counter++;
                        }
                        if (Information.counter < 3) {
                            Information.speak("what is the date of event.");
                            //Toast.makeText(this, "what is the date of event.", Toast.LENGTH_SHORT).show();
                            Information.counter++;
                            startTakingVoiceWithADelay();
                            currentSay = "";
                        }
                        if (currentSay.equals("yes") || currentSay.equals("Yes") || Information.counter > 4) {
                            if (Information.counter == 5) {
                                //save time here
                                saveTimeWithVoice(currentSay);
                                Information.speak("Did I get the time right?");
                                //Toast.makeText(this, "Did I get the time right?", Toast.LENGTH_SHORT).show();
                                startTakingVoiceWithADelay();
                                Information.counter++;
                            }
                            if (Information.counter < 5) {
                                Information.speak("what is the time for event.");
                                //Toast.makeText(this, "what is time for event.", Toast.LENGTH_SHORT).show();
                                Information.counter++;
                                startTakingVoiceWithADelay();
                                currentSay = "";
                            }
                            if (currentSay.equals("yes") || currentSay.equals("Yes") || Information.counter > 6) {
                                if (Information.counter == 7) {
                                    //take to the location selection
                                    //Information.speak("Do you want to change location?");
                                    //Toast.makeText(this, "Did you want to change location?", Toast.LENGTH_SHORT).show();
                                    //startVoiceInput();
                                    Information.counter++;
                                }
                                if (Information.counter < 7) {
                                    //Information.speak("Select the location of event.");
                                    //Toast.makeText(this, "Select the location of event.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainEvent.this, Locations.class);
                                    startActivity(intent);
                                    Information.counter++;
                                    //startVoiceInput();
                                    currentSay = "";
                                }
                                if (currentSay.equals("no") || currentSay.equals("No") || Information.counter > 8 || currentSay.equals("now")) {
                                    // all information completed generate event.
                                    if (Information.counter < 9) {
                                        Information.speak("Do you want to make this event or do you want to discard it.");
                                        startTakingVoiceWithADelay();
                                        Information.counter++;
                                    }
                                    if (currentSay.equals("make") || currentSay.equals("make it") || currentSay.equals("make this event")) {
                                        Information.speak(" The Event has been generated. Thank you for you co-operation.");
                                        Random rdm = new Random();
                                        int num = rdm.nextInt() / 1000000;
                                        num = num * num;
                                        Information.galarmRequest_Code = "" + num;
                                        EventHelperClass eventHelperClass = new EventHelperClass();
                                        eventHelperClass.SetEvent(Information.geventsubject, Information.gEventVoiceTime, Information.gEventVoiceDate, Information.geventdiscription, Information.geventlocation, Information.glocationlongitude, Information.glocationlatitude, Information.galarmRequest_Code, Information.gringToneUrl);
                                        Information.gVoiceActive = false;
                                        //Toast.makeText(this, "Event has been generated.", Toast.LENGTH_SHORT).show();
                                        Information.counter = 0;
                                    } else if (currentSay.equals("discard") || currentSay.equals("Discard it") || currentSay.equals("discard it")) {
                                        Information.speak("All the event information has been discarded.");
                                        Information.gVoiceActive = false;
                                        Information.counter = 0;
                                    }
                                } else if (currentSay.equals("yes") || currentSay.equals("Yes")) {
                                    //goto locations once again
                                    //startVoiceInput();
                                    Information.counter--;
                                    Intent intent = new Intent(MainEvent.this, Locations.class);
                                    startActivity(intent);
                                }
                            } else if (currentSay.equals("no") || currentSay.equals("No")) {
                                Information.speak("Tell me the time again.");
                                //ask for reenter time
                                startTakingVoiceWithADelay();
                                Information.counter--;
                            }
                        } else if (currentSay.equals("no")) {
                            //ask for recall date
                            Information.speak("Tell me the date again.");
                            startTakingVoiceWithADelay();
                            Information.counter--;
                        }
                    } else if (currentSay.equals("no")) {
                        //ask for recall subject
                        Information.speak("Tell me the subject again.");
                        startTakingVoiceWithADelay();
                        Information.counter--;
                    }
                }
                break;
            }
        }

    }

    void saveDateWithVoice(String dateAndMonth) {
        try {
            String date = "", month = "", year = "";
            String[] arr = dateAndMonth.split(" ");
            if (arr.length > 1){
                if (isNumeric(arr[0])) {
                    date = (arr[0]);
                }
                else
                    date = getDateNumber(arr);
                month = getMonthNumber(arr);
                Date dateForYear = Calendar.getInstance().getTime();
                arr = dateForYear.toString().split(" ");
                String Year = (arr[5]);
                arr = Year.split("/");
                year = (arr[0]);
                Information.gEventVoiceDate = month + "/" + date + "/" + year;
                Toast.makeText(this, "" + Information.gEventVoiceDate, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error in MainEvent(saveDateWithVoice). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void saveTimeWithVoice(String time){
        String hour="",minute="0";
        String[] arr = time.split(" ");
        if (arr.length > 1){
            if (isNumeric(arr[0])){
                hour = arr[0];
            }
            else if (arr[0].contains(":")){
                String[] timeArr = arr[0].split(":");
                hour = timeArr[0];
                minute = timeArr[1];
            }
            if (arr[1].equals("p.m."))
            {
                hour = (Integer.parseInt(hour) + 12) + "";
            }
            Information.gEventVoiceTime = hour + ":" + minute;
        }
        else
            Information.speak("The time format may be incorrect.");
        Toast.makeText(this,   "Time: " + time + "|" + Information.gEventVoiceTime , Toast.LENGTH_SHORT).show();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public void createAlertForEveryDay() {
        //System request code
        int DATA_FETCHER_RC = 123;
        //Create an alarm manager
        AlarmManager mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Create the time of day you would like it to go off. Use a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        //Creating an intent that points to the receiver. The system will notify the app about the current time, and send a broadcast to the app
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DATA_FETCHER_RC, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //initializing the alarm by using in exact repeating. This allows the system to scheduler your alarm at the most efficient time around your
        //set time, it is usually a few seconds off your requested time.
        // you can also use setExact however this is not recommended. Use this only if it must be done then.

        //Also set the interval using the AlarmManager constants
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    private ArrayList<EventHelperClass> collectEventSubject(Map<String, Object> users) {
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
            double num1 = (double) singleUser.get("eventlocationlongitude");
            double num2 = (double) singleUser.get("eventlocationlatitude");
            eventHelperClass.eventlocationlongitude = (float) num1;
            eventHelperClass.eventlocationlatitude = (float) num2;
            eventList.add(eventHelperClass);
        }
        return eventList;
    }
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, I am listening.");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        if (Information.gReschedulingType.equals("Sleep Time Rescheduling")){
            Toast.makeText(this, list[position] + " is selected.", Toast.LENGTH_SHORT).show();
            String[] timeArr = list[position].split(":");
            Information.geventtime = timeArr[0]+ ":" + timeArr[1];
            Information.geventdate = DateToday();
            Information.geventlocation = "Unknown";
            String[] locations = Information.gCurrentCoordinates.split(",");
            Information.glocationlatitude = Float.parseFloat(locations[0]);
            Information.glocationlongitude = Float.parseFloat(locations[1]);
            Random rdm = new Random();
            int num = rdm.nextInt() / 1000000;
            num = num * num;
            Information.galarmRequest_Code = "" + num;
            EventHelperClass eventHelperClass = new EventHelperClass();
            eventHelperClass.SetEvent(Information.gReschedulingType, Information.geventtime, Information.geventdate, Information.geventdiscription, Information.geventlocation, Information.glocationlongitude, Information.glocationlatitude, Information.galarmRequest_Code, Information.gringToneUrl);
            Toast.makeText(this, Information.gReschedulingType + " Rescheduled.", Toast.LENGTH_SHORT).show();
        }
        else if (Information.gReschedulingType.equals("Food Event Reschedule")){
            String[] timeArr = list[position].split(":");
            String time = timeArr[0]+ ":" + timeArr[1];
            Recommendations rec = new Recommendations();
            if (Information.gOccasion.equals("Breakfast Time Coming Soon.")) {
                //save data on database
                rec.BreakFastRecommendation(time,this);
            } else if (Information.gOccasion.equals("Lunch Time Coming Soon.")) {
                //save data on database
                rec.LunchRecommendation(time,this);
            } else if (Information.gOccasion.equals("Dinner Time Coming Soon.")) {
                //save data on database
                rec.DinnerRecommendation(time,this);
            }
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}

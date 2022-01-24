package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class EventPlanned extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef2;
    TextView showdate, showtime, subject, discription, showlocation;
    Button proceed, viewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_planned);
        viewEvent = findViewById(R.id.button_EventPlanned_change);
        viewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventPlanned.this, EventListing.class);
                startActivity(intent);
            }
        });

        subject = findViewById(R.id.tv_subject);
        subject.setText(("subject : " + Information.geventsubject));

        discription = findViewById(R.id.tv_discription);
        discription.setText(("Discription : " + Information.geventdiscription));

        showdate = findViewById(R.id.tv_date);
        showdate.setText("Date : " + Information.geventdate);

        showtime = findViewById(R.id.tv_time);
        showtime.setText("Time : " + Information.geventtime);

        showlocation = findViewById(R.id.tv_location);
        showlocation.setText("Loation : "+ Information.geventlocation);

        proceed = findViewById(R.id.button);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveInformation();
            }
        });
    }

    private void SaveInformation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You are one step away from completing one time setup! \n Do you want to Proceed with given Information.");
        try {
            Information.speak("You are one step away from completing one time setup! Do you want to Proceed with given Information.");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            Random rdm = new Random();
                            int num = rdm.nextInt() / 1000000;
                            num = num * num;
                            Information.galarmRequest_Code = "" + num;
                            EventHelperClass eventHelperClass = new EventHelperClass();
                            eventHelperClass.SetEvent(Information.geventsubject, Information.geventtime, Information.geventdate, Information.geventdiscription, Information.geventlocation, Information.glocationlongitude, Information.glocationlatitude, Information.galarmRequest_Code, Information.gringToneUrl);
                            Toast.makeText(EventPlanned.this, "All Information saved. \n Event Generated.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EventPlanned.this, MainEvent.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(EventPlanned.this, "Error in saving event. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        try {
            Information.speak("Do you want to proceed?");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void cancelAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(EventPlanned.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
}
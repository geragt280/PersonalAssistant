package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class sleep extends AppCompatActivity {

    Button setSleepHours;
    EditText sleepTime;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TimePicker sleepingtime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        sleepTime = findViewById(R.id.edit_SleepHours);
        setSleepHours = findViewById(R.id.button_sleepHour);
        setSleepHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Information.gsleepTime = sleepTime.getText().toString();
                if (sleepTime.getEditableText().toString().equals("")) {
                    sleepTime.setError("Please enter your sleeping hours");
                    sleepTime.requestFocus();
                }
                else{
                    Information.gsleepTime = sleepTime.getText().toString();
                }
                openEvent();
            }
        });

        sleepTime.setEnabled(false);
        sleepingtime = findViewById(R.id.time_picker_sleep);
        sleepingtime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Information.gSleepingTime = hourOfDay + ":" + minute;
                sleepTime.setEnabled(true);
            }
        });

        try {
            Information.speak("select your sleeping hours");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //Information.speak("do you want to proceed with the given information?");
    }

    private void openEvent() {
        //   Toast.makeText(this, "Your Sleeping hours are : " + Information.gsleepTime, Toast.LENGTH_LONG).show();
        SaveInformation();
    }

    private void SaveInformation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You are one step away from completing one time setup! \n Do you want to Proceed with given Information.");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            database = FirebaseDatabase.getInstance();
                            myRef = database.getReference("users");
                            UserHelperClass userHelperClass = new UserHelperClass(Information.gname, Information.ggender, Information.gteatime, Information.gusername, Information.gpw, Information.gemail, Information.gphoneNo, Information.gbirthDate, Information.gsleepTime, Information.glunchTime, Information.gbreakfastTime, Information.gdinner, Information.gSleepingTime);
                            myRef.child(Information.gusername).setValue(userHelperClass);
                            Toast.makeText(sleep.this, "All Information has been saved.", Toast.LENGTH_LONG).show();


                            SharedPref sp = new SharedPref();
                            sp.save(Information.gContext, Information.gusername);
                            Intent intent = new Intent(sleep.this, MainEvent.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(sleep.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
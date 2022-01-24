package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import java.util.Calendar;

public class Event extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button show, setRingtone, showTime, next;
    TextView date, Time;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        date=findViewById(R.id.date_pick);
        Time=findViewById(R.id.time_pick);

        Time = (TextView) findViewById(R.id.time_pick);
        next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLocation();
            }
        });

        try {
            Information.speak("select! event date and time");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        date = (TextView) findViewById(R.id.date_pick);
        show = (Button) findViewById(R.id.button_show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        showTime = (Button) findViewById(R.id.button_showTime);
        showTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeButton();
            }
        });

        //while updating
        if (Information.update)
        {
            date.setText(Information.geventdate);
            Time.setText(Information.geventtime);
        }


        try{
            setRingtone = findViewById(R.id.button_setRingtone);
        setRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingTone();


                    Information.speak("Select your ringtone");

            }
        });
        }
        catch(Exception e){
            Toast.makeText(this, "Error in button set ringtone. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void gotoLocation(){
        if (Information.gFlag){
            Information.speak("Warning!");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("WARNING!!\nYou are going to overwrite the time of an existing event.\nDo you want to proceed ?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            try {
                                Intent intent = new Intent(Event.this, Locations.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(Event.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            Intent intent = new Intent(Event.this, Locations.class);
            startActivity(intent);
        }
    }

    private void setRingTone() {
        RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog.Builder(this, getSupportFragmentManager());
        //Set title of the dialog. //If set null, no title will be displayed. ringtonePickerBuilder.setTitle("Select ringtone");
        //Add the desirable ringtone types. ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
        //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        //ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);
        ringtonePickerBuilder.setPlaySampleWhileSelection(true);
        try {
            Information.speak("your ringtone has been selected!");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            ringtonePickerBuilder.setListener(new RingtonePickerListener() {
                @Override
                public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                    Information.gringToneUrl = ringtoneUri.toString();
                }
            });

            ringtonePickerBuilder.show();
        }catch (Exception e)
        {
            Toast.makeText(Event.this, "Exception in Ringtone picker. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    Scheduler scheduler;
    private void handleTimeButton() {
        Information.gFlag = false;
        Calendar calander = Calendar.getInstance();
        int hour = calander.get(Calendar.HOUR);
        int minutes = calander.get(Calendar.MINUTE);
        boolean is24hourformat = DateFormat.is24HourFormat(this);
        if (datee != null) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                    String Timestring = "Hour: " + hour + " Minutes: " + minute;
                    String newTime = hour + ":" + minute;
                    Time.setText(Timestring);
                    Information.geventtime = newTime;
                    Information.geventdate = datee;

                    Information.speak("event date and time has been picked");

                    boolean check = scheduler.checkSchedule(newTime);
                    if (check) {
                        Toast.makeText(Event.this, "Your picked time is already scheduled.", Toast.LENGTH_SHORT).show();
                    }

                }
            }, hour, minutes, is24hourformat);
            timePickerDialog.show();
        }
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        );
        datePickerDialog.show();
    }
    String datee;
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month) {
        datee = month + "/" + day_of_month + "/" + year;
        Information.geventdate = datee;
        date.setText(month+1 + "/" + day_of_month + "/" + year);
        scheduler = new Scheduler(Information.geventdate);
    }
}

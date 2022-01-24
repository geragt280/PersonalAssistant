package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class lunch extends AppCompatActivity {
    Button btnSetLunchTime;
    TimePicker LunchTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);

        btnSetLunchTime = (Button) findViewById(R.id.button_set_LunchTime);
        btnSetLunchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LunchTimePicker = findViewById(R.id.time_picker_Lunch);
                int hour, minutes;
                hour = LunchTimePicker.getCurrentHour();
                minutes = LunchTimePicker.getCurrentMinute();
                Information.glunchTime = hour + ":" + minutes;
                openDinner();

                Information.speak("your lunch time has been saved");
            }
        });

        try {
            Information.speak("select your lunch time");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void openDinner() {
      //  Toast.makeText(lunch.this, "Saved Values are " + Information.glunchTime, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, dinner.class);
        startActivity(intent);
    }
}

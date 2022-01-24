package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class breakfast extends AppCompatActivity {
    Button btnSetBreakFastTime;
    TimePicker setBreakfastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakfast);

        btnSetBreakFastTime = (Button)findViewById(R.id.button_set_breakfast);
        btnSetBreakFastTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBreakfastTime = findViewById(R.id.time_picker_Breakfast);
                int hour,minutes;
                hour = setBreakfastTime.getCurrentHour();
                minutes = setBreakfastTime.getCurrentMinute();
                Information.gbreakfastTime = hour + ":" + minutes;
                openlunch();
            }
        });

        try {
            Information.speak("select your breakfast time");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void openlunch () {
        //Toast.makeText(breakfast.this, "Saved Values are " + Information.gbreakfastTime, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(breakfast.this, lunch.class);
        startActivity(intent);
    }
}
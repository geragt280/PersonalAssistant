package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class dinner extends AppCompatActivity {
    Button btnSetDinnerTime;
    TimePicker DinnerTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);

        btnSetDinnerTime = (Button) findViewById(R.id.button_set_LunchTime);
        btnSetDinnerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DinnerTimePicker = findViewById(R.id.time_picker_Dinner);
                int hour, minutes;
                hour = DinnerTimePicker.getCurrentHour();
                minutes = DinnerTimePicker.getCurrentMinute();
                Information.gdinner = hour + ":" + minutes;
                openSleep();

                Information.speak("your dinner time has been saved");
            }
        });

        try {
            Information.speak("select your dinner time");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void openSleep () {
       // Toast.makeText(dinner.this, "Saved Values are " + Information.gdinner, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, TeaTime.class);
        startActivity(intent);
    }
}
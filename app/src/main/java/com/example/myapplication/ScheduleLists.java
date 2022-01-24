package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ScheduleLists extends AppCompatActivity {

    ListView ScheduleListView;
    Button Today, Tomorrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_lists);

        try {
            //Information.gTodayScheduler.allActivityCalender();

            ScheduleListView = findViewById(R.id.schedule_event_list);

            ArrayAdapter arrayAdapter = new ArrayAdapter(ScheduleLists.this, R.layout.singlelistitem, Information.gEventSubjectString);
            ScheduleListView.setAdapter(arrayAdapter);

            Today = findViewById(R.id.button_today_event);
            Today.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Information.gTodayScheduler.allActivityCalender();
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ScheduleLists.this, R.layout.singlelistitem, Information.gEventSubjectString);
                    ScheduleListView.setAdapter(arrayAdapter);
                }
            });

            Tomorrow = findViewById(R.id.button_tomorrow_event);
            Tomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Information.gSomedaySchedule.allActivityCalender();
                    ArrayAdapter arrayAdapter = new ArrayAdapter(ScheduleLists.this, R.layout.singlelistitem, Information.gEventSubjectString);
                    ScheduleListView.setAdapter(arrayAdapter);
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "ScheduleList(OnCreate). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
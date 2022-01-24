package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class NewEvent extends AppCompatActivity {

    Button nextNE;
    TextView event_type;
    EditText Subject,Dicsription;
    ArrayList<LogHelperClass> logHelperClasses = new ArrayList<LogHelperClass>(), sameSubjectLogList = new ArrayList<LogHelperClass>();
    LogHelperClass logHelperClass = new LogHelperClass();
    String currentWeekItem, currentMonthItem, allMonthItem, allWeekItem;
    static ArrayList<LogHelperClass> sameDateDayList = new ArrayList<LogHelperClass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        try {

            logHelperClass = new LogHelperClass();
            logHelperClasses = logHelperClass.MakeLogList();

            Subject = (EditText) findViewById(R.id.edit_subject);

            Dicsription = (EditText) findViewById(R.id.edit_discription);

            this.event_type = findViewById(R.id.event_type);

            if (Information.gActivityType.equals("Appointment")) {
                try {
                    Information.speak("Enter subject and description for Appointment");
                } catch (Exception e) {
                    Toast.makeText(this, "Error in Speak NewEvent. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                this.event_type.setText("New Appointment");
            } else {
                try {
                    Information.speak("Enter subject and description for Event");
                } catch (Exception e) {
                    Toast.makeText(this, "Error in Speak NewEvent. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            if (Information.update) {
                Subject.setEnabled(false);
                if (Information.gActivityType.equals("Event")) {
                    this.event_type.setText("Update Event");
                    try {
                        Information.speak("Edit description for Event");
                    } catch (Exception e) {
                        Toast.makeText(this, "Error in Speak NewEvent. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Subject.setText(Information.geventsubject);
                    Dicsription.setText(Information.geventdiscription);
                }
            }


            nextNE = (Button) findViewById(R.id.button_next);
            nextNE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Information.gReschedulingType = "";
                    if (Subject.getEditableText().toString().equals("")) {
                        Subject.requestFocus();
                        Subject.setError("Subject can not be empty.");
                    } else {
                        if (!validateString(Subject.getEditableText().toString())) {
                            Subject.requestFocus();
                            Subject.setError("ENTER ONLY ALPHABETICAL CHARACTER");
                        } else {
                            if (Information.gActivityType.equals("Appointment")) {
                                Information.gAppointmentSubject = Subject.getEditableText().toString().trim();
                                Information.gAppointmentDescription = Dicsription.getEditableText().toString().trim();
                                Intent intent = new Intent(NewEvent.this, Appointments.class);
                                startActivity(intent);
                            } else {
                                CheckForOldEvent(Subject.getEditableText().toString());
                                if (sameSubjectLogList.size() >= 2) {
                                    if (checkSameWeekDay(sameSubjectLogList) || checkSameMonthDay(sameSubjectLogList)) {
                                        Toast.makeText(NewEvent.this, "Old Events Found ..", Toast.LENGTH_SHORT).show();
                                        MakeStringForSameDateDayList();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewEvent.this);
                                        alertDialogBuilder.setMessage("We have found some previous events subjected as this one which match same time and date or day. \nNamed: " + sameStringEventSubjects + ". Do you want this one as an appointment ?");
                                        alertDialogBuilder.setPositiveButton("Yes",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
                                                        //Toast.makeText(NewEvent.this, "The event has been made an appointment.", Toast.LENGTH_SHORT).show();
                                                        if (Information.gAppointmentType.equals("Weekly")) {
                                                            String[] getAnyOneLogInfo = sameDateDayList.get(0).eventLog.split("/");
                                                            getAnyOneLogInfo = getAnyOneLogInfo[1].split(" ");
                                                            String[] Time = getAnyOneLogInfo[3].split(":");
                                                            Information.gAppointmentTime = Time[0] + ":" + Time[1];
                                                            if (getAnyOneLogInfo[0] == "Mon") {
                                                                Information.getAppointmentDay = "Monday";
                                                            } else if (getAnyOneLogInfo[0] == "Tue") {
                                                                Information.getAppointmentDay = "Tuesday";
                                                            } else if (getAnyOneLogInfo[0] == "Wed") {
                                                                Information.getAppointmentDay = "Wednesday";
                                                            } else if (getAnyOneLogInfo[0] == "Thur") {
                                                                Information.getAppointmentDay = "Thursday";
                                                            } else if (getAnyOneLogInfo[0] == "Fri") {
                                                                Information.getAppointmentDay = "Friday";
                                                            } else if (getAnyOneLogInfo[0] == "Sat") {
                                                                Information.getAppointmentDay = "Saturday";
                                                            } else if (getAnyOneLogInfo[0] == "Sun") {
                                                                Information.getAppointmentDay = "Sunday";
                                                            }
                                                            Toast.makeText(NewEvent.this, "Weekly", Toast.LENGTH_SHORT).show();
                                                            Information.gAppointmentSubject = Subject.getEditableText().toString().trim();
                                                            AppointmentHelperClass appointmentHelperClass = new AppointmentHelperClass();
                                                            appointmentHelperClass.SetAppointment(Information.gAppointmentSubject, "", Information.gAppointmentTime, Information.getAppointmentDay, "", Information.gAppointmentType);
                                                        } else if (Information.gAppointmentType.equals("Monthly")) {
                                                            Toast.makeText(NewEvent.this, "Monthly", Toast.LENGTH_SHORT).show();
                                                            String[] getAnyOneLogInfo = sameDateDayList.get(0).eventLog.split("/");
                                                            getAnyOneLogInfo = getAnyOneLogInfo[1].split(" ");
                                                            String[] Time = getAnyOneLogInfo[3].split(":");
                                                            Information.gAppointmentDate = getAnyOneLogInfo[2];
                                                            Information.gAppointmentTime = Time[0] + ":" + Time[1];
                                                            Toast.makeText(NewEvent.this, "Weekly", Toast.LENGTH_SHORT).show();
                                                            Information.gAppointmentSubject = Subject.getEditableText().toString().trim();
                                                            AppointmentHelperClass appointmentHelperClass = new AppointmentHelperClass();
                                                            appointmentHelperClass.SetAppointment(Information.gAppointmentSubject, null, Information.gAppointmentTime, null, Information.gAppointmentDate, Information.gAppointmentType);
                                                        }
                                                    }
                                                });
                                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                OpenEvent(Subject.getEditableText().toString().trim(), Dicsription.getEditableText().toString().trim());
                                                finish();
                                            }
                                        });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                        sameStringEventSubjects = "";
                                    } else {
                                        OpenEvent(Subject.getEditableText().toString().trim(), Dicsription.getEditableText().toString().trim());
                                    }
                                } else {
                                    OpenEvent(Subject.getEditableText().toString().trim(), Dicsription.getEditableText().toString().trim());
                                }
                            }
                        }
                    }
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "NewEvent(onCreate). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void OpenEvent(String subject, String description){
        Information.geventsubject = subject;
        Information.geventdiscription = description;
        Intent intent = new Intent(NewEvent.this, Event.class);
        startActivity(intent);
    }

    boolean validateString(String str) {
        str = str.toLowerCase();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (!(ch >= 'a' && ch <= 'z' || ch == ' ')) {
                return false;
            }
        }
        return true;
    }
    String sameStringEventSubjects = "";
    void CheckForOldEvent(String subject) {
        logHelperClass = new LogHelperClass();
        logHelperClasses = logHelperClass.MakeLogList();
        sameSubjectLogList = new ArrayList<LogHelperClass>();
        if (logHelperClasses.size() > 0) {
            for (int i = 0; i < logHelperClasses.size(); i++) {
                //Toast.makeText(this, ""+ logHelperClasses.get(i).eventLog, Toast.LENGTH_SHORT).show();
                String[] logArray = logHelperClasses.get(i).eventLog.split("/");
                String[] logSubArr = logArray[0].split(" ");
                String[] subjectArr = subject.split(" ");
                //Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                if (subjectArr.length > 0){
                    if (checkSameWords(logSubArr,subjectArr)) {
                        sameSubjectLogList.add(logHelperClasses.get(i));
                    }
                }
            }
        }
    }

    boolean checkSameWords(String[] arr1, String[] arr2){
        int i;
        for (i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr2.length; j++) {
                if (arr1[i].equals(arr2[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean checkSameWeekDay(ArrayList<LogHelperClass> upcomingList){
        sameDateDayList = new ArrayList<LogHelperClass>();
        int count=0;
        for (int i = 0; i < upcomingList.size(); i++) {
            currentWeekItem = upcomingList.get(i).eventLog;
            String[] arr = currentWeekItem.split("/");
            currentWeekItem = arr[2];
            for (int j = 0; j < upcomingList.size(); j++) {
                allWeekItem = upcomingList.get(j).eventLog;
                String[] arr2 = allWeekItem.split("/");
                allWeekItem = arr2[2];
                Log.d("TAG","checked current: " + currentWeekItem + " with all " + allWeekItem);
                if (currentWeekItem.equals(allWeekItem)){
                    count++;
                    if (!sameDateDayList.contains(upcomingList.get(i))){
                        sameDateDayList.add(upcomingList.get(i));
                    }
                    if(count > 2){
                        sameDateDayList.add(upcomingList.get(i));
                        Information.gAppointmentType = "Weekly";
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean checkSameMonthDay(ArrayList<LogHelperClass> upcomingList){
        sameDateDayList = new ArrayList<LogHelperClass>();
        int count=0;
        for (int i = 0; i < upcomingList.size(); i++) {
            currentMonthItem = upcomingList.get(i).eventLog;
            String[] arr = currentMonthItem.split("/");
            currentMonthItem = arr[3];
            for (int j = 0; j < upcomingList.size(); j++) {
                allMonthItem = upcomingList.get(j).eventLog;
                String[] arr2 = allMonthItem.split("/");
                allMonthItem = arr2[3];
                Log.d("TAG","checked current: " + currentMonthItem + " with all " + allMonthItem);
                if (currentMonthItem.equals(allMonthItem)){
                    count++;
                    if (!sameDateDayList.contains(upcomingList.get(i))){
                        sameDateDayList.add(upcomingList.get(i));
                    }
                    if(count > 2){
                        Information.gAppointmentType = "Monthly";
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void MakeStringForSameDateDayList()
    {
        for (int i =0; i<sameDateDayList.size();i++ ){
            String[] arr = sameDateDayList.get(i).eventLog.split("/");
            if (sameStringEventSubjects.equals("")) {
                sameStringEventSubjects = arr[0];
            } else
                sameStringEventSubjects = sameStringEventSubjects + ", " + arr[0];
        }
    }
}




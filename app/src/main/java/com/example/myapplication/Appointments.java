package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Random;

public class Appointments extends AppCompatActivity {
    Button btnSaveAppointmentDate;
    TimePicker timePicker;
    Spinner spinnerAppointmentType, spinnerAppointmentDaysDate;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        try {
            Information.speak("Select your Appointment type.");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        timePicker = findViewById(R.id.timePicker_appointment);
        textView = findViewById(R.id.tv_appointment_info);

        spinnerAppointmentType = findViewById(R.id.spinner_apt);
        spinnerAppointmentDaysDate = findViewById(R.id.spinner_select_day_date);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.appointment_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAppointmentType.setAdapter(adapter);
        spinnerAppointmentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Select Type?")) {
                    textView.setText("Select Appointment Type.");
                    Information.gAppointmentType = null;
                    timePicker.setVisibility(View.INVISIBLE);
                    spinnerAppointmentDaysDate.setVisibility(View.INVISIBLE);
                } else {
                    Information.gAppointmentType = (String) parent.getItemAtPosition(position);
                    textView.setText("Select Time of day for appointment.");
                    if (Information.gAppointmentType.equals("Daily")) {
                        timePicker.setVisibility(View.VISIBLE);
                        spinnerAppointmentDaysDate.setVisibility(View.INVISIBLE);
                    } else if (Information.gAppointmentType.equals("Weekly")) {
                        textView.setText("Select Time and Day for appointment.");
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(Appointments.this, R.array.appointment_days_of_week, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAppointmentDaysDate.setAdapter(adapter2);
                        spinnerAppointmentDaysDate.setVisibility(View.VISIBLE);
                        timePicker.setVisibility(View.VISIBLE);
                    } else if (Information.gAppointmentType.equals("Monthly")) {
                        textView.setText("Select Time and Date for appointment.");
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(Appointments.this, R.array.appointment_date, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerAppointmentDaysDate.setAdapter(adapter2);
                        spinnerAppointmentDaysDate.setVisibility(View.VISIBLE);
                        timePicker.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerAppointmentDaysDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Information.gAppointmentType.equals("Daily"))
                {

                }else if (Information.gAppointmentType.equals("Weekly"))
                {
                    Information.getAppointmentDay = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(Appointments.this, "Selected item. " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                }else if (Information.gAppointmentType.equals("Monthly"))
                {
                    Information.gAppointmentDate = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(Appointments.this, "Selected item. " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSaveAppointmentDate = findViewById(R.id.button_set_AppointmentTime);
        btnSaveAppointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAppointment();
            }
        });

    }
    private void saveAppointment () {
        Information.gAppointmentTime = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("You have selected appointment details! \nDo you want to Proceed with given Information.");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            AppointmentHelperClass appointmentHelperClass = new AppointmentHelperClass();
                            appointmentHelperClass.SetAppointment(Information.gAppointmentSubject,Information.gAppointmentDescription,Information.gAppointmentTime,Information.getAppointmentDay,Information.gAppointmentDate, Information.gAppointmentType);
                            Toast.makeText(Appointments.this, "Information saved. \nAppointment Generated.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Appointments.this, MainEvent.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(Appointments.this, "Error in saving Appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
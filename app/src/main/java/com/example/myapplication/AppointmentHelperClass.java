package com.example.myapplication;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppointmentHelperClass {
    public String appointmentSubject, appointmentTime, appointmentDay, appointmentDescription, appointmentDate, appointmentType;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    public AppointmentHelperClass() {

    }

    public AppointmentHelperClass(String subject, String description, String time, String day, String date, String type) {
        appointmentSubject = subject;
        appointmentDescription = description;
        appointmentTime = time;
        appointmentDay = day;
        appointmentDate = date;
        appointmentType = type;
    }

    public void SetAppointment(String subject, String description, String time, String day, String date, String type) {
        {
            try{
                firebaseDatabase = FirebaseDatabase.getInstance();
                //  myRef2 = database2.getReference("users");
                reference = firebaseDatabase.getReference("users/" + Information.gusername + "/appointments/");
                AppointmentHelperClass helperClass = new AppointmentHelperClass(subject, description, time, day, date, type);
                reference.child(Information.gAppointmentSubject).setValue(helperClass);
            }catch (Exception e){
                Toast.makeText(Information.gContext, "Error in AppointmentHelperClass(SetAppointment) Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
}

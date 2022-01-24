package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class Eventinformation extends AppCompatActivity {

    TextView subject, description, time, date, location;
    Button Update_button, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventinformation);
        try {
            if (Information.update)
            {
                Information.geventsubject = savedInstanceState.getString("eventSubject");
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events").child(Information.geventsubject);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                Information.geventsubject = snapshot.child("eventsubject").getValue(String.class);
                                Information.geventdiscription = snapshot.child("eventdescription").getValue(String.class);
                                Information.geventtime = snapshot.child("eventtime").getValue(String.class);
                                Information.geventdate = snapshot.child("eventdate").getValue(String.class);
                                Information.geventlocation = snapshot.child("eventlocationname").getValue(String.class);
                                Information.glocationlongitude = snapshot.child("eventlocationlongitude").getValue(float.class);
                                Information.glocationlatitude = snapshot.child("eventlocationlatitude").getValue(float.class);
                                Information.galarmRequest_Code = snapshot.child("eventAlarmRequestCode").getValue(String.class);

                                Intent intent = new Intent(Eventinformation.this, Eventinformation.class);
                                startActivity(intent);
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(Eventinformation.this, "Exception in getting information. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }



            subject = findViewById(R.id.tv_subject);
            subject.setText(Information.geventsubject);

            description = findViewById(R.id.tv_description);
            description.setText(Information.geventdiscription);

            time = findViewById(R.id.tv_time);
            time.setText(Information.geventtime);

            date = findViewById(R.id.tv_date);
            date.setText(Information.geventdate);

            location = findViewById(R.id.tv_location);
            location.setText(Information.geventlocation);

            Update_button = findViewById(R.id.edit_info_button);
            Update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Information.update = true;
                    Intent intent = new Intent(Eventinformation.this, NewEvent.class);
                    startActivity(intent);
                }
            });

            delete = findViewById(R.id.delete_info_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteEvent();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error In EventInformation", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteEvent() {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("You are about to delete this event. \n Do you want to Proceed.");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Information.gusername).child("events").child(Information.geventsubject);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            if (!Information.galarmRequest_Code.equals("")) {
                                int requestCode = Integer.parseInt(Information.galarmRequest_Code);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(Eventinformation.this, AlertReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(Eventinformation.this, requestCode, intent, 0);
                                alarmManager.cancel(pendingIntent);

                                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(Eventinformation.this, requestCode + 1, intent, 0);
                                alarmManager.cancel(pendingIntent2);

                                RedirectToList();
                                finish();
                            }
                        }

                    });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent i = new Intent(Eventinformation.this,MainEvent.class);
                    startActivity(i);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }catch (Exception e){
            Toast.makeText(this, "Error in Deleting and Canceling Event.", Toast.LENGTH_SHORT).show();
        }
    }
    void RedirectToList(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Information.gusername).child("events");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 1)
                {
                    Intent i = new Intent(Eventinformation.this, EventListing.class);
                    startActivity(i);
                }
                else
                {
                    Intent intent = new Intent(Eventinformation.this,MainEvent.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
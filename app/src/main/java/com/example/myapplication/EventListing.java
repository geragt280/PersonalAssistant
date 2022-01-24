package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventListing extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView eventListView;
    Button next, Addevent;
    private DatabaseReference databaseReference;
    ArrayList<EventHelperClass> eventList;
    ArrayList<String> arraylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_listing);

        try {
            Addevent = findViewById(R.id.btn_add_event);
            Addevent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Information.update = false;
                    Intent intent = new Intent(EventListing.this, NewEvent.class);
                    startActivity(intent);
                }
            });

            next = findViewById(R.id.button_Edit_info);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EventListing.this, MainEvent.class);
                    startActivity(intent);
                }
            });

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(Information.gusername).child("events");


        } catch (Exception e) {
            Toast.makeText(this, "Error in start: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        try {

            eventList = new ArrayList<EventHelperClass>();
            databaseReference.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            collecteventsubject((Map<String, Object>) dataSnapshot.getValue());

                            Toast.makeText(EventListing.this, "" + arraylist.size(), Toast.LENGTH_SHORT).show();
                            ArrayAdapter arrayAdapter = new ArrayAdapter(EventListing.this, android.R.layout.simple_list_item_1, arraylist);

                            eventListView = (ListView) findViewById(R.id.spinner_event);

                            eventListView.setOnItemClickListener(EventListing.this);
                            eventListView.setOnItemLongClickListener(EventListing.this);
                            eventListView.setItemsCanFocus(false);
                            eventListView.setAdapter(arrayAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error in listview " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private ArrayList<String> collecteventsubject(Map<String, Object> users) {

        ArrayList<String> eventSubject = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            arraylist.add((String) singleUser.get("eventsubject"));
        }

        return eventSubject;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getItemAtPosition(position);
        getInformation(name);
        //name is the pressed string.
        //Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }

    public void getInformation(String event_name) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events").child(event_name);
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

                        Information.update = false;
                        Intent intent = new Intent(EventListing.this, Eventinformation.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(EventListing.this, "Exception in getting information. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String name = (String) parent.getItemAtPosition(position);
        if (eventListView.isItemChecked(position)) {
            eventListView.setItemChecked(position, false);
        } else {
            eventListView.setItemChecked(position, true);
        }
        //Log.v(LOG_TAG,"long clicked pos: " + pos);
        eventListView.setSelection(position);
        return false;
    }

}
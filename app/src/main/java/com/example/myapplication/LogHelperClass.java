package com.example.myapplication;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class LogHelperClass {
    public String eventLog;
    ArrayList<LogHelperClass> logHelperClassList = new ArrayList<LogHelperClass>();

    public LogHelperClass(){}

    public LogHelperClass(String log){
        this.eventLog = log;
    }

    public ArrayList<LogHelperClass> MakeLogList() {
        final ArrayList<LogHelperClass>[] finalLogs = new ArrayList[]{new ArrayList<LogHelperClass>()};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("eventLogs");
        try {

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Information.logsList = collectEventLogs((Map<String, Object>) snapshot.getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(Information.gContext, "Error in LogHelperClass(MakingLogList). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            //Log.d("TAG", "Error in Counting Logs. Error: " + e.getMessage());
        }
        return Information.logsList;
    }

    private ArrayList<LogHelperClass> collectEventLogs(Map<String, Object> users) {
        try {
            LogHelperClass logHelperClass;
            //iterate through each user, ignoring their UID
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                logHelperClass = new LogHelperClass();
                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list
                logHelperClass.eventLog = (String) singleUser.get("eventLog");
                logHelperClassList.add(logHelperClass);
            }
        }catch (Exception e) {
            Toast.makeText(Information.gContext, "Error in LogHelperClass(collectEventLogs) Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return logHelperClassList;
    }

}

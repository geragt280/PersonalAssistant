package com.example.myapplication;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHelperClass {

    public String name,username, password, email, phoneNo, birthdate ,gender, sleepinghours  ,lunchtime, breakfasttime, dinnertime,teatime, sleepingTime;
    DatabaseReference reference;

    public UserHelperClass() {
    }

    public  UserHelperClass(String gname,String gender,String gteatime, String gusername, String gpw, String gemail, String gphoneNo, String gbirthDate, String gsleepTime, String glunchTime, String gbreakfastTime, String gdinner, String sleepingtime)
    {
        this.name = gname;
        this.username = gusername;
        this.password = gpw;
        this.email = gemail;
        this.phoneNo = gphoneNo;
        this.birthdate = gbirthDate;
        this.sleepinghours = gsleepTime;
        this.lunchtime = glunchTime;
        this.breakfasttime = gbreakfastTime;
        this.dinnertime = gdinner;
        this.teatime=gteatime;
        this.gender=gender;
        this.sleepingTime = sleepingtime;
    }

    public void getUserInformation(final String username){
        try{
            reference = FirebaseDatabase.getInstance().getReference("users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Information.gbirthDate = snapshot.child(username).child("birthdate").getValue(String.class);
                    Information.gbreakfastTime = snapshot.child(username).child("breakfasttime").getValue(String.class);
                    Information.gdinner = snapshot.child(username).child("dinnertime").getValue(String.class);
                    Information.gemail = snapshot.child(username).child("email").getValue(String.class);
                    Information.glunchTime = snapshot.child(username).child("lunchtime").getValue(String.class);
                    Information.gsleepTime = snapshot.child(username).child("sleepinghours").getValue(String.class);
                    Information.gusername = snapshot.child(username).child("username").getValue(String.class);
                    Information.gteatime = snapshot.child(username).child("teatime").getValue(String.class);
                    Information.gSleepingTime = snapshot.child(username).child("sleepingTime").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Toast.makeText(Information.gContext, "Error in UserHelperClass(getUserInformation). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
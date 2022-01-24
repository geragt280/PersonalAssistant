package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DialogActivity extends Activity implements SelectSingleItemDialog.SingleChoiceListener {

    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn, mCancelBtn;
    TextView DialogLog;
    String occasion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        try{
            LayoutInflater inflater = getLayoutInflater();

            Information.gTotalResultPoints = 10;
            // Build the dialog
            mAlertDlgBuilder = new AlertDialog.Builder(this);
            mDialogView = inflater.inflate(R.layout.activity_dialog, null);
            if (Information.todayResult == 0){
                Toast.makeText(this, "I ran", Toast.LENGTH_SHORT).show();
                Information.todayResult = 10;
            }
            else if (Information.todayResult < 2){
                Information.todayResult = 0;
            }
            Toast.makeText(this, "Information.gTodayResult: " + Information.todayResult, Toast.LENGTH_SHORT).show();
            DialogLog = mDialogView.findViewById(R.id.occasion_information_tv);
            if (Information.gOccasion == null)
            {
                Information.gOccasion = "occasion";
            }
            if (Information.gOccasion.equals("Its Tea Time.") || Information.gOccasion.equals("Its Coffee Time.")){
                DialogLog.setText(" " + Information.gOccasion + ". \n Since you are not home.\n Do you want any recommendations ? ");

            }
            else {
                DialogLog.setText("Hello Sir!\n Its " + Information.gOccasion + ". \nAre you gonna eat Now Or Later ? ");
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users/"+Information.gusername+"/resultingPoints/");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.exists()){
                        final Integer value = dataSnapshot.getValue(Integer.class);
                        if (value != 0){
                            exist = true;
                            Information.gTemp = value;
                            int num  = value + Information.todayResult;
                            Toast.makeText(DialogActivity.this, "Num value: " + num, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("TAG", "Value is: " + value);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });

            FirebaseDatabase totalPointsDatabase = FirebaseDatabase.getInstance();
            DatabaseReference totalPointsRef = totalPointsDatabase.getReference("users/"+Information.gusername+"/totalResultingPoints/");
            totalPointsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.exists()){
                        final Integer value = dataSnapshot.getValue(Integer.class);
                        if (value != 0){
                            exist = true;
                            Information.gTotalResultPoints = value + Information.gTotalResultPoints;
                            Toast.makeText(DialogActivity.this, "Total day points: " + Information.gTotalResultPoints, Toast.LENGTH_SHORT).show();
                        }
                        Log.d("TAG", "Value is: " + value);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("TAG", "Failed to read value.", error.toException());
                }
            });

            mOKBtn = mDialogView.findViewById(R.id.ID_Ok);
            mCancelBtn = mDialogView.findViewById(R.id.ID_Cancel);
            mOKBtn.setOnClickListener(mDialogbuttonClickListener);
            mCancelBtn.setOnClickListener(mDialogbuttonClickListener);
            mAlertDlgBuilder.setCancelable(false);
            mAlertDlgBuilder.setInverseBackgroundForced(true);
            mAlertDlgBuilder.setView(mDialogView);
            mAlertDialog = mAlertDlgBuilder.create();
            mAlertDialog.show();
            SharedPref sp = new SharedPref();
            if (Information.gOccasion.equals("Its Tea Time.") || Information.gOccasion.equals("Its Coffee Time.") && sp.getPREF_KEY_home_location(this) != null){
                String[] home = sp.getPREF_KEY_home_location(this).split(","); // getting home longitude and latitude
                String[] current = sp.getPREF_KEY_current_location(this).split(","); //getting current longitude and latitude
                double distance, longhome = Double.parseDouble(home[1]), lathome = Double.parseDouble(home[0]), longcurrent= Double.parseDouble(current[1]), latcurrent = Double.parseDouble(current[0]);
                distance = DistanceBetweenTwoPoints(lathome,longhome,latcurrent,longcurrent);
                Toast.makeText(this, "Distance is " + distance, Toast.LENGTH_SHORT).show();
                if (distance < 100){
                    finish();
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(this, "DialogActivity(onCreate). Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    double radian(double x) {
        return x * Math.PI / 180;
    }

    double DistanceBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {
        int R = 6378137; // Earth’s mean radius in meter
        double dLat = radian(lat2 - lat1);
        double dLong = radian(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(radian(lat1)) * Math.cos(radian(lat2)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter
    }

    void RedirectSuggestToMaps() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=4+star+plus+rating+cafe"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error in MainEvent(RedirectToMaps) Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean exist = false;

    View.OnClickListener mDialogbuttonClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onClick(View v) {
            if (v.getId() == R.id.ID_Ok) {
                if (Information.gOccasion.equals("Its Tea Time.") || Information.gOccasion.equals("Its Coffee Time.")) {
                    Information.gOccasion = "";
                    RedirectSuggestToMaps();
                } else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef2 = database.getReference("users/" + Information.gusername + "/resultingPoints/");
                    myRef2.setValue(Information.todayResult + Information.gTemp);
                    Information.todayResult = 0;
                    setAlarmFor15min();
                    addTotalPoints();
                }
                mAlertDialog.dismiss();
                finish();
            } else if (v.getId() == R.id.ID_Cancel) {
                if (Information.gOccasion.equals("Its Tea Time.") || Information.gOccasion.equals("Its Coffee Time.")) {
                    Information.gOccasion="";
                    Toast.makeText(DialogActivity.this, "Tea Recommendation declined.", Toast.LENGTH_SHORT).show();
                } else {
                    Information.gReschedulingType = "Food Event Reschedule";

                    FreeScheduleForMealTimes();

                    Information.speak("Select the time to rescheduling this meal time.");

                    Information.todayResult = Information.todayResult - 3;

                    OpenRescheduleDialog();
                }
                mAlertDialog.dismiss();
                finish();
            }
        }
    };

    public void OpenRescheduleDialog(){
        try{
            DialogFragment singleChoiceDialog = new SelectSingleItemDialog();
            singleChoiceDialog.setCancelable(false);
            singleChoiceDialog.show(Information.gFragment, "Personal Assistant");
        }catch (Exception e){
            Toast.makeText(this, "DialogActivity(OpenRescheduleDialog). Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void addTotalPoints(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users/"+Information.gusername+"/totalResultingPoints/");
        reference.setValue(Information.gTotalResultPoints);
    }

    public void setAlarmFor15min(){
        Calendar crab;
        //Toast.makeText(this, "" +datetime , Toast.LENGTH_SHORT).show();
        try
        {
            crab = Calendar.getInstance();
            crab.add(Calendar.MINUTE,15);
            Toast.makeText(Information.gContext, "Alarm set for 15mins. ", Toast.LENGTH_LONG).show();

            AlarmManager alarmManager = (AlarmManager) Information.gContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(Information.gContext, AlertReceiver.class);
            intent.putExtra("requestCode", 550);
            intent.putExtra("eventSubject", Information.gReschedulingType);
            intent.putExtra("AlarmAccess", true);
            intent.putExtra("notificationType", "Alarm");
            intent.putExtra("ringtoneUrl", Information.gringToneUrl);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(Information.gContext, 550, intent, 0);
            if (crab.before(Calendar.getInstance())){
                //crab.add(Calendar.DATE, 1);
                Toast.makeText(DialogActivity.this, "Inside Before session day++.", Toast.LENGTH_SHORT).show();
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, crab.getTimeInMillis(), pendingIntent);

        }catch (Exception e){

            Toast.makeText(this, "Error in setting Calender. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    void FreeScheduleForMealTimes(){
        Information.gFreeTimeCalenderString = new ArrayList<String>();
        Information.gFreeTimeCalender = new ArrayList<Calendar>();
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.SECOND, 0);
        c1.add(Calendar.MINUTE, 30);
        Toast.makeText(this, "c1 value: " + c1.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
        if (c1.get(Calendar.MINUTE) >= 30)
        {
            //Toast.makeText(this, "1 hour added.", Toast.LENGTH_LONG).show();
            c1.add(Calendar.HOUR_OF_DAY,1);
            c1.set(Calendar.MINUTE,0);
        }
        else if (c1.get(Calendar.MINUTE) < 30){
            //Toast.makeText(this, "30 min added. Time: " + c1.getTime(), Toast.LENGTH_LONG).show();
            c1.set(Calendar.MINUTE, 30);
        }
        String timing = c1.getTime().toString();
        String[] timingArr = timing.split(" ");
        Information.gFreeTimeCalenderString.add(timingArr[3]);
        Information.gFreeTimeCalender.add(c1);
        c1.add(Calendar.MINUTE,30);
        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.HOUR_OF_DAY, 0);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.add(Calendar.DATE, 1);
        boolean exist = Information.gTodayScheduler.makeFreeTimeSchedule(c1,0);
        while (true){
            Date x = c2.getTime();
            if (x.after(c1.getTime())){
                if (exist){
                    break;
                }else {
                    c1.add(Calendar.MINUTE,30);
                }
                exist = Information.gTodayScheduler.makeFreeTimeSchedule(c1,0);
            }
            else
                break;
        }
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}

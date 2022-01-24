package com.example.myapplication;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class Scheduler {
    String breakfasttime, dinnertime, lunchtime;
    String dater;
    Calendar sleephours;
    String[] SleepingTime;

    ArrayList<EventHelperClass> eventList = new ArrayList<EventHelperClass>();
    ArrayList<EventHelperClass> todayEventList = new ArrayList<EventHelperClass>();
    ArrayList<AppointmentHelperClass> todayAppointmentList = new ArrayList<AppointmentHelperClass>();
    ArrayList<AppointmentHelperClass> appointmentList = new ArrayList<AppointmentHelperClass>();

    public Scheduler(){
        dater = DateToday();
        makeTodaySchedule();
    }

    public Scheduler(String date) {
        dater = date;
        makeSchedule();
    }

    String DateToday(){
        String date = "", month = "", year = "";
        Date da_te = Calendar.getInstance().getTime();
        String dateToday = da_te.toString();
        String[] arr = dateToday.split(" ");
        if (arr[1].equals("January") || arr[1].equals("Jan")) {
            month = "0";
        } else if (arr[1].equals("February") || arr[1].equals("Feb")) {
            month = "1";
        } else if (arr[1].equals("March") || arr[1].equals("Mar")) {
            month = "2";
        } else if (arr[1].equals("April") || arr[1].equals("Apr")) {
            month = "3";
        } else if (arr[1].equals("May") || arr[1].equals("Mets") || arr[1].equals("Mats")) {
            month = "4";
        } else if (arr[1].equals("June") || arr[1].equals("Jun")) {
            month = "5";
        } else if (arr[1].equals("July") || arr[1].equals("Jul")) {
            month = "6";
        } else if (arr[1].equals("August") || arr[1].equals("Aug")) {
            month = "7";
        } else if (arr[1].equals("September") || arr[1].equals("Sept")) {
            month = "8";
        } else if (arr[1].equals("October") || arr[1].equals("Oct")) {
            month = "9";
        } else if (arr[1].equals("November") || arr[1].equals("Nov")) {
            month = "10";
        } else if (arr[1].equals("December") || arr[1].equals("Dec")) {
            month = "11";
        }
        date = arr[2];
        year = arr[5];
        return month + "/" + date + "/" + year;
    }

    String DateTomorrow(){
        String date = "", month = "", year = "";
        Date da_te = Calendar.getInstance().getTime();
        String dateToday = da_te.toString();
        String[] arr = dateToday.split(" ");
        month = getMonthNumber(arr);
        date = arr[2];
        year = arr[5];
        return month + "/" + (Integer.parseInt(date)+1) + "/" + year;
    }

    String getMonthNumber(String[] arr){
        String month;
        if (arr[1].equals("January") || arr[1].equals("Jan")) {
            month = "0";
        } else if (arr[1].equals("February") || arr[1].equals("Feb")) {
            month = "1";
        } else if (arr[1].equals("March") || arr[1].equals("Mar")) {
            month = "2";
        } else if (arr[1].equals("April") || arr[1].equals("Apr")) {
            month = "3";
        } else if (arr[1].equals("May") || arr[1].equals("Mets") || arr[1].equals("Mats")) {
            month = "4";
        } else if (arr[1].equals("June") || arr[1].equals("Jun")) {
            month = "5";
        } else if (arr[1].equals("July") || arr[1].equals("Jul")) {
            month = "6";
        } else if (arr[1].equals("August") || arr[1].equals("Aug")) {
            month = "7";
        } else if (arr[1].equals("September") || arr[1].equals("Sept")) {
            month = "8";
        } else if (arr[1].equals("October") || arr[1].equals("Oct")) {
            month = "9";
        } else if (arr[1].equals("November") || arr[1].equals("Nov")) {
            month = "10";
        } else if (arr[1].equals("December") || arr[1].equals("Dec")) {
            month = "11";
        }
        else month = "";
        return month;
    }

    public void makeTodaySchedule() {
        try{
            todayEventList = new ArrayList<EventHelperClass>();
            //Toast.makeText(Information.gContext, "makeTodayScheduler: " , Toast.LENGTH_SHORT).show();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            breakfasttime = snapshot.child("breakfasttime").getValue(String.class);
                            dinnertime = snapshot.child("dinnertime").getValue(String.class);
                            lunchtime = snapshot.child("lunchtime").getValue(String.class);
                            String sleepingHours = snapshot.child("sleepinghours").getValue(String.class);
                            SleepingTime = (snapshot.child("sleepingTime").getValue(String.class)).split(":");
                            assert sleepingHours != null;
                            sleephours = createSleepingHoursTill(Integer.parseInt(SleepingTime[0]), Integer.parseInt(sleepingHours), DateToday());
                        }
                        else{

                            //Toast.makeText(Information.gContext, "Didn't find anything.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(Information.gContext, "Scheduler(makeTodaySchedule) Getting information from firebase. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events");
            eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        collectEvent((Map<String, Object>) snapshot.getValue());
                        if (eventList.size() > 0) {
                            for (int i = 0; i < eventList.size(); i++) {
                                Information.geventdate = eventList.get(i).eventdate;
                                Information.geventtime = eventList.get(i).eventtime;
                                Calendar c = createCalender();
                                Date date = Calendar.getInstance().getTime();
                                if (c.get(Calendar.DATE) == date.getDate()) {
                                    //Toast.makeText(Information.gContext, "Event found.", Toast.LENGTH_SHORT).show();
                                    EventHelperClass today_event = eventList.get(i);
                                    todayEventList.add(today_event);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference appointmentReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("appointments");
            appointmentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        collectAppointment((Map<String, Object>) snapshot.getValue());
                        if (appointmentList.size() > 0) {
                            for (int i = 0; i < appointmentList.size(); i++) {
                                Information.gAppointmentDate = appointmentList.get(i).appointmentDate;
                                Information.gAppointmentTime = appointmentList.get(i).appointmentTime;
                                Calendar c = createCalender2();
                                Date date = Calendar.getInstance().getTime();
                                if (c.get(Calendar.DATE) == date.getDate()) {
                                    Toast.makeText(Information.gContext, "Appointment found.", Toast.LENGTH_SHORT).show();
                                    AppointmentHelperClass today_appointment = appointmentList.get(i);
                                    todayAppointmentList.add(today_appointment);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e)
        {
            Toast.makeText(Information.gContext, "Scheduler(makeTodaySchedule). Error: " + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    void makeSchedule() {
        try{
            todayEventList = new ArrayList<EventHelperClass>();
            //Toast.makeText(Information.gContext, "makeSchedule: ", Toast.LENGTH_SHORT).show();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername);
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.exists()) {
                            breakfasttime = snapshot.child("breakfasttime").getValue(String.class);
                            dinnertime = snapshot.child("dinnertime").getValue(String.class);
                            lunchtime = snapshot.child("lunchtime").getValue(String.class);
                            String sleepingHours = snapshot.child("sleepinghours").getValue(String.class);
                            SleepingTime = (snapshot.child("sleepingTime").getValue(String.class)).split(":");
                            assert sleepingHours != null;
                            sleephours = createSleepingHoursTill(Integer.parseInt(SleepingTime[0]), Integer.parseInt(sleepingHours), dater);
                        }
                        else {
                            Toast.makeText(Information.gContext, "Snapshot not available.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(Information.gContext, "makeSchedule. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("events");
            eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        collectEvent((Map<String, Object>) snapshot.getValue());
                        if (eventList.size() > 0) {
                            for (int i = 0; i < eventList.size(); i++) {
                                Information.geventdate = eventList.get(i).eventdate;
                                Information.geventtime = eventList.get(i).eventtime;
                                Calendar c = createCalender();
                                Date date = Calendar.getInstance().getTime();
                                String[] dateArr = dater.split("/");
                                date.setDate(Integer.parseInt(dateArr[1]));
                                if (c.get(Calendar.DATE) == date.getDate()) {
                                    //Toast.makeText(Information.gContext, "Event found.", Toast.LENGTH_SHORT).show();
                                    EventHelperClass today_event = eventList.get(i);
                                    todayEventList.add(today_event);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference appointmentReference = FirebaseDatabase.getInstance().getReference("users").child(Information.gusername).child("appointments");
            appointmentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        collectAppointment((Map<String, Object>) snapshot.getValue());
                        if (appointmentList.size() > 0) {
                            for (int i = 0; i < appointmentList.size(); i++) {
                                Information.gAppointmentDate = appointmentList.get(i).appointmentDate;
                                Information.gAppointmentTime = appointmentList.get(i).appointmentTime;
                                Calendar c = createCalender2();
                                Date date = Calendar.getInstance().getTime();
                                String[] dateArr = dater.split("/");
                                date.setDate(Integer.parseInt(dateArr[1]));
                                if (c.get(Calendar.DATE) == date.getDate()) {
                                    Toast.makeText(Information.gContext, "Appointment found.", Toast.LENGTH_SHORT).show();
                                    AppointmentHelperClass today_appointment = appointmentList.get(i);
                                    todayAppointmentList.add(today_appointment);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e)
        {
            Toast.makeText(Information.gContext, "Error in Scheduler: " + e.getMessage() , Toast.LENGTH_LONG).show();
        }
    }

    //this method will simply create a Calender type variable for specific hour given for the given date.
    Calendar createSleepingHours(int hours,String date) {
        Calendar c = Calendar.getInstance();
        String[] arrDate = date.split("/");
        c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
        c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
        c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c;
    }

    //this method helps to create Calender type variable which will be telling the end time of person sleeping it can also create a
    //variable if sleepingHours is given 0 will make variable with starting time of the persons sleep.
    Calendar createSleepingHoursTill(int hours, int sleepingHours, String date) {
        Calendar c = Calendar.getInstance();
        String[] arrDate = date.split("/");
        c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
        c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
        c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        if (12 < hours) {
            hours = hours - 24;
        }
        c.add(Calendar.HOUR_OF_DAY, hours);
        c.add(Calendar.HOUR_OF_DAY, sleepingHours);
        return c;
    }

    ArrayList<Calendar> arrCal;

    //the method checks the given time and tell whether the time is occupied or not for the day which was given in the constructor while making this classes object.
    public boolean checkSchedule(String Time){
        allActivityCalender();
        boolean exist =false;
        try{
            Calendar c = createCalenderForAll(dater, Time);
            if (arrCal != null){
                for (int i = 0; i<arrCal.size(); i++){
                    //Log.d("TAG","Checking for values: " + c.getTime() + " colling with time: " + arrCal.get(i).getTime());
                    String date1 = c.getTime().toString();
                    String date2 = arrCal.get(i).getTime().toString();
                    if (date1.equals(date2))
                    {
                        Toast.makeText(Information.gContext, "Time has clashed with an existing event." , Toast.LENGTH_LONG).show();
                        Information.gFlag = true;
                        exist=true;
                        break;
                    }
                    //Log.d("TAG","Checking for values: " + c.getTime() + " colling with time: after: " + arrCal.get(i).getTime());
                    Calendar tempCal1 = Calendar.getInstance();
                    tempCal1.setTime(arrCal.get(i).getTime());
                    Calendar tempCal2 = Calendar.getInstance();
                    tempCal2.setTime(arrCal.get(i).getTime());
                    tempCal2.add(Calendar.MINUTE,30);

                    Date time1 = tempCal1.getTime();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(time1);


                    Date time2 = tempCal2.getTime();
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(time2);

                    Date d = c.getTime();
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(d);

                    Date x = calendar3.getTime();
                    Log.d("TAG","Checking for values: " + c.getTime() + " colling with time: after: " + tempCal1.getTime() + " before: " + tempCal2.getTime());
                    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                        Toast.makeText(Information.gContext, "This time is colliding with an event.", Toast.LENGTH_LONG).show();
                        exist = true;
                        break;
                    }
                }
                Date x = c.getTime();
                Calendar midNight = createSleepingHoursTill(Integer.parseInt(SleepingTime[0]),0,dater);
                if (x.after(midNight.getTime()) && x.before(sleephours.getTime())) {
                    Toast.makeText(Information.gContext, "Time for sleep overlapped.", Toast.LENGTH_SHORT).show();
                    exist = true;
                }
                if (Integer.parseInt(SleepingTime[0]) > 12){
                    midNight = createSleepingHours(Integer.parseInt(SleepingTime[0]), dater);
                    Calendar till12 = createSleepingHoursTill(0,24, dater);
                    if (x.after(midNight.getTime()) && x.before(till12.getTime())) {
                        Toast.makeText(Information.gContext, "Time for sleep overlapped.", Toast.LENGTH_SHORT).show();
                        exist = true;
                    }
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(Information.gContext, "Scheduler(checkSchedule). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return exist;
    }

    //THE METHOD BRINGS time in Information.FreeTimeCalender and Its strings in Information.FreeTimeStrings
    public boolean makeFreeTimeSchedule(Calendar time, int index){
        allActivityCalender();
        boolean exist =false;
        try{
            if (arrCal != null){
                for (int i = 0; i<arrCal.size(); i++){
                    //Log.d("TAG","Checking for values: " + c.getTime() + " colling with time: " + arrCal.get(i).getTime());
                    String date1 = time.getTime().toString();
                    String date2 = arrCal.get(i).getTime().toString();
                    if (date1.equals(date2))
                    {
                        exist=true;
                        break;
                    }
                    //Log.d("TAG","Checking for values: " + c.getTime() + " colling with time: after: " + arrCal.get(i).getTime());
                    Calendar tempCal1 = Calendar.getInstance();
                    tempCal1.setTime(arrCal.get(i).getTime());
                    Calendar tempCal2 = Calendar.getInstance();
                    tempCal2.setTime(arrCal.get(i).getTime());
                    tempCal2.add(Calendar.MINUTE,30);

                    Date time1 = tempCal1.getTime();
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.setTime(time1);


                    Date time2 = tempCal2.getTime();
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.setTime(time2);

                    Date d = time.getTime();
                    Calendar calendar3 = Calendar.getInstance();
                    calendar3.setTime(d);

                    Date x = calendar3.getTime();
                    Log.d("TAG","Checking for values: " + time.getTime() + " colling with time: after: " + tempCal1.getTime() + " before: " + tempCal2.getTime());
                    if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                        exist = true;
                        break;
                    }
                }
                Date x = time.getTime();
                Calendar midNight = createSleepingHoursTill(Integer.parseInt(SleepingTime[0]),0,dater);
                if (x.after(midNight.getTime()) && x.before(sleephours.getTime())) {
                    exist = true;
                }
                if (Integer.parseInt(SleepingTime[0]) > 12){
                    midNight = createSleepingHours(Integer.parseInt(SleepingTime[0]), dater);
                    Calendar till12 = createSleepingHoursTill(0,24, dater);
                    if (x.after(midNight.getTime()) && x.before(till12.getTime())) {
                        exist = true;
                    }
                }
                if (!exist){
                    Log.d("TAG", "Free schedule checking time: " + time.getTime().toString());
                    if (index != 0 && Information.gFreeTimeCalenderString.size() != 0){
                        String var = time.getTime().toString();
                        Information.gFreeTimeCalenderString.set(index, var);
                    }
                    String timing = time.getTime().toString();
                    String[] timingArr = timing.split(" ");
                    Calendar till12 = createSleepingHoursTill(0,24, DateToday());
                    if (!Information.gFreeTimeCalenderString.contains(timingArr[3]) && time.getTime().before(till12.getTime())){
                        Information.gFreeTimeCalenderString.add(timingArr[3]);
                        Information.gFreeTimeCalender.add(time);
                    }
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(Information.gContext, "Scheduler(checkSchedule). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return exist;
    }

    public void allActivityCalender()
    {
        try{
            Information.gEventSubjectString = new ArrayList<String>();
            arrCal = new ArrayList<Calendar>();
            Calendar breakfastTime = createCalenderForAll(dater, breakfasttime);
            Log.d("TAG", "Calender created for " + breakfastTime.getTime());
            Calendar lunchTime = createCalenderForAll(dater, lunchtime);
            Log.d("TAG", "Calender created for " + lunchTime.getTime());
            Calendar dinnerTime = createCalenderForAll(dater, dinnertime);
            Log.d("TAG", "Calender created for " + dinnerTime.getTime());

            arrCal.add(breakfastTime);
            Information.gEventSubjectString.add("Breakfast Time : " + breakfasttime + "");
            arrCal.add(lunchTime);
            Information.gEventSubjectString.add("Lunch Time : " + lunchtime + "");
            arrCal.add(dinnerTime);
            Information.gEventSubjectString.add("Dinner Time : " + dinnertime + "");

            for (int i = 0; i<todayAppointmentList.size(); i++){
                Calendar cal = createCalenderForAll(dater,todayAppointmentList.get(i).appointmentTime);
                Log.d("TAG", "Calender created for " + cal.getTime());
                Information.gEventSubjectString.add(todayAppointmentList.get(i).appointmentSubject + " : " + todayAppointmentList.get(i).appointmentTime + "");
                arrCal.add(cal);
            }
            for (int i = 0; i<todayEventList.size(); i++){
                Calendar cal = createCalenderForAll(dater,todayEventList.get(i).eventtime);
                Log.d("TAG", "Calender created for " + cal.getTime());
                Information.gEventSubjectString.add(todayEventList.get(i).eventsubject + " : " + todayEventList.get(i).eventtime + "");
                arrCal.add(cal);
            }
            Information.gEventSubjectString.add("Sleeping Time : " + Information.gSleepingTime + "");
            arrCal.add(createCalenderForAll(DateTomorrow(), Information.gSleepingTime));
            Information.gAllCalenderArray = sortCalenderArrayList(arrCal);
        }catch (Exception e){
            Toast.makeText(Information.gContext, "Scheduler(allActivityCalender). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<Calendar> sortCalenderArrayList(ArrayList<Calendar> arrCalender){
        int n = arrCalender.size();
        for (int i = 1; i < n; ++i) {
            Calendar key = arrCalender.get(i);
            String key2 = Information.gEventSubjectString.get(i);
            int j = i - 1;
            while (j >= 0 && arrCalender.get(j).getTime().after(key.getTime())) {
                arrCalender.set(j + 1,arrCalender.get(j));
                Information.gEventSubjectString.set(j + 1, Information.gEventSubjectString.get(j));
                j = j - 1;
            }
            arrCalender.set(j + 1, key);
            Information.gEventSubjectString.set(j + 1, key2);
        }
        return arrCalender;
    }

    Calendar createCalenderForAll(String date, String time)
    {
        Calendar c = Calendar.getInstance();
        try{
            String[] arrDate = date.split("/");
            String[] arrTime = time.split(":");
            c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
            c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
            c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
            c.set(Calendar.SECOND, 0);
        }catch (Exception e){
            Toast.makeText(Information.gContext,"Scheduler(createCalenderForAll). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return c;
    }

    Calendar createCalender() {
        Calendar c = Calendar.getInstance();
        try{
            String[] arrDate = Information.geventdate.split("/");
            String[] arrTime = Information.geventtime.split(":");
            c.set(Calendar.YEAR, Integer.parseInt(arrDate[2]));
            c.set(Calendar.MONTH, Integer.parseInt(arrDate[0]));
            c.set(Calendar.DATE, Integer.parseInt(arrDate[1]));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
            c.set(Calendar.SECOND, 0);
        }catch (Exception e){
            Toast.makeText(Information.gContext,"Scheduler(createCalender). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return c;
    }

    Calendar createCalender2() {
        Calendar c = Calendar.getInstance();
        try{
            String arrDate = Information.gAppointmentDate;
            String[] arrTime = Information.gAppointmentTime.split(":");
            c.set(Calendar.DATE, Integer.parseInt(arrDate));
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arrTime[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(arrTime[1]));
            c.set(Calendar.SECOND, 0);
        }
        catch (Exception e){
            Toast.makeText(Information.gContext, "Scheduler(createCalender2). Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return c;
    }

    private ArrayList<EventHelperClass> collectEvent(Map<String, Object> users) {
        eventList = new ArrayList<EventHelperClass>();
        EventHelperClass eventHelperClass;
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            eventHelperClass = new EventHelperClass();
            Map singleUser = (Map) entry.getValue();
            eventHelperClass.eventsubject = (String) singleUser.get("eventsubject");
            eventHelperClass.eventdate = (String) singleUser.get("eventdate");
            eventHelperClass.eventtime = (String) singleUser.get("eventtime");
            eventList.add(eventHelperClass);
        }
        return eventList;
    }

    private ArrayList<AppointmentHelperClass> collectAppointment(Map<String, Object> users) {
        appointmentList = new ArrayList<AppointmentHelperClass>();
        AppointmentHelperClass appointmentHelperClass;
        try{
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                appointmentHelperClass = new AppointmentHelperClass();
                Map singleUser = (Map) entry.getValue();
                appointmentHelperClass.appointmentSubject = (String) singleUser.get("appointmentSubject");
                appointmentHelperClass.appointmentTime = (String) singleUser.get("appointmentTime");
                appointmentHelperClass.appointmentDate = (String) singleUser.get("appointmentDate");
                if (appointmentHelperClass.appointmentDate != null){
                    //Toast.makeText(Information.gContext, "Counted Appointment.", Toast.LENGTH_SHORT).show();
                    appointmentList.add(appointmentHelperClass);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(Information.gContext, "Error in checking appointments. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return appointmentList;
    }
}
package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.api.client.util.ObjectParser;

public class TeaTime extends AppCompatActivity {
    Button btnSetTeaTime,btnskip;
    TimePicker teaTimePicker;
    Spinner spinner, spinner2;
    int times;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tea_time);

        btnskip=findViewById(R.id.button_skip);
        btnskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TeaTime.this,sleep.class);
                startActivity(intent);
            }
        });



        try {
            Information.speak("select your drink times");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }



        try {
            spinner = (Spinner) findViewById(R.id.drinking_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.drinking_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);


            spinner2 = (Spinner) findViewById(R.id.drinking_times);
// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                    R.array.drinking_times_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner2.setAdapter(adapter2);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (parent.getItemAtPosition(position).equals("Select Drink?")) {
                        Information.gteatime = null;
                    } else {
                        Information.gteatime = (String) parent.getItemAtPosition(position);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String get = (String) parent.getItemAtPosition(position);
                    if (isNumeric(get)) {
                        times = Integer.parseInt(get);
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(TeaTime.this);
                        dlgAlert.setMessage("You have selected " + times + " times kindly input " + times + " different times.");
                        dlgAlert.setTitle("Time for drinking Hours.");
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dismiss the dialog
                                    }
                                });
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            btnSetTeaTime = (Button) findViewById(R.id.button_set_teatime);
            btnSetTeaTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teaTimePicker = findViewById(R.id.time_picker_tea);
                    int hour, minutes;
                    hour = teaTimePicker.getCurrentHour();
                    minutes = teaTimePicker.getCurrentMinute();
                    count++;
                    Information.gteatime = Information.gteatime + " " + count + " " + hour + ":" + minutes;
                    timeReminder();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void timeReminder() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage("You have selected " + (count) + " times kindly input " + (times - count) + " more times.");
        dlgAlert.setTitle("Time for drinking Hours.");
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                        if (count >= times) {
                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(TeaTime.this);
                            dlgAlert.setMessage(Information.gteatime);
                            dlgAlert.setTitle("Time for drinking Hours.");
                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (count == times)
                                            {
                                                Intent intent = new Intent(TeaTime.this,sleep.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    }
                });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
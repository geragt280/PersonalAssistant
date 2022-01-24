package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Secondactivity extends AppCompatActivity {
    Button button,btnlogout,btnSaveDate;
    RadioGroup radioGroup;
    RadioButton radioButton;
    DatePicker dp;
    private long BackPressTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondactivity);

        radioGroup = findViewById(R.id.radio_gender);

        btnSaveDate = (Button) findViewById(R.id.button_next_second);
        btnSaveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dp = findViewById(R.id.dobPicker);
                int day, month, year;
                day = dp.getDayOfMonth();
                month = dp.getMonth();
                year = dp.getYear();
                Information.gbirthDate = day + "/" + month + "/" + year;
                //  Toast.makeText(Secondactivity.this, "DOB: " + Information.gbirthDate, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Secondactivity.this, breakfast.class);
                startActivity(intent);
                checkButton();
                Information.ggender=radioButton.getText().toString();
            }
        });
        try {
            Information.speak("select your date of birth and gender");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        if (BackPressTime + 2000 > System.currentTimeMillis()) {
            finish();
            finishAffinity();
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
           backToast= Toast.makeText(getBaseContext(),"Press Back Again To Exit",Toast.LENGTH_SHORT);
           backToast.show();
        }
        BackPressTime=System.currentTimeMillis();
    }

    public void checkButton () {

        int radioid = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
        //Toast.makeText(this, "Selected radio button:" + radioButton.getText(), Toast.LENGTH_SHORT).show();
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.gender_male:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.gender_female:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.gender_custom:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

}

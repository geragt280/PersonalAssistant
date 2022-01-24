package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    Button signin;
    EditText username, password;
    TextView tv2;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        try {
            StartSpeech();
        } catch (Exception e){
            Toast.makeText(this, "error in start speech. Error: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            Information.speak("please ! login if you an existing account");
        } catch (Exception e) {
            Toast.makeText(this, "error in speak. Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        SharedPref sp = new SharedPref();
        try {
            Information.gContext = this;
            Information.gusername = sp.getData(this);
            if (Information.gusername != null){
                Intent intent = new Intent(this, MainEvent.class);
                startActivity(intent);
            }
        }catch (Exception e){
            Toast.makeText(this, "Error in shared preferences code. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        username = findViewById(R.id.edit_username);
        password = findViewById(R.id.edit_password);

        tv2 = findViewById(R.id.text_view2);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSignIn = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intSignIn);
            }
        });

        signin = findViewById(R.id.button_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    username.setError("Please enter your Email");
                    username.requestFocus();
                } else if (pwd.isEmpty()) {
                    password.setError("Please enter your Password");
                    password.requestFocus();
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "fields is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    isUser();
                }
            }

        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            try {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                if (acct != null) {

                    Information.gname = acct.getDisplayName();
                    Information.gemail = acct.getEmail();
                    Information.gusername = acct.getId();
                    Uri personPhoto = acct.getPhotoUrl();

                }
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (!Information.gusername.equals("") && !Information.gemail.equals("") && !Information.gname.equals("")){
                Intent intent = new Intent(LoginActivity.this, Secondactivity.class);
                startActivity(intent);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void isUser()
    {

        final String UserEnteredUsername =username.getEditableText().toString().trim();
        final String UserEnteredpassword=password.getEditableText().toString().trim();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        Query checkUser=reference.orderByChild("username").equalTo(UserEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                try
                {
                    if (datasnapshot.exists()) {
                        Information.gusername = username.getEditableText().toString().trim();
                        String passwordfromDB = datasnapshot.child(UserEnteredUsername).child("password").getValue(String.class);
                        if(passwordfromDB.equals(UserEnteredpassword)){
                            SharedPref sp = new SharedPref();
                            sp.save(Information.gContext, Information.gusername);

                            Intent intent=new Intent(LoginActivity.this,MainEvent.class);
                            startActivity(intent);
                        }
                        else {
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                    }
                    else {
                        username.setError(("No such user exist"));
                        username.requestFocus();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void StartSpeech() {
        Information.mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if ((status == TextToSpeech.SUCCESS)) {
                    int result = Information.mtts.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported ");
                    } else {
                        //mButtonspeek.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "Speech Is enabled.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TTS", "Initialization failed ");
                }
            }
        });
    }
    protected void onDestroy() {
        if (Information.mtts != null){
            Information.mtts.stop();
            Information.mtts.shutdown();
        }
        super.onDestroy();
    }
}
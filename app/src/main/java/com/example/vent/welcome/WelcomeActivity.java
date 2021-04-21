package com.example.vent.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.vent.MainActivity;
import com.example.vent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        //creates new thread to handle splash screen timeout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                //if user has not created an account send to LoginActivity
                if(firebaseUser == null){
                    Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    loginIntent.putExtra("type", "sign_in");
                    startActivity(loginIntent);
                    finish();
                }

                finish();
            }
        }, SPLASH_TIME_OUT);

    }


    //Splash screen will display only when user has not created an account
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //user has created account, send to MainActivity
        if(firebaseUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}

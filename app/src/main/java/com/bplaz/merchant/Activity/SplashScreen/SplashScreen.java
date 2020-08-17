package com.bplaz.merchant.Activity.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bplaz.merchant.Activity.Main.MainActivity;
import com.bplaz.merchant.R;

public class SplashScreen extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent next = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(next);
                SplashScreen.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }, 2000);
    }
}

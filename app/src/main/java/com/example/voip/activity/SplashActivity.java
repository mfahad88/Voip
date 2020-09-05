package com.example.voip.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.example.voip.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNext(LoginActivity.class);
            }
        },5000);
    }

    private void startNext(Class clazz){
        Intent intent=new Intent(this,clazz);
        startActivity(intent);
    }
}
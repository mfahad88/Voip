package com.example.voip.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.voip.R;
import com.example.voip.fragment.DialPadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {
    private LinearLayout linear_nav,linear_history,linear_dial,linear_contacts;
    private FrameLayout main_container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        linear_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new DialPadFragment());
            }
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container,fragment);
        ft.commit();
    }

    private void initViews() {
        linear_nav = findViewById(R.id.linear_nav);
        linear_history = findViewById(R.id.linear_history);
        linear_dial = findViewById(R.id.linear_dial);
        linear_contacts = findViewById(R.id.linear_contacts);
        main_container = findViewById(R.id.main_container);
    }

}
package com.example.voip.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.voip.R;
import com.example.voip.fragment.ContactsFragment;
import com.example.voip.fragment.DialPadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {
    private LinearLayout linear_nav,linear_history,linear_dial,linear_contacts;
    private FrameLayout main_container;
    private int PERMISSION_CODE = 1;
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
        linear_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED){
                    replaceFragment(new ContactsFragment());
                }else{
                    requestContactPermission();
                }
            }
        });

    }

    private void requestContactPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_CONTACTS)){
            replaceFragment(new ContactsFragment());
            /*new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_CONTACTS}, PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();*/

        }else{
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //replaceFragment(new DialPadFragment());
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
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
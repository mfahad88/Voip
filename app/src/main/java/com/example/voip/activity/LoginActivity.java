package com.example.voip.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.voip.R;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_username,edt_password;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        btn_login.setOnClickListener(this);
    }

    private void initViews() {
        edt_username=findViewById(R.id.edt_username);
        edt_password=findViewById(R.id.edt_password);
        btn_login=findViewById(R.id.btn_login);
    }



    @Override
    public void onClick(View view) {
        String username=edt_username.getText().toString();
        String password=edt_password.getText().toString();
        if(!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)){
            Toast.makeText(view.getContext(), "Username: "+username+"\nPassword: "+password, Toast.LENGTH_SHORT).show();
            startNext(MainActivity.class);
        }else{
            final AlertDialog.Builder dialog=new AlertDialog.Builder(view.getContext());
            dialog.setTitle("Error");
            dialog.setMessage("Please check username and password...");
            dialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog=dialog.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private void startNext(Class clazz){
        Intent intent=new Intent(this,clazz);
        startActivity(intent);
    }
}
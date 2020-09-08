package com.example.voip.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.voip.R;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.sdk.OnRegistrationListener;
import org.abtollc.utils.Log;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edt_username,edt_password;
    private Button btn_login;
    private AbtoPhone abtoPhone;
    public static String RegDomain   = "itel.vokka.net";
    public static String RegProxy    = "sip.vokka.net";
    public static String RegPassword = "123123";
    public static String RegUser     = "0561944200";
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
            abtoPhone = ((AbtoApplication) getApplication()).getAbtoPhone();
            // Set registration event
            abtoPhone.setRegistrationStateListener(new OnRegistrationListener() {

                public void onRegistrationFailed(long accId, int statusCode, String statusText) {

                    if(dialog != null) dialog.dismiss();

                    android.app.AlertDialog.Builder fail = new android.app.AlertDialog.Builder(LoginActivity.this);
                    fail.setTitle("Registration failed");
                    fail.setMessage(statusCode + " - " + statusText);
                    fail.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    fail.show();
                }

                public void onRegistered(long accId) {

                    //Hide progress
                    if(dialog != null) dialog.dismiss();

                    //Unsubscribe reg events
                    abtoPhone.setRegistrationStateListener(null);

                    //Start main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    //Close this activity
                    finish();
                }

                @Override
                public void onUnRegistered(long accId) {
                    Log.e(this.toString(), "log succ acc = " + accId);

                    Toast.makeText(LoginActivity.this, "RegisterActivity::onUnRegistered", Toast.LENGTH_SHORT).show();
                }
            }); //registration listener
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            //Show progress
            if(dialog==null)
            {
                dialog = new ProgressDialog(view.getContext());
                dialog.setCancelable(false);
                dialog.setMessage("Registering...");
                dialog.setCancelable(false);
            }
            dialog.show();
            int regExpire = 3600;


            //Register
            try {
                long accId1 = abtoPhone.getConfig().addAccount(RegDomain, RegProxy, username, password, null, "", regExpire, false);

                abtoPhone.register();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            startNext(MainActivity.class);
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
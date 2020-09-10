package com.example.voip.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.voip.R;

import org.abtollc.api.SipProfileState;
import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.sdk.AbtoPhoneCfg;
import org.abtollc.sdk.OnInitializeListener;
import org.abtollc.utils.codec.Codec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends Activity implements OnInitializeListener {
    private AbtoPhone abtoPhone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Get AbtoPhone instance
        abtoPhone = ((AbtoApplication) getApplication()).getAbtoPhone();

        boolean bCanStartPhoneInitialization = (Build.VERSION.SDK_INT >= 23) ?  askPermissions() : true;

        if(bCanStartPhoneInitialization)    initPhone();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNext(LoginActivity.class);
            }
        },5000);*/
    }

    void startNextActivity()
    {
        //Detect is account registered.
        //If 'yes' - go directly to MainActivity
        //If 'no' - go to RegisterActivity
        Intent intent = new Intent(this, isAccountRegistered() ? MainActivity.class : LoginActivity.class);
        startActivity(intent);
        finish();
    }


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private boolean askPermissions()
    {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))           permissionsNeeded.add("Record audio");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissionsNeeded.add("Write logs to sd card");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))                 permissionsNeeded.add("Camera");
        if (!addPermission(permissionsList, Manifest.permission.USE_SIP))                permissionsNeeded.add("Use SIP protocol");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))                permissionsNeeded.add("Read Contacts");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                //String message = "You need to grant access to " + permissionsNeeded.get(0);
                //for (int i = 1; i < permissionsNeeded.size(); i++) message = message + ", " + permissionsNeeded.get(i);


                ActivityCompat.requestPermissions(this,
                        permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                return false;
            }

            ActivityCompat.requestPermissions(this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))     return false;
        }


        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }




    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                //Initial
                perms.put(Manifest.permission.RECORD_AUDIO,           PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA,                 PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.USE_SIP,                PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS,                PackageManager.PERMISSION_GRANTED);

                //Fill with results
                for (int i = 0; i < permissions.length; i++) perms.put(permissions[i], grantResults[i]);

                //Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.USE_SIP) == PackageManager.PERMISSION_GRANTED &&
                        perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    initPhone();
                } else {
                    // Permission Denied
                    Toast.makeText(SplashActivity.this, "Some permissions were denied", Toast.LENGTH_SHORT).show();
                    ProgressBar bar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
                    bar.setVisibility(View.GONE);
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    protected void initPhone()
    {
        //Verify is SDKs service already running
        if (abtoPhone.isActive())
        {
            startNextActivity();
            return;
        }

        abtoPhone.setInitializeListener(this);

        //configure phone instance
        AbtoPhoneCfg config = abtoPhone.getConfig();

        for (Codec c : Codec.values()) config.setCodecPriority(c, (short) 0);
        config.setCodecPriority(Codec.PCMA, (short) 80);
        config.setCodecPriority(Codec.PCMU, (short) 79);
        config.setCodecPriority(Codec.G729, (short) 78);

        config.setCodecPriority(Codec.H264, (short) 220);
        config.setCodecPriority(Codec.H263_1998, (short) 0);

        config.setSignallingTransport(AbtoPhoneCfg.SignalingTransportType.TLS); // Switch to TLS SIP signalling
        config.setTLSVerifyServer(false); // Disable Certificate verification for initial connect
        //config.setSignallingTransport(AbtoPhoneCfg.SignalingTransportType.UDP);//TCP);//TLS);
        config.setKeepAliveInterval(AbtoPhoneCfg.SignalingTransportType.UDP, 30);
        //config.setTLSVerifyServer(true);

        config.setSipPort(0);
        //config.setDTMFmode(AbtoPhoneCfg.DTMF_MODE.INFO);
        //config.setSipPort(5555);

        //config.setSTUNEnabled(true);
        //config.setSTUNServer("stun.l.google.com:19302");

        config.setUseSRTP(false);
        config.setEnableAutoSendRtpVideo(true);
        config.setUserAgent(abtoPhone.version());
        config.setHangupTimeout(3000);


        config.setEnableSipsSchemeUse(false);

        config.setSTUNEnabled(false);
        //config.setSipPort(32323);

        AbtoPhoneCfg.setLogLevel(7, true);
        //config.setMwiEnabled(true);

        // Start initializing - !app has invoke this method only once!
        abtoPhone.initialize(true);//start service in 'sticky' mode - when app removed from recent service will be restarted automatically
        //abtoPhone.initializeForeground(null);//start service in foreground mode
    }

    public void onDestroy()
    {
        abtoPhone.setInitializeListener(null);
        super.onDestroy();

    }//onDestroy


    private boolean isAccountRegistered()
    {
        //Get current account
        long acc = abtoPhone.getCurrentAccountId();
        if ((acc == -1) || !abtoPhone.isActive()) return false;

        //Check accounts status (service keeps it registered)
        try {
            SipProfileState accState = abtoPhone.getSipProfileState(acc);
            if ((accState != null) && accState.isActive() && (accState.getStatusCode() == 200))
            {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void onInitializeState(OnInitializeListener.InitializeState state, String message) {
        switch (state) {
            case START:
            case INFO:
            case WARNING: break;
            case FAIL:

                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Error")
                        .setMessage(message)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dlg, int which) {
                                dlg.dismiss();

                            }
                        }).create().show();
                break;
            case SUCCESS:
                startNextActivity();
                break;

            default:
                break;
        }


    }
}
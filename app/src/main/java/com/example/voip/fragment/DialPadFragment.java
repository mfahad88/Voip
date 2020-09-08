package com.example.voip.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voip.R;
import com.example.voip.activity.LoginActivity;
import com.example.voip.activity.ScreenAV;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.sdk.OnRegistrationListener;


public class DialPadFragment extends Fragment implements View.OnClickListener, OnRegistrationListener {

    private View root;
    private ProgressDialog dialog;
    public static String START_VIDEO_CALL = "START_VIDEO_CALL";
    private TextView txt_dial_number,accLabel;
    private LinearLayout linear_one;
    private LinearLayout linear_two;
    private LinearLayout linear_three;
    private LinearLayout linear_four;
    private LinearLayout linear_five;
    private LinearLayout linear_six;
    private LinearLayout linear_seven;
    private LinearLayout linear_eight;
    private LinearLayout linear_nine;
    private LinearLayout linear_zero;
    private LinearLayout linear_star;
    private LinearLayout linear_hash;
    private LinearLayout linear_call;
    private ImageView image_remove;
    private AbtoPhone abtoPhone;
    private String domain;
    int accExpire;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_dial_pad, container, false);
        initViews();
        setListener();

        int accId = (int)abtoPhone.getCurrentAccountId();
        accExpire = abtoPhone.getConfig().getAccountExpire(accId);
        String contact = abtoPhone.getConfig().getAccount(accId).acc_id;
        contact = contact.replace("<", "");
        contact = contact.replace(">", "");

        if (accExpire == 0)  {
            accLabel.setText("Local contact: " + contact + ":" + abtoPhone.getConfig().getSipPort());

            domain = "";
        }
        else  {
            accLabel.setText("Registered as : " + contact);
            domain = abtoPhone.getConfig().getAccountDomain(accId);
        }
        return root;
    }

    private void setListener() {
        linear_hash.setOnClickListener(this);
        linear_star.setOnClickListener(this);
        linear_call.setOnClickListener(this);
        linear_zero.setOnClickListener(this);
        linear_one.setOnClickListener(this);
        linear_two.setOnClickListener(this);
        linear_three.setOnClickListener(this);
        linear_four.setOnClickListener(this);
        linear_five.setOnClickListener(this);
        linear_six.setOnClickListener(this);
        linear_seven.setOnClickListener(this);
        linear_eight.setOnClickListener(this);
        linear_nine.setOnClickListener(this);
        image_remove.setOnClickListener(this);
        abtoPhone.setRegistrationStateListener(this);
    }

    private void initViews() {
        accLabel = root.findViewById(R.id.account_label);
        txt_dial_number=root.findViewById(R.id.txt_dial_number);
        linear_call=root.findViewById(R.id.linear_call);
        linear_star=root.findViewById(R.id.linear_star);
        linear_hash=root.findViewById(R.id.linear_hash);
        linear_zero=root.findViewById(R.id.linear_zero);
        linear_one=root.findViewById(R.id.linear_one);
        linear_two=root.findViewById(R.id.linear_two);
        linear_three=root.findViewById(R.id.linear_three);
        linear_four=root.findViewById(R.id.linear_four);
        linear_five=root.findViewById(R.id.linear_five);
        linear_six=root.findViewById(R.id.linear_six);
        linear_seven=root.findViewById(R.id.linear_seven);
        linear_eight=root.findViewById(R.id.linear_eight);
        linear_nine=root.findViewById(R.id.linear_nine);
        image_remove=root.findViewById(R.id.image_remove);
        abtoPhone = ((AbtoApplication) getActivity().getApplication()).getAbtoPhone();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linear_call:
                startCall(false);
                break;

            case R.id.linear_star:
                txt_dial_number.append("*");
                break;

            case R.id.linear_hash:
                txt_dial_number.append("#");
                break;

            case R.id.linear_zero:
                txt_dial_number.append("0");
                break;

            case R.id.linear_one:
                txt_dial_number.append("1");
                break;

            case R.id.linear_two:
                txt_dial_number.append("2");
                break;

            case R.id.linear_three:
                txt_dial_number.append("3");
                break;

            case R.id.linear_four:
                txt_dial_number.append("4");
                break;

            case R.id.linear_five:
                txt_dial_number.append("5");
                break;

            case R.id.linear_six:
                txt_dial_number.append("6");
                break;

            case R.id.linear_seven:
                txt_dial_number.append("7");
                break;

            case R.id.linear_eight:
                txt_dial_number.append("8");
                break;

            case R.id.linear_nine:
                txt_dial_number.append("9");
                break;

            case R.id.image_remove:
                if(txt_dial_number.length()>0)
                    txt_dial_number.setText(txt_dial_number.getText().toString().substring(0,txt_dial_number.getText().length()-1));
                break;

        }
    }

    public void startCall(boolean bVideo)   {

        //Check empty
        String sipNumber = txt_dial_number.getText().toString();
        if(sipNumber.isEmpty())  return;

        //Verify direct call mode
        if(TextUtils.isEmpty(domain) && !sipNumber.contains("@") ) {
            Toast.makeText(getContext(), "Specify remote side address as 'number@domain:port'", Toast.LENGTH_SHORT).show();
            return;
        }

        //Build address to dial
        StringBuffer buildString = new StringBuffer();
        buildString.append(sipNumber);

        //Append domain (if required)
        if(!sipNumber.contains("@")) {
            buildString.append("@");
            buildString.append(domain);
        }
/*
        //Append headers
        try {
            buildString.append("?");
            buildString.append("header1=");
            buildString.append(URLEncoder.encode("qq<q>q", "UTF-8"));//value of 'header1'

            buildString.append("&");

            buildString.append("header2=");
            buildString.append(URLEncoder.encode("a@b", "UTF-8"));//value of 'header2'
            //=======================
            buildString.append("?");
            buildString.append("X-AccountId=");
            buildString.append(URLEncoder.encode("2223344", "UTF-8"));//value of 'header1'

            buildString.append("&");

            buildString.append("X-Geolocation=");
            buildString.append(URLEncoder.encode("<geo:43.6665599,-79.3791219;timestamp=20200317094000>", "UTF-8"));//value of 'header2'

        }
        catch (UnsupportedEncodingException e)
        {
        }*/

        Intent intent = new Intent(getContext(), ScreenAV.class);
        intent.putExtra(AbtoPhone.IS_INCOMING, false);//!
        intent.putExtra(AbtoPhone.REMOTE_CONTACT, buildString.toString());
        intent.putExtra(START_VIDEO_CALL, bVideo);
        startActivity(intent);
        txt_dial_number.setText("");
    }

    @Override
    public void onRegistrationFailed(long accId, int statusCode, String statusText) {



        AlertDialog.Builder fail = new AlertDialog.Builder(getContext());
        fail.setTitle("Registration failed");
        fail.setMessage(statusCode + " - " + statusText);
        fail.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        fail.show();

        onUnRegistered(0);
    }

    @Override
    public void onRegistered(long accId) {
        //Toast.makeText(this, "MainActivity - onRegistered", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnRegistered(long arg0) {

        if(dialog != null) dialog.dismiss();

        //Unsubscribe reg events
        abtoPhone.setRegistrationStateListener(null);

        //Start reg activity
        startActivity(new Intent(getContext(), LoginActivity.class));

        //Close this activity
        getActivity().finish();
    }
}
